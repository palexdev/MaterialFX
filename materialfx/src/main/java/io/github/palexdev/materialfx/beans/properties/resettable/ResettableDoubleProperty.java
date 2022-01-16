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

package io.github.palexdev.materialfx.beans.properties.resettable;

import io.github.palexdev.materialfx.beans.properties.base.ResettableProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A {@link SimpleDoubleProperty} that implements {@link ResettableProperty}.
 */
public class ResettableDoubleProperty extends SimpleDoubleProperty implements ResettableProperty<Number> {
	//================================================================================
	// Properties
	//================================================================================
	private double defaultValue;
	private boolean fireChangeOnReset = false;
	private boolean hasBeenReset = false;

	//================================================================================
	// Constructors
	//================================================================================
	public ResettableDoubleProperty() {
	}

	public ResettableDoubleProperty(double initialValue) {
		super(initialValue);
	}

	public ResettableDoubleProperty(double initialValue, double defaultValue) {
		super(initialValue);
		this.defaultValue = defaultValue;
	}

	public ResettableDoubleProperty(Object bean, String name) {
		super(bean, name);
	}

	public ResettableDoubleProperty(Object bean, String name, double initialValue) {
		super(bean, name, initialValue);
	}

	public ResettableDoubleProperty(Object bean, String name, double initialValue, Double defaultValue) {
		super(bean, name, initialValue);
		this.defaultValue = defaultValue;
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public boolean isFireChangeOnReset() {
		return fireChangeOnReset;
	}

	@Override
	public void setFireChangeOnReset(boolean fireChangeOnReset) {
		this.fireChangeOnReset = fireChangeOnReset;
	}

	@Override
	public void set(double newValue) {
		hasBeenReset = newValue == defaultValue;
		super.set(newValue);
	}

	@Override
	protected void fireValueChangedEvent() {
		if (getValue() == defaultValue && !fireChangeOnReset) {
			return;
		}

		super.fireValueChangedEvent();
	}

	@Override
	public boolean hasBeenReset() {
		return hasBeenReset;
	}

	@Override
	public Double getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Number defaultValue) {
		this.defaultValue = defaultValue.doubleValue();
	}
}
