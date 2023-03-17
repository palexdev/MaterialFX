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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Concrete implementation of {@link When} that uses {@link InvalidationListener}s to
 * listen for changes for a given {@link ObservableValue}.
 * <p></p>
 * You can specify the action to perform when this happens using a {@link Consumer},
 * {@link #then(Consumer)}.
 * <p>
 * You can also set a condition that has to be met for the action to be executed (see {@link #condition(Function)}),
 * and an "else" action that is executed when it is not met, (see {@link #otherwise(BiConsumer)}).
 * <p></p>
 * Optionally you could also tell the construct to execute the given action immediately, the {@link Consumer} will
 * take {@link ObservableValue#getValue()} as input.
 * <p></p>
 * To activate the construct do not forget to call {@link #listen()} at the end.
 * <p></p>
 * An example:
 * <pre>
 * {@code
 *      BooleanProperty aSwitch = new SimpleBooleanProperty(false);
 *      When.onInvalidated(aSwitch) // You can also use... OnInvalidated.forObservable(...)
 *              .condition(aCondition)
 *              .then(value -> System.out.println("Value switched to: " + value))
 *              .otherwise((ref, oldValue, newValue) -> System.out.println("Condition not met, execution action B"))
 *              .oneShot()
 *              .executeNow() // This could also be moved after the listen method
 *              .listen();
 * }
 * </pre>
 */
public class OnInvalidated<T> extends When<T> {
	//================================================================================
	// Properties
	//================================================================================
	private InvalidationListener listener;
	private Consumer<T> action;
	private BiConsumer<WeakReference<When<T>>, T> otherwise = (w, t) -> {
	};
	private Function<T, Boolean> condition = t -> true;

	//================================================================================
	// Constructors
	//================================================================================
	private OnInvalidated(ObservableValue<T> observableValue) {
		super(observableValue);
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Creates and instance of this construct for the given {@link ObservableValue}.
	 */
	public static <T> OnInvalidated<T> forObservable(ObservableValue<T> observableValue) {
		return new OnInvalidated<>(observableValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * To set the action to perform when the specified {@link ObservableValue}
	 * becomes invalid. The action is a {@link Consumer} that carries the new value
	 * of the observable.
	 */
	public OnInvalidated<T> then(Consumer<T> action) {
		this.action = action;
		return this;
	}

	/**
	 * Allows to set an action to perform when the given {@link #condition(Function)} is not met.
	 * <p></p>
	 * This makes the "system" much more versatile. Imagine having a one-shot listener that you want to
	 * dispose anyway even if the condition is not met, you can write something like this;
	 * <pre>
	 * {@code
	 * When.onChanged(observable)
	 *      .condition(aCondition)
	 *      .then(action)
	 *      .otherwise((w, t) -> Optional.ofNullable(w.get()).ifPresent(When::dispose)) // Note the null check
	 *      .listen();
	 *
	 * }
	 * </pre>
	 * <p></p>
	 * Also note that the otherwise action also carries the reference to this object wrapped in a {@link WeakReference}.
	 */
	public OnInvalidated<T> otherwise(BiConsumer<WeakReference<When<T>>, T> otherwise) {
		this.otherwise = otherwise;
		return this;
	}

	/**
	 * Allows to specify a condition under which the set action (see {@link #then(Consumer)})
	 * is to be executed.
	 * <p></p>
	 * The condition is specified through a {@link Function} that provides the current value
	 * of the {@link ObservableValue}.
	 * <p></p>
	 * In case the condition is not met the {@link #otherwise(BiConsumer)} action is executed instead.
	 * <p></p>
	 * For one-shot listeners, the action is executed and the listener disposed only if the condition is met, else
	 * the {@link #otherwise(BiConsumer)} action is executed instead.
	 */
	public OnInvalidated<T> condition(Function<T, Boolean> condition) {
		this.condition = condition;
		return this;
	}

	/**
	 * Executes the given action immediately with the current value of the
	 * given {@link ObservableValue}.
	 */
	public OnInvalidated<T> executeNow() {
		action.accept(observableValue.getValue());
		return this;
	}

	/**
	 * Calls {@link #executeNow()} if the given condition is true.
	 */
	public OnInvalidated<T> executeNow(Supplier<Boolean> condition) {
		if (condition.get()) executeNow();
		return this;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Activates the {@code OnInvalidated} construct with the previously specified parameters.
	 * So, builds the {@link InvalidationListener} according to the {@link #isOneShot()} parameter,
	 * then adds the listener to the specified {@link ObservableValue} and finally puts the Observable and
	 * the OnInvalidated construct in the map.
	 * <p></p>
	 * Before activating the listener, it also activates all the invalidating sources added through {@link #invalidating(Observable)}.
	 */
	@Override
	public OnInvalidated<T> listen() {
		if (oneShot) {
			listener = invalidated -> {
				T value = observableValue.getValue();
				if (condition.apply(value)) {
					action.accept(value);
					dispose();
				} else {
					otherwise.accept(new WeakReference<>(this), value);
				}
			};
		} else {
			listener = invalidated -> {
				T value = observableValue.getValue();
				if (condition.apply(value)) {
					action.accept(value);
				} else {
					otherwise.accept(new WeakReference<>(this), value);
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
	 * invoked and causes {@link #executeNow(Supplier)} to execute. The condition function is supplied with {@link ObservableValue#getValue()}.
	 */
	@Override
	protected When<T> invalidate() {
		executeNow(() -> condition.apply(observableValue.getValue()));
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Disposes the {@code OnInvalidated} construct by removing the {@link InvalidationListener}
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
