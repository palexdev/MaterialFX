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

package io.github.palexdev.materialfx.beans.properties.base;

import io.github.palexdev.materialfx.bindings.BiBindingHelper;
import io.github.palexdev.materialfx.bindings.BindingHelper;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;

import java.util.function.Supplier;

/**
 * Public API of every synchronized property.
 * <p>
 * Extends {@link Property}.
 * <p></p>
 * A synchronized property is a property that doesn't immediately fire a change event when it changes, but it waits
 * for some other observable. We could say that a synchronized property, "synchronized" to another observable, acts
 * "atomically" in terms of changes/change listeners/invalidation listeners. Let's see an example to better understand what I mean:
 * <p></p>
 * <pre>
 * {@code
 *      // Let's say we have two properties dependant on each other
 *      // This means that when one changes the other has to change and
 *      // when a change event is caught with a listener both should be updated!
 *      // In case of JavaFX properties...
 *      IntegerProperty propertyA = new SimpleIntegerProperty();
 *      IntegerProperty propertyB = new SimpleIntegerProperty();
 *      propertyA.addListener((ob, ol, nw) -> System.out.println("B is: " + propertyB.get()));
 *      propertyB.addListener((ob, ol, nw) -> System.out.println("A is: " + propertyA.get()));
 *
 *      // Now set the properties with an update method and check the print...
 *      // Oh, by default integer properties are 0 in JavaFX
 *      public void update() {
 *          propertyA.set(8);
 *          propertyB.set(10);
 *      }
 *
 *      // The printed text will be...
 *      // B is: 0
 *      // A is: 8
 *
 *      // Of course right? That's how JavaFX properties and listeners work, it's obvious
 *      // Well this behavior is not ideal when for example we work with selection models because
 *      // the selected index and item are strictly connected, their update must be "atomic"
 *      // Let's see with a synchronized property...
 *      SynchronizedIntegerProperty propertyA = new SynchronizedIntegerProperty();
 *      IntegerProperty propertyB = new SimpleIntegerProperty(); // The other property can be a generic observable, even of a different type
 *      propertyA.addListener((ob, ol, nw) -> System.out.println("B is: " + propertyB.get()));
 *      propertyB.addListener((ob, ol, nw) -> System.out.println("A is: " + propertyA.get()));
 *
 *      // The update method has to be changed a bit...
 *      public void update() {
 *          propertyA.setAndWait(8, propertyB);
 *          propertyB.set(10);
 *      }
 *
 *      // The printed text will be...
 *      // B is: 10
 *      // A is: 8
 *
 *      // That's why they are called "Synchronized" because they wait for some other observable to change as well before
 *      // firing a change event.
 *      // Further details can be found in methods documentation, and subclasses
 * }
 * </pre>
 * <p></p>
 * SynchronizedProperties override the binding mechanism as well because for some reason the default JavaFX implementation doesn't work
 * properly for them and also because the new mechanism is a lot more flexible, a bit more complex though, but you just need to read the
 * new classes' documentation, {@link BindingHelper}, {@link BiBindingHelper}.
 *
 * @param <T> the type of the wrapped value
 */
public interface SynchronizedProperty<T> extends Property<T> {

	/**
	 * Sets this property's state to "waiting" then uses {@link ExecutionUtils#executeWhen(Observable, Runnable, boolean, Supplier, boolean)}
	 * to "awake" the property when the given observable changes.
	 * <p></p>
	 * Just like JavaFX properties if the new value is the same as the current value the method returns and does nothing.
	 *
	 * @param value      the new value of the property
	 * @param observable the observable to wait for
	 * @throws IllegalArgumentException if the given observable is the property itself or if the passed observable
	 *                                  is another SynchronizedProperty, and it is already waiting for some other observable
	 * @throws IllegalStateException    if this property is already waiting for another observable
	 */
	void setAndWait(T value, ObservableValue<?> observable);

	/**
	 * @return whether this property is in waiting state
	 */
	boolean isWaiting();

	/**
	 * @return the waiting state property as a read only property
	 */
	ReadOnlyBooleanProperty waiting();

	/**
	 * Awakes the property by setting {@link #waiting()} to false.
	 * <p></p>
	 * This method should never be invoked by the user, the awakening is automatically
	 * managed by the property. If for some reason the property stays in waiting state you
	 * are probably doing something wrong.
	 */
	void awake();

	/**
	 * Helper class to avoid code duplication.
	 */
	class Helper {

		/**
		 * Check some parameters before proceeding with the set and wait method
		 *
		 * @param value      the new value of the property
		 * @param observable the observable to wait for
		 * @return whether the check failed
		 * @throws IllegalArgumentException if the given observable is the property itself or if the passed observable
		 *                                  is another SynchronizedProperty, and it is already waiting for some other observable
		 * @throws IllegalStateException    if the property is bound unidirectionally, or
		 *                                  if this property is already waiting for another observable
		 */
		public static <T> boolean check(SynchronizedProperty<T> property, T value, ObservableValue<?> observable) {
			if (observable == property) {
				throw new IllegalArgumentException("The passed property cannot be the same as this!" +
						" Proceeding with this method would lead to a deadlock.");
			}
			if (property.isBound()) {
				throw new IllegalStateException("A bound value cannot be set!");
			}
			if (property.isWaiting()) {
				throw new IllegalStateException("The property is already waiting for some other observable!");
			}
			if (observable instanceof SynchronizedProperty) {
				SynchronizedProperty<?> synchronizedProperty = (SynchronizedProperty<?>) observable;
				if (synchronizedProperty.isWaiting()) {
					throw new IllegalArgumentException("The passed property is already waiting for some other property!" +
							" Proceeding with this method would lead to a deadlock.");
				}
			}
			return property.getValue() == null || !property.getValue().equals(value);
		}
	}
}
