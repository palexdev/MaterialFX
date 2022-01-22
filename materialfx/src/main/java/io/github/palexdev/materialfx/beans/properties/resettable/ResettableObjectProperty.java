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
import javafx.beans.property.SimpleObjectProperty;

/**
 * A {@link SimpleObjectProperty} that implements {@link ResettableProperty}.
 */
public class ResettableObjectProperty<T> extends SimpleObjectProperty<T> implements ResettableProperty<T> {
	//================================================================================
	// Properties
	//================================================================================
	private T defaultValue;
	private boolean fireChangeOnReset = false;
	private boolean hasBeenReset = false;

	//================================================================================
	// Constructors
	//================================================================================
	public ResettableObjectProperty() {
	}

	public ResettableObjectProperty(T initialValue) {
		super(initialValue);
	}

	public ResettableObjectProperty(T initialValue, T defaultValue) {
		super(initialValue);
		this.defaultValue = defaultValue;
	}

	public ResettableObjectProperty(Object bean, String name) {
		super(bean, name);
	}

	public ResettableObjectProperty(Object bean, String name, T initialValue) {
		super(bean, name, initialValue);
	}

	public ResettableObjectProperty(Object bean, String name, T initialValue, T defaultValue) {
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
	public void set(T newValue) {
		hasBeenReset = newValue == defaultValue;
		super.set(newValue);
	}

	@Override
	protected void fireValueChangedEvent() {
		if (getValue().equals(defaultValue) && !fireChangeOnReset) {
			return;
		}

		super.fireValueChangedEvent();
	}

	@Override
	public boolean hasBeenReset() {
		return hasBeenReset;
	}

	@Override
	public T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}
}
