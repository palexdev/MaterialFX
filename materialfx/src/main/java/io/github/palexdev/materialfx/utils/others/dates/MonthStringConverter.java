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
