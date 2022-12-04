/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxresources.builders;

import io.github.palexdev.mfxresources.font.MFXFontIcon;
import javafx.scene.paint.Color;

public class IconBuilder {
	//================================================================================
	// Properties
	//================================================================================
	protected final MFXFontIcon icon;

	//================================================================================
	// Constructors
	//================================================================================
	public IconBuilder() {
		this.icon = new MFXFontIcon();
	}

	public IconBuilder(MFXFontIcon icon) {
		this.icon = icon;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static IconBuilder build() {
		return new IconBuilder();
	}

	public static IconBuilder build(MFXFontIcon icon) {
		return new IconBuilder(icon);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public IconBuilder setColor(Color color) {
		icon.setColor(color);
		return this;
	}

	public IconBuilder setDescription(String code) {
		icon.setDescription(code);
		return this;
	}

	public IconBuilder setSize(double size) {
		icon.setSize(size);
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================
	public MFXFontIcon get() {
		return icon;
	}
}
