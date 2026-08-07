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

import java.util.Map;
import java.util.Optional;

import io.github.palexdev.mfxcore.base.beans.Position;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

import static io.github.palexdev.mfxcore.base.beans.Position.position;
import static io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Direction.*;

/// This class provides a series of utilities for computing a position relative to a certain anchor, which is represented
/// by a [Pos].
///
/// These are the requirements for the computation:
/// - The bounds of an 'owner' node, also called 'reference bounds'
/// - The bounds of another node which has to be positioned relative to the 'owner', also called 'subject bounds'
/// - The anchor to get the point relative to which compute the position (see example below), represented as a [Pos]
/// - The vertical and horizontal directions relative to the anchor, see [Direction]
///
/// _Example 1:_
/// ```java
/// Button btn = ...;
/// Popup p = ...;
/// // I want the popup to be adjacent to the button, at its right
/// // The anchor is the top right corner of the button
/// Pos anchor = Pos.TOP_RIGHT;
/// // Since I want it to be next to the button, the directions are:
/// Direction hDir = Direction.AFTER;
/// Direction vDir = Direction.AFTER;
/// ```
///
/// _Example 2:_
/// ```java
/// // Now let's suppose I want to show the menu of a combo box
/// ComboBox combo = ...;
/// Popup p = ...;
/// // Typically, combo boxes' menus are shown below the component and facing inwards:
/// // ┌───────────┐
/// // │   Combo   │
/// // └───────────┘
/// //   ┌─ Popup ─┐
/// //   │         │
/// //   │ ◄────── │
/// //   │ Inwards │
/// //   │         │
/// //   └─────────┘
/// // The anchor is the bottom right corner of the component
/// Pos anchor = Pos.BOTTOM_RIGHT;
/// // The popup is below the anchor and to the left (inwards), so the directions are:
/// Direction hDir = Direction.BEFORE;
/// Direction vDir = Direction.AFTER;
/// ```
/// It's a bit tricky to understand, but super flexible. Note that, although the examples show how to position a popup
/// relative to a certain owner, the utilities are generic enough to be used with anything.<br >
/// Also, there are several presets available in the [Placement] class that cover most use cases.
///
/// The public API to compute a position given those requirements is specified by the [AnchorHandler] interface.
/// The class offers pre-made handlers for each anchor in [Pos]. You can retrieve the related handler with [#handler(Pos)]
/// or use the static methods directly. (Baseline positions are not covered!!)
///
/// The core methods responsible for the x and y computations are [#computeX(Bounds, Bounds, HPos, Direction)] and
/// [#computeY(Bounds, Bounds, VPos, Direction)].
public class AnchorHandlers {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final Map<Pos, AnchorHandler> HANDLERS = Map.of(
        Pos.TOP_LEFT, AnchorHandlers::topLeft,
        Pos.TOP_CENTER, AnchorHandlers::topCenter,
        Pos.TOP_RIGHT, AnchorHandlers::topRight,
        Pos.CENTER_LEFT, AnchorHandlers::centerLeft,
        Pos.CENTER, AnchorHandlers::center,
        Pos.CENTER_RIGHT, AnchorHandlers::centerRight,
        Pos.BOTTOM_LEFT, AnchorHandlers::bottomLeft,
        Pos.BOTTOM_CENTER, AnchorHandlers::bottomCenter,
        Pos.BOTTOM_RIGHT, AnchorHandlers::bottomRight
    );

    //================================================================================
    // Constructors
    //================================================================================
    private AnchorHandlers() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// @return an [AnchorHandler] appropriate for the given [Pos], or defaults to [Pos#CENTER]
    public static AnchorHandler handler(Pos anchor) {
        return HANDLERS.getOrDefault(anchor, AnchorHandlers::center);
    }

