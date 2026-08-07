/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxresources.icon.packs;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/// Interface representing a pack of font icons, see [Font Icon](https://fonts.google.com/knowledge/glossary/icon_font).
/// The bare minimum API for any icon pack is:
/// - Being able to retrieve the Unicode character of an icon from its name/description
///   (e.g.: "fas-user" -> '\\uEE0D')
/// - Expose the font file (as a JavaFX [Font]) needed for the icons to be rendered
public interface FontIconsPack {

    /// Default size for every icon pack's [Font].
    double DEFAULT_FONT_SIZE = 16.0;

    /// Default color for every icon.
    Color DEFAULT_COLOR = Color.web("#454545");

    /// Icon packs can optionally specify their name (by default the class' simple name).
    default String name() {
        return getClass().getSimpleName();
    }

    /// Icon packs can optionally expose all the names of their icons.<br >
    /// Their order is not guaranteed!
    default String[] iconNames() {
        return new String[0];
    }

    /// @return the Unicode character associated with the given icon name as a string.
    String icon(String name);

    /// @return the [Font] needed for the icons to be rendered properly.
    Font font();

    /// @return a new [Font] instance with the given size and [#font()] as the base.
    /// @see Font#font(String, double)
    default Font font(double size) {
        Font font = font();
        return size == font.getSize() ? font : Font.font(font.getFamily(), size);
    }

    /// Converts a Unicode character to its escape sequence representation.
    ///
    /// @param text the input string containing the Unicode character
    /// @return the Unicode escape sequence (e.g., "★" becomes "\\u2605")
    ///         or "\\u0000" if the input is empty
    static String textToUnicode(String text) {
        try {
            if (text == null || text.isEmpty()) {
                return "\\u0000";
            }

            StringBuilder result = new StringBuilder();
            for (char c : text.toCharArray()) {
                result.append(String.format("\\u%04X", (int) c));
            }
            return result.toString();
        } catch (Exception ignored) {
            return "\\u0000";
        }
    }
}
