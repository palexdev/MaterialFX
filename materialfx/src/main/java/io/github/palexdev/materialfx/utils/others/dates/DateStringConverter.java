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

package io.github.palexdev.materialfx.utils.others.dates;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * A {@link StringConverter} capable of converting {@link LocalDate} to/from Strings.
 */
public class DateStringConverter extends StringConverter<LocalDate> {
	//================================================================================
	// Properties
	//================================================================================
	private final DateTimeFormatter formatter;

	//================================================================================
	// Constructors
	//================================================================================
	public DateStringConverter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	public DateStringConverter(FormatStyle formatStyle) {
		this.formatter = DateTimeFormatter.ofLocalizedDate(formatStyle);
	}

	public DateStringConverter(String pattern, Locale locale) {
		this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String toString(LocalDate date) {
		if (date == null) return "";
		return formatter.format(date);
	}

	@Override
	public LocalDate fromString(String string) {
		if (string == null) return null;
		return LocalDate.parse(string, formatter);
	}
}
