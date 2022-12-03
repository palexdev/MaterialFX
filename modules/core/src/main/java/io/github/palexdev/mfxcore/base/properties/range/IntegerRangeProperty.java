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

package io.github.palexdev.mfxcore.base.properties.range;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.base.beans.range.NumberRange;
import io.github.palexdev.mfxcore.base.properties.base.NumberRangeProperty;

/**
 * Implementation of {@link NumberRangeProperty} for {@code Integer} ranges.
 */
public class IntegerRangeProperty extends NumberRangeProperty<Integer> {

	//================================================================================
	// Constructors
	//================================================================================
	public IntegerRangeProperty() {
	}

	public IntegerRangeProperty(NumberRange<Integer> initialValue) {
		super(initialValue);
	}

	public IntegerRangeProperty(Object bean, String name) {
		super(bean, name);
	}

	public IntegerRangeProperty(Object bean, String name, NumberRange<Integer> initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void setRange(Integer value) {
		set(IntegerRange.of(value));
	}

	@Override
	public void setRange(Integer min, Integer max) {
		set(IntegerRange.of(min, max));
	}
}
