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

package io.github.palexdev.materialfx.effects.ripple.base;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.collections.ObservableStack;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.skins.MFXCheckboxSkin;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract class that defines all the properties and behaviors a RippleGenerator should have.
 * <p>
 * Also defines the style class ("mfx-ripple-generator") for all generators that extend this class.
 * <p></p>
 * When generating ripples, four are three important information:
 * <p> - Region: the generator must have a reference to the {@code Region} in which it will generate ripples.
 * <p> - Position: the generator must know where you want to generate the ripple, so x and y coordinates ({@link #positionFunction}.
 * <p> - Ripple type/shape: the generator must know what kind of ripple you want to generate (circle, rectangle...), {@link #rippleSupplier}.
 * <p> - Clip/Ripple Bounds: the generator should know the bounds beyond which the ripple must not go. In JavaFX to achieve such behavior there is the clip concept, {@link #clipSupplier}.
 *
 * @param <T> the types of ripple accepted by the generator
 */
public abstract class AbstractMFXRippleGenerator<T extends IRipple> extends Region implements IRippleGenerator<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final String STYLE_CLASS = "mfx-ripple-generator";

	protected final ObservableStack<Animation> animationsStack;
	protected final Region region;
	protected Supplier<Shape> clipSupplier;
	protected Supplier<T> rippleSupplier;
	protected Function<MouseEvent, PositionBean> positionFunction;

	protected final BooleanProperty animateBackground = new SimpleBooleanProperty(true);
	protected final BooleanProperty animateShadow = new SimpleBooleanProperty(false);
	protected final BooleanProperty checkBounds = new SimpleBooleanProperty(true);
	protected final IntegerProperty depthLevelOffset = new SimpleIntegerProperty(1);

	//================================================================================
	// Constructors
	//================================================================================
	protected AbstractMFXRippleGenerator(Region region) {
		this.animationsStack = new ObservableStack<>();
		this.region = region;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Abstract method. Every generator must provide a way to generate ripples.
	 */
	public abstract void generateRipple(MouseEvent event);

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This method must be called by all subclasses in the constructor after the super call.
	 * It is responsible for setting the style class, the region size and firing {@link RippleGeneratorEvent} events.
	 */
	protected void initialize() {
		getStyleClass().setAll(STYLE_CLASS);
		prefWidthProperty().bind(region.widthProperty());
		prefHeightProperty().bind(region.heightProperty());
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

		animationsStack.addListener((ListChangeListener<? super Animation>) change -> {
			if (change.getList().isEmpty()) {
				fireGeneratorEvent(RippleGeneratorEvent.ANIMATION_FINISHED_EVENT);
			}
		});
	}

	/**
	 * Checks if the mouse event coordinates are within the {@link Bounds} of the region.
	 */
	protected boolean isWithinBounds(MouseEvent event) {
		if (event == null) {
			return true;
		}

		double eventX = event.getX();
		double eventY = event.getY();
		Bounds bounds = region.getLayoutBounds();
		return bounds.contains(eventX, eventY);
	}

	public boolean isAnimateBackground() {
		return animateBackground.get();
	}

	/**
	 * Specifies if the background of the region should be animated too.
	 * <p>
	 * The animation generally consists in temporarily adding a shape to the generator,
	 * set its fill same as the ripple color, and manipulate its opacity with a timeline.
	 */
	public BooleanProperty animateBackgroundProperty() {
		return animateBackground;
	}

	public void setAnimateBackground(boolean animateBackground) {
		this.animateBackground.set(animateBackground);
	}

	public boolean isAnimateShadow() {
		return animateShadow.get();
	}

	/**
	 * Specifies if the {@link DropShadow} effect of the region should be animates too.
	 * <p>
	 * Mostly used for {@code MFXButtons}.
	 */
	public BooleanProperty animateShadowProperty() {
		return animateShadow;
	}

	public void setAnimateShadow(boolean animateShadow) {
		this.animateShadow.set(animateShadow);
	}

	public boolean isCheckBounds() {
		return checkBounds.get();
	}

	/**
	 * Specifies if {@link #isWithinBounds(MouseEvent)} should be called before generating the ripple.
	 * <p>
	 * The purpose of this property is to disable/bypass the bounds check, it may happen in some cases
	 * that the check must be disabled to make the generator work properly. An example is the {@link MFXCheckboxSkin}.
	 */
	public BooleanProperty checkBoundsProperty() {
		return checkBounds;
	}

	public void setCheckBounds(boolean checkBounds) {
		this.checkBounds.set(checkBounds);
	}

	public int getDepthLevelOffset() {
		return depthLevelOffset.get();
	}

	/**
	 * Specifies by how many levels the shadow should be increased.
	 * For example if the {@link DropShadow} effect is of {@link DepthLevel#LEVEL1} and the
	 * offset is set to 2 then the shadow will shift to {@link DepthLevel#LEVEL3},
	 * (reverted at the end of the animation of course).
	 */
	public IntegerProperty depthLevelOffsetProperty() {
		return depthLevelOffset;
	}

	public void setDepthLevelOffset(int depthLevelOffset) {
		this.depthLevelOffset.set(depthLevelOffset);
	}

	//================================================================================
	// Events
	//================================================================================

	/**
	 * Events class for RippleGenerators.
	 * <p>
	 * Defines a new EventType:
	 * <p>
	 * - ANIMATION_FINISHED_EVENT: when the ripple animation has finished this event is fired by the generator. The tricky part,
	 * When the ripple animation is finished? For this purpose an {@link ObservableStack} is used. When a ripple is generated its animation should be added
	 * to the stack and removed only when the animation is finished. A change listener added to the stack checks if it is empty. When it's empty it means
	 * that all ripple animations have ended, at that point the event is fired <p></p>
	 * <p>
	 * These events are automatically fired by the generator so they should not be fired by users.
	 */
	public static class RippleGeneratorEvent extends Event {

		public static final EventType<RippleGeneratorEvent> ANIMATION_FINISHED_EVENT = new EventType<>(ANY, "ANIMATION_FINISHED_EVENT");

		public RippleGeneratorEvent(EventType<? extends Event> eventType) {
			super(eventType);
		}
	}

	public final EventHandlerProperty<RippleGeneratorEvent> onAnimationFinished = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(RippleGeneratorEvent.ANIMATION_FINISHED_EVENT, get());
		}
	};

	public EventHandler<RippleGeneratorEvent> getOnAnimationFinished() {
		return onAnimationFinished.get();
	}

	/**
	 * Specifies the action to perform when a {@link RippleGeneratorEvent#ANIMATION_FINISHED_EVENT} is fired.
	 *
	 * @see RippleGeneratorEvent
	 */
	public EventHandlerProperty<RippleGeneratorEvent> onAnimationFinishedProperty() {
		return onAnimationFinished;
	}

	public void setOnAnimationFinished(EventHandler<RippleGeneratorEvent> onAnimationFinished) {
		this.onAnimationFinished.set(onAnimationFinished);
	}

	/**
	 * Convenience method to fire {@link RippleGeneratorEvent} events.
	 */
	public void fireGeneratorEvent(EventType<RippleGeneratorEvent> eventType) {
		fireEvent(new RippleGeneratorEvent(eventType));
	}
}
