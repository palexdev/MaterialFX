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

package io.github.palexdev.mfxeffects.animations.base;

import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.enums.Interpolators;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * Extension of {@link Transition} to make some methods use fluent API.
 */
public abstract class FluentTransition extends Transition {

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the transition duration.
	 */
	public FluentTransition setDuration(Duration duration) {
		this.setCycleDuration(duration);
		return this;
	}

	/**
	 * Sets the transition duration in milliseconds.
	 */
	public FluentTransition setDuration(double millis) {
		this.setCycleDuration(Duration.millis(millis));
		return this;
	}

	/**
	 * Sets the transition's interpolator.
	 */
	public FluentTransition setInterpolatorFluent(Interpolator interpolator) {
		this.setInterpolator(interpolator);
		return this;
	}

	/**
	 * Sets the transition's interpolator to one provided by {@link Interpolators}.
	 */
	public FluentTransition setInterpolatorFluent(Interpolators interpolator) {
		return setInterpolatorFluent(interpolator.toInterpolator());
	}

	/**
	 * Sets the transition's delay.
	 */
	public FluentTransition setDelayFluent(Duration duration) {
		this.setDelay(duration);
		return this;
	}

	/**
	 * Sets the action to perform at the end of the animation.
	 */
	public FluentTransition setOnFinishedFluent(EventHandler<ActionEvent> handler) {
		setOnFinished(handler);
		return this;
	}

	/**
	 * Sets the action to perform when the animation stops, please make sure to understand the difference
	 * between 'end' and 'stop', see {@link Animations#onStatus(Animation, Status, Runnable, boolean)}.
	 */
	public FluentTransition setOnStopped(Runnable action, boolean oneshot) {
		Animations.onStopped(this, action, oneshot);
		return this;
	}
}
