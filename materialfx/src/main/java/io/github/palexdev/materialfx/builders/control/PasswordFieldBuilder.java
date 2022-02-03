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

import io.github.palexdev.materialfx.controls.MFXPasswordField;

public class PasswordFieldBuilder extends TextFieldBuilder<MFXPasswordField> {

	//================================================================================
	// Constructors
	//================================================================================
	public PasswordFieldBuilder() {
		this(new MFXPasswordField());
	}

	public PasswordFieldBuilder(MFXPasswordField passwordField) {
		super(passwordField);
	}
	public static PasswordFieldBuilder passwordField() {
		return new PasswordFieldBuilder();
	}

	public static PasswordFieldBuilder passwordField(MFXPasswordField passwordField) {
		return new PasswordFieldBuilder(passwordField);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public PasswordFieldBuilder setAllowCopy(boolean allowCopy) {
		node.setAllowCopy(allowCopy);
		return this;
	}

	public PasswordFieldBuilder setAllowCut(boolean allowCut) {
		node.setAllowCut(allowCut);
		return this;
	}

	public PasswordFieldBuilder setAllowPaste(boolean allowPaste) {
		node.setAllowPaste(allowPaste);
		return this;
	}

	public PasswordFieldBuilder setShowPassword(boolean showPassword) {
		node.setShowPassword(showPassword);
		return this;
	}

	public PasswordFieldBuilder setHideCharacter(String hideCharacter) {
		node.setHideCharacter(hideCharacter);
		return this;
	}
}
