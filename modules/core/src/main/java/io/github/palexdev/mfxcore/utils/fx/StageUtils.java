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

import io.github.palexdev.mfxcore.utils.fx.resize.Resizer;
import io.github.palexdev.mfxcore.utils.fx.resize.targets.StageTarget;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/// This class contains utilities to be used on [Windows][Window].
public class StageUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private StageUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// Makes the given [Window] draggable by the given node.
    ///
    /// Ideally, you may want to use this on windows without the native header. In such cases, it's common to have a
    /// region at the top of the custom window that replaces the native header. Such a region can indeed be used as the
    /// window's dragging point.
    public static WindowMover makeDraggable(Window window, Node anchor) {
        WindowMover mover = new WindowMover();
        mover.install(window, anchor);
        return mover;
    }


    /// Makes the given [Stage] resizable.
    ///
    /// Expects the stage to be either [StageStyle#TRANSPARENT] or [StageStyle#UNDECORATED].
    ///
    /// @see Resizer
    /// @see StageTarget
    public static Resizer<Stage> makeResizable(Stage stage) {
        if (stage.getStyle() != StageStyle.TRANSPARENT && stage.getStyle() != StageStyle.UNDECORATED)
            throw new IllegalArgumentException("Expected transparent or undecorated stage");
        return Resizer.resizer(stage).install();
    }
}
