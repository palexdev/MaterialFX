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

package io.github.palexdev.mfxcore.utils.fx.resize;

import io.github.palexdev.mfxcore.enums.Zone;

/// Describes how thick a node's resize border is, and how much of it belongs to the corners.
///
/// - `inner` reaches from the node's edge inwards, `outer` from the same edge outwards. Together they're how thick an
/// edge feels to grab. `outer` is only reachable if the [Resizer] was installed on an ancestor, since on a self-install
/// there's nothing out there to receive the event
/// - `corner` is how far the L of a corner zone runs along each of its two edges. `0` disables corners entirely
///
/// The corner arms are clamped per axis to a third of the node, so on something too short for two full arms the edges
/// stay reachable instead of the whole border becoming corner.
// TODO unit tests
public record ResizeBand(double inner, double outer, double corner) {

    //================================================================================
    // Properties
    //================================================================================

    public static final ResizeBand DEFAULT = new ResizeBand(8.0, 6.0, 20.0);

    //================================================================================
    // Constructors
    //================================================================================

    /// @return a band reaching equally far in and out of the node's edge, with proportionate corners
    public static ResizeBand symmetric(double thickness) {
        return new ResizeBand(thickness, thickness, thickness * 2.5);
    }

    /// @return a band of the given thickness, entirely inside the node, with proportionate corners
    public static ResizeBand ofInner(double inner) {
        return new ResizeBand(inner, 0.0, inner * 2.5);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Runs the band against a `w` by `h` box, where `x` and `y` are the pointer relative to the box's top left corner.
    ///
    /// @return the zone the pointer is over, or [Zone#NONE]
    public Zone detect(double x, double y, double w, double h) {
        if (x < -outer || x > w + outer || y < -outer || y > h + outer) return Zone.NONE;

        boolean left = x >= -outer && x <= inner;
        boolean right = x >= w - inner && x <= w + outer;
        boolean top = y >= -outer && y <= inner;
        boolean bottom = y >= h - inner && y <= h + outer;
        if (!(left || right || top || bottom)) return Zone.NONE;

        if (left && right) {
            if (x < w / 2.0) right = false;
            else left = false;
        }
        if (top && bottom) {
            if (y < h / 2.0) bottom = false;
            else top = false;
        }

        if (corner > 0) {
            double armX = Math.min(corner, w / 3.0);
            double armY = Math.min(corner, h / 3.0);
            boolean nearLeft = x <= armX;
            boolean nearRight = x >= w - armX;
            boolean nearTop = y <= armY;
            boolean nearBottom = y >= h - armY;

            if ((top && left) || (top && nearLeft) || (left && nearTop)) return Zone.TOP_LEFT;
            if ((top && right) || (top && nearRight) || (right && nearTop)) return Zone.TOP_RIGHT;
            if ((bottom && left) || (bottom && nearLeft) || (left && nearBottom)) return Zone.BOTTOM_LEFT;
            if ((bottom && right) || (bottom && nearRight) || (right && nearBottom)) return Zone.BOTTOM_RIGHT;
        }

        if (top) return Zone.TOP_CENTER;
        if (bottom) return Zone.BOTTOM_CENTER;
        if (left) return Zone.CENTER_LEFT;
        if (right) return Zone.CENTER_RIGHT;
        return Zone.NONE; // should be unreachable
    }
}
