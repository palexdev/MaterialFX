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

public class ComboSelectionModelMock<T> {
    private final MFXComboBox<T> comboBox;
    private final ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyIntegerWrapper selectedIndex = new ReadOnlyIntegerWrapper(-1);

    public ComboSelectionModelMock(MFXComboBox<T> comboBox) {
        this.comboBox = comboBox;

        selectedItem.addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        selectedIndex.addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }

    public void clearSelection() {
        selectedItem.set(null);
        selectedIndex.set(-1);
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
}
