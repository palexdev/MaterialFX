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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/// This is the implementation of a circular FIFO queue.
///
/// Insertions are never rejected upfront: elements are added at the requested index and then the queue is made to
/// fit its capacity by evicting from one of its two ends, as specified by the [EvictionPolicy].
/// [EvictionPolicy#HEAD] (the default) keeps the most recent elements, [EvictionPolicy#TAIL] keeps the eldest ones.
/// A consequence of this is that an insertion may end up evicting the very elements it just added.
///
/// See [ObservableCircularQueue] for an observable variant.
public class CircularQueue<E> extends LinkedList<E> {
    //================================================================================
    // Properties
    //================================================================================
    private int capacity;
    private EvictionPolicy policy;

    //================================================================================
    // Constructors
    //================================================================================
    public CircularQueue(int capacity) {
        this(capacity, EvictionPolicy.HEAD);
    }

    public CircularQueue(int capacity, EvictionPolicy policy) {
        super();
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0 but was: " + capacity);
        this.capacity = capacity;
        this.policy = Objects.requireNonNull(policy);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void trim() {
        while (super.size() > capacity) {
            policy.evict(this);
        }
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// @return false if the queue is full and the [EvictionPolicy] makes the given element the one to be evicted
    @Override
    public boolean add(E e) {
        boolean full = super.size() == capacity;
        super.addLast(e);
        trim();
        return !(full && policy == EvictionPolicy.TAIL);
    }

    @Override
    public void add(int index, E e) {
        super.add(index, e);
        trim();
    }

    @Override
    public void addFirst(E e) {
        super.addFirst(e);
        trim();
    }

    @Override
    public void addLast(E e) {
        super.addLast(e);
        trim();
    }

    /// {@inheritDoc}
    ///
    /// @return false if the queue is full and the [EvictionPolicy] makes the given elements the ones to be evicted
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) return false;
        boolean evicted = super.size() == capacity &&
                          (policy == EvictionPolicy.HEAD ? index == 0 : index == super.size());
        super.addAll(index, c);
        trim();
        return !evicted;
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

    /// @return which element is evicted when the queue is full
    public EvictionPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(EvictionPolicy policy) {
        this.policy = Objects.requireNonNull(policy);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Specifies from which end of a circular queue elements are dropped when the capacity is exceeded.
    public enum EvictionPolicy {
        HEAD,
        TAIL;

        public <E> E evict(List<E> list) {
            return list.remove(this == HEAD ? 0 : list.size() - 1);
        }
    }
}
