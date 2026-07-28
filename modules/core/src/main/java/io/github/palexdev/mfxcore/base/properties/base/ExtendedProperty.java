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

package io.github.palexdev.mfxcore.base.properties.base;

import java.util.Objects;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import static java.util.Optional.ofNullable;

/// Base interface for all _extended_ properties. Enriches a generic [Property] with the concept of a **default value**,
/// and a bunch of convenience methods built on top of it.
///
/// Contrary to what one may expect, this is not a new property implementation, but rather a wrapper based on composition.
/// Implementations only have to specify the wrapped property, [#delegate()], and its [#defaultValue()]. Every method of
/// the [Property] API is then implemented by default through delegation. This way, any existing property can be turned
/// into an [ExtendedProperty] without reimplementing a thing, see [#wrap(Property, Object)].
///
/// On top of the standard API you get:
/// - [#reset()] to restore the default value
/// - [#isDefault()] to check whether the current value still is the default one
/// - [#getOrDefault()] and the [#getOrElse(Object)] variants to never deal with `null` values
///
/// @param <T> the type of the wrapped value
public interface ExtendedProperty<T> extends Property<T> {

    /// @return the property's default value
    T defaultValue();

    default ExtendedProperty<T> set(T value) {
        setValue(value);
        return this;
    }

    /// Sets the property's value back to the [#defaultValue()].
    default ExtendedProperty<T> reset() {
        return set(defaultValue());
    }

    /// @return whether the current value is equal to the [#defaultValue()]
    default boolean isDefault() {
        return Objects.equals(getValue(), defaultValue());
    }

    /// @return the current value, or the [#defaultValue()] if it is `null`
    default T getOrDefault() {
        return getOrElse(defaultValue());
    }

    /// @return the current value, or the given fallback if it is `null`
    default T getOrElse(T fallback) {
        return ofNullable(getValue()).orElse(fallback);
    }

    /// Same as [#getOrElse(Object)], but the fallback is computed lazily, only when the current value is `null`.
    default T getOrElse(Supplier<T> fallback) {
        return ofNullable(getValue()).orElseGet(fallback);
    }

    /// @return the wrapped property, the one to which every method of the [Property] API is delegated
    Property<T> delegate();

    //@formatter:off

    /// Wraps the given property in an [ExtendedProperty] which uses the given value as its [#defaultValue()].
    ///
    /// **Note:** the default value is not set as the initial value, to do so call [#reset()] on the new instance.
    /// (this note does not apply when using [PropUtils])
    static <T> ExtendedProperty<T> wrap(Property<T> property, T defaultValue) {
        return new ExtendedProperty<>() {
            @Override public T defaultValue() {return defaultValue;}
            @Override public Property<T> delegate() {return property;}
        };
    }

    //================================================================================
    // Delegate Methods
    //================================================================================

    // Listeners
    @Override default void addListener(ChangeListener<? super T> listener) {delegate().addListener(listener);}
    @Override default void removeListener(ChangeListener<? super T> listener) {delegate().removeListener(listener);}
    @Override default void addListener(InvalidationListener listener) {delegate().addListener(listener);}
    @Override default void removeListener(InvalidationListener listener) {delegate().removeListener(listener);}

    // Value
    @Override default T getValue() {return delegate().getValue();}
    @Override default void setValue(T value) {delegate().setValue(value);}

    // Bindings
    @Override default void bind(ObservableValue<? extends T> observable) {delegate().bind(observable);}
    @Override default void unbind() {delegate().unbind();}
    @Override default boolean isBound() {return delegate().isBound();}
    @Override default void bindBidirectional(Property<T> other) {delegate().bindBidirectional(other);}
    @Override default void unbindBidirectional(Property<T> other) {delegate().unbindBidirectional(other);}

    // Bean Info
    @Override default Object getBean() {return delegate().getBean();}
    @Override default String getName() {return delegate().getName();}
}
