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

package io.github.palexdev.materialfx.demo.model;

import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedIntegerProperty;
import io.github.palexdev.materialfx.beans.properties.synced.SynchronizedObjectProperty;
import io.github.palexdev.materialfx.bindings.BindingHelper;
import io.github.palexdev.materialfx.selection.SingleSelectionModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import java.util.Objects;
import java.util.function.Function;

public class SimplePerson {
    private final String name;

    public SimplePerson(String name) {
        this.name = name;

        SingleSelectionModel<String> sm = new SingleSelectionModel<>(new SimpleObjectProperty<>(FXCollections.observableArrayList())) {
            @Override
            public void bindIndex(ObservableValue<? extends Number> source, Function<Integer, String> indexConverter) {
                SynchronizedIntegerProperty selectedIndex = selectionManager.selectedIndexProperty();
                SynchronizedObjectProperty<String> selectedItem = selectionManager.selectedItemProperty();
                selectedIndex.provideHelperFactory(property -> new BindingHelper<>() {
                    @Override
                    protected void updateBound(Number newValue) {
                        String item = indexConverter.apply(newValue.intValue());
                        selectedIndex.setAndWait(newValue.intValue(), selectedItem);
                        selectedItem.set(item);
                    }
                });
            }
        };
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePerson that = (SimplePerson) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
