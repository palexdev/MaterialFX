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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
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
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXDialog.css");

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

        setManaged(isCenterBeforeShow());
        centerBeforeShow.addListener((observable, oldValue, newValue) -> {
            if (isDraggable()) {
                return;
            }
            setManaged(newValue);
        });
    }

    /**
     * This method sets the bottom of the dialog (extends BorderPane) to the specified HBox.
     * <p></p>
     * The idea is to give users an easy way to add actions to the dialog, you can add any
     * node wrapped in a HBox
     *
     * @param actionsBox the HBox which contains the nodes for actions
     */
    @Override
    public MFXDialog setActions(HBox actionsBox) {
        setBottom(actionsBox);
        return this;
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Tries to center the dialog before showing.
     */
    @Override
    public void computeSizeAndPosition() {
        Parent parent = this.getParent();
        if (!(parent instanceof Pane)) {
            return;
        }

        Pane dialogParent = (Pane) parent;
        double w = getLayoutBounds().getWidth();
        double h = getLayoutBounds().getHeight();
        double x = (dialogParent.getWidth() - w) / 2.0;
        double y = (dialogParent.getHeight() - h) / 2.0;
        resizeRelocate(x, y, w, h);
    }

    /**
     * Shows the dialog, computes the center and plays animations if requested.
     * <p></p>
     * Its also responsible for firing the following events: {@link MFXDialogEvent#BEFORE_OPEN_EVENT}, {@link MFXDialogEvent#ON_OPENED_EVENT}.
     */
    @Override
    public void show() {
        fireDialogEvent(MFXDialogEvent.BEFORE_OPEN_EVENT);
        if (isCenterBeforeShow()) {
            computeSizeAndPosition();
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
            inAnimation.setOnFinished(event -> fireDialogEvent(MFXDialogEvent.ON_OPENED_EVENT));
            setVisible(true);
            inAnimation.play();
        } else {
            if (scrimBackground.get()) {
                scrimEffect.modalScrim((Pane) getParent(), this, scrimOpacity.get());
            }
            setVisible(true);
            fireDialogEvent(MFXDialogEvent.ON_OPENED_EVENT);
        }
    }

    /**
     * Closes the dialog, plays animations if requested.
     * <p></p>
     * Its also responsible for firing the following events: {@link MFXDialogEvent#BEFORE_CLOSE_EVENT}, {@link MFXDialogEvent#ON_CLOSED_EVENT}.
     */
    @Override
    public void close() {
        fireDialogEvent(MFXDialogEvent.BEFORE_CLOSE_EVENT);
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
                fireDialogEvent(MFXDialogEvent.ON_CLOSED_EVENT);
            });
            outAnimation.play();
        } else {
            if (scrimBackground.get()) {
                scrimEffect.removeEffect((Pane) getParent());
            }
            setVisible(false);
            fireDialogEvent(MFXDialogEvent.ON_CLOSED_EVENT);
        }
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
