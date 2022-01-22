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
import javafx.beans.property.SimpleStringProperty;

/**
 * A {@link SimpleStringProperty} that implements {@link ResettableProperty}.
 */
public class ResettableStringProperty extends SimpleStringProperty implements ResettableProperty<String> {
	//================================================================================
	// Properties
	//================================================================================
	private String defaultValue;
	private boolean fireChangeOnReset = false;
	private boolean hasBeenReset = false;

	//================================================================================
	// Constructors
	//================================================================================
	public ResettableStringProperty() {
	}

	public ResettableStringProperty(String initialValue) {
		super(initialValue);
	}

	public ResettableStringProperty(String initialValue, String defaultValue) {
		super(initialValue);
		this.defaultValue = defaultValue;
	}

	public ResettableStringProperty(Object bean, String name) {
		super(bean, name);
	}

	public ResettableStringProperty(Object bean, String name, String initialValue) {
		super(bean, name, initialValue);
	}

	public ResettableStringProperty(Object bean, String name, String initialValue, String defaultValue) {
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
	public void set(String newValue) {
		hasBeenReset = newValue.equals(defaultValue);
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
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
