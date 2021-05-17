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

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.animation.*;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXProgressBar}.
 */
public class MFXProgressBarSkin extends SkinBase<MFXProgressBar> {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane track;
    private final StackPane bar1;
    private final StackPane bar2;

    private boolean wasIndeterminate = false;
    private double barWidth = 0;
    private ParallelTransition indeterminateTransition;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXProgressBarSkin(MFXProgressBar progressBar) {
        super(progressBar);

        track = new StackPane();
        track.getStyleClass().add("track");

        bar1 = new StackPane();
        bar1.getStyleClass().add("bar");
        bar2 = new StackPane();
        bar2.getStyleClass().add("bar");

        setListeners();
        getChildren().setAll(track, bar1, bar2);
    }

    //================================================================================
    // Methods
    //================================================================================
    private Rectangle buildClip() {
        MFXProgressBar progressBar = getSkinnable();

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(progressBar.widthProperty());
        clip.heightProperty().bind(progressBar.heightProperty());
        return clip;
    }

    /**
     * Adds listeners for: width, visible, parent and scene properties.
     */
    private void setListeners() {
        MFXProgressBar progressBar = getSkinnable();

        progressBar.progressProperty().addListener(invalidated -> {
            progressBar.requestLayout();
            updateProgress();
        });
        progressBar.widthProperty().addListener((observable, oldValue, newValue) -> updateProgress());
        progressBar.visibleProperty().addListener((observable, oldValue, newValue) -> updateAnimation());
        progressBar.parentProperty().addListener((observable, oldValue, newValue) -> updateAnimation());
        progressBar.sceneProperty().addListener((observable, oldValue, newValue) -> updateAnimation());
    }

    /**
     * Resets the animation.
     */
    private void clearAnimation() {
        if (indeterminateTransition != null) {
            indeterminateTransition.stop();
            indeterminateTransition.getChildren().clear();
            indeterminateTransition = null;
        }
    }

    /**
     * Creates the animation for the indeterminate bar.
     */
    private void createIndeterminateTimeline() {
        MFXProgressBar progressBar = getSkinnable();

        if (indeterminateTransition != null) {
            clearAnimation();
        }

        final double width = progressBar.getWidth() - (snappedLeftInset() + snappedRightInset());

        KeyFrame kf0 = new KeyFrame(Duration.ZERO,
                new KeyValue(bar1.scaleXProperty(), 0.7),
                new KeyValue(bar1.translateXProperty(), -width),
                new KeyValue(bar2.translateXProperty(), -width)
        );
        KeyFrame kf1 = new KeyFrame(Duration.millis(700),
                new KeyValue(bar1.scaleXProperty(), 1.25, Interpolator.EASE_BOTH)
        );
        KeyFrame kf2 = new KeyFrame(Duration.millis(1300),
                new KeyValue(bar1.translateXProperty(), width, Interpolator.LINEAR)
        );
        KeyFrame kf3 = new KeyFrame(Duration.millis(900),
                new KeyValue(bar1.scaleXProperty(), 1.0, Interpolator.EASE_OUT)
        );
        KeyFrame kf4 = new KeyFrame(Duration.millis(1100),
                new KeyValue(bar2.translateXProperty(), width * 2, Interpolator.LINEAR),
                new KeyValue(bar2.scaleXProperty(), 2.25, Interpolator.EASE_BOTH)
        );

        Timeline bar1Animation = new Timeline(kf0, kf1, kf2, kf3);
        Timeline bar2Animation = new Timeline(kf4);
        bar2Animation.setDelay(Duration.millis(1100));

        indeterminateTransition = new ParallelTransition(bar1Animation, bar2Animation);
        indeterminateTransition.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Pauses/Resumes the animation.
     */
    private void updateTimeline(boolean pause) {
        MFXProgressBar progressBar = getSkinnable();

        if (progressBar.isIndeterminate()) {
            if (indeterminateTransition == null) {
                createIndeterminateTimeline();
            }
            if (pause) {
                indeterminateTransition.pause();
            } else {
                indeterminateTransition.play();
            }
        }
    }

    private void updateAnimation() {
        final boolean isTreeVisible = isTreeVisible();
        if (indeterminateTransition != null) {
            updateTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            createIndeterminateTimeline();
        }
    }

    /**
     * Updates the bar progress.
     */
    private void updateProgress() {
        MFXProgressBar progressBar = getSkinnable();

        final boolean isIndeterminate = progressBar.isIndeterminate();
        if (!(isIndeterminate && wasIndeterminate)) {
            barWidth = ((int) (progressBar.getWidth() - snappedLeftInset() - snappedRightInset()) * 2
                    * Math.min(1, Math.max(0, progressBar.getProgress()))) / 2.0F;
            progressBar.requestLayout();
        }
        wasIndeterminate = isIndeterminate;
    }

    private boolean isTreeVisible() {
        MFXProgressBar progressBar = getSkinnable();
        return progressBar.isVisible() && progressBar.getParent() != null && progressBar.getScene() != null;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(100, leftInset + bar1.prefWidth(getSkinnable().getWidth()) + rightInset);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + bar1.prefHeight(width) + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (indeterminateTransition != null) {
            clearAnimation();
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXProgressBar progressBar = getSkinnable();

        track.resizeRelocate(x, y, w, h);
        bar1.resizeRelocate(x, y, progressBar.isIndeterminate() ? w / 2 : barWidth, h);
        bar2.resizeRelocate(x, y, progressBar.isIndeterminate() ? w / 2 : 0, h);

        if (progressBar.isIndeterminate()) {
            bar1.setTranslateX(-w);
            bar2.setTranslateX(-w);
            createIndeterminateTimeline();
            indeterminateTransition.play();
            progressBar.setClip(buildClip());
        } else {
            bar1.setTranslateX(0);
            bar2.setTranslateX(0);
            clearAnimation();
            progressBar.setClip(null);
        }
    }
}
