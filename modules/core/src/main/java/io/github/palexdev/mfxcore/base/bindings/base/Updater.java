package io.github.palexdev.mfxcore.base.bindings.base;

import io.github.palexdev.mfxcore.base.bindings.Target;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Can be considered as a mix of {@link Consumer} and {@link BiConsumer}. Has only one generic type
 * but the method expects two values of the same type. This is specifically made for use with
 * binding' sources and {@link ChangeListener}s.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface Updater<T> {

	/**
	 * Performs this operation on the given oldValue and newValue.
	 */
	void update(T oldValue, T newValue);

	/**
	 * Returns a composed {@code Updater} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation. If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code Updater} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default Updater<T> andThen(Updater<? super T> after) {
		Objects.requireNonNull(after);
		return (oldValue, newValue) -> {
			update(oldValue, newValue);
			after.update(oldValue, newValue);
		};
	}

	/**
	 * Attempts to create an {@code Updater} for the given {@link ObservableValue}.
	 * This works only for observables that are instance of {@link Property}, as the most
	 * basic {@code Updater} for them is {@code (oldValue, newValue) -> property.setValue(newValue)}.
	 *
	 * @throws IllegalArgumentException if the given {@link ObservableValue} is not instance of {@link Property}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	static <T> Updater<T> implicit(ObservableValue observable) {
		if (!(observable instanceof Property)) {
			throw new IllegalArgumentException("Cannot create implicit updater for " + observable);
		}
		return (oldValue, newValue) -> ((Property<T>) observable).setValue(newValue);
	}

	/**
	 * Calls {@link #implicit(ObservableValue)} with {@link Target#getObservable()}.
	 */
	@SuppressWarnings("rawtypes")
	static <T> Updater<T> implicit(Target target) {
		return implicit(target.getObservable());
	}
}
