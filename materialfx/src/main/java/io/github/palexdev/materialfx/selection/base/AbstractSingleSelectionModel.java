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

package io.github.palexdev.materialfx.selection.base;

import io.github.palexdev.materialfx.selection.SingleSelectionManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Abstract base class for all SingleSelectionModels.
 * <p>
 * This class holds a property for the items list. Controls that uses this selection model are
 * responsible for changes in the source list, so if anything changes there be sure to keep the
 * selection model in a consistent state.
 * Also holds a reference for {@link SingleSelectionManager}, the class that is effectively
 * responsible for updating/managing the selection model' state.
 */
public abstract class AbstractSingleSelectionModel<T> implements ISingleSelectionModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>();
	protected final SingleSelectionManager<T> selectionManager = new SingleSelectionManager<>(this);

	//================================================================================
	// Constructors
	//================================================================================
	protected AbstractSingleSelectionModel(ObservableList<T> items) {
		this.items.set(items);
	}

	protected AbstractSingleSelectionModel(ObservableValue<? extends ObservableList<T>> items) {
		this.items.bind(items);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return an unmodifiable copy of the items list
	 */
	public ObservableList<T> getUnmodifiableItems() {
		return FXCollections.unmodifiableObservableList(items.get());
	}

	/**
	 * @return the items list
	 */
	final protected ObservableList<T> getItems() {
		return items.get();
	}
}
