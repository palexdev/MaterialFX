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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;

/**
 * Concrete implementation of {@link When} that uses {@link ChangeListener}s to
 * listen for changes for a given {@link ObservableValue}.
 * <p></p>
 * You can specify the action to perform when this happens using a {@link BiConsumer},
 * {@link #then(BiConsumer)}.
 * <p>
 * To activate the construct do not forget to call {@link #listen()} at the end.
 * <p></p>
 * An example:
 * <pre>
 * {@code
 *      IntegerProperty aNumber = new SimpleIntegerProperty(69);
 *      When.onChanged(aNumber) // You can also use... OnChanged.forObservable(...)
 *              .then((oldValue, newValue) -> System.out.println("Value switched from: " + oldValue + " to " + newValue))
 *              .oneShot()
 *              .listen();
 * }
 * </pre>
 */
public class OnChanged<T> extends When<T> {
	//================================================================================
	// Properties
	//================================================================================
	private ChangeListener<T> listener;
	private BiConsumer<T, T> action;

	//================================================================================
	// Constructors
	//================================================================================
	private OnChanged(ObservableValue<T> observableValue) {
		super(observableValue);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Creates and instance of this construct for the given {@link ObservableValue}.
	 */
	public static <T> OnChanged<T> forObservable(ObservableValue<T> observableValue) {
		return new OnChanged<>(observableValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * To set the action to perform when the specified {@link ObservableValue}
	 * changes. The action is a {@link BiConsumer} that carries both the old value
	 * and the new value of the observable.
	 */
	public OnChanged<T> then(BiConsumer<T, T> action) {
		this.action = action;
		return this;
	}

	/**
	 * Activates the {@code OnChanged} construct with the previously specified parameters.
	 * So, builds the {@link ChangeListener} according to the {@link #isOneShot()} parameter,
	 * then adds the listener to the specified {@link ObservableValue} and finally puts the Observable and
	 * the OnChanged construct in the map.
	 */
	@Override
	public OnChanged<T> listen() {
		if (oneShot) {
			listener = (observable, oldValue, newValue) -> {
				action.accept(oldValue, newValue);
				dispose();
			};
		} else {
			listener = (observable, oldValue, newValue) -> action.accept(oldValue, newValue);
		}

		observableValue.addListener(listener);
		whens.put(observableValue, this);
		return this;
	}

	/**
	 * Disposes the {@code OnChanged} construct by removing the {@link ChangeListener}
	 * from the {@link ObservableValue}, then sets the listener to null and finally removes
	 * the observable from the map.
	 */
	@Override
	public void dispose() {
		if (observableValue != null && listener != null) {
			observableValue.removeListener(listener);
			listener = null;
			whens.remove(observableValue);
		}
	}
}
