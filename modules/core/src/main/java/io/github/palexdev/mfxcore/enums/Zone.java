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

package io.github.palexdev.mfxcore.enums;

public enum Zone {
	TOP_RIGHT, TOP_CENTER, TOP_LEFT,
	BOTTOM_RIGHT, BOTTOM_CENTER, BOTTOM_LEFT,
	CENTER_RIGHT, CENTER_LEFT,
	NONE, ALL;

	public static boolean isRight(Zone zone) {
		return zone == TOP_RIGHT || zone == CENTER_RIGHT || zone == BOTTOM_RIGHT;
	}

	public static boolean isLeft(Zone zone) {
		return zone == TOP_LEFT || zone == CENTER_LEFT || zone == BOTTOM_LEFT;
	}

	public static boolean isTop(Zone zone) {
		return zone == TOP_LEFT || zone == TOP_CENTER || zone == TOP_RIGHT;
	}

	public static boolean isBottom(Zone zone) {
		return zone == BOTTOM_RIGHT || zone == BOTTOM_CENTER || zone == BOTTOM_LEFT;
	}
}
