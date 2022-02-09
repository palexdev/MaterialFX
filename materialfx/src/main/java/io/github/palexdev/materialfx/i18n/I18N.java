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

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * Class to handle internationalization.
 * <p>
 * To change the project's language you should use {@link #setLanguage(Language)} before
 * loading any node (for example at the top of the {@link Application#start(Stage)} method).
 */
public class I18N {
	//================================================================================
	// Properties
	//================================================================================
	private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>();

	//================================================================================
	// Static Block
	//================================================================================
	static {
		setLanguage(Language.defaultLanguage());
		locale.addListener(invalidated -> Locale.setDefault(getLocale()));
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the current specified locale, {@link #localeProperty()}
	 */
	public static String get(String key, Object... args) {
		ResourceBundle bundle = getBundle(getLocale());
		return MessageFormat.format(bundle.getString(key), args);
	}

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the specified language parameter
	 */
	public static String get(Language language, String key, Object... args) {
		ResourceBundle bundle = getBundle(language.getLocale());
		return MessageFormat.format(bundle.getString(key), args);
	}

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the current specified locale, {@link #localeProperty()}
	 * If the bundle doesn't provide any value for the given key, returns the value from the
	 * default language, {@link Language#defaultLanguage()}
	 */
	public static String getOrDefault(String key, Object... args) {
		ResourceBundle bundle = getBundle(getLocale());
		try {
			String s = bundle.getString(key);
			return MessageFormat.format(s, args);
		} catch (Exception ex) {
			return get(Language.defaultLanguage(), key, args);
		}
	}

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the specified language parameter.
	 * If the bundle doesn't provide any value for the given key, returns the value from the
	 * default language, {@link Language#defaultLanguage()}
	 */
	public static String getOrDefault(Language language, String key, Object... args) {
		ResourceBundle bundle = getBundle(language.getLocale());
		try {
			String s = bundle.getString(key);
			return MessageFormat.format(s, args);
		} catch (Exception ex) {
			return get(Language.defaultLanguage(), key, args);
		}
	}

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the current specified locale, {@link #localeProperty()}
	 * If the bundle doesn't provide any value for the given key, returns the given def parameter
	 */
	public static String getOrDefault(String key, String def, Object... args) {
		ResourceBundle bundle = getBundle(getLocale());
		try {
			String s = bundle.getString(key);
			return MessageFormat.format(s, args);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * @return the String associated with the given key.
	 * The resource bundle used is loaded from the specified language parameter.
	 * If the bundle doesn't provide any value for the given key, returns the given def parameter
	 */
	public static String getOrDefault(Language language, String key, String def, Object... args) {
		ResourceBundle bundle = getBundle(language.getLocale());
		try {
			String s = bundle.getString(key);
			return MessageFormat.format(s, args);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * @return a {@link StringBinding} that updates whenever the {@link #localeProperty()} changes.
	 * The localized String is loaded using {@link #getOrDefault(String, Object...)}
	 */
	public static StringBinding getBinding(String key, Object... args) {
		return Bindings.createStringBinding(() -> getOrDefault(key, args), locale);
	}

	/**
	 * @return a {@link StringBinding} that updates whenever the {@link #localeProperty()} changes.
	 * The value is computed according to the given {@link Callable}
	 */
	public static StringBinding getBinding(Callable<String> callable) {
		return Bindings.createStringBinding(callable, locale);
	}

	/**
	 * Responsible for loading a {@link ResourceBundle} for the given Locale
	 */
	private static ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(getBundleBaseName(), locale);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public static Locale getLocale() {
		return locale.get();
	}

	/**
	 * Specifies the current MaterialFX language.
	 * <p></p>
	 * <b>NOTE:</b> it is not recommended to set the Locale from this property, you
	 * should use the given setter, {@link #setLanguage(Language)}, since MaterialFX may not
	 * support all Locales.
	 *
	 * @see Language
	 */
	public static ObjectProperty<Locale> localeProperty() {
		return locale;
	}

	public static void setLanguage(Language language) {
		locale.set(language.getLocale());
	}

	/**
	 * @return all the supported languages
	 */
	public static Language[] getSupportedLanguages() {
		return Language.values();
	}

	/**
	 * @return the {@link ResourceBundle}'s base name
	 */
	public static String getBundleBaseName() {
		return "io.github.palexdev.materialfx.i18n.mfxlang";
	}
}
