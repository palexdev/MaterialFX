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

package io.github.palexdev.mfxcore.utils.fx.resize.targets;

import io.github.palexdev.mfxcore.utils.fx.resize.ResizeHandler;
import io.github.palexdev.mfxcore.utils.fx.resize.Resizer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/// [ResizeTarget] for a [Rectangle]: writes `x`/`y`/`width`/`height` directly, all four in the shape's own frame.
///
/// Unbounded in both directions: a [Rectangle] has no notion of a min or max size. If you need limits, implement this
/// interface yourself or use a [ResizeHandler] on the [Resizer].
public record RectangleTarget(Rectangle target) implements ResizeTarget<Rectangle> {

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Node hitNode() {
        return target;
    }

    @Override
    public Bounds bounds() {
        return new BoundingBox(
            target.getX(), target.getY(),
            target.getWidth(), target.getHeight()
        );
    }

    @Override
    public void apply(double x, double y, double w, double h) {
        target.setX(x);
        target.setY(y);
        target.setWidth(w);
        target.setHeight(h);
    }

    @Override
    public double minW() {
        return 0;
    }

    @Override
    public double minH() {
        return 0;
    }

    @Override
    public double maxW() {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxH() {
        return Double.MAX_VALUE;
    }
}
