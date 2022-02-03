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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HBoxBuilder extends PaneBuilder<HBox> {

	//================================================================================
	// Constructors
	//================================================================================
	public HBoxBuilder() {
		this(new HBox());
	}

	public HBoxBuilder(HBox pane) {
		super(pane);
	}

	public static HBoxBuilder hBox() {
		return new HBoxBuilder();
	}

	public static HBoxBuilder hBox(HBox pane) {
		return new HBoxBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public HBoxBuilder setHGrow(Node child, Priority priority) {
		HBox.setHgrow(child, priority);
		return this;
	}

	public HBoxBuilder setMargin(Node child, Insets margin) {
		HBox.setMargin(child, margin);
		return this;
	}

	public HBoxBuilder clearConstraints(Node child) {
		HBox.clearConstraints(child);
		return this;
	}

	public HBoxBuilder setSpacing(double spacing) {
		node.setSpacing(spacing);
		return this;
	}

	public HBoxBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public HBoxBuilder setFillHeight(boolean fillHeight) {
		node.setFillHeight(fillHeight);
		return this;
	}
}
