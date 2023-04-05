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

package io.github.palexdev.mfxcomponents.theming.enums;

import io.github.palexdev.mfxresources.MFXResources;
import javafx.scene.Scene;

import java.net.URL;

public enum MFXThemeManager {
	PURPLE_LIGHT("themes/material/md-purple-light.css"),
	PURPLE_DARK("themes/material/md-purple-dark.css"),
	;

	private final String path;

	MFXThemeManager(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void addOn(Scene scene) {
		String stylesheet = load();
		if (!scene.getStylesheets().contains(stylesheet))
			scene.getStylesheets().add(stylesheet);
	}

	public String load() {
		return MFXResources.load(getPath());
	}

	public URL loadURL() {
		return MFXResources.loadURL(getPath());
	}
}
