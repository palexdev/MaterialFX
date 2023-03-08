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
import io.github.palexdev.mfxeffects.beans.Offset;

/**
 * A cubic polynomial composed of two curves that share a common center point.
 * <p>
 * The curve runs through three points: (0,0), the {@code midpoint}, and (1,1).
 * <p>
 * The {@link Motion} class contains a curve defined with this class:
 * {@link Motion#EASE_IN_OUT_CUBIC_EMPHASIZED}.
 * <p>
 * The {@code ThreePointCubic} class implements third-order Bézier curves, where two
 * curves share an interior {@code midpoint} that the curve passes through. If the
 * control points surrounding the middle point ({@code b1}, and {@code a2}) are not
 * co-linear with the middle point, then the curve's derivative will have a
 * discontinuity (a cusp) at the shared middle point.
 * <p></p>
 * <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_ease_in_out_cubic_emphasized.mp4>ThreePointCubic Example</a>
 * Note: the above example is built with the given {@link Offset}s in order:
 * <pre>
 * {@code
 *   Offset(0.05, 0), Offset(0.133333, 0.06), // a1 and b1
 *   Offset(0.166666, 0.4), // midpoint
 *   Offset(0.208333, 0.82), Offset(0.25, 1) // a2 and b2
 * }
 * </pre>
 */
public class ThreePointCubic extends Curve {
	private final Offset a1;
	private final Offset b1;
	private final Offset midpoint;
	private final Offset a2;
	private final Offset b2;

	public ThreePointCubic(Offset a1, Offset b1, Offset midpoint, Offset a2, Offset b2) {
		this.a1 = a1;
		this.b1 = b1;
		this.midpoint = midpoint;
		this.a2 = a2;
		this.b2 = b2;
	}

	@Override
	public double curve(double t) {
		boolean firstCurve = t < midpoint.getDx();
		double scaleX = firstCurve ? midpoint.getDx() : 1.0 - midpoint.getDx();
		double scaleY = firstCurve ? midpoint.getDy() : 1.0 - midpoint.getDy();
		double scaledT = (t - (firstCurve ? 0.0 : midpoint.getDx())) / scaleX;
		if (firstCurve) {
			return new Cubic(
					a1.getDx() / scaleX,
					a1.getDy() / scaleY,
					b1.getDx() / scaleX,
					b1.getDy() / scaleY
			).curve(scaledT) * scaleY;
		} else {
			return new Cubic(
					(a2.getDx() - midpoint.getDx()) / scaleX,
					(a2.getDy() - midpoint.getDy()) / scaleY,
					(b2.getDx() - midpoint.getDx()) / scaleX,
					(b2.getDy() - midpoint.getDy()) / scaleY
			).curve(scaledT) * scaleY + midpoint.getDy();
		}
	}
}
