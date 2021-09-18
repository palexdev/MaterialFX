/*
 * Copyright (C) 2021 Parisi Alessandro
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

package io.github.palexdev.materialfx.bindings;

import io.github.palexdev.materialfx.beans.properties.base.SynchronizedProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * Helper class to manage unidirectional and bidirectional bindings for JavaFX properties.
 * <p></p>
 * This class holds four references:
 * <p> - A reference for the {@link BindingHelper} and a factory (Function) to build it
 * <p> - A reference for the {@link BidirectionalBindingHelper} and a factory (Function) to build it
 * <p>
 * Mostly relevant for {@link SynchronizedProperty} since those properties override the default bindings mechanism but
 * since it uses JavaFX base classes (Property and ObservableValue) it should be usable for everything.
 * <p>
 * There's an issue with unidirectional bindings though, see {@link #isIgnoreBound()}.
 *
 * <p></p>
 * <b>Note:</b> properties using this class must provide default factories before getting the helpers, so call
 * {@link #provideHelperFactory(Function)} for unidirectional bindings and {@link #provideBidirectionalHelperFactory(Function)} for
 * bidirectional bindings.
 *
 * @param <T> the properties' value type
 */
public class BindingManager<T> {
    //================================================================================
    // Properties
    //================================================================================
    private Function<ObservableValue<? extends T>, BindingHelper<T>> bindingHelperFactory;
    private BindingHelper<T> bindingHelper;

    private Function<Property<T>, BidirectionalBindingHelper<T>> bidirectionalBindingHelperFactory;
    private BidirectionalBindingHelper<T> bidirectionalBindingHelper;

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Replaces the {@link BindingHelper} instance by using the current factory and returns the new helper.
     *
     * @param source the observable to bind to
     * @return a new instance of {@link BindingHelper}
     */
    public BindingHelper<T> getBindingHelper(ObservableValue<? extends T> source) {
        bindingHelper = bindingHelperFactory.apply(source);
        return bindingHelper;
    }

    /**
     * Unlike {@link #getBindingHelper(ObservableValue)} this does not replace the {@link BidirectionalBindingHelper} reference
     * and builds it only if it's null or {@link BidirectionalBindingHelper#isDispose()} returns true.
     * This is because a property can have multiple bidirectional bindings at the same time so the same helper is used.
     *
     * @param property the first property
     * @return an instance of {@link BidirectionalBindingHelper}
     */
    public BidirectionalBindingHelper<T> getBidirectionalBindingHelper(Property<T> property) {
        if (bidirectionalBindingHelper == null || bidirectionalBindingHelper.isDispose()) {
            bidirectionalBindingHelper = bidirectionalBindingHelperFactory.apply(property);
        }
        return bidirectionalBindingHelper;
    }

    /**
     * Replaces the factory for {@link BindingHelper} with the given one.
     * <p>
     * The input of the function is the property to bind!
     * <p>
     * The method {@link #unbind()} is called beforehand.
     */
    public void provideHelperFactory(Function<ObservableValue<? extends T>, BindingHelper<T>> factory) {
        unbind();
        this.bindingHelperFactory = factory;
    }

    /**
     * Replaces the factory for {@link BindingHelper} with the given one.
     * <p>
     * The input of the function is the property to bind!
     * <p>
     * The method {@link #clearBidirectional()} is called beforehand;
     */
    public void provideBidirectionalHelperFactory(Function<Property<T>, BidirectionalBindingHelper<T>> factory) {
        clearBidirectional();
        this.bidirectionalBindingHelperFactory = factory;
    }

    /**
     * If the bindings helper instance is not null calls {@link BindingHelper#unbind()}
     * and then sets the reference to null.
     */
    public void unbind() {
        if (bindingHelper != null) {
            bindingHelper.unbind();
            bindingHelper = null;
        }
    }

    /**
     * If the bidirectional bindings helper instance is not null calls {@link BidirectionalBindingHelper#unbind(Property)}.
     * <p>
     * Unlike {@link #unbind()} the reference is not set to null since there may be others bindings still established.
     */
    public void unbindBidirectional(Property<T> otherProperty) {
        if (bidirectionalBindingHelper != null) {
            bidirectionalBindingHelper.unbind(otherProperty);
        }
    }

    /**
     * If the bidirectional bindings helper instance is not null calls {@link BidirectionalBindingHelper#clear()}.
     * <p>
     * Unlike {@link #unbindBidirectional(Property)} sets the reference to null at the end.
     */
    public void clearBidirectional() {
        if (bidirectionalBindingHelper != null) {
            bidirectionalBindingHelper.clear();
            bidirectionalBindingHelper = null;
        }
    }

    /**
     * Delegate method for {@link BidirectionalBindingHelper#dispose()}.
     */
    public void dispose() {
        bidirectionalBindingHelper.dispose();
    }

    /**
     * Delegate method for {@link BindingHelper#isBound()}.
     */
    public boolean isBound() {
        return bindingHelper != null && bindingHelper.isBound();
    }

    /**
     * If the bindings helper is not null checks if {@link BindingHelper#isIgnoreBound()} is true.
     * <p></p>
     * Delegate method, for documentation see {@link BindingHelper#isIgnoreBound()}.
     */
    public boolean isIgnoreBound() {
        return bindingHelper != null && bindingHelper.isIgnoreBound();
    }
}
