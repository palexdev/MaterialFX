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

import io.github.palexdev.materialfx.utils.others.TriConsumer;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Concrete implementation of {@link When} that uses {@link ChangeListener}s to
 * listen for changes for a given {@link ObservableValue}.
 * <p></p>
 * You can specify the action to perform when this happens using a {@link BiConsumer},
 * {@link #then(BiConsumer)}.
 * <p>
 * You can also set a condition that has to be met for the action to be executed (see {@link #condition(BiFunction)}),
 * and an "else" action that is executed when it is not met, (see {@link #otherwise(TriConsumer)}).
 * <p></p>
 * Optionally you could also tell the construct to execute te given action immediately, the {@link BiConsumer} will
 * take null and {@link ObservableValue#getValue()} as the old and new values.
 * <p></p>
 * To activate the construct do not forget to call {@link #listen()} at the end.
 * <p></p>
 * An example:
 * <pre>
 * {@code
 *      IntegerProperty aNumber = new SimpleIntegerProperty(69);
 *      When.onChanged(aNumber) // You can also use... OnChanged.forObservable(...)
 *              .condition(aCondition)
 *              .then((oldValue, newValue) -> System.out.println("Value switched from: " + oldValue + " to " + newValue))
 *              .otherwise((ref, oldValue, newValue) -> System.out.println("Condition not met, execution action B"))
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
	private TriConsumer<WeakReference<When<T>>, T, T> otherwise = (w, o, n) -> {
	};
	private BiFunction<T, T, Boolean> condition = (o, n) -> true;

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
	 * Allows to set an action to perform when the given {@link #condition(BiFunction)} is not met.
	 * <p></p>
	 * This makes the "system" much more versatile. Imagine having a one-shot listener that you want to
	 * dispose anyway even if the condition is not met, you can write something like this;
	 * <pre>
	 * {@code
	 * When.onChanged(observable)
	 *      .condition(aCondition)
	 *      .then(action)
	 *      .otherwise((w, o, n) -> Optional.ofNullable(w.get()).ifPresent(When::dispose)) // Note the null check
	 *      .listen();
	 *
	 * }
	 * </pre>
	 * <p></p>
	 * Also note that the otherwise action also carries the reference to this object wrapped in a {@link WeakReference}.
	 */
	public OnChanged<T> otherwise(TriConsumer<WeakReference<When<T>>, T, T> otherwise) {
		this.otherwise = otherwise;
		return this;
	}

	/**
	 * Allows to specify a condition under which the set action (see {@link #then(BiConsumer)})
	 * is to be executed.
	 * <p></p>
	 * The condition is specified through a {@link BiFunction} that provides both the old and new values
	 * of the {@link ObservableValue}.
	 * <p></p>
	 * In case the condition is not met the {@link #otherwise(TriConsumer)} action is executed instead.
	 * <p></p>
	 * For one-shot listeners, the action is executed and the listener disposed only if the condition is met, else
	 * the {@link #otherwise(TriConsumer)} action is executed instead.
	 */
	public OnChanged<T> condition(BiFunction<T, T, Boolean> condition) {
		this.condition = condition;
		return this;
	}

	/**
	 * Executes the given action immediately with null as the old value and the current value of the
	 * given {@link ObservableValue} as the new value.
	 */
	public OnChanged<T> executeNow() {
		action.accept(null, observableValue.getValue());
		return this;
	}

	/**
	 * Calls {@link #executeNow()} if the given condition is true.
	 */
	public OnChanged<T> executeNow(Supplier<Boolean> condition) {
		if (condition.get()) executeNow();
		return this;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Activates the {@code OnChanged} construct with the previously specified parameters.
	 * So, builds the {@link ChangeListener} according to the {@link #isOneShot()} parameter,
	 * then adds the listener to the specified {@link ObservableValue} and finally puts the Observable and
	 * the OnChanged construct in the map.
	 * <p></p>
	 * Before activating the listener, it also activates all the invalidating sources added through {@link #invalidating(Observable)}.
	 */
	@Override
	public OnChanged<T> listen() {
		if (oneShot) {
			listener = (observable, oldValue, newValue) -> {
				if (condition.apply(oldValue, newValue)) {
					action.accept(oldValue, newValue);
					dispose();
				} else {
					otherwise.accept(new WeakReference<>(this), oldValue, newValue);
				}
			};
		} else {
			listener = (observable, oldValue, newValue) -> {
				if (condition.apply(oldValue, newValue)) {
					action.accept(oldValue, newValue);
				} else {
					otherwise.accept(new WeakReference<>(this), oldValue, newValue);
				}
			};
		}

		invalidatingObservables.forEach(o -> o.addListener(invalidationListener));
		register();
		observableValue.addListener(listener);
		return this;
	}

	/**
	 * When one of the invalidating sources added through {@link #invalidating(Observable)} changes, this method will be
	 * invoked and causes {@link #executeNow(Supplier)} to execute. The condition function is supplied with 'null' as the
	 * old value, and {@link ObservableValue#getValue()} as the new value.
	 */
	@Override
	protected When<T> invalidate() {
		executeNow(() -> condition.apply(null, observableValue.getValue()));
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Disposes the {@code OnChanged} construct by removing the {@link ChangeListener}
	 * from the {@link ObservableValue}, then sets the listener to null and finally removes
	 * the observable from the map.
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (observableValue != null && listener != null) {
			observableValue.removeListener(listener);
			listener = null;
			whens.remove(observableValue);
		}
	}
}
