package io.github.palexdev.materialfx.beans.properties;

import io.github.palexdev.materialfx.beans.NumberRange;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link NumberRange}.
 *
 * @param <T> the range's number type
 */
public class NumberRangeProperty<T extends Number> extends SimpleObjectProperty<NumberRange<T>> {

	//================================================================================
	// Constructors
	//================================================================================
	public NumberRangeProperty() {
	}

	public NumberRangeProperty(NumberRange<T> initialValue) {
		super(initialValue);
	}

	public NumberRangeProperty(Object bean, String name) {
		super(bean, name);
	}

	public NumberRangeProperty(Object bean, String name, NumberRange<T> initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Convenience method to get the range's lower bound.
	 * Null if the range is null.
	 */
	public T getMin() {
		return get() == null ? null : get().getMin();
	}

	/**
	 * Convenience method to get the range's upper bound.
	 * Null if the range is null.
	 */
	public T getMax() {
		return get() == null ? null : get().getMin();
	}

	/**
	 * Convenience method to set a range with both min and max equal.
	 */
	public void setRange(T value) {
		set(NumberRange.of(value));
	}

	/**
	 * Convenience method to set a range with the given min and max values.
	 */
	public void setRange(T min, T max) {
		set(NumberRange.of(min, max));
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Overridden to check equality between ranges and return in case ranges are the same.
	 */
	@Override
	public void set(NumberRange<T> newValue) {
		NumberRange<T> oldValue = get();
		if (newValue.equals(oldValue)) return;
		super.set(newValue);
	}
}
