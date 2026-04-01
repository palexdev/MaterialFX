/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.*;

import io.github.palexdev.mfxcore.enums.SelectionMode;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/// Custom implementation and expansion of that pitiful thing that is [javafx.scene.control.ToggleGroup].
///
/// A `SelectionGroup` will work with anything that implements the necessary API described by the [Selectable]
/// interface. Not only that, it is also a lot more flexible and convenient.
///
/// You can set the selection to be single or multiple, by just setting the [#selectionModeProperty()], as well as
/// tell the group to always keep at least one [Selectable] active, by setting the [#atLeastOneSelectedProperty()]
/// to true. <br >
/// So you now have a grouping API for everything, not only controls, as long as they implement [Selectable], and you
/// also have capabilities such as 'at most/at least one selected', in single and multiple configurations, in just one class!
///
/// #### Caveats
/// - If you want to use this, you will be forced to use the two custom properties: [SelectionProperty] and [SelectionGroupProperty].
///   The reason for this is to make the implementation/integration for users less of a pain and less error-prone,
///   more info can be found in the relative classes' docs.
/// - Since this also supports multiple selection, for obvious reasons, the selection is a collection of `Selectables`.
///   To be precise, both the collections used to keep the `Selectables` that are managed by the group, and the ones
///   that are currently selected, are [SetProperties][SetProperty] backed by a [LinkedHashSet].
///   The usage of such collections vastly helps to avoid duplicates while also having fast insertions, removals and lookups.
///
/// ##### Behavior
/// - When switching from [SelectionMode#MULTIPLE] to [SelectionMode#SINGLE] the selection is cleared.
///   If [#isAtLeastOneSelected()] is also active then the first entry in the group will be selected.
/// - When activating the 'atLeastOneSelected' mode, if there are `Selectables` in the group the first will
///   be immediately selected! If none is available, the first added to the group will be.
/// - When `atLeastOneSelected` is active, attempts to deselect the selected entry will be blocked.
/// - When mode is [SelectionMode#SINGLE] and there's already one entry selected, adding new entries will force them to
///   "well-behave", meaning that if their state is 'selected', they will be flipped back to 'unselected', to respect
///   the current selection.
///
/// ##### Implementation Details
/// - The group is optimized to perform the bare minimum operations required to update the state and to make them appear
///   to the user as a single operation/"atomic".<br >
///   In other words, changes to the selection are copy-on-write, the `Set` is first copied (or created empty), modified,
///   and then set as the new selection.
/// - Because [Selectables][Selectable] must follow the group's rules, their selection state may need to be adjusted while
///   the group is transitioning to a new state. While any operation on the group is occurring, it is `locked`.
///   The [SelectionProperty] and [SelectionGroupProperty] will see this and bypass group's methods (which would lead
///   to a circular execution, `StackOverflowException`). The logic is very simple actually:
///   - If the group is locked, it means that the state transition request came from the group, so just call `super.set(...)`
///   - If the group is _not_ locked, it means the state transition came from the user, but the request may not be honored
///     as it may break the group's rules, therefore before delegating to `super.set(...)` it needs to consult the group
public class SelectionGroup {

    //================================================================================
    // Properties
    //================================================================================

