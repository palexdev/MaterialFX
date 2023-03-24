/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxeffects.ripple;

import io.github.palexdev.mfxeffects.animations.Animations.AbstractBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.base.Ripple;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static javafx.util.Duration.ZERO;

/**
 * Basic implementation of the ripple effect described by Material Design guidelines, adapted to JavaFX.
 * <p>
 * Implements {@link RippleGenerator} and uses {@code 'mfx-ripple-generator'} as style class for CSS.
 * <p></p>
 * For this to work there are some properties and conditions that are needed to correctly generate ripples when requested:
 * <p> 1) The generator keeps a reference of the region on which ripple effects will be applied. To be more precise
 * the ripple generator itself is a {@link Region}, all the effects and animations are applied on itself.
 * <p> 2) For the above reason, a region that wishes to show ripple effects must add the generator in its children list,
 * typically the generator is the first node added.
 * <p> 3) The generator needs to know where to generate the ripple, the x and y positions at which the ripple will be placed.
 * This is "optional" as all generators, all generators use the default function provided by {@link #defaultPositionFunction()}.
 * Although the function takes a {@link MouseEvent} as input it is not necessary for the ripple generation, {@link #generate(MouseEvent)}
 * can be also called with a 'null' event, as long as the position is valid.
 * <p> 4) The generator allows the user to define the ripple shape through {@link #setRippleSupplier(Supplier)}.
 * This is a need information, but "optional", all generators use the default supplier given by {@link #defaultRippleSupplier()}
 * <p> 5) If generating from a {@link MouseEvent}, and {@link #checkBoundsProperty()} is true, the event position must
 * be in the region's bounds
 * <p> 6) If the background animation is active, {@link #animateBackgroundProperty()}, it's needed to tell the generator
 * which {@link Shape} to use for the animation through {@link #setClipSupplier(Supplier)}. This should be the same shape of the region.
 * All generators by default use the supplier given by {@link #defaultClipSupplier()}
 * <p></p>
 * Other than that, the generator has a lot of other properties that allow to customize the effect and all of them can be
 * set through CSS too:
 * <p> - It's possible to enable/disable the background animation through {@link #animateBackgroundProperty()}
 * <p> - It's possible to set the speed of the animation through {@link #animationSpeedProperty()}
 * <p> - It's possible to set the color and opacity of the background through {@link #backgroundColorProperty()} and {@link #backgroundOpacityProperty()}
 * <p> - It's possible to set the color and opacity of the ripples through {@link #rippleColorProperty()} and {@link #rippleOpacityProperty()}
 * <p> - It's also possible to set the size of the ripples through {@link #ripplePrefSizeProperty()} and {@link #rippleSizeMultiplierProperty()}
 * <p> - It's possible to disable the bounds check for the ripple generation through {@link #checkBoundsProperty()}
 * <p> - It's possible, with an experimental feature, to automatically detect the shape of the region and build a clip
 * for the generator accordingly, more info here {@link #autoClipProperty()} and here {@link #buildClip()}
 * <p></p>
 * Last but not least, know that there are 3 main ways to stop the generator if you don't want it on added controls:
 * <p> 1) You can disable the generator just like any other node with {@link #disableProperty()}
 * <p> 2) You can set the visibility to hidden, {@link #visibleProperty()}
 * <p> 3) You can set the ripples opacity to 0, {@link #rippleOpacityProperty()}
 */
// TODO next improvement is to keep the ripple until the mouse is released (?)
public class MFXRippleGenerator extends Region implements RippleGenerator {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-ripple-generator";
	private final Region region;
	private Supplier<Shape> clipSupplier;
	private Supplier<? extends Ripple<?>> rippleSupplier;
	private Function<MouseEvent, Position> positionFunction;

