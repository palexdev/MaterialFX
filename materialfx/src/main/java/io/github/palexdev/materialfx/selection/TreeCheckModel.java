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

import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.selection.base.ITreeCheckModel;
import io.github.palexdev.materialfx.utils.TreeItemStream;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.palexdev.materialfx.controls.MFXCheckTreeItem.CheckTreeItemEvent;

/**
 * Concrete implementation of the {@code ITreeCheckModel} interface.
 * <p>
 * This provides common methods for items check. Also, since it extends TreeSelectionModel it also provides
 * all the methods for items selection.
 * <p>
 * The check should be handled internally only. This is because the mechanism is kind of tricky.
 * If you take a look at the MFXCheckTreeItem's skin, {@link io.github.palexdev.materialfx.skins.MFXCheckTreeItemSkin},
 * you can see that when the checkbox is fired, a CHECK_EVENT is fired and "travels" up to the root. Each item then calls
 * {@link #check(MFXCheckTreeItem, CheckTreeItemEvent)}.
 */
public class TreeCheckModel<T> extends TreeSelectionModel<T> implements ITreeCheckModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ListProperty<MFXCheckTreeItem<T>> checkedItems = new SimpleListProperty<>(FXCollections.observableArrayList());

	//================================================================================
	// Constructors
	//================================================================================
	public TreeCheckModel() {
		super();
		checkedItems.addListener((ListChangeListener<MFXCheckTreeItem<T>>) change -> {
			List<MFXCheckTreeItem<T>> tmpRemoved = new ArrayList<>();
			List<MFXCheckTreeItem<T>> tmpAdded = new ArrayList<>();

			while (change.next()) {
				tmpRemoved.addAll(change.getRemoved());
				tmpAdded.addAll(change.getAddedSubList());
			}
			tmpRemoved.forEach(item -> item.setChecked(false));
			tmpAdded.forEach(item -> item.setChecked(true));
		});
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This method is called when the event argument passed to {@link #check(MFXCheckTreeItem, CheckTreeItemEvent)}
	 * is null. It is used for example when you want the tree to start with one or more checked items like this:
	 * <pre>
	 *     {@code
	 *         MFXCheckTreeItem<String> root = new MFXCheckTreeItem<>("ROOT");
	 *         MFXCheckTreeItem<String> i1 = new MFXCheckTreeItem<>("I1");
	 *         MFXCheckTreeItem<String> i1a = new MFXCheckTreeItem<>("I1A");
	 *         MFXCheckTreeItem<String> i2 = new MFXCheckTreeItem<>("I1B");
	 *
	 *         i1.setChecked(true);
	 *         i1a.setChecked(true);
	 *         i2.setChecked(true);
	 *     }
	 * </pre>
	 *
	 * @param item the item to check
	 * @see MFXCheckTreeItem
	 */
	private void check(MFXCheckTreeItem<T> item) {
		if (item.isChecked()) {
			checkedItems.remove(item);
		} else {
			checkedItems.add(item);
		}
	}

	/**
	 * Adds all the passed items to the checkedItems list.
	 */
	private void checkAll(List<MFXCheckTreeItem<T>> items) {
		checkedItems.addAll(items);
	}

	/**
	 * Removes all the passed items from the checkedItems list.
	 */
	private void uncheckAll(List<MFXCheckTreeItem<T>> items) {
		checkedItems.removeAll(items);
	}

	/**
	 * Counts all the checked children of the passed item.
	 */
	private int checkedChildren(MFXCheckTreeItem<T> item) {
		int cnt = 0;
		for (AbstractMFXTreeItem<T> treeItem : item.getItems()) {
			MFXCheckTreeItem<T> cItem = (MFXCheckTreeItem<T>) treeItem;
			if (cItem.isChecked()) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * Counts all the indeterminate children of the passed item.
	 */
	private int indeterminateChildren(MFXCheckTreeItem<T> item) {
		int cnt = 0;
		for (AbstractMFXTreeItem<T> treeItem : item.getItems()) {
			MFXCheckTreeItem<T> cItem = (MFXCheckTreeItem<T>) treeItem;
			if (cItem.isIndeterminate()) {
				cnt++;
			}
		}
		return cnt;
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * If you set some item to be checked before the tree is laid out then it's needed
	 * to scan the tree and add all the checked items to the list.
	 */
	@Override
	public void scanTree(MFXCheckTreeItem<T> item) {
		TreeItemStream.flattenTree(item).forEach(treeItem -> {
			MFXCheckTreeItem<T> cItem = (MFXCheckTreeItem<T>) treeItem;
			if (cItem.isChecked() && !checkedItems.contains(cItem)) {
				check(cItem, null);
			}
		});
	}

	/**
	 * This method is called by {@link io.github.palexdev.materialfx.skins.MFXCheckTreeItemSkin} when
	 * the checkbox is fired. We need the event as a parameter to distinguish between the item
	 * on which the CHECK_EVENT was fired and the parent items.
	 * <p>
	 * If the event is null we call the other {@link #check(MFXCheckTreeItem)} method.
	 */
	@Override
	public void check(MFXCheckTreeItem<T> item, CheckTreeItemEvent<?> event) {
		if (event == null) {
			List<MFXCheckTreeItem<T>> items = TreeItemStream.flattenTree(item).map(cItem -> (MFXCheckTreeItem<T>) cItem).collect(Collectors.toList());
			checkAll(items);
			return;
		}

		if (event.getItemRef() != null && event.getItemRef() == item) {
			List<MFXCheckTreeItem<T>> items = TreeItemStream.flattenTree(item).map(cItem -> (MFXCheckTreeItem<T>) cItem).collect(Collectors.toList());
			if (!item.isChecked() || item.isIndeterminate()) {
				checkAll(items);
			} else {
				uncheckAll(items);
			}
			return;
		}

		if (checkedChildren(item) == item.getItems().size()) {
			item.setIndeterminate(false);
			check(item);
		} else if (indeterminateChildren(item) != 0) {
			checkedItems.remove(item);
			item.setIndeterminate(true);
		} else if (checkedChildren(item) == 0) {
			checkedItems.remove(item);
			item.setIndeterminate(false);
		} else {
			checkedItems.remove(item);
			item.setIndeterminate(true);
		}
	}

	/**
	 * Resets every item in the list to checked false and then clears the list.
	 */
	@Override
	public void clearChecked() {
		if (checkedItems.isEmpty()) {
			return;
		}

		checkedItems.forEach(item -> item.setChecked(false));
		checkedItems.clear();
	}

	/**
	 * @return the ListProperty which contains all the checked items.
	 */
	@Override
	public ListProperty<MFXCheckTreeItem<T>> getCheckedItems() {
		return this.checkedItems;
	}
}
