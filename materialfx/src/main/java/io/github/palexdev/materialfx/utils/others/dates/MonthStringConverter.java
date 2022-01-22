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

import io.github.palexdev.materialfx.utils.EnumUtils;
import javafx.util.StringConverter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A {@link StringConverter} capable of converting {@link Month} to/from Strings.
 */
public class MonthStringConverter extends StringConverter<Month> {
	//================================================================================
	// Properties
	//================================================================================
	private final Locale locale;
	private final TextStyle textStyle;

	//================================================================================
	// Constructors
	//================================================================================
	public MonthStringConverter(Locale locale, TextStyle textStyle) {
		this.locale = locale;
		this.textStyle = textStyle;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String toString(Month month) {
		if (month == null) return "";
		return month.getDisplayName(textStyle, locale);
	}

	@Override
	public Month fromString(String string) {
		if (string == null) return null;
		return EnumUtils.valueOfIgnoreCase(Month.class, string);
	}
}