    private final ObjectProperty<SelectionMode> selectionMode = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            SelectionMode mode = get();
            handler = mode == SelectionMode.SINGLE ? new SingleSelectionHandler() : new MultipleSelectionHandler();
            if (mode == SelectionMode.SINGLE) clearSelection();
        }
    };
    private final BooleanProperty atLeastOneSelected = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            if (get() && isSelectionEmpty())
                selectables.getFirst().ifPresent(s -> {
                    try {
                        locked = true;
                        s.setSelected(handler.handleSelect(s, true));
                    } finally {
                        locked = false;
                    }
                });
        }
    };
    private final SelectablesSet selectables = new SelectablesSet();
    private final SelectablesSet selection = new SelectablesSet();
    private SelectionHandler handler;
    private boolean locked = false;

    //================================================================================
    // Constructors
    //================================================================================

    public SelectionGroup() {
        this(SelectionMode.SINGLE);
    }

    public SelectionGroup(SelectionMode selectionMode) {
        setSelectionMode(selectionMode);
    }

    public SelectionGroup(SelectionMode selectionMode, boolean atLeastOneSelected) {
        setSelectionMode(selectionMode);
        setAtLeastOneSelected(atLeastOneSelected);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// @see SelectionHandler#check(Selectable, boolean)
    public boolean check(Selectable selectable, boolean state) {
        return handler.check(selectable, state);
    }

    /// Locks the group and delegates to [SelectionHandler#handleSelect(Selectable, boolean)].
    public boolean select(Selectable selectable, boolean state) {
        try {
            locked = true;
            return handler.handleSelect(selectable, state);
        } finally {
            locked = false;
        }
    }

    /// Locks the group and delegates to [SelectionHandler#handleAdd(Selectable...)]
    protected void handleAdd(Selectable... selectables) {
        try {
            locked = true;
            handler.handleAdd(selectables);
        } finally {
            locked = false;
        }
    }

    /// Locks the group and delegates to [SelectionHandler#handleRemoval(Selectable...)]
    protected void handleRemoval(Selectable... selectables) {
        try {
            locked = true;
            handler.handleRemoval(selectables);
        } finally {
            locked = false;
        }
    }

    public SelectionGroup add(Selectable... selectables) {
        handleAdd(selectables);
        return this;
    }

    public SelectionGroup addAll(Collection<? extends Selectable> selectables) {
        return add(selectables.toArray(Selectable[]::new));
    }

    public SelectionGroup remove(Selectable... selectables) {
        handleRemoval(selectables);
        return this;
    }

    public SelectionGroup removeAll(Collection<? extends Selectable> selectables) {
        return remove(selectables.toArray(Selectable[]::new));
    }

    /// Clears the group's selection. If [#isAtLeastOneSelected()] is `true` the first [Selectable] in [#getSelectables()]
    /// will be selected.
    public void clearSelection() {
        try {
            locked = true;
            handler.clearSelection();
        } finally {
            locked = false;
        }
    }

    /// Removes all [Selectables][Selectable] from the group, preserving their selection state.
    public SelectionGroup clearGroup() {
        return removeAll(selectables);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return whether a state transition is occurring on the group
    public boolean locked() {
        return locked;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    /// Specifies the group's [SelectionMode]
    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode mode) {
        this.selectionMode.set(mode);
    }

    public boolean isAtLeastOneSelected() {
        return atLeastOneSelected.get();
    }

    /// Specifies whether at least one [Selectable] from the [selectables Set][#getSelectables()] should be selected at
    /// all times.
    public BooleanProperty atLeastOneSelectedProperty() {
        return atLeastOneSelected;
    }

    public void setAtLeastOneSelected(boolean atLeastOneSelected) {
        this.atLeastOneSelected.set(atLeastOneSelected);
    }

    /// @return the `Set` of managed [Selectables][Selectable] as a [ReadOnlySetProperty]
    public ReadOnlySetProperty<Selectable> getSelectables() {
        return selectables;
    }

    /// @return the group's current selection as a [ReadOnlySetProperty]
    public ReadOnlySetProperty<Selectable> getSelection() {
        return selection;
    }

    /// @return the group's current selection as an unmodifiable list
    public List<Selectable> getSelectionList() {
        return List.copyOf(selection.backingSet);
    }

    /// @return the first selected entry as an [Optional] (may be absent)
    public Optional<Selectable> getFirstSelected() {
        return selection.getFirst();
    }

    /// @return the last selected entry as an [Optional] (may be absent or same as [#getFirstSelected()])
    public Optional<Selectable> getLastSelected() {
        return selection.getLast();
    }

    public int selectionSize() {
        return selection.size();
    }

    public ReadOnlyIntegerProperty selectionSizeProperty() {
        return selection.sizeProperty();
    }

    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }

    public ReadOnlyBooleanProperty selectionEmptyProperty() {
        return selection.emptyProperty();
    }

    public int groupSize() {
        return selectables.size();
    }

    public ReadOnlyIntegerProperty groupSizeProperty() {
        return selectables.sizeProperty();
    }

    public boolean isGroupEmpty() {
        return selectables.isEmpty();
    }

    public ReadOnlyBooleanProperty groupEmptyProperty() {
        return selectables.emptyProperty();
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    interface SelectionHandler {

        /// Checks whether the given [Selectable] is allowed to transition to the given selection state.
        ///
        /// If it is not part of the group, it returns the given state.<br >
        ///
        /// Example: an entry that wants to go to `false`, in a group in [SelectionMode#SINGLE] mode and with [#isAtLeastOneSelected()]
        /// will be negated and return `true` (stay selected)
        boolean check(Selectable selectable, boolean state);

        /// Adds or removes the given [Selectable] from the group's selection after checking the request is valid with
        /// [#check(Selectable, boolean)].
        ///
        /// Depending on the [SelectionMode], the [Selectable] can be added/removed or the selection cleared and replaced.
        boolean handleSelect(Selectable selectable, boolean state);

        /// Adds the given [Selectables][Selectable] to the group, ensuring that the [#isAtLeastOneSelected()] constraint
        /// is honored.
        ///
        /// In [SelectionMode#SINGLE] mode, if the [Selectable] is already selected, it will be flipped to unselected if
        /// there is already something else selected.
        void handleAdd(Selectable... selectables);

        /// Removes the given [Selectables][Selectable] from the group, ensuring that the [#isAtLeastOneSelected()]
        /// constraint is honored.
        void handleRemoval(Selectable... selectables);

        /// Deselects all group's entries while ensuring that the [#isAtLeastOneSelected()] constraint is honored.
        void clearSelection();
    }

    class SingleSelectionHandler implements SelectionHandler {

        @Override
        public boolean check(Selectable selectable, boolean state) {
            if (!selectables.contains(selectable)) return state;
            if (!state) {
                if (isAtLeastOneSelected()) {
                    // returns true if selected and therefore cannot flip to unselected state
                    return selection.getFirst().filter(s -> s == selectable).isPresent();
                }
            }
            return state;
        }

        @Override
        public boolean handleSelect(Selectable selectable, boolean state) {
            // check that requested state is allowed
            // exit if final state is equal to current one
            state = check(selectable, state);
            if (selectable.isSelected() == state) return state;

            ObservableSet<Selectable> copy = selection.copy();
            if (state) {
                copy.stream().filter(Selectable::isSelected)
                    .findFirst()
                    .ifPresent(s -> s.setSelected(false));
                copy.clear();
                copy.add(selectable);
                selection.set(copy);
                return true;
            }

            copy.remove(selectable);
            selection.set(copy);
            return false;
        }

        @Override
        public void handleAdd(Selectable... selectables) {
            if (selectables.length == 0) return;
            Selectable firstSelected = null;
            for (Selectable selectable : selectables) {
                SelectionGroup.this.selectables.add(selectable);
                selectable.setSelectionGroup(SelectionGroup.this);
                if (selectable.isSelected() && firstSelected == null) {
                    firstSelected = selectable;
                    continue;
                }
                selectable.setSelected(false);
            }

            if (isSelectionEmpty() && (isAtLeastOneSelected() || firstSelected != null)) {
                Optional.ofNullable(firstSelected)
                    .or(SelectionGroup.this.selectables::getFirst)
                    .ifPresent(s -> {
                        s.setSelected(true);
                        selection.add(s);
                    });
            } else if (firstSelected != null) {
                firstSelected.setSelected(false);
            }
        }

        @Override
        public void handleRemoval(Selectable... selectables) {
            if (selectables.length == 0) return;
            boolean needsToUpdate = false;
            for (Selectable selectable : selectables) {
                SelectionGroup.this.selectables.remove(selectable);
                needsToUpdate |= selection.contains(selectable);
                selectable.setSelectionGroup(null);
            }

            ObservableSet<Selectable> copy;
            if (needsToUpdate) {
                copy = selection.copy();
                copy.removeIf(s -> !SelectionGroup.this.selectables.contains(s));

                if (isAtLeastOneSelected() && copy.isEmpty()) {
                    SelectionGroup.this.selectables.getFirst().ifPresent(s -> {
                        s.setSelected(true);
                        copy.add(s);
                    });
                }
                selection.set(copy);
            }
        }

        @Override
        public void clearSelection() {
            if (isGroupEmpty()) return;
            Selectable first = selectables.getFirst().filter(Selectable::isSelected).orElse(null);
            for (Selectable selectable : selectables) {
                if (selectable == first) continue;
                selectable.setSelected(false);
            }
            if (isSelectionEmpty() && !isAtLeastOneSelected()) return;

            ObservableSet<Selectable> empty = selection.newSet();
            if (isAtLeastOneSelected()) {
                Selectable toSelect = Optional.ofNullable(first).orElse(selectables.getFirst().get());
                toSelect.setSelected(true);
                empty.add(toSelect);
            } else if (first != null) {
                first.setSelected(false);
            }
            selection.set(empty);
        }
    }

    class MultipleSelectionHandler implements SelectionHandler {

        @Override
        public boolean check(Selectable selectable, boolean state) {
            if (!selectables.contains(selectable)) return state;
            if (!state) {
                if (selectionSize() <= 1 && isAtLeastOneSelected()) {
                    // returns true if selected and therefore cannot flip to unselected state
                    return selection.getFirst().filter(s -> s == selectable).isPresent();
                }
            }
            return state;
        }

        @Override
        public boolean handleSelect(Selectable selectable, boolean state) {
            // check that requested state is allowed
            // exit if final state is equal to current one
            state = check(selectable, state);
            if (selectable.isSelected() == state) return state;

            ObservableSet<Selectable> copy = selection.copy();
            if (state) {
                copy.add(selectable);
            } else {
                copy.remove(selectable);
            }
            selection.set(copy);
            return state;
        }

        @Override
        public void handleAdd(Selectable... selectables) {
            if (selectables.length == 0) return;
            List<Selectable> selected = new ArrayList<>();
            for (Selectable selectable : selectables) {
                if (SelectionGroup.this.selectables.add(selectable)) {
                    selectable.setSelectionGroup(SelectionGroup.this);
                    if (selectable.isSelected()) {
                        selected.add(selectable);
                    }
                }
            }

            if (!selected.isEmpty()) {
                ObservableSet<Selectable> copy = selection.copy();
                copy.addAll(selected);
                selection.set(copy);
                return;
            }

            if (isAtLeastOneSelected() && isSelectionEmpty()) {
                SelectionGroup.this.selectables.getFirst().ifPresent(s -> {
                    s.setSelected(true);
                    selection.add(s);
                });
            }
        }

        @Override
        public void handleRemoval(Selectable... selectables) {
            if (selectables.length == 0) return;
            boolean needsToUpdate = false;
            for (Selectable selectable : selectables) {
                SelectionGroup.this.selectables.remove(selectable);
                needsToUpdate |= selection.contains(selectable);
                selectable.setSelectionGroup(null);
            }

            ObservableSet<Selectable> copy;
            if (needsToUpdate) {
                copy = selection.copy();
                copy.removeIf(s -> !SelectionGroup.this.selectables.contains(s));

                if (isAtLeastOneSelected() && copy.isEmpty()) {
                    SelectionGroup.this.selectables.getFirst().ifPresent(s -> {
                        s.setSelected(true);
                        copy.add(s);
                    });
                }
                selection.set(copy);
            }
        }

        @Override
        public void clearSelection() {
            if (isGroupEmpty()) return;
            Selectable first = selectables.getFirst().filter(Selectable::isSelected).orElse(null);
            for (Selectable selectable : selectables) {
                if (selectable == first) continue;
                selectable.setSelected(false);
            }
            if (isSelectionEmpty() && !isAtLeastOneSelected()) return;

            ObservableSet<Selectable> empty = selection.newSet();
            if (isAtLeastOneSelected()) {
                Selectable toSelect = Optional.ofNullable(first).orElse(selectables.getFirst().get());
                toSelect.setSelected(true);
                empty.add(toSelect);
            } else if (first != null) {
                first.setSelected(false);
            }
            selection.set(empty);
        }
    }

    // Note: get operations must occur before the backing set is replaced (new or copy)
    private static class SelectablesSet extends SimpleSetProperty<Selectable> {
        private SequencedSet<Selectable> backingSet;

        public SelectablesSet() {
            set(newSet());
        }

        public Optional<Selectable> getFirst() {
            if (isEmpty()) return Optional.empty();
            return Optional.of(backingSet.getFirst());
        }

        public Optional<Selectable> getLast() {
            if (isEmpty()) return Optional.empty();
            return Optional.of(backingSet.getLast());
        }

        private ObservableSet<Selectable> newSet() {
            backingSet = new LinkedHashSet<>();
            return FXCollections.observableSet(backingSet);
        }

        private ObservableSet<Selectable> copy() {
            backingSet = new LinkedHashSet<>(this);
            return FXCollections.observableSet(backingSet);
        }
    }
}
