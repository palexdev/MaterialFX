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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.INodeBuilder;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.font.FontResources;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.scene.paint.Color;

public class IconBuilder implements INodeBuilder<MFXFontIcon> {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXFontIcon icon;

	//================================================================================
	// Constructors
	//================================================================================
	public IconBuilder() {
		icon = new MFXFontIcon();
	}

	public IconBuilder(MFXFontIcon icon) {
		this.icon = icon;
	}

	public static IconBuilder icon() {
		return new IconBuilder();
	}

	public static IconBuilder icon(MFXFontIcon icon) {
		return new IconBuilder(icon);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public static MFXFontIcon getRandomIcon(double size, Color color) {
		return MFXFontIcon.getRandomIcon(size, color);
	}

	public IconBuilder setColor(Color color) {
		icon.setColor(color);
		return this;
	}

	public IconBuilder setDescription(String code) {
		icon.setDescription(code);
		return this;
	}

	public IconBuilder setDescription(FontResources resource) {
		icon.setDescription(resource.getDescription());
		return this;
	}

	public IconBuilder setSize(double size) {
		icon.setSize(size);
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================
	public MFXIconWrapper wrapIcon(double size, boolean addRippleGenerator, boolean makeCircular) {
		MFXIconWrapper wrapped = new MFXIconWrapper(icon, size);
		if (addRippleGenerator) wrapped.defaultRippleGeneratorBehavior();
		if (makeCircular) NodeUtils.makeRegionCircular(wrapped);
		return wrapped;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public MFXFontIcon getNode() {
		return null;
	}
}
