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

package io.github.palexdev.materialfx.theming;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.theming.base.Theme;

import java.io.InputStream;

/**
 * Enumeration of the standard JavaFX themes. This is useful when used with {@link UserAgentBuilder}.
 * Implements {@link Theme}.
 */
public enum JavaFXThemes implements Theme {
	CASPIAN("css/jfx/caspian/caspian.css"),
	CASPIAN_NO_TRANSPARENCY("css/jfx/caspian/caspian-no-transparency.css"),
	CASPIAN_TWO_LEVEL_FOCUS("css/jfx/caspian/two-level-focus.css"),
	MODENA("css/jfx/modena/modena.css"),
	MODENA_NO_TRANSPARENCY("css/jfx/modena/modena-no-transparency.css"),
	MODENA_TOUCH("css/jfx/modena/touch.css"),
	MODENA_TWO_LEVEL_FOCUS("css/jfx/modena/two-level-focus.css"),
	FXVK("css/jfx/caspian/fxvk.css");

	private final String path;

	JavaFXThemes(String path) {
		this.path = path;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public InputStream assets() {
		String path = path().substring(0, path().lastIndexOf("/") + 1) + "assets.zip";
		return MFXResourcesLoader.loadStream(path);
	}
}
