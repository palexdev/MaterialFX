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

import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.selection.base.ITableSelectionModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@code ITableSelectionModel} interface.
 * <p>
 * Basic selection model, allows to: clear the selection, single and multiple selection for {@code MFXTableRows} data.
 *
 * @see MFXTableRow
 */
public class TableSelectionModel<T> implements ITableSelectionModel<T> {
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
     * Builds a new observable hash map.
     */
    protected ObservableMap<Integer, T> getMap() {
        return FXCollections.observableHashMap();
    }

    /**
     * This method is called when the mouse event passed to {@link #select(int, T, MouseEvent)}
     * is null. Since it's null there's no check for isShiftDown() or isControlDown(), so in case
     * of multiple selection enabled the passed index and data will always be added to the map.
     */
    private void select(int index, T data) {
        if (allowsMultipleSelection) {
            selectedItems.put(index, data);
        } else {
            ObservableMap<Integer, T> tmpMap = getMap();
            tmpMap.put(index, data);
            selectedItems.set(tmpMap);
        }
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Checks if the map contains the given item.
     */
    @Override
    public boolean containsSelected(T data) {
        return selectedItems.containsValue(data);
    }

    /**
     * Checks if the map contains the given index key.
     */
    @Override
    public boolean containSelected(int index) {
        return selectedItems.containsKey(index);
    }

    /**
     * Called by the rows when the mouse is pressed.
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
     * managed by the rows.
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
    public void clearSelectedItem(T item) {
        selectedItems.entrySet().stream()
                .filter(entry -> entry.getValue().equals(item))
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
     * @return the currently selected index, 0 if more than one item is selected,
     * -1 if no item is selected
     */
    @Override
    public int getSelectedIndex() {
        List<Integer> keys = new ArrayList<>(selectedItems.keySet());
        return !keys.isEmpty() ? keys.get(0) : -1;
    }

    /**
     * @return an unmodifiable list containing the currently selected indexes
     */
    @Override
    public List<Integer> getSelectedIndexes() {
        return List.copyOf(selectedItems.keySet());
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
        List<T> items = new ArrayList<>(selectedItems.values());
        return items.size() > index ? items.get(index) : null;
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
        return selectedItems;
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
     * Specifies if the model is being updated by the table view after a change
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
