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

/**
 * A sawtooth curve that repeats a given number of times over the unit interval.
 * <p>
 * The curve rises linearly from 0.0 to 1.0 and then falls discontinuously back
 * to 0.0 each iteration.
 * <p>
 * <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_sawtooth.mp4>SawTooth</a>
 */
public class SawTooth extends Curve {
	private final int count;

	public SawTooth(int count) {
		this.count = count;
	}

	@Override
	public double curve(double t) {
		t *= count;
		return t - (int) t;
	}
}
