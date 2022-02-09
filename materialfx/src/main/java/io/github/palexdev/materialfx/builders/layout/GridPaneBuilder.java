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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class GridPaneBuilder extends PaneBuilder<GridPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public GridPaneBuilder() {
		this(new GridPane());
	}

	public GridPaneBuilder(GridPane pane) {
		super(pane);
	}

	public static GridPaneBuilder gridPane() {
		return new GridPaneBuilder();
	}

	public static GridPaneBuilder gridPane(GridPane pane) {
		return new GridPaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public GridPaneBuilder setRowIndex(Node child, int rowIndex) {
		GridPane.setRowIndex(child, rowIndex);
		return this;
	}

	public GridPaneBuilder setColumnIndex(Node child, int columnIndex) {
		GridPane.setColumnIndex(child, columnIndex);
		return this;
	}

	public GridPaneBuilder setRowSpan(Node child, int rowSpan) {
		GridPane.setRowSpan(child, rowSpan);
		return this;
	}

	public GridPaneBuilder setColumnSpan(Node child, int columnSpan) {
		GridPane.setColumnSpan(child, columnSpan);
		return this;
	}

	public GridPaneBuilder setMargin(Node child, Insets margin) {
		GridPane.setMargin(child, margin);
		return this;
	}

	public GridPaneBuilder setHAlignment(Node child, HPos hPos) {
		GridPane.setHalignment(child, hPos);
		return this;
	}

	public GridPaneBuilder setVAlignment(Node child, VPos vPos) {
		GridPane.setValignment(child, vPos);
		return this;
	}

	public GridPaneBuilder setHGrow(Node child, Priority priority) {
		GridPane.setHgrow(child, priority);
		return this;
	}

	public GridPaneBuilder setVGrow(Node child, Priority priority) {
		GridPane.setVgrow(child, priority);
		return this;
	}

	public GridPaneBuilder setFillWidth(Node child, boolean fillWidth) {
		GridPane.setFillWidth(child, fillWidth);
		return this;
	}

	public GridPaneBuilder setFillHeight(Node child, boolean fillHeight) {
		GridPane.setFillHeight(child, fillHeight);
		return this;
	}

	public GridPaneBuilder setConstraints(Node child, int columnIndex, int rowIndex) {
		GridPane.setConstraints(child, columnIndex, rowIndex);
		return this;
	}

	public GridPaneBuilder setConstraints(Node child, int columnIndex, int rowIndex, int columnSpan, int rowspan) {
		GridPane.setConstraints(child, columnIndex, rowIndex, columnSpan, rowspan);
		return this;
	}

	public GridPaneBuilder setConstraints(Node child, int columnIndex, int rowIndex, int columnSpan, int rowspan, HPos hAlignment, VPos vAlignment) {
		GridPane.setConstraints(child, columnIndex, rowIndex, columnSpan, rowspan, hAlignment, vAlignment);
		return this;
	}

	public GridPaneBuilder setConstraints(Node child, int columnIndex, int rowIndex, int columnSpan, int rowspan, HPos hAlignment, VPos vAlignment, Priority hGrow, Priority vGrow) {
		GridPane.setConstraints(child, columnIndex, rowIndex, columnSpan, rowspan, hAlignment, vAlignment, hGrow, vGrow);
		return this;
	}

	public GridPaneBuilder setConstraints(Node child, int columnIndex, int rowIndex, int columnSpan, int rowspan, HPos hAlignment, VPos vAlignment, Priority hGrow, Priority vGrow, Insets margin) {
		GridPane.setConstraints(child, columnIndex, rowIndex, columnSpan, rowspan, hAlignment, vAlignment, hGrow, vGrow, margin);
		return this;
	}

	public GridPaneBuilder clearConstraints(Node child) {
		GridPane.clearConstraints(child);
		return this;
	}

	public GridPaneBuilder setHGap(double hGap) {
		node.setHgap(hGap);
		return this;
	}

	public GridPaneBuilder setVGap(double vGap) {
		node.setVgap(vGap);
		return this;
	}

	public GridPaneBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public GridPaneBuilder setGridLinesVisible(boolean gridLinesVisible) {
		node.setGridLinesVisible(gridLinesVisible);
		return this;
	}

	public GridPaneBuilder addRowConstraints(RowConstraints... constraints) {
		node.getRowConstraints().addAll(constraints);
		return this;
	}

	public GridPaneBuilder setRowConstraints(RowConstraints... constraints) {
		node.getRowConstraints().setAll(constraints);
		return this;
	}

	public GridPaneBuilder addColumnConstraints(ColumnConstraints... constraints) {
		node.getColumnConstraints().addAll(constraints);
		return this;
	}

	public GridPaneBuilder setColumnConstraints(ColumnConstraints... constraints) {
		node.getColumnConstraints().setAll(constraints);
		return this;
	}

	public GridPaneBuilder add(Node child, int columnIndex, int rowIndex) {
		node.add(child, columnIndex, rowIndex);
		return this;
	}

	public GridPaneBuilder add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan) {
		node.add(child, columnIndex, rowIndex, colspan, rowspan);
		return this;
	}

	public GridPaneBuilder addRow(int rowIndex, Node... children) {
		node.addRow(rowIndex, children);
		return this;
	}

	public GridPaneBuilder addColumn(int columnIndex, Node... children) {
		node.addColumn(columnIndex, children);
		return this;
	}
}
