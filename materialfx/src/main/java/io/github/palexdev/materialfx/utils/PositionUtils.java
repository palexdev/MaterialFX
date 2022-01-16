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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Utilities for JavaFX's {@link Pos} and {@link NotificationPos}.
 */
public class PositionUtils {

	private PositionUtils() {
	}

	//================================================================================
	// POS
	//================================================================================
	public static boolean isTop(Pos pos) {
		return pos == Pos.TOP_LEFT || pos == Pos.TOP_CENTER || pos == Pos.TOP_RIGHT;
	}

	public static boolean isCenter(Pos pos) {
		return pos == Pos.CENTER_LEFT || pos == Pos.CENTER || pos == Pos.CENTER_RIGHT || pos == Pos.TOP_CENTER || pos == Pos.BOTTOM_CENTER;
	}

	public static boolean isBottom(Pos pos) {
		return pos == Pos.BOTTOM_LEFT || pos == Pos.BOTTOM_CENTER || pos == Pos.BOTTOM_RIGHT;
	}

	public static boolean isLeft(Pos pos) {
		return pos == Pos.TOP_LEFT || pos == Pos.CENTER_LEFT || pos == Pos.BOTTOM_LEFT;
	}

	public static boolean isRight(Pos pos) {
		return pos == Pos.TOP_RIGHT || pos == Pos.CENTER_RIGHT || pos == Pos.BOTTOM_RIGHT;
	}

	//================================================================================
	// NotificationPos
	//================================================================================
	public static boolean isTop(NotificationPos pos) {
		return pos == NotificationPos.TOP_LEFT || pos == NotificationPos.TOP_CENTER || pos == NotificationPos.TOP_RIGHT;
	}

	public static boolean isCenter(NotificationPos pos) {
		return pos == NotificationPos.TOP_CENTER || pos == NotificationPos.BOTTOM_CENTER;
	}

	public static boolean isRight(NotificationPos pos) {
		return pos == NotificationPos.TOP_RIGHT || pos == NotificationPos.BOTTOM_RIGHT;
	}

	//================================================================================
	// Position Computing for Regions
	//================================================================================
	public static PositionBean computePosition(Region parent, Node child, double areaX, double areaY, double areaWidth, double areaHeight,
	                                           double areaBaselineOffset, Insets margin, HPos hAlignment, VPos vAlignment) {
		return computePosition(parent, child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset, margin, hAlignment, vAlignment, true);
	}

	public static PositionBean computePosition(Region parent, Node child, double areaX, double areaY, double areaWidth, double areaHeight,
	                                           double areaBaselineOffset, Insets margin, HPos hAlignment, VPos vAlignment, boolean snapToPixel) {

		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapToPixel) {
			snappedMargin = InsetsFactory.of(
					parent.snapSpaceY(snappedMargin.getTop()),
					parent.snapSpaceX(snappedMargin.getRight()),
					parent.snapSpaceY(snappedMargin.getBottom()),
					parent.snapSpaceX(snappedMargin.getLeft())
			);
		}

		double xPosition = computeXPosition(parent, child, areaX, areaWidth, snappedMargin, false, hAlignment, snapToPixel);
		double yPosition = computeYPosition(parent, child, areaY, areaHeight, areaBaselineOffset, snappedMargin, false, vAlignment, snapToPixel);
		return PositionBean.of(xPosition, yPosition);
	}

	public static double computeXPosition(Region parent, Node child, double areaX, double areaWidth, Insets margin, boolean snapMargin, HPos hAlignment, boolean snapToPixel) {
		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapMargin) {
			snappedMargin = InsetsFactory.of(
					parent.snapSpaceY(snappedMargin.getTop()),
					parent.snapSpaceX(snappedMargin.getRight()),
					parent.snapSpaceY(snappedMargin.getBottom()),
					parent.snapSpaceX(snappedMargin.getLeft())
			);
		}

		final double leftMargin = snappedMargin.getLeft();
		final double rightMargin = snappedMargin.getRight();
		final double xOffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin, child.getLayoutBounds().getWidth(), hAlignment);
		final double xPosition = areaX + xOffset;
		return snapToPixel ? parent.snapPositionX(xPosition) : xPosition;
	}

	public static double computeYPosition(Region parent, Node child, double areaY, double areaHeight, double areaBaselineOffset, Insets margin, boolean snapMargin, VPos vAlignment, boolean snapToPixel) {
		Insets snappedMargin = margin == null ? Insets.EMPTY : margin;
		if (snapMargin) {
			snappedMargin = InsetsFactory.of(
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
				// We already know the layout bounds at this stage, so we can use them
				yOffset = areaBaselineOffset - child.getLayoutBounds().getHeight();
			} else {
				yOffset = areaBaselineOffset - bo;
			}
		} else {
			yOffset = topMargin + computeYOffset(areaHeight - topMargin - bottomMargin, child.getLayoutBounds().getHeight(), vAlignment);
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
}
