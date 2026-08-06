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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.palexdev.mfxcore.collections.CircularQueue.EvictionPolicy;
import javafx.collections.ModifiableObservableListBase;

/// An [javafx.collections.ObservableList] with a maximum capacity.
///
/// Insertions are never rejected upfront: elements are added at the requested index and then the queue is made to
/// fit its capacity by evicting from one of its two ends, as specified by the [EvictionPolicy].
/// [EvictionPolicy#HEAD] (the default) keeps the most recent elements, [EvictionPolicy#TAIL] keeps the eldest ones.
/// A consequence of this is that an insertion may end up evicting the very elements it just added.
///
/// Unlike [CircularQueue], evictions are notified to the listeners, and each operation, bulk ones included, produces
/// a single change containing both the removals and the additions.
///
/// @param <E> Any type
public class ObservableCircularQueue<E> extends ModifiableObservableListBase<E> {
    //================================================================================
    // Properties
    //================================================================================
    private final List<E> delegate = new ArrayList<>();
    private int capacity;
    private EvictionPolicy policy;

    //================================================================================
    // Constructors
    //================================================================================
    public ObservableCircularQueue(int capacity) {
        this(capacity, EvictionPolicy.HEAD);
    }

    public ObservableCircularQueue(int capacity, EvictionPolicy policy) {
        this.policy = Objects.requireNonNull(policy);
        setCapacity(capacity);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void trim() {
        if (size() <= capacity) return;
        beginChange();
        try {
            while (size() > capacity) policy.evict(this);
        } finally {
            endChange();
        }
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// @return false if the queue is full and the [EvictionPolicy] makes the given element the one to be evicted
    @Override
    public boolean add(E element) {
        return addAll(size(), Collections.singletonList(element));
    }

    @Override
    public void add(int index, E element) {
        addAll(index, Collections.singletonList(element));
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    /// {@inheritDoc}
    ///
    /// Elements that the following eviction would immediately discard are not inserted in the first place, so that
    /// the operation always results in a single change with at most one removal and one addition.
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.checkIndex(index, size() + 1);
        if (c.isEmpty()) return false;

        List<E> toAdd = new ArrayList<>(c);
        int at = index;
        int excess = size() + toAdd.size() - capacity;
        beginChange();
        try {
            if (excess > 0) {
                boolean head = policy == EvictionPolicy.HEAD;
                int fromList = Math.min(excess, head ? at : size() - at);
                for (int i = 0; i < fromList; i++) policy.evict(this);
                int fromAdd = excess - fromList;
                if (head) {
                    at -= fromList;
                    if (fromAdd > 0) toAdd = toAdd.subList(fromAdd, toAdd.size());
                } else if (fromAdd > 0) {
                    toAdd = toAdd.subList(0, toAdd.size() - fromAdd);
                }
            }
            if (toAdd.isEmpty()) return false;
            delegate.addAll(at, toAdd);
            nextAdd(at, at + toAdd.size());
            ++modCount;
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
    protected void doAdd(int index, E element) {
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

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the maximum number of elements the queue can contain before it starts evicting them
    public int getCapacity() {
        return capacity;
    }

    /// Sets the maximum size of the queue and evicts exceeding elements if the specified size is lesser than the
    /// number of elements.
    ///
    /// @param capacity The new desired size
    /// @throws IllegalArgumentException if the desired size is not greater than 0
    public void setCapacity(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0 but was: " + capacity);
        this.capacity = capacity;
        trim();
    }

    /// @return from which end elements are evicted when the capacity is exceeded
    public EvictionPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(EvictionPolicy policy) {
        this.policy = Objects.requireNonNull(policy);
    }
}
