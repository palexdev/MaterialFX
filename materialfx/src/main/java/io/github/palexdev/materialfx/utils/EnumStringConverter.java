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

package io.github.palexdev.materialfx.utils;

import javafx.util.StringConverter;

/**
 * Implementation of {@link StringConverter} to work with a generic {@link Enum}.
 * <p></p>
 * For this to work, it's necessary to specify the enumerator class, see {@link Enum#valueOf(Class, String)}.
 */
public class EnumStringConverter<E extends Enum<E>> extends StringConverter<E> {
	//================================================================================
	// Properties
	//================================================================================
	private final Class<E> type;

	//================================================================================
	// Constructors
	//================================================================================
	public EnumStringConverter(Class<E> type) {
		this.type = type;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Calls toString() on the given enumeration.
	 */
	@Override
	public String toString(E e) {
		return e.toString();
	}

	/**
	 * Uses {@link EnumUtils#valueOfIgnoreCase(Class, String)} to convert the given String to an enumeration.
	 */
	@Override
	public E fromString(String string) {
		return EnumUtils.valueOfIgnoreCase(type, string);
	}
}
