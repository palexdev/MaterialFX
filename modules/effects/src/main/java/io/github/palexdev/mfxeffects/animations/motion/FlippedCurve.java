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
 * This {@link Curve} is capable of flipping the motion of other curves:
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_bounce_in.mp4>BounceIn</a>
 * <p> - <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_flipped.mp4>Flipped BounceIn</a>
 */
public class FlippedCurve extends Curve {
	private final Curve curve;

	public FlippedCurve(Curve curve) {
		this.curve = curve;
	}

	@Override
	public double curve(double t) {
		return 1.0 - curve.curve(1.0 - t);
	}
}
