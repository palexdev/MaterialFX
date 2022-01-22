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

package io.github.palexdev.materialfx.effects.ripple;

import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.List;

import static io.github.palexdev.materialfx.effects.MFXDepthManager.shadowOf;

/**
 * Convenience class for creating highly customizable ripple effects.
 * <p>
 * Extends {@code Group} and sets the style class to "ripple-generator" for usage in CSS.
 */
@Deprecated
public class RippleGenerator extends Group {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "ripple-generator";
	private static final StyleablePropertyFactory<RippleGenerator> FACTORY = new StyleablePropertyFactory<>(Group.getClassCssMetaData());

	private final Region region;

	private RippleClipTypeFactory rippleClipTypeFactory = new RippleClipTypeFactory(RippleClipType.RECTANGLE);
	private DepthLevel level = null;
	//private final Interpolator rippleInterpolator = Interpolator.SPLINE(0.1, 0.50, 0.3, 0.85);
	private final StyleableObjectProperty<Paint> rippleColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.RIPPLE_COLOR,
			this,
			"rippleColor",
			Color.ROYALBLUE
	);

	private final StyleableDoubleProperty rippleRadius = new SimpleStyleableDoubleProperty(
			StyleableProperties.RIPPLE_RADIUS,
			this,
			"rippleRadius",
			10.0
	);
	private final StyleableBooleanProperty animateBackground = new SimpleStyleableBooleanProperty(
			StyleableProperties.ANIMATE_BACKGROUND,
			this,
			"animateBackground",
			true
	);
	private final ObjectProperty<Duration> inDuration = new SimpleObjectProperty<>(Duration.millis(700));
	private final ObjectProperty<Duration> outDuration = new SimpleObjectProperty<>(inDuration.get().divide(2));

	private double generatorCenterX = 0.0;
	private double generatorCenterY = 0.0;

	//================================================================================
	// Constructors
	//================================================================================
	public RippleGenerator(Region region) {
		this.region = region;
		getStyleClass().add(STYLE_CLASS);
		inDuration.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				outDuration.set(newValue.divide(2));
			}
		});
		outDuration.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				inDuration.set(newValue.multiply(2));
			}
		});
	}

	public RippleGenerator(Region region, DepthLevel shadowLevel) {
		this(region);
		this.level = shadowLevel;
	}

	public RippleGenerator(Region region, RippleClipTypeFactory factory) {
		this(region);
		this.rippleClipTypeFactory = factory;
	}

	public RippleGenerator(Region region, DepthLevel shadowLevel, RippleClipTypeFactory factory) {
		this(region, shadowLevel);
		this.rippleClipTypeFactory = factory;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Creates a new {@code Ripple} at the specified coordinates.
	 * <p>
	 * Each {@code Ripple} is a new instance, this allows multiple ripples to be generated at the same time.
	 */
	public void createRipple() {
		final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
		getChildren().add(ripple);

		Shape shape = rippleClipTypeFactory.build(region);
		if (animateBackground.get()) {
			shape.setFill(rippleColor.get());
			shape.setOpacity(0);
			getChildren().add(0, shape);

			KeyValue keyValueIn = new KeyValue(shape.opacityProperty(), 0.3);
			KeyValue keyValueOut = new KeyValue(shape.opacityProperty(), 0);
			KeyFrame keyFrameIn = new KeyFrame(inDuration.get(), keyValueIn);
			KeyFrame keyFrameOut = new KeyFrame(outDuration.get(), keyValueOut);
			ripple.inAnimation.getKeyFrames().add(keyFrameIn);
			ripple.outAnimation.getKeyFrames().add(keyFrameOut);
		}

		ripple.parallelTransition.setOnFinished(event -> getChildren().removeAll(ripple, shape));
		ripple.parallelTransition.play();
	}

	public void setGeneratorCenterX(double generatorCenterX) {
		this.generatorCenterX = generatorCenterX;
	}

	public void setGeneratorCenterY(double generatorCenterY) {
		this.generatorCenterY = generatorCenterY;
	}

	public void setRippleClipTypeFactory(RippleClipTypeFactory rippleClipTypeFactory) {
		this.rippleClipTypeFactory = rippleClipTypeFactory;
	}

	public Paint getRippleColor() {
		return rippleColor.get();
	}

	public final StyleableObjectProperty<Paint> rippleColorProperty() {
		return rippleColor;
	}

	public void setRippleColor(Paint rippleColor) {
		this.rippleColor.set(rippleColor);
	}

	public double getRippleRadius() {
		return rippleRadius.get();
	}

	public StyleableDoubleProperty rippleRadiusProperty() {
		return rippleRadius;
	}

	public void setRippleRadius(double rippleRadius) {
		this.rippleRadius.set(rippleRadius);
	}

	public boolean isAnimateBackground() {
		return animateBackground.get();
	}

	public StyleableBooleanProperty animateBackgroundProperty() {
		return animateBackground;
	}

	public void setAnimateBackground(boolean animateBackground) {
		this.animateBackground.set(animateBackground);
	}

	public Duration getInDuration() {
		return inDuration.get();
	}

	public ObjectProperty<Duration> inDurationProperty() {
		return inDuration;
	}

	public void setInDuration(Duration inDuration) {
		this.inDuration.set(inDuration);
	}

	public Duration getOutDuration() {
		return outDuration.get();
	}

	public ObjectProperty<Duration> outDurationProperty() {
		return outDuration;
	}

	public void setOutDuration(Duration outDuration) {
		this.outDuration.set(outDuration);
	}

	/**
	 * This class defines a ripple as a {@code Circle} and contains all it's properties, mainly
	 * it builds the animation of the ripple.
	 */
	private class Ripple extends Circle {
		//================================================================================
		// Properties
		//================================================================================
		private final int shadowDelta = 1;

		private final Timeline inAnimation = new Timeline();
		private final Timeline outAnimation = new Timeline();
		private final Timeline shadowAnimation = new Timeline();
		private final SequentialTransition sequentialTransition = new SequentialTransition();
		private final ParallelTransition parallelTransition = new ParallelTransition();

		//================================================================================
		// Constructors
		//================================================================================
		private Ripple(double centerX, double centerY) {
			super(centerX, centerY, 0, Color.TRANSPARENT);
			setFill(rippleColor.get());
			setClip(rippleClipTypeFactory.build(region));
			buildAnimation();
		}

		//================================================================================
		// Methods
		//================================================================================

		/**
		 * Build the ripple's animation
		 */
		private void buildAnimation() {
			KeyValue keyValue1 = new KeyValue(radiusProperty(), rippleRadius.get());
			KeyValue keyValue2 = new KeyValue(opacityProperty(), 1.0);
			KeyFrame keyFrame1 = new KeyFrame(inDuration.get(), keyValue1);
			KeyFrame keyFrame2 = new KeyFrame(inDuration.get(), keyValue2);
			inAnimation.getKeyFrames().addAll(keyFrame1, keyFrame2);

			KeyValue keyValue3 = new KeyValue(radiusProperty(), rippleRadius.get() * 2);
			KeyValue keyValue4 = new KeyValue(opacityProperty(), 0.0);
			KeyFrame keyFrame3 = new KeyFrame(outDuration.get(), keyValue3);
			KeyFrame keyFrame4 = new KeyFrame(outDuration.get(), keyValue4);
			outAnimation.getKeyFrames().addAll(keyFrame3, keyFrame4);

			if (level != null) {
				Control control = (Control) region;
				DropShadow shadowEffect = (DropShadow) ((Control) control.getSkin().getSkinnable()).getEffect();
				DropShadow startShadow = shadowOf(level);
				DropShadow endShadow = shadowOf(level, shadowDelta);

				// Spread
				KeyValue keyValue5 = new KeyValue(shadowEffect.spreadProperty(), endShadow.getSpread(), Interpolator.LINEAR);
				KeyValue keyValue6 = new KeyValue(shadowEffect.spreadProperty(), startShadow.getSpread(), Interpolator.LINEAR);
				//Radius
				KeyValue keyValue7 = new KeyValue(shadowEffect.radiusProperty(), endShadow.getRadius(), Interpolator.LINEAR);
				KeyValue keyValue8 = new KeyValue(shadowEffect.radiusProperty(), startShadow.getRadius(), Interpolator.LINEAR);
				// Offsets
				KeyValue keyValue9 = new KeyValue(shadowEffect.offsetXProperty(), endShadow.getOffsetX(), Interpolator.LINEAR);
				KeyValue keyValue10 = new KeyValue(shadowEffect.offsetXProperty(), startShadow.getOffsetX(), Interpolator.LINEAR);
				KeyValue keyValue11 = new KeyValue(shadowEffect.offsetYProperty(), endShadow.getOffsetY(), Interpolator.LINEAR);
				KeyValue keyValue12 = new KeyValue(shadowEffect.offsetYProperty(), startShadow.getOffsetY(), Interpolator.LINEAR);
				KeyFrame keyFrame5 = new KeyFrame(Duration.ZERO, keyValue5, keyValue7, keyValue9, keyValue11);
				KeyFrame keyFrame6 = new KeyFrame(inDuration.get(), keyValue6, keyValue8, keyValue10, keyValue12);
				shadowAnimation.getKeyFrames().addAll(keyFrame5, keyFrame6);
				parallelTransition.getChildren().add(0, shadowAnimation);
			}

			sequentialTransition.getChildren().addAll(inAnimation, outAnimation);
			parallelTransition.setInterpolator(MFXAnimationFactory.INTERPOLATOR_V2);
			parallelTransition.getChildren().add(sequentialTransition);
		}
	}

	//================================================================================
	// Stylesheet Properties
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<RippleGenerator, Paint> RIPPLE_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-ripple-color",
						RippleGenerator::rippleColorProperty
				);

		private static final CssMetaData<RippleGenerator, Number> RIPPLE_RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-radius",
						RippleGenerator::rippleRadiusProperty,
						10.0
				);

		private static final CssMetaData<RippleGenerator, Boolean> ANIMATE_BACKGROUND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-animate-background",
						RippleGenerator::animateBackgroundProperty,
						true
				);

		static {
			cssMetaDataList = List.of(RIPPLE_COLOR, RIPPLE_RADIUS, ANIMATE_BACKGROUND);
		}

	}

	public static List<CssMetaData<? extends Styleable, ?>> getGroupCssMetaDataList() {
		return RippleGenerator.StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return RippleGenerator.getGroupCssMetaDataList();
	}
}