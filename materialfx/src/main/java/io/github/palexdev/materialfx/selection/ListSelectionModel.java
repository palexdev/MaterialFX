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

import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListCell;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class ListSelectionModel<T> implements IListSelectionModel<T> {
    private final ListProperty<AbstractMFXFlowlessListCell<T>> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    private boolean allowsMultipleSelection = false;

    public ListSelectionModel() {
        selectedItems.addListener((ListChangeListener<AbstractMFXFlowlessListCell<T>>) change -> {
            List<AbstractMFXFlowlessListCell<T>> tmpRemoved = new ArrayList<>();
            List<AbstractMFXFlowlessListCell<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }
            tmpRemoved.forEach(item -> item.setSelected(false));
            tmpAdded.forEach(item -> item.setSelected(true));
        });
    }

    @SuppressWarnings("unchecked")
    protected void select(AbstractMFXFlowlessListCell<T> item) {
        if (!allowsMultipleSelection) {
            clearSelection();
            selectedItems.setAll(item);
        } else {
            selectedItems.add(item);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void select(AbstractMFXFlowlessListCell<T> item, MouseEvent mouseEvent) {
        if (mouseEvent == null) {
            select(item);
            return;
        }

        if (!allowsMultipleSelection && !selectedItems.contains(item)) {
            selectedItems.setAll(item);
            return;
        }

        if (mouseEvent.isShiftDown() || mouseEvent.isControlDown()) {
            if (item.isSelected()) {
                selectedItems.remove(item);
            } else {
                selectedItems.add(item);
            }
        } else if (!selectedItems.contains(item)) {
            selectedItems.setAll(item);
        }
    }

    @Override
    public void clearSelection() {
        if (selectedItems.isEmpty()) {
            return;
        }

        selectedItems.forEach(item -> item.setSelected(false));
        selectedItems.clear();
    }

    @Override
    public AbstractMFXFlowlessListCell<T> getSelectedItem() {
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    @Override
    public ListProperty<AbstractMFXFlowlessListCell<T>> getSelectedItems() {
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
}
