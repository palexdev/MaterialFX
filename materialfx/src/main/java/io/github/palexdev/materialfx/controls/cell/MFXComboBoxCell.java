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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.base.MFXCombo;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * Cells used by default by {@link MFXComboBox}
 * <p></p>
 * The cell's structure is pretty similar to the {@link MFXListCell} one, but doesn't include
 * a ripple generator as it is not necessary (the popup is closed on selection, the ripple effect is
 * barely noticeable).
 * <p></p>
 * The label used to display the data (in case it's not a Node), uses the combo box's
 * {@link StringConverter} to convert the data to a String. In case it's null, toString() is
 * called on the data.
 */
public class MFXComboBoxCell<T> extends HBox implements Cell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-combo-box-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXComboBoxCell.css");

	protected final ReadOnlyObjectWrapper<T> data = new ReadOnlyObjectWrapper<>();
	protected final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();

	protected final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	protected final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	protected final MFXCombo<T> comboBox;
	private final Label label;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXComboBoxCell(MFXCombo<T> combo, T data) {
		this.comboBox = combo;

		setPrefHeight(32);
		setMaxHeight(USE_PREF_SIZE);
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(5);

		if (!(data instanceof Node)) {
			label = new Label();
			label.getStyleClass().add("data-label");
			label.textProperty().bind(Bindings.createStringBinding(
					() -> {
						StringConverter<T> converter = combo.getConverter();
						return converter != null ? converter.toString(getData()) : getData().toString();
					}, dataProperty(), combo.converterProperty()
			));
		} else {
			label = null;
		}

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setBehavior();
		render(getData());
	}

	/**
	 * Sets the following behaviors:
	 * <p>
	 * - Binds the selected property to the combo' selection model (checks for index). <p>
	 * - Updates the selected PseudoClass state when selected property changes.<p>
	 * - Adds and handler for MOUSE_PRESSED events to call {@link #updateSelection(MouseEvent)}.
	 */
	protected void setBehavior() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(
				() -> comboBox.getSelectionModel().getSelectedIndex() == index.get(),
				comboBox.getSelectionModel().selectedIndexProperty(), index
		));
		addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateSelection);
	}

	/**
	 * Responsible for rendering the cell's content.
	 * <p>
	 * If the given data type is a Node, it is added to the children list,
	 * otherwise a label is used to display the data.
	 */
	protected void render(T data) {
		if (data instanceof Node) {
			getChildren().setAll((Node) data);
		} else {
			getChildren().setAll(label);
		}
	}

	/**
	 * If the pressed mouse button is not the primary, exits immediately.
	 * <p>
	 * Orders the combo's selection model to select the index of this cell.
	 */
	protected void updateSelection(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) {
			return;
		}

		int index = getIndex();
		comboBox.getSelectionModel().selectIndex(index);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Node getNode() {
		return this;
	}

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

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
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
