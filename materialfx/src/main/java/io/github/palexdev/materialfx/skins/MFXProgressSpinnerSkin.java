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

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXProgressSpinner}.
 */
public class MFXProgressSpinnerSkin extends SkinBase<MFXProgressSpinner> {
	//================================================================================
	// Properties
	//================================================================================
	private boolean isValid = false;
	private boolean wasIndeterminate = false;
	private double arcLength = -1;

	private final Arc arc;
	private final Arc track;
	private final StackPane arcPane;
	private final Rectangle fillRect;
	private final Text text;

	private Timeline animation;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXProgressSpinnerSkin(MFXProgressSpinner spinner) {
		super(spinner);

		arc = new Arc();
		arc.setManaged(false);
		arc.setStartAngle(0);
		arc.setLength(180);
		arc.getStyleClass().setAll("arc");
		arc.setFill(Color.TRANSPARENT);
		arc.setStrokeWidth(3);

		track = new Arc();
		track.setManaged(false);
		track.setStartAngle(0);
		track.setLength(360);
		track.setStrokeWidth(3);
		track.getStyleClass().setAll("track");
		track.setFill(Color.TRANSPARENT);

		fillRect = new Rectangle();
		fillRect.setFill(Color.TRANSPARENT);

		text = new Text();
		text.getStyleClass().setAll("text", "percentage");

		final Group group = new Group(fillRect, track, arc, text);
		group.setManaged(false);

		arcPane = new StackPane(group);
		arcPane.setPrefSize(50, 50);
		getChildren().setAll(arcPane);

		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds listeners to: indeterminate, progress, visible, parent and scene properties.
	 */
	private void setListeners() {
		MFXProgressSpinner spinner = getSkinnable();

		spinner.indeterminateProperty().addListener((observable, oldValue, newValue) -> reset());
		spinner.progressProperty().addListener((observable, oldValue, newValue) -> updateProgress());
		spinner.visibleProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				resetAndStop();
			} else {
				updateAnimation();
			}
		});
		spinner.disabledProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				resetAndStop();
				return;
			}
			updateAnimation();
		});
		spinner.parentProperty().addListener((observable, oldValue, newValue) -> updateAnimation());

		spinner.color1Property().addListener((observable, oldValue, newValue) -> {
			if (!spinner.isIndeterminate()) return;
			clearAnimation();
			reset();
		});
		spinner.color2Property().addListener((observable, oldValue, newValue) -> {
			if (!spinner.isIndeterminate()) return;
			clearAnimation();
			reset();
		});
		spinner.color3Property().addListener((observable, oldValue, newValue) -> {
			if (!spinner.isIndeterminate()) return;
			clearAnimation();
			reset();
		});
		spinner.color4Property().addListener((observable, oldValue, newValue) -> {
			if (!spinner.isIndeterminate()) return;
			clearAnimation();
			reset();
		});

		NodeUtils.waitForScene(
				spinner,
				() -> {
					if (!spinner.isVisible() || spinner.isDisabled()) {
						resetAndStop();
						return;
					}
					updateAnimation();
				}, true, false
		);
	}

	/**
	 * Updates the progress.
	 */
	protected void updateProgress() {
		MFXProgressSpinner spinner = getSkinnable();

		final boolean isIndeterminate = spinner.isIndeterminate();
		if (!(isIndeterminate && wasIndeterminate)) {
			arcLength = -360 * spinner.getProgress();
			spinner.requestLayout();
		}
		wasIndeterminate = isIndeterminate;
	}

	/**
	 * Resets the spinner animation.
	 */
	private void reset() {
		MFXProgressSpinner spinner = getSkinnable();

		if (spinner.isIndeterminate()) {
			if (animation == null) {
				createAnimation();
			}
		} else {
			clearAnimation();
			arc.setStartAngle(spinner.getStartingAngle());
			updateProgress();
		}
	}

	/**
	 * Resets and stops the spinner animation.
	 */
	private void resetAndStop() {
		MFXProgressSpinner spinner = getSkinnable();

		if (spinner.isIndeterminate()) {
			if (animation != null) animation.stop();
		} else {
			clearAnimation();
			arc.setStartAngle(spinner.getStartingAngle());
			updateProgress();
		}
	}

	/**
	 * Clears the animation.
	 */
	private void clearAnimation() {
		if (animation != null) {
			animation.stop();
			animation.getKeyFrames().clear();
			animation = null;
		}
	}

	/**
	 * Updates the animation.
	 */
	private void updateAnimation() {
		MFXProgressSpinner spinner = getSkinnable();

		final boolean isTreeVisible = spinner.isVisible() &&
				spinner.getParent() != null &&
				spinner.getScene() != null;
		if (animation != null) {
			pauseTimeline(!isTreeVisible);
		} else if (isTreeVisible) {
			createAnimation();
		}
	}

	/**
	 * Creates the animation.
	 */
	private void createAnimation() {
		MFXProgressSpinner spinner = getSkinnable();

		Color color1 = spinner.getColor1();
		Color color2 = spinner.getColor2();
		Color color3 = spinner.getColor3();
		Color color4 = spinner.getColor4();

		if (!spinner.isIndeterminate()) return;
		if (arc.getStroke() == null) {
			arc.setStroke(color1);
		}

		KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6),
				new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
				new KeyValue(arc.startAngleProperty(), 1845 + spinner.getStartingAngle(), Interpolator.LINEAR));

		KeyFrame[] allFrames = Stream.of(
				getKeyFrames(0, 0, color1),
				getKeyFrames(450, 1.4, color2),
				getKeyFrames(900, 2.8, color3),
				getKeyFrames(1350, 4.2, color4),
				new KeyFrame[]{endingFrame}
		).flatMap(Arrays::stream).toArray(KeyFrame[]::new);

		if (animation != null) {
			animation.stop();
			animation.getKeyFrames().clear();
		}
		animation = AnimationUtils.TimelineBuilder.build()
				.add(allFrames)
				.setDelay(Duration.ZERO)
				.setCycleCount(Timeline.INDEFINITE)
				.getAnimation();
		if (spinner.isVisible() && !spinner.isDisabled()) animation.playFromStart();
	}

	/**
	 * Pauses the animation
	 */
	private void pauseTimeline(boolean pause) {
		MFXProgressSpinner spinner = getSkinnable();

		if (spinner.isIndeterminate()) {
			if (animation == null) {
				createAnimation();
			}
			if (pause) {
				animation.pause();
			} else {
				animation.play();
			}
		}
	}

	/**
	 * Creates the needed key frames for the animation.
	 */
	private KeyFrame[] getKeyFrames(double angle, double duration, Paint color) {
		MFXProgressSpinner spinner = getSkinnable();

		KeyFrame kf1 = new KeyFrame(Duration.seconds(duration),
				new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
				new KeyValue(arc.startAngleProperty(), angle + 45 + spinner.getStartingAngle(), Interpolator.LINEAR));

		KeyFrame kf2 = new KeyFrame(Duration.seconds(duration + 0.4),
				new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR),
				new KeyValue(arc.startAngleProperty(), angle + 90 + spinner.getStartingAngle(), Interpolator.LINEAR));

		KeyFrame kf3 = new KeyFrame(Duration.seconds(duration + 0.7),
				new KeyValue(arc.lengthProperty(), 250, Interpolator.LINEAR),
				new KeyValue(arc.startAngleProperty(), angle + 135 + spinner.getStartingAngle(), Interpolator.LINEAR));

		KeyFrame kf4 = new KeyFrame(Duration.seconds(duration + 1.1),
				new KeyValue(arc.lengthProperty(), 5, Interpolator.LINEAR),
				new KeyValue(arc.startAngleProperty(), angle + 435 + spinner.getStartingAngle(), Interpolator.LINEAR),
				new KeyValue(arc.strokeProperty(), color, Interpolator.EASE_BOTH));

		return new KeyFrame[]{kf1, kf2, kf3, kf4};
	}

	/**
	 * Updates the arc.
	 */
	private void updateArcLayout(double radius, double arcSize) {
		arc.setRadiusX(radius);
		arc.setRadiusY(radius);
		arc.setCenterX(arcSize / 2);
		arc.setCenterY(arcSize / 2);

		track.setRadiusX(radius);
		track.setRadiusY(radius);
		track.setCenterX(arcSize / 2);
		track.setCenterY(arcSize / 2);
		track.setStrokeWidth(arc.getStrokeWidth());
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXProgressSpinner spinner = getSkinnable();

		if (Region.USE_COMPUTED_SIZE == spinner.getRadius()) {
			return spinner.prefHeight(width);
		} else {
			return spinner.getRadius() * 2 + arc.getStrokeWidth() * 2;
		}
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXProgressSpinner spinner = getSkinnable();

		if (Region.USE_COMPUTED_SIZE == spinner.getRadius()) {
			return spinner.prefWidth(height);
		} else {
			return spinner.getRadius() * 2 + arc.getStrokeWidth() * 2;
		}
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return arcPane.prefWidth(-1);
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return arcPane.prefHeight(-1);
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		MFXProgressSpinner spinner = getSkinnable();

		final double strokeWidth = arc.getStrokeWidth();
		final double radius = Math.min(contentWidth, contentHeight) / 2 - strokeWidth / 2;
		final double arcSize = snapSizeX(radius * 2 + strokeWidth);

		arcPane.resizeRelocate((contentWidth - arcSize) / 2 + 1, (contentHeight - arcSize) / 2 + 1, arcSize, arcSize);
		updateArcLayout(radius, arcSize);

		fillRect.setWidth(arcSize);
		fillRect.setHeight(arcSize);

		if (!isValid) {
			reset();
			isValid = true;
		}

		if (!spinner.isIndeterminate()) {
			arc.setLength(arcLength);
			if (text.isVisible()) {
				final double progress = spinner.getProgress();
				int intProgress = (int) Math.round(progress * 100.0);
				Font font = text.getFont();
				text.setFont(Font.font(font.getFamily(), radius / 1.7));
				text.setText((progress > 1 ? 100 : intProgress) + "%");
				text.relocate((arcSize - text.getLayoutBounds().getWidth()) / 2, (arcSize - text.getLayoutBounds().getHeight()) / 2);
			}
		}
	}
}
