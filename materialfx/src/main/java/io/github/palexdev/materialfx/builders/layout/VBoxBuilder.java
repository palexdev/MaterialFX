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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxBuilder extends PaneBuilder<VBox> {

	//================================================================================
	// Constructors
	//================================================================================
	public VBoxBuilder() {
		this(new VBox());
	}

	public VBoxBuilder(VBox pane) {
		super(pane);
	}

	public static VBoxBuilder vBox() {
		return new VBoxBuilder();
	}

	public static VBoxBuilder vBox(VBox pane) {
		return new VBoxBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public VBoxBuilder setVGrow(Node child, Priority priority) {
		VBox.setVgrow(child, priority);
		return this;
	}

	public VBoxBuilder setMargin(Node child, Insets margin) {
		VBox.setMargin(child, margin);
		return this;
	}

	public VBoxBuilder clearConstraints(Node child) {
		VBox.clearConstraints(child);
		return this;
	}

	public VBoxBuilder setSpacing(double spacing) {
		node.setSpacing(spacing);
		return this;
	}

	public VBoxBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public VBoxBuilder setFillWidth(boolean fillWidth) {
		node.setFillWidth(fillWidth);
		return this;
	}
}
