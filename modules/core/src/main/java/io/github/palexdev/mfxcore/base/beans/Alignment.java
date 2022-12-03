/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.beans;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

/**
 * This immutable bean's purpose is to allow specifying positions based on
 * {@link HPos} and {@link VPos} enumerations.
 */
public class Alignment {
	//================================================================================
	// Properties
	//================================================================================
	private final HPos hPos;
	private final VPos vPos;

	//================================================================================
	// Constructors
	//================================================================================
	public Alignment(HPos hPos, VPos vPos) {
		this.hPos = hPos;
		this.vPos = vPos;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static Alignment of(HPos hPos, VPos vPos) {
		return new Alignment(hPos, vPos);
	}

	//================================================================================
	// Getters
	//================================================================================
	public HPos getHPos() {
		return hPos;
	}

	public VPos getVPos() {
		return vPos;
	}
}
