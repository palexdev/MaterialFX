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
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class AnchorPaneBuilder extends PaneBuilder<AnchorPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public AnchorPaneBuilder() {
		this(new AnchorPane());
	}

	public AnchorPaneBuilder(AnchorPane pane) {
		super(pane);
	}

	public static AnchorPaneBuilder anchorPane() {
		return new AnchorPaneBuilder();
	}

	public static AnchorPaneBuilder anchorPane(AnchorPane pane) {
		return new AnchorPaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public AnchorPaneBuilder setAllConstraints(Node child, Insets constraints) {
		setTopAnchor(child, constraints.getTop());
		setRightAnchor(child, constraints.getRight());
		setBottomAnchor(child, constraints.getBottom());
		setLeftAnchor(child, constraints.getLeft());
		return this;
	}

	public AnchorPaneBuilder setTopAnchor(Node child, double top) {
		AnchorPane.setTopAnchor(child, top);
		return this;
	}

	public AnchorPaneBuilder setRightAnchor(Node child, double right) {
		AnchorPane.setRightAnchor(child, right);
		return this;
	}

	public AnchorPaneBuilder setBottomAnchor(Node child, double bottom) {
		AnchorPane.setBottomAnchor(child, bottom);
		return this;
	}

	public AnchorPaneBuilder setLeftAnchor(Node child, double left) {
		AnchorPane.setLeftAnchor(child, left);
		return this;
	}

	public AnchorPaneBuilder clearConstraints(Node child) {
		AnchorPane.clearConstraints(child);
		return this;
	}
}
