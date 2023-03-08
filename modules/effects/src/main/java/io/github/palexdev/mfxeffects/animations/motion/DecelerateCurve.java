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
 * A curve where the rate of change starts out quickly and then decelerates; an
 * upside-down {@code `f(t) = t²`} parabola.
 * <p></p>
 * <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_decelerate.mp4>Decelerate</a>
 */
public class DecelerateCurve extends Curve {
	@Override
	public double curve(double t) {
		t = 1.0 - t;
		return 1.0 - t * t;
	}
}
