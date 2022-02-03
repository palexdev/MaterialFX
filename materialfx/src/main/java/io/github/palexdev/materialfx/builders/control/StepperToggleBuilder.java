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

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import io.github.palexdev.materialfx.enums.TextPosition;
import javafx.scene.Node;

public class StepperToggleBuilder extends ControlBuilder<MFXStepperToggle> {

	//================================================================================
	// Constructors
	//================================================================================
	public StepperToggleBuilder() {
		this(new MFXStepperToggle());
	}

	public StepperToggleBuilder(MFXStepperToggle stepperToggle) {
		super(stepperToggle);
	}

	public static StepperToggleBuilder stepperToggle() {
		return new StepperToggleBuilder();
	}

	public static StepperToggleBuilder stepperToggle(MFXStepperToggle stepperToggle) {
		return new StepperToggleBuilder(stepperToggle);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public StepperToggleBuilder setContent(Node content) {
		node.setContent(content);
		return this;
	}

	public StepperToggleBuilder setText(String text) {
		node.setText(text);
		return this;
	}

	public StepperToggleBuilder setIcon(Node icon) {
		node.setIcon(icon);
		return this;
	}

	public StepperToggleBuilder setState(StepperToggleState state) {
		node.setState(state);
		return this;
	}

	public StepperToggleBuilder setShowErrorIcon(boolean showErrorIcon) {
		node.setShowErrorIcon(showErrorIcon);
		return this;
	}

	public StepperToggleBuilder setLabelTextGap(double labelTextGap) {
		node.setLabelTextGap(labelTextGap);
		return this;
	}

	public StepperToggleBuilder setTextPosition(TextPosition textPosition) {
		node.setTextPosition(textPosition);
		return this;
	}

	public StepperToggleBuilder setSize(double size) {
		node.setSize(size);
		return this;
	}

	public StepperToggleBuilder setStrokeWidth(double strokeWidth) {
		node.setStrokeWidth(strokeWidth);
		return this;
	}
}
