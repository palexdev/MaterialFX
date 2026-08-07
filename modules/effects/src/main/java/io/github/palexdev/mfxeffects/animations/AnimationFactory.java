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

package io.github.palexdev.mfxeffects.animations;

import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

/// Convenience factory for various animations applied to `Nodes`.
///
/// @see #extraOffset
/// @see Timeline
public enum AnimationFactory {
    FADE_IN {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            return new KeyFrame[]{
                KeyFrames.of(0, node.opacityProperty(), 0.0),
                KeyFrames.of(millis, node.opacityProperty(), 1.0, i)
            };
        }
    },
    FADE_OUT {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            return new KeyFrame[]{
                KeyFrames.of(0, node.opacityProperty(), 1.0),
                KeyFrames.of(millis, node.opacityProperty(), 0.0, i)
            };
        }
    },
    SLIDE_IN_BOTTOM {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceBottom(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateYProperty(), distance),
                KeyFrames.of(millis, node.translateYProperty(), 0, i)
            };
        }
    },
    SLIDE_OUT_BOTTOM {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceBottom(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateYProperty(), 0),
                KeyFrames.of(millis, node.translateYProperty(), distance, i)
            };
        }
    },
    SLIDE_IN_LEFT {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceLeft(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateXProperty(), -distance),
                KeyFrames.of(millis, node.translateXProperty(), 0, i)
            };
        }
    },
    SLIDE_OUT_LEFT {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceLeft(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateXProperty(), 0),
                KeyFrames.of(millis, node.translateXProperty(), -distance, i)
            };
        }
    },
    SLIDE_IN_RIGHT {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceRight(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateXProperty(), distance),
                KeyFrames.of(millis, node.translateXProperty(), 0, i)
            };
        }
    },
    SLIDE_OUT_RIGHT {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceRight(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateXProperty(), 0),
                KeyFrames.of(millis, node.translateXProperty(), distance, i)
            };
        }
    },
    SLIDE_IN_TOP {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceTop(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateYProperty(), distance),
                KeyFrames.of(millis, node.translateYProperty(), 0, i)
            };
        }
    },
    SLIDE_OUT_TOP {
        @Override
        public KeyFrame[] keyFrames(Node node, double millis, Interpolator i) {
            double distance = computeDistanceTop(node);
            return new KeyFrame[]{
                KeyFrames.of(0, node.translateYProperty(), 0),
                KeyFrames.of(millis, node.translateYProperty(), -distance, i)
            };
        }
    };

    public static final Interpolator INTERPOLATOR_V1 = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    public static final Interpolator INTERPOLATOR_V2 = Interpolator.SPLINE(0.0825D, 0.3025D, 0.0875D, 0.9975D);

    /// This special variable is used in slide animations when the "travel distance" is computed.
    /// This extra offset is added to the computed value to ensure the node is outside the parent, for a smooth animation.
    @SuppressWarnings("NonFinalFieldInEnum")
    public static double extraOffset = 5.0;

    /// Computes the distance between the node and the left side of its parent by using its
    /// [Node#boundsInParentProperty()]. This distance ensures the node is going to be outside/inside the parent
    /// towards the animation's ending.
    ///
    /// @see #extraOffset
    public double computeDistanceLeft(Node node) {
        double w = node.getBoundsInParent().getWidth();
        return node.getBoundsInParent().getMinX() + w + extraOffset;
    }

    /// Computes the distance between the node and the right side of its parent. For this computation the
    /// [Node#parentProperty()] must not return a `null` value (the node must be a child of some other node).
    ///
    /// If the parent is `null,` a 'fallback' value is returned: the width of the node plus the extra offset.
    ///
    /// Otherwise, the value is computed like this:
    /// `parent.getLayoutBounds().getWidth() - node.getBoundsInParent().getMaxX() + node.getBoundsInParent().getWidth() + extraOffset`
    ///
    /// @see #extraOffset
    public double computeDistanceRight(Node node) {
        double w = node.getBoundsInParent().getWidth();
        Parent parent = node.getParent();
        if (parent == null) return w + extraOffset;
        return parent.getLayoutBounds().getWidth() - node.getBoundsInParent().getMaxX() + w + extraOffset;
    }

    /// Computes the distance between the node and the top side of its parent by using its
    /// [Node#boundsInParentProperty()]. This distance ensures the node is going to be outside/inside the parent
    /// towards the animation's ending.
    ///
    /// @see #extraOffset
    public double computeDistanceTop(Node node) {
        double h = node.getBoundsInParent().getHeight();
        return node.getBoundsInParent().getMinY() + h + extraOffset;
    }

    /// Computes the distance between the node and the bottom side of its parent. For this computation the
    /// [Node#parentProperty()] must not return a `null` value (the node must be a child of some other node).
    ///
    /// If the parent is `null`, a 'fallback' value is returned: the height of the node plus the extra offset.
    ///
    /// Otherwise, the value is computed like this:
    /// `parent.getLayoutBounds().getHeight() - node.getBoundsInParent().getMaxY() + node.getBoundsInParent().getHeight() + extraOffset`
    ///
    /// @see #extraOffset
    public double computeDistanceBottom(Node node) {
        double h = node.getBoundsInParent().getHeight();
        Parent parent = node.getParent();
        if (parent == null) return h + extraOffset;
        return parent.getLayoutBounds().getHeight() - node.getBoundsInParent().getMaxY() + h + extraOffset;
    }

    /// Calls [#build(Node, double, Interpolator)] with [#INTERPOLATOR_V1] as the default interpolator.
    public Timeline build(Node node, double millis) {
        return build(node, millis, INTERPOLATOR_V1);
    }

    /// Calls [#build(Node, double)] with the given duration converted to milliseconds.
    public Timeline build(Node node, Duration duration) {
        return build(node, duration.toMillis());
    }

    /// Calls [#build(Node, double, Interpolator)] with the given duration converted to milliseconds and the given
    /// interpolator.
    public Timeline build(Node node, Duration duration, Interpolator i) {
        return build(node, duration.toMillis(), i);
    }

    /// Each enum constant will produce a [Timeline] with the given parameters.
    ///
    /// @param node the [Node] on which perform the animation
    /// @param millis the duration of the animation in milliseconds
    /// @param i the [Interpolator] used by the animations
    public Timeline build(Node node, double millis, Interpolator i) {
        return TimelineBuilder.build().add(keyFrames(node, millis, i)).getAnimation();
    }

    /// Each enum constant should produce the [KeyFrames][KeyFrame] for the animation from the given parameters.
    public abstract KeyFrame[] keyFrames(Node node, double millis, Interpolator i);
}
