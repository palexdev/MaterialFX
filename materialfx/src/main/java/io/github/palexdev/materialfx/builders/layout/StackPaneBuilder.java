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
import javafx.scene.layout.StackPane;

public class StackPaneBuilder extends PaneBuilder<StackPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public StackPaneBuilder() {
		this(new StackPane());
	}

	public StackPaneBuilder(StackPane pane) {
		super(pane);
	}

	public static StackPaneBuilder stackPane() {
		return new StackPaneBuilder();
	}

	public static StackPaneBuilder stackPane(StackPane pane) {
		return new StackPaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public StackPaneBuilder setAlignment(Node child, Pos alignment) {
		StackPane.setAlignment(child, alignment);
		return this;
	}

	public StackPaneBuilder setMargin(Node child, Insets margin) {
		StackPane.setMargin(child, margin);
		return this;
	}

	public StackPaneBuilder clearConstraints(Node child) {
		StackPane.clearConstraints(child);
		return this;
	}

	public StackPaneBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}
}
