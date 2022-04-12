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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.selection.base.AbstractMultipleSelectionModel;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

// TODO introduce bindings eventually

/**
 * Helper class that is capable of managing/update MultipleSelectionModels.
 */
@SuppressWarnings("unchecked")
public class MultipleSelectionManager<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final AbstractMultipleSelectionModel<T> selectionModel;
	private final MapProperty<Integer, T> selection = new SimpleMapProperty<>(getMap());
	private boolean allowsMultipleSelection = true;

	//================================================================================
	// Constructors
	//================================================================================
	public MultipleSelectionManager(AbstractMultipleSelectionModel<T> selectionModel) {
		this.selectionModel = selectionModel;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Clears the selection by setting it to an empty map.
	 */
	public void clearSelection() {
		selection.set(getMap());
	}

	/**
	 * Removes the given index from the selection map.
	 */
	public void deselectIndex(int index) {
		selection.remove(index);
	}

	/**
	 * Retrieves the index of the given item from the items list and if it's not -1
	 * removes it from the selection map.
	 */
	public void deselectItem(T item) {
		int index = selectionModel.getItems().indexOf(item);
		if (index >= 0) {
			selection.remove(index);
		}
	}

	/**
	 * Removes all the specified indexes from the selection map, done
	 * by creating a tmp map, updating the tmp map and then replacing the
	 * selection with this new map.
	 */
	public void deselectIndexes(int... indexes) {
		ObservableMap<Integer, T> tmp = getMap(selection);
		for (int index : indexes) {
			tmp.remove(index);
		}
		selection.set(tmp);
	}

	/**
	 * Filters the items list to check if the given items exist, then retrieves
	 * their index and collect them to a tmp map, then replaces the
	 * selection with this new map.
	 */
	public void deselectItems(T... items) {
		Map<Integer, T> tmp = Arrays.stream(items)
				.filter(item -> selectionModel.getItems().contains(item))
				.collect(Collectors.toMap(
						item -> selectionModel.getItems().indexOf(item),
						item -> item
				));
		ObservableMap<Integer, T> newSelection = getMap(tmp);
		selection.set(newSelection);
	}

	/**
	 * If multiple selection is allowed adds the given index (and the retrieved item) to the selection map,
	 * otherwise creates a new tmp map containing only the given index-item entry and replaces the selection.
	 */
	public void updateSelection(int index) {
		T item = selectionModel.getItems().get(index);
		if (allowsMultipleSelection) {
			selection.put(index, item);
		} else {
			ObservableMap<Integer, T> map = getMap();
			map.put(index, item);
			selection.set(map);
		}
	}

	/**
	 * If multiple selection is allowed adds the given item (and the retrieved index) to the selection map,
	 * otherwise creates a new tmp map containing only the given index-item entry and replaces the selection.
	 */
	public void updateSelection(T item) {
		int index = selectionModel.getItems().indexOf(item);
		if (allowsMultipleSelection) {
			selection.put(index, item);
		} else {
			ObservableMap<Integer, T> map = getMap();
			map.put(index, item);
			selection.set(map);
		}
	}

	/**
	 * If multiple selection is allowed adds all the given indexes to the selection
	 * (and the retrieved items), otherwise replaces the selection with the first index given in the list.
	 */
	public void updateSelectionByIndexes(List<Integer> indexes) {
		if (indexes.isEmpty()) return;

		if (allowsMultipleSelection) {
			Set<Integer> indexesSet = new LinkedHashSet<>(indexes);
			Map<Integer, T> newSelection = indexesSet.stream().collect(Collectors.toMap(
					i -> i,
					i -> selectionModel.getItems().get(i),
					(t, t2) -> t2,
					LinkedHashMap::new
			));
			selection.putAll(newSelection);
		} else {
			int index = indexes.get(0);
			T item = selectionModel.getItems().get(index);
			ObservableMap<Integer, T> map = getMap();
			map.put(index, item);
			selection.set(map);
		}
	}

	/**
	 * If multiple selection is allowed adds all the given items to the selection
	 * (and the retrieved indexes), otherwise replaces the selection with the first item given in the list.
	 */
	public void updateSelectionByItems(List<T> items) {
		if (items.isEmpty()) return;

		if (allowsMultipleSelection) {
			Set<Integer> indexesSet = items.stream()
					.mapToInt(item -> selectionModel.getItems().indexOf(item))
					.boxed()
					.collect(Collectors.toSet());
			Map<Integer, T> newSelection = indexesSet.stream().collect(Collectors.toMap(
					i -> i,
					items::get
			));
			selection.putAll(newSelection);
		} else {
			T item = items.get(0);
			int index = selectionModel.getItems().indexOf(item);
			ObservableMap<Integer, T> map = getMap();
			map.put(index, item);
			selection.set(map);
		}
	}

	/**
	 * This is responsible for expanding the selection in the given index direction.
	 * There are 4 cases to consider:
	 * <p> 1) The selection is empty: the new selection will go from [0 to index]
	 * <p> 2) The minimum selected index is equal to the given index: the new selection will just be [index]
	 * <p> 3) The given index is lesser than the minimum index: the new selection will go from [index to min]
	 * <p> 4) The given index is greater than the minimum index: the new selection will go from [min to index]
	 */
	public void expandSelection(int index) {
		if (selection.isEmpty()) {
			replaceSelection(NumberRange.expandRangeToArray(0, index));
			return;
		}

		int min = selection.keySet().stream().min(Integer::compareTo).orElse(-1);
		if (index == min) {
			replaceSelection(index);
			return;
		}

		if (index < min) {
			replaceSelection(NumberRange.expandRangeToArray(index, min));
		} else {
			replaceSelection(NumberRange.expandRangeToArray(min, index));
		}
	}

	/**
	 * If multiple selection is allowed replaces the selection with all the given indexes
	 * (and the retrieved items), otherwise replaces the selection with the first given index.
	 */
	public void replaceSelection(Integer... indexes) {
		ObservableMap<Integer, T> newSelection = getMap();
		if (allowsMultipleSelection) {
			newSelection.putAll(
					Arrays.stream(indexes).collect(Collectors.toMap(
							i -> i,
							i -> selectionModel.getItems().get(i)
					))
			);
		} else {
			int index = indexes[0];
			newSelection.put(index, selectionModel.getItems().get(index));
		}
		selection.set(newSelection);
	}

	/**
	 * If multiple selection is allowed replaces the selection with all the given items
	 * (and the retrieved indexes), otherwise replaces the selection with the first given item.
	 */
	public void replaceSelection(T... items) {
		ObservableMap<Integer, T> newSelection = getMap();
		if (allowsMultipleSelection) {
			newSelection.putAll(
					Arrays.stream(items).collect(Collectors.toMap(
							item -> selectionModel.getItems().indexOf(item),
							item -> item
					))
			);
		} else {
			T item = items[0];
			newSelection.put(selectionModel.getItems().indexOf(item), item);
		}
		selection.set(newSelection);
	}

	/**
	 * Builds a new observable hash map backed by a {@link LinkedHashMap}.
	 */
	protected ObservableMap<Integer, T> getMap() {
		return FXCollections.observableMap(new LinkedHashMap<>());
	}

	/**
	 * Builds a new observable hash map backed by a {@link LinkedHashMap}, initialized with the given map.
	 */
	protected ObservableMap<Integer, T> getMap(Map<Integer, T> map) {
		return FXCollections.observableMap(new LinkedHashMap<>(map));
	}


	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the selection {@link ObservableMap}
	 */
	public ObservableMap<Integer, T> getSelection() {
		return selection.get();
	}

	/**
	 * The {@link MapProperty} used to keep track of multiple selection.
	 * <p></p>
	 * We use a {@link MapProperty} to represent multiple selection because this way
	 * we can always update it "atomically", meaning that when the selected indexes changes
	 * the selected items are updated as well (also true viceversa).
	 */
	public MapProperty<Integer, T> selectionProperty() {
		return selection;
	}

	/**
	 * Replaces the selection with the given {@link ObservableMap}.
	 */
	public void setSelection(ObservableMap<Integer, T> selection) {
		this.selection.set(selection);
	}

	/**
	 * Returns an unmodifiable {@link List} containing all the selected values extracted from
	 * {@link Map#values()}.
	 * The values order is kept since the selection is backed by a {@link LinkedHashMap}.
	 */
	public List<T> getSelectedValues() {
		return List.copyOf(selection.values());
	}

	/**
	 * Specifies if this model allows multiple selection or should act like
	 * a SingleSelectionModel.
	 */
	public boolean allowsMultipleSelection() {
		return allowsMultipleSelection;
	}

	/**
	 * Sets the selection behavior of this model to be multiple (true) or
	 * single (false).
	 * <p>
	 * If it's set to false the selection is cleared.
	 */
	public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
		if (!allowsMultipleSelection) clearSelection();
		this.allowsMultipleSelection = allowsMultipleSelection;
	}
}
