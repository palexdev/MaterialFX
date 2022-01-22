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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.cell.MFXCheckTreeCell;
import io.github.palexdev.materialfx.selection.TreeCheckModel;
import javafx.scene.control.CheckBox;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXCheckTreeItemSkin}.
 *
 * @see MFXCheckTreeItem
 * @see TreeCheckModel
 */
public class MFXCheckTreeItemSkin<T> extends MFXTreeItemSkin<T> {
	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckTreeItemSkin(MFXCheckTreeItem<T> item) {
		super(item);

		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds a listener for handling CHECK_EVENTs and call {@link TreeCheckModel#check(MFXCheckTreeItem, CheckTreeItemEvent)}.
	 */
	private void setListeners() {
		MFXCheckTreeItem<T> item = (MFXCheckTreeItem<T>) getSkinnable();

		item.addEventHandler(CheckTreeItemEvent.CHECK_EVENT, event -> item.getSelectionModel().check(item, event));
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Overridden method to create a MFXCheckTreeCell and fire a CHECK_EVENT
	 * on checkbox action.
	 */
	@Override
	protected AbstractMFXTreeCell<T> createCell() {
		MFXCheckTreeItem<T> item = (MFXCheckTreeItem<T>) getSkinnable();

		MFXCheckTreeCell<T> cell = (MFXCheckTreeCell<T>) super.createCell();
		CheckBox checkbox = cell.getCheckbox();
		checkbox.setOnAction(event -> {
			item.fireEvent(new CheckTreeItemEvent<>(CheckTreeItemEvent.CHECK_EVENT, item));
			event.consume();
		});
		return cell;
	}
}
