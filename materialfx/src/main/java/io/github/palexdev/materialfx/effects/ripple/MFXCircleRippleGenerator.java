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

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator.CircleRipple;
import io.github.palexdev.materialfx.effects.ripple.base.AbstractMFXRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.base.IRipple;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Concrete implementation of {@link AbstractMFXRippleGenerator<CircleRipple>}.
 * <p>
 * This is the most basic ripple generator that generates circular ripples.
 * Accepted ripples are {@link CircleRipple} or subclasses.
 * <p></p>
 * Usage example:
 * <pre>
 * {@code
 *      Region region = ...
 *      MFXCircleRippleGenerator generator = new MFXCircleRippleGenerator(region) // It's needed to pass the region reference
 *      generator.setRipplePositionFunction(mouseEvent -> ...) // This is needed to specify where the ripple should be placed when generated
 *      region.getChildren().add(generator) // Ripples are added to the generator's container so the generator must be added to the region
 *      region.addEventHandler(MouseEvent.MOUSE_PRESSED, generator::generateRipple)
 * }
 * </pre>
 */
public class MFXCircleRippleGenerator extends AbstractMFXRippleGenerator<CircleRipple> {
	//================================================================================
	// Properties
	//================================================================================
	private final BooleanProperty computeRadiusMultiplier = new SimpleBooleanProperty(false);
	private final DoubleProperty radiusMultiplier = new SimpleDoubleProperty(2.0);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCircleRippleGenerator(Region region) {
		super(region);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Calls {@link AbstractMFXRippleGenerator#initialize()}, {@link #defaultClipSupplier()} ()},
	 * {@link #defaultPositionFunction()} and {@link #defaultRippleSupplier()}
	 */
	protected void initialize() {
		super.initialize();
		defaultClipSupplier();
		defaultPositionFunction();
		defaultRippleSupplier();
	}

	/**
	 * Generates a ripple.
	 * <p></p>
	 * <p> - If {@link #checkBoundsProperty()} is true calls {@link #isWithinBounds(MouseEvent)}. Exits if returns false.
	 * <p> - Sets the generator clip/bounds by calling {@link #buildClip()}.
	 * <p> - Computes the ripple coordinates by calling {@link #getRipplePositionFunction()} applied on the passed mouse event.
	 * <p> - Creates the ripple by calling {@link #getRippleSupplier()}. Sets the center and fill properties of the ripple.
	 * <p> - Creates the animations by calling {@link CircleRipple#getAnimation()}
	 * <p> - If {@link #animateBackgroundProperty()} and {@link #animateShadowProperty()} are true, creates the respective animations
	 * by calling {@link #getBackgroundAnimation()} and {@link #getShadowAnimation()}.
	 * <p> - The animations are added to a {@link ParallelTransition} which is added to the animation stack.
	 * <p> - The ripple is added to the generator's children list.
	 * <p> - The animation starts.
	 *
	 * @see RippleGeneratorEvent
	 */
	@Override
	public void generateRipple(MouseEvent event) {
		if (isPaused()) return;

		if (isCheckBounds() && !isWithinBounds(event)) {
			return;
		}

		if (getClip() != null) {
			setClip(null);
		}
		setClip(buildClip());

		PositionBean position = getRipplePositionFunction().apply(event);

		CircleRipple ripple = getRippleSupplier().get();
		ripple.centerXProperty().bind(position.xProperty());
		ripple.centerYProperty().bind(position.yProperty());
		ripple.setFill(getRippleColor());
		ripple.setOpacity(getRippleOpacity());

		Animation rippleAnimation = ripple.getAnimation();
		rippleAnimation.setRate(getAnimationSpeed());

		ParallelTransition transition = new ParallelTransition(rippleAnimation);
		if (isAnimateBackground()) {
			Animation backgroundAnimation = getBackgroundAnimation();
			transition.getChildren().add(backgroundAnimation);
		}
		if (isAnimateShadow()) {
			Animation shadowAnimation = getShadowAnimation();
			transition.getChildren().add(shadowAnimation);
		}

		transition.setOnFinished(end -> getChildren().remove(ripple));
		getChildren().add(ripple);
		animationsStack.add(transition);
		transition.play();
	}

	/**
	 * Builds the background animation. It consists in a temporary
	 * shape added to the generator's children list in position 0.
	 * The opacity of the shape is increased to the value specified by {@link #backgroundOpacityProperty()},
	 * and then dropped to 0. When the opacity is 0 it is removed from the children list.
	 */
	protected Animation getBackgroundAnimation() {
		if (getClipSupplier() == null || getClipSupplier().get() == null) {
			throw new NullPointerException("RippleGenerator cannot animate background because clip supplier is null!");
		}

		Shape shape = getClipSupplier().get();
		shape.setFill(getRippleColor());
		shape.setOpacity(0);
		shape.opacityProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() == 0) {
				getChildren().remove(shape);
			}
		});

		return AnimationUtils.TimelineBuilder.build()
				.add(
						KeyFrames.of(Duration.ZERO, event -> getChildren().add(0, shape)),
						KeyFrames.of(Duration.millis(300), shape.opacityProperty(), getBackgroundOpacity()),
						KeyFrames.of(Duration.millis(450), shape.opacityProperty(), 0, Interpolator.LINEAR)
				).getAnimation();
	}

