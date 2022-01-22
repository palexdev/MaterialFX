/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.NumberRange;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link NumberRange}.
 *
 * @param <T> the range's number type
 */
public class NumberRangeProperty<T extends Number> extends SimpleObjectProperty<NumberRange<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public NumberRangeProperty() {
	}

	public NumberRangeProperty(NumberRange<T> initialValue) {
		super(initialValue);
	}

	public NumberRangeProperty(Object bean, String name) {
		super(bean, name);
	}

	public NumberRangeProperty(Object bean, String name, NumberRange<T> initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Convenience method to get the range's lower bound.
	 * Null if the range is null.
	 */
	public T getMin() {
		return get() == null ? null : get().getMin();
	}

	/**
	 * Convenience method to get the range's upper bound.
	 * Null if the range is null.
	 */
	public T getMax() {
		return get() == null ? null : get().getMin();
	}

	/**
	 * Convenience method to set a range with both min and max equal.
	 */
	public void setRange(T value) {
		set(NumberRange.of(value));
	}

	/**
	 * Convenience method to set a range with the given min and max values.
	 */
	public void setRange(T min, T max) {
		set(NumberRange.of(min, max));
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Overridden to check equality between ranges and return in case ranges are the same.
	 */
	@Override
	public void set(NumberRange<T> newValue) {
		NumberRange<T> oldValue = get();
		if (newValue.equals(oldValue)) return;
		super.set(newValue);
	}
}
