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

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage bidirectional bindings.
 * <p></p>
 * A bidirectional bindings is basically a listener attached to a property and another listener
 * attached to a variable number of other properties since you can have multiple bidirectional bindings.
 * <p></p>
 * This helper store the reference of the "this" property and then has a list of all the other properties.
 * <p>
 * It has two change listeners, one to listen for changes of the "this" property, called "thisListener",
 * and one attached to every other property "this" is bound to, called "otherListener".
 * <p>
 * <p> - "thisListener" is responsible for updating the other properties
 * <p> - "otherListener" is responsible for updating "this" property
 * <p></p>
 * When "this" changes the listener executes these actions in sequence:
 * <p> - Checks !{@link #isFromThis()}
 * <p> - Calls {@link #beforeUpdateOthers()}
 * <p> - Calls {@link #updateOthers(Object)}
 * <p> - Calls {@link #afterUpdateOthers()}
 * <p>
 * When one of the other properties change the listener executes these actions in sequence:
 * <p> - {@link #beforeUpdateThis()}
 * <p> - {@link #updateThis(Object)}
 * <p> - {@link #afterUpdateThis()}
 * <p></p>
 * You may ask: "What is that `isFromThis()`?"
 * <p>
 * When one of the other properties changes the "this" property is updated, this will cause the "thisListener"
 * to trigger as well and call {@link #updateOthers(Object)}, the issue is that if you have multiple bindings this will
 * update all the other properties but that's incorrect behavior, let's see what I mean with an example:
 * <pre>
 * {@code
 *         IntegerProperty propertyA = new SimpleIntegerProperty();
 *         IntegerProperty propertyB = new SimpleIntegerProperty();
 *         IntegerProperty propertyC = new SimpleIntegerProperty();
 *         BindingManager<Number> bindingManager = new BindingManager<>();
 *         bindingManager.provideBidirectionalHelperFactory((property) -> new BidirectionalBindingHelper<>(property) {
 *             @Override protected void updateThis(Number newValue) { property.setValue(newValue); }
 *             @Override protected void updateOther(Property<Number> other, Number newValue) { other.setValue(newValue); }
 *         });
 *
 *         bindingManager.getBidirectionalBindingHelper(propertyA).bind(propertyB);
 *         bindingManager.getBidirectionalBindingHelper(propertyA).bind(propertyC);
 *
 *         propertyA.set(8); // All properties must be 8
 *         propertyB.set(10); // Only A and B will be 10, C will remain 8
 *         propertyC.set(12); // Only A and C will be 12, B will remain 10
 *         // This is the correct behavior, without that flag all properties would always have the same value
 * }
 * </pre>
 * <p>
 * The solution to this issue is to set a flag, "fromThis", to true before calling {@link #updateThis(Object)} so
 * that the "thisListener" won't trigger the {@link #updateOthers(Object)} process.
 * <p>
 * After updateThis the flag is reset with {@link #afterUpdateThis()}.
 * <p></p>
 * Just like JavaFX to remove a bidirectional bindings you must pass the reference of the property you want to unbind,
 * {@link #unbind(Property)}, but this helper also offers the possibility of remove all bidirectional bindings by calling
 * {@link #clear()}. Note that clearing all the bindings will keep the helper still usable, to complete the disposal
 * you should also call {@link #dispose()}.
 *
 * @param <T> the properties' value type
 */
public abstract class BidirectionalBindingHelper<T> {
    //================================================================================
    // Properties
    //================================================================================
    private Property<T> thisProperty;
    private final List<Property<T>> properties = new ArrayList<>();
    private final ChangeListener<? super T> thisListener = (observable, oldValue, newValue) -> {
        if (!isFromThis()) {
            beforeUpdateOthers();
            updateOthers(newValue);
            afterUpdateOthers();
        }
    };
    private final ChangeListener<? super T> otherListener = (observable, oldValue, newValue) -> {
        beforeUpdateThis();
        updateThis(newValue);
        afterUpdateThis();
    };
    private boolean fromThis;

    //================================================================================
    // Constructors
    //================================================================================
    public BidirectionalBindingHelper(Property<T> thisProperty) {
        this.thisProperty = thisProperty;
        thisProperty.addListener(thisListener);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Abstract method, it's needed to implement this in order to specify the way the "this" property
     * will be updated.
     * <p></p>
     * A simple implementation could just be {@code property.set(newValue);}.
     */
    protected abstract void updateThis(T newValue);

    /**
     * Abstract method, it's needed to implement this in order to specify the way the other properties
     * will be updated.
     * <p></p>
     * A simple implementation could just be {@code other.set(newValue);}.
     */
    protected abstract void updateOther(Property<T> other, T newValue);

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Responsible for updating all the other properties by iterating
     * over the list of the other properties an calling {@link #updateOther(Property, Object)}
     * for each of them.
     */
    protected void updateOthers(T newValue) {
        properties.forEach(property -> updateOther(property, newValue));
    }

    /**
     * Adds the given property to the others list, adds the "otherListener" to it
     * and then calls {@link #afterBind()}.
     */
    public void bind(Property<T> otherProperty) {
        properties.add(otherProperty);
        otherProperty.addListener(otherListener);
        afterBind();
    }

    /**
     * Remove the "otherListener" from the given properties, removes it from the others list
     * and then calls {@link #afterUnbind()}.
     */
    public void unbind(Property<T> otherProperty) {
        otherProperty.removeListener(otherListener);
        properties.remove(otherProperty);
        afterUnbind();
    }

    /**
     * Removes the "otherListener" from all the other properties and then
     * clears the others list.
     * <p></p>
     * Note that this won't remove the "thisListener" from the "this" property leaving this
     * helper still usable.
     */
    public void clear() {
        properties.forEach(property -> property.removeListener(otherListener));
        properties.clear();
    }

    /**
     * Removes the "thisListener" from the "this" property and sets its reference to null.
     * <p>
     * <b>WARNING:</b> this will make this helper unusable afterwards!
     */
    public void dispose() {
        thisProperty.removeListener(thisListener);
        thisProperty = null;
    }

    /**
     * @return true if this helper has been disposed
     */
    public boolean isDispose() {
        return thisProperty == null;
    }

    /**
     * @return the "fromThis" flag state
     */
    public boolean isFromThis() {
        return fromThis;
    }

    /**
     * By default sets the "fromThis" flag to false.
     */
    protected void afterUpdateThis() {
        fromThis = false;
    }

    /**
     * By default sets the "fromThis" flag to true.
     */
    protected void beforeUpdateThis() {
        fromThis = true;
    }

    /**
     * By default empty.
     */
    protected void afterUpdateOthers() {}

    /**
     * By default empty.
     */
    protected void beforeUpdateOthers() {}

    /**
     * By default empty.
     */
    protected void afterBind() {}

    /**
     * By default empty.
     */
    protected void afterUnbind() {}
}