	/**
	 * Builds the animation of the {@link DropShadow} effect.
	 * If the region's effect is not a DropShadow or its level is not recognized by {@link DepthLevel#from(DropShadow)}
	 * then an empty animation is returned.
	 * <p>
	 * If the effect is recognized then builds two new DropShadow effects, a start one and an end one.
	 * The start effect is the same level as the region's one and the end is computed by {@link MFXDepthManager#shadowOf(DepthLevel, int)}
	 * using {@link #depthLevelOffsetProperty()} as argument.
	 * <p>
	 * <b>N.B: as stated above the animation works only for shadows defined by {@link DepthLevel}</b>
	 */
	protected Animation getShadowAnimation() {
		Timeline animation = new Timeline();

		if (region.getEffect() != null && region.getEffect() instanceof DropShadow) {
			DropShadow shadowEffect = (DropShadow) region.getEffect();
			DepthLevel level = DepthLevel.from(shadowEffect);
			if (level != null) {
				DropShadow startShadow = MFXDepthManager.shadowOf(level);
				DropShadow endShadow = MFXDepthManager.shadowOf(level, getDepthLevelOffset());

				// Spread
				KeyValue keyValue5 = new KeyValue(shadowEffect.spreadProperty(), endShadow.getSpread());
				KeyValue keyValue6 = new KeyValue(shadowEffect.spreadProperty(), startShadow.getSpread());
				//Radius
				KeyValue keyValue7 = new KeyValue(shadowEffect.radiusProperty(), endShadow.getRadius());
				KeyValue keyValue8 = new KeyValue(shadowEffect.radiusProperty(), startShadow.getRadius());
				// Offsets
				KeyValue keyValue9 = new KeyValue(shadowEffect.offsetXProperty(), endShadow.getOffsetX());
				KeyValue keyValue10 = new KeyValue(shadowEffect.offsetXProperty(), startShadow.getOffsetX());
				KeyValue keyValue11 = new KeyValue(shadowEffect.offsetYProperty(), endShadow.getOffsetY());
				KeyValue keyValue12 = new KeyValue(shadowEffect.offsetYProperty(), startShadow.getOffsetY());
				KeyFrame keyFrame5 = new KeyFrame(Duration.millis(350), keyValue5, keyValue7, keyValue9, keyValue11);
				KeyFrame keyFrame6 = new KeyFrame(Duration.millis(700), keyValue6, keyValue8, keyValue10, keyValue12);

				animation.getKeyFrames().setAll(keyFrame5, keyFrame6);
			}
		}
		return animation;
	}

