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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Helper class to manage unidirectional bindings.
 * <p></p>
 * A bindings is basically a listener attached to an observable which acts as "source",
 * when the source changes the bound property is updated.
 * <p></p>
 * This raises an issue though, in JavaFX if a property is bound the value cannot be changed
 * with the `set(...)` method as it would throw an exception. Unfortunately there's no way to avoid the check unless you override
 * the 'isBound()' method, let's see an example:
 * <p></p>
 * <pre>
 * {@code
 *         BindingManager<Number> bindingManager = new BindingManager<>();
 *         IntegerProperty property = new SimpleIntegerProperty() {
 *             @Override
 *             public boolean isBound() {
 *                 return super.isBound() && !bindingManager.isIgnoreBound();
 *             }
 *         };
 *         IntegerProperty source = new SimpleIntegerProperty();
 *
 *         bindingManager.provideHelperFactory(other -> new BindingHelper<>() {
 *             @Override protected void updateBound(Number newValue) { property.set(newValue.intValue()); }
 *         });
 *         bindingManager.getBindingHelper(source).bind(source);
 *         source.set(8);
 * }
 * </pre>
 * <p>
 * There's also another correlated issue. When you bind a JavaFX property it stores a reference to the observable, the isBound()
 * method simply checks if that reference is not null. Since it's a private variable we have no way to set it so the above code should be changed a little:
 * <pre>
 * {@code
 *      // The only thing to change is the isBound() override...
 *      IntegerProperty property = new SimpleIntegerProperty() {
 *          @Override
 *          public boolean isBound() {
 *              return bindingManager.isBound() && !bindingManager.isIgnoreBound();
 *          }
 *      };
 * }
 * </pre>
 * <p></p>
 * The "bound" and "ignoreBound" flags are managed by default by these methods {@link #afterBind()}, {@link #afterUnbind()}.
 * {@link #beforeUpdate()}, {@link #afterUpdate()}.
 * <p></p>
 * I know, it's not the most elegant solution, but it works. Maybe another way would be to override the {@link #beforeUpdate()} and
 * {@link #afterUpdate()} int the "provideFactory" call to unbind the property temporarily and re-bind it immediately after the update,
 * but the above method is the recommended one though.
 * <p></p>
 * So this helper has a change listener that will be attached to the source property which calls {@link #beforeUpdate()} before calling
 * {@link #updateBound(Object)} and then calls {@link #afterUpdate()} immediately after. This way a property which has its "isBound()" method overridden like the
 * above one, will call {@link BindingManager#isIgnoreBound()} which is a delegate to this method {@link #isIgnoreBound()} and bypass the bound check.
 *
 * @param <T> the properties' value type
 */
public abstract class BindingHelper<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ChangeListener<? super T> listener = (observable, oldValue, newValue) -> {
        beforeUpdate();
        updateBound(newValue);
        afterUpdate();
    };
    protected boolean bound;
    protected boolean ignoreBound;
    private ObservableValue<? extends T> observedValue;

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Abstract method, it's needed to implement this in order to specify the way the bound property will be updated.
     * <p></p>
     * A simple implementation could just be {@code property.set(newValue);}.
     */
    protected abstract void updateBound(T newValue);

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Stores the reference of the given property and adds the listener to it.
     * <p>
     * At the end calls {@link #afterBind()}.
     */
    public void bind(ObservableValue<? extends T> source) {
        observedValue = source;
        observedValue.addListener(listener);
        afterBind();
    }

    /**
     * Removes the listener from the source property and then sets the reference to null.
     * <p>
     * Calls {@link #afterUnbind()} at the end.
     */
    public void unbind() {
        observedValue.removeListener(listener);
        observedValue = null;
        afterUnbind();
    }

    /**
     * @return the "bound" flag state
     */
    public boolean isBound() {
        return bound;
    }

    /**
     * @return the "ignoreBound" flag state
     */
    public boolean isIgnoreBound() {
        return ignoreBound;
    }

    /**
     * By default sets the "ignoreBound" flag to false.
     */
    protected void afterUpdate() {
        ignoreBound = false;
    }

    /**
     * By default sets the "ignoreBound" flag to true.
     */
    protected void beforeUpdate() {
        ignoreBound = true;
    }

    /**
     * By default sets the "bound" flag to true.
     */
    protected void afterBind() {
        bound = true;
    }

    /**
     * By default sets the "bound" flag to false.
     */
    protected void afterUnbind() {
        bound = false;
    }
}
