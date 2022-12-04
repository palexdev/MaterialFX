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

package io.github.palexdev.mfxcore.base.beans.range;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link NumberRange} to represent a Float range.
 */
public class FloatRange extends NumberRange<Float> {

	//================================================================================
	// Constructors
	//================================================================================
	public FloatRange(Float min, Float max) {
		super(min, max);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * @return a new instance of {@code FloatRange} with the given min and max bounds.
	 */
	public static FloatRange of(Float min, Float max) {
		return new FloatRange(min, max);
	}

	/**
	 * @return a new instance of {@code FloatRange} with the given val as both min and max bounds.
	 */
	public static FloatRange of(Float val) {
		return new FloatRange(val, val);
	}

	/**
	 * Checks if the given value is contained in the given range (bounds are included).
	 */
	public static boolean inRangeOf(float val, FloatRange range) {
		return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
	}

	/**
	 * Expands a range of floats to a {@code List} with the given step.
	 */
	public static List<Float> expandRange(FloatRange range, float step) {
		List<Float> l = new ArrayList<>();
		float start = range.getMin();
		do {
			l.add(start);
			start += step;
		} while (start <= range.getMax());
		return l;
	}

	/**
	 * Expands a range of floats to a {@code Set} with the given step.
	 * <p>
	 * The {@code Set} is ordered.
	 */
	public static Set<Float> expandRangeToSet(FloatRange range, float step) {
		Set<Float> s = new LinkedHashSet<>();
		float start = range.getMin();
		do {
			s.add(start);
			start += step;
		} while (start <= range.getMax());
		return s;
	}

	/**
	 * Expands a range of floats to an array.
	 */
	public static Float[] expandRangeToArray(float min, float max, float step) {
		return expandRange(of(min, max), step).toArray(Float[]::new);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Float sum() {
		return getMin() + getMax();
	}

	@Override
	public Float diff() {
		return getMax() - getMin();
	}
}
