/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.selection.base.IListCheckModel;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the {@code IListCheckModel} interface.
 * <p>
 * Extends {@link ListSelectionModel}.
 */
public class ListCheckModel<T> extends ListSelectionModel<T> implements IListCheckModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MapProperty<Integer, T> checkedItems = new SimpleMapProperty<>(getMap());

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Checks if the map contains the given index key.
     */
    @Override
    public boolean containsChecked(int index) {
        return checkedItems.containsKey(index);
    }

    /**
     * Puts the specified entry in the map.
     */
    @Override
    public void check(int index, T data) {
        checkedItems.put(index, data);
    }

    /**
     * This method is called when the cell finds the data in the check model
     * but the index changed so it needs to be updated.
     */
    @Override
    public void updateIndex(T data, int index) {
        super.updateIndex(data, index);

        int mapIndex = checkedItems.entrySet()
                .stream()
                .filter(entry -> data.equals(entry.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(-1);
        if (mapIndex != -1) {
            checkedItems.put(mapIndex, data);
        }
    }

    /**
     * Removes the mapping for the given index.
     */
    @Override
    public void clearCheckedItem(int index) {
        checkedItems.remove(index);
    }

    /**
     * Retrieves the index for the given data, if preset
     * removes the mapping for that index.
     */
    @Override
    public void clearCheckedItem(T data) {
        checkedItems.entrySet().stream()
                .filter(entry -> entry.getValue().equals(data))
                .findFirst()
                .ifPresent(entry -> checkedItems.remove(entry.getKey()));
    }

    /**
     * Removes all the entries from the map.
     */
    @Override
    public void clearChecked() {
        checkedItems.set(getMap());
    }

    /**
     * @return the checked item in the map with the given index or null
     * if not found
     */
    @Override
    public T getCheckedItem(int index) {
        if (checkedItems.isEmpty()) {
            return null;
        }

        try {
            return checkedItems.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @return an unmodifiable list of all the checked items
     */
    @Override
    public List<T> getCheckedItems() {
        return List.copyOf(checkedItems.values());
    }

    /**
     * @return the map property used for the check
     */
    @Override
    public MapProperty<Integer, T> checkedItemsProperty() {
        return checkedItems;
    }
}
