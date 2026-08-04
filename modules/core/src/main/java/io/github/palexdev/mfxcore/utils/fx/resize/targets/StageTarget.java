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
 * See the GNU Lesser General Public License for more detailtarget.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx.resize.targets;

import io.github.palexdev.mfxcore.utils.fx.resize.Resizer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/// [ResizeTarget] for a [Stage]: bounds, pointer and writes are all in *screen* coordinates.
///
/// The hit node is the scene's root, so a plain [Resizer#resizer(Stage)] grabs the window's edges from anywhere inside it.
public record StageTarget(Stage target) implements ResizeTarget<Stage> {

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Node hitNode() {
        return root();
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
        return target.getMinWidth();
    }

    @Override
    public double minH() {
        return target.getMinHeight();
    }

    @Override
    public double maxW() {
        return target.getMaxWidth();
    }

    @Override
    public double maxH() {
        return target.getMaxHeight();
    }

    @Override
    public boolean isResizable() {
        return target.isResizable();
    }

    @Override
    public Point2D pointer(MouseEvent me) {
        return new Point2D(me.getScreenX(), me.getScreenY());
    }

    //================================================================================
    // Methods
    //================================================================================

    /// @throws IllegalStateException if the stage has no scene, or the scene no root
    private Node root() {
        Scene scene;
        if ((scene = target.getScene()) == null) throw new IllegalStateException("Stage has no scene");
        Node root;
        if ((root = scene.getRoot()) == null) throw new IllegalStateException("Scene has no root");
        return root;
    }
}
