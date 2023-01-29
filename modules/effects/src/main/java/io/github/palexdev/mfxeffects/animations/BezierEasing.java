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

import javafx.animation.Interpolator;

/**
 * Cubic Bezier implementation from <a href="https://github.com/gre/bezier-easing">https://github.com/gre/bezier-easing</a>
 * <p>
 * Make sure to check this tool out! <a href="https://cubic-bezier.com/#0,0,.58,1">https://cubic-bezier.com</a>
 */
public class BezierEasing {
	private static final int NEWTON_ITERATIONS = 4;
	private static final double NEWTON_MIN_SLOPE = 0.001;
	private static final double SUBDIVISION_PRECISION = 0.0000001;
	private static final int SUBDIVISION_MAX_ITERATIONS = 10;

	private static final int kSplineTableSize = 11;
	private static final double kSampleStepSize = 1.0 / (kSplineTableSize - 1.0);

	public static final Interpolator EASE = toInterpolator(.25, 1, .25, 1);
	public static final Interpolator LINEAR = toInterpolator(0, 0, 1, 1);
	public static final Interpolator EASE_IN = toInterpolator(.42, 0, 1, 1);
	public static final Interpolator EASE_OUT = toInterpolator(0, 0, .58, 1);
	public static final Interpolator EASE_BOTH = toInterpolator(.42, 0, .58, 1);

	private BezierEasing() {
	}

	private static double a(double a1, double a2) {
		return 1.0 - 3.0 * a2 + 3.0 * a1;
	}

	private static double b(double a1, double a2) {
		return 3.0 * a2 - 6.0 * a1;
	}

	private static double c(double a1) {
		return 3.0 * a1;
	}

	private static double calcBezier(double t, double a1, double a2) {
		return ((a(a1, a2) * t + b(a1, a2)) * t + c(a1)) * t;
	}

	// Returns dx/dt given t, x1, and x2, or dy/dt given t, y1, and y2.
	private static double getSlope(double aT, double aA1, double aA2) {
		return 3.0 * a(aA1, aA2) * aT * aT + 2.0 * b(aA1, aA2) * aT + c(aA1);
	}

	private static double binarySubdivide(double aX, double aA, double aB, double mX1, double mX2) {
		double currentX, currentT, i = 0;
		do {
			currentT = aA + (aB - aA) / 2.0;
			currentX = calcBezier(currentT, mX1, mX2) - aX;
			if (currentX > 0.0) {
				aB = currentT;
			} else {
				aA = currentT;
			}
		} while (Math.abs(currentX) > SUBDIVISION_PRECISION && ++i < SUBDIVISION_MAX_ITERATIONS);
		return currentT;
	}

	private static double newtonRaphsonIterate(double aX, double aGuessT, double mX1, double mX2) {
		for (var i = 0; i < NEWTON_ITERATIONS; ++i) {
			var currentSlope = getSlope(aGuessT, mX1, mX2);
			if (currentSlope == 0.0) {
				return aGuessT;
			}
			var currentX = calcBezier(aGuessT, mX1, mX2) - aX;
			aGuessT -= currentX / currentSlope;
		}
		return aGuessT;
	}

	public static double bezier(double frac, double x1, double y1, double x2, double y2) {
		double[] values = new double[kSplineTableSize];
		for (int i = 0; i < values.length; ++i) {
			values[i] = calcBezier(i * kSampleStepSize, x1, x2);
		}

		double intervalStart = 0.0;
		int currentSample = 1;
		double lastSample = kSplineTableSize - 1;

		for (; currentSample != lastSample && values[currentSample] <= frac; ++currentSample) {
			intervalStart += kSampleStepSize;
		}
		--currentSample;

		// Interpolate to provide an initial guess for t
		double dist = (frac - values[currentSample]) / (values[currentSample + 1] - values[currentSample]);
		double guessForT = intervalStart + dist * kSampleStepSize;

		double initialSlope = getSlope(guessForT, x1, x2);
		double fracToT;
		if (initialSlope >= NEWTON_MIN_SLOPE) {
			fracToT = newtonRaphsonIterate(frac, guessForT, x1, x2);
		} else if (initialSlope == 0.0) {
			fracToT = guessForT;
		} else {
			fracToT = binarySubdivide(frac, intervalStart, intervalStart + kSampleStepSize, x1, x2);
		}

		return calcBezier(fracToT, y1, y2);
	}

	public static Interpolator toInterpolator(double x1, double y1, double x2, double y2) {
		return new Interpolator() {
			@Override
			protected double curve(double t) {
				return bezier(t, x1, y1, x2, y2);
			}
		};
	}
}