	/**
	 * Attempts to compute the radius multiplier from the
	 * starting ripple radius and the x position at which the ripple will be generated.
	 * <p>
	 * Currently the value is computed by finding the region' side closest to the specified x coordinate.
	 * The final radius is computed as the starting radius plus "Math.abs(xCoordinate - nearestBound)".
	 * Then the multiplier is calculated as the final radius divided by the starting radius.
	 * <p>
	 * This method works in most cases. Sometimes the computed multiplier is not enough to cover the entire region.
	 * This is probably because the right way would be to consider the farthermost vertex rather than side.
	 * However, it's not a big issue since you can also manipulate the initial radius or set the multiplier manually.
	 *
	 * @param xCoordinate the x coordinate at which the ripple will be generated
	 * @see #radiusMultiplierProperty()
	 */
	protected double computeRadiusMultiplier(double xCoordinate) {
		Bounds bounds = region.getLayoutBounds();
		double distanceFromMax = Math.abs(bounds.getMaxX() - xCoordinate);

		double nearestBound;
		if (distanceFromMax < xCoordinate) {
			nearestBound = 0;
		} else {
			nearestBound = bounds.getMaxX();
		}

		double finalRadius = getRippleRadius() + Math.abs(xCoordinate - nearestBound);
		return finalRadius / getRippleRadius();
	}

	/**
	 * Responsible for building the ripple generator's clip,
	 * which avoids ripple ending outside the region.
	 *
	 * @see NodeUtils#parseCornerRadius(Region)
	 * @see NodeUtils#setBackground(Region, Paint, CornerRadii)
	 */
	protected Node buildClip() {
		if (isAutoClip()) {
			CornerRadii radius = NodeUtils.parseCornerRadius(region);
			Region clip = new Region();
			clip.resizeRelocate(0, 0, region.getWidth(), region.getHeight());
			NodeUtils.setBackground(clip, Color.WHITE, radius);
			return clip;
		}
		return getClipSupplier().get();
	}

	public boolean isComputeRadiusMultiplier() {
		return computeRadiusMultiplier.get();
	}

	/**
	 * Specifies if the ripple's radius multiplier should be computed automatically.
	 * If this is true the value specified by {@link #radiusMultiplierProperty()} will be ignored
	 * and {@link #computeRadiusMultiplier(double)} will be called instead.
	 */
	public BooleanProperty computeRadiusMultiplierProperty() {
		return computeRadiusMultiplier;
	}

	public void setComputeRadiusMultiplier(boolean computeRadiusMultiplier) {
		this.computeRadiusMultiplier.set(computeRadiusMultiplier);
	}

	public double getRadiusMultiplier() {
		return radiusMultiplier.get();
	}

	/**
	 * Specifies the multiplier used to obtain the final ripple's radius.
	 * <p></p>
	 * If you are still wondering what the heck is this multiplier then read this.
	 * <p></p>
	 * The ripple is basically a shape, in this case a circle with initial radius 0, created at specified coordinates.
	 * <p>
	 * Generally speaking:
	 * <p>
	 * The first phase of the ripple animation consists in increasing the radius to the value specified by {@link #rippleRadiusProperty()}.
	 * <p>
	 * The second phase is to further expand that radius (like a ripple in a lake lol) to make the circle cover entirely or almost the region.
	 * <p>
	 * The last phase is to drop the circle opacity to 0 and remove the ripple.
	 */
	public DoubleProperty radiusMultiplierProperty() {
		return radiusMultiplier;
	}

	public void setRadiusMultiplier(double radiusMultiplier) {
		this.radiusMultiplier.set(radiusMultiplier);
	}


	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public Region getRegion() {
		return region;
	}

