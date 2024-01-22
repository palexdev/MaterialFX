/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.resize;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Special extension of {@link RegionDragResizer} which can be used to resize a {@link Stage} by its content
 * (given that the root of the content is a {@link Region}).
 */
public class StageResizer extends RegionDragResizer {

	//================================================================================
	// Constructors
	//================================================================================
	public StageResizer(Region node, Stage stage) {
		super(node);
		setResizeHandler((n, x, y, w, h) -> resizeStage(stage, w, h));
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void resizeStage(Stage stage, double w, double h) {
		stage.setWidth(w);
		stage.setHeight(h);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void handlePressed(MouseEvent event) {
		node.requestFocus();
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
		nodeX = nodeX();
		nodeY = nodeY();
		nodeW = nodeW();
		nodeH = nodeH();
		draggedZone = getZoneByEvent(event);
	}

	@Override
	protected void handleDragged(MouseEvent event) {
		if (node.getCursor() == Cursor.MOVE) return;
		super.handleDragged(event);
	}
}
