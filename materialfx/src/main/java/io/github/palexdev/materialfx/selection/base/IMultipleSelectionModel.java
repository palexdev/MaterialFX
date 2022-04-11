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

import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Public API that every MultipleSelectionModel must implement.
 */
@SuppressWarnings("unchecked")
public interface IMultipleSelectionModel<T> {

	/**
	 * Clears the selection.
	 */
	void clearSelection();

	/**
	 * Deselects the given index.
	 */
	void deselectIndex(int index);

	/**
	 * Deselects the given item.
	 */
	void deselectItem(T item);

	/**
	 * Deselects the given indexes.
	 */
	void deselectIndexes(int... indexes);

	/**
	 * Deselects the given items.
	 */
	void deselectItems(T... items);

	/**
	 * Selects the given index.
	 */
	void selectIndex(int index);

	/**
	 * Selects the given item.
	 */
	void selectItem(T item);

	/**
	 * Selects the given indexes list.
	 */
	void selectIndexes(List<Integer> indexes);

	/**
	 * Selects the given items list.
	 */
	void selectItems(List<T> items);

	/**
	 * Expands the selection in the given index direction.
	 */
	void expandSelection(int index);

	/**
	 * Clears the selection and replaces it with the given indexes.
	 */
	void replaceSelection(Integer... indexes);

	/**
	 * Clears the selection and replaces it with the given items.
	 */
	void replaceSelection(T... items);

	/**
	 * @return the selection {@link ObservableMap}
	 */
	ObservableMap<Integer, T> getSelection();

	/**
	 * The {@link MapProperty} used to keep track of multiple selection.
	 * <p></p>
	 * We use a {@link MapProperty} to represent multiple selection because this way
	 * we can always update it "atomically", meaning that when the selected indexes changes
	 * the selected items are updated as well (also true viceversa).
	 */
	MapProperty<Integer, T> selectionProperty();

	/**
	 * Replaces the selection with the given {@link ObservableMap}.
	 */
	void setSelection(ObservableMap<Integer, T> newSelection);

	/**
	 * Returns an unmodifiable {@link List} containing all the selected values extracted from
	 * {@link Map#values()}.
	 * The values order is kept since the selection is backed by a {@link LinkedHashMap}.
	 */
	List<T> getSelectedValues();

	/**
	 * Specifies if this model allows multiple selection or should act like
	 * a SingleSelectionModel.
	 */
	boolean allowsMultipleSelection();

	/**
	 * Sets the selection behavior of this model to be multiple (true) or
	 * single (false).
	 */
	void setAllowsMultipleSelection(boolean allowsMultipleSelection);
}
