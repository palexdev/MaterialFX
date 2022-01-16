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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXContextMenu;

/**
 * Every control offering a {@link MFXContextMenu} by default should
 * implement this interface.
 */
public interface MFXMenuControl {

	/**
	 * @return the context menu of the control
	 */
	MFXContextMenu getMFXContextMenu();

	/**
	 * @see MFXContextMenu#isDisabled()
	 */
	default boolean isContextMenuDisabled() {
		return getMFXContextMenu() != null && getMFXContextMenu().isDisabled();
	}

	/**
	 * @see MFXContextMenu#setDisabled(boolean)
	 */
	default void setContextMenuDisabled(boolean disabled) {
		if (getMFXContextMenu() != null) {
			getMFXContextMenu().setDisabled(disabled);
		}
	}
}
