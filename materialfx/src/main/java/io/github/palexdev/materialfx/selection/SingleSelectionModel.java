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

import io.github.palexdev.materialfx.selection.base.AbstractSingleSelectionModel;
import io.github.palexdev.materialfx.selection.base.ISingleSelectionModel;
import io.github.palexdev.materialfx.utils.others.TriConsumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.function.Function;

/**
 * Implementation of {@link AbstractSingleSelectionModel} to implement the API
 * specified by {@link ISingleSelectionModel}.
 * <p></p>
 * The logic is handled by {@link SingleSelectionManager}, in fact all methods are just delegates.
 */
public class SingleSelectionModel<T> extends AbstractSingleSelectionModel<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public SingleSelectionModel(ObservableList<T> items) {
		super(items);
	}

	public SingleSelectionModel(ObjectProperty<ObservableList<T>> items) {
		super(items);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate method for {@link SingleSelectionManager#clearSelection()}.
	 */
	@Override
	public void clearSelection() {
		selectionManager.clearSelection();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#updateSelection(int)}.
	 */
	@Override
	public void selectIndex(int index) {
		selectionManager.updateSelection(index);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#updateSelection(Object)}.
	 */
	@Override
	public void selectItem(T item) {
		selectionManager.updateSelection(item);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#getSelectedIndex()}}.
	 */
	@Override
	public int getSelectedIndex() {
		return selectionManager.getSelectedIndex();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#selectedIndexProperty()}, but
	 * a read-only property is returned.
	 */
	@Override
	public ReadOnlyIntegerProperty selectedIndexProperty() {
		return selectionManager.selectedIndexProperty().getReadOnlyProperty();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#getSelectedItem()}.
	 */
	@Override
	public T getSelectedItem() {
		return selectionManager.getSelectedItem();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#selectedItemProperty()}, but
	 * a read-only property is returned.
	 */
	@Override
	public ReadOnlyObjectProperty<T> selectedItemProperty() {
		return selectionManager.selectedItemProperty().getReadOnlyProperty();
	}

	//================================================================================
	// Bindings
	//================================================================================

	/**
	 * Binds this selection model's index to the given selection model's index,
	 * calls {@link SingleSelectionManager#bindIndex(ObservableValue, Function)}.
	 * <p></p>
	 * Default implementation:
	 * <pre>
	 * {@code
	 *      selectionManager.bindIndex(selectionModel.selectionManager.selectedIndexProperty(), getItems()::get);
	 * }
	 * </pre>
	 */
	public void bindIndex(SingleSelectionModel<T> selectionModel) {
		selectionManager.bindIndex(selectionModel.selectionManager.selectedIndexProperty(), getItems()::get);
	}

	/**
	 * Binds this selection model's index bidirectionally to the given selection model's index,
	 * calls {@link SingleSelectionManager#bindIndexBidirectional(Property, Function, TriConsumer)}.
	 * <p></p>
	 * Default implementation:
	 * <pre>
	 * {@code
	 *         selectionManager.bindIndexBidirectional(
	 *                 selectionModel.selectionManager.selectedIndexProperty(),
	 *                 getItems()::get,
	 *                 (clearing, i, other) -> {
	 *                     selectionModel.selectionManager.setClearing(clearing);
	 *                     selectionModel.selectionManager.updateSelection(i);
	 *                 }
	 *         );
	 * }
	 * </pre>
	 */
	public void bindIndexBidirectional(SingleSelectionModel<T> selectionModel) {
		selectionManager.bindIndexBidirectional(
				selectionModel.selectionManager.selectedIndexProperty(),
				getItems()::get,
				(clearing, i, other) -> {
					selectionModel.selectionManager.setClearing(clearing);
					selectionModel.selectionManager.updateSelection(i);
				}
		);
	}

	/**
	 * Binds this selection model's item to the given selection model's item,
	 * calls {@link SingleSelectionManager#bindItem(ObservableValue, Function)}.
	 * <p></p>
	 * Default implementation:
	 * <pre>
	 * {@code
	 *         selectionManager.bindItem(selectionModel.selectionManager.selectedItemProperty(), getItems()::indexOf);
	 * }
	 * </pre>
	 */
	public void bindItem(SingleSelectionModel<T> selectionModel) {
		selectionManager.bindItem(selectionModel.selectionManager.selectedItemProperty(), getItems()::indexOf);
	}

	/**
	 * Binds this selection model's item bidirectionally to the given selection model's item,
	 * calls {@link SingleSelectionManager#bindItemBidirectional(Property, Function, TriConsumer)}.
	 * <p></p>
	 * Default implementation:
	 * <pre>
	 * {@code
	 *         selectionManager.bindItemBidirectional(
	 *                 selectionModel.selectionManager.selectedItemProperty(),
	 *                 getItems()::indexOf,
	 *                 (clearing, item, other) -> {
	 *                     selectionModel.selectionManager.setClearing(clearing);
	 *                     selectionModel.selectionManager.updateSelection(item);
	 *                 }
	 *         );
	 * }
	 * </pre>
	 */
	public void bindItemBidirectional(SingleSelectionModel<T> selectionModel) {
		selectionManager.bindItemBidirectional(
				selectionModel.selectionManager.selectedItemProperty(),
				getItems()::indexOf,
				(clearing, item, other) -> {
					selectionModel.selectionManager.setClearing(clearing);
					selectionModel.selectionManager.updateSelection(item);
				}
		);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#bindIndex(ObservableValue, Function)}.
	 */
	public void bindIndex(ObservableValue<? extends Number> source, Function<Integer, T> indexConverter) {
		selectionManager.bindIndex(source, indexConverter);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#bindIndexBidirectional(Property, Function, TriConsumer)}.
	 */
	public void bindIndexBidirectional(Property<Number> other, Function<Integer, T> indexConverter, TriConsumer<Boolean, Integer, Property<Number>> updateOther) {
		selectionManager.bindIndexBidirectional(other, indexConverter, updateOther);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#bindItem(ObservableValue, Function)}.
	 */
	public void bindItem(ObservableValue<? extends T> source, Function<T, Integer> itemConverter) {
		selectionManager.bindItem(source, itemConverter);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#bindItemBidirectional(Property, Function, TriConsumer)}.
	 */
	public void bindItemBidirectional(Property<T> other, Function<T, Integer> itemConverter, TriConsumer<Boolean, T, Property<T>> updateOther) {
		selectionManager.bindItemBidirectional(other, itemConverter, updateOther);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#unbind()}.
	 */
	public void unbind() {
		selectionManager.unbind();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#unbindIndexBidirectional(Property)}.
	 */
	public void unbindIndexBidirectional(Property<Number> other) {
		selectionManager.unbindIndexBidirectional(other);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#unbindItemBidirectional(Property)}.
	 */
	public void unbindItemBidirectional(Property<T> other) {
		selectionManager.unbindItemBidirectional(other);
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#unbindBidirectional()}.
	 */
	public void unbindBidirectional() {
		selectionManager.unbindBidirectional();
	}

	/**
	 * Delegate method for {@link SingleSelectionManager#isBound()}.
	 */
	public boolean isBound() {
		return selectionManager.isBound();
	}
}
