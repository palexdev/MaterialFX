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

package io.github.palexdev.mfxcore.selection.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/// My own API for selection models that can both in single and multiple selection modes.
///
/// The selection is expressed through an observable map, which associates the selected data with its index in the source list.
/// As you may guess, operations on indexes are generally faster and, if possible, should be preferred over operations on data.
///
/// Additionally, it provides pre-defined handlers to manage selection on user input (mouse or keyboard), see [SelectionEventHandler].
@SuppressWarnings("unchecked")
public interface ISelectionModel<T> {

    /// Checks if the element associated with the given index is currently selected.
    boolean contains(int index);

    /// Checks if the given element is currently selected.
    boolean contains(T element);

    /// Clears the selection.
    void clearSelection();

    /// Deselects the element associated with the given index if present.
    void deselectIndex(int index);

    /// Deselects all the elements associated with the given indexes if present.
    void deselectIndexes(int... indexes);

    /// Deselects all the elements associated with the given range if present.
    void deselectIndexes(IntegerRange range);

    /// Deselects the given element if present.
    void deselectItem(T item);

    /// Deselects all the given elements if present.
    void deselectItems(T... items);

    /// Convenience method to select the first item in the list, delegates to [#selectIndex(int)].<br >
    default void selectFirst() {
        selectIndex(0);
    }

    /// Convenience method to select the last item in the list, delegates to [#selectIndex(int)].<br >
    default void selectLast() {
        selectIndex(size() - 1);
    }

    /// Selects the element associated with the given index if present.
    void selectIndex(int index);

    /// Selects all the elements associated with the given indexes if present.
    void selectIndexes(Integer... indexes);

    /// Selects all the elements associated with the given range if present.
    void selectIndexes(IntegerRange range);

    /// Delegates to [#selectIndexes(IntegerRange)] with `IntegerRange.of(0, Integer.MAX_VALUE)`.
    default void selectAll() {
        selectIndexes(IntegerRange.of(0, Integer.MAX_VALUE));
    }

    /// Selects the given element if present.
    void selectItem(T item);

    /// Selects all the given elements if present.
    void selectItems(T... items);

    /// Expands the selection depending on the `fromLast` parameter:
    /// - If `false`, the selection is expanded towards the given index.
    /// - If `true`, the last selected index is taken as reference, and the selection is replaced by the range that
    /// includes the last selected index and the given index (which is first/last depends on which is smaller).
    ///
    /// This is typically used when selecting with the _Shift_ key modifier.
    void expandSelection(int index, boolean fromLast);

    /// Replaces the selection with the given indexes and the associated elements.
    void replaceSelection(Integer... indexes);

    /// Replaces the selection with the given range and the associated elements.
    void replaceSelection(IntegerRange range);

    /// Replaces the selection with the given elements.
    void replaceSelection(T... items);

    /// @return the [ObservableMap] keeping track of the selected entries
    MapProperty<Integer, T> selection();

    /// @return the list of currently selected items
    List<T> getSelectedItems();

    /// Convenience method to retrieve the first selected item or `null` if the selection is empty.<br >
    /// (useful for single selection mode)
    default T getSelectedItem() {
        return (size() == 0) ? null : getSelectedItems().getFirst();
    }

    /// Wraps [#getSelectedItem] in an [Optional].
    default Optional<T> getSelectedItemOpt() {
        return Optional.ofNullable(getSelectedItem());
    }

    /// Convenience method to retrieve the last selected item or `null` if the selection is empty.
    default T getLastSelectedItem() {
        int size = size();
        return (size == 0) ? null : getSelectedItems().get(size - 1);
    }

    /// Wraps [#getLastSelectedItem] in an [Optional].
    default Optional<T> getLastSelectedItemOpt() {
        return Optional.ofNullable(getLastSelectedItem());
    }

    /// @return the number of selected items
    default int size() {
        return selection().size();
    }

    /// @return whether the selection is empty
    default boolean isEmpty() {
        return selection().isEmpty();
    }

    /// @return whether multiple selection is allowed
    boolean allowsMultipleSelection();

    /// Sets whether to allow multiple selection.
    void setAllowsMultipleSelection(boolean allowsMultipleSelection);

    /// @return the [SelectionEventHandler] implementation used to handle selection on user inputs (from mouse or keyboard)
    SelectionEventHandler eventHandler();

    /// Sets the [SelectionEventHandler] implementation used to handle selection on user inputs (from mouse or keyboard)
    /// from the given factory.
    void setEventHandler(Function<ISelectionModel<T>, SelectionEventHandler> fn);

    /// Disposes the selection model if needed.
    default void dispose() {}

    //================================================================================
    // Inner Classes
    //================================================================================

    /// A simple interface which handles mouse and keyboard interactions that may modify the selection state in a selection model.
    ///
    /// There are two concrete implementations: [SingleSelectionHandler] for single selection mode, and [MultipleSelectionHandler]
    /// for multiple selection mode.
    interface SelectionEventHandler {

        /// Handles the selection at the given index on a [MouseEvent] interaction.
        void handle(MouseEvent me, int index);

        /// Handles the selection at the given index on a [KeyEvent] interaction.
        void handle(KeyEvent ke, int index);
    }

    class SingleSelectionHandler implements SelectionEventHandler {
        private final ISelectionModel<?> sm;

        public SingleSelectionHandler(ISelectionModel<?> sm) {this.sm = sm;}

        @Override
        public void handle(MouseEvent me, int index) {
            if (me.getButton() != MouseButton.PRIMARY) return;
            boolean selected = sm.contains(index);
            if (selected) {
                sm.selectIndex(index);
            } else {
                sm.deselectIndex(index);
            }
        }

        @Override
        public void handle(KeyEvent ke, int index) {
            if (ke.getCode() == KeyCode.ENTER || ke.getCode() == KeyCode.SPACE) {
                boolean selected = sm.contains(index);
                if (selected) {
                    sm.selectIndex(index);
                } else {
                    sm.deselectIndex(index);
                }
            }
        }
    }

    class MultipleSelectionHandler implements SelectionEventHandler {
        private final ISelectionModel<?> sm;

        public MultipleSelectionHandler(ISelectionModel<?> sm) {this.sm = sm;}

        @Override
        public void handle(MouseEvent me, int index) {
            boolean shiftDown = me.isShiftDown();
            boolean ctrlDown = me.isControlDown();
            if (!shiftDown && ctrlDown) {
                boolean selected = sm.contains(index);
                if (selected) {
                    sm.deselectIndex(index);
                } else {
                    sm.selectIndex(index);
                }
                return;
            }

            if (shiftDown) {
                sm.expandSelection(index, ctrlDown);
                return;
            }
            sm.replaceSelection(index);
        }

        @Override
        public void handle(KeyEvent ke, int index) {
            if (ke.getCode() != KeyCode.ENTER && ke.getCode() != KeyCode.SPACE) return;
            boolean selected = sm.contains(index);
            if (selected) {
                sm.deselectIndex(index);
            } else {
                sm.selectIndex(index);
            }
        }
    }
}
