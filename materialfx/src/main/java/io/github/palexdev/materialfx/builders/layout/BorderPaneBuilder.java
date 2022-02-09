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
import javafx.scene.layout.BorderPane;

public class BorderPaneBuilder extends PaneBuilder<BorderPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public BorderPaneBuilder() {
		this(new BorderPane());
	}

	public BorderPaneBuilder(BorderPane pane) {
		super(pane);
	}

	public static BorderPaneBuilder borderPane() {
		return new BorderPaneBuilder();
	}

	public static BorderPaneBuilder borderPane(BorderPane pane) {
		return new BorderPaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public BorderPaneBuilder setAlignment(Node child, Pos pos) {
		BorderPane.setAlignment(child, pos);
		return this;
	}

	public BorderPaneBuilder setMargin(Node child, Insets margin) {
		BorderPane.setMargin(child, margin);
		return this;
	}

	public BorderPaneBuilder clearConstraints(Node child) {
		BorderPane.clearConstraints(child);
		return this;
	}

	public BorderPaneBuilder setCenter(Node center) {
		node.setCenter(center);
		return this;
	}

	public BorderPaneBuilder setTop(Node top) {
		node.setTop(top);
		return this;
	}

	public BorderPaneBuilder setRight(Node right) {
		node.setRight(right);
		return this;
	}

	public BorderPaneBuilder setBottom(Node bottom) {
		node.setBottom(bottom);
		return this;
	}

	public BorderPaneBuilder setLeft(Node left) {
		node.setLeft(left);
		return this;
	}
}
