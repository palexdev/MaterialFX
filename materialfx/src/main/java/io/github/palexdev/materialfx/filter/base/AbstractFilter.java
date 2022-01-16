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

package io.github.palexdev.materialfx.filter.base;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.enums.ChainMode;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base class for all filters.
 * <p></p>
 * A filter is a class capable of operating on a given T object type for
 * a given U field of that object.
 * <p>
 * In other words, it is capable of extracting a field U from an object T (this is the extractor function)
 * and producing a {@link Predicate} given a certain input (also called query) and it's a String.
 * <p>
 * To make the filter system flexible and yet highly specialized, every implementation must specify a
 * {@link StringConverter} which is used to convert the query to an object of type U.
 * <p></p>
 * At this point we have all the basic elements to describe how the {@link Predicate} is predicate is produced.
 * Every implementation of this base class has some predefined {@link BiPredicate} which operate on U objects.
 * The query is converted to an object of type U, and the extractor gets the U field from a T object, both U
 * objects are fed to the {@link BiPredicate}. In code:
 * <pre>
 * {@code
 *      // We have the query...
 *      String query = ...;
 *      U convertedQuery = converter.fromString(query);
 *
 *      // We can build a Predicate<T> by doing this...
 *      Predicate<T> predicate = t -> biPredicate.test(extractor.apply(t), convertedQuery);
 * }
 * </pre>
 * <p></p>
 * Filters are intended to be used with UI controls, they provide an interactive way to build a {@link Predicate}
 * and filter a collection with generics, however you can also use them without an UI, however some other
 * aspects needs to be discussed because they are strictly related to UI usage:
 * <p> Every filter has a name, see {@link MFXFilterPane} documentation for an example
 * <p> {@link BiPredicate}s are wrapped in a {@link BiPredicateBean}
 * <p> The BiPredicate to use is "selected" with an index property (ideal for comboboxes), see {@link #predicateFor(String)}.
 *
 * @param <T> the type of objects to filter
 * @param <U> the objects' field on which to operate
 */
public abstract class AbstractFilter<T, U> {
	//================================================================================
	// Properties
	//================================================================================
	private final String name;
	private final Function<T, U> extractor;
	protected final ObservableList<BiPredicateBean<U, U>> predicates;
	protected final IntegerProperty selectedPredicateIndex = new SimpleIntegerProperty(-1);
	protected final StringConverter<U> converter;

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractFilter(String name, Function<T, U> extractor, StringConverter<U> converter) {
		this.name = name;
		this.extractor = extractor;
		this.converter = converter;
		this.predicates = defaultPredicates();
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Every implementation of {@link AbstractFilter} must define some default {@link BiPredicate}s.
	 */
	protected abstract ObservableList<BiPredicateBean<U, U>> defaultPredicates();

	/**
	 * Allows to add some extra {@link BiPredicateBean}s alongside the default ones.
	 */
	@SuppressWarnings("unchecked")
	protected abstract AbstractFilter<T, U> extend(BiPredicateBean<U, U>... predicateBeans);

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Converts a given input String to an object of type U using
	 * the {@link StringConverter} specified by this filter.
	 */
	public U getValue(String input) {
		return getConverter().fromString(input);
	}

	/**
	 * Produces a {@link Predicate} from the given input.
	 * <p></p>
	 * First checks if a {@link BiPredicate} is selected by checking
	 * the selected index property, see {@link #checkIndex()}.
	 * <p>
	 * Then converts the input to an object of type U by using {@link #getValue(String)},
	 * and then returns a Predicate that applies the selected BiPredicate to the extracted U field of T
	 * and the converted U input.
	 * <p></p>
	 * In code:
	 * <pre>
	 * {@code
	 *      return t -> biPredicate.test(extractor.apply(t), convertedQuery);
	 * }
	 * </pre>
	 */
	public Predicate<T> predicateFor(String input) {
		checkIndex();
		int index = getSelectedPredicateIndex();
		U convertedInput = getValue(input);
		return t -> predicates.get(index).predicate().test(extractor.apply(t), convertedInput);
	}

	/**
	 * Produces a {@link Predicate} from the given input and {@link BiPredicate}.
	 * <p></p>
	 * First converts the input to an object of type U by using {@link #getValue(String)},
	 * and then returns a Predicate that applies the given BiPredicate to the extracted U field of T
	 * and the converted U input.
	 * <p></p>
	 * In code:
	 * <pre>
	 * {@code
	 *      return t -> biPredicate.test(extractor.apply(t), convertedQuery);
	 * }
	 * </pre>
	 * <p></p>
	 * <b>WARN:</b> to be honest this method should have been removed but I wanted to keep it
	 * since it adds some flexibility to the filter system. Note that using this method may lead
	 * to inconsistencies in UI controls since the given argument is not a {@link BiPredicateBean},
	 * which means that it won't be added to the predicates list of this filter, and the selected predicate index
	 * property won't be updated. This also means that any other method that relies on that index will fail.
	 */
	public Predicate<T> predicateFor(String input, BiPredicate<U, U> biPredicate) {
		U convertedInput = getValue(input);
		return t -> biPredicate.test(extractor.apply(t), convertedInput);
	}

	/**
	 * Converts this filter to a {@link FilterBean} from the given input.
	 * <p></p>
	 * Checks for the selected BiPredicate, see {@link #checkIndex()}.
	 */
	public FilterBean<T, U> toFilterBean(String input) {
		checkIndex();
		int index = getSelectedPredicateIndex();
		BiPredicateBean<U, U> bean = predicates.get(index);
		return new FilterBean<>(input, this, bean);
	}

	/**
	 * Converts this filter to a {@link FilterBean} from the given input and {@link ChainMode}.
	 * <p></p>
	 * Checks for the selected BiPredicate, see {@link #checkIndex()}.
	 */
	public FilterBean<T, U> toFilterBean(String input, ChainMode mode) {
		checkIndex();
		int index = getSelectedPredicateIndex();
		BiPredicateBean<U, U> bean = predicates.get(index);
		return new FilterBean<>(input, this, bean, mode);
	}

	/**
	 * Converts this filter to a {@link FilterBean} from the given input, {@link BiPredicateBean} and {@link ChainMode}.
	 */
	public FilterBean<T, U> toFilterBean(String input, BiPredicateBean<U, U> bean, ChainMode mode) {
		return new FilterBean<>(input, this, bean, mode);
	}

	/**
	 * Used in methods which rely on a selected {@link BiPredicateBean}.
	 *
	 * @throws IllegalStateException if the selected index is not valid
	 */
	private void checkIndex() throws IllegalStateException {
		int index = getSelectedPredicateIndex();
		if (index < 0) {
			throw new IllegalStateException("No predicate selected for filter: " + name);
		}
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the filter's name
	 */
	public String name() {
		return name;
	}

	/**
	 * @return the function used to extract a field of type U from an object of type T
	 */
	public Function<T, U> getExtractor() {
		return extractor;
	}

	/**
	 * @return the list of usable {@link BiPredicate}s, each wrapped in a {@link BiPredicateBean}
	 */
	public ObservableList<BiPredicateBean<U, U>> getPredicates() {
		return predicates;
	}

	public int getSelectedPredicateIndex() {
		return selectedPredicateIndex.get();
	}

	/**
	 * Used to specify the selected {@link BiPredicateBean}.
	 */
	public IntegerProperty selectedPredicateIndexProperty() {
		return selectedPredicateIndex;
	}

	public void setSelectedPredicateIndex(int selectedPredicateIndex) {
		this.selectedPredicateIndex.set(selectedPredicateIndex);
	}

	/**
	 * @return the {@link StringConverter} used to convert the input String to an object of type U
	 */
	public StringConverter<U> getConverter() {
		return converter;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String toString() {
		return name;
	}
}
