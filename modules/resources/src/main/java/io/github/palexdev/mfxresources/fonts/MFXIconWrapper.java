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

package io.github.palexdev.mfxresources.fonts;

import io.github.palexdev.mfxeffects.animations.AnimationFactory;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This component is intended for wrapping {@link MFXFontIcon}s, extends {@link StackPane} and offers
 * some common features that one may want to use with font icons like:
 * <p> - Generate ripple effects on click
 * <p> - Make the icon round
 * <p></p>
 * The new API makes these two features easier to use for developers. Many times I had to pass the wrapper instance to a
 * controller just to enable them. Now, they both can be activated in CSS and in FXML, you don't even need their instance
 * in the control as the icon can be specified by editing manually the FXML, the only limit being that you can't change
 * the icons' provider.
 * <p></p>
 * Note that by default the size of the wrapper is always the same for both width and height, it can be specified via the
 * {@link #sizeProperty()} or you could let this figure it out automatically at layout time. In the latter case, the
 * size is computed as the maximum between the icon's width and height.
 * <p></p>
 * <b>Animation API</b>
 * <p></p>
 * {@code MFXIconWrapper} is now capable of switching the wrapped icon through an animation and here I'm going to explain
 * here some crucial details here. The API has been developed to be as flexible as possible while still following some
 * critical rules.
 * <p>
 * Following classical ways to implement such thing, there is now a new property, {@link #animatedProperty()}, that allows
 * to enable/disable the system. Then a user must specify a {@link BiFunction} that given the old/current icon and the
 * new icon builds the animation, to be precise the return type is {@link IconAnimation}, I'll tell why in a moment.
 * <p>
 * The main issue with this wrapper is that it has been developed to keep at max two nodes, the ripple generator and the
 * icon. However, some animations may need to operate on both the old and the new. I had different choices at this point,
 * like for example give the user the modifiable children list and let the management be manual. But I didn't want to
 * break this rule so the here's the solution I came up with.
 * <p>
 * When the icon changes, the new one is added to the children list alongside the old one, but it's set to be <b>invisible</b>.
 * At this point, if an animation is already playing it is stopped by invoking {@link IconAnimation#stop(MFXIconWrapper)}.
 * This wrapping class, contains three values, the animation playing, the old icon and the new icon. If multiple animations
 * are played in rapid succession, by stopping the last one with that method, we ensure {@code MFXIconWrapper} is in a
 * consistent state. However it's <b>important</b> to precise that it only ensures the children list consists of the
 * ripple generator (if enabled) and the new icon. If any property of the new icon was changed, that is user's responsibility
 * to reset when the animation stops (see {@link Animations#onStopped(Animation, Runnable, boolean)}).
 * <p>
 * For example, if the animation is changing the opacity value of a node, and the animation is stopped for whatever
 * reason, then the user that coded the animation should take this fact into account and add the proper reset code.
 * <p>
 * After ensuring that no other animation is currently running the set {@link BiFunction} will produce a new {@link IconAnimation}
 * object, and play the new animation.
 * <p>
 * Final note, the {@link IconAnimation} object is also responsible for removing the old icon from the wrapper once
 * the animation ends/stops (again see {@link Animations#onStatus(Animation, Animation.Status, Runnable, boolean)} to
 * understand the difference).
 * <p>
 * Last but not least, I understand that in some occasions it may be difficult to set an animation via code, since often
 * the wrapper is part of the view/skin. So, just like the ripple generator and the round feature, I added a property
 * {@link #animationPresetProperty()} that allows to set one of the predefined animations in {@link AnimationPresets}
 * from CSS by setting the '-mfx-animation-preset' property.
 * <p>
 * <b>A very important note</b> about this: the property is just a bridge to CSS. DO NOT use it via code, there's no
 * point in it. If you use {@link #setAnimationProvider(BiFunction)} and {@link #setAnimationPreset(AnimationPresets)}
 * together the one will overwrite the other!
 */
public class MFXIconWrapper extends StackPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-icon-wrapper";

	private final IconProperty icon = new IconProperty() {
		@Override
		public void set(MFXFontIcon newValue) {
			MFXFontIcon oldValue = get();
			if (isAnimated() && newValue != null) {
				super.set(newValue);
				newValue.setVisible(false);
				addChild(newValue);
				if (animation != null) animation.stop(MFXIconWrapper.this);
				if (animationProvider != null) {
					animation = animationProvider.apply(oldValue, newValue);
					animation.play();
					return;
				}
			}
			removeChild(oldValue);
			super.set(newValue);
			if (newValue != null) addChild(newValue);
		}

		@Override
		protected void invalidated() {
			updateChildren();
		}
	};
	private MFXRippleGenerator rg;
	private EventHandler<MouseEvent> pressHandler;
	private EventHandler<MouseEvent> releaseHandler;
	private EventHandler<MouseEvent> exitHandler;

	private IconAnimation animation;
	private BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> animationProvider;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXIconWrapper() {
		this(new MFXFontIcon(), -1.0);
	}

	public MFXIconWrapper(MFXFontIcon icon) {
		this(icon, -1.0);
	}

	public MFXIconWrapper(MFXFontIcon icon, double size) {
		initialize();
		setIcon(icon);
		setSize(size);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		addEventHandler(MouseEvent.MOUSE_PRESSED, e -> requestFocus());
		setAnimationPreset(AnimationPresets.FADE);
	}

	/**
	 * Calls {@link #enableRippleGenerator(boolean, Function)}, the function to determine the ripple location
	 * uses the {@link MouseEvent#getX()} and {@link MouseEvent#getY()} coordinates.
	 */
	public MFXIconWrapper enableRippleGenerator(boolean enable) {
		return enableRippleGenerator(enable, e -> Position.of(e.getX(), e.getY()));
	}

	/**
	 * Enables or disables the ripple effect for this wrapper depending on the given boolean flag.
	 * <p></p>
	 * If the flag is false the ripple generator is removed from the container and set to null.
	 * <p></p>
	 * If the flag is true a new ripple generator is created, the given function determines where ripple effects will be
	 * generated. An {@link EventHandler} is also added to generate ripples on {@link MouseEvent#MOUSE_PRESSED} when
	 * the clicked button is the primary.
	 *
	 * @throws IllegalStateException if the boolean flag is true and the ripple generator is already present
	 */
	public MFXIconWrapper enableRippleGenerator(boolean enable, Function<MouseEvent, Position> positionFunction) {
		if (!enable) {
			if (rg != null && pressHandler != null) {
				removeEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
				removeEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
				removeEventHandler(MouseEvent.MOUSE_EXITED, exitHandler);
				super.getChildren().remove(rg);
				rg = null;
			}
			return this;
		}

		if (rg != null)
			throw new IllegalStateException("Ripple generator has already been enabled for this icon!");

		if (pressHandler == null) {
			pressHandler = e -> {
				if (e.getButton() == MouseButton.PRIMARY)
					rg.generate(e);
			};
			releaseHandler = e -> rg.release();
			exitHandler = e -> rg.release();
		}

		rg = new MFXRippleGenerator(this);
		rg.setManaged(false);
		rg.setMeToPosConverter(positionFunction);
		addEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
		addEventHandler(MouseEvent.MOUSE_RELEASED, releaseHandler);
		addEventHandler(MouseEvent.MOUSE_EXITED, exitHandler);

		// If while an animation is playing, the ripple generator is being enabled/disabled, we want to stop the animation
		// to ensure the wrapper is in a consistent state before calling updateChildren()
		if (animation != null) animation.stop(this);
		addChild(rg);
		setEnableRipple(true);
		return this;
	}

	/**
	 * Makes this container round by applying a {@link Circle} clip on it.
	 * <p></p>
	 * If the given boolean flag is false the clip is removed.
	 * <p>
	 * If the given boolean flag is true and the clip is already set, simply returns.
	 */
	public MFXIconWrapper makeRound(boolean state) {
		if (!state) {
			setClip(null);
			return this;
		}

		Node clip = getClip();
		if (clip != null) return this;

		Circle circle = new Circle();
		circle.radiusProperty().bind(widthProperty().divide(2.0));
		circle.centerXProperty().bind(widthProperty().divide(2.0));
		circle.centerYProperty().bind(heightProperty().divide(2.0));
		setClip(circle);
		setRound(true);
		return this;
	}

	/**
	 * Makes this container round by applying a {@link Circle} clip on it, uses the given radius for the circle.
	 * <p></p>
	 * If the given boolean flag is false the clip is removed.
	 * <p>
	 * If the given boolean flag is true and the clip is already set, simply returns.
	 */
	public MFXIconWrapper makeRound(boolean state, double radius) {
		if (!state) {
			setClip(null);
			return this;
		}

		Node clip = getClip();
		if (clip != null) return this;

		Circle circle = new Circle(radius);
		circle.centerXProperty().bind(widthProperty().divide(2.0));
		circle.centerYProperty().bind(heightProperty().divide(2.0));
		setClip(circle);
		setRound(true);
		return this;
	}

	/**
	 * Responsible for managing the children of this container.
	 * We want the ripple generator to always "appear" in the back of the icon.
	 * <p>
	 * However, instead of directly managing the children list, it's best for performance to use the
	 * {@link Node#viewOrderProperty()}. The ripple generator is always given order 0, while the icon has order 1,
	 * this way the latter always appears in front of the first.
	 */
	protected void updateChildren() {
		MFXFontIcon icon = getIcon();
		if (rg != null) rg.setViewOrder(0);
		if (icon != null) icon.setViewOrder(1);
	}

	/**
	 * Convenience method for calling {@code super.getChildren().add(child)}.
	 */
	protected void addChild(Node child) {
		super.getChildren().add(child);
	}

	/**
	 * Convenience method for calling {@code super.getChildren().remove(child)}.
	 */
	protected void removeChild(Node child) {
		super.getChildren().remove(child);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to return {@link #getChildrenUnmodifiable()}
	 *
	 * @return
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to also compute the {@link #sizeProperty()} when its value is set to "-1", the size
	 * is computed as the maximum between the icon's width and height and takes into account the padding.
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		if (rg != null) rg.resizeRelocate(0, 0, getWidth(), getHeight());

		MFXFontIcon icon = getIcon();
		if (icon != null && icon.getDescription() != null && !icon.getDescription().isBlank() && getSize() == -1) {
			double iW = icon.prefWidth(-1);
			double iH = icon.prefHeight(-1);
			double size = Math.max(
				snappedLeftInset() + iW + snappedRightInset(),
				snappedTopInset() + iH + snappedBottomInset()
			);
			setSize(size);
		}
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty animated = new SimpleStyleableBooleanProperty(
		StyleableProperties.ANIMATED,
		this,
		"animated",
		false
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableObjectProperty<AnimationPresets> animationPreset = new SimpleStyleableObjectProperty<>(
		StyleableProperties.ANIMATION_PRESET,
		this,
		"animationPreset",
		null
	) {
		@Override
		protected void invalidated() {
			AnimationPresets p = get();
			if (p == null) return;
			setAnimationProvider(p);
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
		StyleableProperties.SIZE,
		this,
		"size",
		-1.0
	) {
		@Override
		protected void invalidated() {
			setPrefSize(get(), get());
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty enableRipple = new SimpleStyleableBooleanProperty(
		StyleableProperties.ENABLE_RIPPLE,
		this,
		"enableRipple",
		false
	) {
		@Override
		protected void invalidated() {
			boolean state = get();
			if (!state && rg != null) enableRippleGenerator(false);
			if (state && rg == null) enableRippleGenerator(true);
			updateChildren();
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableBooleanProperty round = new SimpleStyleableBooleanProperty(
		StyleableProperties.ROUND,
		this,
		"round",
		false
	) {
		@Override
		protected void invalidated() {
			boolean state = get();
			makeRound(state);
		}

		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies whether icon switching should be animated.
	 * <p>
	 * Can be set in CSS via the property: '-mfx-animated'.
	 *
	 * @see #setAnimationProvider(BiFunction)
	 * @see #setAnimationProvider(AnimationPresets)
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public MFXIconWrapper setAnimated(boolean animated) {
		this.animated.set(animated);
		return this;
	}

	public AnimationPresets getAnimationPreset() {
		return animationPreset.get();
	}

	public StyleableObjectProperty<AnimationPresets> animationPresetProperty() {
		return animationPreset;
	}

	public void setAnimationPreset(AnimationPresets animationPreset) {
		this.animationPreset.set(animationPreset);
	}

	public double getSize() {
		return size.get();
	}

	/**
	 * Specifies the size of this container, when set to -1 it will figure out the value automatically at layout time.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-size'.
	 */
	public StyleableDoubleProperty sizeProperty() {
		return size;
	}

	public MFXIconWrapper setSize(double size) {
		this.size.set(size);
		return this;
	}

	public boolean isEnableRipple() {
		return enableRipple.get();
	}

	/**
	 * A useful property to enable ripple effect from CSS, the property will automatically call
	 * {@link #enableRippleGenerator(boolean)}.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-enable-ripple'.
	 */
	public StyleableBooleanProperty enableRippleProperty() {
		return enableRipple;
	}

	public MFXIconWrapper setEnableRipple(boolean enableRipple) {
		this.enableRipple.set(enableRipple);
		return this;
	}

	public boolean isRound() {
		return round.get();
	}

	/**
	 * A useful property to make this container round from CSS, the property will automatically call
	 * {@link #makeRound(boolean)}.
	 * <p></p>
	 * Settable in CSS via the property: '-mfx-round'.
	 */
	public StyleableBooleanProperty roundProperty() {
		return round;
	}

	public MFXIconWrapper setRound(boolean round) {
		this.round.set(round);
		return this;
	}

	//================================================================================
	// CssMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXIconWrapper> FACTORY = new StyleablePropertyFactory<>(StackPane.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXIconWrapper, Boolean> ANIMATED =
			FACTORY.createBooleanCssMetaData(
				"-mfx-animated",
				MFXIconWrapper::animatedProperty,
				false
			);

		private static final CssMetaData<MFXIconWrapper, AnimationPresets> ANIMATION_PRESET =
			FACTORY.createEnumCssMetaData(
				AnimationPresets.class,
				"-mfx-animation-preset",
				MFXIconWrapper::animationPresetProperty,
				null
			);

		private static final CssMetaData<MFXIconWrapper, Number> SIZE =
			FACTORY.createSizeCssMetaData(
				"-mfx-size",
				MFXIconWrapper::sizeProperty,
				-1.0
			);

		private static final CssMetaData<MFXIconWrapper, Boolean> ENABLE_RIPPLE =
			FACTORY.createBooleanCssMetaData(
				"-mfx-enable-ripple",
				MFXIconWrapper::enableRippleProperty,
				false
			);

		private static final CssMetaData<MFXIconWrapper, Boolean> ROUND =
			FACTORY.createBooleanCssMetaData(
				"-mfx-round",
				MFXIconWrapper::roundProperty,
				false
			);

		static {
			List<CssMetaData<? extends Styleable, ?>> data = new ArrayList<>(StackPane.getClassCssMetaData());
			Collections.addAll(data, ANIMATED, ANIMATION_PRESET, SIZE, ENABLE_RIPPLE, ROUND);
			cssMetaDataList = List.copyOf(data);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return MFXIconWrapper.getClassCssMetaData();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the {@link MFXRippleGenerator} instance for this wrapper, note that
	 * if the generator is not enabled this will return null
	 */
	public MFXRippleGenerator getRippleGenerator() {
		return rg;
	}

	public MFXFontIcon getIcon() {
		return icon.get();
	}

	/**
	 * Specifies the currently contained {@link MFXFontIcon}.
	 */
	public IconProperty iconProperty() {
		return icon;
	}

	public MFXIconWrapper setIcon(MFXFontIcon icon) {
		this.icon.set(icon);
		return this;
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given an icon
	 * description/name.
	 * <p>
	 * Keep in mind that the default icons provider for new {@link MFXFontIcon} is {@link IconsProviders#defaultProvider()}
	 */
	public MFXIconWrapper setIcon(String desc) {
		setIcon(new MFXFontIcon(desc));
		return this;
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given an {@link IconProvider} and
	 * an icon description/name.
	 */
	public MFXIconWrapper setIcon(IconProvider provider, String desc) {
		setIcon(new MFXFontIcon().setIconsProvider(provider).setDescription(desc));
		return this;
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given a font icon pack,
	 * the function to convert descriptions/names to unicode characters, and the icon description/name.
	 *
	 * @see MFXFontIcon#setIconsProvider(Font, Function)
	 */
	public MFXIconWrapper setIcon(Font font, Function<String, Character> converter, String desc) {
		setIcon(new MFXFontIcon().setIconsProvider(font, converter).setDescription(desc));
		return this;
	}

	/**
	 * Convenience method to set the {@link #iconProperty()} to a new {@link MFXFontIcon} instance given an {@link IconDescriptor}.
	 */
	public MFXIconWrapper setIcon(IconDescriptor descriptor) {
		setIcon(new MFXFontIcon(descriptor));
		return this;
	}

	/**
	 * @see #setAnimationProvider(BiFunction)
	 */
	public BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> getAnimationProvider() {
		return animationProvider;
	}

	/**
	 * Sets the function responsible for building the animation used when switching icons, this must be used in combination
	 * with {@link #animatedProperty()}, if that is not enabled than no animation will play.
	 * <p>
	 * Also, to be precise, the function must return an {@link IconAnimation} object, see its docs for the why.
	 */
	public MFXIconWrapper setAnimationProvider(BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> animationProvider) {
		this.animationProvider = animationProvider;
		return this;
	}

	/**
	 * This can be used to convert one of the predefined animations in {@link AnimationPresets} to the {@link BiFunction}
	 * needed by {@link #setAnimationProvider(BiFunction)}.
	 */
	public MFXIconWrapper setAnimationProvider(AnimationPresets preset) {
		this.animationProvider = (o, n) -> preset.animate(this, o, n);
		return this;
	}

	//================================================================================
	// Internal Classes
	//================================================================================

	/**
	 * This wrapper class is used by {@link MFXIconWrapper} to manage its state when switching icons through animations.
	 * It has three properties: the animation used for the switch, the old/current icon, and the new icon.
	 * <p>
	 * There are two main functionalities here:
	 * <p> 1) When the animation ends, makes sure to remove the old icon from the children list
	 * <p> 2) When the animation is stopped, ensures that {@link MFXIconWrapper} is in a consistent state by calling
	 * {@link MFXIconWrapper#updateChildren()}
	 *
	 * @see #stop(MFXIconWrapper)
	 */
	public static class IconAnimation {
		private final Animation animation;
		private final MFXFontIcon oldIcon;
		private final MFXFontIcon newIcon;

		public IconAnimation(MFXIconWrapper wrapper, Animation animation, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
			this.animation = animation;
			this.oldIcon = oldIcon;
			this.newIcon = newIcon;
			Animations.onStopped(animation, () -> wrapper.removeChild(oldIcon), true);
		}

		/**
		 * Delegate for {@link Animation#play()}.
		 */
		public IconAnimation play() {
			if (animation != null) animation.play();
			return this;
		}

		/**
		 * This method is automatically invoked by {@link MFXIconWrapper#iconProperty()} when it changes, if another
		 * animation is playing.
		 * <p></p>
		 * If the animation is stopped before its end this method makes sure that the {@link MFXIconWrapper} is in a
		 * consistent state. In particular two things need to be done:
		 * <p> 1) One thing is to ensure the children list contains the right nodes, it's enough to call
		 * {@link MFXIconWrapper#updateChildren()}
		 * <p> 2) The other is not responsibility of this method but rather the animation. When their state transitions
		 * to {@link Animation.Status#STOPPED}, they should restore changes that otherwise may leave the {@link MFXIconWrapper}
		 * and its children in an inconsistent state. More on the difference between 'stopped' and 'finished' here:
		 * {@link Animations#onStatus(Animation, Animation.Status, Runnable, boolean)}.
		 * <p> An example for point 2 may be an animation that scales some content, say from 1.0 to 0.0. If for whatever
		 * reason (like the start of a new animation) the animation is stopped, it's needed to reset the changes, so set
		 * the scale back to 1.0.
		 * <p></p>
		 * The automatic call of this method is to ensure no strange effects occur if multiple animations are started in
		 * rapid succession. For this reason, it's also recommended to keep animations as short as possible. For instance
		 * most of the predefined animations in {@link AnimationPresets} last 200ms.
		 */
		public void stop(MFXIconWrapper wrapper) {
			if (!Animations.isStopped(animation)) {
				animation.stop();
				wrapper.removeChild(oldIcon);
				wrapper.updateChildren();
			}
		}

		public Animation getAnimation() {
			return animation;
		}

		public MFXFontIcon getOldIcon() {
			return oldIcon;
		}

		public MFXFontIcon getNewIcon() {
			return newIcon;
		}
	}

	/**
	 * Enumeration that allows building a series of predefined animations to be used with
	 * {@link MFXIconWrapper#setAnimationProvider(AnimationPresets)}.
	 */
	public enum AnimationPresets {
		/**
		 * This animation fades out the old/current icon and fades in the new icon.
		 */
		FADE {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				newIcon.setOpacity(0.0);
				newIcon.setVisible(true);
				Duration d = M3Motion.SHORT4;
				Interpolator i = M3Motion.STANDARD;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null) sb.add(AnimationFactory.FADE_OUT.build(oldIcon, d, i));
				sb.add(AnimationFactory.FADE_IN.build(newIcon, d, i));
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides up the old/current icon and then down the new icon.
		 */
		SLIDE_UP {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_TOP.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_TOP.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides down the old/current icon and then up the new icon.
		 */
		SLIDE_BOTTOM {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_BOTTOM.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_BOTTOM.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides down both the old/current and new icons.
		 */
		SLIDE_BOTTOM_UP {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_BOTTOM.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_TOP.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides down both the old/current and new icons.
		 */
		SLIDE_UP_BOTTOM {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_TOP.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_BOTTOM.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides right the old/current icon and then left the new icon.
		 */
		SLIDE_RIGHT {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_RIGHT.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_RIGHT.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides left the old/current icon and then right the new icon.
		 */
		SLIDE_LEFT {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_LEFT.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_LEFT.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides right both the old/current and new icons
		 */
		SLIDE_RIGHT_LEFT {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_RIGHT.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_RIGHT.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation slides left both the old/current and new icons
		 */
		SLIDE_LEFT_RIGHT {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				clip(wrapper);
				Duration d = M3Motion.MEDIUM1;
				SequentialBuilder sb = new SequentialBuilder();
				if (oldIcon != null)
					sb.add(AnimationFactory.SLIDE_OUT_LEFT.build(oldIcon, d, M3Motion.EMPHASIZED_ACCELERATE));
				Timeline t = AnimationFactory.SLIDE_IN_LEFT.build(newIcon, d, M3Motion.EMPHASIZED_DECELERATE);
				t.getKeyFrames().add(KeyFrames.of(0, e -> newIcon.setVisible(true)));
				sb.add(t);
				Animations.onStopped(sb.getAnimation(), () -> {
					if (!wrapper.isRound()) wrapper.setClip(null);
				}, true);
				return new IconAnimation(wrapper, sb.getAnimation(), oldIcon, newIcon);
			}
		},

		/**
		 * This animation is similar to the one seen in standard FABs. It animates the scale properties of the
		 * {@link MFXIconWrapper} as well as the opacity of the two icons. It is composed of two phases: the first animates
		 * out the old/current icon, the second animates in the new one.
		 * <p>
		 * A thing worth mentioning is that the animation scales the {@link MFXIconWrapper} directly because for some reason
		 * scaling the two icons results in a wobbly layout.
		 */
		SCALE {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				newIcon.setOpacity(0.0);
				newIcon.setVisible(true);
				Duration d = M3Motion.SHORT4;
				Interpolator downCurve = M3Motion.EMPHASIZED_ACCELERATE;
				Interpolator upCurve = M3Motion.EMPHASIZED_DECELERATE;
				Animation animation = SequentialBuilder.build()
					.add(TimelineBuilder.build()
						.add(KeyFrames.of(d, oldIcon.opacityProperty(), 0.0, downCurve))
						.add(KeyFrames.of(d, wrapper.scaleXProperty(), 0.0, downCurve))
						.add(KeyFrames.of(d, wrapper.scaleYProperty(), 0.0, downCurve))
						.getAnimation()
					)
					.add(TimelineBuilder.build()
						.add(KeyFrames.of(d, newIcon.opacityProperty(), 1.0, upCurve))
						.add(KeyFrames.of(d, wrapper.scaleXProperty(), 1.0, upCurve))
						.add(KeyFrames.of(d, wrapper.scaleYProperty(), 1.0, upCurve))
						.getAnimation()
					)
					.getAnimation();
				Animations.onStopped(animation, () -> {
					newIcon.setOpacity(1.0);
					wrapper.setScaleX(1.0);
					wrapper.setScaleY(1.0);
				}, true);
				return new IconAnimation(wrapper, animation, oldIcon, newIcon);
			}
		},

		/**
		 * This is animation in quite particular and looks good in very few occasions. It uses two Rectangle clips to
		 * hide the old/current icon and at the same time show the new icon.
		 * <p>
		 * The old icon's clip is set to have the width and height of the old icon, while the new one's clip is set to have
		 * a width of 0 and the height of the new icon. The animation sets the first's width progressively to 0, while
		 * the other progressively to the new icon's width.
		 * <p>
		 * Note that in order to get the new icon's width in the wrapper, it's necessary to force a CSS pass by calling
		 * {@link MFXIconWrapper#applyCss()}.
		 */
		CLIP {
			@Override
			public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
				TimelineBuilder aBuilder = new TimelineBuilder();
				Duration d = M3Motion.LONG1;
				Interpolator i = M3Motion.EMPHASIZED;
				double wh = wrapper.getHeight();

				if (oldIcon != null) {
					Rectangle oClip = new Rectangle(oldIcon.prefWidth(-1), oldIcon.prefHeight(-1));
					oClip.setLayoutY(-oClip.getHeight());
					oldIcon.setClip(oClip);
					aBuilder.add(KeyFrames.of(d, oClip.widthProperty(), 0.0, i));
				}

				wrapper.applyCss();
				double nSize = newIcon.prefWidth(-1);
				Rectangle nClip = new Rectangle(0, wh);
				nClip.setLayoutY(-wh);
				newIcon.setClip(nClip);
				newIcon.setVisible(true);
				aBuilder.add(KeyFrames.of(d, nClip.widthProperty(), nSize, i));
				Animations.onStopped(aBuilder.getAnimation(), () -> {
					if (oldIcon != null) oldIcon.setClip(null);
					newIcon.setClip(null);
				}, true);
				return new IconAnimation(wrapper, aBuilder.getAnimation(), oldIcon, newIcon);
			}
		};

		public abstract IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon);

		/**
		 * Used by the slide animations to clip the {@link MFXIconWrapper} so that icons that go outside its bounds are
		 * hidden. The animations automatically remove it once they stop/end.
		 */
		protected void clip(MFXIconWrapper wrapper) {
			if (wrapper.isRound()) return;
			Rectangle r = new Rectangle();
			r.widthProperty().bind(wrapper.widthProperty());
			r.heightProperty().bind(wrapper.heightProperty());
			wrapper.setClip(r);
		}
	}
}
