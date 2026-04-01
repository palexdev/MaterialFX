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

import java.util.function.Consumer;

/// Public API for components that are selectable (checks, radios, toggles and such).
///
/// Ideally, such controls should have two things:
///  1) A property for the selection state obviously
///  2) A way to group such controls, see [SelectionGroup]
///
/// Note how the API enforces the use of [SelectionProperty] and [SelectionGroupProperty].
///
/// The reason for this decision is that: compared to the pitiful JavaFX API, `SelectionGroups` allow multiple selection,
/// the possibility of always having at least one selection, as well as allowing the grouping of different components
/// as long as they implement this. Those custom properties handle some hassles (like what happens when switching groups,
/// or delegating the selection state to the group if the component is assigned to one) automatically so that neither developers
/// nor users need to worry about that. It's worth mentioning though that this is experimental, meaning that
/// the API may not take into account all the user cases, if such is the case, in the future the API may change and use
/// standard properties.
public interface Selectable {

    default boolean isSelected() {
        return selectedProperty().get();
    }

    /// Specifies the selection state.
    ///
    /// @see SelectionProperty
    SelectionProperty selectedProperty();

    default void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    /// Convenience method to flip the selection state.<br >
    /// Does nothing if the [#selectedProperty()] is bound.
    default void toggle() {
        if (!selectedProperty().isBound())
            setSelected(!isSelected());
    }

    default SelectionGroup getSelectionGroup() {
        return selectionGroupProperty().get();
    }

    /// Specifies the [SelectionGroup] at which this `Selectable` is assigned.
    SelectionGroupProperty selectionGroupProperty();

    default void setSelectionGroup(SelectionGroup group) {
        selectionGroupProperty().set(group);
    }

    /// Allows specifying a callback to invoke when the selection state of this `Selectable` changes.
    ///
    /// **This is an optional API. By default, throws an `UnsupportedOperationException`.**
    default void onSelectionChanged(Consumer<Boolean> action) {
        throw new UnsupportedOperationException("API not implemented by " + getClass());
    }

    /// Delegate to [#onSelectionChanged(Consumer)] that runs the given action only when selection state is `true`.
    default void onSelected(Runnable action) {
        onSelectionChanged(s -> {if (s) action.run();});
    }
}