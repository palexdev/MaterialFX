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

import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ListSelectionModel<T> implements IListSelectionModel<T> {
    private final MapProperty<Integer, T> selectedItems = new SimpleMapProperty<>(getObservableTreeMap());
    private boolean allowsMultipleSelection = false;

    protected void select(int index, T data) {
        if (allowsMultipleSelection) {
            selectedItems.put(index, data);
        } else {
            ObservableMap<Integer, T> tmpMap = getObservableTreeMap();
            tmpMap.put(index, data);
            selectedItems.set(tmpMap);
        }
    }

    @Override
    public void select(int index, T data, MouseEvent mouseEvent) {
        if (mouseEvent == null) {
            select(index, data);
            return;
        }

        if (mouseEvent.isShiftDown() || mouseEvent.isControlDown()) {
            selectedItems.put(index, data);
        } else {
            ObservableMap<Integer, T> tmpMap = getObservableTreeMap();
            tmpMap.put(index, data);
            selectedItems.set(tmpMap);
        }
    }

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

    @Override
    public void clearSelectedItem(int index) {
        selectedItems.remove(index);
    }

    @Override
    public void clearSelectedItem(T data) {
        selectedItems.entrySet().stream()
                .filter(entry -> entry.getValue().equals(data))
                .findFirst()
                .ifPresent(entry -> selectedItems.remove(entry.getKey()));
    }

    @Override
    public void clearSelection() {
        selectedItems.clear();
    }

    @Override
    public T getSelectedItem() {
        return getSelectedItem(0);
    }

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

    @Override
    public List<T> getSelectedItems() {
        return List.copyOf(selectedItems.values());
    }

    @Override
    public MapProperty<Integer, T> selectedItemsProperty() {
        return this.selectedItems;
    }

    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    @Override
    public void setAllowsMultipleSelection(boolean multipleSelection) {
        this.allowsMultipleSelection = multipleSelection;
    }

    protected ObservableMap<Integer, T> getObservableTreeMap() {
        return FXCollections.observableMap(new TreeMap<>());
    }
}
