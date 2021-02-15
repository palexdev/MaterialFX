/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.selection.TreeCheckModelTree;

/**
 * This is the container for a tree made of MFXCheckTreeItems.
 * <p>
 * Note: this could also work with other item classes since the TreeCheckModelTree extends TreeTreeSelectionModel,
 * but of course it is not recommended to do so.
 * @param <T> The type of the data within the items.
 */
public class MFXCheckTreeView<T> extends MFXTreeView<T> {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckTreeView() {
        super();
    }

    public MFXCheckTreeView(MFXCheckTreeItem<T> root) {
        super(root);
    }

    //================================================================================
    // Methods
    //================================================================================
    public TreeCheckModelTree<T> getCheckModel() {
        return (TreeCheckModelTree<T>) super.getSelectionModel();
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Overridden method to install a TreeCheckModelTree.
     * <p>
     * By default it is set to allow multiple selection.
     */
    @Override
    protected void installSelectionModel() {
        TreeCheckModelTree<T> treeCheckModel = new TreeCheckModelTree<>();
        treeCheckModel.setAllowsMultipleSelection(true);
        setSelectionModel(treeCheckModel);
    }
}
