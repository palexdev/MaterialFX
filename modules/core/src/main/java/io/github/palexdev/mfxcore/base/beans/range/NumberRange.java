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

import java.util.Objects;

/**
 * Base class to represent a range of numbers between a min and a max.
 *
 * @param <N> The type of Number to represent (must also be a {@link Comparable})
 */
public abstract class NumberRange<N extends Number & Comparable<N>> {
	//================================================================================
	// Properties
	//================================================================================
	private final N min;
	private final N max;

	//================================================================================
	// Constructors
	//================================================================================
	public NumberRange(N min, N max) {
		if (min == null || max == null || min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Invalid range for values: Min[" + min + "], Max[" + max + "]");
		}

		this.min = min;
		this.max = max;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * @return the sum of {@link #getMin()} and {@link #getMax()}
	 */
	public abstract N sum();

	/**
	 * @return the difference between {@link #getMax()} and {@link #getMin()}
	 */
	public abstract N diff();

	//================================================================================
	// Getters
	//================================================================================

	/**
	 * @return the lower bound
	 */
	public N getMin() {
		return min;
	}

	/**
	 * @return the upper bound
	 */
	public N getMax() {
		return max;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NumberRange<?> that = (NumberRange<?>) o;
		return getMin().equals(that.getMin()) && getMax().equals(that.getMax());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getMin(), getMax());
	}

	@Override
	public String toString() {
		return "Min[" + getMin() + "], Max[" + getMax() + "]";
	}
}
