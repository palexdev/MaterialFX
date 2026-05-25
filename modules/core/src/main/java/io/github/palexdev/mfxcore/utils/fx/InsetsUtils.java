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

package io.github.palexdev.mfxcore.utils.fx;

import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;

/// Utility class for [Insets], also hosts the [InsetsBuilder] inner class.
public class InsetsUtils {

    //================================================================================
    // Constructors
    //================================================================================

    private InsetsUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    public static InsetsBuilder insets() {
        return new InsetsBuilder();
    }

    public static InsetsBuilder uniform(double all) {
        return new InsetsBuilder(new Insets(all, all, all, all));
    }

    public static InsetsBuilder top(double top) {
        return new InsetsBuilder(new Insets(top, 0, 0, 0));
    }

    public static InsetsBuilder right(double right) {
        return new InsetsBuilder(new Insets(0, right, 0, 0));
    }

    public static InsetsBuilder bottom(double bottom) {
        return new InsetsBuilder(new Insets(0, 0, bottom, 0));
    }

    public static InsetsBuilder left(double left) {
        return new InsetsBuilder(new Insets(0, 0, 0, left));
    }

    public static boolean isUniform(Insets insets) {
        return insets.getTop() == insets.getRight() &&
               insets.getTop() == insets.getBottom() &&
               insets.getTop() == insets.getLeft();
    }

    public static String stringify(Insets insets) {
        if (isUniform(insets)) return String.valueOf(insets.getTop());
        return insets.getTop() + " " +
               insets.getRight() + " " +
               insets.getBottom() + " " +
               insets.getLeft();
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    public static class InsetsBuilder {

        private Insets insets;

        public InsetsBuilder() {
            this(Insets.EMPTY);
        }

        public InsetsBuilder(Insets insets) {
            this.insets = insets;
        }

        public InsetsBuilder top(double top) {
            insets = new Insets(top, insets.getRight(), insets.getBottom(), insets.getLeft());
            return this;
        }

        public InsetsBuilder right(double right) {
            insets = new Insets(insets.getTop(), right, insets.getBottom(), insets.getLeft());
            return this;
        }

        public InsetsBuilder bottom(double bottom) {
            insets = new Insets(insets.getTop(), insets.getRight(), bottom, insets.getLeft());
            return this;
        }

        public InsetsBuilder left(double left) {
            insets = new Insets(insets.getTop(), insets.getRight(), insets.getBottom(), left);
            return this;
        }

        public InsetsBuilder vertical(double topBottom) {
            insets = new Insets(topBottom, insets.getRight(), topBottom, insets.getLeft());
            return this;
        }

        public InsetsBuilder horizontal(double leftRight) {
            insets = new Insets(insets.getTop(), leftRight, insets.getBottom(), leftRight);
            return this;
        }

        public Insets get() {
            return insets;
        }

        public CornerRadii toRadius(boolean asPercent) {
            return new CornerRadii(insets.getTop(), insets.getRight(), insets.getBottom(), insets.getLeft(), asPercent);
        }

        public boolean isUniform() {
            return InsetsUtils.isUniform(insets);
        }

        public String stringify() {
            return InsetsUtils.stringify(insets);
        }
    }
}