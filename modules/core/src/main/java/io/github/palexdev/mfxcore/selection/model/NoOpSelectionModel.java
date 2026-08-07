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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/// A no-op implementation of [ISelectionModel].
@SuppressWarnings({"unchecked", "rawtypes"})
public final class NoOpSelectionModel<T> implements ISelectionModel<T> {
    //================================================================================
    // Instance
    //================================================================================
    private static final NoOpSelectionModel instance = new NoOpSelectionModel();

    public static <T> NoOpSelectionModel<T> instance() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final SelectionEventHandler seh = new SelectionEventHandler() {
        @Override
        public void handle(MouseEvent me, int index) {}

        @Override
        public void handle(KeyEvent ke, int index) {}
    };

    //================================================================================
    // Constructors
    //================================================================================
    private NoOpSelectionModel() {}

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public boolean contains(int index) {
        return false;
    }

    @Override
    public boolean contains(T element) {
        return false;
    }

    @Override
    public void clearSelection() {}

    @Override
    public void deselectIndex(int index) {}

    @Override
    public void deselectItem(T item) {}

    @Override
    public void deselectIndexes(int... indexes) {}

    @Override
    public void deselectIndexes(IntegerRange range) {}

    @Override
    public void deselectItems(T... items) {}

    @Override
    public void selectIndex(int index) {}

    @Override
    public void selectItem(T item) {}

    @Override
    public void selectIndexes(Integer... indexes) {}

    @Override
    public void selectIndexes(IntegerRange range) {}

    @Override
    public void selectItems(T... items) {}

    @Override
    public void expandSelection(int index, boolean fromLast) {}

    @Override
    public void replaceSelection(Integer... indexes) {}

    @Override
    public void replaceSelection(IntegerRange range) {}

    @Override
    public void replaceSelection(T... items) {}

    @Override
    public MapProperty<Integer, T> selection() {
        return new SimpleMapProperty<>();
    }

    @Override
    public List<T> getSelectedItems() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean allowsMultipleSelection() {
        return false;
    }

    @Override
    public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {}

    @Override
    public SelectionEventHandler eventHandler() {
        return seh;
    }

    @Override
    public void setEventHandler(Function<ISelectionModel<T>, SelectionEventHandler> fn) {}
}
