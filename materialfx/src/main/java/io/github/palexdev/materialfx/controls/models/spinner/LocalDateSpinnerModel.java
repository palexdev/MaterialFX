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

import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAmount;

/**
 * Concrete implementation of {@link AbstractSpinnerModel} to work with {@link LocalDate} values.
 * <p></p>
 * {@code LocalDateSpinnerModel} adds the {@link #converterProperty()} (since we know the kind of data the model will deal with),
 * and three new properties, {@link #minProperty()}, {@link #maxProperty()} and {@link #incrementProperty()}.
 * <p></p>
 * The constructor initializes the model with these values:
 * <p> - The converter uses {@link DateStringConverter} with format {@link FormatStyle#MEDIUM}
 * <p> - The default value is {@link LocalDate#EPOCH}
 * <p> - The min value is {@link LocalDate#EPOCH}
 * <p> - The max value is {@link LocalDate#MAX}
 * <p> - The increment is {@link Duration#ofDays(long)}, 1 day
 * <p> - The initial value depends on the chosen constructor
 */
public class LocalDateSpinnerModel extends AbstractSpinnerModel<LocalDate> {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<StringConverter<LocalDate>> converter = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> min = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDate> max = new SimpleObjectProperty<>();
	private final ObjectProperty<TemporalAmount> increment = new SimpleObjectProperty<>();

	//================================================================================
	// Constructors
	//================================================================================
	public LocalDateSpinnerModel() {
		this(LocalDate.EPOCH);
	}

	public LocalDateSpinnerModel(LocalDate initialValue) {
		setConverter(new DateStringConverter(FormatStyle.MEDIUM));
		setDefaultValue(LocalDate.EPOCH);
		setMin(LocalDate.EPOCH);
		setMax(LocalDate.MAX);
		setIncrement(Duration.ofDays(1));
		setValue(initialValue);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void next() {
		LocalDate next = getValue().plus(getIncrement());
		if (next.isAfter(getMax())) {
			next = isWrapAround() ? getMin() : getMax();
		}
		setValue(next);
	}

	@Override
	public void previous() {
		LocalDate prev = getValue().minus(getIncrement());
		if (prev.isBefore(getMin())) {
			prev = isWrapAround() ? getMax() : getMin();
		}
		setValue(prev);
	}

	@Override
	public StringConverter<LocalDate> getConverter() {
		return converter.get();
	}

	@Override
	public ObjectProperty<StringConverter<LocalDate>> converterProperty() {
		return converter;
	}

	@Override
	public void setConverter(StringConverter<LocalDate> converter) {
		this.converter.set(converter);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public LocalDate getMin() {
		return min.get();
	}

	/**
	 * Specifies the minimum date reachable by the spinner.
	 */
	public ObjectProperty<LocalDate> minProperty() {
		return min;
	}

	public void setMin(LocalDate min) {
		this.min.set(min);
	}

	public LocalDate getMax() {
		return max.get();
	}

	/**
	 * Specifies the maximum date reachable by the spinner.
	 */
	public ObjectProperty<LocalDate> maxProperty() {
		return max;
	}

	public void setMax(LocalDate max) {
		this.max.set(max);
	}

	public TemporalAmount getIncrement() {
		return increment.get();
	}

	/**
	 * Specifies the increment/decrement value to add/subtract from
	 * the current index when calling {@link #next()} or {@link #previous()}.
	 * <p></p>
	 * The amount is a generic {@link TemporalAmount}.
	 */
	public ObjectProperty<TemporalAmount> incrementProperty() {
		return increment;
	}

	public void setIncrement(TemporalAmount increment) {
		this.increment.set(increment);
	}
}
