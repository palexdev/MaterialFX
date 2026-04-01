/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.selection;

import javafx.beans.property.SimpleObjectProperty;

/// Extension of [SimpleObjectProperty] meant to be used by classes implementing [Selectable].
///
/// This property overrides the [#set(SelectionGroup)] method to correctly handle the assignment/removal/switch of a
/// [Selectable] from a [SelectionGroup], see [#set(SelectionGroup)] for more info.
///
/// Note that for this purpose this property needs the reference of the [Selectable] in which it will operate
/// to be able to add/remove the `Selectable` to/from the [SelectionGroup].
public class SelectionGroupProperty extends SimpleObjectProperty<SelectionGroup> {
    //================================================================================
    // Properties
    //================================================================================
    private final Selectable selectable;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionGroupProperty(Selectable selectable) {
        this.selectable = selectable;
    }

    public SelectionGroupProperty(SelectionGroup initialValue, Selectable selectable) {
        super(initialValue);
        this.selectable = selectable;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to correctly handle the addition/removal of a [Selectable] to/from a [SelectionGroup].
    ///
    /// If the `Selectable` was already in a group, it first needs to be removed from it by calling
    /// [SelectionGroup#remove(Selectable)].
    ///
    /// Then it's added to the new group with [SelectionGroup#add(Selectable)] method is invoked.
    ///
    /// Calls to `super.set(...)` are issued only if the group is not locked, see [SelectionGroup] and [SelectionGroup#locked()].
    @Override
    public void set(SelectionGroup newValue) {
        SelectionGroup oldValue = get();
        if (oldValue != null && !oldValue.locked()) {
            oldValue.handleRemoval(selectable);
        }
        if (newValue != null) {
            if (!newValue.locked()) newValue.add(selectable);
            super.set(newValue);
            return;
        }
        super.set(null);
    }
}
