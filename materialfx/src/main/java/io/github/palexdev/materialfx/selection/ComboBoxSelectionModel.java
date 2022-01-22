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

package io.github.palexdev.materialfx.selection;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/**
 * Extension of {@link SingleSelectionModel} to implement a few more methods for comboboxes.
 */
public class ComboBoxSelectionModel<T> extends SingleSelectionModel<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public ComboBoxSelectionModel(ObservableList<T> items) {
		super(items);
	}

	public ComboBoxSelectionModel(ObjectProperty<ObservableList<T>> items) {
		super(items);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Selects the first item of the combobox if the items list is not empty.
	 */
	public void selectFirst() {
		if (itemsEmpty()) return;
		selectIndex(0);
	}

	/**
	 * Selects the next item of the combobox if exists.
	 */
	public void selectNext() {
		int index = getSelectedIndex() + 1;
		if (index < itemsSize()) {
			selectIndex(index);
		}
	}

	/**
	 * Selects the previous item of the combobox if exists.
	 */
	public void selectPrevious() {
		int index = getSelectedIndex() - 1;
		if (index >= 0 && !itemsEmpty()) {
			selectIndex(index);
		}
	}

	/**
	 * Selects the last item of the combobox if the items list is not empty.
	 */
	public void selectLast() {
		if (itemsEmpty()) return;
		int index = itemsSize() - 1;
		selectIndex(index);
	}

	/**
	 * Convenience method to get the items list size.
	 */
	private int itemsSize() {
		return items.get().size();
	}

	/**
	 * Convenience method to check if the items list is empty.
	 */
	private boolean itemsEmpty() {
		return items.get().isEmpty();
	}
}
