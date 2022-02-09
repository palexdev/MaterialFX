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

package io.github.palexdev.materialfx.builders.base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;

public class ButtonBaseBuilder<B extends ButtonBase> extends LabeledBuilder<B> {

	//================================================================================
	// Constructors
	//================================================================================
	public ButtonBaseBuilder(B buttonBase) {
		super(buttonBase);
	}

	public static ButtonBaseBuilder<ButtonBase> control(ButtonBase buttonBase) {
		return new ButtonBaseBuilder<>(buttonBase);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ButtonBaseBuilder<B> setOnAction(EventHandler<ActionEvent> handler) {
		node.setOnAction(handler);
		return this;
	}

	public ButtonBaseBuilder<B> arm() {
		node.arm();
		return this;
	}

	public ButtonBaseBuilder<B> disarm() {
		node.disarm();
		return this;
	}

	public ButtonBaseBuilder<B> fire() {
		node.fire();
		return this;
	}
}
