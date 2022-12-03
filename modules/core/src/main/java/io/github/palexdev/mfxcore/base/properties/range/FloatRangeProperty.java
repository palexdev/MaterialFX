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

import io.github.palexdev.mfxcore.base.beans.range.FloatRange;
import io.github.palexdev.mfxcore.base.beans.range.NumberRange;
import io.github.palexdev.mfxcore.base.properties.base.NumberRangeProperty;

/**
 * Implementation of {@link NumberRangeProperty} for {@code Float} ranges.
 */
public class FloatRangeProperty extends NumberRangeProperty<Float> {

	//================================================================================
	// Constructors
	//================================================================================
	public FloatRangeProperty() {
	}

	public FloatRangeProperty(NumberRange<Float> initialValue) {
		super(initialValue);
	}

	public FloatRangeProperty(Object bean, String name) {
		super(bean, name);
	}

	public FloatRangeProperty(Object bean, String name, NumberRange<Float> initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void setRange(Float value) {
		set(FloatRange.of(value));
	}

	@Override
	public void setRange(Float min, Float max) {
		set(FloatRange.of(min, max));
	}
}
