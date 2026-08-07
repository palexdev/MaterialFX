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

package io.github.palexdev.mfxeffects.enums;

import javafx.scene.Node;

/// Enumeration for ripple generators to allow users to specify the behavior for mouse events.
public enum MouseMode {

    /// The generator will detect all mouse events.
    OFF,

    /// The generator will ignore mouse events on the "bounds area" and only consider the geometric shape of the node,
    /// better explained here [Node#pickOnBoundsProperty()].
    DONT_PICK_ON_BOUNDS,

    /// The generator will ignore all mouse events.
    MOUSE_TRANSPARENT
}
