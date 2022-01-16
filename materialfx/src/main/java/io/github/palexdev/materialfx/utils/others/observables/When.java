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

package io.github.palexdev.materialfx.utils.others.observables;

import javafx.beans.value.ObservableValue;

import java.util.WeakHashMap;

/**
 * Useful class to listen to changes for a given {@link ObservableValue} and perform any
 * specified action when it changes.
 * <p>
 * You can read this construct as "When condition changes, then do this"
 * <p>
 * This is just an abstract class that defines common properties and behavior but it has two concrete
 * implementation, {@link OnChanged} and {@link OnInvalidated}.
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
	protected static final WeakHashMap<ObservableValue<?>, When<?>> whens = new WeakHashMap<>();
	protected final ObservableValue<T> observableValue;
	protected boolean oneShot = false;

	//================================================================================
	// Constructors
	//================================================================================
	protected When(ObservableValue<T> observableValue) {
		this.observableValue = observableValue;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================
	public abstract When<T> listen();

	public abstract void dispose();

	//================================================================================
	// Methods
	//================================================================================

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
	 * If a When constructs exists for the given {@link ObservableValue},
	 * {@link #dispose()} is invoked.
	 */
	public static void disposeFor(ObservableValue<?> observableValue) {
		When<?> remove = whens.remove(observableValue);
		if (remove != null) remove.dispose();
	}
}
