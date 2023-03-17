package io.github.palexdev.materialfx.css.themes;

import io.github.palexdev.materialfx.MFXResourcesLoader;

/**
 * Enumerator implementing {@link Theme} to expose all the themes part of MaterialFX.
 * <p></p>
 * Makes use of the cache offered by {@link Theme}, subsequent loads of the same theme will be faster.
 */
public enum Themes implements Theme {
	/**
	 * This theme contains all the stylesheets for the new MaterialFX controls.
	 */
	DEFAULT("DefaultTheme.css"),

	/**
	 * This theme contains all the stylesheets for the legacy controls styled by MaterialFX.
	 */
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
		return Helper.cacheTheme(this, MFXResourcesLoader.load(mfxBaseDir() + getTheme()));
	}
}
