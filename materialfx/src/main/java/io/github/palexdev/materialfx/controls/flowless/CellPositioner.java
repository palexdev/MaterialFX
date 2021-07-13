/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.flowless;

import org.reactfx.collection.MemoizationList;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Helper class for properly {@link javafx.scene.Node#resize(double, double) resizing} and
 * {@link javafx.scene.Node#relocate(double, double) relocating} a {@link Cell}'s {@link javafx.scene.Node} as well
 * as handling calls related to whether a cell's node is visible (displayed in the viewport) or not.
 */
final class CellPositioner<T, C extends Cell<T, ?>> {
    private final CellListManager<T, C> cellManager;
    private final OrientationHelper orientation;
    private final SizeTracker sizeTracker;

    public CellPositioner(
            CellListManager<T, C> cellManager,
            OrientationHelper orientation,
            SizeTracker sizeTracker) {
        this.cellManager = cellManager;
        this.orientation = orientation;
        this.sizeTracker = sizeTracker;
    }

    public void cropTo(int from, int to) {
        cellManager.cropTo(from, to);
    }

    public C getVisibleCell(int itemIndex) {
        C cell = cellManager.getPresentCell(itemIndex);
        if (cell.getNode().isVisible()) {
            return cell;
        } else {
            throw new NoSuchElementException(
                    "Cell " + itemIndex + " is not visible");
        }
    }

    public Optional<C> getCellIfVisible(int itemIndex) {
        return cellManager.getCellIfPresent(itemIndex)
                .filter(c -> c.getNode().isVisible());
    }

    public OptionalInt lastVisibleBefore(int position) {
        MemoizationList<C> cells = cellManager.getLazyCellList();
        int presentBefore = cells.getMemoizedCountBefore(position);
        for (int i = presentBefore - 1; i >= 0; --i) {
            C cell = cells.memoizedItems().get(i);
            if (cell.getNode().isVisible()) {
                return OptionalInt.of(cells.indexOfMemoizedItem(i));
            }
        }
        return OptionalInt.empty();
    }

    public OptionalInt firstVisibleAfter(int position) {
        MemoizationList<C> cells = cellManager.getLazyCellList();
        int presentBefore = cells.getMemoizedCountBefore(position);
        int present = cells.getMemoizedCount();
        for (int i = presentBefore; i < present; ++i) {
            C cell = cells.memoizedItems().get(i);
            if (cell.getNode().isVisible()) {
                return OptionalInt.of(cells.indexOfMemoizedItem(i));
            }
        }
        return OptionalInt.empty();
    }

    public OptionalInt getLastVisibleIndex() {
        return lastVisibleBefore(cellManager.getLazyCellList().size());
    }

    public OptionalInt getFirstVisibleIndex() {
        return firstVisibleAfter(0);
    }

    /**
     * Gets the shortest delta amount by which to scroll the viewport's length in order to fully display a
     * partially-displayed cell's node.
     */
    public double shortestDeltaToViewport(C cell) {
        return shortestDeltaToViewport(cell, 0.0, orientation.length(cell));
    }

    public double shortestDeltaToViewport(C cell, double fromY, double toY) {
        double cellMinY = orientation.minY(cell);
        double gapBefore = cellMinY + fromY;
        double gapAfter = sizeTracker.getViewportLength() - (cellMinY + toY);

        return (gapBefore < 0 && gapAfter > 0) ? Math.min(-gapBefore, gapAfter) :
                (gapBefore > 0 && gapAfter < 0) ? Math.max(-gapBefore, gapAfter) :
                        0.0;
    }

    /**
     * Moves the given cell's node's "layoutY" value by {@code delta}. See {@link OrientationHelper}'s javadoc for more
     * explanation on what quoted terms mean.
     */
    public void shiftCellBy(C cell, double delta) {
        double y = orientation.minY(cell) + delta;
        relocate(cell, 0, y);
    }

    /**
     * Properly resizes the cell's node, and sets its "layoutY" value, so that is the first visible
     * node in the viewport, and further offsets this value by {@code startOffStart}, so that
     * the node's <em>top</em> edge appears (if negative) "above," (if 0) "at," or (if negative) "below" the viewport's
     * "top" edge. See {@link OrientationHelper}'s javadoc for more explanation on what quoted terms mean.
     *
     * <pre><code>
     *      --------- top of cell's node if startOffStart is negative
     *
     *     __________ "top edge" of viewport / top of cell's node if startOffStart = 0
     *     |
     *     |
     *     |--------- top of cell's node if startOffStart is positive
     *     |
     * </code></pre>
     *
     * @param itemIndex     the index of the item in the list of all (not currently visible) cells
     * @param startOffStart the amount by which to offset the "layoutY" value of the cell's node
     */
    public C placeStartAt(int itemIndex, double startOffStart) {
        C cell = getSizedCell(itemIndex);
        relocate(cell, 0, startOffStart);
        cell.getNode().setVisible(true);
        return cell;
    }

    /**
     * Properly resizes the cell's node, and sets its "layoutY" value, so that is the last visible
     * node in the viewport, and further offsets this value by {@code endOffStart}, so that
     * the node's <em>top</em> edge appears (if negative) "above," (if 0) "at," or (if negative) "below" the
     * viewport's "bottom" edge. See {@link OrientationHelper}'s javadoc for more explanation on what quoted terms mean.
     *
     * <pre><code>
     *     |--------- top of cell's node if endOffStart is negative
     *     |
     *     |
     *     |_________ "bottom edge" of viewport / top of cell's node if endOffStart = 0
     *
     *
     *      --------- top of cell's node if endOffStart is positive
     * </code></pre>
     *
     * @param itemIndex   the index of the item in the list of all (not currently visible) cells
     * @param endOffStart the amount by which to offset the "layoutY" value of the cell's node
     */
    public C placeEndFromStart(int itemIndex, double endOffStart) {
        C cell = getSizedCell(itemIndex);
        relocate(cell, 0, endOffStart - orientation.length(cell));
        cell.getNode().setVisible(true);
        return cell;
    }

    /**
     * Properly resizes the cell's node, and sets its "layoutY" value, so that is the last visible
     * node in the viewport, and further offsets this value by {@code endOffStart}, so that
     * the node's <em>bottom</em> edge appears (if negative) "above," (if 0) "at," or (if negative) "below" the
     * viewport's "bottom" edge. See {@link OrientationHelper}'s javadoc for more explanation on what quoted terms mean.
     *
     * <pre><code>
     *     |--------- bottom of cell's node if endOffEnd is negative
     *     |
     *     |_________ "bottom edge" of viewport / bottom of cell's node if endOffEnd = 0
     *
     *
     *      --------- bottom of cell's node if endOffEnd is positive
     * </code></pre>
     *
     * @param itemIndex the index of the item in the list of all (not currently visible) cells
     * @param endOffEnd the amount by which to offset the "layoutY" value of the cell's node
     */
    public C placeEndFromEnd(int itemIndex, double endOffEnd) {
        C cell = getSizedCell(itemIndex);
        double y = sizeTracker.getViewportLength() + endOffEnd - orientation.length(cell);
        relocate(cell, 0, y);
        cell.getNode().setVisible(true);
        return cell;
    }

    /**
     * Properly resizes the cell's node, and sets its "layoutY" value, so that is the last visible
     * node in the viewport, and further offsets this value by {@code endOffStart}, so that
     * the node's <em>bottom</em> edge appears (if negative) "above," (if 0) "at," or (if negative) "below" the
     * viewport's "top" edge. See {@link OrientationHelper}'s javadoc for more explanation on what quoted terms mean.
     *
     * <pre><code>
     *      --------- bottom of cell's node if startOffStart is negative
     *
     *     __________ "top edge" of viewport / bottom of cell's node if startOffStart = 0
     *     |
     *     |
     *     |--------- bottom of cell's node if startOffStart is positive
     *     |
     * </code></pre>
     *
     * @param itemIndex   the index of the item in the list of all (not currently visible) cells
     * @param startOffEnd the amount by which to offset the "layoutY" value of the cell's node
     */
    public C placeStartFromEnd(int itemIndex, double startOffEnd) {
        C cell = getSizedCell(itemIndex);
        double y = sizeTracker.getViewportLength() + startOffEnd;
        relocate(cell, 0, y);
        cell.getNode().setVisible(true);
        return cell;
    }

    /**
     * Returns properly sized, but not properly positioned cell for the given
     * index.
     */
    C getSizedCell(int itemIndex) {
        C cell = cellManager.getCell(itemIndex);
        double breadth = sizeTracker.breadthFor(itemIndex);
        double length = sizeTracker.lengthFor(itemIndex);
        orientation.resize(cell, breadth, length);
        return cell;
    }

    private void relocate(C cell, double breadth0, double length0) {
        orientation.relocate(cell, breadth0, length0);
    }
}