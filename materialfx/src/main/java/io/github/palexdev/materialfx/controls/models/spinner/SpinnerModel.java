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

import io.github.palexdev.materialfx.controls.MFXSpinner;
import javafx.beans.property.ObjectProperty;
import javafx.util.StringConverter;

/**
 * Defines the public API for all models to be used with {@link MFXSpinner}.
 * <p>
 * {@code SpinnerModel} is basically an helper class to allow the spinner to work on any object
 * type as long as a model exists for it. The model is responsible for changing the spinner's value by
 * going forward or backwards ({@link #next() or {@link #previous()}}.
 * <p>
 * Along this core functionality the model also specifies a {@link StringConverter} which will be
 * used to convert the T value to a String, which will be the spinner's text.
 * <p>
 * The spinner should also allow to cycle through the values, meaning that when reaching the the last value,
 * {@link #next()} will go to the first value (and the other way around)
 */
public interface SpinnerModel<T> {

	/**
	 * Steps to the next value.
	 */
	void next();

	/**
	 * Steps to the previous value.
	 */
	void previous();

	/**
	 * Resets the spinner's value.
	 */
	void reset();

	T getValue();

	/**
	 * Specifies the spinner's value.
	 */
	ObjectProperty<T> valueProperty();

	void setValue(T value);

	StringConverter<T> getConverter();

	/**
	 * Specifies the {@link StringConverter} used to convert the spinner value to a String.
	 */
	ObjectProperty<StringConverter<T>> converterProperty();

	void setConverter(StringConverter<T> converter);

	/**
	 * @return whether the spinner can cycle through the values when
	 * reaching the last/first value
	 */
	boolean isWrapAround();

	/**
	 * Sets whether the spinner can cycle through the values when
	 * reaching the last/first value
	 */
	void setWrapAround(boolean wrapAround);
}
