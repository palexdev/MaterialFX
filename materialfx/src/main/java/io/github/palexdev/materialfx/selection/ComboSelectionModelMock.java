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

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Rather than recreating and adapting the entire JavaFX's selection model for usage
 * in {@link MFXComboBox} we "mock" it. The combo box listview selection model is bound to this one
 * and vice versa.
 * <p></p>
 * <b>Note: if the select methods do not work try adding a listener to the skin property
 * of the control and using the methods there.</b>
 */
public class ComboSelectionModelMock<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXComboBox<T> comboBox;
    private final ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyIntegerWrapper selectedIndex = new ReadOnlyIntegerWrapper(-1);
    private boolean isClearRequested = false;

    //================================================================================
    // Constructors
    //================================================================================
    public ComboSelectionModelMock(MFXComboBox<T> comboBox) {
        this.comboBox = comboBox;
    }

    //================================================================================
    // Methods
    //================================================================================
    public void clearSelection() {
        isClearRequested = true;
        selectedItem.set(null);
        selectedIndex.set(-1);
        isClearRequested = false;
    }

    public void selectItem(T item) {
        if (!comboBox.getItems().contains(item)) {
            return;
        }
        selectedItem.set(item);
    }

    public void selectFirst() {
        selectedIndex.set(0);
    }

    public void selectNext() {
        if (getSelectedIndex() == (comboBox.getItems().size() - 1)) {
            return;
        }
        selectedIndex.add(1);
    }

    public void selectLast() {
        selectedIndex.set(comboBox.getItems().size());
    }

    public void selectPrevious() {
        if (getSelectedIndex() == -1) {
            return;
        }
        selectedIndex.subtract(1);
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    public ReadOnlyIntegerWrapper selectedIndexProperty() {
        return selectedIndex;
    }

    public T getSelectedItem() {
        return selectedItem.get();
    }

    public ReadOnlyObjectWrapper<T> selectedItemProperty() {
        return selectedItem;
    }

    public boolean isClearRequested() {
        return isClearRequested;
    }
}
