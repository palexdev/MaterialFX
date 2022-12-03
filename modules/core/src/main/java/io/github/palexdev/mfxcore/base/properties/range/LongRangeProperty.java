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

import io.github.palexdev.mfxcore.base.beans.range.LongRange;
import io.github.palexdev.mfxcore.base.beans.range.NumberRange;
import io.github.palexdev.mfxcore.base.properties.base.NumberRangeProperty;

/**
 * Implementation of {@link NumberRangeProperty} for {@code Long} ranges.
 */
public class LongRangeProperty extends NumberRangeProperty<Long> {

	//================================================================================
	// Constructors
	//================================================================================
	public LongRangeProperty() {
	}

	public LongRangeProperty(NumberRange<Long> initialValue) {
		super(initialValue);
	}

	public LongRangeProperty(Object bean, String name) {
		super(bean, name);
	}

	public LongRangeProperty(Object bean, String name, NumberRange<Long> initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void setRange(Long value) {
		set(LongRange.of(value));
	}

	@Override
	public void setRange(Long min, Long max) {
		set(LongRange.of(min, max));
	}
}
