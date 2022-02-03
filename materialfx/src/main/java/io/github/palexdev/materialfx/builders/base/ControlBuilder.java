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

package io.github.palexdev.materialfx.builders.base;

import io.github.palexdev.materialfx.builders.layout.RegionBuilder;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;

public class ControlBuilder<C extends Control> extends RegionBuilder<C> {

	//================================================================================
	// Constructors
	//================================================================================
	public ControlBuilder(C control) {
		super(control);
	}

	public static ControlBuilder<Control> control(Control control) {
		return new ControlBuilder<>(control);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ControlBuilder<C> setSkin(Skin<?> value) {
		node.setSkin(value);
		return this;
	}

	public ControlBuilder<C> setTooltip(Tooltip value) {
		node.setTooltip(value);
		return this;
	}

	public ControlBuilder<C> setContextMenu(ContextMenu value) {
		node.setContextMenu(value);
		return this;
	}
}
