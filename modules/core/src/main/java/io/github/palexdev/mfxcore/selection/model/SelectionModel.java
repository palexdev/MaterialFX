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

package io.github.palexdev.mfxcore.selection.model;

import java.util.*;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.collections.RefineList;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/// Implementation of [ISelectionModel] that either works on a [ObservableList] or a [ListProperty] as the source.
///
/// Because the source is a dependency, this implementation automatically 'fixes' the selection when the source changes
/// using a [ListChangeHelper].
///
/// The backing map used to keep track of the selection is a [LinkedHashMap], so the selection order is preserved.
/// If you want to change this, you can simply override the two methods: [#newMap()] and [#newMap(Map)].
///
/// To make operation on the selection appear as 'atomic', most of them are performed on a temporary map created by the
/// aforementioned methods. At the end, the selection is replaced with the new map. See [MapProperty].
///
/// #### RefineList sources
///
/// When the source is a [RefineList], the items property stores its source list instead, so that indices are tracked in
/// source-space and the [ListChangeHelper] only ever sees physical additions/removals, never filter or sort changes.
/// Indexes accepted and reported by the public API stay in view-space; a [RefineSelectionShim] translates them, and owns
/// the whole operation whenever it is present.
@SuppressWarnings("unchecked")
public class SelectionModel<T> implements ISelectionModel<T> {
    //================================================================================
    // Static Properties
    //================================================================================

    protected static final IntegerRange INVALID_RANGE = IntegerRange.of(-1);

    //================================================================================
    // Properties
    //================================================================================

    private final ListProperty<T> items = new SimpleListProperty<>() {
        @Override
        public void set(ObservableList<T> newValue) {
            if (newValue instanceof RefineList<T> rl) {
                shim = new RefineSelectionShim<>(SelectionModel.this, rl);
                super.set(rl.getSource());
            } else {
                shim = null;
                super.set(newValue);
            }
        }
    };
    private RefineSelectionShim<T> shim;

    private final MapProperty<Integer, T> selection = new SimpleMapProperty<>(newMap());
    protected SequencedMap<Integer, T> backingMap;
    private boolean allowsMultipleSelection = true;

    private Function<ISelectionModel<T>, SelectionEventHandler> ehSupplier = sm ->
        sm.allowsMultipleSelection() ? new MultipleSelectionHandler(sm) : new SingleSelectionHandler(sm);
    private SelectionEventHandler eh = ehSupplier.apply(this);

    private final List<Disposable> disposables = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================

    public SelectionModel(ObservableList<T> items) {
        this.items.set(items);
        init();
    }

    public SelectionModel(ListProperty<T> list) {
        this(list.get());
        disposables.add(onInvalidated(list).then(items::set).listen());
    }

    //================================================================================
    // Methods
    //================================================================================

    protected void init() {
        disposables.add(new ListChangeHelper<>(items)
            .setOnClear(this::clearSelection)
            .setOnPermutation(p -> replaceSelection(
                selection.keySet().stream()
                    .map(p::get)
                    .filter(Objects::nonNull)
                    .toArray(Integer[]::new)
            ))
            .setOnReplace(rep -> {
                if (selection.containsKey(rep))
                    selection.put(rep, items.get(rep));
            })
            .setOnRemoved(rem -> {
                List<Integer> updated = ListChangeHelper.shiftOnRemove(selection.keySet(), rem, rem.first());
                if (updated.isEmpty()) clearSelection();
                else replaceSelection(updated.toArray(Integer[]::new));
            })
            .setOnAdded(add -> {
                List<Integer> updated = ListChangeHelper.shiftOnAdd(selection.keySet(), add);
                replaceSelection(updated.toArray(Integer[]::new));
            }).init());
    }

    protected ObservableMap<Integer, T> newMap() {
        this.backingMap = new LinkedHashMap<>();
        return FXCollections.observableMap(backingMap);
    }

    protected ObservableMap<Integer, T> newMap(Map<Integer, T> map) {
        this.backingMap = new LinkedHashMap<>(map);
        return FXCollections.observableMap(backingMap);
    }

