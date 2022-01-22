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

package io.github.palexdev.materialfx.beans.properties.base;

import javafx.beans.property.Property;

/**
 * Base interface for all resettable properties.
 *
 * @param <T>
 */
public interface ResettableProperty<T> extends Property<T> {

	/**
	 * Sets the property's value to the default value.
	 */
	default void reset() {
		setValue(getDefaultValue());
	}

	boolean isFireChangeOnReset();

	/**
	 * Specifies if the property should fire a change event when it is reset or not.
	 */
	void setFireChangeOnReset(boolean fireChangeOnReset);

	/**
	 * Specifies if the property has been reset.
	 */
	boolean hasBeenReset();

	/**
	 * @return the property's default value
	 */
	T getDefaultValue();

	/**
	 * Sets the property's default value to the given value.
	 */
	void setDefaultValue(T defaultValue);
}
