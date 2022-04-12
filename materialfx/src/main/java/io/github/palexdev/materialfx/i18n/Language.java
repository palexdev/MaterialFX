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

package io.github.palexdev.materialfx.i18n;

import java.util.Locale;

/**
 * Enumerator to list all the supported {@link Locale}s by MaterialFX.
 * <p>
 * Every {@code Language} enumeration is associated with a {@code Locale}.
 * <p>
 * The enumerator also specifies the project's default language, {@link #defaultLanguage()}.
 */
public enum Language {
	ARABIC(Locale.forLanguageTag("ar")),
	ENGLISH(Locale.ENGLISH),
	FRENCH(Locale.FRENCH),
	ITALIANO(Locale.ITALIAN),
	RUSSIAN(Locale.forLanguageTag("ru")),
	SIMPLIFIED_CHINESE(Locale.SIMPLIFIED_CHINESE),
	TRADITIONAL_CHINESE(Locale.TRADITIONAL_CHINESE);

	private final Locale locale;

	Language(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the project's default language, {@link Language#ENGLISH}
	 */
	public static Language defaultLanguage() {
		return ENGLISH;
	}

	public Locale getLocale() {
		return locale;
	}
}
