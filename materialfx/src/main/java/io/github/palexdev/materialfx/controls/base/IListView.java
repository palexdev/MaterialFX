/*
 * Copyright (C) 2022 Parisi Alessandro
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

import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Interface that defines the public API for all the listviews based on VirtualizedFX.
 *
 * @param <T> the type of data within the ListView
 * @param <C> the type of cells that will be used
 */
public interface IListView<T, C extends Cell<T>> {

	/**
	 * @return the items observable list
	 */
	ObservableList<T> getItems();

	/**
	 * The items list property.
	 */
	ObjectProperty<ObservableList<T>> itemsProperty();

	/**
	 * Replaces the items list with the given one.
	 */
	void setItems(ObservableList<T> items);

	StringConverter<T> getConverter();

	/**
	 * Specifies the {@link StringConverter} used to convert a generic
	 * item to a String. It is used by the list cells.
	 */
	ObjectProperty<StringConverter<T>> converterProperty();

	void setConverter(StringConverter<T> converter);

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
	 * @return the listview selection model
	 */
	IMultipleSelectionModel<T> getSelectionModel();
}
