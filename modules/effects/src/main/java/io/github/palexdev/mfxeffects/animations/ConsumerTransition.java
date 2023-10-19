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
import io.github.palexdev.mfxeffects.enums.Interpolators;
import javafx.animation.Interpolator;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * A simple implementation of {@link FluentTransition} that allows specifying
 * what to do when the {@link #interpolate(double)} method is called by using
 * a {@link Consumer}.
 */
public class ConsumerTransition extends FluentTransition {
	//================================================================================
	// Properties
	//================================================================================
	private Consumer<Double> interpolateConsumer;

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the consumer used by the {@link #interpolate(double)} method.
	 */
	public ConsumerTransition setInterpolateConsumer(Consumer<Double> interpolateConsumer) {
		this.interpolateConsumer = interpolateConsumer;
		return this;
	}

	/**
	 * Calls {@link #setInterpolateConsumer(Consumer)} and then starts the animation.
	 */
	public void playWithConsumer(Consumer<Double> interpolateConsumer) {
		setInterpolateConsumer(interpolateConsumer);
		this.play();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Implementation to make use of a {@link Consumer}.
	 */
	@Override
	protected void interpolate(double frac) {
		this.interpolateConsumer.accept(frac);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer.
	 */
	public static ConsumerTransition of(Consumer<Double> interpolateConsumer) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer);
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer and duration.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, Duration duration) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration);
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer and duration in milliseconds.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, double duration) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration);
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer, duration and interpolator.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, Duration duration, Interpolator interpolator) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator);
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer, duration in milliseconds and interpolator.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, double duration, Interpolator interpolator) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator);
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer, duration and interpolator.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, Duration duration, Interpolators interpolator) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator.toInterpolator());
	}

	/**
	 * Creates a new {@code ConsumerTransition} with the given consumer, duration in milliseconds and interpolator.
	 */
	public static FluentTransition of(Consumer<Double> interpolateConsumer, double duration, Interpolators interpolator) {
		return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator.toInterpolator());
	}
}
