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
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;

/**
 * Simple implementation of a {@link Cell} capable of representing {@link LocalDate} values.
 * <p></p>
 * It has three main states:
 * <p> - selected: when the cell's value is equal to {@link MFXDatePicker#valueProperty()}
 * <p> - current: when the cell's value is equal to {@link MFXDatePicker#currentDateProperty()}
 * <p> - extra: to mark this cells as belonging to a different month
 */
public class MFXDateCell extends Label implements Cell<LocalDate> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-date-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXDateCell.css");

	private final MFXDatePicker datePicker;
	private final ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	protected static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	private final ReadOnlyBooleanWrapper current = new ReadOnlyBooleanWrapper();
	protected static final PseudoClass CURRENT_PSEUDO_CLASS = PseudoClass.getPseudoClass("current");

	private boolean extra = false;
	protected static final PseudoClass EXTRA_PSEUDO_CLASS = PseudoClass.getPseudoClass("extra");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXDateCell(MFXDatePicker datePicker, LocalDate date) {
		this.datePicker = datePicker;
		updateItem(date);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setAlignment(Pos.CENTER);
		setBehavior();
	}

	/**
	 * Sets the behavior for selected and current states. Binds the text to {@link LocalDate#getDayOfMonth()} (from the current value),
	 * binds the visible property to the cell's text (hidden if text is empty, visible if text is not empty)
	 * <p>
	 * Also handles MOUSE_PRESSED events to change the date picker's value.
	 */
	protected void setBehavior() {
		selected.addListener((observable, oldValue, newValue) -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		current.addListener((observable, oldValue, newValue) -> pseudoClassStateChanged(CURRENT_PSEUDO_CLASS, current.get()));

		textProperty().bind(Bindings.createStringBinding(
				() -> getDate() != null ? String.valueOf(getDate().getDayOfMonth()) : "",
				dateProperty()
		));
		visibleProperty().bind(textProperty().isNotEmpty());

		selected.bind(datePicker.valueProperty().isEqualTo(dateProperty()));
		current.bind(datePicker.currentDateProperty().isEqualTo(dateProperty()));
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> datePicker.setValue(getDate()));
	}

	/**
	 * Marks this cell as an extra cell.
	 */
	public void markAsExtra() {
		extra = true;
		pseudoClassStateChanged(EXTRA_PSEUDO_CLASS, true);
	}

	/**
	 * Un-marks this cell as extra.
	 */
	public void unmarkAsExtra() {
		extra = false;
		pseudoClassStateChanged(EXTRA_PSEUDO_CLASS, false);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void updateItem(LocalDate date) {
		setDate(date);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public LocalDate getDate() {
		return date.get();
	}

	/**
	 * Specifies the cell's represented date.
	 */
	public ReadOnlyObjectProperty<LocalDate> dateProperty() {
		return date.getReadOnlyProperty();
	}

	protected void setDate(LocalDate date) {
		this.date.set(date);
	}

	/**
	 * @return whether the cell is an extra cell
	 */
	public boolean isExtra() {
		return extra;
	}
}
