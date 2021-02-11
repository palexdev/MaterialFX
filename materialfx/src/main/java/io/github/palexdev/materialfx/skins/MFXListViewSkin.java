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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.Set;
/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXListView}.
 * <p>
 * The most important thing this skin does is replacing the default scrollbars with new ones,
 * this makes styling them a lot more easy.
 */
public class MFXListViewSkin<T> extends ListViewSkin<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final VirtualFlow<?> virtualFlow;

    private final ScrollBar vBar;
    private final ScrollBar hBar;

    private final Timeline hideBars;
    private final Timeline showBars;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXListViewSkin(final MFXListView<T> listView) {
        super(listView);

        virtualFlow = (VirtualFlow<?>) listView.lookup(".virtual-flow");
        listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));

        this.vBar = new ScrollBar();
        this.hBar = new ScrollBar();
        bindScrollBars(listView);
        getChildren().addAll(vBar, hBar);

        vBar.setManaged(false);
        vBar.setOrientation(Orientation.VERTICAL);
        vBar.getStyleClass().add("mfx-scroll-bar");
        vBar.visibleProperty().bind(vBar.visibleAmountProperty().isNotEqualTo(0));

        hBar.setManaged(false);
        hBar.setOrientation(Orientation.HORIZONTAL);
        hBar.getStyleClass().add("mfx-scroll-bar");
        hBar.visibleProperty().bind(hBar.visibleAmountProperty().isNotEqualTo(0));

        hideBars = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(vBar.opacityProperty(), 0.0, MFXAnimationFactory.getInterpolator()),
                        new KeyValue(hBar.opacityProperty(), 0.0, MFXAnimationFactory.getInterpolator()))
        );
        showBars = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(vBar.opacityProperty(), 1.0, MFXAnimationFactory.getInterpolator()),
                        new KeyValue(hBar.opacityProperty(), 1.0, MFXAnimationFactory.getInterpolator()))
        );

        if (listView.isHideScrollBars()) {
            vBar.setOpacity(0.0);
            hBar.setOpacity(0.0);
        }

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: mouseExited, mouseEntered, hideScrollBars, and depthLevel properties.
     */
    private void setListeners() {
        MFXListView<T> listView = (MFXListView<T>) getSkinnable();

        listView.setOnMouseExited(event -> {
            if (listView.isHideScrollBars()) {
                hideBars.setDelay(listView.getHideAfter());

                if (hBar.isPressed()) {
                    hBar.pressedProperty().addListener(new ChangeListener<>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (!newValue) {
                                hideBars.play();
                            }
                            hBar.pressedProperty().removeListener(this);
                        }
                    });
                    return;
                }

                if (vBar.isPressed()) {
                    vBar.pressedProperty().addListener(new ChangeListener<>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (!newValue) {
                                hideBars.play();
                            }
                            vBar.pressedProperty().removeListener(this);
                        }
                    });
                    return;
                }

                hideBars.play();
            }
        });

        listView.setOnMouseEntered(event -> {
            if (hideBars.getStatus().equals(Animation.Status.RUNNING)) {
                hideBars.stop();
            }
            showBars.play();
        });

        listView.hideScrollBarsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hideBars.play();
            } else {
                showBars.play();
            }
            if (newValue &&
                    hideBars.getStatus() != Animation.Status.RUNNING ||
                    vBar.getOpacity() != 0 ||
                    hBar.getOpacity() != 0
            ) {
                vBar.setOpacity(0.0);
                hBar.setOpacity(0.0);
            }
        });

        listView.depthLevelProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));
            }
        });
    }

    private void bindScrollBars(MFXListView<?> listView) {
        final Set<Node> nodes = listView.lookupAll("VirtualScrollBar");
        for (Node node : nodes) {
            if (node instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) node;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    bindScrollBars(vBar, bar);
                } else if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
                    bindScrollBars(hBar, bar);
                }
            }
        }
    }

    private void bindScrollBars(ScrollBar scrollBarA, ScrollBar scrollBarB) {
        scrollBarA.valueProperty().bindBidirectional(scrollBarB.valueProperty());
        scrollBarA.minProperty().bindBidirectional(scrollBarB.minProperty());
        scrollBarA.maxProperty().bindBidirectional(scrollBarB.maxProperty());
        scrollBarA.visibleAmountProperty().bindBidirectional(scrollBarB.visibleAmountProperty());
        scrollBarA.unitIncrementProperty().bindBidirectional(scrollBarB.unitIncrementProperty());
        scrollBarA.blockIncrementProperty().bindBidirectional(scrollBarB.blockIncrementProperty());
    }

    private double estimateHeight() {
        double borderWidth = snapVerticalInsets();

        double cellsHeight = 0;
        for (int i = 0; i < virtualFlow.getCellCount(); i++) {
            ListCell<?> cell = (ListCell<?>) virtualFlow.getCell(i);

            cellsHeight += cell.getHeight();
        }

        return cellsHeight + borderWidth;
    }

    private double snapVerticalInsets() {
        return getSkinnable().snappedBottomInset() + getSkinnable().snappedTopInset();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 200;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final int itemsCount = getSkinnable().getItems().size();
        if (getSkinnable().maxHeightProperty().isBound() || itemsCount <= 0) {
            return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        final double fixedCellSize = getSkinnable().getFixedCellSize();
        double computedHeight = fixedCellSize != Region.USE_COMPUTED_SIZE ?
                fixedCellSize * itemsCount + snapVerticalInsets() : estimateHeight();
        double height = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        if (height > computedHeight) {
            height = computedHeight;
        }

        if (getSkinnable().getMaxHeight() > 0 && computedHeight > getSkinnable().getMaxHeight()) {
            return getSkinnable().getMaxHeight();
        }

        return height;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        Insets insets = getSkinnable().getInsets();
        final double prefWidth = vBar.prefWidth(-1);
        vBar.resizeRelocate(w - prefWidth - insets.getRight(), insets.getTop(), prefWidth, h - insets.getTop() - insets.getBottom());

        final double prefHeight = hBar.prefHeight(-1);
        hBar.resizeRelocate(insets.getLeft(), h - prefHeight - insets.getBottom(), w - insets.getLeft() - insets.getRight(), prefHeight);
    }
}
