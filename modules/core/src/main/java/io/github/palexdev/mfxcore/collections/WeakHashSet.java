/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A weak {@link HashSet}. An element stored in the {@code WeakHashSet} might be
 * garbage collected, if there is no strong reference to this element.
 */
public class WeakHashSet<T> extends HashSet<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final Set<WeakElement<T>> set = new HashSet<>();
    // Helps detect GCed values
    private final ReferenceQueue<T> queue = new ReferenceQueue<>();

    //================================================================================
    // Constructors
    //================================================================================

    public WeakHashSet() {
    }

    public WeakHashSet(Collection<? extends T> c) {
        super(c);
    }

    public WeakHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public WeakHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds the specified element to this set if it is not already
     * present.
     *
     * @param t element to be added to this set.
     * @return `true` if the set did not already contain the specified
     * element.
     */
    @Override
    public boolean add(T t) {
        processQueue();
        return set.add(WeakElement.of(t, queue));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            if (!add(t)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<T> itemsToRemove = new HashSet<>(c.size());
        for (T t : this) {
            if (!c.contains(t)) {
                itemsToRemove.add(t);
            }
        }
        if (itemsToRemove.isEmpty()) return false;

        for (T t : itemsToRemove) {
            remove(t);
        }
        return true;
    }

    /**
     * Removes the given element from this set if it is present.
     *
     * @param o object to be removed from this set, if present.
     * @return `true` if the set contained the specified element.
     */
    @Override
    public boolean remove(Object o) {
        boolean ret = set.remove(WeakElement.of(o));
        processQueue();
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            if (remove(o)) changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
    }

    /**
     * Returns `true` if this set contains the specified element.
     *
     * @param o element whose presence in this set is to be tested.
     * @return `true` if this set contains the specified element.
     */
    @Override
    public boolean contains(Object o) {
        return set.contains(WeakElement.of(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns an iterator over the elements in this set. The elements
     * are returned in no particular order.
     *
     * @return an Iterator over the elements in this set.
     */
    @Override
    public Iterator<T> iterator() {
        // Remove GCed elements
        processQueue();

        Iterator<WeakElement<T>> it = set.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next().get();
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }

    /**
     * Removes all GCed values from the delegate Set.
     * Since we don't know how much the ReferenceQueue.poll() operation
     * costs, we should call it only in methods that change the Set, like add or remove.
     */
    protected void processQueue() {
        WeakElement<? extends T> e;
        while (true) {
            e = (WeakElement<? extends T>) queue.poll();
            if (e == null) break;
            set.remove(e);
        }
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * A {@code WeakHasSet} stores objects of class WeakElement.
     * A {@code WeakElement} wraps the element that should be stored in the {@code WeakHashSet}.
     * {@code WeakElement} inherits from {@link WeakReference}.
     * It redefines equals and hashCode which delegate to the corresponding methods
     * of the wrapped element.
     */
    protected static class WeakElement<T> extends WeakReference<T> {
        private final int hash;

        public WeakElement(T t) {
            super(t);
            this.hash = (t != null) ? t.hashCode() : 0;
        }

        public WeakElement(T t, ReferenceQueue<? super T> q) {
            super(t, q);
            this.hash = (t != null) ? t.hashCode() : 0;
        }

        public static <T> WeakElement<T> of(T t) {
            return new WeakElement<>(t);
        }

        public static <T> WeakElement<T> of(T t, ReferenceQueue<? super T> q) {
            return new WeakElement<>(t, q);
        }

        /**
         * A {@code WeakElement} is equal to another {@code WeakElement} if they both refer to objects
         * that are, in turn, equal according to their own equals methods
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            T t = get();
            Object oT = ((WeakElement<?>) o).get();
            if (t == oT) return true;
            return (t == null || oT == null) ? false : t.equals(oT);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
