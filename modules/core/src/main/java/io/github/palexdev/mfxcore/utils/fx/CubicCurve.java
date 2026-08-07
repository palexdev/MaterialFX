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

import javafx.animation.Interpolator;

/// Backport from effects module.
public class CubicCurve extends Interpolator {
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    private static final double CUBIC_ERROR_BOUND = 0.001;

    public CubicCurve(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    double elevateCubic(double a, double b, double m) {
        return 3 * a * (1 - m) * (1 - m) * m +
               3 * b * (1 - m) * m * m +
               m * m * m;
    }

    @Override
    public double curve(double t) {
        double start = 0.0;
        double end = 1.0;
        while (true) {
            final double midpoint = (start + end) / 2;
            final double estimate = elevateCubic(x1, x2, midpoint);
            if (Math.abs(t - estimate) < CUBIC_ERROR_BOUND) {
                return elevateCubic(y1, y2, midpoint);
            }
            if (estimate < t) {
                start = midpoint;
            } else {
                end = midpoint;
            }
        }
    }
}
