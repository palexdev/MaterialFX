/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.materialfx.behaviors;

import io.github.palexdev.materialfx.controls.buttons.MFXButton;
import io.github.palexdev.materialfx.skins.MFXButtonSkin;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.scene.input.MouseEvent;

/**
 * This is the default behavior used by all {@link MFXButton}s.
 * <p>
 * Defines the actions to:
 * <p> - generate ripples
 * <p> - handle mouse press
 * <p> - handle mouse click
 */
public class MFXButtonBehavior extends BehaviorBase<MFXButton> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXButtonBehavior(MFXButton node) {
		super(node);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Instructs the given {@link MFXRippleGenerator} to generate a ripple for the given
	 * {@link MouseEvent}.
	 * <p></p>
	 * The parameters are given by the default skin, {@link MFXButtonSkin}, associated to each button.
	 */
	public void generateRipple(MFXRippleGenerator rg, MouseEvent me) {
		rg.generate(me);
	}

	/**
	 * Requests focus on mouse pressed.
	 */
	public void mousePressed() {
		getNode().requestFocus();
	}

	/**
	 * Calls {@link MFXButton#fire()} on mouse clicked.
	 */
	public void mouseClicked() {
		getNode().fire();
	}
}
