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

import io.github.palexdev.mfxcore.collections.DoublyLinkedList.Elem;

/// A doubly linked list that exposes its nodes ({@link Elem}) directly, allowing callers to traverse via
/// {@link Elem#next()} / {@link Elem#prev()}.
///
/// Internally backed by an {@link ArrayList} for O(1) indexed access. Since this does not extend from [Collection],
/// you can access an unmodifiable copy of the backing list with [#backingList()]
public class DoublyLinkedList<T> implements Iterable<Elem<T>> {

    //================================================================================
    // Properties
    //================================================================================

    private final List<Elem<T>> backingList = new ArrayList<>();

    //================================================================================
    // Methods
    //================================================================================

    // Basic Operations

    /// @return an unmodifiable view of the backing list
    public List<Elem<T>> backingList() {
        return Collections.unmodifiableList(backingList);
    }

    /// @return the number of elements.
    public int size() {
        return backingList.size();
    }

    /// @return whether the list is empty.
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    /// @return the head element, or null if empty.
    public Elem<T> head() {
        return size() > 0 ? get(0) : null;
    }

    /// @return the tail element, or null if empty.
    public Elem<T> tail() {
        return size() > 0 ? get(size() - 1) : null;
    }

    /// @return the element at the given index.
    public Elem<T> get(int index) {
        return backingList.get(index);
    }

    /// @return true if the list contains an element with the given value.
    public boolean contains(T t) {
        for (Elem<T> e : backingList) {
            if (Objects.equals(e.value(), t)) {
                return true;
            }
        }
        return false;
    }

    // Insertion Operations

    /// Inserts a new element at the head.
    public void push(T t) {
        Elem<T> newHead = new Elem<>(t);
        if (!isEmpty()) {
            Elem<T> curr = head();
            curr.prev = newHead;
            newHead.next = curr;
        }
        backingList.addFirst(newHead);
    }

    /// Appends a new element at the tail.
    public void offer(T t) {
        Elem<T> newTail = new Elem<>(t);
        if (isEmpty()) {
            push(t);
        } else {
            Elem<T> curr = tail();
            curr.next = newTail;
            newTail.prev = curr;
            backingList.add(newTail);
        }
    }

    /// Inserts an element at the given index.
    public void add(int index, T t) {
        Elem<T> newElem = new Elem<>(t);
        if (index == 0) {
            push(t);
        } else if (index == size()) {
            offer(t);
        } else {
            Elem<T> left = get(index - 1);
            Elem<T> right = get(index);
            left.next = newElem;
            newElem.prev = left;
            right.prev = newElem;
            newElem.next = right;
            backingList.add(index, newElem);
        }
    }

    /// Appends all values to the tail.
    public void addAll(T... ts) {
        for (T t : ts) {
            offer(t);
        }
    }

    /// Appends all values from the collection to the tail.
    public void addAll(Collection<? extends T> c) {
        for (T t : c) {
            offer(t);
        }
    }

    /// Adds all values at the given index.
    public void addAll(int index, T... ts) {
        for (T t : ts) {
            add(index++, t);
        }
    }

    /// Adds all values from the collection at the given index.
    public void addAll(int index, Collection<? extends T> c) {
        for (T t : c) {
            add(index++, t);
        }
    }

    // Removal Operations

    /// Removes and returns the head element, or null if empty.
    public Elem<T> pop() {
        if (isEmpty()) return null;
        Elem<T> curr = head();
        Elem<T> next = curr.next;
        if (next != null) {
            curr.next = null;
            next.prev = null;
        }
        backingList.removeFirst();
        return curr;
    }

    /// Removes and returns the tail element, or null if empty.
    public Elem<T> poll() {
        if (isEmpty()) return null;
        Elem<T> curr = tail();
        Elem<T> prev = curr.prev;
        if (prev != null) {
            curr.prev = null;
            prev.next = null;
        }
        backingList.removeLast();
        return curr;
    }

    /// Removes and returns the element at the given index.
    public Elem<T> remove(int index) {
        if (index == 0) {
            return pop();
        }
        if (index == size() - 1) {
            return poll();
        }
        Elem<T> removed = get(index);
        Elem<T> left = get(index - 1);
        Elem<T> right = get(index + 1);
        left.next = right;
        right.prev = left;
        backingList.remove(index);
        return removed;
    }

    /// Removes the first occurrence of the given value.
    ///
    /// @return true if the value was found and removed
    public boolean remove(T t) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i).value(), t)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /// Removes all occurrences of the given values.
    public void removeAll(Collection<? extends T> ts) {
        if (ts.isEmpty()) return;
        Set<T> toRemove = new HashSet<>(ts);

        List<Elem<T>> kept = new ArrayList<>();
        for (Elem<T> e : backingList) {
            if (!toRemove.contains(e.value())) {
                e.prev = null; // clear stale links before rewiring
                e.next = null;
                kept.add(e);
            }
        }

        // Rewire kept elements in one pass
        for (int i = 0; i < kept.size(); i++) {
            Elem<T> e = kept.get(i);
            e.prev = i > 0               ? kept.get(i - 1) : null;
            e.next = i < kept.size() - 1 ? kept.get(i + 1) : null;
        }

        backingList.clear();
        backingList.addAll(kept);
    }

    /// Removes all occurrences of the given values.
    public void removeAll(T... ts) {
        removeAll(Arrays.asList(ts));
    }

    /// Removes all elements.
    public void clear() {
        backingList.clear();
    }

    // Conversion

    /// @return an array of all elements.
    public Object[] toArray() {
        return backingList.toArray();
    }

    /// @return an array of all elements, typed as specified.
    public <T> T[] toArray(T[] a) {
        return backingList.toArray(a);
    }

    // Iteration

    /// @return a forward iterator.
    @Override
    public Iterator<Elem<T>> iterator() {
        return new LinkedListIterator<>(this, false);
    }

    /// @return a reverse iterator.
    public Iterator<Elem<T>> descendingIterator() {
        return new LinkedListIterator<>(this, true);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Iterator that traverses the list in either direction.
    public static class LinkedListIterator<T> implements Iterator<Elem<T>> {
        private boolean reverse;
        private Elem<T> curr;

        public LinkedListIterator(DoublyLinkedList<T> list, boolean reverse) {
            this.reverse = reverse;
            this.curr = reverse ? list.tail() : list.head();
        }

        @Override
        public Elem<T> next() {
            if (!hasNext()) throw new NoSuchElementException();
            Elem<T> ret = curr;
            curr = reverse ? curr.prev : curr.next;
            return ret;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }
    }

    /// A node holding a value and links to adjacent nodes.
    public static final class Elem<T> {
        private final T value;
        private Elem<T> next;
        private Elem<T> prev;

        public Elem(T value) {
            this.value = value;
        }

        public T value() {return value;}

        public Elem<T> next() {return next;}

        public Elem<T> prev() {return prev;}

        @Override
        public String toString() {
            return "Elem{" +
                   "value=" + value +
                   ", hasPrev=" + (prev != null) +
                   ", hasNext=" + (next != null) +
                   '}';
        }
    }
}