    protected IntegerRange clampRange(IntegerRange range) {
        return items.isEmpty() ? INVALID_RANGE : IntegerRange.of(
            Math.max(0, range.getMin()),
            Math.min(items.size() - 1, range.getMax())
        );
    }

    public ObservableList<T> getItems() {
        return FXCollections.unmodifiableObservableList(items);
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < items.size();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public boolean contains(int index) {
        if (shim != null) return shim.contains(index);
        return selection.containsKey(index);
    }

    /// {@inheritDoc}
    ///
    /// @see Map#containsValue(Object)
    @Override
    public boolean contains(T element) {
        return selection.containsValue(element);
    }

    @Override
    public void clearSelection() {
        selection.set(newMap());
    }

    @Override
    public void deselectIndex(int index) {
        if (shim != null) {
            shim.deselectIndex(index);
            return;
        }
        selection.remove(index);
    }

    @Override
    public void deselectIndexes(int... indexes) {
        if (shim != null) {
            shim.deselectIndexes(indexes);
            return;
        }
        ObservableMap<Integer, T> tmp = newMap(selection);
        for (int index : indexes) {
            tmp.remove(index);
        }
        selection.set(tmp);
    }

    @Override
    public void deselectIndexes(IntegerRange range) {
        if (shim != null) {
            shim.deselectRange(range);
            return;
        }
        range = clampRange(range);
        if (INVALID_RANGE.equals(range)) return;
        ObservableMap<Integer, T> tmp = newMap(selection);
        for (Integer index : range) {
            tmp.remove(index);
        }
        selection.set(tmp);
    }

    /// {@inheritDoc}
    ///
    /// Uses [List#indexOf(Object)]!
    @Override
    public void deselectItem(T item) {
        int index = items.indexOf(item);
        if (index != -1) {
            selection.remove(index);
        }
    }

    /// {@inheritDoc}
    ///
    /// Uses [List#indexOf(Object)] on each item!!
    @Override
    public void deselectItems(T... items) {
        ObservableMap<Integer, T> tmp = newMap(selection);
        for (T item : items) {
            int index = this.items.indexOf(item);
            if (isValidIndex(index)) tmp.remove(index);
        }
        selection.set(tmp);
    }

    @Override
    public void selectIndex(int index) {
        if (shim != null) {
            shim.selectIndex(index);
            return;
        }
        if (!isValidIndex(index)) return;
        T item = items.get(index);
        if (allowsMultipleSelection) {
            selection.put(index, item);
        } else {
            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void selectIndexes(Integer... indexes) {
        if (shim != null) {
            shim.selectIndexes(indexes);
            return;
        }
        if (indexes.length == 0) return;
        if (allowsMultipleSelection) {
            Map<Integer, T> newSelection = Arrays.stream(indexes)
                .filter(this::isValidIndex)
                .collect(toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
                    LinkedHashMap::new
                ));
            selection.putAll(newSelection);
        } else {
            selectIndex(indexes[indexes.length - 1]);
        }
    }

    @Override
    public void selectIndexes(IntegerRange range) {
        if (shim != null) {
            shim.selectRange(range);
            return;
        }
        range = clampRange(range);
        if (INVALID_RANGE.equals(range)) return;
        if (allowsMultipleSelection) {
            Map<Integer, T> newSelection = range.stream()
                .filter(this::isValidIndex)
                .collect(toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
                    LinkedHashMap::new
                ));
            selection.putAll(newSelection);
        } else {
            selectIndex(range.getMax());
        }
    }

    /// {@inheritDoc}
    ///
    /// Uses [List#indexOf(Object)]!
    @Override
    public void selectItem(T item) {
        if (shim != null) {
            shim.selectItem(item);
            return;
        }
        selectIndex(items.indexOf(item));
    }

    /// {@inheritDoc}
    ///
    /// Uses [List#indexOf(Object)] on each item!!
    @Override
    public void selectItems(T... items) {
        if (shim != null) {
            shim.selectItems(items);
            return;
        }
        if (items.length == 0) return;
        if (allowsMultipleSelection) {
            Map<Integer, T> newSelection = new LinkedHashMap<>();
            for (T item : items) {
                int index = this.items.indexOf(item);
                if (isValidIndex(index)) newSelection.put(index, item);
            }
            selection.putAll(newSelection);
        } else {
            T item = items[items.length - 1];
            int index = this.items.indexOf(item);
            selectIndex(index);
        }
    }

    @Override
    public void expandSelection(int index, boolean fromLast) {
        if (shim != null) {
            shim.expandSelection(index, fromLast);
            return;
        }
        if (selection.isEmpty()) {
            replaceSelection(IntegerRange.of(0, index));
            return;
        }

        if (fromLast) {
            Map.Entry<Integer, T> last = backingMap.lastEntry();
            Integer lastIndex = last.getKey();
            int min = Math.min(lastIndex, index);
            int max = Math.max(lastIndex, index);
            selectIndexes(IntegerRange.of(min, max));
            return;
        }

        int min = selection.keySet().stream()
            .min(Integer::compareTo)
            .orElse(-1);
        if (index == min) {
            replaceSelection(index);
            return;
        }

        if (index < min) {
            replaceSelection(IntegerRange.of(index, min));
        } else {
            replaceSelection(IntegerRange.of(min, index));
        }
    }

    @Override
    public void replaceSelection(Integer... indexes) {
        if (shim != null) {
            shim.replaceIndexes(indexes);
            return;
        }
        if (indexes.length == 0) return;
        if (allowsMultipleSelection) {
            ObservableMap<Integer, T> newSelection = Arrays.stream(indexes)
                .filter(this::isValidIndex)
                .collect(toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
                    this::newMap
                ));
            selection.set(newSelection);
        } else {
            Integer index = indexes[indexes.length - 1];
            selectIndex(index);
        }
    }

