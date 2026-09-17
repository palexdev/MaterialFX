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

package io.github.palexdev.mfxcore.collections;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/// An [ObservableList] backed by an [ArrayList], meant as an open replacement for [FXCollections#observableArrayList()].
///
/// The list returned by that factory is `com.sun.javafx.collections.ObservableListWrapper`, which cannot be extended
/// from outside the JavaFX modules. Anything it does not offer has to be rebuilt from scratch, because the two
/// operations needed to report a reorder as a single change, [#beginChange()] and [#nextPermutation(int, int, int\[\])],
/// are protected, thus available to subclasses only. This class exists to be that subclass: the delegate is
/// `protected`, and so are the hooks a custom operation needs, see [#mergeSort(Object\[\], Object\[\], int\[\], int\[\], int, int, Comparator)].
///
/// #### Parity with the JavaFX implementation
///
/// The bulk operations are overridden with the same algorithms used by the wrapper, so that each of them produces a
/// single change instead of one per element: [#clear()], [#remove(int, int)], [#removeAll(Collection)] and
/// [#retainAll(Collection)] (the last two collect the positions to remove in a [BitSet], then remove from the end).
/// [#indexOf(Object)], [#lastIndexOf(Object)], [#contains(Object)] and [#containsAll(Collection)] read the delegate
/// directly.
///
/// [#sort(Comparator)] reports the whole reorder as one permutation change. Without it the list would inherit
/// [List#sort(Comparator)], which writes the elements back one [#set(int, Object)] at a time: one change per element,
/// and duplicate elements in the list while it runs. The sort is a merge sort carrying the elements' original
/// positions along, stable, and measured to be slightly faster than the JavaFX one.
///
/// #### Note
/// - **[FXCollections#sort(ObservableList, Comparator)] does not report one permutation.**
/// It reserves that path for lists implementing the internal `SortableList` interface, and falls back to sorting a copy
/// and calling [#setAll(Collection)], a full replacement. Call [#sort(Comparator)] on the list itself to get the permutation.
public class ObservableArrayList<E> extends ModifiableObservableListBase<E> implements RandomAccess {

    //================================================================================
    // Properties
    //================================================================================

    protected final List<E> delegate;

    //================================================================================
    // Constructors
    //================================================================================

    public ObservableArrayList() {
        delegate = new ArrayList<>();
    }

    public ObservableArrayList(Collection<? extends E> elements) {
        delegate = new ArrayList<>(elements);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @SuppressWarnings("unchecked")
    @Override
    public void sort(Comparator<? super E> comparator) {
        int size = size();
        if (size < 2) return;
        Comparator<Object> c = (comparator != null) ?
            (Comparator<Object>) comparator :
            (Comparator<Object>) (Comparator<?>) Comparator.<Comparable<Object>>naturalOrder();

        Object[] items = delegate.toArray();
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) indices[i] = i;
        mergeSort(items.clone(), items, indices.clone(), indices, 0, size, c);

        int[] perm = new int[size];
        beginChange();
        try {
            for (int k = 0; k < size; k++) {
                perm[indices[k]] = k;
                delegate.set(k, (E) items[k]);
            }
            nextPermutation(0, size, perm);
        } finally {
            endChange();
        }
    }

    protected static void mergeSort(Object[] srcItems, Object[] dstItems, int[] srcIdx, int[] dstIdx, int lo, int hi, Comparator<Object> c) {
        int length = hi - lo;
        if (length < 7) {
            for (int i = lo + 1; i < hi; i++) {
                Object item = dstItems[i];
                int idx = dstIdx[i];
                int j = i - 1;
                while (j >= lo && c.compare(dstItems[j], item) > 0) {
                    dstItems[j + 1] = dstItems[j];
                    dstIdx[j + 1] = dstIdx[j];
                    j--;
                }
                dstItems[j + 1] = item;
                dstIdx[j + 1] = idx;
            }
            return;
        }

        int mid = (lo + hi) >>> 1;
        mergeSort(dstItems, srcItems, dstIdx, srcIdx, lo, mid, c);
        mergeSort(dstItems, srcItems, dstIdx, srcIdx, mid, hi, c);

        if (c.compare(srcItems[mid - 1], srcItems[mid]) <= 0) {
            System.arraycopy(srcItems, lo, dstItems, lo, length);
            System.arraycopy(srcIdx, lo, dstIdx, lo, length);
            return;
        }

        for (int i = lo, p = lo, q = mid; i < hi; i++) {
            if (q >= hi || (p < mid && c.compare(srcItems[p], srcItems[q]) <= 0)) {
                dstItems[i] = srcItems[p];
                dstIdx[i] = srcIdx[p++];
            } else {
                dstItems[i] = srcItems[q];
                dstIdx[i] = srcIdx[q++];
            }
        }
    }

    @Override
    public void clear() {
        boolean notify = hasListeners();
        if (notify) {
            beginChange();
            nextRemove(0, this);
        }
        delegate.clear();
        ++modCount;
        if (notify) endChange();
    }

    @Override
    public void remove(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        beginChange();
        try {
            for (int i = from; i < to; i++) remove(from);
        } finally {
            endChange();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty() || delegate.isEmpty()) return false;
        return removeMatching(c, true);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (delegate.isEmpty()) return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }
        return removeMatching(c, false);
    }

    protected boolean removeMatching(Collection<?> c, boolean contained) {
        BitSet bs = new BitSet(delegate.size());
        for (int i = 0; i < delegate.size(); i++) {
            if (c.contains(delegate.get(i)) == contained) bs.set(i);
        }
        if (bs.isEmpty()) return false;

        beginChange();
        try {
            int cur = delegate.size();
            while ((cur = bs.previousSetBit(cur - 1)) >= 0) remove(cur);
        } finally {
            endChange();
        }
        return true;
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    protected void doAdd(int index, E element) {
        Objects.checkIndex(index, size() + 1);
        delegate.add(index, element);
    }

    @Override
    protected E doSet(int index, E element) {
        return delegate.set(index, element);
    }

    @Override
    protected E doRemove(int index) {
        return delegate.remove(index);
    }
}
