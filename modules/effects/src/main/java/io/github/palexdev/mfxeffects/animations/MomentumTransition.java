/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxeffects.animations;

import io.github.palexdev.mfxeffects.animations.base.FluentTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * A particular type of {@link Transition} that follows the laws of the UAM (Uniformly Accelerated Motion)
 * to animate a target and make it look like it is decelerating towards the end of the animation.
 * <p></p>
 * A bit of terminology and explanations:
 * <p> - The {@code momentum} is the initial velocity of the animation, the speed at which the target will progress towards
 * the end at the start of the animation. This velocity is not constant though as it is decreased every frame by the
 * deceleration(negative acceleration) property.
 * <p> - The {@code acceleration} is always a negative value since this motion in particular is a UDM (Uniformly Decelerated Motion);
 * hence the terms acceleration and deceleration mean the same thing is this class. Every frame the momentum described above
 * is diminished by the computed or given deceleration, this makes it look like the target is deceleration towards the end.
 * <p> - The {@code displacement} is the difference between the end value and the start value. In an example, let's say
 * a Rectangle is at x coordinate 100, and we want it to move to x coordinate 300. The displacement is given by Xf - Xi,
 * 300 - 100 = 200; The displacement is always a positive number, this class will automatically correct it if a negative number is given.
 * However, the sign of the displacement determines the direction: negative displacements mean backwards, positive displacements mean forward.
 * So, the value fed to {@link #update()} will be multiplied according to the direction: -1 for backwards, 1 for forward.
 * <p></p>
 * It is indeed possible to set an {@link Interpolator} to influence the progression of the animation.
 * <p></p>
 * Each frame the progression (relative to each frame) is computed and given to the specified {@link #getOnUpdate()} consumer.
 * <p>
 * To make things clearer, let's see an example with the aforementioned Rectangle:
 * <pre>
 * {@code
 *
 * // We assume that the Rectangle is at Xi = 100 because of a translateX,
 * // therefore what we want to animate is the translateX property.
 * // We also assume that we want to move the Rectangle every time we detect a SCROLL event (but it could be everything,
 * // the concept remains the same)
 *
 * Rectangle rt = new ...;
 * pane.addEventHandler(ScrollEvent.SCROLL, e -> {
 *     // Every scroll we start a new MomentumTransition
 *     // I will use the fromTime(...) static method which makes things easier,
 *     // but you can also use the fromDeceleration(...) static method if you want a constant deceleration,
 *     // or specify all the parameters with the constructor (not recommended unless you really know what you are dealing with)
 *     // The Rectangle will move of 20 pixels every scroll and the animation will last 500 milliseconds
 *     MomentumTransition.fromTime(20, 500)
 *         .setOnUpdate(delta -> rt.setTranslateX(rt.getTranslateX() + delta))
 *         .setInterpolatorFluent(...) // Optional
 *         .play();
 * })
 * }
 * </pre>
 * <p></p>
 * A little side note: if you try to debug the values before and after the transition, you may notice that the target
 * value may be a little different. MomentumTransition is not 100% precise in the calculations, and this is also needed
 * to make the transition look smooth. This phenomenon is called 'overshoot' and can described as follows
 * <pre>
 *     Easing and overshoot are terms that describe how an object moves in and out of a keyframe.
 *     Easing refers to the gradual acceleration or deceleration of an object,
 *     while overshoot refers to the extra movement that occurs when an object passes its target position and then bounces back.
 *     Easing and overshoot can create a sense of realism, weight, and energy in your animation, depending on how you use them.
 * </pre>
 * The overshoot can be avoided or mitigated with different methods: clamping, changing the transition parameters, changing
 * the interpolator. The clamping method completely removes the effect.
 * <p>
 * You could track the target is an external variable that is not modified by the transition but from the action that leads
 * to the transition. Then in the update consumer (setOnUpdate(delta -> {...}) you can compute the new value and clamp it
 * with Math.min(...) or Math.max(...) so that it cannot go above/below the desired target.
 * <p>
 * Example:
 * <pre>
 * {@code
 * // I'll use the Rectangle example again... let's initialize the target with the current position
 * double target = 100.0;
 *
 * Rectangle rt = new ...;
 * pane.addEventHandler(ScrollEvent.SCROLL, e -> {
 *     // Increase target by displacement
 *     target += 20;
 *
 *     MomentumTransition mt = MomentumTransition.fromTime(20, 500);
 *     mt.setOnUpdate(delta -> {
 *        double val = rt.getTranslateX() + delta;
 *        double clamped = Math.min(val, target); // We don't want it to exceed the target
 *        rt.setTranslateX(clamped);
 *     });
 *     mt.setInterpolator(...); // Optional
 *     mt.play();
 * }
 * }
 * </pre>
 * That said, I still do not recommend removing the overshoot effect as it makes animations more appealing.
 */
public class MomentumTransition extends FluentTransition {
	//================================================================================
	// Properties
	//================================================================================
	private double momentum;
	private double acceleration;
	private double displacement;
	private int direction = 1;

	private double displacementDelta;
	private double lastFrameTime;
	private Consumer<Double> onUpdate = delta -> {};

	//================================================================================
	// Constructors
	//================================================================================
	protected MomentumTransition() {
	}

	public MomentumTransition(double momentum, double acceleration, double displacement) {
		assert acceleration < 0;
		this.momentum = momentum;
		this.acceleration = acceleration;
		this.direction = (displacement < 0) ? -1 : 1;
		this.displacement = Math.abs(displacement);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Builds a {@code MomentumTransition} given the displacement and the duration of the animation.
	 * <p>
	 * The momentum and the deceleration are computed automatically by using {@link #timeToMomentum(double, double)}
	 * and {@link #momentumToDeceleration(double, double)}.
	 */
	public static MomentumTransition fromTime(double displacement, double millis) {
		MomentumTransition mt = new MomentumTransition();
		mt.direction = (displacement < 0) ? -1 : 1;
		double absDisplacement = Math.abs(displacement);
		mt.displacement = absDisplacement;
		mt.setCycleDuration(Duration.millis(millis));
		mt.momentum = timeToMomentum(absDisplacement, millis);
		mt.acceleration = momentumToDeceleration(mt.momentum, millis);
		return mt;
	}

	/**
	 * Builds a {@code MomentumTransition} given the displacement and the deceleration.
	 * <p>
	 * The momentum and the duration of the animation are computed automatically by using {@link #decelerationToMomentum(double, double)}
	 * and {@link #momentumToTime(double, double)}.
	 * <p></p>
	 * Please note that the {@code deceleration} param must be a negative number.
	 */
	public static MomentumTransition fromDeceleration(double displacement, double deceleration) {
		assert deceleration < 0;
		MomentumTransition mt = new MomentumTransition();
		mt.direction = (displacement < 0) ? -1 : 1;
		double absDisplacement = Math.abs(displacement);
		mt.displacement = absDisplacement;
		mt.acceleration = deceleration;
		mt.momentum = decelerationToMomentum(absDisplacement, deceleration);
		mt.setCycleDuration(Duration.millis(momentumToTime(mt.momentum, deceleration)));
		return mt;
	}

	/**
	 * Given the displacement and the time, computes the momentum.
	 * <p></p>
	 * Formula: {@code (2 * displacement) / time}
	 */
	public static double timeToMomentum(double displacement, double time) {
		return (2 * displacement) / time;
	}

	/**
	 * Given the momentum and duration, computes the deceleration.
	 * <p></p>
	 * Formula: {@code -(momentum / time)}
	 */
	public static double momentumToDeceleration(double momentum, double time) {
		return -(momentum / time);
	}

	/**
	 * Given the displacement and the deceleration, computes the momentum.
	 * <p></p>
	 * Formula: {@code Math.sqrt(-2 * deceleration * displacement)}
	 */
	public static double decelerationToMomentum(double displacement, double deceleration) {
		return Math.sqrt(-2 * deceleration * displacement);
	}

	/**
	 * Given the momentum and the deceleration, computes the duration.
	 * <p></p>
	 * Formula: {@code -(momentum / deceleration)}
	 */
	public static double momentumToTime(double momentum, double deceleration) {
		return -(momentum / deceleration);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void interpolate(double frac) {
		double deltaFrameTime = getDeltaFrameTime(frac);
		displacementDelta = momentum * deltaFrameTime;
		momentum += acceleration * deltaFrameTime;
		update();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Runs every frame to call the {@link #getOnUpdate()} consumer.
	 * <p>
	 * The delta displacement given by the {@link Consumer} is first multiplied by the direction detected when building
	 * the animation, -1 for negative displacements, 1 for positive displacements.
	 */
	public void update() {
		if (onUpdate != null) onUpdate.accept(displacementDelta * direction);
	}

	/**
	 * Computes the difference between the current frame time and the last frame time.
	 * <p>
	 * The current frame time is not computed by simply calling {@link #getCurrentTime()}, but rather we use the 'frac'
	 * parameter of the {@link #interpolate(double)} method. This is crucial because {@link Interpolator}s in JavaFX
	 * directly affect the value of the 'frac' parameter, in other words by getting the current frame time as
	 * {@code getCycleDuration().toMillis() * frac} we automatically take into account the animation's interpolator.
	 */
	private double getDeltaFrameTime(double frac) {
		double frameTime = getCycleDuration().toMillis() * frac;
		double deltaFrameTime = frameTime - lastFrameTime;
		lastFrameTime = frameTime;
		return deltaFrameTime;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the momentum of the transition
	 */
	public double getMomentum() {
		return momentum;
	}

	/**
	 * @return the acceleration of the transition
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * @return the displacement of the transition
	 */
	public double getDisplacement() {
		return displacement;
	}

	/**
	 * @return the displacement's direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @return the action that runs every frame of the animation. The {@link Consumer}
	 * carries the progress made during the last frame (relative to the given displacement)
	 */
	public Consumer<Double> getOnUpdate() {
		return onUpdate;
	}

	/**
	 * @see #getOnUpdate()
	 */
	public MomentumTransition setOnUpdate(Consumer<Double> onUpdate) {
		this.onUpdate = onUpdate;
		return this;
	}
}
