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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Base implementation for {@link SpinnerModel}.
 * <p>
 * This is still not enough to use with a spinner but it's a good base to implement
 * new models.
 * Defines all the specification from the {@link SpinnerModel} interface and adds a
 * new property, {@link #defaultValueProperty()}, which is the spinner's value when calling {@link #reset()}.
 */
public abstract class AbstractSpinnerModel<T> implements SpinnerModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final ObjectProperty<T> value = new SimpleObjectProperty<>();
	protected final ObjectProperty<T> defaultValue = new SimpleObjectProperty<>();
	protected boolean wrapAround;

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Sets the spinner's value to the value specified by {@link #defaultValueProperty()}.
	 */
	@Override
	public void reset() {
		setValue(getDefaultValue());
	}

	@Override
	public T getValue() {
		return value.get();
	}

	@Override
	public ObjectProperty<T> valueProperty() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value.set(value);
	}

	@Override
	public boolean isWrapAround() {
		return wrapAround;
	}

	@Override
	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public T getDefaultValue() {
		return defaultValue.get();
	}

	/**
	 * Specifies the default value of the spinner.
	 * <p></p>
	 * Note that this may not be the spinner's initial value, and usually this
	 * is used when calling the {@link #reset()} method (depends on the implementation).
	 */
	public ObjectProperty<T> defaultValueProperty() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue.set(defaultValue);
	}
}

