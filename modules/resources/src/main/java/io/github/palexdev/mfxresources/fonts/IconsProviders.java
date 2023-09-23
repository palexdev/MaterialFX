/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxresources.fonts;

import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeBrands;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeRegular;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;

import java.io.InputStream;
import java.util.function.Function;

/**
 * This enum contains all the "officially" supported icon fonts.
 */
public enum IconsProviders implements IconProvider {
	FONTAWESOME_BRANDS("FontAwesome/brands/FontAwesomeBrands.ttf", FontAwesomeBrands::toCode),
	FONTAWESOME_REGULAR("FontAwesome/regular/FontAwesomeRegular.ttf", FontAwesomeRegular::toCode),
	FONTAWESOME_SOLID("FontAwesome/solid/FontAwesomeSolid.ttf", FontAwesomeSolid::toCode),
	;

	private final String font;
	private final Function<String, Character> converter;

	IconsProviders(String font, Function<String, Character> converter) {
		this.font = font;
		this.converter = converter;
	}

	@Override
	public String getFontPath() {
		return font;
	}

	@Override
	public Function<String, Character> getConverter() {
		return converter;
	}

	@Override
	public InputStream load() {
		return MFXResources.loadFont(font);
	}

	/**
	 * @return the default icon provider used by {@link MFXFontIcon}s, currently {@link #FONTAWESOME_SOLID}
	 */
	public static IconsProviders defaultProvider() {
		return FONTAWESOME_SOLID;
	}
}
