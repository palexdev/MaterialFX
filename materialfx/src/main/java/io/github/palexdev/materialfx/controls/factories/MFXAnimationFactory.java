package io.github.palexdev.materialfx.controls.factories;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Convenience factory for various animations applied to {@code Node}s.
 *
 * @see Timeline
 */
public enum MFXAnimationFactory {
    FADE_IN {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.opacityProperty(), 0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.opacityProperty(), 1.0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    FADE_OUT {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.opacityProperty(), 1.0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.opacityProperty(), 0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_IN_BOTTOM {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight() * 2, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateYProperty(), 0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_OUT_BOTTOM {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateYProperty(), 0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight() * 2, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_IN_LEFT {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth() * 2, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateXProperty(), 0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_OUT_LEFT {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateXProperty(), 0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth() * 2, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_IN_RIGHT {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateXProperty(), 0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_OUT_RIGHT {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateXProperty(), 0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_IN_TOP {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight() * 2, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateYProperty(), 0, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    },
    SLIDE_OUT_TOP {
        @Override
        public Timeline build(Node node, double durationMillis) {
            MFXAnimationFactory.resetNode(node);
            KeyValue keyValue1 = new KeyValue(node.translateYProperty(), 0, interpolator);
            KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

            KeyValue keyValue2 = new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight() * 2, interpolator);
            KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

            return new Timeline(keyFrame1, keyFrame2);
        }
    };

    private static final Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    public static Interpolator getInterpolator() {
        return interpolator;
    }

    private static void resetNode(Node node) {
        node.setTranslateX(0);
        node.setTranslateY(0);
    }
    public abstract Timeline build(Node node, double durationMillis);
}
