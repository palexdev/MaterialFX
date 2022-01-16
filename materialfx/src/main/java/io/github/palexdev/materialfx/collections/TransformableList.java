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
import io.github.palexdev.materialfx.collections.NonIterableChange.GenericAddRemoveChange;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.TransformationList;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@code TransformableList} is a particular type of List which wraps another
 * List called "source" and allows manipulations such as: filter and sort, retaining
 * the original items' index.
 * <p></p>
 * Extends {@link TransformationList}, it's basically the same thing of a {@link FilteredList}
 * and a {@link SortedList} but combined into one.
 * <p></p>
 * A more detailed (and hopefully more clear) explanation about the "indexes retention mentioned above":
 * <p>
 * Think of this List as a View for the source list. The underlying data provided by the source is
 * not guaranteed to be what the user sees but the items' properties are maintained.
 * Let's see a brief example:
 * <pre>
 * {@code
 *     // Let's say I have this ObservableList
 *     ObservableList<String> source = FXCollections.observableArrayList("A", "B", "C"):
 *
 *     // Now let's say I want to sort this list in reverse order (CBA) and that
 *     // for some reason I still want A to be the element at index 0, B-1 and C-2
 *     // This is exactly the purpose of the TransformableList...
 *     TransformableList<String> transformed = new TransformableList<>(source);
 *     transformed.setSorter(Comparator.reverseOrder());
 *
 *     // Now that the order is (CBA) let's see how the list behaves:
 *     transformed.get(0); // Returns C
 *     transformed.indexOf("C"); // Returns 2, the index is retrieved in the source list
 *     transformed.viewToSource(0); // Returns 2, it maps an index of the transformed list to the index of the source list, at 0 we have C which is at index 2 in the source list
 *     transformed.sourceToView(0); // Also returns 2, it maps an index of the source list to the index of the transformed list, at 0 we have C which is at index 2 in the transformed list
 *
 *     // To better see its behavior try to sort and filter the list at the same time.
 *     // You'll notice that sometimes sourceToView will return a negative index because the item is not in the transformed list (after a filter operation)
 * }
 * </pre>
 * <p>
 * Check {@link #computeIndexes()} documentation to see how indexes are calculated.
 * <p></p>
 * <b>IMPORTANT:</b> If using a reversed comparator please use {@link #setComparator(Comparator, boolean)} with 'true' as argument,
 * as {@link #setComparator(Comparator)} will always assume it is a natural order comparator. This is needed to make {@link #sourceToView(int)}
 * properly work as it uses a binary search algorithm to find the right index.
 *
 * @param <T> the items' type
 */
public class TransformableList<T> extends TransformationList<T, T> {
	//================================================================================
	// Constructors
	//================================================================================
	private final List<Integer> indexes = new ArrayList<>();
	private boolean reversed = false;

	private final PredicateProperty<T> predicate = new PredicateProperty<>() {
		@Override
		protected void invalidated() {
			update();
		}
	};

	private final ComparatorProperty<T> comparator = new ComparatorProperty<>() {
		@Override
		protected void invalidated() {
			update();
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public TransformableList(ObservableList<? extends T> source) {
		this(source, null);
	}

	public TransformableList(ObservableList<? extends T> source, Predicate<T> predicate) {
		this(source, predicate, null);
	}

	public TransformableList(ObservableList<? extends T> source, Predicate<T> predicate, Comparator<T> comparator) {
		super(source);
		setPredicate(predicate);
		setComparator(comparator);
		update();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Calls {@link #getSourceIndex(int)}, just with a different name to be more clear.
	 * <p></p>
	 * Maps an index of the transformed list, to the index of the source list.
	 */
	public int viewToSource(int index) {
		return getSourceIndex(index);
	}

	/**
	 * Calls {@link #getViewIndex(int)}, just with a different name to be more clear.
	 * <p></p>
	 * Maps an index of the source list, to the index of the transformed list.
	 */
	public int sourceToView(int index) {
		return getViewIndex(index);
	}

	/**
	 * Responsible for updating the transformed indexes when the
	 * predicate or the comparator change.
	 */
	private void update() {
		indexes.clear();
		indexes.addAll(computeIndexes());
		if (this.hasListeners()) {
			this.fireChange(new GenericAddRemoveChange<>(0, size(), new ArrayList<>(this), this));
		}
	}

	/**
	 * Core method of TransformableLists. This is responsible for computing
	 * the transformed indexes by creating a {@link SortedMap} and mapping every index from 0 to source size
	 * to its item. Before mapping, items are filtered with the given predicate, {@link #predicateProperty()}.
	 * Before returning, the map's entry set is sorted by its values with the given comparator, {@link #comparatorProperty()}.
	 * Finally, returns the map's key set, this set contains the transformed indexes, filtered and sorted.
	 */
	private Collection<Integer> computeIndexes() {
		Predicate<? super T> filter = this.getPredicate();
		Comparator<? super T> sorter = this.getComparator();
		SortedMap<Integer, T> sourceMap;
		if (filter != null) {
			sourceMap = IntStream.range(0, getSource().size())
					.filter((index) -> filter.test(getSource().get(index)))
					.collect(TreeMap::new, (map, index) -> map.put(index, getSource().get(index)), TreeMap::putAll);
		} else {
			sourceMap = IntStream.range(0, getSource().size())
					.collect(TreeMap::new, (map, index) -> map.put(index, getSource().get(index)), TreeMap::putAll);
		}

		return sorter != null ? sourceMap.entrySet().stream()
				.sorted((o1, o2) -> sorter.compare(o1.getValue(), o2.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList()) : sourceMap.keySet();
	}

	public Predicate<? super T> getPredicate() {
		return this.predicate.get();
	}

	/**
	 * Specifies the predicate used to filter the source list.
	 */
	public PredicateProperty<T> predicateProperty() {
		return this.predicate;
	}

	public void setPredicate(Predicate<T> predicate) {
		this.predicate.set(predicate);
	}

	public Comparator<T> getComparator() {
		return this.comparator.get();
	}

	/**
	 * Specifies the comparator used to sort the source list.
	 *
	 * @see #setComparator(Comparator, boolean)
	 */
	public ComparatorProperty<T> comparatorProperty() {
		return this.comparator;
	}

	public void setComparator(Comparator<T> comparator) {
		this.reversed = false;
		this.comparator.set(comparator);
	}

	/**
	 * This method is NECESSARY if using a reversed comparator,
	 * a special flag is set to true and {@link #sourceToView(int)} behaves accordingly.
	 */
	public void setComparator(Comparator<T> comparator, boolean reversed) {
		this.reversed = reversed;
		this.comparator.set(comparator);
	}

	/**
	 * Specifies if a reversed comparator is being used.
	 */
	public boolean isReversed() {
		return reversed;
	}

	/**
	 * Communicates to the transformed list, specifically to {@link #getViewIndex(int)},
	 * if the list is sorted in reversed order.
	 */
	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Calls {@link #update()}.
	 */
	@Override
	protected void sourceChanged(ListChangeListener.Change<? extends T> c) {
		beginChange();
		update();
		endChange();
	}

	/**
	 * @return the number of items in the transformable list
	 */
	@Override
	public int size() {
		return indexes.size();
	}

	/**
	 * Retrieves and return the item at the given index in the transformable list.
	 * This means transformations due to {@link #predicateProperty()} or {@link #comparatorProperty()}
	 * are taken into account.
	 */
	@Override
	public T get(int index) {
		if (index > size()) {
			throw new IndexOutOfBoundsException(index);
		} else {
			return getSource().get(indexes.get(index));
		}
	}

	@Override
	public int getSourceIndex(int index) {
		if (index > size()) {
			throw new IndexOutOfBoundsException(index);
		} else {
			return indexes.get(index);
		}
	}

	@Override
	public int getViewIndex(int index) {
		int viewIndex = reversed ?
				Collections.binarySearch(indexes, index, Collections.reverseOrder()) :
				Collections.binarySearch(indexes, index);
		return viewIndex < 0 ? -1 : viewIndex;
	}
}
