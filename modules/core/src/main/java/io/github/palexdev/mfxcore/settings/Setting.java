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

public abstract class Setting<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final String name;
	protected final String description;
	protected final T defaultValue;
	protected final Settings container;
	protected boolean avoidEmpty = false;

	//================================================================================
	// Constructors
	//================================================================================
	protected Setting(String name, String description, T defaultValue, Settings container) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.container = container;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================
	public abstract T get();

	public abstract void set(T val);

	//================================================================================
	// Methods
	//================================================================================
	public void reset() {
		set(defaultValue);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String name() {
		return name;
	}

	public String description() {
		return description;
	}

	public T defValue() {
		return defaultValue;
	}

	public Settings container() {
		return container;
	}

	public boolean isAvoidEmpty() {
		return avoidEmpty;
	}

	public Setting<T> setAvoidEmpty(boolean avoidEmpty) {
		this.avoidEmpty = avoidEmpty;
		return this;
	}
}
