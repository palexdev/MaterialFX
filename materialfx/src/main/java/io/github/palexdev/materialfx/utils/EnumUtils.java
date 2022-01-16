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

/**
 * Utility class which provides convenience methods for enumerators
 */
public class EnumUtils {

	private EnumUtils() {
	}

	/**
	 * Checks if the given enumerator (as a class) contains the given String,
	 * same as {@link Enum#valueOf(Class, String)} but case-insensitive.
	 *
	 * @param clazz the Class object of the enum class from which to return a constant
	 * @param name  the name of the constant to return
	 * @return the enum constant of the specified enum class with the specified name
	 */
	public static <E extends Enum<E>> E valueOfIgnoreCase(Class<E> clazz, String name) {
		E enumeration = null;
		for (E e : clazz.getEnumConstants()) {
			if (e.name().equalsIgnoreCase(name)) {
				enumeration = e;
			}
		}

		if (enumeration == null) {
			throw new IllegalArgumentException("No enum constant " + clazz.getCanonicalName() + "." + name);
		}

		return enumeration;
	}

	/**
	 * Given an enumerator (as a class) retrieves a random constant
	 * using {@link Class#getEnumConstants()}.
	 */
	public static <E extends Enum<E>> E randomEnum(Class<E> clazz) {
		return RandomUtils.randFromArray(clazz.getEnumConstants());
	}
}
