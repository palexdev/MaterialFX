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
import io.github.palexdev.mfxeffects.utils.NumberUtils;

/**
 * A curve that is 0.0 until {@code begin}, then curved (according to given {@code Curve}) from
 * 0.0 at {@code begin} to 1.0 at {@code end}, then remains 1.0 past {@code end}.
 * <p>
 * An {@code Interval} can be used to delay an animation. For example, a six-second
 * animation that uses an {@code Interval} with its {@code begin} set to 0.5 and its {@code end}
 * set to 1.0 will essentially become a three-second animation that starts
 * three seconds later.
 * <p>
 * <a href=https://flutter.github.io/assets-for-api-docs/assets/animation/curve_interval.mp4>Interval</a>
 */
public class Interval extends Curve {
	private final double begin;
	private final double end;
	private final Curve curve;

	public Interval(double begin, double end) {
		this.begin = begin;
		this.end = end;
		this.curve = Motion.LINEAR;
	}

	@Override
	public double curve(double t) {
		assert (begin >= 0.0);
		assert (begin <= 1.0);
		assert (end >= 0.0);
		assert (end <= 1.0);
		assert (end >= begin);
		t = NumberUtils.clamp((t - begin) / (end - begin), 0.0, 1.0);
		if (t == 0.0 || t == 1.0) {
			return t;
		}
		return curve.curve(t);
	}

}
