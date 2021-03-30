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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * This is the implementation of a dialog following Google's material design guidelines in JavaFX.
 * <p>
 * It's a concrete implementation of {@code AbstractMFXDialog} and redefines the style class to "mfx-dialog"
 * for usage in CSS.
 * <p>
 * <b>Notice: the dialog is visible so during initialization, before showing it you should
 * use {@code setVisible(false)}</b>
 */
public class MFXDialog extends AbstractMFXDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-dialog";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-dialog.css");

    //================================================================================
    // Constructors
    //================================================================================

    /**
     * <b>Notice: the dialog is visible so during initialization, before showing it you should
     * use {@code setVisible(false)}</b>
     */
    public MFXDialog() {
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        getStylesheets().setAll(STYLESHEET);

        overlayClose.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                addOverlayHandler();
            } else {
                removeOverlayHandler();
            }
        }));

        isDraggable.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                makeDraggable();
            } else {
                clearDragHandlers();
            }
        });
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Tries to center the dialog before showing, currently works only for {@code AnchorPane}s
     */
    @Override
    public void computeCenter() {
        Parent parent = this.getParent();
        if (!(parent instanceof Pane)) {
            return;
        }

        Pane dialogParent = (Pane) parent;
        if (dialogParent instanceof AnchorPane) {
            double topBottom = (dialogParent.getHeight() - getPrefHeight()) / 2;
            double leftRight = (dialogParent.getWidth() - getPrefWidth()) / 2;
            setLayoutX(leftRight);
            setLayoutY(topBottom);
        }
    }

    /**
     * Shows the dialog, computes the center and plays animations if requested
     */
    @Override
    public void show() {
        if (centerBeforeShow) {
            computeCenter();
        }

        if (animateIn.get()) {
            inAnimation.getChildren().setAll(inAnimationType.build(this, animationMillis.get()));
            if (scrimBackground.get()) {
                Timeline fadeInScrim = MFXAnimationFactory.FADE_IN.build(scrimEffect.getScrimNode(), animationMillis.get());
                fadeInScrim.getKeyFrames().add(0,
                        new KeyFrame(Duration.ZERO, event -> scrimEffect.modalScrim((Pane) getParent(), this, scrimOpacity.get()))
                );
                inAnimation.getChildren().add(fadeInScrim);
            }
            inAnimation.play();
        } else {
            if (scrimBackground.get()) {
                scrimEffect.modalScrim((Pane) getParent(), this, scrimOpacity.get());
            }
        }
        setVisible(true);
    }

    /**
     * Closes the dialog, plays animations if requested
     */
    @Override
    public void close() {
        if (animateOut.get()) {
            outAnimation.getChildren().setAll(outAnimationType.build(this, animationMillis.get()));
            if (scrimBackground.get()) {
                Timeline fadeOutScrim = MFXAnimationFactory.FADE_OUT.build(scrimEffect.getScrimNode(), animationMillis.get());
                fadeOutScrim.setOnFinished(event -> scrimEffect.removeEffect((Pane) getParent()));
                outAnimation.getChildren().add(fadeOutScrim);
            }
            outAnimation.setOnFinished(event -> {
                setVisible(false);
                setOpacity(1.0);
            });
            outAnimation.play();
        } else {
            if (scrimBackground.get()) {
                scrimEffect.removeEffect((Pane) getParent());
            }
            setVisible(false);
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
