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
 * An oscillating curve that grows and then shrinks in {@code magnitude} while overshooting its bounds.
 * <p></p>
 * <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_elastic_in_out.mp4>Elastic In Out Example</a>
 * <p>
 * The above example is built with a {@code magnitude} of {@code 0.4}.
 */
public class ElasticInOutCurve extends Curve {
	private final double period;

	public ElasticInOutCurve() {
		this(0.4);
	}

	public ElasticInOutCurve(double period) {
		this.period = period;
	}

	@Override
	public double curve(double t) {
		final double s = period / 4.0;
		t = 2.0 * t - 1.0;
		double sin = Math.sin((t - s) * (Math.PI * 2.0) / period);
		if (t < 0.0) {
			return -0.5 * Math.pow(2.0, 10.0 * t) * sin;
		} else {
			return Math.pow(2.0, -10.0 * t) * sin * 0.5 + 1.0;
		}
	}
}
