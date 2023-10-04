/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.settings;

public class StringSetting extends Setting<String> {

	//================================================================================
	// Constructors
	//================================================================================
	public StringSetting(String name, String description, String defaultValue, Settings container) {
		super(name, description, defaultValue, container);
	}

	public static StringSetting of(String name, String description, String defaultValue, Settings container) {
		return new StringSetting(name, description, defaultValue, container);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String get() {
		return container.prefs().get(name, defaultValue);
	}

	@Override
	public void set(String val) {
		container.prefs().put(name, val);
	}
}
