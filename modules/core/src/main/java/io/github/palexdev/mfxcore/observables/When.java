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

package io.github.palexdev.mfxcore.observables;

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
 * <b>Note 1: </b>Once the construct is not needed anymore it's highly recommended to dispose it
 * using {@link #disposeFor(ObservableValue)} to avoid memory leaks.
 * When constructs and ObservableValues are stored in a {@link WeakHashMap} for this purpose.
 * <p></p>
 * <b>Note 2: </b> Every {@link ObservableValue} can have at max one When construct listening to it, attempts at
 * registering more than one will result with an exception thrown by {@link #register()}.
 * <p>
 * One can also build a construct using {@link #forceListen()}, this will lead to the disposal of the previously built
 * construct (if any was built).
 */
public abstract class When<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected static final WeakHashMap<ObservableValue<?>, WeakReference<When<?>>> whens = new WeakHashMap<>();
	protected ObservableValue<T> observableValue;
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

	/**
	 * Implementations of this should provide the logic that adds the listener on the given {@link ObservableValue},
	 * as well as handling cases such {@link #oneShot()} and {@link #invalidating(Observable)} as well as making sure that
	 * the construct is registered at the end, {@link #register()}.
	 */
	public abstract When<T> listen();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Forces the creation/activation of this {@code When} construct by first checking if there's already one for the
	 * current {@link ObservableValue} by disposing it first and then calling {@link #listen()}.
	 */
	public When<T> forceListen() {
		if (whens.containsKey(observableValue))
			disposeFor(observableValue);
		return listen();
	}

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

	/**
	 * Attempts to dispose this construct by invoking {@link #disposeFor(ObservableValue)} on the current given
	 * observable.
	 * <p></p>
	 * If the construct has not been registered yet, or the observable is null, nothing will happen.
	 */
	public void requestDisposal() {
		disposeFor(observableValue);
	}

	/**
	 * @return whether this construct has been disposed before. By default, checks if the given {@link ObservableValue}
	 * is null, there are no invalidating sources and the invalidation listener is null. A construct is considered to be
	 * properly disposed only when all these conditions are verified
	 */
	public boolean isDisposed() {
		return observableValue == null &&
				invalidatingObservables.isEmpty() &&
				invalidationListener == null;
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
