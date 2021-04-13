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

import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListCell;
import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListView;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.flowless.MFXVirtualizedScrollPane;
import io.github.palexdev.materialfx.controls.flowless.VirtualFlow;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.util.Duration;

/**
 * Implementation of the skin used by all list views based on Flowless.
 */
public class MFXFlowlessListViewSkin<T> extends SkinBase<AbstractMFXFlowlessListView<T, ?, ?>> {
    //================================================================================
    // Properties
    //================================================================================
    private final ScrollBar vBar;
    private final ScrollBar hBar;
    private Timeline hideBars;
    private Timeline showBars;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFlowlessListViewSkin(AbstractMFXFlowlessListView<T, ?, ?> listView) {
        super(listView);

        VirtualFlow<T, AbstractMFXFlowlessListCell<T>> flow = VirtualFlow.createVertical(
                listView.getItems(),
                listView.getCellFactory()
        );
        flow.setId("virtualFlow");
        flow.getStylesheets().setAll(listView.getUserAgentStylesheet());

        MFXVirtualizedScrollPane<VirtualFlow<T, AbstractMFXFlowlessListCell<T>>> virtualScrollPane = new MFXVirtualizedScrollPane<>(flow);
        virtualScrollPane.setId("virtualScrollPane");
        virtualScrollPane.getStylesheets().setAll(listView.getUserAgentStylesheet());

        vBar = virtualScrollPane.getVBar();
        hBar = virtualScrollPane.getHBar();

        hideBars = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(vBar.opacityProperty(), 0.0, MFXAnimationFactory.getInterpolatorV1()),
                        new KeyValue(hBar.opacityProperty(), 0.0, MFXAnimationFactory.getInterpolatorV1()))
        );
        showBars = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(vBar.opacityProperty(), 1.0, MFXAnimationFactory.getInterpolatorV1()),
                        new KeyValue(hBar.opacityProperty(), 1.0, MFXAnimationFactory.getInterpolatorV1()))
        );

        if (listView.isHideScrollBars()) {
            vBar.setOpacity(0.0);
            hBar.setOpacity(0.0);
        }

        getChildren().add(virtualScrollPane);

        listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Calls {@link #setScrollBarHandlers()}, adds a listener to the list view's depth property.
     */
    private void setListeners() {
        AbstractMFXFlowlessListView<T, ?, ?> listView = getSkinnable();

        setScrollBarHandlers();
        listView.depthLevelProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));
            }
        });
    }

    /**
     * Sets up the scroll bars behavior.
     */
    private void setScrollBarHandlers() {
        AbstractMFXFlowlessListView<T, ?, ?> listView = getSkinnable();

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

    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + 200 + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + 350 + bottomInset;
    }

    @Override
    public void dispose() {
        super.dispose();

        if (hideBars != null) {
            hideBars = null;
        }
        if (showBars != null) {
            showBars = null;
        }
    }
}
