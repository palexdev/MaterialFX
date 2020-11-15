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

public class MFXDialog extends AbstractMFXDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-dialog";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-dialog.css").toString();

    //================================================================================
    // Constructors
    //================================================================================
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
