/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.selection.model;

import java.util.List;

import javafx.beans.property.MapProperty;

/// An interface for components that have a selection model.<br >
/// Also offers a bunch of delegate methods to the selection model.
public interface WithSelectionModel<T> {
    ISelectionModel<T> getSelectionModel();

    default void selectFirst() {
        getSelectionModel().selectFirst();
    }

    default void selectLast() {
        getSelectionModel().selectLast();
    }

    default MapProperty<Integer, T> selection() {
        return getSelectionModel().selection();
    }

    default List<T> selectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    default T getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }
}
