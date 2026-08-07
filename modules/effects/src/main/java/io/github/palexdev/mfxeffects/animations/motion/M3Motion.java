/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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
import javafx.util.Duration;

import static javafx.util.Duration.millis;

/// This class contains all the 'tokens' needed by Material 3 components regarding animations/motion, such as
/// [Curve]s and [Duration]s.
///
/// Durations:
///  - [#SHORT1]: 50ms
///  - [#SHORT2]: 100ms
///  - [#SHORT3]: 150ms
///  - [#SHORT4]: 200ms
///
///  - [#MEDIUM1]: 250ms
///  - [#MEDIUM2]: 300ms
///  - [#MEDIUM3]: 350ms
///  - [#MEDIUM4]: 400ms
///
///  - [#LONG1]: 450ms
///  - [#LONG2]: 500ms
///  - [#LONG3]: 550ms
///  - [#LONG4]: 600ms
///
///  - [#EXTRA_LONG1]: 700ms
///  - [#EXTRA_LONG2]: 800ms
///  - [#EXTRA_LONG3]: 900ms
///  - [#EXTRA_LONG4]: 1000ms
///
/// #### Material Expressive Update
/// Quoting the official documentation:
///
///  - Expressive is Material’s opinionated motion scheme, and should be used for most situations,
/// particularly hero moments (whatever that means) and key interactions.
///
///  - Standard feels more functional with minimal bounce and should be used for utilitarian products.
///
/// @see [Material 3 Guidelines](https://m3.material.io/styles/motion/overview)
public class M3Motion {
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
    // Curves
    //================================================================================
    public static final Curve LINEAR = Motion.LINEAR;
    public static final Curve STANDARD = new Cubic(0.2, 0.0, 0.0, 1.0);
    public static final Curve STANDARD_ACCELERATE = new Cubic(0.3, 0, 1.0, 1.0);
    public static final Curve STANDARD_DECELERATE = new Cubic(0, 0, 0, 1.0);
    public static final Curve EMPHASIZED = Motion.EASE_IN_OUT_CUBIC_EMPHASIZED;
    public static final Curve EMPHASIZED_ACCELERATE = new Cubic(0.3, 0.0, 0.8, 0.15);
    public static final Curve EMPHASIZED_DECELERATE = new Cubic(0.05, 0.7, 0.1, 1.0);
    public static final Curve LEGACY = new Cubic(0.4, 0, 0.2, 1.0);
    public static final Curve LEGACY_ACCELERATE = new Cubic(0.4, 0, 1.0, 1.0);
    public static final Curve LEGACY_DECELERATE = new Cubic(0, 0, 0.2, 1.0);

    public static final MotionPreset EXPRESSIVE_FAST_SPATIAL = MotionPreset.of(
        new Cubic(0.42, 1.67, 0.21, 0.90),
        MEDIUM3
    );
    public static final MotionPreset EXPRESSIVE_DEFAULT_SPATIAL = MotionPreset.of(
        new Cubic(0.38, 1.21, 0.22, 1.0),
        LONG2
    );
    public static final MotionPreset EXPRESSIVE_SLOW_SPATIAL = MotionPreset.of(
        new Cubic(0.39, 1.29, 0.35, 0.98),
        650
    );

    public static final MotionPreset EXPRESSIVE_FAST_EFFECTS = MotionPreset.of(
        new Cubic(0.31, 0.94, 0.34, 1.0),
        SHORT3
    );
    public static final MotionPreset EXPRESSIVE_DEFAULT_EFFECTS = MotionPreset.of(
        new Cubic(0.34, 0.80, 0.34, 1.0),
        SHORT4
    );
    public static final MotionPreset EXPRESSIVE_SLOW_EFFECTS = MotionPreset.of(
        new Cubic(0.34, 0.88, 0.34, 1.0),
        MEDIUM2
    );

    public static final MotionPreset STANDARD_FAST_SPATIAL = MotionPreset.of(
        new Cubic(0.27, 1.06, 0.18, 1.0),
        MEDIUM3
    );
    public static final MotionPreset STANDARD_DEFAULT_SPATIAL = MotionPreset.of(
        STANDARD_FAST_SPATIAL.curve,
        LONG2
    );
    public static final MotionPreset STANDARD_SLOW_SPATIAL = MotionPreset.of(
        STANDARD_FAST_SPATIAL.curve,
        750
    );

    public static final MotionPreset STANDARD_FAST_EFFECTS = EXPRESSIVE_FAST_EFFECTS;
    public static final MotionPreset STANDARD_DEFAULT_EFFECTS = EXPRESSIVE_DEFAULT_EFFECTS;
    public static final MotionPreset STANDARD_SLOW_EFFECTS = EXPRESSIVE_SLOW_EFFECTS;

    //================================================================================
    // Constructors
    //================================================================================
    private M3Motion() {}

    //================================================================================
    // Inner Classes
    //================================================================================
    public record MotionPreset(
        Curve curve,
        Duration duration
    ) {
        public static MotionPreset of(Curve curve, double millis) {
            return of(curve, Duration.millis(millis));
        }

        public static MotionPreset of(Curve curve, Duration duration) {
            return new MotionPreset(curve, duration);
        }

        public double millis() {
            return duration.toMillis();
        }
    }
}
