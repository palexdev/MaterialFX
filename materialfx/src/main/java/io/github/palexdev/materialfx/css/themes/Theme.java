package io.github.palexdev.materialfx.css.themes;

import java.util.HashMap;
import java.util.Map;

public interface Theme {

	String getTheme();

	String loadTheme();

	default String baseDir() {
		return "css/";
	}

	class Helper {
		private static final Map<Theme, String> CACHE = new HashMap<>();

		protected static boolean isCached(Theme theme) {
			return CACHE.containsKey(theme);
		}

		protected static String cacheTheme(Theme theme, String loaded) {
			CACHE.put(theme, loaded);
			return loaded;
		}

		protected static String getCachedTheme(Theme theme) {
			return CACHE.get(theme);
		}
	}
}
