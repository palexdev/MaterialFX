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

package io.github.palexdev.materialfx.effects;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Utility class which manages a preset number of {@code DropShadow} effects ordered by {@code DepthLevel}, but
 * it also allows to create custom {@code DropShadow} effects with {@link #shadowOf(Color, double, double, double, double)}.
 * <p></p>
 * {@link DepthLevel}
 */
public class MFXDepthManager {

	/**
	 * Returns a new instance of {@code DropShadow} with the specified characteristics.
	 *
	 * @return The desired custom {@code DropShadow} effect
	 * @see DropShadow
	 */
	public static DropShadow shadowOf(Color color, double radius, double spread, double offsetX, double offsetY) {
		return new DropShadow(
				BlurType.GAUSSIAN,
				color,
				radius,
				spread,
				offsetX,
				offsetY
		);
	}

	/**
	 * Retrieves the {@code DropShadow} associated with the specified {@code DepthLevel}.
	 *
	 * @param level The desired {@code DepthLevel} between 1 and 5
	 * @return The desired {@code DropShadow} effect
	 */
	public static DropShadow shadowOf(DepthLevel level) {
		return new DropShadow(
				BlurType.GAUSSIAN,
				level.getColor(),
				level.getRadius(),
				level.getSpread(),
				level.getOffsetX(),
				level.getOffsetY()
		);
	}

	/**
	 * Retrieves the {@code DropShadow} associated with the specified {@code DepthLevel} added to delta.
	 * <p></p>
	 * Example 1: for a depth level equal to 3 and a delta equal to 2, the returned {@code DropShadow} effect is
	 * the effected associated to a depth level of 5.
	 * <p></p>
	 * Example 2: for a depth level equal to 5 and a delta equal to whatever integer, the returned {@code DropShadow} effect is
	 * the effected associated to a depth level of 5.
	 *
	 * @param level The desired {@code DepthLevel} between 1 and 5
	 * @param delta The number of levels to shift
	 * @return The final {@code DropShadow} effect}
	 * <p></p>
	 * {@link #nextLevel(DepthLevel)}
	 */
	public static DropShadow shadowOf(DepthLevel level, int delta) {
		DepthLevel endLevel = level;
		for (int i = 0; i < delta; i++) {
			endLevel = nextLevel(endLevel);
		}
		return shadowOf(endLevel);
	}

	/**
	 * From a starting {@code DepthLevel} retrieves the {@code DropShadow} effect associated to the next {@code DepthLevel}.
	 *
	 * @param startLevel The starting {@code DepthLevel}
	 * @return The {@code DropShadow} effect associated to the next {@code DepthLevel}
	 * @see DepthLevel
	 */
	private static DepthLevel nextLevel(DepthLevel startLevel) {
		return !(startLevel.equals(DepthLevel.LEVEL5)) ? startLevel.next() : DepthLevel.LEVEL5;
	}
}
