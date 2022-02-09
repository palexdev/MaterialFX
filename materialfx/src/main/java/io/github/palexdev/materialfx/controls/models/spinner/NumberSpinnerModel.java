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

package io.github.palexdev.materialfx.controls.models.spinner;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;

/**
 * Base class to easily implement spinner models for numeric values, extends {@link AbstractSpinnerModel}.
 * <p></p>
 * {@code NumberSpinnerModel} adds the {@link #converterProperty()} (since we know the kind of data the model will deal with),
 * and three new properties, {@link #minProperty()}, {@link #maxProperty()}, {@link #incrementProperty()}.
 */
public abstract class NumberSpinnerModel<T extends Number> extends AbstractSpinnerModel<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
	private final ObjectProperty<T> min = new SimpleObjectProperty<>();
	private final ObjectProperty<T> max = new SimpleObjectProperty<>();
	private final ObjectProperty<T> increment = new SimpleObjectProperty<>();

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public StringConverter<T> getConverter() {
		return converter.get();
	}

	@Override
	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	@Override
	public void setConverter(StringConverter<T> converter) {
		this.converter.set(converter);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public T getMin() {
		return min.get();
	}

	/**
	 * Specifies the minimum number reachable by the spinner.
	 */
	public ObjectProperty<T> minProperty() {
		return min;
	}

	public void setMin(T min) {
		this.min.set(min);
	}

	public T getMax() {
		return max.get();
	}

	/**
	 * Specifies the maximum number reachable by the spinner.
	 */
	public ObjectProperty<T> maxProperty() {
		return max;
	}

	public void setMax(T max) {
		this.max.set(max);
	}

	public T getIncrement() {
		return increment.get();
	}

	/**
	 * Specifies the increment/decrement value to add/subtract from
	 * the current value when calling {@link #next()} or {@link #previous()}.
	 */
	public ObjectProperty<T> incrementProperty() {
		return increment;
	}

	public void setIncrement(T increment) {
		this.increment.set(increment);
	}
}
