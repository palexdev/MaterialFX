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

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

public class FlowPaneBuilder extends PaneBuilder<FlowPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public FlowPaneBuilder() {
		this(new FlowPane());
	}

	public FlowPaneBuilder(FlowPane pane) {
		super(pane);
	}

	public static FlowPaneBuilder flowPane() {
		return new FlowPaneBuilder();
	}

	public static FlowPaneBuilder flowPane(FlowPane pane) {
		return new FlowPaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public FlowPaneBuilder setMargin(Node child, Insets margin) {
		FlowPane.setMargin(child, margin);
		return this;
	}

	public FlowPaneBuilder clearConstraints(Node child) {
		FlowPane.clearConstraints(child);
		return this;
	}

	public FlowPaneBuilder setOrientation(Orientation orientation) {
		node.setOrientation(orientation);
		return this;
	}

	public FlowPaneBuilder setHGap(double hGap) {
		node.setHgap(hGap);
		return this;
	}

	public FlowPaneBuilder setVGap(double vGap) {
		node.setVgap(vGap);
		return this;
	}

	public FlowPaneBuilder setPrefWrapLength(double prefWrapLength) {
		node.setPrefWrapLength(prefWrapLength);
		return this;
	}

	public FlowPaneBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public FlowPaneBuilder setColumnHAlignment(HPos columnHAlignment) {
		node.setColumnHalignment(columnHAlignment);
		return this;
	}

	public FlowPaneBuilder setRowVAlignment(VPos rowVAlignment) {
		node.setRowValignment(rowVAlignment);
		return this;
	}
}
