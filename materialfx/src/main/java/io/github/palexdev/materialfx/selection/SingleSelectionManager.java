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

import io.github.palexdev.materialfx.beans.properties.base.SynchronizedProperty;
import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedIntegerProperty;
import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedObjectProperty;
import io.github.palexdev.materialfx.bindings.BiBindingManager;
import io.github.palexdev.materialfx.bindings.BindingManager;
import io.github.palexdev.materialfx.selection.base.AbstractSingleSelectionModel;
import io.github.palexdev.materialfx.utils.others.TriConsumer;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * Helper class for {@link AbstractSingleSelectionModel} models to properly handle the selection
 * and the bindings with properties or other models.
 * <p></p>
 * Both the selectedIndex and selectedItem properties are SynchronizedProperties, see {@link SynchronizedProperty}.
 * <p>
 * So when you select an index the item will be automatically updated and only then a change event will be fired,
 * the same thing happens if you select an item.
 * <p></p>
 * Invalid values, like -1 for the index or null for the item, will throw an exception. To clear the selection
 * use {@link #clearSelection()}, a boolean flag will be set to true thus allowing setting the aforementioned values.
 */
public class SingleSelectionManager<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final AbstractSingleSelectionModel<T> selectionModel;
	private final SynchronizedIntegerProperty selectedIndex = new SynchronizedIntegerProperty(-1);
	private final SynchronizedObjectProperty<T> selectedItem = new SynchronizedObjectProperty<>(null);
	private boolean clearing;

	//================================================================================
	// Constructors
	//================================================================================
	public SingleSelectionManager(AbstractSingleSelectionModel<T> selectionModel) {
		this.selectionModel = selectionModel;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the index to -1 and item to null by using {@link SynchronizedProperty#setAndWait(Object, ObservableValue)}.
	 *
	 * @throws IllegalStateException if the selection model is bound, {@link #isBound()}
	 */
	public void clearSelection() {
		if (isBound()) {
			throw new IllegalStateException("Cannot clear the selection as this selection model is bound to some other property");
		}

		clearing = true;
		selectedIndex.setAndWait(-1, selectedItem);
		selectedItem.set(null);
		selectedIndex.awake();
		clearing = false;
	}

	/**
	 * Updates the selection with the given index (and the retrieved item) by using
	 * {@link SynchronizedProperty#setAndWait(Object, ObservableValue)}.
	 *
	 * @throws IllegalStateException if the selection model is bound, {@link #isBound()}
	 */
	public void updateSelection(int index) {
		if (isBound()) {
			throw new IllegalStateException("Cannot set the selected index as this selection model is bound to some other property");
		}

		if (clearing) {
			clearSelection();
			return;
		}

		T item = selectionModel.getUnmodifiableItems().get(index);
		selectedIndex.setAndWait(index, selectedItem);
		selectedItem.set(item);
		if (selectedIndex.isWaiting()) selectedIndex.awake();
	}

	/**
	 * Updates the selection with the given item (and the retrieved index) by using
	 * {@link SynchronizedProperty#setAndWait(Object, ObservableValue)}.
	 *
	 * @throws IllegalStateException if the selection model is bound, {@link #isBound()}
	 */
	public void updateSelection(T item) {
		if (isBound()) {
			throw new IllegalStateException("Cannot set the selected item as this selection model is bound to some other property");
		}

		if (clearing) {
			clearSelection();
			return;
		}

		int index = selectionModel.getUnmodifiableItems().indexOf(item);
		if (index == -1) {
			throw new IllegalArgumentException("The given item is not present is this selection model's list");
		}
		selectedItem.setAndWait(item, selectedIndex);
		selectedIndex.set(index);
		if (selectedItem.isWaiting()) selectedItem.awake();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the current selected index
	 */
	public int getSelectedIndex() {
		return selectedIndex.get();
	}

	/**
	 * The selected index property.
	 */
	public SynchronizedIntegerProperty selectedIndexProperty() {
		return selectedIndex;
	}

	/**
	 * @return the current selected item
	 */
	public T getSelectedItem() {
		return selectedItem.get();
	}

	/**
	 * The selected item property.
	 */
	public SynchronizedObjectProperty<T> selectedItemProperty() {
		return selectedItem;
	}

	/**
	 * Flag to specify that updateSelection should be ignored as {@link #clearSelection()} was invoked.
	 */
	public void setClearing(boolean clearing) {
		this.clearing = clearing;
	}

	//================================================================================
	// Bindings
	//================================================================================

	/**
	 * Binds the index property to given source {@link ObservableValue}.
	 * The indexConverter function is used to convert the index values to an item
	 * of the selection model.
	 * <p></p>
	 * By default creates this binding:
	 * <pre>
	 * {@code
	 *      BindingManager.instance().bind(selectedIndex)
	 *          .with((oldValue, newValue) -> {
	 *              T item = indexConverter.apply(newValue.intValue());
	 *              selectedIndex.setAndWait(newValue.intValue(), selectedItem);
	 *              selectedItem.set(item);
	 *          })
	 *          .to(source)
	 *          .create();
	 * }
	 * </pre>
	 * To change it you should override the {@link SingleSelectionModel} method.
	 */
	public void bindIndex(ObservableValue<? extends Number> source, Function<Integer, T> indexConverter) {
		if (selectedIndex.isBound()) selectedIndex.unbind();
		BindingManager.instance().bind(selectedIndex)
				.with((oldValue, newValue) -> {
					T item = indexConverter.apply(newValue.intValue());
					selectedIndex.setAndWait(newValue.intValue(), selectedItem);
					selectedItem.set(item);
				})
				.to(source)
				.create();
	}

	/**
	 * Binds the index property bidirectionally to given other {@link Property}.
	 * The indexConverter function is used to convert the index from the other property
	 * to an item of the selection model.
	 * <p>
	 * The updateOther {@link TriConsumer} is used to customize the way the other
	 * property is updated, the first parameter is the clearing flag of the selection manager,
	 * the second parameter is the new index, the third parameter is the other property reference.
	 * <p></p>
	 * By default creates this binding:
	 * <pre>
	 * {@code
	 *      BiBindingManager.instance().bindBidirectional(selectedIndex)
	 *          .with((oldValue, newValue) -> {
	 *              if (newValue.intValue() == -1) {
	 *                  clearSelection();
	 *                  return;
	 *              }
	 *
	 *              if (newValue.intValue() == selectedIndex.getValue()) {
	 *                  return;
	 *              }
	 *          T item = indexConverter.apply(newValue.intValue());
	 *          selectedIndex.setAndWait(newValue.intValue(), selectedItem);
	 *          selectedItem.set(item);
	 *      })
	 *      .to(other, (oldValue, newValue) -> updateOther.accept(clearing, newValue.intValue(), other))
	 *      .create();
	 * }
	 * </pre>
	 * To change it you should override the {@link SingleSelectionModel} method.
	 */
	public void bindIndexBidirectional(Property<Number> other, Function<Integer, T> indexConverter, TriConsumer<Boolean, Integer, Property<Number>> updateOther) {
		if (selectedIndex.isBound()) selectedIndex.unbind();
		BiBindingManager.instance().bindBidirectional(selectedIndex)
				.with((oldValue, newValue) -> {
					if (newValue.intValue() == -1) {
						clearSelection();
						return;
					}

					if (newValue.intValue() == selectedIndex.getValue()) {
						return;
					}
					T item = indexConverter.apply(newValue.intValue());
					selectedIndex.setAndWait(newValue.intValue(), selectedItem);
					selectedItem.set(item);
				})
				.to(other, (oldValue, newValue) -> updateOther.accept(clearing, newValue.intValue(), other))
				.create();
	}

	/**
	 * Binds the item property to given source {@link ObservableValue}.
	 * The itemConverter function is used to convert the item values to an index
	 * of the selection model.
	 * <p></p>
	 * By default creates this binding:
	 * <pre>
	 * {@code
	 *      BindingManager.instance().bind(selectedItem)
	 *          .with((oldValue, newValue) -> {
	 *              if (!selectionModel.getUnmodifiableItems().contains(newValue)) {
	 *                  throw new IllegalArgumentException("The given item is not present is this selection model's list");
	 *              }
	 *              int index = itemConverter.apply(newValue);
	 *              selectedItem.setAndWait(newValue, selectedIndex);
	 *              selectedIndex.set(index);
	 *          })
	 *          .to(source)
	 *          .create();
	 * }
	 * </pre>
	 * To change it you should override the {@link SingleSelectionModel} method.
	 */
	public void bindItem(ObservableValue<? extends T> source, Function<T, Integer> itemConverter) {
		if (selectedItem.isBound()) selectedItem.unbind();
		BindingManager.instance().bind(selectedItem)
				.with((oldValue, newValue) -> {
					if (!selectionModel.getUnmodifiableItems().contains(newValue)) {
						throw new IllegalArgumentException("The given item is not present is this selection model's list");
					}
					int index = itemConverter.apply(newValue);
					selectedItem.setAndWait(newValue, selectedIndex);
					selectedIndex.set(index);
				})
				.to(source)
				.create();
	}

	/**
	 * Binds the item property bidirectionally to given other {@link Property}.
	 * The itemConverter function is used to convert the item from the other property
	 * to an index of the selection model.
	 * <p>
	 * The updateOther {@link TriConsumer} is used to customize the way the other
	 * property is updated, the first parameter is the clearing flag of the selection manager,
	 * the second parameter is the new item, the third parameter is the other property reference.
	 * <p></p>
	 * By default creates this binding:
	 * <pre>
	 * {@code
	 *      BiBindingManager.instance().bindBidirectional(selectedItem)
	 *          .with((oldValue, newValue) -> {
	 *              if (newValue == null) {
	 *                  clearSelection();
	 *                  return;
	 *              }
	 *
	 *              if (!selectionModel.getUnmodifiableItems().contains(newValue)) {
	 *                  throw new IllegalArgumentException("The given item is not present is this selection model's list");
	 *              }
	 *
	 *              int index = itemConverter.apply(newValue);
	 *              selectedItem.setAndWait(newValue, selectedIndex);
	 *              selectedIndex.set(index);
	 *          })
	 *          .to(other, (oldValue, newValue) -> updateOther.accept(clearing, newValue, other))
	 *          .create();
	 * }
	 * </pre>
	 * To change it you should override the {@link SingleSelectionModel} method.
	 */
	public void bindItemBidirectional(Property<T> other, Function<T, Integer> itemConverter, TriConsumer<Boolean, T, Property<T>> updateOther) {
		if (selectedItem.isBound()) selectedItem.unbind();
		BiBindingManager.instance().bindBidirectional(selectedItem)
				.with((oldValue, newValue) -> {
					if (newValue == null) {
						clearSelection();
						return;
					}

					if (!selectionModel.getUnmodifiableItems().contains(newValue)) {
						throw new IllegalArgumentException("The given item is not present is this selection model's list");
					}

					int index = itemConverter.apply(newValue);
					selectedItem.setAndWait(newValue, selectedIndex);
					selectedIndex.set(index);
				})
				.to(other, (oldValue, newValue) -> updateOther.accept(clearing, newValue, other))
				.create();
	}

	/**
	 * Unbinds the selection.
	 */
	public void unbind() {
		if (selectedIndex.isBound()) {
			selectedIndex.unbind();
		}
		if (selectedItem.isBound()) {
			selectedItem.unbind();
		}
	}

	/**
	 * Removes the bidirectional binding between the index property and the given other property.
	 */
	public void unbindIndexBidirectional(Property<Number> other) {
		selectedIndex.unbindBidirectional(other);
	}

	/**
	 * Removes the bidirectional binding between the item property and the given other property.
	 */
	public void unbindItemBidirectional(Property<T> other) {
		selectedItem.unbindBidirectional(other);
	}

	/**
	 * Removes all bidirectional bindings.
	 */
	public void unbindBidirectional() {
		selectedIndex.clearBidirectional();
		selectedItem.clearBidirectional();
	}

	/**
	 * Returns true if the selected index or item properties are bound
	 * unidirectionally.
	 */
	public boolean isBound() {
		return selectedIndex.isBound() || selectedItem.isBound();
	}
}

