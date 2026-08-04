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

import io.github.palexdev.mfxcore.enums.Zone;
import io.github.palexdev.mfxcore.utils.fx.resize.ResizeBand;
import io.github.palexdev.mfxcore.utils.fx.resize.Resizer;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/// The strategy through which a [Resizer] reads and writes the geometry of whatever it is resizing.
///
/// Implementations are the only place that knows what `T` really is: a [Region] writes its layout position and pref
/// sizes, a [Stage] writes its screen bounds, a [Circle] writes a centre and a radius. The [Resizer] itself only ever
/// deals in a `<x, y, w, h>` box.
///
/// Two coordinate spaces are involved, and mixing them up is the quickest way to break a target:
/// - [#bounds()] and [#apply(double, double, double, double)] work in the target's own frame, whatever that is, and
/// must agree with each other
/// - [#detectZone(MouseEvent, ResizeBand)] hit-tests in [#hitNode()]'s local coordinates instead
///
/// [#pointer(MouseEvent)] only ever contributes deltas, so what it must match is the frame of [#bounds()].
///
/// The defaults cover any target laid out by a parent. Override [#pointer(MouseEvent)] when the target's frame is not
/// the hit node's parent (see [StageTarget]), and [#detectZone(MouseEvent, ResizeBand)] when the hit area is not a
/// rectangle (see [CircleTarget]).
public interface ResizeTarget<T> {

    /// @return the object being resized
    T target();

    /// @return the node to hit-test against, also the default hit source the [Resizer] registers its handlers on
    Node hitNode();

    /// @return the target's current geometry. Taken once, at press.
    Bounds bounds();

    /// Writes the new geometry back to the target. Called on every drag event, and by [Resizer#cancel()] with the
    /// original bounds.
    void apply(double x, double y, double w, double h);

    /// @return the smallest width the target may be resized to
    double minW();

    /// @return the smallest height the target may be resized to
    double minH();

    /// @return the largest width the target may be resized to
    double maxW();

    /// @return the largest height the target may be resized to
    double maxH();

    /// @return whether the target can currently be resized at all. When `false` no zone is ever detected, so there's
    /// no cursor either.
    default boolean isResizable() {
        return true;
    }

    /// @return the pointer in the same frame as [#bounds()]. Defaults to the hit node's parent, falling back to scene
    /// coordinates when there is no parent.
    default Point2D pointer(MouseEvent me) {
        Parent p = hitNode().getParent();
        double x = me.getSceneX();
        double y = me.getSceneY();
        return p != null ? p.sceneToLocal(x, y) : new Point2D(x, y);
    }

    /// @return which zone the pointer is over, or [Zone#NONE]. Defaults to running the band against [#hitNode()]'s
    /// layout bounds.
    default Zone detectZone(MouseEvent me, ResizeBand band) {
        Node node = hitNode();
        Point2D pointer = node.sceneToLocal(me.getSceneX(), me.getSceneY());
        Bounds b = node.getLayoutBounds();
        return band.detect(
            pointer.getX() - b.getMinX(),
            pointer.getY() - b.getMinY(),
            b.getWidth(), b.getHeight()
        );
    }
}
