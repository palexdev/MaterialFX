/*
 * Copyright (C) 2021 Parisi Alessandro
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

import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the {@code IListSelectionModel} interface.
 */
public class ListSelectionModel<T> implements IListSelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MapProperty<Integer, T> selectedItems = new SimpleMapProperty<>(getMap());
    private final BooleanProperty updating = new SimpleBooleanProperty();
    private boolean allowsMultipleSelection = false;

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This method is called when the mouse event passed to {@link #select(int, T, MouseEvent)}
     * is null. Since it's null there's no check for isShiftDown() or isControlDown(), so in case
     * of multiple selection enabled the passed index and data will always be added to the map.
     */
    protected void select(int index, T data) {
        if (allowsMultipleSelection) {
            selectedItems.put(index, data);
        } else {
            ObservableMap<Integer, T> tmpMap = getMap();
            tmpMap.put(index, data);
            selectedItems.set(tmpMap);
        }
    }

    /**
     * Builds a new observable hash map.
     */
    protected ObservableMap<Integer, T> getMap() {
        return FXCollections.observableHashMap();
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Checks if the map contains the given index key.
     */
    @Override
    public boolean containSelected(int index) {
        return selectedItems.containsKey(index);
    }

    /**
     * Called by the list cells when the mouse is pressed.
     * The mouse event is needed in case of multiple selection allowed because
     * we check if the Shift key or Ctrl key were pressed.
     * <p>
     * If the mouseEvent is null we call the other {@link #select(int, T)} method.
     * <p>
     * If the selection is multiple and Shift or Ctrl are pressed the new entry
     * is put in the map.
     * <p>
     * If the selection is single the map is replaced by a new one that contains only the
     * passed entry.
     * <p>
     * Note that if the item is already selected it is removed from the map, this behavior though is
     * managed by the cells.
     */
    @Override
    public void select(int index, T data, MouseEvent mouseEvent) {
        if (mouseEvent == null) {
            select(index, data);
            return;
        }

        if (allowsMultipleSelection && (mouseEvent.isShiftDown() || mouseEvent.isControlDown())) {
            selectedItems.put(index, data);
        } else {
            ObservableMap<Integer, T> tmpMap = getMap();
            tmpMap.put(index, data);
            selectedItems.set(tmpMap);
        }
    }

    /**
     * This method is called when the cell finds the data in the selection model
     * but the index changed so it needs to be updated.
     */
    @Override
    public void updateIndex(T data, int index) {
        int mapIndex = selectedItems.entrySet()
                .stream()
                .filter(entry -> data.equals(entry.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(-1);
        if (mapIndex != -1) {
            selectedItems.put(mapIndex, data);
        }
    }

    /**
     * Removes the mapping for the given index.
     */
    @Override
    public void clearSelectedItem(int index) {
        selectedItems.remove(index);
    }

    /**
     * Retrieves the index for the given data, if preset
     * removes the mapping for that index.
     */
    @Override
    public void clearSelectedItem(T data) {
        selectedItems.entrySet().stream()
                .filter(entry -> entry.getValue().equals(data))
                .findFirst()
                .ifPresent(entry -> selectedItems.remove(entry.getKey()));
    }

    /**
     * Removes all the entries from the map.
     */
    @Override
    public void clearSelection() {
        selectedItems.set(getMap());
    }

    /**
     * @return the first selected item in the map
     */
    @Override
    public T getSelectedItem() {
        return getSelectedItem(0);
    }

    /**
     * @return the selected item in the map with the given index or null
     * if not found
     */
    @Override
    public T getSelectedItem(int index) {
        if (selectedItems.isEmpty()) {
            return null;
        }

        try {
            return selectedItems.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @return an unmodifiable list of all the selected items
     */
    @Override
    public List<T> getSelectedItems() {
        return List.copyOf(selectedItems.values());
    }

    /**
     * @return the map property used for the selection
     */
    @Override
    public MapProperty<Integer, T> selectedItemsProperty() {
        return this.selectedItems;
    }

    /**
     * @return true if allows multiple selection, false if not.
     */
    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    /**
     * Sets the selection mode of the model, single or multiple.
     */
    @Override
    public void setAllowsMultipleSelection(boolean multipleSelection) {
        this.allowsMultipleSelection = multipleSelection;
    }

    @Override
    public boolean isUpdating() {
        return updating.get();
    }

    /**
     * Specifies if the model is being updated by the list view after a change
     * in the items observable list.
     */
    @Override
    public BooleanProperty updatingProperty() {
        return updating;
    }

    @Override
    public void setUpdating(boolean updating) {
        this.updating.set(updating);
    }
}
