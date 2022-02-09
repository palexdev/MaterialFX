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

package io.github.palexdev.materialfx.builders.layout;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

public class RegionBuilder<R extends Region> extends ParentBuilder<R> {

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("unchecked")
	public RegionBuilder() {
		this((R) new Region());
	}
	
	public RegionBuilder(R region) {
		super(region);
	}

	public static RegionBuilder<Region> region() {
		return new RegionBuilder<>();
	}
	
	public static RegionBuilder<Region> region(Region region) {
		return new RegionBuilder<>(region);
	}
	
	//================================================================================
	// Delegate Methods
	//================================================================================
	public RegionBuilder<R> setSnapToPixel(boolean snapToPixel) {
		node.setSnapToPixel(snapToPixel);
		return this;
	}

	public RegionBuilder<R> setPadding(Insets padding) {
		node.setPadding(padding);
		return this;
	}

	public RegionBuilder<R> setBackground(Background background) {
		node.setBackground(background);
		return this;
	}

	public RegionBuilder<R> setBorder(Border border) {
		node.setBorder(border);
		return this;
	}

	public RegionBuilder<R> setMinWidth(double minWidth) {
		node.setMinWidth(minWidth);
		return this;
	}

	public RegionBuilder<R> setMinHeight(double minHeight) {
		node.setMinHeight(minHeight);
		return this;
	}

	public RegionBuilder<R> setMinSize(double minWidth, double minHeight) {
		node.setMinSize(minWidth, minHeight);
		return this;
	}

	public RegionBuilder<R> setPrefWidth(double prefWidth) {
		node.setPrefWidth(prefWidth);
		return this;
	}

	public RegionBuilder<R> setPrefHeight(double prefHeight) {
		node.setPrefHeight(prefHeight);
		return this;
	}

	public RegionBuilder<R> setPrefSize(double prefWidth, double prefHeight) {
		node.setPrefSize(prefWidth, prefHeight);
		return this;
	}

	public RegionBuilder<R> setMaxWidth(double maxWidth) {
		node.setMaxWidth(maxWidth);
		return this;
	}

	public RegionBuilder<R> setMaxHeight(double maxHeight) {
		node.setMaxHeight(maxHeight);
		return this;
	}

	public RegionBuilder<R> setMaxSize(double maxWidth, double maxHeight) {
		node.setMaxSize(maxWidth, maxHeight);
		return this;
	}

	public RegionBuilder<R> setShape(Shape value) {
		node.setShape(value);
		return this;
	}

	public RegionBuilder<R> setScaleShape(boolean scaleShape) {
		node.setScaleShape(scaleShape);
		return this;
	}

	public RegionBuilder<R> setCenterShape(boolean centerShape) {
		node.setCenterShape(centerShape);
		return this;
	}

	public RegionBuilder<R> setCacheShape(boolean cacheShape) {
		node.setCacheShape(cacheShape);
		return this;
	}
}
