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

import io.github.palexdev.materialfx.selection.base.AbstractMultipleSelectionModel;
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

/**
 * Implementation of {@link AbstractMultipleSelectionModel} to implement the API
 * specified by {@link IMultipleSelectionModel}.
 * <p></p>
 * The logic is handled by {@link MultipleSelectionManager}, in fact all methods are just delegates.
 */
@SuppressWarnings("unchecked")
public class MultipleSelectionModel<T> extends AbstractMultipleSelectionModel<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public MultipleSelectionModel(ObservableList<T> items) {
		super(items);
	}

	public MultipleSelectionModel(ObjectProperty<ObservableList<T>> items) {
		super(items);
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Delegate method for {@link MultipleSelectionManager#clearSelection()}.
	 */
	@Override
	public void clearSelection() {
		selectionManager.clearSelection();
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#deselectIndex(int)}.
	 */
	@Override
	public void deselectIndex(int index) {
		selectionManager.deselectIndex(index);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#deselectItem(Object)}.
	 */
	@Override
	public void deselectItem(T item) {
		selectionManager.deselectItem(item);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#deselectIndexes(int...)}.
	 */
	@Override
	public void deselectIndexes(int... indexes) {
		selectionManager.deselectIndexes(indexes);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#deselectItems(Object[])}.
	 */
	@Override
	public void deselectItems(T... items) {
		selectionManager.deselectItems(items);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#updateSelection(int)}.
	 */
	@Override
	public void selectIndex(int index) {
		selectionManager.updateSelection(index);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#updateSelection(Object)}.
	 */
	@Override
	public void selectItem(T item) {
		selectionManager.updateSelection(item);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#updateSelectionByIndexes(List)}.
	 */
	@Override
	public void selectIndexes(List<Integer> indexes) {
		selectionManager.updateSelectionByIndexes(indexes);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#updateSelectionByItems(List)}.
	 */
	@Override
	public void selectItems(List<T> items) {
		selectionManager.updateSelectionByItems(items);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#expandSelection(int)}.
	 */
	@Override
	public void expandSelection(int index) {
		selectionManager.expandSelection(index);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#replaceSelection(Integer...)}.
	 */
	@Override
	public void replaceSelection(Integer... indexes) {
		selectionManager.replaceSelection(indexes);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#replaceSelection(Object[])}.
	 */
	@Override
	public void replaceSelection(T... items) {
		selectionManager.replaceSelection(items);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#getSelection()}.
	 */
	@Override
	public ObservableMap<Integer, T> getSelection() {
		return selectionManager.getSelection();
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#selectionProperty()}.
	 */
	@Override
	public MapProperty<Integer, T> selectionProperty() {
		return selectionManager.selectionProperty();
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#setSelection(ObservableMap)}.
	 */
	@Override
	public void setSelection(ObservableMap<Integer, T> newSelection) {
		selectionManager.setSelection(newSelection);
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#getSelectedValues()}.
	 */
	@Override
	public List<T> getSelectedValues() {
		return selectionManager.getSelectedValues();
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#allowsMultipleSelection()}.
	 */
	@Override
	public boolean allowsMultipleSelection() {
		return selectionManager.allowsMultipleSelection();
	}

	/**
	 * Delegate method for {@link MultipleSelectionManager#setAllowsMultipleSelection(boolean)}.
	 */
	@Override
	public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
		selectionManager.setAllowsMultipleSelection(allowsMultipleSelection);
	}
}
