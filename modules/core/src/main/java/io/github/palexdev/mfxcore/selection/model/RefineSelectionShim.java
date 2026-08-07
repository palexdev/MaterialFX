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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.collections.RefineList;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/// Translation layer used by [SelectionModel] when its source is a [RefineList].
///
/// Public indexes are in view-space (filtered + sorted), while the selection map stays keyed by source indexes so that
/// predicate and comparator changes never perturb it. Every operation resolves the translation and then writes the
/// selection map directly: it must never call back into [SelectionModel]'s guarded methods, or the translation would be
/// applied twice.
class RefineSelectionShim<T> {

    //================================================================================
    // Properties
    //================================================================================

    private final SelectionModel<T> model;
    private final RefineList<T> view;

    //================================================================================
    // Constructors
    //================================================================================

    RefineSelectionShim(SelectionModel<T> model, RefineList<T> view) {
        this.model = model;
        this.view = view;
    }

    //================================================================================
    // Methods
    //================================================================================

    boolean contains(int index) {
        int src = toSource(index);
        return src >= 0 && model.selection().containsKey(src);
    }

    void selectIndex(int index) {
        int src = toSource(index);
        if (src < 0) return;
        if (model.allowsMultipleSelection()) {
            model.selection().put(src, view.get(index));
        } else {
            ObservableMap<Integer, T> map = model.newMap();
            map.put(src, view.get(index));
            model.selection().set(map);
        }
    }

    void selectIndexes(Integer... indexes) {
        if (indexes.length == 0) return;
        if (!model.allowsMultipleSelection()) {
            selectIndex(indexes[indexes.length - 1]);
            return;
        }
        model.selection().putAll(collect(List.of(indexes)));
    }

    void selectRange(IntegerRange range) {
        range = clamp(range);
        if (range == null) return;
        if (!model.allowsMultipleSelection()) {
            selectIndex(range.getMax());
            return;
        }
        model.selection().putAll(collect(IntegerRange.expandRangeToSet(range)));
    }

    void deselectIndex(int index) {
        int src = toSource(index);
        if (src >= 0) model.selection().remove(src);
    }

    void deselectIndexes(int... indexes) {
        ObservableMap<Integer, T> tmp = model.newMap(model.selection());
        for (int index : indexes) {
            int src = toSource(index);
            if (src >= 0) tmp.remove(src);
        }
        model.selection().set(tmp);
    }

    void deselectRange(IntegerRange range) {
        range = clamp(range);
        if (range == null) return;
        ObservableMap<Integer, T> tmp = model.newMap(model.selection());
        for (Integer index : range) {
            int src = toSource(index);
            if (src >= 0) tmp.remove(src);
        }
        model.selection().set(tmp);
    }

    void replaceIndexes(Integer... indexes) {
        if (indexes.length == 0) return;
        if (!model.allowsMultipleSelection()) {
            selectIndex(indexes[indexes.length - 1]);
            return;
        }
        replaceWith(collect(List.of(indexes)));
    }

    void replaceRange(IntegerRange range) {
        range = clamp(range);
        if (range == null) return;
        if (!model.allowsMultipleSelection()) {
            selectIndex(range.getMax());
            return;
        }
        replaceWith(collect(IntegerRange.expandRangeToSet(range)));
    }

    void expandSelection(int index, boolean fromLast) {
        if (index < 0 || index >= view.size()) return;
        if (model.selection().isEmpty()) {
            replaceRange(IntegerRange.of(0, index));
            return;
        }

        if (fromLast) {
            int anchor = toView(model.backingMap.lastEntry().getKey());
            if (anchor < 0) {
                selectIndex(index);
                return;
            }
            selectRange(IntegerRange.of(Math.min(anchor, index), Math.max(anchor, index)));
            return;
        }

        int min = minSelectedViewIndex();
        if (min < 0 || index == min) {
            replaceIndexes(index);
            return;
        }
        replaceRange(index < min ? IntegerRange.of(index, min) : IntegerRange.of(min, index));
    }

    // Item-based operations need no translation, but SelectionModel resolves them to source indexes and then feeds them
    // to the guarded index methods, which would translate them a second time. The shim owns them for that reason alone.

    void selectItem(T item) {
        int src = view.getSource().indexOf(item);
        if (!model.isValidIndex(src)) return;
        if (model.allowsMultipleSelection()) {
            model.selection().put(src, item);
        } else {
            ObservableMap<Integer, T> map = model.newMap();
            map.put(src, item);
            model.selection().set(map);
        }
    }

    void selectItems(T[] items) {
        if (items.length == 0) return;
        if (!model.allowsMultipleSelection()) {
            selectItem(items[items.length - 1]);
            return;
        }
        Map<Integer, T> add = new LinkedHashMap<>();
        ObservableList<T> src = view.getSource();
        for (T item : items) {
            int index = src.indexOf(item);
            if (model.isValidIndex(index)) add.put(index, item);
        }
        model.selection().putAll(add);
    }

    void replaceItems(T[] items) {
        if (items.length == 0) return;
        if (!model.allowsMultipleSelection()) {
            selectItem(items[items.length - 1]);
            return;
        }
        Map<Integer, T> replacement = new LinkedHashMap<>();
        ObservableList<T> src = view.getSource();
        for (T item : items) {
            int index = src.indexOf(item);
            if (model.isValidIndex(index)) replacement.put(index, item);
        }
        replaceWith(replacement);
    }

    private int toSource(int index) {
        if (index < 0 || index >= view.size()) return -1;
        return view.viewToSource(index);
    }

    private int toView(int sourceIndex) {
        int index = view.sourceToView(sourceIndex);
        return index < 0 ? -1 : index;
    }

    private int minSelectedViewIndex() {
        int min = -1;
        for (Integer src : model.selection().keySet()) {
            int index = toView(src);
            if (index < 0) continue;
            if (min < 0 || index < min) min = index;
        }
        return min;
    }

    private Map<Integer, T> collect(Iterable<Integer> indexes) {
        Map<Integer, T> map = new LinkedHashMap<>();
        for (Integer index : indexes) {
            int src = toSource(index);
            if (src >= 0) map.put(src, view.get(index));
        }
        return map;
    }

    private void replaceWith(Map<Integer, T> replacement) {
        ObservableMap<Integer, T> map = model.newMap();
        map.putAll(replacement);
        model.selection().set(map);
    }

    private IntegerRange clamp(IntegerRange range) {
        if (view.isEmpty()) return null;
        int min = Math.max(0, range.getMin());
        int max = Math.min(view.size() - 1, range.getMax());
        return min > max ? null : IntegerRange.of(min, max);
    }
}
