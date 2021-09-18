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

package io.github.palexdev.materialfx.beans.properties.synced;

import io.github.palexdev.materialfx.beans.properties.base.SynchronizedProperty;
import io.github.palexdev.materialfx.bindings.BidirectionalBindingHelper;
import io.github.palexdev.materialfx.bindings.BindingHelper;
import io.github.palexdev.materialfx.bindings.BindingManager;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * Implementation of {@link SynchronizedProperty} for generic values.
 *
 * @param <T> the type of the wrapped Object
 */
public class SynchronizedObjectProperty<T> extends ReadOnlyObjectWrapper<T> implements SynchronizedProperty<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ReadOnlyBooleanWrapper waiting = new ReadOnlyBooleanWrapper();
    protected final BindingManager<T> bindingManager = new BindingManager<>();

    //================================================================================
    // Constructors
    //================================================================================
    public SynchronizedObjectProperty() {
        initialize();
    }

    public SynchronizedObjectProperty(T initialValue) {
        super(initialValue);
        initialize();
    }

    public SynchronizedObjectProperty(Object bean, String name) {
        super(bean, name);
        initialize();
    }

    public SynchronizedObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a listener to the property by calling {@link ExecutionUtils#executeWhen(BooleanExpression, Runnable, Runnable, boolean, boolean, boolean, boolean)}
     * to call {@link #fireValueChangedEvent()} when the property is awakened, {@link #awake()}.
     * <p></p>
     * Also provides default factories for {@link BindingHelper} and {@link BidirectionalBindingHelper},
     * see {@link #provideHelperFactory(Function)}, {@link SynchronizedProperty#provideBidirectionalHelperFactory(Function)}.
     */
    private void initialize() {
        ExecutionUtils.executeWhen(
                waiting,
                () -> {},
                this::fireValueChangedEvent,
                false,
                false,
                false,
                false
        );

        provideHelperFactory(property -> new BindingHelper<>() {
            @Override protected void updateBound(T newValue) { set(newValue); }
        });
        provideBidirectionalHelperFactory((property) -> new BidirectionalBindingHelper<>(property) {
            @Override protected void updateThis(T newValue) { set(newValue); }
            @Override protected void updateOther(Property<T> other, T newValue) { other.setValue(newValue); }
        });
    }

    //================================================================================
    // Implemented/Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAndWait(T value, ObservableValue<?> observable) {
        if (!Helper.check(this, value, observable)) return;

        waiting.set(true);
        ExecutionUtils.executeWhen(
                observable,
                (oldValue, newValue) -> awake(),
                false,
                (oldValue, newValue) -> true,
                true
        );
        set(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWaiting() {
        return waiting.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty waiting() {
        return waiting.getReadOnlyProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void awake() {
        waiting.set(false);
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to not fire a change event if {@link #waiting()} is true.
     */
    @Override
    protected void fireValueChangedEvent() {
        if (isWaiting()) return;
        super.fireValueChangedEvent();
    }

    //================================================================================
    // Binding
    //================================================================================

    /**
     * Creates a unidirectional bindings with the given observable.
     * <p>
     * Creates the bindings helper, {@link BindingManager#getBindingHelper(ObservableValue)}, and then
     * creates the bind {@link BindingHelper#bind(ObservableValue)}.
     * <p></p>
     * If the property is already bound it is automatically unbound before bindings to the new observable.
     *
     * @throws IllegalArgumentException if the given observable is the property itself
     * @see BindingHelper
     */
    @Override
    public void bind(ObservableValue<? extends T> source) {
        if (this == source) {
            throw new IllegalArgumentException("Cannot bind to itself!");
        }

        if (isBound()) unbind();
        bindingManager.getBindingHelper(source).bind(source);
    }

    /**
     * Creates a bidirectional bindings between this property and the given property.
     * <p>
     * Creates the bindings helper, {@link BindingManager#getBidirectionalBindingHelper(Property)},
     * and then creates the bind {@link BidirectionalBindingHelper#bind(Property)}.
     * <p></p>
     * If the property is already bound unidirectionally it is automatically unbound.
     * <p>
     * If the property is already bound bidirectionally it won't be automatically unbound, just like JavaFX,
     * this way you can have multiple bidirectional bindings
     *
     * @throws IllegalArgumentException if the given observable is the property itself
     * @see BidirectionalBindingHelper
     */
    @Override
    public void bindBidirectional(Property<T> other) {
        if (this == other) {
            throw new IllegalArgumentException("Cannot bind to itself!");
        }

        if (isBound()) unbind();
        bindingManager.getBidirectionalBindingHelper(this).bind(other);
    }

    /**
     * Overridden to call {@link BindingManager#unbind()}.
     */    @Override
    public void unbind() {
        bindingManager.unbind();
    }

    /**
     * Overridden to call {@link BindingManager#unbindBidirectional(Property)}.
     */
    @Override
    public void unbindBidirectional(Property<T> other) {
        bindingManager.unbindBidirectional(other);
    }

    /**
     * Delegate method for {@link BindingManager#clearBidirectional()}.
     */
    public void clearBidirectional() {
        bindingManager.clearBidirectional();
    }

    /**
     * Overridden to check the {@link BindingManager#isBound()} flag value and {@link BindingManager#isIgnoreBound()}.
     *
     * @return true only if `BindingManager.isBound()` is true and `isIgnoreBound()` is false
     */
    @Override
    public boolean isBound() {
        return bindingManager.isBound() && !bindingManager.isIgnoreBound();
    }

    /**
     * Delegate method for {@link BindingManager#provideHelperFactory(Function)}.
     */
    @Override
    public void provideHelperFactory(Function<ObservableValue<? extends T>, BindingHelper<T>> factory) {
        bindingManager.provideHelperFactory(factory);
    }

    /**
     * Delegate method for {@link BindingManager#provideBidirectionalHelperFactory(Function)}.
     */
    @Override
    public void provideBidirectionalHelperFactory(Function<Property<T>, BidirectionalBindingHelper<T>> factory) {
        bindingManager.provideBidirectionalHelperFactory(factory);
    }
}
