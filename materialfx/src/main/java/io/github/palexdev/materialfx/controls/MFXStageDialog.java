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

import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.factories.MFXStageDialogFactory;
import io.github.palexdev.materialfx.effects.MFXScrimEffect;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Wrapper class for creating MFXDialogs that use a new {@code Stage} to show instead of using a container.
 */
public class MFXStageDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final Stage dialogStage;
    private final BooleanProperty centerInOwner = new SimpleBooleanProperty(false);
    private boolean allowDrag = true;

    private final MFXScrimEffect scrimEffect = new MFXScrimEffect();
    private double scrimOpacity = 0.15;
    private boolean scrimBackground = false;

    private boolean animate = true;
    private double animationMillis = 400;
    private final ParallelTransition inAnimation = new ParallelTransition();
    private final ParallelTransition outAnimation = new ParallelTransition();

    private double xOffset;
    private double yOffset;

    private boolean manualPosition;
    private double manualX;
    private double manualY;

    private final EventHandler<WindowEvent> centerHandler = new EventHandler<>() {
        @Override
        public void handle(WindowEvent event) {
            double centerXPosition = dialogStage.getOwner().getX() + dialogStage.getOwner().getWidth()/2d;
            double centerYPosition = dialogStage.getOwner().getY() + dialogStage.getOwner().getHeight()/2d;
            dialogStage.setX(centerXPosition - dialogStage.getWidth()/2d);
            dialogStage.setY(centerYPosition - dialogStage.getHeight()/2d);
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXStageDialog(AbstractMFXDialog dialog) {
        this.dialogStage = MFXStageDialogFactory.buildDialog(dialog);
        initialize();
    }

    public MFXStageDialog(DialogType type, String title, String content) {
        this.dialogStage = MFXStageDialogFactory.buildDialog(type, title, content);
        initialize();
    }

    private void initialize() {
        this.dialogStage.getScene().getRoot().setOnMousePressed(event -> {
            xOffset = dialogStage.getX() - event.getScreenX();
            yOffset = dialogStage.getY() - event.getScreenY();
        });
        this.dialogStage.getScene().getRoot().setOnMouseDragged(event -> {
            if (allowDrag) {
                dialogStage.setX(event.getScreenX() + xOffset);
                dialogStage.setY(event.getScreenY() + yOffset);
            }
        });
        this.centerInOwner.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dialogStage.addEventHandler(WindowEvent.WINDOW_SHOWN, centerHandler);
            } else {
                dialogStage.removeEventHandler(WindowEvent.WINDOW_SHOWN, centerHandler);
            }
        });

        getDialog().setCloseHandler(event -> close());
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Shows the dialog by showing the stage, center the stage in its owner and plays animations if requested
     */
    public void show() {
        if (animate) {
            resetAnimation();
        }

        if (scrimBackground) {
            if (dialogStage.getOwner() == null || dialogStage.getModality().equals(Modality.NONE)) {
                throw new IllegalStateException("Scrim background is set to true but the dialog stage owner is null or modality is not set!!");
            }
            if (animate) {
                Timeline fadeInScrim = MFXAnimationFactory.FADE_IN.build(scrimEffect.getScrimNode(), animationMillis);
                fadeInScrim.getKeyFrames().add(
                        new KeyFrame(Duration.ONE, event -> scrimEffect.scrimWindow(dialogStage.getOwner(), scrimOpacity))
                );
                inAnimation.getChildren().add(fadeInScrim);
            } else {
                scrimEffect.scrimWindow(dialogStage.getOwner(), scrimOpacity);
            }
        }

        if (animate) {
            inAnimation.play();
        }
        if (centerInOwner.get()) {
            if (dialogStage.getOwner() == null) {
                throw new NullPointerException("Center in owner is set to true but dialog stage owner is null!!");
            }
        }

        if (!centerInOwner.get() && manualPosition) {
            dialogStage.setX(manualX);
            dialogStage.setY(manualY);
        }

        this.dialogStage.show();
    }

    /**
     * Closes the dialog by closing the stage, plays animations if requested.
     */
    public void close() {
        if (animate) {
            resetAnimation();
        }

        if (scrimBackground) {
            if (animate) {
                Timeline fadeOutScrim = MFXAnimationFactory.FADE_OUT.build(scrimEffect.getScrimNode(), animationMillis);
                fadeOutScrim.setOnFinished(event -> scrimEffect.removeEffect(dialogStage.getOwner()));
                outAnimation.getChildren().add(fadeOutScrim);
            } else {
                scrimEffect.removeEffect(dialogStage.getOwner());
            }
        }

        if (animate) {
            outAnimation.setOnFinished(event -> this.dialogStage.close());
            outAnimation.play();
        } else {
            this.dialogStage.close();
        }
    }

    /**
     * Sets the stage's owner
     * @see Stage
     */
    public void setOwner(Window owner) {
        try {
            this.dialogStage.initOwner(owner);
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Sets the stage modality
     * @see Stage
     */
    public void setModality(Modality modality) {
        try {
            this.dialogStage.initModality(modality);
        } catch (IllegalStateException ignored) {
        }
    }

    public void toFront() {
        this.dialogStage.toFront();
    }

    public void toBack() {
        this.dialogStage.toBack();
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.dialogStage.setAlwaysOnTop(alwaysOnTop);
    }

    /**
     * Returns the AbstractMFXDialog associated with this MFXStageDialog
     * You can only change title and content as other properties are ignored
     */
    public AbstractMFXDialog getDialog() {
        return (AbstractMFXDialog) this.dialogStage.getScene().getRoot();
    }

    /**
     * Resets all parallel animations to one single animation, respectively FADE_IN and FADE_OUT
     */
    private void resetAnimation() {
        this.inAnimation.getChildren().clear();
        this.inAnimation.getChildren().add(MFXAnimationFactory.FADE_IN.build(dialogStage.getScene().getRoot(), animationMillis));

        this.outAnimation.getChildren().clear();
        outAnimation.getChildren().add(MFXAnimationFactory.FADE_OUT.build(dialogStage.getScene().getRoot(), animationMillis));
    }

    public boolean isCenterInOwner() {
        return centerInOwner.get();
    }

    public BooleanProperty centerInOwnerProperty() {
        return centerInOwner;
    }

    public void setCenterInOwner(boolean centerInOwner) {
        this.centerInOwner.set(centerInOwner);
    }

    public boolean isAllowDrag() {
        return allowDrag;
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
    }

    public void setScrimOpacity(double scrimOpacity) {
        this.scrimOpacity = scrimOpacity;
    }

    public void setScrimBackground(boolean scrimBackground) {
        this.scrimBackground = scrimBackground;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void setAnimationMillis(double animationMillis) {
        this.animationMillis = animationMillis;
    }

    public boolean isManualPosition() {
        return manualPosition;
    }

    public void setManualPosition(boolean manualPosition) {
        this.manualPosition = manualPosition;
    }

    public double getManualX() {
        return manualX;
    }

    public void setManualX(double manualX) {
        this.manualX = manualX;
    }

    public double getManualY() {
        return manualY;
    }

    public void setManualY(double manualY) {
        this.manualY = manualY;
    }
}