    public static Position topLeft(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.LEFT, hDir),
            computeY(refBounds, subjectBounds, VPos.TOP, vDir)
        );
    }

    public static Position topCenter(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.CENTER, hDir),
            computeY(refBounds, subjectBounds, VPos.TOP, vDir)
        );
    }

    public static Position topRight(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.RIGHT, hDir),
            computeY(refBounds, subjectBounds, VPos.TOP, vDir)
        );
    }

    public static Position centerLeft(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.LEFT, hDir),
            computeY(refBounds, subjectBounds, VPos.CENTER, vDir)
        );
    }

    public static Position center(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.CENTER, hDir),
            computeY(refBounds, subjectBounds, VPos.CENTER, vDir)
        );
    }

    public static Position centerRight(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.RIGHT, hDir),
            computeY(refBounds, subjectBounds, VPos.CENTER, vDir)
        );
    }

    public static Position bottomLeft(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.LEFT, hDir),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, vDir)
        );
    }

    public static Position bottomCenter(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.CENTER, hDir),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, vDir)
        );
    }

    public static Position bottomRight(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir) {
        return position(
            computeX(refBounds, subjectBounds, HPos.RIGHT, hDir),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, vDir)
        );
    }

    /// Responsible for computing the x position given the: reference bounds, the node bounds, the horizontal anchor and
    /// the horizontal direction.
    ///
    /// First, it computes the anchor as follows:
    /// - `LEFT -> refBounds.minX`
    /// - `CENTER -> refBounds.centerX`
    /// - `RIGHT -> refBounds.maxX`
    ///
    /// Then computes the final position applying the given direction to the retrieved anchor position:
    /// - `BEFORE -> anchorX - subjectBounds.width`
    /// - `CENTER -> anchorX - subjectBounds.width / 2`
    /// - `AFTER -> anchorX`
    public static double computeX(Bounds refBounds, Bounds subjectBounds, HPos hAnchor, Direction hDir) {
        double anchorX = switch (hAnchor) {
            case LEFT -> refBounds.getMinX();
            case CENTER -> refBounds.getCenterX();
            case RIGHT -> refBounds.getMaxX();
        };

        return switch (hDir) {
            case BEFORE -> anchorX - subjectBounds.getWidth();
            case CENTER -> anchorX - subjectBounds.getWidth() / 2.0;
            case AFTER -> anchorX;
        };
    }

    /// Responsible for computing the y position given the: reference bounds, the node bounds, the vertical anchor and
    /// the vertical direction.
    ///
    /// First, it computes the anchor as follows:
    /// - `TOP -> refBounds.minY`
    /// - `CENTER -> refBounds.centerY`
    /// - `BOTTOM -> refBounds.maxY`
    ///
    /// Then computes the final position applying the given direction to the retrieved anchor position:
    /// - `ABOVE -> anchorY - subjectBounds.height`
    /// - `CENTER -> anchorY - subjectBounds.height / 2`
    /// - `BELOW -> anchorY`
    public static double computeY(Bounds refBounds, Bounds subjectBounds, VPos vAnchor, Direction vDir) {
        double anchorY = switch (vAnchor) {
            case TOP -> refBounds.getMinY();
            case CENTER -> refBounds.getCenterY();
            case BASELINE, BOTTOM -> refBounds.getMaxY();
        };

        return switch (vDir) {
            case BEFORE -> anchorY - subjectBounds.getHeight();
            case CENTER -> anchorY - subjectBounds.getHeight() / 2.0;
            case AFTER -> anchorY;
        };
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Represents a directional offset from an anchor point on a single axis.
    /// - `BEFORE`: extends in the negative direction (left for X, up for Y)
    /// - `CENTER`: centers on the anchor point
    /// - `AFTER`: extends in the positive direction (right for X, down for Y)
    public enum Direction {
        BEFORE, CENTER, AFTER
    }

    /// Defines how a subject (e.g., popup, tooltip) should be positioned relative to an anchor point on an owner's bounds.
    ///
    /// A placement consists of three components:
    /// - `anchor`: the reference point on the owner's bounds (from [Pos])
    /// - `xDirection`: how the subject extends horizontally from the anchor ([Direction])
    /// - `yDirection`: how the subject extends vertically from the anchor ([Direction])
    ///
    /// (See [AnchorHandlers] for some examples)
    public record Placement(Pos anchor, Direction xDirection, Direction yDirection) {
        public static Placement placement(Pos anchor, Direction xDirection, Direction yDirection) {
            return new Placement(anchor, xDirection, yDirection);
        }

        //================================================================================
        // Presets
        //================================================================================

        /// Center point inside the owner.
        public static final Placement IN_CENTER = placement(Pos.CENTER, Direction.CENTER, Direction.CENTER);

        public static class Inside {
            /// Subject extends right and down from the top-left corner (inward).
            public static final Placement TOP_LEFT = placement(Pos.TOP_LEFT, AFTER, AFTER);

            /// Subject is horizontally centered and extends down from the top edge (inward).
            public static final Placement TOP_CENTER = placement(Pos.TOP_CENTER, CENTER, AFTER);

            /// Subject extends left and down from the top-right corner (inward).
            public static final Placement TOP_RIGHT = placement(Pos.TOP_RIGHT, BEFORE, AFTER);

            /// Subject extends right and is vertically centered from the left edge (inward).
            public static final Placement CENTER_LEFT = placement(Pos.CENTER_LEFT, AFTER, CENTER);

            /// Subject extends left and is vertically centered from the right edge (inward).
            public static final Placement CENTER_RIGHT = placement(Pos.CENTER_RIGHT, BEFORE, CENTER);

            /// Subject extends right and up from the bottom-left corner (inward).
            public static final Placement BOTTOM_LEFT = placement(Pos.BOTTOM_LEFT, AFTER, BEFORE);

            /// Subject is horizontally centered and extends up from the bottom edge (inward).
            public static final Placement BOTTOM_CENTER = placement(Pos.BOTTOM_CENTER, CENTER, BEFORE);

            /// Subject extends left and up from the bottom-right corner (inward).
            public static final Placement BOTTOM_RIGHT = placement(Pos.BOTTOM_RIGHT, BEFORE, BEFORE);
        }

        public static class Outside {
            /// Subject extends left and up from the top-left corner (outward).
            public static final Placement TOP_LEFT = placement(Pos.TOP_LEFT, BEFORE, BEFORE);

            /// Subject is horizontally centered and extends up from the top edge (outward).
            public static final Placement TOP_CENTER = placement(Pos.TOP_CENTER, CENTER, BEFORE);

            /// Subject extends right and up from the top-right corner (outward).
            public static final Placement TOP_RIGHT = placement(Pos.TOP_RIGHT, AFTER, BEFORE);

            /// Subject extends left and is vertically centered from the left edge (outward).
            public static final Placement CENTER_LEFT = placement(Pos.CENTER_LEFT, BEFORE, CENTER);

            /// Subject extends right and is vertically centered from the right edge (outward).
            public static final Placement CENTER_RIGHT = placement(Pos.CENTER_RIGHT, AFTER, CENTER);

            /// Subject extends left and down from the bottom-left corner (outward).
            public static final Placement BOTTOM_LEFT = placement(Pos.BOTTOM_LEFT, BEFORE, AFTER);

            /// Subject is horizontally centered and extends down from the bottom edge (outward).
            public static final Placement BOTTOM_CENTER = placement(Pos.BOTTOM_CENTER, CENTER, AFTER);

            /// Subject extends right and down from the bottom-right corner (outward).
            public static final Placement BOTTOM_RIGHT = placement(Pos.BOTTOM_RIGHT, AFTER, AFTER);
        }
    }

    /// API to compute the position of something relative to the bounds of something else
    /// (the bounds could be of a node, a window or anything else).
    @FunctionalInterface
    public interface AnchorHandler {
        Position compute(Bounds refBounds, Bounds subjectBounds, Direction hDir, Direction vDir);

        /// Delegates to [#compute(Bounds, Bounds, Direction, Direction)] after modifying the given bounds to take into account the
        /// given offset.
        default Position compute(Bounds refBounds, Node node, Direction hDir, Direction vDir, Position offset) {
            if (!Position.origin().equals(offset)) {
                refBounds = new BoundingBox(
                    refBounds.getMinX() + offset.x(),
                    refBounds.getMinY() + offset.y(),
                    refBounds.getWidth(),
                    refBounds.getHeight()
                );
            }
            return compute(refBounds, node.getLayoutBounds(), hDir, vDir);
        }

        /// Delegates to [#compute(Bounds, Node, Direction, Direction, Position)] after building a [BoundingBox] with the position
        /// and size of the given owner [Window].
        ///
        /// This method prioritizes using the scene root's bounds converted to screen coordinates to get the actual
        /// content area of the window (excluding decorations like title bar and borders).
        /// If the scene or root is unavailable, it falls back to the raw window bounds.
        default Position compute(Window owner, Node content, Direction hDir, Direction vDir, Position offset) {
            Bounds owBounds = Optional.ofNullable(owner.getScene())
                .map(Scene::getRoot)
                .map(n -> n.localToScreen(n.getLayoutBounds()))
                .orElseGet(() ->
                    new BoundingBox(
                        owner.getX(), owner.getY(),
                        owner.getWidth(), owner.getHeight()
                    )
                );
            return compute(owBounds, content, hDir, vDir, offset);
        }

        /// Delegates to [#compute(Bounds, Node, Direction, Direction, Position)] after converting the owner's bounds to screen coordinates.
        default Position compute(Node owner, Node content, Direction hDir, Direction vDir, Position offset) {
            Bounds onBounds = owner.localToScreen(owner.getLayoutBounds());
            return compute(onBounds, content, hDir, vDir, offset);
        }
    }
}
