/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.LinkedList;

/**
 * This is the implementation of a circular FIFO queue.
 * When the maximum size is reached the oldest element is removed and replaced
 * by the new one.
 */
public class CircularQueue<E> extends LinkedList<E> {
	//================================================================================
	// Properties
	//================================================================================
	private int capacity;

	//================================================================================
	// Constructors
	//================================================================================
	public CircularQueue(int capacity) {
		super();
		this.capacity = capacity;
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Adds the specified element to the queue and if it is full removes the oldest element
	 * and then adds the new one.
	 * <p></p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E e) {
		if (super.size() == this.capacity) {
			super.remove();
		}
		return super.add(e);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the maximum number of elements the queue can contain before it starts deleting them
	 * from the head
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the maximum size of the queue and removes exceeding elements
	 * if the specified size is lesser than the number of elements.
	 *
	 * @param capacity The new desired size
	 * @throws IllegalArgumentException if the desired size is 0
	 */
	public void setCapacity(int capacity) {
		if (capacity == 0) {
			throw new IllegalArgumentException("Size cannot be 0!");
		}

		if (capacity < super.size()) {
			for (int i = 0; i < (super.size() - capacity); i++) {
				super.remove();
			}
		}
		this.capacity = capacity;
	}
}
