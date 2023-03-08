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

package io.github.palexdev.mfxeffects.animations.base;

import io.github.palexdev.mfxeffects.animations.motion.*;
import javafx.animation.Interpolator;

/**
 * Equivalent of JavaFX's {@link Interpolator} (in fact the class extends it), in {@code Flutter} they
 * are called {@code Curves}.
 * <p></p>
 * Aside from just a "rename", the protected method {@link Interpolator#curve(double)} has been made public
 * since some curves need to access other curves' algorithm; also adds a method to quickly create a {@link FlippedCurve}
 * from any implementation.
 * <p></p>
 *
 * @see Motion for a series of pre-defines curves
 * @see BounceInCurve
 * @see BounceOutCurve
 * @see BounceInOutCurve
 * @see Cubic
 * @see DecelerateCurve
 * @see ElasticInCurve
 * @see ElasticOutCurve
 * @see ElasticInOutCurve
 * @see FlippedCurve
 * @see Interval
 * @see Linear
 * @see SawTooth
 * @see ThreePointCubic
 * @see Threshold
 */
public abstract class Curve extends Interpolator {
	public abstract double curve(double t);

	/**
	 * Flips this curve's motion. This is done by creating a new {@link FlippedCurve}.
	 */
	public Curve flipped() {
		return new FlippedCurve(this);
	}
}
