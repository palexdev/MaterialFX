package io.github.palexdev.materialfx.utils.others;

import io.github.palexdev.materialfx.utils.EnumUtils;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A {@link StringConverter} capable of converting {@link DayOfWeek} to/from Strings.
 */
public class DayOfWeekStringConverter extends StringConverter<DayOfWeek> {
	//================================================================================
	// Properties
	//================================================================================
	private final Locale locale;
	private final TextStyle textStyle;

	//================================================================================
	// Constructors
	//================================================================================
	public DayOfWeekStringConverter(Locale locale, TextStyle textStyle) {
		this.locale = locale;
		this.textStyle = textStyle;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String toString(DayOfWeek dayOfWeek) {
		if (dayOfWeek == null) return "";
		return dayOfWeek.getDisplayName(textStyle, locale);
	}

	@Override
	public DayOfWeek fromString(String string) {
		if (string == null) return null;
		return EnumUtils.valueOfIgnoreCase(DayOfWeek.class, string);
	}
}
