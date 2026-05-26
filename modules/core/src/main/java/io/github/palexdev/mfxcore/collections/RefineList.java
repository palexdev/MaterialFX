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

package io.github.palexdev.mfxcore.collections;

import java.util.*;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.TransformationList;

/// A more flexible alternative to [FilteredList] and [SortedList], combining both into a single class
/// while still allowing modifications to the underlying source list.
///
/// #### Design
///
/// Internally, a [FilteredList] wraps the source, and a [SortedList] wraps the filtered list.
/// This means the sorted list can also be filtered, and represents the "view" of the data.
///
/// #### Split Contract
///
/// This class operates in two spaces deliberately:
/// - **Read operations** (`size`, `get`, `iterator`, etc.) and **observability** (listeners) are delegated to the view
///   (`sorted`), so consumers always see filtered and sorted data.
/// - **Write operations** (`add`, `remove`, `set`, etc.) are delegated to `src` directly, bypassing JavaFX's read-only
///   restrictions on [TransformationList] subclasses. Indexed write operations therefore expect **source-space** indices.
///
/// Use [sourceToView] and [viewToSource] to translate between the two spaces when needed.
public class RefineList<T> implements ObservableList<T> {

    //================================================================================
    // Properties
    //================================================================================

    private final ObservableList<T> src;
    private final FilteredList<T> filtered;
    private final SortedList<T> sorted;

    //================================================================================
    // Constructors
    //================================================================================

    public RefineList() {
        this(FXCollections.observableArrayList());
    }

    public RefineList(ObservableList<T> src) {
        this.src = src;
        this.filtered = new FilteredList<>(src);
        this.sorted = new SortedList<>(filtered);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Maps a source index to the corresponding index in the final view (filtered + sorted).
    ///
    /// The translation mirrors the internal pipeline (`src → filtered → sorted`) in two steps:
    /// 1. If a predicate is active, [FilteredList#getViewIndex(int)] maps the source index to a position
    ///    inside `filtered`. A negative return value means the element is excluded — propagated immediately.
    /// 2. [SortedList#getViewIndex(int)] then maps that filtered-layer index to the final sorted position.
    ///    When no comparator is set this step is a no-op (identity), so the same call handles both cases.
    ///
    /// If the element at the given source index is excluded by the current predicate,
    /// returns a negative value (the encoded insertion point, per [FilteredList#getViewIndex(int)]).
    public int sourceToView(int index) {
        if (getPredicate() != null) {
            int filterIndex = filtered.getViewIndex(index);
            if (filterIndex < 0) return filterIndex;
            return sorted.getViewIndex(filterIndex);
        }
        return sorted.getViewIndex(index);
    }

    /// Delegates to [SortedList#getSourceIndexFor(ObservableList, int)] with the source as the list parameter.
    public int viewToSource(int index) {
        return sorted.getSourceIndexFor(src, index);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    // Listeners
    @Override public void addListener(ListChangeListener<? super T> listener) {sorted.addListener(listener);}
    @Override public void removeListener(ListChangeListener<? super T> listener) {sorted.removeListener(listener);}
    @Override public void addListener(InvalidationListener listener) {sorted.addListener(listener);}
    @Override public void removeListener(InvalidationListener listener) {sorted.removeListener(listener);}

    // Basic Operations
    @Override public int size() {return sorted.size();}
    @Override public boolean isEmpty() {return sorted.isEmpty();}
    @Override public boolean contains(Object o) {return sorted.contains(o);}
    @Override public boolean add(T t) {return src.add(t);}
    @Override public boolean remove(Object o) {return src.remove(o);}
    @Override public void clear() {src.clear();}

    // Bulk Operations
    @Override public boolean addAll(T... elements) {return src.addAll(elements);}
    @Override public boolean addAll(Collection<? extends T> c) {return src.addAll(c);}
    @Override public boolean addAll(int index, Collection<? extends T> c) {return src.addAll(index, c);}
    @Override public boolean setAll(T... elements) {return src.setAll(elements);}
    @Override public boolean setAll(Collection<? extends T> col) {return src.setAll(col);}
    @Override public boolean removeAll(T... elements) {return src.removeAll(elements);}
    @Override public boolean removeAll(Collection<?> c) {return src.removeAll(c);}
    @Override public boolean retainAll(T... elements) {return src.retainAll(elements);}
    @Override public boolean retainAll(Collection<?> c) {return src.retainAll(c);}
    @Override public boolean containsAll(Collection<?> c) {return sorted.containsAll(c);}

    // Indexed Operations
    @Override public T get(int index) {return sorted.get(index);}
    @Override public T set(int index, T element) {return src.set(index, element);}
    @Override public void add(int index, T element) {src.add(index, element);}
    @Override public T remove(int index) {return src.remove(index);}
    @Override public void remove(int from, int to) {src.remove(from, to);}

    // Positional Search
    @Override public int indexOf(Object o) {return sorted.indexOf(o);}
    @Override public int lastIndexOf(Object o) {return sorted.lastIndexOf(o);}

    // Iterators
    @Override public Iterator<T> iterator() {return sorted.iterator();}
    @Override public ListIterator<T> listIterator() {return sorted.listIterator();}
    @Override public ListIterator<T> listIterator(int index) {return sorted.listIterator(index);}

    // Sublist Operations
    @Override public List<T> subList(int fromIndex, int toIndex) {return sorted.subList(fromIndex, toIndex);}

    // Array Conversions
    @Override public Object[] toArray() {return sorted.toArray();}
    @Override public <T1> T1[] toArray(T1[] a) {return sorted.toArray(a);}

    //================================================================================
    // Getters/Setters
    //================================================================================
    public ObservableList<T> getSource() {
        return src;
    }

    @SuppressWarnings("unchecked")
    public Predicate<T> getPredicate() {
        return (Predicate<T>) filtered.getPredicate();
    }

    public void setPredicate(Predicate<T> predicate) {
        filtered.setPredicate(predicate);
    }

    @SuppressWarnings("unchecked")
    public Comparator<T> getComparator() {
        return (Comparator<T>) sorted.getComparator();
    }

    public void setComparator(Comparator<T> comparator) {
        sorted.setComparator(comparator);
    }
}