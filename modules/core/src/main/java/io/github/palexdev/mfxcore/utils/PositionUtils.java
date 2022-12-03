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

package io.github.palexdev.mfxcore.utils;

import javafx.geometry.Pos;

/**
 * Utilities for JavaFX's {@link Pos}.
 */
public class PositionUtils {

	private PositionUtils() {
	}

	public static boolean isTop(Pos pos) {
		return pos == Pos.TOP_LEFT || pos == Pos.TOP_CENTER || pos == Pos.TOP_RIGHT;
	}

	public static boolean isCenter(Pos pos) {
		return pos == Pos.CENTER_LEFT || pos == Pos.CENTER || pos == Pos.CENTER_RIGHT || pos == Pos.TOP_CENTER || pos == Pos.BOTTOM_CENTER;
	}

	public static boolean isBottom(Pos pos) {
		return pos == Pos.BOTTOM_LEFT || pos == Pos.BOTTOM_CENTER || pos == Pos.BOTTOM_RIGHT;
	}

	public static boolean isLeft(Pos pos) {
		return pos == Pos.TOP_LEFT || pos == Pos.CENTER_LEFT || pos == Pos.BOTTOM_LEFT;
	}

	public static boolean isRight(Pos pos) {
		return pos == Pos.TOP_RIGHT || pos == Pos.CENTER_RIGHT || pos == Pos.BOTTOM_RIGHT;
	}
}
