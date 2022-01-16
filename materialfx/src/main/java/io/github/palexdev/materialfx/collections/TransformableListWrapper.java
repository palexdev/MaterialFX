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

import io.github.palexdev.materialfx.beans.properties.functional.ComparatorProperty;
import io.github.palexdev.materialfx.beans.properties.functional.PredicateProperty;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * For some idiot reason JavaFX's {@link TransformationList}s do not allow modifying the
 * source list.
 * <p>
 * This class fixes that. It wraps an {@link ObservableList} which is the source list and
 * the new {@link TransformableList} which is built from that source.
 * <p>
 * This way you can benefit of the futures of the new {@link TransformableList} (sorting and filtering)
 * while also being able to directly modify the source list.
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public class TransformableListWrapper<T> extends AbstractList<T> implements ObservableList<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObservableList<T> source;
	private final TransformableList<T> transformableList;

	//================================================================================
	// Constructors
	//================================================================================
	public TransformableListWrapper(ObservableList<T> source) {
		this.source = source;
		this.transformableList = new TransformableList<>(source);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the {@link TransformableList}.
	 */
	@Override
	public void addListener(ListChangeListener<? super T> listener) {
		transformableList.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed from the {@link TransformableList}.
	 */
	@Override
	public void removeListener(ListChangeListener<? super T> listener) {
		transformableList.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the source list.
	 */
	@Override
	public boolean add(T t) {
		return source.add(t);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Set on the source list.
	 */
	@Override
	public T set(int index, T element) {
		return source.set(index, element);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the source list.
	 */
	@Override
	public void add(int index, T element) {
		source.add(index, element);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed from the source list.
	 */
	@Override
	public T remove(int index) {
		return source.remove(index);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Retrieved from the {@link TransformableList}.
	 */
	@Override
	public int indexOf(Object o) {
		return transformableList.indexOf(o);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Retrieved from the {@link TransformableList}.
	 */
	@Override
	public int lastIndexOf(Object o) {
		return transformableList.lastIndexOf(o);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The source list is cleared.
	 */
	@Override
	public void clear() {
		source.clear();
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the source list.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return source.addAll(index, c);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the source list.
	 */
	@Override
	public boolean addAll(T... elements) {
		return source.addAll(elements);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Set on the source list.
	 */
	@Override
	public boolean setAll(T... elements) {
		return source.setAll(elements);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Set on the source list.
	 */
	@Override
	public boolean setAll(Collection<? extends T> col) {
		return source.setAll(col);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed from the source list.
	 */
	@Override
	public boolean removeAll(T... elements) {
		return source.removeAll(elements);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Retained on the source list.
	 */
	@Override
	public boolean retainAll(T... elements) {
		return source.retainAll(elements);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed from the source list.
	 */
	@Override
	public void remove(int from, int to) {
		source.remove(from, to);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Added to the {@link TransformableList}.
	 */
	@Override
	public void addListener(InvalidationListener listener) {
		transformableList.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removed from the {@link TransformableList}.
	 */
	@Override
	public void removeListener(InvalidationListener listener) {
		transformableList.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Retrieved from the {@link TransformableList}.
	 */
	@Override
	public T get(int index) {
		return transformableList.get(index);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Size of the {@link TransformableList}.
	 */
	@Override
	public int size() {
		return transformableList.size();
	}

	/**
	 * @return the source observable list
	 */
	public ObservableList<? extends T> getSource() {
		return transformableList.getSource();
	}

	/**
	 * Delegate for {@link TransformableList#viewToSource(int)}.
	 */
	public int viewToSource(int index) {
		return transformableList.viewToSource(index);
	}

	/**
	 * Delegate for {@link TransformableList#sourceToView(int)}.
	 */
	public int sourceToView(int index) {
		return transformableList.sourceToView(index);
	}

	public Predicate<? super T> getPredicate() {
		return transformableList.getPredicate();
	}

	/**
	 * Delegate for {@link TransformableList#predicateProperty()}.
	 */
	public PredicateProperty<T> predicateProperty() {
		return transformableList.predicateProperty();
	}

	public void setPredicate(Predicate<T> predicate) {
		transformableList.setPredicate(predicate);
	}

	public Comparator<T> getComparator() {
		return transformableList.getComparator();
	}

	/**
	 * Delegate for {@link TransformableList#comparatorProperty()}.
	 */
	public ComparatorProperty<T> comparatorProperty() {
		return transformableList.comparatorProperty();
	}

	public void setComparator(Comparator<T> comparator) {
		transformableList.setComparator(comparator);
	}

	/**
	 * Delegate for {@link TransformableList#setComparator(Comparator, boolean)}.
	 */
	public void setComparator(Comparator<T> sorter, boolean reversed) {
		transformableList.setComparator(sorter, reversed);
	}

	/**
	 * Delegate for {@link TransformableList#isReversed()}.
	 */
	public boolean isReversed() {
		return transformableList.isReversed();
	}

	/**
	 * Delegate for {@link TransformableList#setReversed(boolean)}.
	 */
	public void setReversed(boolean reversed) {
		transformableList.setReversed(reversed);
	}

	/**
	 * @return the wrapped {@link TransformableList}
	 */
	public TransformableList<T> getTransformableList() {
		return transformableList;
	}
}
