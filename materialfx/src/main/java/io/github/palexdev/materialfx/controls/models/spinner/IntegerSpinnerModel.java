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

package io.github.palexdev.materialfx.controls.models.spinner;

import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;

import java.util.Objects;

/**
 * Concrete implementation of {@link NumberSpinnerModel} to work with integer value.
 * <p></p>
 * The constructor initializes the model with these values:
 * <p> - The converter uses {@link Integer#parseInt(String)} and {@link Objects#toString(Object)}
 * <p> - The default value is 0
 * <p> - The min value is 0
 * <p> - The max value is {@link Integer#MAX_VALUE}
 * <p> - The increment is 1
 * <p> - The initial value depends on the chosen constructor
 */
public class IntegerSpinnerModel extends NumberSpinnerModel<Integer> {

	//================================================================================
	// Constructors
	//================================================================================
	public IntegerSpinnerModel() {
		this(0);
	}

	public IntegerSpinnerModel(int initialValue) {
		setConverter(FunctionalStringConverter.converter(
				Integer::parseInt,
				Objects::toString
		));
		setDefaultValue(0);
		setMin(0);
		setMax(Integer.MAX_VALUE);
		setIncrement(1);
		setValue(initialValue);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Increments the current value by {@link #incrementProperty()}.
	 * <p></p>
	 * If the new value is greater than the {@link #maxProperty()} and {@link #isWrapAround()} is true
	 * the new value will be the {@link #minProperty()}.
	 */
	@Override
	public void next() {
		int newVal = getValue() + getIncrement();
		if (newVal > getMax()) {
			newVal = isWrapAround() ? getMin() : getMax();
		}
		setValue(newVal);
	}

	/**
	 * Decrements the current value by {@link #incrementProperty()}.
	 * <p></p>
	 * If the new value is lesser than the {@link #minProperty()} and {@link #isWrapAround()} is true
	 * the new value will be the {@link #maxProperty()}.
	 */
	@Override
	public void previous() {
		int newVal = getValue() - getIncrement();
		if (newVal < getMin()) {
			newVal = isWrapAround() ? getMax() : getMin();
		}
		setValue(newVal);
	}
}
