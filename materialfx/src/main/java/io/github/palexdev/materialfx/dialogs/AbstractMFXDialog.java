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

package io.github.palexdev.materialfx.dialogs;

import javafx.scene.layout.BorderPane;

/**
 * Base class every {@code MFXDialog} should extend.
 */
public abstract class AbstractMFXDialog extends BorderPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-dialog";

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXDialog() {
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setMinSize(400, 200);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}
}
