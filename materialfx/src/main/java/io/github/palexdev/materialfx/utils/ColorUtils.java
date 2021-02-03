/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Utils class for JavaFX's {@code Color}s and CSS.
 */
public class ColorUtils {
    private static final Random random = new Random(System.currentTimeMillis());

    private ColorUtils() {
    }

    /**
     * Converts a JavaFX's {@code Color} to CSS corresponding rgb function.
     * @return the rgb function as a string
     */
    public static String rgb(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }

    /**
     * Converts a JavaFX's {@code Color} to CSS corresponding rgba function.
     * @return the rgba function as a string
     */
    public static String rgba(Color color) {
        return String.format("rgba(%d, %d, %d, %s)",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()),
                color.getOpacity());
    }

    /**
     * Generates a random {@code Color} using java.util.Random.
     */
    public static Color getRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
