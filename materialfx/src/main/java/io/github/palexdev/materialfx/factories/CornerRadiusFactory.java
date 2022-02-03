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

package io.github.palexdev.materialfx.factories;

import javafx.scene.layout.CornerRadii;

/**
 * Convenience class to build {@link CornerRadii} objects.
 */
public class CornerRadiusFactory {

	//================================================================================
	// Constructors
	//================================================================================
	private CornerRadiusFactory() {}

	//================================================================================
	// Static Methods
	//================================================================================
	public static CornerRadii all(double topRightBottomLeft) {
		return new CornerRadii(topRightBottomLeft);
	}

	public static CornerRadii none() {
		return CornerRadii.EMPTY;
	}

	public static CornerRadii top(double topLeft) {
		return new CornerRadii(topLeft, 0, 0, 0, false);
	}

	public static CornerRadii right(double topRight) {
		return new CornerRadii(0, topRight, 0, 0, false);
	}

	public static CornerRadii bottom(double bottomRight) {
		return new CornerRadii(0, 0, bottomRight, 0, false);
	}

	public static CornerRadii left(double bottomLeft) {
		return new CornerRadii(0, 0, 0, bottomLeft, false);
	}

	public static CornerRadii of(double topLeft, double topRight) {
		return new CornerRadii(topLeft, topRight, 0, 0, false);
	}

	public static CornerRadii of(double topLeft, double topRight, double bottomRight) {
		return new CornerRadii(topLeft, topRight, bottomRight, 0, false);
	}

	public static CornerRadii of(double topLeft, double topRight, double bottomRight, double bottomLeft) {
		return new CornerRadii(topLeft, topRight, bottomRight, bottomLeft, false);
	}
}
