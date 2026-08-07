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

package io.github.palexdev.mfxresources.icon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/// Simple record to create a clip Node of the desired shape and radius.
///
/// The `radius` parameter can have different meanings depending on the shape. For example, for a [Circle]
/// it represents its size, while for a [Rectangle] it represents the arcs' width and height.
public record IconClip(
    ClipShape shape,
    double radius
) {

    //================================================================================
    // Methods
    //================================================================================
    public static IconClip of(ClipShape type, double radius) {
        return new IconClip(type, radius);
    }

    public Node build(Region region) {
        return shape.buildClip(region, radius);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Enumeration to represent the various possible shapes of a clip node.
    public enum ClipShape {
        /// Creates a [Circle] clip which will be centered on its "parent". The radius is either the given parameter
        /// if positive; otherwise it's given by the maximum between the parent's width and height, divided by two.
        ROUNDED {
            @Override
            public Node buildClip(Region region, double radius) {
                Circle circle = new Circle();
                if (radius < 0) {
                    circle.radiusProperty().bind(region.layoutBoundsProperty()
                        .map(b -> Math.max(b.getWidth(), b.getHeight()) / 2.0));
                } else {
                    circle.setRadius(radius);
                }
                circle.centerXProperty().bind(region.widthProperty().divide(2.0));
                circle.centerYProperty().bind(region.heightProperty().divide(2.0));
                return circle;
            }
        },

        /// Creates a [Rectangle] clip which will have the same width and height of its "parent". The clip is rounded
        /// by the given radius parameter.
        SQUARED {
            @Override
            public Node buildClip(Region region, double radius) {
                Rectangle rect = new Rectangle();
                rect.widthProperty().bind(region.widthProperty());
                rect.heightProperty().bind(region.heightProperty());
                rect.setArcWidth(radius);
                rect.setArcHeight(radius);
                return rect;
            }
        };

        /// Builds the clip node with the shape indicated by the constant, the given radius, for the given "parent".
        ///
        /// @param region the "parent" on which the clip will be applied, note that the clip is not set automatically
        /// @param radius the clip's radius, the actual meaning depends on the shape
        public abstract Node buildClip(Region region, double radius);
    }

    /// Extension of [StyleConverter] which allows setting a clip for a node that adds the related CSS metadata from
    /// CSS. Unfortunately, JavaFX seems to parse sequence properties in special ways internally (like -fx-font) and therefore
    /// there doesn't seem to be any way but to make this a string.
    ///
    ///
    /// **Usage:** `-property: <shape> <radius>` where `<shape>` is the name of a constant from [ClipShape]
    /// (can be lowercase) and `<radius>` is a number (size units are supported).
    public static class IconClipConverter extends StyleConverter<String, IconClip> {
        private static final IconClipConverter INSTANCE = new IconClipConverter();

        public static IconClipConverter instance() {
            return INSTANCE;
        }

        private IconClipConverter() {}

        @Override
        public IconClip convert(ParsedValue<String, IconClip> value, Font font) {
            String sVal = value.getValue();
            if (sVal == null || sVal.isBlank()) return null;

            String[] sVals = sVal.split(" ");
            ClipShape shape = ClipShape.valueOf(sVals[0].toUpperCase());
            double px = convertSize(sVals);
            return new IconClip(shape, px);
        }

        private double convertSize(String[] sVals) {
            if (sVals.length == 1) return -1.0;

            Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d+)([a-zA-Z%]+)");
            Matcher matcher = pattern.matcher(sVals[1].trim());
            if (!matcher.matches())
                throw new IllegalArgumentException("Invalid size format: " + sVals[1]);

            double val = Double.parseDouble(matcher.group(1));
            SizeUnits units = SizeUnits.valueOf(matcher.group(2).toUpperCase());
            return new Size(val, units).pixels();
        }
    }
}
