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

package io.github.palexdev.materialfx.selection.base;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Public API that every SingleSelectionModel must implement.
 */
public interface ISingleSelectionModel<T> {

	/**
	 * Clears the selection.
	 */
	void clearSelection();

	/**
	 * Selects the given index.
	 */
	void selectIndex(int index);

	/**
	 * Selects the given item.
	 */
	void selectItem(T item);

	/**
	 * @return the current selected index
	 */
	int getSelectedIndex();

	/**
	 * The selected index property as a read-only property.
	 * Selection should always be updated with the dedicated methods.
	 */
	ReadOnlyIntegerProperty selectedIndexProperty();

	/**
	 * @return the current selected item
	 */
	T getSelectedItem();

	/**
	 * The selected item property as a read-only property.
	 * Selection should always be updated with the dedicated methods.
	 */
	ReadOnlyObjectProperty<T> selectedItemProperty();

}
