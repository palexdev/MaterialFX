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

package io.github.palexdev.materialfx.controls.cell.base;

import io.github.palexdev.materialfx.controls.base.AbstractMFXListView;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Base class for all cells used in listviews based on VirtualizedFX,
 * defines common properties and behavior (e.g. selection), has the selected property
 * and PseudoClass ":selected" for usage in CSS.
 * <p>
 * Extends {@link HBox} and implements {@link Cell}.
 *
 * @param <T> the type of data within the ListView
 */
public abstract class AbstractMFXListCell<T> extends HBox implements Cell<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected final AbstractMFXListView<T, ?> listView;
	protected final ReadOnlyObjectWrapper<T> data = new ReadOnlyObjectWrapper<>();
	protected final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();

	protected final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	protected final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXListCell(AbstractMFXListView<T, ?> listView, T data) {
		this.listView = listView;
		setData(data);
		setPrefHeight(32);
		setMaxHeight(USE_PREF_SIZE);
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(5);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Abstract method which defines how the cell should process and show the given data.
	 */
	protected abstract void render(T data);

	//================================================================================
	// Methods
	//================================================================================
	protected void initialize() {
		setBehavior();
	}

	/**
	 * Sets the following behaviors:
	 * <p>
	 * - Binds the selected property to the list' selection model (checks for index). <p>
	 * - Updates the selected PseudoClass state when selected property changes.<p>
	 * - Adds and handler for MOUSE_PRESSED events to call {@link #updateSelection(MouseEvent)}.
	 */
	protected void setBehavior() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(
				() -> listView.getSelectionModel().getSelection().containsKey(index.get()),
				listView.getSelectionModel().selectionProperty(), index
		));

		addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateSelection);
	}

	/**
	 * If the pressed mouse button is not the primary, exits immediately.
	 * <p>
	 * If the cell is already deselected calls {@link MultipleSelectionModel#deselectIndex(int)},
	 * otherwise checks if Shift or Ctrl are pressed and updates the selection accordingly,
	 * adds if they were pressed, replaces if not (acting like single selection),
	 * see {@link MultipleSelectionModel#selectIndex(int)}, {@link MultipleSelectionModel#replaceSelection(Integer...)}.
	 */
	protected void updateSelection(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) return;

		int index = getIndex();
		if (event.isControlDown()) {
			if (isSelected()) {
				listView.getSelectionModel().deselectIndex(index);
			} else {
				listView.getSelectionModel().selectIndex(index);
			}
			return;
		}

		if (event.isShiftDown()) {
			listView.getSelectionModel().expandSelection(index);
			return;
		}

		listView.getSelectionModel().replaceSelection(index);
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Updates the index property of the cell.
	 * <p>
	 * This is called before {@link #updateItem(Object)}.
	 */
	@Override
	public void updateIndex(int index) {
		setIndex(index);
	}

	/**
	 * Updates the data property of the cell.
	 * <p>
	 * This is called after {@link #updateIndex(int)}.
	 */
	@Override
	public void updateItem(T item) {
		setData(item);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public T getData() {
		return data.get();
	}

	/**
	 * Data property of the cell.
	 */
	public ReadOnlyObjectProperty<T> dataProperty() {
		return data.getReadOnlyProperty();
	}

	protected void setData(T data) {
		this.data.set(data);
	}

	public int getIndex() {
		return index.get();
	}

	/**
	 * Specifies the cell's index.
	 */
	public ReadOnlyIntegerProperty indexProperty() {
		return index.getReadOnlyProperty();
	}

	protected void setIndex(int index) {
		this.index.set(index);
	}

	public boolean isSelected() {
		return selected.get();
	}

	/**
	 * Specifies the selection state of the cell.
	 */
	public ReadOnlyBooleanProperty selectedProperty() {
		return selected.getReadOnlyProperty();
	}

	protected void setSelected(boolean selected) {
		this.selected.set(selected);
	}
}
