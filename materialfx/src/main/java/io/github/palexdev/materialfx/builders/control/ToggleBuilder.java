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

import io.github.palexdev.materialfx.builders.base.LabeledBuilder;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

public class ToggleBuilder extends LabeledBuilder<MFXToggleButton> {

	//================================================================================
	// Constructors
	//================================================================================
	public ToggleBuilder() {
		this(new MFXToggleButton());
	}

	public ToggleBuilder(MFXToggleButton toggleButton) {
		super(toggleButton);
	}

	public static ToggleBuilder toggle() {
		return new ToggleBuilder();
	}

	public static ToggleBuilder toggle(MFXToggleButton toggleButton) {
		return new ToggleBuilder(toggleButton);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ToggleBuilder setToggleGroup(ToggleGroup toggleGroup) {
		node.setToggleGroup(toggleGroup);
		return this;
	}

	public ToggleBuilder setSelected(boolean selected) {
		node.setSelected(selected);
		return this;
	}

	public ToggleBuilder setOnAction(EventHandler<ActionEvent> onAction) {
		node.setOnAction(onAction);
		return this;
	}

	public ToggleBuilder setContentDisposition(ContentDisplay contentDisposition) {
		node.setContentDisposition(contentDisposition);
		return this;
	}

	public ToggleBuilder setGap(double gap) {
		node.setGap(gap);
		return this;
	}

	public ToggleBuilder setLength(double length) {
		node.setLength(length);
		return this;
	}

	public ToggleBuilder setRadius(double radius) {
		node.setRadius(radius);
		return this;
	}

	public ToggleBuilder setTextExpand(boolean textExpand) {
		node.setTextExpand(textExpand);
		return this;
	}

	public ToggleBuilder setMainColor(Color color) {
		node.setMainColor(color);
		return this;
	}

	public ToggleBuilder setSecondaryColor(Color color) {
		node.setSecondaryColor(color);
		return this;
	}

	public ToggleBuilder setColors(Color main, Color secondary) {
		node.setColors(main, secondary);
		return this;
	}
}
