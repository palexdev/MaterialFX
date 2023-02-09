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

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXElevatedButton;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

import java.util.List;

/**
 * Extension of {@link MFXElevatedButton} and base class to implement the Floating Action Buttons shown
 * in the MD3 guidelines.
 * <p></p>
 * This base class has two variants: {@link MFXFab} and {@link MFXExtendedFab}.
 * <p>
 * This is meant to be used by users that want an untouched base FAB, this component just like {@link MFXButton} is
 * not styled by the themes by default.
 * <p>
 * It's selector in CSS is: '.mfx-button.fab-base'.
 * <p></p>
 * Since FABs are meant to be used with icons, these enforce the usage of {@link MFXFontIcon}s
 * rather than generic nodes.
 */
public class MFXFabBase extends MFXElevatedButton {
	//================================================================================
	// Properties
	//================================================================================
	private final IconProperty icon = new IconProperty();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabBase() {
		this("");
	}

	public MFXFabBase(String text) {
		this(text, null);
	}

	public MFXFabBase(MFXFontIcon icon) {
		this("", icon);
	}

	public MFXFabBase(String text, MFXFontIcon icon) {
		super(text, icon);
		initialize();
		setIcon(icon);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		graphicProperty().bind(icon);
		sceneBuilderIntegration();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button", "fab-base");
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXFontIcon getIcon() {
		return iconProperty().get();
	}

	/**
	 * Specifies the FAB's icon.
	 */
	public IconProperty iconProperty() {
		return icon;
	}

	public void setIcon(MFXFontIcon icon) {
		iconProperty().set(icon);
	}
}
