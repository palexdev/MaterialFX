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

import io.github.palexdev.materialfx.controls.base.AbstractMFXToggleNode;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;

public class BaseToggleNodeBuilder<T extends AbstractMFXToggleNode> extends ButtonBaseBuilder<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public BaseToggleNodeBuilder(T toggleNode) {
		super(toggleNode);
	}

	public static BaseToggleNodeBuilder<AbstractMFXToggleNode> toggleNode(AbstractMFXToggleNode toggleNode) {
		return new BaseToggleNodeBuilder<>(toggleNode);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public BaseToggleNodeBuilder<T> setLabelLeadingIcon(Node labelLeadingIcon) {
		node.setLabelLeadingIcon(labelLeadingIcon);
		return this;
	}

	public BaseToggleNodeBuilder<T> setLabelTrailingIcon(Node labelTrailingIcon) {
		node.setLabelTrailingIcon(labelTrailingIcon);
		return this;
	}

	public BaseToggleNodeBuilder<T> setSelected(boolean value) {
		node.setSelected(value);
		return this;
	}

	public BaseToggleNodeBuilder<T> setToggleGroup(ToggleGroup value) {
		node.setToggleGroup(value);
		return this;
	}
}
