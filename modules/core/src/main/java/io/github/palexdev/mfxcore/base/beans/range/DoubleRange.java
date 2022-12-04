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
 * Implementation of {@link NumberRange} to represent a Double range.
 */
public class DoubleRange extends NumberRange<Double> {

	//================================================================================
	// Constructors
	//================================================================================
	public DoubleRange(Double min, Double max) {
		super(min, max);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * @return a new instance of {@code DoubleRange} with the given min and max bounds.
	 */
	public static DoubleRange of(Double min, Double max) {
		return new DoubleRange(min, max);
	}

	/**
	 * @return a new instance of {@code DoubleRange} with the given val as both min and max bounds.
	 */
	public static DoubleRange of(Double val) {
		return new DoubleRange(val, val);
	}

	/**
	 * Checks if the given value is contained in the given range (bounds are included).
	 */
	public static boolean inRangeOf(double val, DoubleRange range) {
		return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
	}

	/**
	 * Expands a range of doubles to a {@code List} with the given step.
	 */
	public static List<Double> expandRange(DoubleRange range, double step) {
		List<Double> l = new ArrayList<>();
		double start = range.getMin();
		do {
			l.add(start);
			start += step;
		} while (start <= range.getMax());
		return l;
	}

	/**
	 * Expands a range of doubles to a {@code Set} with the given step.
	 * <p>
	 * The {@code Set} is ordered.
	 */
	public static Set<Double> expandRangeToSet(DoubleRange range, double step) {
		Set<Double> s = new LinkedHashSet<>();
		double start = range.getMin();
		do {
			s.add(start);
			start += step;
		} while (start <= range.getMax());
		return s;
	}

	/**
	 * Expands a range of double to an array.
	 */
	public static Double[] expandRangeToArray(double min, double max, double step) {
		return expandRange(of(min, max), step).toArray(Double[]::new);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Double sum() {
		return getMin() + getMax();
	}

	@Override
	public Double diff() {
		return getMax() - getMin();
	}
}
