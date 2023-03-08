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

package io.github.palexdev.mfxeffects.animations.motion;

import io.github.palexdev.mfxeffects.animations.base.Curve;
import javafx.animation.Interpolator;
import javafx.util.Duration;

import static javafx.util.Duration.millis;

/**
 * This class contains all the 'tokens' needed by Material 3 components regarding animations/motion, such as
 * {@link Curve}s and {@link Duration}s.
 * <p></p>
 * Durations:
 * <p> - {@link #SHORT1}: 50ms
 * <p> - {@link #SHORT2}: 100ms
 * <p> - {@link #SHORT3}: 150ms
 * <p> - {@link #SHORT4}: 200ms
 * <p></p>
 * <p> - {@link #MEDIUM1}: 250ms
 * <p> - {@link #MEDIUM2}: 300ms
 * <p> - {@link #MEDIUM3}: 350ms
 * <p> - {@link #MEDIUM4}: 400ms
 * <p></p>
 * <p> - {@link #LONG1}: 450ms
 * <p> - {@link #LONG2}: 500ms
 * <p> - {@link #LONG3}: 550ms
 * <p> - {@link #LONG4}: 600ms
 * <p></p>
 * <p> - {@link #EXTRA_LONG1}: 700ms
 * <p> - {@link #EXTRA_LONG2}: 800ms
 * <p> - {@link #EXTRA_LONG3}: 900ms
 * <p> - {@link #EXTRA_LONG4}: 1000ms
 *
 * @see <a href=https://m3.material.io/styles/motion/overview>Material 3 Guidelines</a>
 */
public class M3Motion {
	//================================================================================
	// Curves
	//================================================================================
	public static final Interpolator LINEAR = Motion.LINEAR;
	public static final Interpolator STANDARD = new Cubic(0.2, 0.0, 0.0, 1.0);
	public static final Interpolator STANDARD_ACCELERATE = new Cubic(0.3, 0, 1.0, 1.0);
	public static final Interpolator STANDARD_DECELERATE = new Cubic(0, 0, 0, 1.0);
	public static final Interpolator EMPHASIZED = Motion.EASE_IN_OUT_CUBIC_EMPHASIZED;
	public static final Interpolator EMPHASIZED_ACCELERATE = new Cubic(0.3, 0.0, 0.8, 0.15);
	public static final Interpolator EMPHASIZED_DECELERATE = new Cubic(0.05, 0.7, 0.1, 1.0);
	public static final Interpolator LEGACY = new Cubic(0.4, 0, 0.2, 1.0);
	public static final Interpolator LEGACY_ACCELERATE = new Cubic(0.4, 0, 1.0, 1.0);
	public static final Interpolator LEGACY_DECELERATE = new Cubic(0, 0, 0.2, 1.0);

	//================================================================================
	// Durations
	//================================================================================
	public static final Duration SHORT1 = millis(50);
	public static final Duration SHORT2 = millis(100);
	public static final Duration SHORT3 = millis(150);
	public static final Duration SHORT4 = millis(200);

	public static final Duration MEDIUM1 = millis(250);
	public static final Duration MEDIUM2 = millis(300);
	public static final Duration MEDIUM3 = millis(350);
	public static final Duration MEDIUM4 = millis(400);

	public static final Duration LONG1 = millis(450);
	public static final Duration LONG2 = millis(500);
	public static final Duration LONG3 = millis(550);
	public static final Duration LONG4 = millis(600);

	public static final Duration EXTRA_LONG1 = millis(700);
	public static final Duration EXTRA_LONG2 = millis(800);
	public static final Duration EXTRA_LONG3 = millis(900);
	public static final Duration EXTRA_LONG4 = millis(1000);

	//================================================================================
	// Constructors
	//================================================================================
	private M3Motion() {
	}
}
