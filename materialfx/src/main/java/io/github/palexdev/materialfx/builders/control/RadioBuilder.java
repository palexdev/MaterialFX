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
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleGroup;

public class RadioBuilder extends ButtonBaseBuilder<MFXRadioButton> {

	//================================================================================
	// Constructors
	//================================================================================
	public RadioBuilder() {
		this(new MFXRadioButton());
	}

	public RadioBuilder(MFXRadioButton radioButton) {
		super(radioButton);
	}

	public static RadioBuilder radio() {
		return new RadioBuilder();
	}

	public static RadioBuilder radio(MFXRadioButton radioButton) {
		return new RadioBuilder(radioButton);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public RadioBuilder setContentDisposition(ContentDisplay contentDisposition) {
		node.setContentDisposition(contentDisposition);
		return this;
	}

	public RadioBuilder setGap(double gap) {
		node.setGap(gap);
		return this;
	}

	public RadioBuilder setRadioGap(double radioGap) {
		node.setRadioGap(radioGap);
		return this;
	}

	public RadioBuilder setRadius(double radius) {
		node.setRadius(radius);
		return this;
	}

	public RadioBuilder setTextExpand(boolean textExpand) {
		node.setTextExpand(textExpand);
		return this;
	}

	public RadioBuilder setSelected(boolean value) {
		node.setSelected(value);
		return this;
	}

	public RadioBuilder setToggleGroup(ToggleGroup value) {
		node.setToggleGroup(value);
		return this;
	}
}
