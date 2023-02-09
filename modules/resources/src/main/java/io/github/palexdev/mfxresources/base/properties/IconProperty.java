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

package io.github.palexdev.mfxresources.base.properties;

import io.github.palexdev.mfxresources.fonts.IconProvider;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.text.Font;

import java.util.function.Function;

/**
 * Simple extension of {@link ReadOnlyObjectWrapper} to be used for {@link MFXFontIcon} objects,
 * also offers a series of convenient methods to manipulate the icon with fluent API.
 */
public class IconProperty extends ReadOnlyObjectWrapper<MFXFontIcon> {

	//================================================================================
	// Constructors
	//================================================================================
	public IconProperty() {
	}

	public IconProperty(MFXFontIcon initialValue) {
		super(initialValue);
	}

	public IconProperty(Object bean, String name) {
		super(bean, name);
	}

	public IconProperty(Object bean, String name, MFXFontIcon initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Setters
	//================================================================================

	/**
	 * Changes the {@link MFXFontIcon#descriptionProperty()} of the current value.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXFontIcon} will
	 * be created and no exception will be raised.
	 */
	public IconProperty setDescription(String description) {
		MFXFontIcon val = get();
		if (val == null) {
			set(new MFXFontIcon(description));
		} else {
			val.setDescription(description);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXFontIcon#setIconsProvider(IconProvider)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXFontIcon} will
	 * be created and no exception will be raised.
	 */
	public IconProperty setProvider(IconProvider provider) {
		MFXFontIcon val = get();
		if (val == null) {
			set(new MFXFontIcon().setIconsProvider(provider));
		} else {
			val.setIconsProvider(provider);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXFontIcon#setIconsProvider(Font, Function)}.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXFontIcon} will
	 * be created and no exception will be raised.
	 */
	public IconProperty setProvider(Font font, Function<String, Character> converter) {
		MFXFontIcon val = get();
		if (val == null) {
			set(new MFXFontIcon().setIconsProvider(font, converter));
		} else {
			val.setIconsProvider(font, converter);
		}
		return this;
	}

	/**
	 * Delegate for {@link MFXFontIcon#setIconsProvider(IconProvider)}, additionally
	 * also changes the {@link MFXFontIcon#descriptionProperty()} to the given one.
	 * <p>
	 * This is null-safe, meaning that if the current value of the property is null a new {@link MFXFontIcon} will
	 * be created and no exception will be raised.
	 */
	public IconProperty setProvider(IconProvider provider, String description) {
		MFXFontIcon val = get();
		if (val == null) {
			set(new MFXFontIcon().setIconsProvider(provider).setDescription(description));
		} else {
			val.setIconsProvider(provider).setDescription(description);
		}
		return this;
	}
}