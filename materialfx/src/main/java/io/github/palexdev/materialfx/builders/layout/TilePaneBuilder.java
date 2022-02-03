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
import javafx.scene.layout.TilePane;

public class TilePaneBuilder extends PaneBuilder<TilePane> {

	//================================================================================
	// Constructors
	//================================================================================
	public TilePaneBuilder() {
		this(new TilePane());
	}

	public TilePaneBuilder(TilePane pane) {
		super(pane);
	}

	public static TilePaneBuilder tilePane() {
		return new TilePaneBuilder();
	}

	public static TilePaneBuilder tilePane(TilePane pane) {
		return new TilePaneBuilder(pane);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public TilePaneBuilder setAlignment(Node node, Pos alignment) {
		TilePane.setAlignment(node, alignment);
		return this;
	}

	public TilePaneBuilder setMargin(Node node, Insets margin) {
		TilePane.setMargin(node, margin);
		return this;
	}

	public TilePaneBuilder clearConstraints(Node child) {
		TilePane.clearConstraints(child);
		return this;
	}

	public TilePaneBuilder setPrefRows(int prefRows) {
		node.setPrefRows(prefRows);
		return this;
	}

	public TilePaneBuilder setPrefColumns(int prefColumns) {
		node.setPrefColumns(prefColumns);
		return this;
	}

	public TilePaneBuilder setPrefTileWidth(double prefTileWidth) {
		node.setPrefTileWidth(prefTileWidth);
		return this;
	}

	public TilePaneBuilder setPrefTileHeight(double prefTileHeight) {
		node.setPrefTileHeight(prefTileHeight);
		return this;
	}

	public TilePaneBuilder setHGap(double hGap) {
		node.setHgap(hGap);
		return this;
	}

	public TilePaneBuilder setVGap(double vGap) {
		node.setVgap(vGap);
		return this;
	}

	public TilePaneBuilder setAlignment(Pos alignment) {
		node.setAlignment(alignment);
		return this;
	}

	public TilePaneBuilder setTileAlignment(Pos tileAlignment) {
		node.setTileAlignment(tileAlignment);
		return this;
	}
}
