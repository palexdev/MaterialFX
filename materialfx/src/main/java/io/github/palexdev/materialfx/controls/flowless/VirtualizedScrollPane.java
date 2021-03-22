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

import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

@DefaultProperty("content")
public class VirtualizedScrollPane<V extends Node & Virtualized> extends Region implements Virtualized {

    private static final PseudoClass CONTENT_FOCUSED = PseudoClass.getPseudoClass("content-focused");

    private final ScrollBar hBar;
    private final ScrollBar vBar;
    private final V content;
    private final ChangeListener<Boolean> contentFocusedListener;

    private final Var<Double> hBarValue;
    private final Var<Double> vBarValue;

    /**
     * The Policy for the Horizontal ScrollBar
     */
    private final Var<ScrollPane.ScrollBarPolicy> hBarPolicy;

    public final ScrollPane.ScrollBarPolicy getHBarPolicy() {
        return hBarPolicy.getValue();
    }

    public final void setHBarPolicy(ScrollPane.ScrollBarPolicy value) {
        hBarPolicy.setValue(value);
    }

    public final Var<ScrollPane.ScrollBarPolicy> hBarPolicyProperty() {
        return hBarPolicy;
    }

    /**
     * The Policy for the Vertical ScrollBar
     */
    private final Var<ScrollPane.ScrollBarPolicy> vBarPolicy;

    public final ScrollPane.ScrollBarPolicy getVBarPolicy() {
        return vBarPolicy.getValue();
    }

    public final void setVBarPolicy(ScrollPane.ScrollBarPolicy value) {
        vBarPolicy.setValue(value);
    }

    public final Var<ScrollPane.ScrollBarPolicy> vBarPolicyProperty() {
        return vBarPolicy;
    }

    /**
     * Constructs a VirtualizedScrollPane with the given content and policies
     */
    public VirtualizedScrollPane(
            @NamedArg("content") V content,
            @NamedArg("hPolicy") ScrollPane.ScrollBarPolicy hPolicy,
            @NamedArg("vPolicy") ScrollPane.ScrollBarPolicy vPolicy
    ) {
        this.getStyleClass().add("virtualized-scroll-pane");
        this.content = content;

        // create scrollbars
        hBar = new ScrollBar();
        vBar = new ScrollBar();
        hBar.setOrientation(Orientation.HORIZONTAL);
        vBar.setOrientation(Orientation.VERTICAL);

        // scrollbar ranges
        hBar.setMin(0);
        vBar.setMin(0);
        hBar.maxProperty().bind(content.totalWidthEstimateProperty());
        vBar.maxProperty().bind(content.totalHeightEstimateProperty());

        // scrollbar increments
        setupUnitIncrement(hBar);
        setupUnitIncrement(vBar);
        hBar.blockIncrementProperty().bind(hBar.visibleAmountProperty());
        vBar.blockIncrementProperty().bind(vBar.visibleAmountProperty());

        // scrollbar positions
        Var<Double> hPosEstimate = Val
                .combine(
                        content.estimatedScrollXProperty(),
                        Val.map(content.layoutBoundsProperty(), Bounds::getWidth),
                        content.totalWidthEstimateProperty(),
                        VirtualizedScrollPane::offsetToScrollbarPosition)
                .asVar(this::setHPosition);
        Var<Double> vPosEstimate = Val
                .combine(
                        content.estimatedScrollYProperty(),
                        Val.map(content.layoutBoundsProperty(), Bounds::getHeight),
                        content.totalHeightEstimateProperty(),
                        VirtualizedScrollPane::offsetToScrollbarPosition)
                .orElseConst(0.0)
                .asVar(this::setVPosition);
        hBarValue = Var.doubleVar(hBar.valueProperty());
        vBarValue = Var.doubleVar(vBar.valueProperty());
        Bindings.bindBidirectional(hBarValue, hPosEstimate);
        Bindings.bindBidirectional(vBarValue, vPosEstimate);

        // scrollbar visibility
        hBarPolicy = Var.newSimpleVar(hPolicy);
        vBarPolicy = Var.newSimpleVar(vPolicy);

        Val<Double> layoutWidth = Val.map(layoutBoundsProperty(), Bounds::getWidth);
        Val<Double> layoutHeight = Val.map(layoutBoundsProperty(), Bounds::getHeight);
        Val<Boolean> needsHBar0 = Val.combine(
                content.totalWidthEstimateProperty(),
                layoutWidth,
                (cw, lw) -> cw > lw);
        Val<Boolean> needsVBar0 = Val.combine(
                content.totalHeightEstimateProperty(),
                layoutHeight,
                (ch, lh) -> ch > lh);
        Val<Boolean> needsHBar = Val.combine(
                needsHBar0,
                needsVBar0,
                content.totalWidthEstimateProperty(),
                vBar.widthProperty(),
                layoutWidth,
                (needsH, needsV, cw, vbw, lw) -> needsH || needsV && cw + vbw.doubleValue() > lw);
        Val<Boolean> needsVBar = Val.combine(
                needsVBar0,
                needsHBar0,
                content.totalHeightEstimateProperty(),
                hBar.heightProperty(),
                layoutHeight,
                (needsV, needsH, ch, hbh, lh) -> needsV || needsH && ch + hbh.doubleValue() > lh);

        Val<Boolean> shouldDisplayHorizontal = Val.flatMap(hBarPolicy, policy -> {
            switch (policy) {
                case NEVER:
                    return Val.constant(false);
                case ALWAYS:
                    return Val.constant(true);
                default: // AS_NEEDED
                    return needsHBar;
            }
        });
        Val<Boolean> shouldDisplayVertical = Val.flatMap(vBarPolicy, policy -> {
            switch (policy) {
                case NEVER:
                    return Val.constant(false);
                case ALWAYS:
                    return Val.constant(true);
                default: // AS_NEEDED
                    return needsVBar;
            }
        });

        // request layout later, because if currently in layout, the request is ignored
        shouldDisplayHorizontal.addListener(obs -> Platform.runLater(this::requestLayout));
        shouldDisplayVertical.addListener(obs -> Platform.runLater(this::requestLayout));

        hBar.visibleProperty().bind(shouldDisplayHorizontal);
        vBar.visibleProperty().bind(shouldDisplayVertical);

        contentFocusedListener = (obs, ov, nv) -> pseudoClassStateChanged(CONTENT_FOCUSED, nv);
        content.focusedProperty().addListener(contentFocusedListener);
        getChildren().addAll(content, hBar, vBar);
        getChildren().addListener((Observable obs) -> dispose());
    }

