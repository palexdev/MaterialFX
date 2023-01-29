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

import io.github.palexdev.mfxeffects.enums.Interpolators;
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
 * <p> - The {@code acceleration} is always a negative value since this motion in particular is a UDM (Uniformly Decelerated Motion),
 * hence the terms acceleration and deceleration mean the same thing is this class. Every frame the momentum described above
 * is diminished by the computed or given deceleration, this makes it look like the target is deceleration towards the end.
 * <p> - The {@code displacement} is the difference between the end value and the start value. In an example let's say
 * a Rectangle is at x coordinate 100, and we want it to move to x coordinate 300. The displacement is given by Xf - Xi,
 * 300 - 100 = 200; The displacement is always a positive number, this class will automatically correct it if a negative number is given.
 * <p></p>
 * It is indeed possible to set an {@link Interpolator} to influence the progression of the animation.
 * <p></p>
 * Each frame the progression (relative to each frame) is computed and given to the specified {@link #getOnUpdate()} consumer.
 * <p>
 * To make things more clear let's see an example with the aforementioned Rectangle:
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
 *     MomentumTransition mt = MomentumTransition.fromTime(20, 500);
 *     mt.setOnUpdate(delta -> rt.setTranslateX(rt.getTranslateX() + delta));
 *     mt.setInterpolator(...); // Optional
 *     mt.play();
 * })
 * }
 * </pre>
 * <p></p>
 * A little side note: if you try to debug the values before and after the transition you may notice that the target
 * value may be a little different. MomentumTransition is not 100% precise in the calculations and this is also needed
 * to make the transition look smooth, I'm talking about just some decimals nothing too big.
 * There are ways to make it precise tough.
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
 */
public class MomentumTransition extends Transition {
	//================================================================================
	// Properties
	//================================================================================
	private double momentum;
	private double acceleration;
	private double displacement;

	private double displacementDelta;
	private double lastFrameTime;
	private Consumer<Double> onUpdate = delta -> {
	};

	//================================================================================
	// Constructors
	//================================================================================
	protected MomentumTransition() {
	}

	public MomentumTransition(double momentum, double acceleration, double displacement) {
		assert acceleration < 0;
		this.momentum = momentum;
		this.acceleration = acceleration;
		this.displacement = Math.abs(displacement);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Builds a {@code MomentumTransition} given the displacement and the duration of the animation.
	 * <p>
	 * The momentum and the deceleration are computed automatically by using {@link #timeToMomentum(double, double)}
	 * and {@link #mtToDeceleration(double, double)}.
	 */
	public static MomentumTransition fromTime(double displacement, double millis) {
		MomentumTransition mt = new MomentumTransition();
		double absDisplacement = Math.abs(displacement);
		mt.displacement = absDisplacement;
		mt.setCycleDuration(Duration.millis(millis));
		mt.momentum = timeToMomentum(absDisplacement, millis);
		mt.acceleration = mtToDeceleration(mt.momentum, millis);
		return mt;
	}

	/**
	 * Builds a {@code MomentumTransition} given the displacement and the deceleration.
	 * <p>
	 * The momentum and the duration of the animation are computed automatically by using {@link #decelerationToMomentum(double, double)}
	 * and {@link #mdToTime(double, double)}.
	 * <p></p>
	 * Please note that the {@code deceleration} param must be a negative number.
	 */
	public static MomentumTransition fromDeceleration(double displacement, double deceleration) {
		assert deceleration < 0;
		MomentumTransition mt = new MomentumTransition();
		double absDisplacement = Math.abs(displacement);
		mt.displacement = absDisplacement;
		mt.acceleration = deceleration;
		mt.momentum = decelerationToMomentum(absDisplacement, deceleration);
		mt.setCycleDuration(Duration.millis(mdToTime(mt.momentum, deceleration)));
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
	public static double mtToDeceleration(double momentum, double time) {
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
	public static double mdToTime(double momentum, double deceleration) {
		return -(momentum / deceleration);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void interpolate(double frac) {
		double deltaFrameTime = getDeltaFrameTime();
		displacementDelta = momentum * deltaFrameTime;
		momentum += acceleration * deltaFrameTime;
		update();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Runs every frame to call the {@link #getOnUpdate()} consumer.
	 */
	public void update() {
		if (onUpdate != null) onUpdate.accept(displacementDelta);
	}

	/**
	 * Computes the difference between the current frame time and the last frame time.
	 */
	private double getDeltaFrameTime() {
		double frameTime = getCurrentTime().toMillis();
		double deltaFrameTime = frameTime - lastFrameTime;
		lastFrameTime = frameTime;
		return deltaFrameTime;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Allows to set the interpolator of this animation by using one of the ones provided by
	 * the class {@link Interpolators}.
	 */
	public void setInterpolator(Interpolators interpolator) {
		setInterpolator(interpolator.toInterpolator());
	}

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
	 * @return the action that runs every frame of the animation. The {@link Consumer}
	 * carries the progress made during the last frame (relative to the given displacement)
	 */
	public Consumer<Double> getOnUpdate() {
		return onUpdate;
	}

	/**
	 * @see #getOnUpdate()
	 */
	public void setOnUpdate(Consumer<Double> onUpdate) {
		this.onUpdate = onUpdate;
	}
}
