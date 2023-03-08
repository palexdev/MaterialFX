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

/**
 * A cubic polynomial mapping of the unit interval.
 * <p>
 * Implements third-order Bézier curves.
 * <p></p>
 * Video examples of various curves based on this:
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_fast_linear_to_slow_ease_in.mp4>Fast Linear To Slow Ease In</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease.mp4>Ease</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in.mp4>Ease In</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_to_linear.mp4>Ease In To Linear</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_sine.mp4>Ease In Sine</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_quad.mp4>Ease In Quad</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_cubic.mp4>Ease In Cubic</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_quart.mp4>Ease In Quart</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_quint.mp4>Ease In Quint</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_expo.mp4>Ease In Expo</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_circ.mp4>Ease In Circ</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_back.mp4>Ease In Back</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out.mp4>Ease Out</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_linear_to_ease_out.mp4>Linear To Ease Out</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_sine.mp4>Ease Out Sine</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_quad.mp4>Ease Out Quad</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_cubic.mp4>Ease Out Cubic</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_quart.mp4>Ease Out Quart</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_quint.mp4>Ease Out Quint</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_expo.mp4>Ease Out Expo</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_circ.mp4>Ease Out Circ</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_out_back.mp4>Ease Out Back</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out.mp4>Ease In Out</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_sine.mp4>Ease In Out Sine</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_quad.mp4>Ease In Out Quad</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_cubic.mp4>Ease In Out Cubic</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_quart.mp4>Ease In Out Quart</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_quint.mp4>Ease In Out Quint</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_expo.mp4>Ease In Out Expo</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_circ.mp4>Ease In Out Circ</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_back.mp4>Ease In Out Back</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_fast_out_slow_in.mp4>Fast Out Slow In</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_slow_middle.mp4>Slow Middle</a>
 */
public class Cubic extends Curve {
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;

	private static final double CUBIC_ERROR_BOUND = 0.001;

	public Cubic(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	double elevateCubic(double a, double b, double m) {
		return 3 * a * (1 - m) * (1 - m) * m +
				3 * b * (1 - m) * m * m +
				m * m * m;
	}

	@Override
	public double curve(double t) {
		double start = 0.0;
		double end = 1.0;
		while (true) {
			final double midpoint = (start + end) / 2;
			final double estimate = elevateCubic(x1, x2, midpoint);
			if (Math.abs(t - estimate) < CUBIC_ERROR_BOUND) {
				return elevateCubic(y1, y2, midpoint);
			}
			if (estimate < t) {
				start = midpoint;
			} else {
				end = midpoint;
			}
		}
	}
}
