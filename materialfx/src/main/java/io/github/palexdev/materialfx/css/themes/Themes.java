package io.github.palexdev.materialfx.css.themes;

import io.github.palexdev.materialfx.MFXResourcesLoader;

public enum Themes implements Theme {
	DEFAULT("DefaultTheme.css"),
	LEGACY("legacy/LegacyControls.css"),
	;

	private final String theme;

	Themes(String theme) {
		this.theme = theme;
	}

	@Override
	public String getTheme() {
		return theme;
	}

	@Override
	public String loadTheme() {
		if (Helper.isCached(this)) return Helper.getCachedTheme(this);
		return Helper.cacheTheme(this, MFXResourcesLoader.load(baseDir() + getTheme()));
	}
}
