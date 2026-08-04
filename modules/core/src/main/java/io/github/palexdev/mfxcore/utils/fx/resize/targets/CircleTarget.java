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
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/// [ResizeTarget] for a [Circle]: writes a centre and a radius, in the shape's own frame.
///
/// A circle has one degree of freedom where the [Resizer] offers two, so the box it computes has to be collapsed back
/// to a single radius. An edge takes the axis it moved; a corner takes whichever axis moved more. The center then
/// shifts so that the side you grabbed is the one that moves and the opposite side stays put, which is what makes the
/// gesture feel like resizing a box even though it isn't one.
///
/// Hit-testing is radial rather than rectangular, so the corners of the bounding box are dead, and the band follows the
/// outline. `corner` is read as an arc length there and is capped at 30° per corner, leaving the edges a third of
/// their nominal 45°.
///
/// Unlike the other targets, this one is stateful because collapsing needs the zone and the press-time bounds. That
/// makes an instance single-use: give each [Resizer] its own.
public class CircleTarget implements ResizeTarget<Circle> {

    //================================================================================
    // Properties
    //================================================================================

    private final Circle target;
    private final double minRadius;

    // latched by detectZone/bounds, both of which run before apply
    private Zone zone = Zone.NONE;
    private Bounds snapshot;

    //================================================================================
    // Constructors
    //================================================================================

    public CircleTarget(Circle target) {
        this(target, 0.0);
    }

    /// @param minRadius the smallest radius the circle may be dragged to. A [Circle] has no max, so there is none here
    public CircleTarget(Circle target, double minRadius) {
        this.target = target;
        this.minRadius = minRadius;
    }

    //================================================================================
    // Methods
    //================================================================================

    private double collapse(Bounds ref, double w, double h) {
        // edges take the side they moved: a max can't shrink, a min can't grow
        if (zone.isCorner())
            return Math.abs(w - ref.getWidth()) >= Math.abs(h - ref.getHeight()) ? w : h;
        return (zone.isLeft() || zone.isRight()) ? w : h;
    }

    /// The radial equivalent of [ResizeBand#detect(double, double, double, double)], where `dx`/`dy` are the pointer
    /// relative to the centre.
    // split out of detectZone so it can be tested without a MouseEvent
    static Zone detect(ResizeBand band, double dx, double dy, double radius) {
        double dist = Math.hypot(dx, dy);
        if (dist < radius - band.inner() || dist > radius + band.outer()) return Zone.NONE;

        // +y points down, so this is already screen-oriented
        double angle = Math.atan2(dy, dx);
        if (angle < 0) angle += 2 * Math.PI;

        // corner is an arc length here, so corner == 0 still means no corners.
        // the half-arc is capped at 30° to leave the edges a third of their nominal 45°
        if (band.corner() > 0 && radius > 0) {
            double half = Math.min(band.corner() / radius, Math.PI / 6);
            int quadrant = (int) Math.floor(angle / (Math.PI / 2));
            double diagonal = quadrant * (Math.PI / 2) + Math.PI / 4;
            if (Math.abs(angle - diagonal) <= half) {
                return switch (quadrant) {
                    case 0 -> Zone.BOTTOM_RIGHT;
                    case 1 -> Zone.BOTTOM_LEFT;
                    case 2 -> Zone.TOP_LEFT;
                    default -> Zone.TOP_RIGHT;
                };
            }
        }

        int sector = (int) Math.floor((angle + Math.PI / 4) / (Math.PI / 2)) % 4;
        return switch (sector) {
            case 0 -> Zone.CENTER_RIGHT;
            case 1 -> Zone.BOTTOM_CENTER;
            case 2 -> Zone.CENTER_LEFT;
            default -> Zone.TOP_CENTER;
        };
    }

    public double minRadius() {
        return minRadius;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Node hitNode() {
        return target;
    }

    @Override
    public Bounds bounds() {
        double r = target.getRadius();
        return snapshot = new BoundingBox(
            target.getCenterX() - r, target.getCenterY() - r,
            r * 2, r * 2
        );
    }

    @Override
    public void apply(double x, double y, double w, double h) {
        // the given position was derived from the un-collapsed sides, so re-pin off the snapshot instead.
        // no gesture in flight (a direct call) means the given box is all there is
        Bounds ref = snapshot != null ? snapshot : new BoundingBox(x, y, w, h);
        double r = collapse(ref, w, h) / 2.0;
        target.setCenterX(zone.isLeft() ? ref.getMaxX() - r : ref.getMinX() + r);
        target.setCenterY(zone.isTop() ? ref.getMaxY() - r : ref.getMinY() + r);
        target.setRadius(r);
    }

    @Override
    public Zone detectZone(MouseEvent me, ResizeBand band) {
        Point2D pointer = target.sceneToLocal(me.getSceneX(), me.getSceneY());
        return zone = detect(
            band,
            pointer.getX() - target.getCenterX(),
            pointer.getY() - target.getCenterY(),
            target.getRadius()
        );
    }

    @Override
    public double minW() {
        return minRadius * 2;
    }

    @Override
    public double minH() {
        return minRadius * 2;
    }

    @Override
    public double maxW() {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxH() {
        return Double.MAX_VALUE;
    }

    @Override
    public Circle target() {
        return target;
    }
}
