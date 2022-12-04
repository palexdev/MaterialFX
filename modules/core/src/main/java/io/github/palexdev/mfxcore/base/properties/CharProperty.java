/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.properties;

import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Simple extension of {@link ReadOnlyObjectWrapper} for {@link Character}s.
 */
public class CharProperty extends ReadOnlyObjectWrapper<Character> {

	//================================================================================
	// Constructors
	//================================================================================
	public CharProperty() {
	}

	public CharProperty(Character initialValue) {
		super(initialValue);
	}

	public CharProperty(Object bean, String name) {
		super(bean, name);
	}

	public CharProperty(Object bean, String name, Character initialValue) {
		super(bean, name, initialValue);
	}
}
