/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXResources (https://github.com/palexdev/MFXResources).
 *
 * MFXResources is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXResources is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXResources.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxresources.font;

import io.github.palexdev.mfxresources.ResourcesManager;
import javafx.scene.text.Font;

/**
 * Handler for MaterialFX font resources.
 */
public class FontHandler {
	private static final Font resources;

	private FontHandler() {
	}

	static {
		resources = Font.loadFont(ResourcesManager.loadStream("fonts/MFXResources.ttf"), 10);
	}

	public static Font getResources() {
		return resources;
	}

	public static char getCode(String description) {
		return FontResources.findByDescription(description).getCode();
	}
}
