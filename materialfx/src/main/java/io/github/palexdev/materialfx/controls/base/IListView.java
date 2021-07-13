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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Function;

/**
 * Interface that defines the public api for all the list views based on Flowless.
 *
 * @param <T> the type of data within the ListView
 * @param <C> the type of cells that will be used
 * @param <S> the type of selection model
 */
public interface IListView<T, C extends AbstractMFXFlowlessListCell<T>, S extends IListSelectionModel<T>> {

    /**
     * @return the items observable list
     */
    ObservableList<T> getItems();

    /**
     * Set all the items to the specified list.
     */
    void setItems(List<T> items);

    /**
     * @return the function used to build the list cells
     */
    Function<T, C> getCellFactory();

    /**
     * @return the cell factory property
     */
    ObjectProperty<Function<T, C>> cellFactoryProperty();

    /**
     * Replaces the cell factory with the given one
     */
    void setCellFactory(Function<T, C> cellFactory);

    /**
     * @return the list view selection model
     */
    S getSelectionModel();

    /**
     * @return the list view selection model property
     */
    ObjectProperty<S> selectionModelProperty();

    /**
     * Replaces the selection model with the given one.
     */
    void setSelectionModel(S selectionModel);
}
