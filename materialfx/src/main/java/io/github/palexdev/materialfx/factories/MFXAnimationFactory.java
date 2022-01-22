/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.factories;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Convenience factory for various animations applied to {@code Nodes}.
 *
 * @see Timeline
 */
public enum MFXAnimationFactory {
	FADE_IN {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.opacityProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.opacityProperty(), 1.0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	FADE_OUT {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.opacityProperty(), 1.0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.opacityProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_IN_BOTTOM {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateYProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_OUT_BOTTOM {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateYProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_IN_LEFT {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateXProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_OUT_LEFT {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateXProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateXProperty(), -node.getBoundsInParent().getWidth() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_IN_RIGHT {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateXProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_OUT_RIGHT {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateXProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateXProperty(), node.getBoundsInParent().getWidth() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_IN_TOP {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateYProperty(), node.getBoundsInParent().getHeight() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateYProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	},
	SLIDE_OUT_TOP {
		@Override
		public Timeline build(Node node, double durationMillis) {
			MFXAnimationFactory.resetNode(node);
			KeyValue keyValue1 = new KeyValue(node.translateYProperty(), 0, INTERPOLATOR_V1);
			KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);

			KeyValue keyValue2 = new KeyValue(node.translateYProperty(), -node.getBoundsInParent().getHeight() * 2, INTERPOLATOR_V1);
			KeyFrame keyFrame2 = new KeyFrame(Duration.millis(durationMillis), keyValue2);

			return new Timeline(keyFrame1, keyFrame2);
		}
	};

	public static final Interpolator INTERPOLATOR_V1 = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
	public static final Interpolator INTERPOLATOR_V2 = Interpolator.SPLINE(0.0825D, 0.3025D, 0.0875D, 0.9975D);

	private static void resetNode(Node node) {
		if (node != null) {
			node.setTranslateX(0);
			node.setTranslateY(0);
		}
	}

	public abstract Timeline build(Node node, double durationMillis);
}
