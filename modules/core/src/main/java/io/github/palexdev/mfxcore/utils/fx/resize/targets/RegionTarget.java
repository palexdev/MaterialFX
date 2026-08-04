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

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/// [ResizeTarget] for any [Region]: writes the layout position and the pref sizes, both in parent coordinates.
///
/// The min/max bounds read the size *override* rather than the resolved value, because the resizer is what writes pref
/// and a [Region#USE_PREF_SIZE] override would otherwise pin the node to the size it already has. A plain
/// [Region#USE_COMPUTED_SIZE] still resolves: a skin that genuinely caps itself should be respected.
public record RegionTarget(Region target) implements ResizeTarget<Region> {

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
            target.getLayoutX(), target.getLayoutY(),
            target.getWidth(), target.getHeight()
        );
    }

    @Override
    public void apply(double x, double y, double w, double h) {
        target.setLayoutX(x);
        target.setLayoutY(y);
        target.setPrefSize(w, h);
    }

    @Override
    public double minW() {
        return target.getMinWidth() == Region.USE_PREF_SIZE ? 0 : target.minWidth(-1);
    }

    @Override
    public double minH() {
        return target.getMinHeight() == Region.USE_PREF_SIZE ? 0 : target.minHeight(-1);
    }

    @Override
    public double maxW() {
        return target.getMaxWidth() == Region.USE_PREF_SIZE ? Double.MAX_VALUE : target.maxWidth(-1);
    }

    @Override
    public double maxH() {
        return target.getMaxHeight() == Region.USE_PREF_SIZE ? Double.MAX_VALUE : target.maxHeight(-1);
    }
}
