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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Paint;

public class ScrollPaneBuilder extends ControlBuilder<MFXScrollPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public ScrollPaneBuilder() {
		this(new MFXScrollPane());
	}

	public ScrollPaneBuilder(MFXScrollPane scrollPane) {
		super(scrollPane);
	}

	public static ScrollPaneBuilder scrollPane() {
		return new ScrollPaneBuilder();
	}

	public static ScrollPaneBuilder scrollPane(MFXScrollPane scrollPane) {
		return new ScrollPaneBuilder(scrollPane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public ScrollPaneBuilder setTrackColor(Paint trackColor) {
		node.setTrackColor(trackColor);
		return this;
	}

	public ScrollPaneBuilder setThumbColor(Paint thumbColor) {
		node.setThumbColor(thumbColor);
		return this;
	}

	public ScrollPaneBuilder setThumbHoverColor(Paint thumbHoverColor) {
		node.setThumbHoverColor(thumbHoverColor);
		return this;
	}

	public ScrollPaneBuilder setHBarPolicy(ScrollPane.ScrollBarPolicy hBarPolicy) {
		node.setHbarPolicy(hBarPolicy);
		return this;
	}

	public ScrollPaneBuilder setVBarPolicy(ScrollPane.ScrollBarPolicy vBarPolicy) {
		node.setVbarPolicy(vBarPolicy);
		return this;
	}

	public ScrollPaneBuilder setContent(Node content) {
		node.setContent(content);
		return this;
	}

	public ScrollPaneBuilder setHValue(double hValue) {
		node.setHvalue(hValue);
		return this;
	}

	public ScrollPaneBuilder setVValue(double vValue) {
		node.setVvalue(vValue);
		return this;
	}

	public ScrollPaneBuilder setHMin(double hMin) {
		node.setHmin(hMin);
		return this;
	}

	public ScrollPaneBuilder setVMin(double vMin) {
		node.setVmin(vMin);
		return this;
	}

	public ScrollPaneBuilder setHMax(double hMax) {
		node.setHmax(hMax);
		return this;
	}

	public ScrollPaneBuilder setVMax(double vMax) {
		node.setVmax(vMax);
		return this;
	}

	public ScrollPaneBuilder setFitToWidth(boolean fitToWidth) {
		node.setFitToWidth(fitToWidth);
		return this;
	}

	public ScrollPaneBuilder setFitToHeight(boolean fitToHeight) {
		node.setFitToHeight(fitToHeight);
		return this;
	}

	public ScrollPaneBuilder setPannable(boolean pannable) {
		node.setPannable(pannable);
		return this;
	}

	public ScrollPaneBuilder setPrefViewportWidth(double prefViewportWidth) {
		node.setPrefViewportWidth(prefViewportWidth);
		return this;
	}

	public ScrollPaneBuilder setPrefViewportHeight(double prefViewportHeight) {
		node.setPrefViewportHeight(prefViewportHeight);
		return this;
	}

	public ScrollPaneBuilder setMinViewportWidth(double minViewportWidth) {
		node.setMinViewportWidth(minViewportWidth);
		return this;
	}

	public ScrollPaneBuilder setMinViewportHeight(double minViewportHeight) {
		node.setMinViewportHeight(minViewportHeight);
		return this;
	}

	public ScrollPaneBuilder setViewportBounds(Bounds viewportBounds) {
		node.setViewportBounds(viewportBounds);
		return this;
	}
}
