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

package io.github.palexdev.mfxcore.utils.fx;

import io.github.palexdev.mfxcore.utils.resize.StageResizer;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class contains utilities to be used on {@link Window}s.
 */
public class StageUtils {

	//================================================================================
	// Constructors
	//================================================================================
	private StageUtils() {}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Makes the given {@link Stage} draggable by the given node.
	 * <p>
	 * Ideally you may want to use this on windows without the native header. In such cases, it's common to have a
	 * region at the top of the custom window that replaces the native header. Such a region can indeed be used as the
	 * window's dragging point.
	 */
	public static void makeDraggable(Stage stage, Node byNode) {
		Delta dragDelta = new Delta();
		AtomicBoolean allowed = new AtomicBoolean(true);
		byNode.setOnMousePressed(e -> {
			// record a delta distance for the drag and drop operation.
			if (!allowed.get()) return;
			dragDelta.x = stage.getX() - e.getScreenX();
			dragDelta.y = stage.getY() - e.getScreenY();
			byNode.setCursor(Cursor.MOVE);
		});
		byNode.setOnMouseReleased(e -> byNode.setCursor(Cursor.HAND));
		byNode.setOnMouseDragged(e -> {
			if (!allowed.get()) return;
			stage.setX(e.getScreenX() + dragDelta.x);
			stage.setY(e.getScreenY() + dragDelta.y);
		});
		byNode.setOnMouseMoved(e -> {
			Node iNode = e.getPickResult().getIntersectedNode();
			allowed.set(iNode == byNode);
			byNode.setCursor(!allowed.get() ? Cursor.DEFAULT : Cursor.HAND);
		});
	}

	/**
	 * Makes the given {@link Stage} resizable.
	 * <p>
	 * Ideally you may want to use this on custom windows that cannot use the native resizing. All windows must have
	 * a scene and therefore a node to show the content. If the content is a {@link Region} (which is a resizable node),
	 * it can be used to also resize the window.
	 * <p>
	 * This makes use of {@link StageResizer}.
	 */
	public static void makeResizable(Stage stage, Region byRegion) {
		StageResizer resizer = new StageResizer(byRegion, stage);
		resizer.makeResizable();
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	private static class Delta {
		private double x;
		private double y;
	}
}
