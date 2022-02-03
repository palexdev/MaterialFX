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
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.scene.paint.Paint;

public class ButtonBuilder extends ButtonBaseBuilder<MFXButton> {

	//================================================================================
	// Constructors
	//================================================================================
	public ButtonBuilder() {
		this(new MFXButton());
	}

	public ButtonBuilder(MFXButton button) {
		super(button);
	}

	public static ButtonBuilder button() {
		return new ButtonBuilder();
	}

	public static ButtonBuilder button(MFXButton button) {
		return new ButtonBuilder(button);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ButtonBuilder setComputeRadiusMultiplier(boolean computeRadiusMultiplier) {
		node.setComputeRadiusMultiplier(computeRadiusMultiplier);
		return this;
	}

	public ButtonBuilder setRippleAnimateBackground(boolean rippleAnimateBackground) {
		node.setRippleAnimateBackground(rippleAnimateBackground);
		return this;
	}

	public ButtonBuilder setRippleAnimateShadow(boolean rippleAnimateShadow) {
		node.setRippleAnimateShadow(rippleAnimateShadow);
		return this;
	}

	public ButtonBuilder setRippleAnimationSpeed(double rippleAnimationSpeed) {
		node.setRippleAnimationSpeed(rippleAnimationSpeed);
		return this;
	}

	public ButtonBuilder setRippleBackgroundOpacity(double rippleBackgroundOpacity) {
		node.setRippleBackgroundOpacity(rippleBackgroundOpacity);
		return this;
	}

	public ButtonBuilder setRippleColor(Paint rippleColor) {
		node.setRippleColor(rippleColor);
		return this;
	}

	public ButtonBuilder setRippleRadius(double rippleRadius) {
		node.setRippleRadius(rippleRadius);
		return this;
	}

	public ButtonBuilder setRippleRadiusMultiplier(double rippleRadiusMultiplier) {
		node.setRippleRadiusMultiplier(rippleRadiusMultiplier);
		return this;
	}

	public ButtonBuilder setDepthLevel(DepthLevel depthLevel) {
		node.setDepthLevel(depthLevel);
		return this;
	}

	public ButtonBuilder setButtonType(ButtonType buttonType) {
		node.setButtonType(buttonType);
		return this;
	}
}
