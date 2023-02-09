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


package io.github.palexdev.mfxcomponents.controls.fab;

import io.github.palexdev.mfxcomponents.theming.base.WithVariants;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

import java.util.List;

/**
 * Extension of {@link MFXFabBase}. This variant allows to show both the text and icon.
 * <p></p>
 * Implements the {@link WithVariants} API, since these type of FABs have slightly different versions, the
 * variants are described by {@link FABVariants}. However, note that extended FABs do not have small and large
 * variants, applying those will result in an un-styled component.
 */
public class MFXExtendedFab extends MFXFabBase implements WithVariants<MFXExtendedFab, FABVariants> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXExtendedFab() {
		this("");
	}

	public MFXExtendedFab(String text) {
		this(text, null);
	}

	public MFXExtendedFab(MFXFontIcon icon) {
		this("", icon);
	}

	public MFXExtendedFab(String text, MFXFontIcon icon) {
		super(text, icon);
	}

	/**
	 * Creates a new {@code MFXExtendedFab}, surface color scheme variant.
	 */
	public static MFXExtendedFab surface() {
		return new MFXExtendedFab().setVariants(FABVariants.SURFACE);
	}

	/**
	 * Creates a new {@code MFXExtendedFab}, secondary color scheme variant.
	 */
	public static MFXExtendedFab secondary() {
		return new MFXExtendedFab().setVariants(FABVariants.SECONDARY);
	}

	/**
	 * Creates a new {@code MFXExtendedFab}, tertiary color scheme variant.
	 */
	public static MFXExtendedFab tertiary() {
		return new MFXExtendedFab().setVariants(FABVariants.TERTIARY);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button", "extended-fab");
	}

	@Override
	protected void applyInitSizes(boolean force) {
		double ih = getInitHeight();
		double iw = getInitWidth();
		if (force || getPrefHeight() <= 0.0) setPrefHeight(ih);
		if (force || getMinWidth() <= 0.0) setMinWidth(iw);
	}

	@Override
	public MFXExtendedFab addVariants(FABVariants... variants) {
		WithVariants.addVariants(this, variants);
		applyInitSizes(true);
		return this;
	}

	@Override
	public MFXExtendedFab setVariants(FABVariants... variants) {
		WithVariants.setVariants(this, variants);
		applyInitSizes(true);
		return this;
	}
}

