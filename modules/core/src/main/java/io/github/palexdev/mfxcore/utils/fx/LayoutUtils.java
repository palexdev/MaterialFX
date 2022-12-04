/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class LayoutUtils {

	private LayoutUtils() {
	}

	public static Position computePosition(Region parent, Node child, double areaX, double areaY, double areaWidth, double areaHeight,
	                                       double areaBaselineOffset, Insets margin, HPos hAlignment, VPos vAlignment) {
		return computePosition(parent, child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, hAlignment, vAlignment, true, true);
	}

	public static Position computePosition(Region parent, Node child, double areaX, double areaY, double areaWidth, double areaHeight,
	                                       double areaBaselineOffset, Insets margin, HPos hAlignment, VPos vAlignment, boolean snapToPixel, boolean computeSizes) {

		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapToPixel) {
			snappedMargin = InsetsBuilder.of(
					parent.snapSpaceY(snappedMargin.getTop()),
					parent.snapSpaceX(snappedMargin.getRight()),
					parent.snapSpaceY(snappedMargin.getBottom()),
					parent.snapSpaceX(snappedMargin.getLeft())
			);
		}

		double xPosition = computeXPosition(parent, child, areaX, areaWidth, snappedMargin, false, hAlignment, snapToPixel, computeSizes);
		double yPosition = computeYPosition(parent, child, areaY, areaHeight, areaBaselineOffset, snappedMargin, false, vAlignment, snapToPixel, computeSizes);
		return Position.of(xPosition, yPosition);
	}

	public static double computeXPosition(Region parent, Node child, double areaX, double areaWidth, Insets margin, boolean snapMargin, HPos hAlignment, boolean snapToPixel, boolean computeSizes) {
		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapMargin) {
			snappedMargin = InsetsBuilder.of(
					parent.snapSpaceY(snappedMargin.getTop()),
					parent.snapSpaceX(snappedMargin.getRight()),
					parent.snapSpaceY(snappedMargin.getBottom()),
					parent.snapSpaceX(snappedMargin.getLeft())
			);
		}

		final double leftMargin = snappedMargin.getLeft();
		final double rightMargin = snappedMargin.getRight();
		final double xOffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin, computeSizes ? boundWidth(child) : child.getLayoutBounds().getWidth(), hAlignment);
		final double xPosition = areaX + xOffset;
		return snapToPixel ? parent.snapPositionX(xPosition) : xPosition;
	}

	public static double computeYPosition(Region parent, Node child, double areaY, double areaHeight, double areaBaselineOffset, Insets margin, boolean snapMargin, VPos vAlignment, boolean snapToPixel, boolean computeSizes) {
		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapMargin) {
			snappedMargin = InsetsBuilder.of(
					parent.snapSpaceY(snappedMargin.getTop()),
					parent.snapSpaceX(snappedMargin.getRight()),
					parent.snapSpaceY(snappedMargin.getBottom()),
					parent.snapSpaceX(snappedMargin.getLeft())
			);
		}

		final double topMargin = snappedMargin.getTop();
		final double bottomMargin = snappedMargin.getBottom();
		final double yOffset;
		if (vAlignment == VPos.BASELINE) {
			double bo = child.getBaselineOffset();
			if (bo == Node.BASELINE_OFFSET_SAME_AS_HEIGHT) {
				yOffset = areaBaselineOffset - (computeSizes ? boundHeight(child) : child.getLayoutBounds().getHeight());
			} else {
				yOffset = areaBaselineOffset - bo;
			}
		} else {
			yOffset = topMargin + computeYOffset(areaHeight - topMargin - bottomMargin, computeSizes ? boundHeight(child) : child.getLayoutBounds().getHeight(), vAlignment);
		}
		final double yPosition = areaY + yOffset;
		return snapToPixel ? parent.snapPositionY(yPosition) : yPosition;
	}

	private static double computeXOffset(double areaWidth, double contentWidth, HPos hAlignment) {
		switch (hAlignment) {
			case LEFT:
				return 0;
			case CENTER:
				return (areaWidth - contentWidth) / 2;
			case RIGHT:
				return areaWidth - contentWidth;
			default:
				throw new AssertionError("Unhandled hPos");
		}
	}

	private static double computeYOffset(double areaHeight, double contentHeight, VPos vAlignment) {
		switch (vAlignment) {
			case BASELINE:
			case TOP:
				return 0;
			case CENTER:
				return (areaHeight - contentHeight) / 2;
			case BOTTOM:
				return areaHeight - contentHeight;
			default:
				throw new AssertionError("Unhandled vPos");
		}
	}

	public static void resize(Region parent, Node node, double w, double h) {
		node.resize(parent.snapSizeX(w), parent.snapSizeY(h));
	}

	public static void relocate(Region parent, Node node, double x, double y) {
		node.relocate(parent.snapPositionX(x), parent.snapPositionY(y));
	}

	public static void resizeRelocate(Region parent, Node node, double x, double y, double w, double h) {
		node.resizeRelocate(parent.snapPositionX(x), parent.snapPositionY(y), parent.snapSizeX(w), parent.snapSizeY(h));
	}

	public static double boundedSize(double min, double pref, double max) {
		double a = Math.max(pref, min);
		double b = Math.max(min, max);
		return Math.min(a, b);
	}

	public static double boundWidth(Node node) {
		return boundedSize(node.minWidth(-1), node.prefWidth(-1), node.maxWidth(-1));
	}

	public static double boundHeight(Node node) {
		return boundedSize(node.minHeight(-1), node.prefHeight(-1), node.maxHeight(-1));
	}
}
