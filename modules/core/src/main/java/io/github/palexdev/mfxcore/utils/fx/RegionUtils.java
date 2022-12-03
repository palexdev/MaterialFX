/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public class RegionUtils {

	private RegionUtils() {
	}

	/**
	 * Makes the given region circular.
	 * <p>
	 * <b>Notice: the region's pref width and height must be set and be equals</b>
	 *
	 * @param region The given region
	 */
	public static void makeRegionCircular(Region region) {
		Circle circle = new Circle();
		circle.radiusProperty().bind(region.widthProperty().divide(2.0));
		circle.centerXProperty().bind(region.widthProperty().divide(2.0));
		circle.centerYProperty().bind(region.heightProperty().divide(2.0));
		try {
			region.setClip(circle);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
		}
	}

	/**
	 * Makes the given region circular with the specified radius.
	 * <p>
	 * <b>Notice: the region's pref width and height must be set and be equals</b>
	 *
	 * @param region The given region
	 * @param radius The wanted radius
	 */
	public static void makeRegionCircular(Region region, double radius) {
		Circle circle = new Circle(radius);
		circle.centerXProperty().bind(region.widthProperty().divide(2.0));
		circle.centerYProperty().bind(region.heightProperty().divide(2.0));
		try {
			region.setClip(circle);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
		}
	}

	/**
	 * Retrieves the region height if it isn't still laid out.
	 *
	 * @param region the Region of which to know the height
	 * @return the calculated height
	 */
	public static double getRegionHeight(Region region) {
		Group group = new Group(region);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		group.getChildren().clear();
		return region.getHeight();
	}

	/**
	 * Retrieves the region width if it isn't still laid out.
	 *
	 * @param region the Region of which to know the width
	 * @return the calculated width
	 */
	public static double getRegionWidth(Region region) {
		Group group = new Group(region);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		group.getChildren().clear();
		return region.getWidth();
	}

	/**
	 * Convenience method for adding the desired value to the region's prefWidth
	 */
	public static void addPrefWidth(Region region, double value) {
		double prefW = region.getPrefWidth();
		region.setPrefWidth(prefW + value);
	}

	/**
	 * Convenience method for adding the desired value to the region's prefHeight
	 */
	public static void addPrefHeight(Region region, double value) {
		double prefH = region.getPrefHeight();
		region.setPrefHeight(prefH + value);
	}
}