    /**
     * Constructs a VirtualizedScrollPane that only displays its horizontal and vertical scroll bars as needed
     */
    public VirtualizedScrollPane(@NamedArg("content") V content) {
        this(content, AS_NEEDED, AS_NEEDED);
    }

    /**
     * Does not unbind scrolling from Content before returning Content.
     *
     * @return - the content
     */
    public V getContent() {
        return content;
    }

    /**
     * Unbinds scrolling from Content before returning Content.
     *
     * @return - the content
     */
    public V removeContent() {
        getChildren().clear();
        return content;
    }

    private void dispose() {
        content.focusedProperty().removeListener(contentFocusedListener);
        hBarValue.unbindBidirectional(content.estimatedScrollXProperty());
        vBarValue.unbindBidirectional(content.estimatedScrollYProperty());
        unbindScrollBar(hBar);
        unbindScrollBar(vBar);
    }

    private void unbindScrollBar(ScrollBar bar) {
        bar.maxProperty().unbind();
        bar.unitIncrementProperty().unbind();
        bar.blockIncrementProperty().unbind();
        bar.visibleProperty().unbind();
    }

    @Override
    public Val<Double> totalWidthEstimateProperty() {
        return content.totalWidthEstimateProperty();
    }

    @Override
    public Val<Double> totalHeightEstimateProperty() {
        return content.totalHeightEstimateProperty();
    }

    @Override
    public Var<Double> estimatedScrollXProperty() {
        return content.estimatedScrollXProperty();
    }

    @Override
    public Var<Double> estimatedScrollYProperty() {
        return content.estimatedScrollYProperty();
    }

    @Override
    public void scrollXBy(double deltaX) {
        content.scrollXBy(deltaX);
    }

    @Override
    public void scrollYBy(double deltaY) {
        content.scrollYBy(deltaY);
    }

    @Override
    public void scrollXToPixel(double pixel) {
        content.scrollXToPixel(pixel);
    }

    @Override
    public void scrollYToPixel(double pixel) {
        content.scrollYToPixel(pixel);
    }

    @Override
    protected double computePrefWidth(double height) {
        return content.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return content.prefHeight(width);
    }

    @Override
    protected double computeMinWidth(double height) {
        return vBar.minWidth(-1);
    }

    @Override
    protected double computeMinHeight(double width) {
        return hBar.minHeight(-1);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return content.maxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return content.maxHeight(width);
    }

    @Override
    protected void layoutChildren() {
        double layoutWidth = snapSizeX(getLayoutBounds().getWidth());
        double layoutHeight = snapSizeY(getLayoutBounds().getHeight());
        boolean vbarVisible = vBar.isVisible();
        boolean hbarVisible = hBar.isVisible();
        double vbarWidth = snapSizeX(vbarVisible ? vBar.prefWidth(-1) : 0);
        double hbarHeight = snapSizeY(hbarVisible ? hBar.prefHeight(-1) : 0);

        double w = layoutWidth - vbarWidth;
        double h = layoutHeight - hbarHeight;

        content.resize(w, h);

        hBar.setVisibleAmount(w);
        vBar.setVisibleAmount(h);

        if (vbarVisible) {
            vBar.resizeRelocate(layoutWidth - vbarWidth, 0, vbarWidth, h);
        }

        if (hbarVisible) {
            hBar.resizeRelocate(0, layoutHeight - hbarHeight, w, hbarHeight);
        }
    }

    private void setHPosition(double pos) {
        double offset = scrollbarPositionToOffset(
                pos,
                content.getLayoutBounds().getWidth(),
                content.totalWidthEstimateProperty().getValue());
        content.estimatedScrollXProperty().setValue(offset);
    }

    private void setVPosition(double pos) {
        double offset = scrollbarPositionToOffset(
                pos,
                content.getLayoutBounds().getHeight(),
                content.totalHeightEstimateProperty().getValue());
        content.estimatedScrollYProperty().setValue(offset);
    }

    private static void setupUnitIncrement(ScrollBar bar) {
        bar.unitIncrementProperty().bind(new DoubleBinding() {
            {
                bind(bar.maxProperty(), bar.visibleAmountProperty());
            }

            @Override
            protected double computeValue() {
                double max = bar.getMax();
                double visible = bar.getVisibleAmount();
                return max > visible
                        ? 16 / (max - visible) * max
                        : 0;
            }
        });
    }

    private static double offsetToScrollbarPosition(
            double contentOffset, double viewportSize, double contentSize) {
        return contentSize > viewportSize
                ? contentOffset / (contentSize - viewportSize) * contentSize
                : 0;
    }

    private static double scrollbarPositionToOffset(
            double scrollbarPos, double viewportSize, double contentSize) {
        return contentSize > viewportSize
                ? scrollbarPos / contentSize * (contentSize - viewportSize)
                : 0;
    }
}
