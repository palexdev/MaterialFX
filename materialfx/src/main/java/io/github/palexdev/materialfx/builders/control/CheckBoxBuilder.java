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

import io.github.palexdev.materialfx.builders.base.ButtonBaseBuilder;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.scene.control.ContentDisplay;

public class CheckBoxBuilder extends ButtonBaseBuilder<MFXCheckbox> {

	//================================================================================
	// Constructors
	//================================================================================
	public CheckBoxBuilder() {
		this(new MFXCheckbox());
	}

	public CheckBoxBuilder(MFXCheckbox checkbox) {
		super(checkbox);
	}

	public static CheckBoxBuilder checkbox() {
		return new CheckBoxBuilder();
	}

	public static CheckBoxBuilder checkbox(MFXCheckbox checkbox) {
		return new CheckBoxBuilder(checkbox);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public CheckBoxBuilder setContentDisposition(ContentDisplay contentDisposition) {
		node.setContentDisposition(contentDisposition);
		return this;
	}

	public CheckBoxBuilder setGap(double gap) {
		node.setGap(gap);
		return this;
	}

	public CheckBoxBuilder setTextExpand(boolean textExpand) {
		node.setTextExpand(textExpand);
		return this;
	}

	public CheckBoxBuilder setIndeterminate(boolean indeterminate) {
		node.setIndeterminate(indeterminate);
		return this;
	}

	public CheckBoxBuilder setSelected(boolean selected) {
		node.setSelected(selected);
		return this;
	}

	public CheckBoxBuilder setAllowIndeterminate(boolean allowIndeterminate) {
		node.setAllowIndeterminate(allowIndeterminate);
		return this;
	}
}
