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
import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;

/**
 * Implementation of a MFXSimpleTreeCell with a checkbox for usage in MFXCheckTreeViews.
 *
 * @param <T>
 */
public class MFXCheckTreeCell<T> extends MFXSimpleTreeCell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-check-tree-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXCheckTreeCell.css");
	private final MFXCheckbox checkbox;

	private static final PseudoClass CHECKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("checked");
	private static final PseudoClass INDETERMINATE_PSEUDO_CLASS = PseudoClass.getPseudoClass("indeterminate");
	private final BooleanProperty checked = new SimpleBooleanProperty(false);
	private final BooleanProperty indeterminate = new SimpleBooleanProperty(false);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckTreeCell(MFXCheckTreeItem<T> item) {
		super(item);
		checkbox = new MFXCheckbox("");
		getChildren().add(1, checkbox);
		initialize(item);
	}

	public MFXCheckTreeCell(MFXCheckTreeItem<T> item, double fixedHeight) {
		super(item, fixedHeight);
		checkbox = new MFXCheckbox("");
		getChildren().add(1, checkbox);
		initialize(item);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the cell style class, sets the fixed cells size to 32, adds bindings for
	 * checked and indeterminate properties.
	 */
	private void initialize(MFXCheckTreeItem<T> item) {
		getStyleClass().add(STYLE_CLASS);
		setFixedCellSize(32);

		addListeners();
		checked.bind(item.checkedProperty());
		indeterminate.bind(item.indeterminateProperty());
	}

	/**
	 * Adds listeners for checked and indeterminate properties.
	 */
	private void addListeners() {
		checked.addListener(invalidate -> pseudoClassStateChanged(CHECKED_PSEUDO_CLASS, checked.get()));
		checked.addListener((observable, oldValue, newValue) -> checkbox.setSelected(newValue));
		indeterminate.addListener(invalidate -> pseudoClassStateChanged(INDETERMINATE_PSEUDO_CLASS, indeterminate.get()));
		indeterminate.addListener((observable, oldValue, newValue) -> checkbox.setIndeterminate(newValue));
	}

	/**
	 * @return this cell's checkbox instance
	 */
	public MFXCheckbox getCheckbox() {
		return checkbox;
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
