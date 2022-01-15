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
