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

package io.github.palexdev.mfxcore.filter.base;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.palexdev.mfxcore.base.beans.BiPredicateBean;
import io.github.palexdev.mfxcore.base.beans.FilterBean;
import io.github.palexdev.mfxcore.enums.ChainMode;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/// Base class for all filters.
///
/// A filter is a class capable of operating on a given T object type for
/// a given U field of that object.
///
/// In other words, it is capable of extracting a field U from an object T (this is the extractor function)
/// and producing a [Predicate] given a certain input (also called query) and it's a String.
///
/// To make the filter system flexible and yet highly specialized, every implementation must specify a
/// [StringConverter] which is used to convert the query to an object of type U.
///
/// At this point we have all the basic elements to describe how the [Predicate] is produced.
/// Every implementation of this base class has some predefined [BiPredicate] which operate on U objects.
/// The query is converted to an object of type U, and the extractor gets the U field from a T object, both U
/// objects are fed to the [BiPredicate]. In code:
/// ```
/// // We have the query...
/// String query = ...;
/// U convertedQuery = converter.fromString(query);
/// // We can build a Predicate<T> by doing this...
/// Predicate<T> predicate = t -> biPredicate.test(extractor.apply(t), convertedQuery);
/// ```
///
/// Filters are intended to be used with UI controls, they provide an interactive way to build a [Predicate]
/// and filter a collection with generics; however, you can also use them without a UI.
///  - Every filter has a name
///  - [BiPredicates][BiPredicate] are wrapped in a [BiPredicateBean]
///  - The predicate to use is "selected" with an index property (ideal for combo boxes), see [#predicateFor(String)].
///
/// @param <T> the type of objects to filter
/// @param <U> the objects' field on which to operate
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

    /// Every implementation of [AbstractFilter] must define some default [BiPredicates][BiPredicate].
    protected abstract ObservableList<BiPredicateBean<U, U>> defaultPredicates();

    /// Allows adding some extra [BiPredicateBeans][BiPredicateBean] alongside the default ones.
    @SuppressWarnings("unchecked")
    protected abstract AbstractFilter<T, U> extend(BiPredicateBean<U, U>... predicateBeans);

    //================================================================================
    // Methods
    //================================================================================

    /// Converts a given input String to an object of type U using
    /// the [StringConverter] specified by this filter.
    public U getValue(String input) {
        return getConverter().fromString(input);
    }

    /// Produces a [Predicate] from the given input.
    ///
    /// First checks if a [BiPredicate] is selected by checking
    /// the selected index property, see [#checkIndex()].
    ///
    /// Then converts the input to an object of type U by using [#getValue(String)],
    /// and then returns a Predicate that applies the selected BiPredicate to the extracted U field of T
    /// and the converted U input.
    ///
    /// In code: `return t -> biPredicate.test(extractor.apply(t), convertedQuery);`
    public Predicate<T> predicateFor(String input) {
        checkIndex();
        int index = getSelectedPredicateIndex();
        U convertedInput = getValue(input);
        return t -> predicates.get(index).predicate().test(extractor.apply(t), convertedInput);
    }

    /// Produces a [Predicate] from the given input and [BiPredicate].
    ///
    /// First converts the input to an object of type U by using [#getValue(String)],
    /// and then returns a Predicate that applies the given BiPredicate to the extracted U field of T
    /// and the converted U input.
    ///
    /// In code: `return t -> biPredicate.test(extractor.apply(t), convertedQuery);`
    ///
    /// **Warning:** to be honest, this method should have been removed, but I wanted to keep it
    /// since it adds some flexibility to the filter system. Using this method may lead to inconsistencies in UI controls
    /// because the given argument is not a [BiPredicateBean]. That means that it won't be added to the predicates' list
    /// of this filter, and the selected predicate index property won't be updated.
    /// This also means that any other method that relies on that index will fail.
    public Predicate<T> predicateFor(String input, BiPredicate<U, U> biPredicate) {
        U convertedInput = getValue(input);
        return t -> biPredicate.test(extractor.apply(t), convertedInput);
    }

    /// Converts this filter to a [FilterBean] from the given input.
    ///
    ///
    /// Checks for the selected BiPredicate, see [#checkIndex()].
    public FilterBean<T, U> toFilterBean(String input) {
        checkIndex();
        int index = getSelectedPredicateIndex();
        BiPredicateBean<U, U> bean = predicates.get(index);
        return new FilterBean<>(input, this, bean);
    }

    /// Converts this filter to a [FilterBean] from the given input and [ChainMode].
    ///
    /// Checks for the selected BiPredicate, see [#checkIndex()].
    public FilterBean<T, U> toFilterBean(String input, ChainMode mode) {
        checkIndex();
        int index = getSelectedPredicateIndex();
        BiPredicateBean<U, U> bean = predicates.get(index);
        return new FilterBean<>(input, this, bean, mode);
    }

    /// Converts this filter to a [FilterBean] from the given input, [BiPredicateBean] and [ChainMode].
    public FilterBean<T, U> toFilterBean(String input, BiPredicateBean<U, U> bean, ChainMode mode) {
        return new FilterBean<>(input, this, bean, mode);
    }

    /// Used in methods which rely on a selected [BiPredicateBean].
    ///
    /// @throws IllegalStateException if the selected index is not valid
    private void checkIndex() throws IllegalStateException {
        int index = getSelectedPredicateIndex();
        if (index < 0) {
            throw new IllegalStateException("No predicate selected for filter: " + name);
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the filter's name
    public String name() {
        return name;
    }

    /// @return the function used to extract a field of type U from an object of type T
    public Function<T, U> getExtractor() {
        return extractor;
    }

    /// @return the list of usable [BiPredicates][BiPredicate], each wrapped in a [BiPredicateBean]
    public ObservableList<BiPredicateBean<U, U>> getPredicates() {
        return predicates;
    }

    public int getSelectedPredicateIndex() {
        return selectedPredicateIndex.get();
    }

    /// Used to specify the selected [BiPredicateBean].
    public IntegerProperty selectedPredicateIndexProperty() {
        return selectedPredicateIndex;
    }

    public void setSelectedPredicateIndex(int selectedPredicateIndex) {
        this.selectedPredicateIndex.set(selectedPredicateIndex);
    }

    /// @return the [StringConverter] used to convert the input String to an object of type U
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