	@Override
	public void defaultClipSupplier() {
		setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.RECTANGLE).build(region));
	}

	@Override
	public Supplier<Shape> getClipSupplier() {
		return super.clipSupplier;
	}

	@Override
	public void setClipSupplier(Supplier<Shape> clipSupplier) {
		super.clipSupplier = clipSupplier;
	}

	@Override
	public void defaultPositionFunction() {
		setRipplePositionFunction(event -> new PositionBean());
	}

	@Override
	public Function<MouseEvent, PositionBean> getRipplePositionFunction() {
		return positionFunction;
	}

	@Override
	public void setRipplePositionFunction(Function<MouseEvent, PositionBean> positionFunction) {
		super.positionFunction = positionFunction;
	}

	@Override
	public void defaultRippleSupplier() {
		setRippleSupplier(CircleRipple::new);
	}

	@Override
	public Supplier<CircleRipple> getRippleSupplier() {
		return rippleSupplier;
	}

	@Override
	public void setRippleSupplier(Supplier<CircleRipple> rippleSupplier) {
		super.rippleSupplier = rippleSupplier;
	}

	//================================================================================
	// Ripple Type Class
	//================================================================================

	/**
	 * Defines a new ripple type which extends {@link Circle} and implements {@link IRipple}.
	 */
	public class CircleRipple extends Circle implements IRipple {
		private double xPosition = -1;

		public CircleRipple() {
			setRadius(0);
		}

		@Override
		public Animation getAnimation() {
			double mul = getRadiusMultiplier();
			if (isComputeRadiusMultiplier()) {
				mul = computeRadiusMultiplier(xPosition);
			}

			return AnimationUtils.TimelineBuilder.build()
					.add(
							KeyFrames.of(150, radiusProperty(), getRippleRadius()),
							KeyFrames.of(400, radiusProperty(), (getRippleRadius() * mul)),
							KeyFrames.of(1200, opacityProperty(), 0, MFXAnimationFactory.INTERPOLATOR_V2),
							KeyFrames.of(500, event -> animationsStack.pop())
					).getAnimation();
		}

		public void setXPosition(double xPosition) {
			this.xPosition = xPosition;
		}
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableDoubleProperty animationSpeed = new StyleableDoubleProperty(
			StyleableProperties.ANIMATION_SPEED,
			this,
			"animationSpeed",
			1.0
	);

	private final StyleableBooleanProperty autoClip = new StyleableBooleanProperty(
			StyleableProperties.AUTO_CLIP,
			this,
			"autoClip",
			false
	);

	private final StyleableDoubleProperty backgroundOpacity = new StyleableDoubleProperty(
			StyleableProperties.BACKGROUND_OPACITY,
			this,
			"backgroundOpacity",
			0.3
	);

	private final StyleableBooleanProperty paused = new StyleableBooleanProperty(
			StyleableProperties.PAUSED,
			this,
			"paused",
			false
	);

	private final StyleableObjectProperty<Paint> rippleColor = new StyleableObjectProperty<>(
			StyleableProperties.RIPPLE_COLOR,
			this,
			"rippleColor",
			Color.LIGHTGRAY
	);

	private final StyleableDoubleProperty rippleOpacity = new StyleableDoubleProperty(
			StyleableProperties.RIPPLE_OPACITY,
			this,
			"rippleOpacity",
			1.0
	);


	private final StyleableDoubleProperty rippleRadius = new StyleableDoubleProperty(
			StyleableProperties.RIPPLE_RADIUS,
			this,
			"radius",
			10.0
	);

	public double getAnimationSpeed() {
		return animationSpeed.get();
	}

	/**
	 * Specifies the speed on the ripples' animation.
	 * This is done by setting the animations rate property, {@link Animation#setRate(double)}
	 */
	public StyleableDoubleProperty animationSpeedProperty() {
		return animationSpeed;
	}

	public void setAnimationSpeed(double animationSpeed) {
		this.animationSpeed.set(animationSpeed);
	}

	public boolean isAutoClip() {
		return autoClip.get();
	}

	/**
	 * Specifies whether the generator should try to {@link #buildClip()} automatically,
	 * this means also trying to fetch the background/border radius.
	 * <p>
	 * <b>EXPERIMENTAL, may not work in all situations</b>
	 */
	public StyleableBooleanProperty autoClipProperty() {
		return autoClip;
	}

	public void setAutoClip(boolean autoClip) {
		this.autoClip.set(autoClip);
	}

	public double getBackgroundOpacity() {
		return backgroundOpacity.get();
	}

	/**
	 * Specifies the strength of the background animation.
	 */
	public StyleableDoubleProperty backgroundOpacityProperty() {
		return backgroundOpacity;
	}

	public void setBackgroundOpacity(double backgroundOpacity) {
		this.backgroundOpacity.set(backgroundOpacity);
	}

	public boolean isPaused() {
		return paused.get();
	}

	/**
	 * Property to enable/disable the ripple generator.
	 */
	public StyleableBooleanProperty pausedProperty() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused.set(paused);
	}

	public Paint getRippleColor() {
		return rippleColor.get();
	}

	/**
	 * Specifies the ripples' color.
	 */
	public StyleableObjectProperty<Paint> rippleColorProperty() {
		return rippleColor;
	}

	public void setRippleColor(Paint rippleColor) {
		this.rippleColor.set(rippleColor);
	}

	public double getRippleOpacity() {
		return rippleOpacity.get();
	}

	/**
	 * Specifies the initial ripple's opacity.
	 */
	public StyleableDoubleProperty rippleOpacityProperty() {
		return rippleOpacity;
	}

	public void setRippleOpacity(double rippleOpacity) {
		this.rippleOpacity.set(rippleOpacity);
	}

	public double getRippleRadius() {
		return rippleRadius.get();
	}

	/**
	 * Specifies the ripples' initial radius.
	 */
	public StyleableDoubleProperty rippleRadiusProperty() {
		return rippleRadius;
	}

	public void setRippleRadius(double radius) {
		this.rippleRadius.set(radius);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXCircleRippleGenerator> FACTORY = new StyleablePropertyFactory<>(AbstractMFXRippleGenerator.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXCircleRippleGenerator, Number> ANIMATION_SPEED =
				FACTORY.createSizeCssMetaData(
						"-mfx-animation-speed",
						MFXCircleRippleGenerator::animationSpeedProperty,
						1.0
				);

		private static final CssMetaData<MFXCircleRippleGenerator, Boolean> AUTO_CLIP =
				FACTORY.createBooleanCssMetaData(
						"-mfx-auto-clip",
						MFXCircleRippleGenerator::autoClipProperty,
						false
				);

		private static final CssMetaData<MFXCircleRippleGenerator, Number> BACKGROUND_OPACITY =
				FACTORY.createSizeCssMetaData(
						"-mfx-background-opacity",
						MFXCircleRippleGenerator::backgroundOpacityProperty,
						0.3
				);

		private static final CssMetaData<MFXCircleRippleGenerator, Paint> RIPPLE_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-ripple-color",
						MFXCircleRippleGenerator::rippleColorProperty,
						Color.LIGHTGRAY
				);


		private static final CssMetaData<MFXCircleRippleGenerator, Number> RIPPLE_OPACITY =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-opacity",
						MFXCircleRippleGenerator::rippleOpacityProperty,
						1.0
				);

		private static final CssMetaData<MFXCircleRippleGenerator, Number> RIPPLE_RADIUS =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-radius",
						MFXCircleRippleGenerator::rippleRadiusProperty,
						10.0
				);

		private static final CssMetaData<MFXCircleRippleGenerator, Boolean> PAUSED =
				FACTORY.createBooleanCssMetaData(
						"-mfx-paused",
						MFXCircleRippleGenerator::pausedProperty,
						false
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					AbstractMFXRippleGenerator.getClassCssMetaData(),
					ANIMATION_SPEED, AUTO_CLIP, BACKGROUND_OPACITY, PAUSED,
					RIPPLE_COLOR, RIPPLE_OPACITY, RIPPLE_RADIUS
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return MFXCircleRippleGenerator.getControlCssMetaDataList();
	}
}
