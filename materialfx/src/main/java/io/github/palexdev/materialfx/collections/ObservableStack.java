/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.collections;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * A crude implementation of an observable stack It includes the main features of a stack namely:
 * push and pop.
 *
 * @param <E> Any type
 */
public class ObservableStack<E> extends SimpleListProperty<E> {
	//================================================================================
	// Properties
	//================================================================================
	private final LinkedList<E> stack;

	//================================================================================
	// Constructors
	//================================================================================
	public ObservableStack() {
		this.stack = new LinkedList<>();
		this.set(FXCollections.observableList(this.stack));
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Places the item at the top of the stack
	 *
	 * @param item the item
	 * @return the item that was just pushed
	 */
	public E push(E item) {
		stack.push(item);
		fireValueChangedEvent(new StackChange(this.get(),
				ChangeType.PUSH.setChangedObj(Collections.singletonList(item))));
		return item;
	}

	/**
	 * @return the item at the top of the stack granted that the stack is not empty
	 * @throws NoSuchElementException if the stack is empty
	 */
	public E pop() throws NoSuchElementException {
		E temp = stack.pop();
		fireValueChangedEvent(new StackChange(this.get(),
				ChangeType.POP.setChangedObj(Collections.singletonList(temp))));
		return temp;
	}

	/**
	 * Pushes the element to the top of the stack
	 *
	 * @param element the element to add
	 * @return Always returns true
	 * @see #push(Object)
	 */
	@Override
	public boolean add(E element) {
		push(element);
		return true;
	}

	/**
	 * Removes an element at the given index
	 *
	 * @param i the index to remove from
	 * @return The element that was removed
	 * @throws IllegalArgumentException if i is not 0. The stack can only access the top element
	 * @see #pop()
	 */
	@Override
	public E remove(int i) throws IllegalArgumentException {
		if (0 == i) {
			return pop();
		}
		throw new IllegalArgumentException("Can only modify the top of the stack " + i);
	}

	/**
	 * Effectively empties the stack given that the stack is not already empty
	 *
	 * @return true if the stack was emptied
	 * @throws NoSuchElementException if the stack is already empty
	 */
	public boolean removeAll() throws NoSuchElementException {
		this.get().remove(0, getSize());
		return true;
	}

	/**
	 * Adds an element to the given index
	 *
	 * @param i       the index to add the element at
	 * @param element the element to add to the stack
	 * @throws IllegalArgumentException if the index specified is not 0. Only the top of the stack
	 *                                  is accessible
	 * @see #push(Object)
	 */
	@Override
	public void add(int i, E element) throws IllegalArgumentException {
		if (0 == i) {
			push(element);
		}
		throw new IllegalArgumentException("Can only modify the top of the stack " + i);
	}

	/**
	 * Adds the elements from the collection into the stack in the order they are specified
	 *
	 * @param elements the collection to be added to this stack
	 * @return true
	 * @throws NullPointerException if the collection is null
	 */
	@Override
	public boolean addAll(Collection<? extends E> elements) throws NullPointerException {
		elements.forEach(stack::push);
		fireValueChangedEvent(new StackChange(this.get(),
				ChangeType.PUSH.setChangedObj(new ArrayList<>(elements))));
		return true;
	}

	/**
	 * Adds the contents of the array into the stack
	 *
	 * @param elements the array of elements to add
	 * @return true
	 * @see #addAll(Collection)
	 */
	@Override
	public boolean addAll(E... elements) {
		return addAll(Arrays.asList(elements));
	}

	@Override
	public boolean addAll(int i, Collection<? extends E> elements) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Attempt to remove an arbitrary object from the stack is not permitted
	 *
	 * @param obj The object to remove
	 * @return Nothing
	 * @throws UnsupportedOperationException Removing an arbitrary object is not permitted Use
	 *                                       {@link #pop()}
	 */
	@Override
	public boolean remove(Object obj) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Operation not allowed, use pop");
	}

	/**
	 * Attempt to remove a range of objects from the stack, this is also not permitted
	 *
	 * @param from Start removing from here
	 * @param to   To here
	 * @throws UnsupportedOperationException {@link #remove(Object)}
	 */
	@Override
	public void remove(int from, int to) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Operation not allowed, use pop");
	}

	@Override
	public boolean removeAll(E... elements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> objects) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Used to determine what change occurred in the stack
	 */
	private enum ChangeType {
		PUSH, POP;

		/**
		 * The object that was changed
		 */
		private List changedObj;

		/**
		 * The changed object(s) are packaged as a list
		 *
		 * @return The list of changed objects
		 */
		public List getChangedObj() {
			return changedObj;
		}

		/**
		 * Method to accept the changed object
		 *
		 * @param obj the list of objects that were changed in the stack
		 * @return this enum
		 */
		public ChangeType setChangedObj(List obj) {
			this.changedObj = obj;
			return this;
		}
	}

	private final class StackChange extends ListChangeListener.Change<E> {

		private final ChangeType type;
		private boolean onChange;

		/**
		 * Constructs a new change done to a list.
		 *
		 * @param list that was changed
		 */
		public StackChange(ObservableList<E> list, ChangeType type) {
			super(list);
			this.type = type;
			onChange = false;
		}

		@Override
		public boolean wasAdded() {
			return type == ChangeType.PUSH;
		}

		@Override
		public boolean wasRemoved() {
			return type == ChangeType.POP;
		}

		@Override
		public boolean next() {
			if (onChange) {
				return false;
			}
			onChange = true;
			return true;
		}

		@Override
		public void reset() {
			onChange = false;
		}

		/**
		 * Because this is a stack, all push and pop happen to the first item in the stack
		 *
		 * @return index of the first item
		 */
		@Override
		public int getFrom() {
			if (!onChange) {
				throw new IllegalStateException(
						"Invalid Change state: next() must be called before inspecting the Change.");
			}
			return 0;
		}

		/**
		 * @return the size of the list returned which indicates the end of the change
		 */
		@Override
		public int getTo() {
			if (!onChange) {
				throw new IllegalStateException(
						"Invalid Change state: next() must be called before inspecting the Change.");
			}
			return type.getChangedObj().size();
		}

		@Override
		public List<E> getRemoved() {
			return wasRemoved() ? type.getChangedObj() :
					Collections.emptyList();
		}

		@Override
		protected int[] getPermutation() {
			return new int[0];
		}
	}
}

