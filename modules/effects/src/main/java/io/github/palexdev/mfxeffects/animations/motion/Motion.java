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

import io.github.palexdev.mfxeffects.animations.motion.base.Curve;
import io.github.palexdev.mfxeffects.beans.Offset;
import javafx.animation.Interpolator;

/**
 * This class holds a collection of {@link Curve}s (JavaFX's {@link Interpolator}s) to produce any kind of animation
 * you could ever want. These have been ported from the {@code Flutter} framework, in particular from the
 * <a href=https://github.com/flutter/flutter/tree/master/packages/flutter/lib/src/animation>animations</a> package.
 */
public class Motion {
	public static final Curve LINEAR = new Linear();
	public static final Curve DECELERATE = new DecelerateCurve();
	public static final Curve FAST_LINEAR_TO_SLOW_EASE_IN = new Cubic(0.18, 1.0, 0.04, 1.0);
	public static final Curve EASE = new Cubic(0.25, 0.1, 0.25, 1.0);
	public static final Curve EASE_IN = new Cubic(0.42, 0.0, 1.0, 1.0);
	public static final Curve EASE_IN_TO_LINEAR = new Cubic(0.67, 0.03, 0.65, 0.09);
	public static final Curve EASE_IN_SINE = new Cubic(0.47, 0.0, 0.745, 0.715);
	public static final Curve EASE_IN_QUAD = new Cubic(0.55, 0.085, 0.68, 0.53);
	public static final Curve EASE_IN_CUBIC = new Cubic(0.55, 0.055, 0.675, 0.19);
	public static final Curve EASE_IN_QUART = new Cubic(0.895, 0.03, 0.685, 0.22);
	public static final Curve EASE_IN_QUINT = new Cubic(0.755, 0.05, 0.855, 0.06);
	public static final Curve EASE_IN_EXPO = new Cubic(0.95, 0.05, 0.795, 0.035);
	public static final Curve EASE_IN_CIRC = new Cubic(0.6, 0.04, 0.98, 0.335);
	public static final Curve EASE_IN_BACK = new Cubic(0.6, -0.28, 0.735, 0.045);
	public static final Curve EASE_OUT = new Cubic(0.0, 0.0, 0.58, 1.0);
	public static final Curve LINEAR_TO_EASE_OUT = new Cubic(0.35, 0.91, 0.33, 0.97);
	public static final Curve EASE_OUT_SINE = new Cubic(0.39, 0.575, 0.565, 1.0);
	public static final Curve EASE_OUT_QUAD = new Cubic(0.25, 0.46, 0.45, 0.94);
	public static final Curve EASE_OUT_CUBIC = new Cubic(0.215, 0.61, 0.355, 1.0);
	public static final Curve EASE_OUT_QUART = new Cubic(0.165, 0.84, 0.44, 1.0);
	public static final Curve EASE_OUT_QUINT = new Cubic(0.23, 1.0, 0.32, 1.0);
	public static final Curve EASE_OUT_EXPO = new Cubic(0.19, 1.0, 0.22, 1.0);
	public static final Curve EASE_OUT_CIRC = new Cubic(0.075, 0.82, 0.165, 1.0);
	public static final Curve EASE_OUT_BACk = new Cubic(0.175, 0.885, 0.32, 1.275);
	public static final Curve EASE_IN_OUT = new Cubic(0.42, 0.0, 0.58, 1.0);
	public static final Curve EASE_IN_OUT_SINE = new Cubic(0.445, 0.05, 0.55, 0.95);
	public static final Curve EASE_IN_OUT_QUAD = new Cubic(0.455, 0.03, 0.515, 0.955);
	public static final Curve EASE_IN_OUT_CUBIC = new Cubic(0.645, 0.045, 0.355, 1.0);
	public static final Curve EASE_IN_OUT_CUBIC_EMPHASIZED = new ThreePointCubic(
			new Offset(0.05, 0), new Offset(0.133333, 0.06),
			new Offset(0.166666, 0.4),
			new Offset(0.208333, 0.82), new Offset(0.25, 1)
	);
	public static final Curve EASE_IN_OUT_QUART = new Cubic(0.77, 0.0, 0.175, 1.0);
	public static final Curve EASE_IN_OUT_QUINT = new Cubic(0.86, 0.0, 0.07, 1.0);
	public static final Curve EASE_IN_OUT_EXPO = new Cubic(1.0, 0.0, 0.0, 1.0);
	public static final Curve EASE_IN_OUT_CIRC = new Cubic(0.785, 0.135, 0.15, 0.86);
	public static final Curve EASE_IN_OUT_BACK = new Cubic(0.68, -0.55, 0.265, 1.55);
	public static final Curve FADE_OUT_SLOW_IN = new Cubic(0.4, 0.0, 0.2, 1.0);
	public static final Curve SLOW_MIDDLE = new Cubic(0.15, 0.85, 0.85, 0.15);
	public static final Curve BOUNCE_IN = new BounceInCurve();
	public static final Curve BOUNCE_OUT = new BounceOutCurve();
	public static final Curve BOUND_IN_OUT = new BounceInOutCurve();
	public static final Curve ELASTIC_IN = new ElasticInCurve();
	public static final Curve ELASTIC_OUT = new ElasticOutCurve();
	public static final Curve ELASTIC_IN_OUT = new ElasticInOutCurve();

	static double bounce(double t) {
		if (t < 1.0 / 2.75) {
			return 7.5625 * t * t;
		} else if (t < 2 / 2.75) {
			t -= 1.5 / 2.75;
			return 7.5625 * t * t + 0.75;
		} else if (t < 2.5 / 2.75) {
			t -= 2.25 / 2.75;
			return 7.5625 * t * t + 0.9375;
		}
		t -= 2.625 / 2.75;
		return 7.5625 * t * t + 0.984375;
	}
}
