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

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.reactfx.collection.MemoizationList;
import org.reactfx.util.Lists;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A VirtualFlow is a memory-efficient viewport that only renders enough of its content to completely fill up the
 * viewport through its {@link Navigator}. Based on the viewport's {@link Gravity}, it sequentially lays out the
 * {@link javafx.scene.Node}s of the {@link Cell}s until the viewport is completely filled up or it has no additional
 * cell's nodes to render.
 *
 * <p>
 * Since this viewport does not fully render all of its content, the scroll values are estimates based on the nodes
 * that are currently displayed in the viewport. If every node that could be rendered is the same width or same
 * height, then the corresponding scroll values (e.g., scrollX or totalX) are accurate.
 * <em>Note:</em> the VirtualFlow does not have scroll bars by default. These can be added by wrapping this object
 * in a {@link VirtualizedScrollPane}.
 * </p>
 *
 * <p>
 * Since the viewport can be used to lay out its content horizontally or vertically, it uses two
 * orientation-agnostic terms to refer to its width and height: "breadth" and "length," respectively. The viewport
 * always lays out its {@link Cell cell}'s {@link javafx.scene.Node}s from "top-to-bottom" or from "bottom-to-top"
 * (these terms should be understood in reference to the viewport's {@link OrientationHelper orientation} and
 * {@link Gravity}). Thus, its length ("height") is independent as the viewport's bounds are dependent upon
 * its parent's bounds whereas its breadth ("width") is dependent upon its length.
 * </p>
 *
 * @param <T> the model content that the {@link Cell#getNode() cell's node} renders
 * @param <C> the {@link Cell} that can render the model with a {@link javafx.scene.Node}.
 */
public class VirtualFlow<T, C extends Cell<T, ?>> extends Region implements Virtualized {

    /**
     * Determines how the cells in the viewport should be laid out and where any extra unused space should exist
     * if there are not enough cells to completely fill up the viewport
     */
    public enum Gravity {
        /**
         * If using a {@link VerticalHelper vertical viewport}, lays out the content from top-to-bottom. The first
         * visible item will appear at the top and the last visible item (or unused space) towards the bottom.
         * <p>
         * If using a {@link HorizontalHelper horizontal viewport}, lays out the content from left-to-right. The first
         * visible item will appear at the left and the last visible item (or unused space) towards the right.
         * </p>
         */
        FRONT,
        /**
         * If using a {@link VerticalHelper vertical viewport}, lays out the content from bottom-to-top. The first
         * visible item will appear at the bottom and the last visible item (or unused space) towards the top.
         * <p>
         * If using a {@link HorizontalHelper horizontal viewport}, lays out the content from right-to-left. The first
         * visible item will appear at the right and the last visible item (or unused space) towards the left.
         * </p>
         */
        REAR
    }

    /**
     * Creates a viewport that lays out content horizontally from left to right
     */
    public static <T, C extends Cell<T, ?>> VirtualFlow<T, C> createHorizontal(
            ObservableList<T> items,
            Function<? super T, ? extends C> cellFactory) {
        return createHorizontal(items, cellFactory, Gravity.FRONT);
    }

    /**
     * Creates a viewport that lays out content horizontally
     */
    public static <T, C extends Cell<T, ?>> VirtualFlow<T, C> createHorizontal(
            ObservableList<T> items,
            Function<? super T, ? extends C> cellFactory,
            Gravity gravity) {
        return new VirtualFlow<>(items, cellFactory, new HorizontalHelper(), gravity);
    }

    /**
     * Creates a viewport that lays out content vertically from top to bottom
     */
    public static <T, C extends Cell<T, ?>> VirtualFlow<T, C> createVertical(
            ObservableList<T> items,
            Function<? super T, ? extends C> cellFactory) {
        return createVertical(items, cellFactory, Gravity.FRONT);
    }

    /**
     * Creates a viewport that lays out content vertically from top to bottom
     */
    public static <T, C extends Cell<T, ?>> VirtualFlow<T, C> createVertical(
            ObservableList<T> items,
            Function<? super T, ? extends C> cellFactory,
            Gravity gravity) {
        return new VirtualFlow<>(items, cellFactory, new VerticalHelper(), gravity);
    }

    private final ObservableList<T> items;
    private final OrientationHelper orientation;
    private final CellListManager<T, C> cellListManager;
    private final SizeTracker sizeTracker;
    private final CellPositioner<T, C> cellPositioner;
    private final Navigator<T, C> navigator;

    private final StyleableObjectProperty<Gravity> gravity = new StyleableObjectProperty<>() {
        @Override
        public Object getBean() {
            return VirtualFlow.this;
        }

        @Override
        public String getName() {
            return "gravity";
        }

        @Override
        public CssMetaData<? extends Styleable, Gravity> getCssMetaData() {
            return GRAVITY;
        }
    };

    // non-negative
    private final Var<Double> breadthOffset0 = Var.newSimpleVar(0.0);
    private final Var<Double> breadthOffset = breadthOffset0.asVar(this::setBreadthOffset);

    public Var<Double> breadthOffsetProperty() {
        return breadthOffset;
    }

    public Val<Double> totalBreadthEstimateProperty() {
        return sizeTracker.maxCellBreadthProperty();
    }

    private final Var<Double> lengthOffsetEstimate;

    public Var<Double> lengthOffsetEstimateProperty() {
        return lengthOffsetEstimate;
    }

    private VirtualFlow(
            ObservableList<T> items,
            Function<? super T, ? extends C> cellFactory,
            OrientationHelper orientation,
            Gravity gravity) {
        this.getStyleClass().add("virtual-flow");
        this.items = items;
        this.orientation = orientation;
        this.cellListManager = new CellListManager<T, C>(this, items, cellFactory);
        this.gravity.set(gravity);
        MemoizationList<C> cells = cellListManager.getLazyCellList();
        this.sizeTracker = new SizeTracker(orientation, layoutBoundsProperty(), cells);
        this.cellPositioner = new CellPositioner<>(cellListManager, orientation, sizeTracker);
        this.navigator = new Navigator<>(cellListManager, cellPositioner, orientation, this.gravity, sizeTracker);

        getChildren().add(navigator);
        clipProperty().bind(Val.map(
                layoutBoundsProperty(),
                b -> new Rectangle(b.getWidth(), b.getHeight())));

        lengthOffsetEstimate = new StableBidirectionalVar<>(sizeTracker.lengthOffsetEstimateProperty(), this::setLengthOffset);

        // scroll content by mouse scroll
        this.addEventHandler(ScrollEvent.ANY, se -> {
            scrollXBy(-se.getDeltaX());
            scrollYBy(-se.getDeltaY());
            se.consume();
        });
    }

    public void dispose() {
        navigator.dispose();
        sizeTracker.dispose();
        cellListManager.dispose();
    }

    /**
     * If the item is out of view, instantiates a new cell for the item.
     * The returned cell will be properly sized, but not properly positioned
     * relative to the cells in the viewport, unless it is itself in the
     * viewport.
     *
     * @return Cell for the given item. The cell will be valid only until the
     * next layout pass. It should therefore not be stored. It is intended to
     * be used for measurement purposes only.
     */
    public C getCell(int itemIndex) {
        Lists.checkIndex(itemIndex, items.size());
        return cellPositioner.getSizedCell(itemIndex);
    }

    /**
     * This method calls {@link #layout()} as a side-effect to insure
     * that the VirtualFlow is up-to-date in light of any changes
     */
    public Optional<C> getCellIfVisible(int itemIndex) {
        // insure cells are up-to-date in light of any changes
        layout();
        return cellPositioner.getCellIfVisible(itemIndex);
    }

    /**
     * This method calls {@link #layout()} as a side-effect to insure
     * that the VirtualFlow is up-to-date in light of any changes
     */
    public ObservableList<C> visibleCells() {
        // insure cells are up-to-date in light of any changes
        layout();
        return cellListManager.getLazyCellList().memoizedItems();
    }

    public Val<Double> totalLengthEstimateProperty() {
        return sizeTracker.totalLengthEstimateProperty();
    }

    public Bounds cellToViewport(C cell, Bounds bounds) {
        return cell.getNode().localToParent(bounds);
    }

    public Point2D cellToViewport(C cell, Point2D point) {
        return cell.getNode().localToParent(point);
    }

    public Point2D cellToViewport(C cell, double x, double y) {
        return cell.getNode().localToParent(x, y);
    }

    @Override
    protected void layoutChildren() {

        // navigate to the target position and fill viewport
        while (true) {
            double oldLayoutBreadth = sizeTracker.getCellLayoutBreadth();
            orientation.resize(navigator, oldLayoutBreadth, sizeTracker.getViewportLength());
            navigator.layout();
            if (oldLayoutBreadth == sizeTracker.getCellLayoutBreadth()) {
                break;
            }
        }

        double viewBreadth = orientation.breadth(this);
        double navigatorBreadth = orientation.breadth(navigator);
        double totalBreadth = breadthOffset0.getValue();
        double breadthDifference = navigatorBreadth - totalBreadth;
        if (breadthDifference < viewBreadth) {
            // viewport is scrolled all the way to the end of its breadth.
            //  but now viewport size (breadth) has increased
            double adjustment = viewBreadth - breadthDifference;
            orientation.relocate(navigator, -(totalBreadth - adjustment), 0);
            breadthOffset0.setValue(totalBreadth - adjustment);
        } else {
            orientation.relocate(navigator, -breadthOffset0.getValue(), 0);
        }
    }

    @Override
    protected final double computePrefWidth(double height) {
        switch (getContentBias()) {
            case HORIZONTAL: // vertical flow
                return computePrefBreadth();
            case VERTICAL: // horizontal flow
                return computePrefLength(height);
            default:
                throw new AssertionError("Unreachable code");
        }
    }

    @Override
    protected final double computePrefHeight(double width) {
        switch (getContentBias()) {
            case HORIZONTAL: // vertical flow
                return computePrefLength(width);
            case VERTICAL: // horizontal flow
                return computePrefBreadth();
            default:
                throw new AssertionError("Unreachable code");
        }
    }

    private double computePrefBreadth() {
        return 100;
    }

    private double computePrefLength(double breadth) {
        return 100;
    }

    @Override
    public final Orientation getContentBias() {
        return orientation.getContentBias();
    }

    void scrollLength(double deltaLength) {
        setLengthOffset(lengthOffsetEstimate.getValue() + deltaLength);
    }

    void scrollBreadth(double deltaBreadth) {
        setBreadthOffset(breadthOffset0.getValue() + deltaBreadth);
    }

    /**
     * Scroll the content horizontally by the given amount.
     *
     * @param deltaX positive value scrolls right, negative value scrolls left
     */
    @Override
    public void scrollXBy(double deltaX) {
        orientation.scrollHorizontallyBy(this, deltaX);
    }

    /**
     * Scroll the content vertically by the given amount.
     *
     * @param deltaY positive value scrolls down, negative value scrolls up
     */
    @Override
    public void scrollYBy(double deltaY) {
        orientation.scrollVerticallyBy(this, deltaY);
    }

    /**
     * Scroll the content horizontally to the pixel
     *
     * @param pixel - the pixel position to which to scroll
     */
    @Override
    public void scrollXToPixel(double pixel) {
        orientation.scrollHorizontallyToPixel(this, pixel);
    }

    /**
     * Scroll the content vertically to the pixel
     *
     * @param pixel - the pixel position to which to scroll
     */
    @Override
    public void scrollYToPixel(double pixel) {
        orientation.scrollVerticallyToPixel(this, pixel);
    }

    @Override
    public Val<Double> totalWidthEstimateProperty() {
        return orientation.widthEstimateProperty(this);
    }

    @Override
    public Val<Double> totalHeightEstimateProperty() {
        return orientation.heightEstimateProperty(this);
    }

    @Override
    public Var<Double> estimatedScrollXProperty() {
        return orientation.estimatedScrollXProperty(this);
    }

    @Override
    public Var<Double> estimatedScrollYProperty() {
        return orientation.estimatedScrollYProperty(this);
    }

    /**
     * Hits this virtual flow at the given coordinates.
     *
     * @param x x offset from the left edge of the viewport
     * @param y y offset from the top edge of the viewport
     * @return hit info containing the cell that was hit and coordinates
     * relative to the cell. If the hit was before the cells (i.e. above a
     * vertical flow content or left of a horizontal flow content), returns
     * a <em>hit before cells</em> containing offset from the top left corner
     * of the content. If the hit was after the cells (i.e. below a vertical
     * flow content or right of a horizontal flow content), returns a
     * <em>hit after cells</em> containing offset from the top right corner of
     * the content of a horizontal flow or bottom left corner of the content of
     * a vertical flow.
     */
    public VirtualFlowHit<C> hit(double x, double y) {
        double bOff = orientation.getX(x, y);
        double lOff = orientation.getY(x, y);

        bOff += breadthOffset0.getValue();

        if (items.isEmpty()) {
            return orientation.hitAfterCells(bOff, lOff);
        }

        layout();

        int firstVisible = getFirstVisibleIndex();
        firstVisible = navigator.fillBackwardFrom0(firstVisible, lOff);
        C firstCell = cellPositioner.getVisibleCell(firstVisible);

        int lastVisible = getLastVisibleIndex();
        lastVisible = navigator.fillForwardFrom0(lastVisible, lOff);
        C lastCell = cellPositioner.getVisibleCell(lastVisible);

        if (lOff < orientation.minY(firstCell)) {
            return orientation.hitBeforeCells(bOff, lOff - orientation.minY(firstCell));
        } else if (lOff >= orientation.maxY(lastCell)) {
            return orientation.hitAfterCells(bOff, lOff - orientation.maxY(lastCell));
        } else {
            for (int i = firstVisible; i <= lastVisible; ++i) {
                C cell = cellPositioner.getVisibleCell(i);
                if (lOff < orientation.maxY(cell)) {
                    return orientation.cellHit(i, cell, bOff, lOff - orientation.minY(cell));
                }
            }
            throw new AssertionError("unreachable code");
        }
    }

    /**
     * Forces the viewport to acts as though it scrolled from 0 to {@code viewportOffset}). <em>Note:</em> the
     * viewport makes an educated guess as to which cell is actually at {@code viewportOffset} if the viewport's
     * entire content was completely rendered.
     *
     * @param viewportOffset See {@link OrientationHelper} and its implementations for explanation on what the offset
     *                       means based on which implementation is used.
     */
    public void show(double viewportOffset) {
        if (viewportOffset < 0) {
            navigator.scrollCurrentPositionBy(viewportOffset);
        } else if (viewportOffset > sizeTracker.getViewportLength()) {
            navigator.scrollCurrentPositionBy(viewportOffset - sizeTracker.getViewportLength());
        } else {
            // do nothing, offset already in the viewport
        }
    }

    /**
     * Forces the viewport to show the given item by "scrolling" to it
     */
    public void show(int itemIdx) {
        navigator.setTargetPosition(new MinDistanceTo(itemIdx));
    }

    /**
     * Forces the viewport to show the given item as the first visible item as determined by its {@link Gravity}.
     */
    public void showAsFirst(int itemIdx) {
        navigator.setTargetPosition(new StartOffStart(itemIdx, 0.0));
    }

    /**
     * Forces the viewport to show the given item as the last visible item as determined by its {@link Gravity}.
     */
    public void showAsLast(int itemIdx) {
        navigator.setTargetPosition(new EndOffEnd(itemIdx, 0.0));
    }

    /**
     * Forces the viewport to show the given item by "scrolling" to it and then further "scrolling" by {@code offset}
     * in one layout call (e.g., this method does not "scroll" twice)
     *
     * @param offset the offset value as determined by the viewport's {@link OrientationHelper}.
     */
    public void showAtOffset(int itemIdx, double offset) {
        navigator.setTargetPosition(new StartOffStart(itemIdx, offset));
    }

    /**
     * Forces the viewport to show the given item by "scrolling" to it and then further "scrolling," so that the
     * {@code region} is visible, in one layout call (e.g., this method does not "scroll" twice).
     */
    public void show(int itemIndex, Bounds region) {
        navigator.showLengthRegion(itemIndex, orientation.minY(region), orientation.maxY(region));
        showBreadthRegion(orientation.minX(region), orientation.maxX(region));
    }

    /**
     * Get the index of the first visible cell (at the time of the last layout).
     *
     * @return The index of the first visible cell
     */
    public int getFirstVisibleIndex() {
        return navigator.getFirstVisibleIndex();
    }

    /**
     * Get the index of the last visible cell (at the time of the last layout).
     *
     * @return The index of the last visible cell
     */
    public int getLastVisibleIndex() {
        return navigator.getLastVisibleIndex();
    }

    private void showBreadthRegion(double fromX, double toX) {
        double bOff = breadthOffset0.getValue();
        double spaceBefore = fromX - bOff;
        double spaceAfter = sizeTracker.getViewportBreadth() - toX + bOff;
        if (spaceBefore < 0 && spaceAfter > 0) {
            double shift = Math.min(-spaceBefore, spaceAfter);
            setBreadthOffset(bOff - shift);
        } else if (spaceAfter < 0 && spaceBefore > 0) {
            double shift = Math.max(spaceAfter, -spaceBefore);
            setBreadthOffset(bOff - shift);
        }
    }

    void setLengthOffset(double pixels) {
        double total = totalLengthEstimateProperty().getOrElse(0.0);
        double length = sizeTracker.getViewportLength();
        double max = Math.max(total - length, 0);
        double current = lengthOffsetEstimate.getValue();

        if (pixels > max) pixels = max;
        if (pixels < 0) pixels = 0;

        double diff = pixels - current;
        if (diff == 0) {
            // do nothing
        } else if (Math.abs(diff) <= length) { // distance less than one screen
            navigator.scrollCurrentPositionBy(diff);
        } else {
            jumpToAbsolutePosition(pixels);
        }
    }

    void setBreadthOffset(double pixels) {
        double total = totalBreadthEstimateProperty().getValue();
        double breadth = sizeTracker.getViewportBreadth();
        double max = Math.max(total - breadth, 0);
        double current = breadthOffset0.getValue();

        if (pixels > max) pixels = max;
        if (pixels < 0) pixels = 0;

        if (pixels != current) {
            breadthOffset0.setValue(pixels);
            requestLayout();
            // TODO: could be safely relocated right away?
            // (Does relocation request layout?)
        }
    }

    private void jumpToAbsolutePosition(double pixels) {
        if (items.isEmpty()) {
            return;
        }

        // guess the first visible cell and its offset in the viewport
        double avgLen = sizeTracker.getAverageLengthEstimate().orElse(0.0);
        if (avgLen == 0.0) return;
        int first = (int) Math.floor(pixels / avgLen);
        double firstOffset = -(pixels % avgLen);

        if (first < items.size()) {
            navigator.setTargetPosition(new StartOffStart(first, firstOffset));
        } else {
            navigator.setTargetPosition(new EndOffEnd(items.size() - 1, 0.0));
        }
    }

    /**
     * The gravity of the virtual flow.  When there are not enough cells to fill
     * the full height (vertical virtual flow) or width (horizontal virtual flow),
     * the cells are placed either at the front (vertical: top, horizontal: left),
     * or rear (vertical: bottom, horizontal: right) of the virtual flow, depending
     * on the value of the gravity property.
     * <p>
     * The gravity can also be styled in CSS, using the "-flowless-gravity" property,
     * for example:
     * <pre>.virtual-flow { -flowless-gravity: rear; }</pre>
     */
    public ObjectProperty<Gravity> gravityProperty() {
        return gravity;
    }

    public Gravity getGravity() {
        return gravity.get();
    }

    public void setGravity(Gravity gravity) {
        this.gravity.set(gravity);
    }

    @SuppressWarnings("unchecked") // Because of the cast we have to perform, below
    private static final CssMetaData<VirtualFlow<?, ?>, Gravity> GRAVITY = new CssMetaData<>(
            "-flowless-gravity",
            // JavaFX seems to have an odd return type on getEnumConverter: "? extends Enum<?>", not E as the second generic type.
            // Even though if you look at the source, the EnumConverter type it uses does have the type E.
            // To get round this, we cast on return:
            StyleConverter.getEnumConverter(Gravity.class),
            Gravity.FRONT) {

        @Override
        public boolean isSettable(VirtualFlow virtualFlow) {
            return !virtualFlow.gravity.isBound();
        }

        @Override
        public StyleableProperty<Gravity> getStyleableProperty(VirtualFlow virtualFlow) {
            return virtualFlow.gravity;
        }
    };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
        List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Region.getClassCssMetaData());
        styleables.add(GRAVITY);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
