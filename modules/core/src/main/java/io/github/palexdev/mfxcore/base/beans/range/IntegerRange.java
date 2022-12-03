/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.beans.range;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Implementation of {@link NumberRange} to represent an Integer range.
 */
public class IntegerRange extends NumberRange<Integer> implements Iterable<Integer> {

	//================================================================================
	// Constructors
	//================================================================================
	public IntegerRange(Integer min, Integer max) {
		super(min, max);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * @return a new instance of {@code IntegerRange} with the given min and max bounds.
	 */
	public static IntegerRange of(Integer min, Integer max) {
		return new IntegerRange(min, max);
	}

	/**
	 * @return a new instance of {@code IntegerRange} with the given val as both min and max bounds.
	 */
	public static IntegerRange of(Integer val) {
		return new IntegerRange(val, val);
	}

	/**
	 * Checks if the given value is contained in the given range (bounds are included).
	 */
	public static boolean inRangeOf(int val, IntegerRange range) {
		return Math.max(range.getMin(), val) == Math.min(val, range.getMax());
	}

	/**
	 * Expands a range of integers to a {@code List} with step=1.
	 */
	public static List<Integer> expandRange(IntegerRange range) {
		return IntStream.rangeClosed(range.getMin(), range.getMax()).collect(ArrayList::new, List::add, List::addAll);
	}

	/**
	 * Expands a range of integers to a {@code List} with the given step.
	 */
	public static List<Integer> expandRange(IntegerRange range, int step) {
		List<Integer> l = new ArrayList<>();
		int start = range.getMin();
		do {
			l.add(start);
			start += step;
		} while (start <= range.getMax());
		return l;
	}

	/**
	 * Expands a range of integers to a {@code Set} with step=1.
	 * <p>
	 * The {@code Set} is ordered.
	 */
	public static Set<Integer> expandRangeToSet(IntegerRange range) {
		return IntStream.rangeClosed(range.getMin(), range.getMax()).collect(LinkedHashSet::new, Set::add, Set::addAll);
	}

	/**
	 * Expands a range of integers to a {@code Set} with the given step.
	 * <p>
	 * The {@code Set} is ordered.
	 */
	public static Set<Integer> expandRangeToSet(IntegerRange range, int step) {
		Set<Integer> s = new LinkedHashSet<>();
		int start = range.getMin();
		do {
			s.add(start);
			start += step;
		} while (start <= range.getMax());
		return s;
	}

	/**
	 * Expands a range of integers to an array.
	 */
	public static Integer[] expandRangeToArray(int min, int max) {
		return expandRange(of(min, max)).toArray(Integer[]::new);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Integer sum() {
		return getMin() + getMax();
	}

	@Override
	public Integer diff() {
		return getMax() - getMin();
	}

	@Override
	public PrimitiveIterator.OfInt iterator() {
		return IntStream.rangeClosed(getMin(), getMax()).iterator();
	}
}
