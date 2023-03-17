/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.materialfx.utils.others.observables;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Useful class to listen to changes for a given {@link ObservableValue} and perform any
 * specified action when it changes.
 * <p>
 * You can read this construct as "When condition changes, then do this"
 * <p>
 * This is just an abstract class that defines common properties and behavior, but it has two concrete
 * implementations, {@link OnChanged} and {@link OnInvalidated}.
 * <p>
 * This construct also allows to define one-shot listeners, meaning that the
 * above phrase changes like this: "When condition changes, then do this, then dispose(remove listener)"
 * <p></p>
 * <b>Note:</b>Once the construct is not needed anymore it's highly recommended to dispose it
 * using {@link #disposeFor(ObservableValue)} to avoid memory leaks.
 * When constructs and ObservableValues are stored in a {@link WeakHashMap} for this purpose.
 */
public abstract class When<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected static final WeakHashMap<ObservableValue<?>, WeakReference<When<?>>> whens = new WeakHashMap<>();
	protected final ObservableValue<T> observableValue;
	protected boolean oneShot = false;

	protected final Set<Observable> invalidatingObservables;
	protected InvalidationListener invalidationListener;

	//================================================================================
	// Constructors
	//================================================================================
	protected When(ObservableValue<T> observableValue) {
		this.observableValue = observableValue;

		this.invalidatingObservables = new HashSet<>();
		this.invalidationListener = o -> invalidate();
	}

	//================================================================================
	// Abstract Methods
	//================================================================================
	public abstract When<T> listen();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * This is responsible for registering the {@code When} construct in a map that
	 * keep references to all the built constructs. This is to avoid garbage collection and to
	 * handle {@code When}s disposal easily.
	 * <p></p>
	 * This should be called by implementations of {@link #listen()}.
	 */
	protected final void register() {
		if (whens.containsKey(observableValue))
			throw new IllegalArgumentException("Cannot register this When construct as the given observable is already being observed");
		whens.put(observableValue, new WeakReference<>(this));
	}

	/**
	 * Adds an {@link Observable} to listen to, when it changes it will cause the invalidation of this construct
	 * by calling {@link #invalidate()}.
	 */
	public When<T> invalidating(Observable obs) {
		invalidatingObservables.add(obs);
		return this;
	}

	/**
	 * The default implementation does nothing.
	 */
	protected When<T> invalidate() {
		return this;
	}

	/**
	 * Implementation of this should allow executing the specified action before the
	 * listener is attached to the observable.
	 * By default, does nothing.
	 */
	public When<T> executeNow() {
		return this;
	}

	/**
	 * Calls {@link #executeNow()} if the given condition is true.
	 */
	public When<T> executeNow(Supplier<Boolean> condition) {
		if (condition.get()) executeNow();
		return this;
	}

	/**
	 * @return whether the construct is "one-shot"
	 * @see #oneShot()
	 */
	public boolean isOneShot() {
		return oneShot;
	}

	/**
	 * Specifies that the construct is "one-shot", meaning that once the
	 * value changes the first time, the construct will automatically dispose itself.
	 */
	public When<T> oneShot() {
		this.oneShot = true;
		return this;
	}

	/**
	 * Removes all the invalidating sources added through {@link #invalidating(Observable)} and removes the listener
	 * from them.
	 */
	protected void dispose() {
		invalidatingObservables.forEach(o -> o.removeListener(invalidationListener));
		invalidatingObservables.clear();
		if (invalidationListener != null)
			invalidationListener = null;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Convenience method to create and instance of {@link OnInvalidated}.
	 */
	public static <T> OnInvalidated<T> onInvalidated(ObservableValue<T> observableValue) {
		return OnInvalidated.forObservable(observableValue);
	}

	/**
	 * Convenience method to create an instance of {@link OnChanged}.
	 */
	public static <T> OnChanged<T> onChanged(ObservableValue<T> observableValue) {
		return OnChanged.forObservable(observableValue);
	}

	/**
	 * If a When construct exists for the given {@link ObservableValue},
	 * {@link #dispose()} is invoked.
	 */
	public static void disposeFor(ObservableValue<?> observableValue) {
		WeakReference<When<?>> ref = whens.remove(observableValue);
		When<?> remove = ref != null ? ref.get() : null;
		if (remove != null) remove.dispose();
	}
}