	private Node cachedClip;
	private final EventHandler<MouseEvent> handler;
	private final InvalidationListener clipUpdater;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXRippleGenerator(Region region) {
		this.region = region;
		handler = this::generate;
		clipUpdater = i -> Optional.ofNullable(cachedClip)
				.ifPresent(n -> n.resizeRelocate(0, 0, region.getWidth(), region.getHeight()));
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setClipSupplier(defaultClipSupplier());
		setPositionFunction(defaultPositionFunction());
		setRippleSupplier(defaultRippleSupplier());
		region.layoutBoundsProperty().addListener(clipUpdater);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to add a {@link MouseEvent#MOUSE_PRESSED} handler on the target region
	 * that will call {@link #generate(MouseEvent)}.
	 */
	@Override
	public void enable() {
		region.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed the handler added by {@link #enable()} thus blocking ripple generation.
	 */
	@Override
	public void disable() {
		region.removeEventHandler(MouseEvent.MOUSE_PRESSED, handler);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The effect is generated only if these two conditions are met first:
	 * <p> 1) The generator has not been disabled, {@link #disableProperty()}
	 * <p> 2) The bounds check is enabled and {@link #isWithinBounds(MouseEvent)} returns true
	 * <p></p>
	 * The first operation is to set the clip for the generator, so that ripples do not overflow.
	 * <p>
	 * Then we use the {@link #getPositionFunction()} to convert the given {@link MouseEvent} to the position
	 * at which the ripple will be generated.
	 * <p>
	 * As already said, the event can also be 'null' (useful for programmatic generation), as long as the position
	 * function can handle the 'null' input and still return a valid position.
	 * <p></p>
	 * Then a ripple shape is created from the given supplier, {@link #getRippleSupplier()}, it's fill and opacity
	 * are set according to the values of {@link #rippleColorProperty()} and {@link #rippleOpacityProperty()}
	 * <p></p>
	 * Now animations are created. Since the generator can optionally animate the background too, a root {@link ParallelTransition}
	 * is used to play both of them at the same time.
	 * <p>
	 * The ripple animation is given by {@link Ripple#animation(RippleGenerator)}, and the background one by
	 * {@link #backgroundAnimation()}.
	 * <p>
	 * Last step is to set the speed of the root animation according to the {@link #animationSpeedProperty()}.
	 * <p></p>
	 * It's also important to tell the root animation to remove the ripple shape from the generator once the transition
	 * has ended.
	 */
	@Override
	public void generate(MouseEvent me) {
		if (isDisabled() || !isVisible() || getRippleOpacity() <= 0) return;
		if (isCheckBounds() && !isWithinBounds(me)) return;
		if (getClip() != null) setClip(null);
		setClip(buildClip());
		Position pos = getPositionFunction().apply(me);

		Ripple<?> ripple = getRippleSupplier().get();
		Shape rNode = ripple.getNode();
		ripple.position(pos);
		rNode.setFill(getRippleColor());
		rNode.setOpacity(getRippleOpacity());

		AbstractBuilder pt = ParallelBuilder.build()
				.setRate(getAnimationSpeed())
				.add(ripple.animation(this))
				.setOnFinished(e -> getChildren().remove(rNode));

		Animation bAnimation;
		if ((bAnimation = backgroundAnimation()) != null) pt.add(bAnimation);

		getChildren().add(rNode);
		pt.getAnimation().play();
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * If the background animation is enabled through {@link #animateBackgroundProperty()} a shape is created by the
	 * {@link #getClipSupplier()}, its fill set according to the {@link #backgroundColorProperty()} and its initial
	 * opacity set to 0.0.
	 * <p>
	 * The animation simply changes the opacity of the generated shape from 0 to and 1 and from 1 to 0 in a single
	 * {@link Timeline}.
	 * <p>
	 * It's important to tell the animation to remove the background shape from the generator once it has ended.
	 * <p></p>
	 * Returns null otherwise.
	 *
	 * @throws IllegalStateException if the {@link #getClipSupplier()} is 'null' or the return values is 'null'
	 */
	@Override
	public Animation backgroundAnimation() {
		if (!isAnimateBackground()) return null;

		Shape shape;
		if (getClipSupplier() == null || (shape = getClipSupplier().get()) == null) {
			throw new IllegalStateException("RippleGenerator cannot animate background because clip supplier is null!");
		}

		shape.setFill(getBackgroundColor());
		shape.setOpacity(0.0);
		return TimelineBuilder.build()
				.add(KeyFrames.of(ZERO, e -> getChildren().add(0, shape)))
				.add(KeyFrames.of(250, shape.opacityProperty(), getBackgroundOpacity()))
				.add(KeyFrames.of(500, shape.opacityProperty(), 0.0, Interpolator.LINEAR))
				.setOnFinished(e -> getChildren().remove(shape))
				.getAnimation();
	}

	/**
	 * Checks if the given {@link MouseEvent} is within the bounds of the target region,
	 * uses {@link javafx.geometry.Bounds#contains(Bounds)} on the region's layout bounds.
	 */
	protected boolean isWithinBounds(MouseEvent event) {
		if (event == null) return true;
		return region.getLayoutBounds().contains(event.getX(), event.getY());
	}

	/**
	 * This is responsible for building the node set as clip of the generator itself avoiding ripples from overflowing.
	 * <p></p>
	 * Normally it's the user's responsibility to specify the clip's shape. Or you could try enabling the
	 * experimental feature {@link #autoClipProperty()}. The generator will try to automatically build a clip that
	 * is appropriate for the target region. Some regions for example, have round backgrounds/borders, this can try to
	 * detect such situations and build a rounded clip.
	 */
	protected Node buildClip() {
		if (isAutoClip()) {
			if (cachedClip != null) return cachedClip;
			CornerRadii radius = StyleUtils.parseCornerRadius(region);
			Region clip = new Region();
			cachedClip = clip;
			StyleUtils.setBackground(clip, Color.WHITE, radius);
			clipUpdater.invalidated(null);
			return clip;
		}
		return getClipSupplier().get();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty animateBackground = new SimpleStyleableBooleanProperty(
			StyleableProperties.ANIMATE_BACKGROUND,
			this,
			"animateBackground",
			false
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty animationSpeed = new SimpleStyleableDoubleProperty(
			StyleableProperties.ANIMATION_SPEED,
			this,
			"animationSpeed",
			1.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty autoClip = new SimpleStyleableBooleanProperty(
			StyleableProperties.AUTO_CLIP,
			this,
			"autoClip",
			false
	) {
		@Override
		protected void invalidated() {
			boolean state = get();
			if (!state) cachedClip = null;
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableObjectProperty<Paint> backgroundColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.BACKGROUND_COLOR,
			this,
			"backgroundColor",
			Color.LIGHTGRAY
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty backgroundOpacity = new SimpleStyleableDoubleProperty(
			StyleableProperties.BACKGROUND_OPACITY,
			this,
			"backgroundOpacity",
			1.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty checkBounds = new SimpleStyleableBooleanProperty(
			StyleableProperties.CHECK_BOUNDS,
			this,
			"checkBounds",
			true
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableObjectProperty<Paint> rippleColor = new SimpleStyleableObjectProperty<>(
			StyleableProperties.RIPPLE_COLOR,
			this,
			"rippleColor",
			Color.LIGHTGRAY
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty rippleOpacity = new SimpleStyleableDoubleProperty(
			StyleableProperties.RIPPLE_OPACITY,
			this,
			"rippleOpacity",
			1.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty ripplePrefSize = new SimpleStyleableDoubleProperty(
			StyleableProperties.RIPPLE_PREF_SIZE,
			this,
			"ripplePrefSize",
			-1.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty rippleSizeMultiplier = new SimpleStyleableDoubleProperty(
			StyleableProperties.RIPPLE_SIZE_MULTIPLIER,
			this,
			"rippleSizeMultiplier",
			4.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	public boolean isAnimateBackground() {
		return animateBackground.get();
	}

	/**
	 * Specifies whether to animate the background.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-animate-background';
	 */
	public StyleableBooleanProperty animateBackgroundProperty() {
		return animateBackground;
	}

	public void setAnimateBackground(boolean animateBackground) {
		this.animateBackground.set(animateBackground);
	}

	public double getAnimationSpeed() {
		return animationSpeed.get();
	}

	/**
	 * Specifies the speed on the ripple animation.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-animation-speed'.
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
	 * Specifies whether to enable the experimental clip feature, see {@link #buildClip()}.
	 * <p>
	 * Can be set in CSS via the property '-mfx-auto-clip'.
	 */
	public StyleableBooleanProperty autoClipProperty() {
		return autoClip;
	}

	public void setAutoClip(boolean autoClip) {
		this.autoClip.set(autoClip);
	}

	public Paint getBackgroundColor() {
		return backgroundColor.get();
	}

	/**
	 * Specifies the color of the shape used for the background animation.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-background-color'.
	 */
	public StyleableObjectProperty<Paint> backgroundColorProperty() {
		return backgroundColor;
	}

	public void setBackgroundColor(Paint backgroundColor) {
		this.backgroundColor.set(backgroundColor);
	}

	public double getBackgroundOpacity() {
		return backgroundOpacity.get();
	}

	/**
	 * Specifies the opacity of the shape used for the background animation.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-background-opacity'.
	 */
	public StyleableDoubleProperty backgroundOpacityProperty() {
		return backgroundOpacity;
	}

	public void setBackgroundOpacity(double backgroundOpacity) {
		this.backgroundOpacity.set(backgroundOpacity);
	}

	public boolean isCheckBounds() {
		return checkBounds.get();
	}

	/**
	 * Specifies whether the bounds check is enabled before the ripple generation, see {@link #isWithinBounds(MouseEvent)}.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-check-bounds'.
	 */
	public StyleableBooleanProperty checkBoundsProperty() {
		return checkBounds;
	}

	public void setCheckBounds(boolean checkBounds) {
		this.checkBounds.set(checkBounds);
	}

	@Override
	public Paint getRippleColor() {
		return rippleColor.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Can be set in CSS via the property: '-mfx-ripple-color'.
	 */
	@Override
	public StyleableObjectProperty<Paint> rippleColorProperty() {
		return rippleColor;
	}

	@Override
	public void setRippleColor(Paint rippleColor) {
		this.rippleColor.set(rippleColor);
	}

	@Override
	public double getRippleOpacity() {
		return rippleOpacity.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Can be set in CSS via the property: '-mfx-ripple-opacity'.
	 */
	@Override
	public StyleableDoubleProperty rippleOpacityProperty() {
		return rippleOpacity;
	}

	@Override
	public void setRippleOpacity(double rippleOpacity) {
		this.rippleOpacity.set(rippleOpacity);
	}

	@Override
	public double getRipplePrefSize() {
		return ripplePrefSize.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Can be set in CSS via the property: '-mfx-ripple-pref-size'.
	 */
	@Override
	public StyleableDoubleProperty ripplePrefSizeProperty() {
		return ripplePrefSize;
	}

	@Override
	public void setRipplePrefSize(double ripplePrefSize) {
		this.ripplePrefSize.set(ripplePrefSize);
	}

	@Override
	public double getRippleSizeMultiplier() {
		return rippleSizeMultiplier.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Can be set in CSS via the property: '-mfx-ripple-size-multiplier'.
	 */
	@Override
	public StyleableDoubleProperty rippleSizeMultiplierProperty() {
		return rippleSizeMultiplier;
	}

	@Override
	public void setRippleSizeMultiplier(double rippleSizeMultiplier) {
		this.rippleSizeMultiplier.set(rippleSizeMultiplier);
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXRippleGenerator> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXRippleGenerator, Boolean> ANIMATE_BACKGROUND =
				FACTORY.createBooleanCssMetaData(
						"-mfx-animate-background",
						MFXRippleGenerator::animateBackgroundProperty,
						false
				);

		private static final CssMetaData<MFXRippleGenerator, Number> ANIMATION_SPEED =
				FACTORY.createSizeCssMetaData(
						"-mfx-animation-speed",
						MFXRippleGenerator::animationSpeedProperty,
						1.0
				);

		private static final CssMetaData<MFXRippleGenerator, Boolean> AUTO_CLIP =
				FACTORY.createBooleanCssMetaData(
						"-mfx-auto-clip",
						MFXRippleGenerator::autoClipProperty,
						false
				);

		private static final CssMetaData<MFXRippleGenerator, Paint> BACKGROUND_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-background-color",
						MFXRippleGenerator::backgroundColorProperty,
						Color.LIGHTGRAY
				);

		private static final CssMetaData<MFXRippleGenerator, Number> BACKGROUND_OPACITY =
				FACTORY.createSizeCssMetaData(
						"-mfx-background-opacity",
						MFXRippleGenerator::backgroundOpacityProperty,
						1.0
				);

		private static final CssMetaData<MFXRippleGenerator, Boolean> CHECK_BOUNDS =
				FACTORY.createBooleanCssMetaData(
						"-mfx-check-bounds",
						MFXRippleGenerator::checkBoundsProperty,
						true
				);

		private static final CssMetaData<MFXRippleGenerator, Paint> RIPPLE_COLOR =
				FACTORY.createPaintCssMetaData(
						"-mfx-ripple-color",
						MFXRippleGenerator::rippleColorProperty,
						Color.LIGHTGRAY
				);

		private static final CssMetaData<MFXRippleGenerator, Number> RIPPLE_OPACITY =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-opacity",
						MFXRippleGenerator::rippleOpacityProperty,
						1.0
				);

		private static final CssMetaData<MFXRippleGenerator, Number> RIPPLE_PREF_SIZE =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-pref-size",
						MFXRippleGenerator::ripplePrefSizeProperty,
						-1.0
				);

		private static final CssMetaData<MFXRippleGenerator, Number> RIPPLE_SIZE_MULTIPLIER =
				FACTORY.createSizeCssMetaData(
						"-mfx-ripple-size-multiplier",
						MFXRippleGenerator::rippleSizeMultiplierProperty,
						4.0
				);

		static {
			List<CssMetaData<? extends Styleable, ?>> data = new ArrayList<>(Region.getClassCssMetaData());
			Collections.addAll(
					data,
					ANIMATE_BACKGROUND, ANIMATION_SPEED, AUTO_CLIP,
					BACKGROUND_COLOR, BACKGROUND_OPACITY, CHECK_BOUNDS,
					RIPPLE_COLOR, RIPPLE_OPACITY, RIPPLE_PREF_SIZE, RIPPLE_SIZE_MULTIPLIER
			);
			cssMetaDataList = List.copyOf(data);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	@Override
	public Region getRegion() {
		return region;
	}

	@Override
	public Supplier<Shape> getClipSupplier() {
		return clipSupplier;
	}

	@Override
	public void setClipSupplier(Supplier<Shape> clipSupplier) {
		this.clipSupplier = clipSupplier;
	}

	@Override
	public Supplier<? extends Ripple<?>> getRippleSupplier() {
		return rippleSupplier;
	}

	@Override
	public void setRippleSupplier(Supplier<? extends Ripple<?>> rippleSupplier) {
		this.rippleSupplier = rippleSupplier;
	}

	@Override
	public Function<MouseEvent, Position> getPositionFunction() {
		return positionFunction;
	}

	@Override
	public void setPositionFunction(Function<MouseEvent, Position> positionFunction) {
		this.positionFunction = positionFunction;
	}
}
