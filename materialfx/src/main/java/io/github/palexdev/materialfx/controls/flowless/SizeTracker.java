/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.flowless;

import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Bounds;
import javafx.scene.control.IndexRange;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.collection.LiveList;
import org.reactfx.collection.MemoizationList;
import org.reactfx.value.Val;
import org.reactfx.value.ValBase;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

/**
 * Estimates the size of the entire viewport (if it was actually completely rendered) based on the known sizes of the
 * {@link Cell}s whose nodes are currently displayed in the viewport and an estimated average of
 * {@link Cell}s whose nodes are not displayed in the viewport. The meaning of {@link #breadthForCells} and
 * {@link #totalLengthEstimate} are dependent upon which implementation of {@link OrientationHelper} is used.
 */
final class SizeTracker {
    private final OrientationHelper orientation;
    private final ObservableObjectValue<Bounds> viewportBounds;
    private final MemoizationList<? extends Cell<?, ?>> cells;

    private final MemoizationList<Double> breadths;
    private final Val<Double> maxKnownMinBreadth;

    /**
     * Stores either the greatest minimum cell's node's breadth or the viewport's breadth
     */
    private final Val<Double> breadthForCells;

    private final MemoizationList<Double> lengths;

    /**
     * Stores either null or the average length of the cells' nodes currently displayed in the viewport
     */
    private final Val<Double> averageLengthEstimate;

    private final Val<Double> totalLengthEstimate;
    private final Val<Double> lengthOffsetEstimate;

    private final Subscription subscription;

    /**
     * Constructs a SizeTracker
     *
     * @param orientation if vertical, breadth = width and length = height;
     *                    if horizontal, breadth = height and length = width
     */
    public SizeTracker(
            OrientationHelper orientation,
            ObservableObjectValue<Bounds> viewportBounds,
            MemoizationList<? extends Cell<?, ?>> lazyCells) {
        this.orientation = orientation;
        this.viewportBounds = viewportBounds;
        this.cells = lazyCells;
        this.breadths = lazyCells.map(orientation::minBreadth).memoize();
        this.maxKnownMinBreadth = breadths.memoizedItems()
                .reduce(Math::max)
                .orElseConst(0.0);
        this.breadthForCells = Val.combine(
                maxKnownMinBreadth,
                viewportBounds,
                (a, b) -> Math.max(a, orientation.breadth(b)));

        Val<Function<Cell<?, ?>, Double>> lengthFn;
        lengthFn = (orientation instanceof HorizontalHelper ? breadthForCells : avoidFalseInvalidations(breadthForCells))
                .map(breadth -> cell -> orientation.prefLength(cell, breadth));

        this.lengths = cells.mapDynamic(lengthFn).memoize();

        LiveList<Double> knownLengths = this.lengths.memoizedItems();
        Val<Double> sumOfKnownLengths = knownLengths.reduce(Double::sum).orElseConst(0.0);
        Val<Integer> knownLengthCount = knownLengths.sizeProperty();

        this.averageLengthEstimate = Val.create(
                () -> {
                    // make sure to use pref lengths of all present cells
                    for (int i = 0; i < cells.getMemoizedCount(); ++i) {
                        int j = cells.indexOfMemoizedItem(i);
                        lengths.force(j, j + 1);
                    }

                    int count = knownLengthCount.getValue();
                    return count == 0
                            ? null
                            : sumOfKnownLengths.getValue() / count;
                },
                sumOfKnownLengths, knownLengthCount);

        this.totalLengthEstimate = Val.combine(
                averageLengthEstimate, cells.sizeProperty(),
                (avg, n) -> n * avg);

        Val<Integer> firstVisibleIndex = Val.create(
                () -> cells.getMemoizedCount() == 0 ? null : cells.indexOfMemoizedItem(0),
                cells, cells.memoizedItems()); // need to observe cells.memoizedItems()
        // as well, because they may change without a change in cells.

        Val<? extends Cell<?, ?>> firstVisibleCell = cells.memoizedItems()
                .collapse(visCells -> visCells.isEmpty() ? null : visCells.get(0));

        Val<Integer> knownLengthCountBeforeFirstVisibleCell = Val.create(() -> firstVisibleIndex.getOpt()
                .map(i -> lengths.getMemoizedCountBefore(Math.min(i, lengths.size())))
                .orElse(0), lengths, firstVisibleIndex);

        Val<Double> totalKnownLengthBeforeFirstVisibleCell = knownLengths.reduceRange(
                knownLengthCountBeforeFirstVisibleCell.map(n -> new IndexRange(0, n)),
                Double::sum).orElseConst(0.0);

        Val<Double> unknownLengthEstimateBeforeFirstVisibleCell = Val.combine(
                firstVisibleIndex,
                knownLengthCountBeforeFirstVisibleCell,
                averageLengthEstimate,
                (firstIdx, knownCnt, avgLen) -> (firstIdx - knownCnt) * avgLen);

        Val<Double> firstCellMinY = firstVisibleCell.flatMap(orientation::minYProperty);

        lengthOffsetEstimate = Val.wrap(EventStreams.combine(
                totalKnownLengthBeforeFirstVisibleCell.values(),
                unknownLengthEstimateBeforeFirstVisibleCell.values(),
                firstCellMinY.values()
        )
                .filter(t3 -> t3.test((a, b, minY) -> a != null && b != null && minY != null))
                .thenRetainLatestFor(Duration.ofMillis(1))
                .map(t3 -> t3.map((a, b, minY) -> a + b - minY))
                .toBinding(0.0));

        // pinning totalLengthEstimate and lengthOffsetEstimate
        // binds it all together and enables memoization
        this.subscription = Subscription.multi(
                totalLengthEstimate.pin(),
                lengthOffsetEstimate.pin());
    }

    private static <T> Val<T> avoidFalseInvalidations(Val<T> src) {
        return new ValBase<>() {
            @Override
            protected Subscription connect() {
                return src.observeChanges((obs, oldVal, newVal) -> invalidate());
            }

            @Override
            protected T computeValue() {
                return src.getValue();
            }
        };
    }

    public void dispose() {
        subscription.unsubscribe();
    }

    public Val<Double> maxCellBreadthProperty() {
        return maxKnownMinBreadth;
    }

    public double getViewportBreadth() {
        return orientation.breadth(viewportBounds.get());
    }

    public double getViewportLength() {
        return orientation.length(viewportBounds.get());
    }

    public Val<Double> averageLengthEstimateProperty() {
        return averageLengthEstimate;
    }

    public Optional<Double> getAverageLengthEstimate() {
        return averageLengthEstimate.getOpt();
    }

    public Val<Double> totalLengthEstimateProperty() {
        return totalLengthEstimate;
    }

    public Val<Double> lengthOffsetEstimateProperty() {
        return lengthOffsetEstimate;
    }

    public double breadthFor(int itemIndex) {
        assert cells.isMemoized(itemIndex);
        breadths.force(itemIndex, itemIndex + 1);
        return breadthForCells.getValue();
    }

    public void forgetSizeOf(int itemIndex) {
        breadths.forget(itemIndex, itemIndex + 1);
        lengths.forget(itemIndex, itemIndex + 1);
    }

    public double lengthFor(int itemIndex) {
        return lengths.get(itemIndex);
    }

    public double getCellLayoutBreadth() {
        return breadthForCells.getValue();
    }
}
