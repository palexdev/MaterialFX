package io.github.palexdev.materialfx.css.themes;

import java.util.HashMap;
import java.util.Map;

/**
 * Bare minimum API for themes.
 * <p>
 * Ideally every theme should expose their path and a way to load them.
 * <p></p>
 * This also offers an internal {@link Helper} class to cache loaded themes.
 */
public interface Theme {

	/**
	 * @return the theme's path/url
	 */
	String getTheme();

	/**
	 * Implementations of this should return the loaded theme as a String.
	 */
	String loadTheme();

	/**
	 * @return the MaterialFX base dir containing all the stylesheets
	 */
	default String mfxBaseDir() {
		return "css/";
	}

	class Helper {
		private static final Map<Theme, String> CACHE = new HashMap<>();

		/**
		 * @return whether the given theme has already been loaded and cached before
		 */
		public static boolean isCached(Theme theme) {
			return CACHE.containsKey(theme);
		}

		/**
		 * Loads the given theme and then caches it.
		 *
		 * @return the loaded theme or an empty string if the result was null
		 */
		public static String cacheTheme(Theme theme) {
			String loaded = theme.loadTheme();
			if (loaded == null) return "";
			CACHE.put(theme, loaded);
			return loaded;
		}

		/**
		 * Caches the given loaded theme.
		 *
		 * @return the loaded theme
		 */
		protected static String cacheTheme(Theme theme, String loaded) {
			CACHE.put(theme, loaded);
			return loaded;
		}

		/**
		 * @return the loaded theme String from the given theme parameter, null if the theme was not cached before
		 */
		public static String getCachedTheme(Theme theme) {
			return CACHE.get(theme);
		}
	}
}
