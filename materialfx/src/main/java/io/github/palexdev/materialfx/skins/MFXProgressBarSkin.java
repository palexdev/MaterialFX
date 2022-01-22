/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXProgressBar}.
 */
public class MFXProgressBarSkin extends SkinBase<MFXProgressBar> {
	//================================================================================
	// Properties
	//================================================================================
	private final StackPane container;
	private final Rectangle track;
	private final Rectangle bar1;
	private final Rectangle bar2;

	private ParallelTransition indeterminateAnimation;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXProgressBarSkin(MFXProgressBar progressBar) {
		super(progressBar);

		track = buildRectangle("track");
		track.heightProperty().bind(progressBar.heightProperty());
		track.widthProperty().bind(progressBar.widthProperty());

		bar1 = buildRectangle("bar1");
		bar1.heightProperty().bind(progressBar.heightProperty());

		bar2 = buildRectangle("bar2");
		bar2.heightProperty().bind(progressBar.heightProperty());
		bar2.visibleProperty().bind(progressBar.indeterminateProperty());

		Rectangle clip = new Rectangle();
		clip.heightProperty().bind(progressBar.heightProperty());
		clip.widthProperty().bind(progressBar.widthProperty());
		clip.arcHeightProperty().bind(track.arcHeightProperty());
		clip.arcWidthProperty().bind(track.arcWidthProperty());

		Group group = new Group(track, bar1, bar2);
		group.setClip(clip);
		group.setManaged(false);

		container = new StackPane(group);
		getChildren().setAll(container);

		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds listeners for: progress, width, visible, parent,scene and animation speed properties.
	 */
	private void setListeners() {
		MFXProgressBar progressBar = getSkinnable();

		progressBar.progressProperty().addListener((observable, oldValue, newValue) -> updateBars());
		progressBar.widthProperty().addListener((observable, oldValue, newValue) -> {
			if (!progressBar.isVisible() || progressBar.isDisabled()) return;
			resetBars();
			updateBars();
		});
		progressBar.visibleProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				resetBars();
				return;
			}
			resetBars();
			updateBars();
		});
		progressBar.disabledProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				resetBars();
				return;
			}
			resetBars();
			updateBars();
		});
		progressBar.parentProperty().addListener((observable, oldValue, newValue) -> {
			resetBars();
			updateBars();
		});
		NodeUtils.waitForScene(progressBar, () -> {
			if (!progressBar.isVisible() || progressBar.isDisabled()) return;
			resetBars();
			updateBars();
		}, true, false);
		progressBar.animationSpeedProperty().addListener((observable, oldValue, newValue) -> {
			resetBars();
			updateBars();
		});
	}

	/**
	 * Responsible for updating the progress bar state.
	 * <p></p>
	 * If it is indeterminate calls {@link #playIndeterminateAnimation()}, otherwise calls
	 * {@link #resetBars()} and {@link #updateProgress()}.
	 */
	protected void updateBars() {
		MFXProgressBar progressBar = getSkinnable();

		if (progressBar.isIndeterminate()) {
			playIndeterminateAnimation();
		} else {
			resetBars();
			updateProgress();
		}
	}

	/**
	 * Responsible for clearing the indeterminate animation (stop, clear children and set to null), and
	 * resetting the bars layout, scale and width properties.
	 */
	protected void resetBars() {
		if (indeterminateAnimation != null) {
			indeterminateAnimation.stop();
			indeterminateAnimation.getChildren().clear();
			indeterminateAnimation = null;
		}

		bar1.setLayoutX(0);
		bar1.setScaleX(1.0);
		bar1.setWidth(0);
		bar2.setLayoutX(0);
		bar2.setScaleX(1.0);
		bar2.setWidth(0);
	}

	/**
	 * Responsible for calculating the bar width according to the current progress
	 * (so when the progress bar is not indeterminate).
	 */
	protected void updateProgress() {
		MFXProgressBar progressBar = getSkinnable();

		double width = ((progressBar.getWidth()) * (progressBar.getProgress() * 100)) / 100;
		bar1.setWidth(width);
	}

	/**
	 * If the indeterminate animation is already playing returns.
	 * <p></p>
	 * Responsible for building the indeterminate animation.
	 */
	protected void playIndeterminateAnimation() {
		MFXProgressBar progressBar = getSkinnable();

		if (indeterminateAnimation != null) {
			return;
		}

		final double width = progressBar.getWidth() - (snappedLeftInset() + snappedRightInset());
		Animation bar1Animation = AnimationUtils.TimelineBuilder.build()
				.add(
						KeyFrames.of(Duration.ONE,
								new KeyValue(bar1.scaleXProperty(), 0.7),
								new KeyValue(bar1.layoutXProperty(), -width),
								new KeyValue(bar1.widthProperty(), width / 2),
								new KeyValue(bar2.layoutXProperty(), -width),
								new KeyValue(bar2.widthProperty(), width / 2)
						),
						KeyFrames.of(750, bar1.scaleXProperty(), 1.25, Interpolator.EASE_BOTH),
						KeyFrames.of(1300, bar1.layoutXProperty(), width, Interpolator.LINEAR),
						KeyFrames.of(1100, bar1.scaleXProperty(), 1.0, Interpolator.EASE_OUT)
				).getAnimation();

		Animation bar2Animation = AnimationUtils.TimelineBuilder.build()
				.add(
						KeyFrames.of(1100,
								new KeyValue(bar2.layoutXProperty(), width * 2, Interpolator.LINEAR),
								new KeyValue(bar2.scaleXProperty(), 2, Interpolator.EASE_BOTH)
						)
				).setDelay(1100).getAnimation();

		indeterminateAnimation = (ParallelTransition) AnimationUtils.ParallelBuilder.build()
				.add(bar1Animation)
				.add(bar2Animation)
				.setCycleCount(Timeline.INDEFINITE)
				.setRate(progressBar.getAnimationSpeed())
				.getAnimation();
		indeterminateAnimation.play();
	}

	/**
	 * Responsible for building the track and the bars for the progress bar.
	 */
	protected Rectangle buildRectangle(String styleClass) {
		Rectangle rectangle = new Rectangle();
		rectangle.getStyleClass().setAll(styleClass);
		rectangle.setStroke(Color.TRANSPARENT);
		rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
		rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
		rectangle.setStrokeType(StrokeType.INSIDE);
		rectangle.setStrokeWidth(0);
		return rectangle;
	}

	//================================================================================
	// OverrideMethods
	//================================================================================

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Math.max(100, leftInset + bar1.prefWidth(getSkinnable().getWidth()) + rightInset);
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Math.max(5, bar1.prefHeight(width)) + topInset + bottomInset;
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (indeterminateAnimation != null) {
			indeterminateAnimation.stop();
			indeterminateAnimation.getChildren().clear();
			indeterminateAnimation = null;
		}
	}
}