    @Override
    public void replaceSelection(IntegerRange range) {
        if (shim != null) {
            shim.replaceRange(range);
            return;
        }
        range = clampRange(range);
        if (INVALID_RANGE.equals(range)) return;
        if (allowsMultipleSelection) {
            ObservableMap<Integer, T> newSelection = range.stream()
                .filter(this::isValidIndex)
                .collect(toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
                    this::newMap
                ));
            selection.set(newSelection);
        } else {
            selectIndex(range.getMax());
        }
    }

    /// {@inheritDoc}
    ///
    /// Uses [List#indexOf(Object)] on each item!!
    @Override
    public void replaceSelection(T... items) {
        if (shim != null) {
            shim.replaceItems(items);
            return;
        }
        if (items.length == 0) return;
        if (allowsMultipleSelection) {
            ObservableMap<Integer, T> newSelection = newMap();
            for (T item : items) {
                int index = this.items.indexOf(item);
                if (index != -1) newSelection.put(index, item);
            }
            selection.set(newSelection);
        } else {
            T item = items[items.length - 1];
            int index = this.items.indexOf(item);
            selectIndex(index);
        }
    }

    @Override
    public MapProperty<Integer, T> selection() {
        return selection;
    }

    @Override
    public List<T> getSelectedItems() {
        return List.copyOf(selection.values());
    }

    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    /// {@inheritDoc}
    ///
    /// The selection is also cleared!
    @Override
    public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
        // Clear selection when switching modes
        if (this.allowsMultipleSelection != allowsMultipleSelection) {
            this.allowsMultipleSelection = allowsMultipleSelection;
            clearSelection();
            eh = ehSupplier.apply(this);
        }
    }

    @Override
    public SelectionEventHandler eventHandler() {
        return eh;
    }

    @Override
    public void setEventHandler(Function<ISelectionModel<T>, SelectionEventHandler> fn) {
        ehSupplier = fn;
        eh = ehSupplier.apply(this);
    }

    @Override
    public void dispose() {
        ehSupplier = null;
        eh = null;
        shim = null;
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        selection.clear();
        selection.set(null);
    }
}
