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

package io.github.palexdev.materialfx.selection.base;

import javafx.beans.property.MapProperty;

import java.util.List;

/**
 * Public API used by any {@code MFXFlowlessCheckListView}.
 */
public interface IListCheckModel<T> extends IListSelectionModel<T> {
    void check(int index, T data);
    void clearCheckedItem(int index);
    void clearCheckedItem(T data);
    void clearChecked();
    T getCheckedItem(int index);
    List<T> getCheckedItems();
    MapProperty<Integer, T> checkedItemsProperty();
}
