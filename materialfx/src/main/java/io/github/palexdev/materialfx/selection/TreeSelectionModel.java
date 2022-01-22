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

package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.selection.base.ITreeSelectionModel;
import io.github.palexdev.materialfx.utils.TreeItemStream;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the {@code ITreeSelectionModel} interface.
 * <p>
 * This provides common methods for items selection.
 * <p>
 * To select an item it should call the TreeSelectionModel associated with the tree which contains the item
 * with {@link AbstractMFXTreeItem#getSelectionModel()} and call the {@link #select(AbstractMFXTreeItem, MouseEvent)} method.
 * In the constructor a listener is added to the ListProperty of this class, which contains all the selected items, and
 * its role is to change the selected property of the item.
 */
public class TreeSelectionModel<T> implements ITreeSelectionModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ListProperty<AbstractMFXTreeItem<T>> selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
	private boolean allowsMultipleSelection = false;

	//================================================================================
	// Constructors
	//================================================================================
	public TreeSelectionModel() {
		selectedItems.addListener((ListChangeListener<AbstractMFXTreeItem<T>>) change -> {
			List<AbstractMFXTreeItem<T>> tmpRemoved = new ArrayList<>();
			List<AbstractMFXTreeItem<T>> tmpAdded = new ArrayList<>();

			while (change.next()) {
				tmpRemoved.addAll(change.getRemoved());
				tmpAdded.addAll(change.getAddedSubList());
			}
			tmpRemoved.forEach(item -> item.setSelected(false));
			tmpAdded.forEach(item -> item.setSelected(true));
		});
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This method is called when the mouseEvent argument passed to {@link #select(AbstractMFXTreeItem, MouseEvent)}
	 * is null. It is used for example when you want the tree to start with one or more selected items like this:
	 * <pre>
	 *     {@code
	 *         MFXTreeItem<String> root = new MFXTreeItem<>("ROOT");
	 *         MFXTreeItem<String> i1 = new MFXTreeItem<>("I1");
	 *         MFXTreeItem<String> i1a = new MFXTreeItem<>("I1A");
	 *         MFXTreeItem<String> i2 = new MFXTreeItem<>("I1B");
	 *
	 *         root.setSelected(true);
	 *         i1.setSelected(true);
	 *         i1a.setSelected(true);
	 *         i2.setSelected(true);
	 *     }
	 * </pre>
	 * <p>
	 * If the model is set to not allow multiple selection then we clear the list
	 * and then add the item to it.
	 *
	 * @param item the item to select
	 * @see MFXTreeItem
	 */
	@SuppressWarnings("unchecked")
	protected void select(AbstractMFXTreeItem<T> item) {
		if (!allowsMultipleSelection) {
			clearSelection();
			selectedItems.setAll(item);
		} else {
			selectedItems.add(item);
		}
	}

	//================================================================================
	// Methods Implementation
	//================================================================================

	/**
	 * If you set some item to be selected before the tree is laid out then it's needed
	 * to scan the tree and add all the selected items to the list.
	 */
	@Override
	public void scanTree(AbstractMFXTreeItem<T> item) {
		TreeItemStream.flattenTree(item).forEach(treeItem -> {
			if (treeItem.isSelected() && !selectedItems.contains(treeItem)) {
				select(treeItem);
			}
		});
	}

	/**
	 * This method is called by {@link io.github.palexdev.materialfx.skins.MFXTreeItemSkin} when
	 * the mouse is pressed on the item. We need the mouse event as a parameter in case multiple selection is
	 * allowed because we need to check if the Shift key or Ctrl key were pressed.
	 * <p>
	 * If the mouseEvent is null we call the other {@link #select(AbstractMFXTreeItem)} method.
	 * <p>
	 * If the selection is single {@link #clearSelection()} we clear the selection
	 * and add the new selected item to the list.
	 * <p>
	 * If the selection is multiple we check if the item was already selected,
	 * if that is the case by default the item is deselected.
	 * <p>
	 * In case neither Shift nor Ctrl are pressed we clear the selection.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void select(AbstractMFXTreeItem<T> item, MouseEvent mouseEvent) {
		if (mouseEvent == null) {
			select(item);
			return;
		}

		if (!allowsMultipleSelection) {
			clearSelection();
			selectedItems.setAll(item);
			return;
		}


		if (mouseEvent.isShiftDown() || mouseEvent.isControlDown()) {
			if (item.isSelected()) {
				selectedItems.remove(item);
			} else {
				selectedItems.add(item);
			}
		} else {
			clearSelection();
			selectedItems.setAll(item);
		}
	}

	/**
	 * Resets every item in the list to selected false and then clears the list.
	 */
	@Override
	public void clearSelection() {
		if (selectedItems.isEmpty()) {
			return;
		}

		selectedItems.forEach(item -> item.setSelected(false));
		selectedItems.clear();
	}

	/**
	 * Gets the selected item. If the selection is multiple {@link #getSelectedItems()} should be
	 * called instead, as this method will only return the first item of the list.
	 *
	 * @return the first selected item of the list
	 */
	@Override
	public AbstractMFXTreeItem<T> getSelectedItem() {
		if (selectedItems.isEmpty()) {
			return null;
		}
		return selectedItems.get(0);
	}

	/**
	 * @return the ListProperty which contains all the selected items.
	 */
	@Override
	public ListProperty<AbstractMFXTreeItem<T>> getSelectedItems() {
		return this.selectedItems;
	}

	/**
	 * @return true if allows multiple selection, false if not.
	 */
	@Override
	public boolean allowsMultipleSelection() {
		return allowsMultipleSelection;
	}

	/**
	 * Sets the selection mode of the model, single or multiple.
	 */
	@Override
	public void setAllowsMultipleSelection(boolean multipleSelection) {
		this.allowsMultipleSelection = multipleSelection;
	}
}
