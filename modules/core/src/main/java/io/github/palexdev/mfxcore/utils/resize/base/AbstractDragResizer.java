/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils.resize.base;

import io.github.palexdev.mfxcore.enums.Zone;
import io.github.palexdev.mfxcore.utils.resize.RegionDragResizer;
import io.github.palexdev.mfxcore.utils.resize.shapes.CircleDragResizer;
import io.github.palexdev.mfxcore.utils.resize.shapes.RectangleDragResizer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.EnumSet;

public abstract class AbstractDragResizer<T extends Node> {
	protected T node;
	private double margin = 6.0;

	private EventHandler<MouseEvent> pressedHandler = this::handlePressed;
	private EventHandler<MouseEvent> draggedHandler = this::handleDragged;
	private EventHandler<MouseEvent> movedHandler = this::handleMoved;
	private EventHandler<MouseEvent> releasedHandler = this::handleReleased;
	private EventHandler<KeyEvent> keyPressedHandler = this::handleKeyPressed;
	protected DragResizeHandler<T> resizeHandler;

	protected final EnumSet<Zone> allowedZones = EnumSet.of(Zone.ALL);
	protected Zone draggedZone = Zone.NONE;
	protected double clickedX;
	protected double clickedY;
	protected double nodeX;
	protected double nodeY;
	protected double nodeW;
	protected double nodeH;

	public AbstractDragResizer(T node, DragResizeHandler<T> resizeHandler) {
		this.node = node;
		this.resizeHandler = resizeHandler;
	}

	protected abstract void handleDragged(MouseEvent event);

	// TODO refactor if upgrading jdk
	public static AbstractDragResizer<? extends Node> resizerFor(Node node) {
		if (node instanceof Region) {
			return new RegionDragResizer(((Region) node));
		}
		if (node instanceof Circle) {
			return new CircleDragResizer(((Circle) node));
		}
		if (node instanceof Rectangle) {
			return new RectangleDragResizer(((Rectangle) node));
		}
		return null;
	}

	public AbstractDragResizer<T> makeResizable() {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		node.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		node.addEventFilter(MouseEvent.MOUSE_MOVED, movedHandler);
		node.addEventFilter(MouseEvent.MOUSE_RELEASED, releasedHandler);
		node.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
		return this;
	}

	public void uninstall() {
		node.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_MOVED, movedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_RELEASED, releasedHandler);
		node.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
	}

	public void dispose() {
		uninstall();
		handleReleased(null);
		pressedHandler = null;
		draggedHandler = null;
		movedHandler = null;
		releasedHandler = null;
		keyPressedHandler = null;
		resizeHandler = null;
		node = null;
	}

	protected void handlePressed(MouseEvent event) {
		node.requestFocus();
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
		nodeX = nodeX();
		nodeY = nodeY();
		nodeW = nodeW();
		nodeH = nodeH();
		draggedZone = getZoneByEvent(event);
		event.consume();
	}

	protected void handleMoved(MouseEvent event) {
		Zone zone = getZoneByEvent(event);
		Cursor cursor = getCursorByZone(zone);
		node.setCursor(cursor);
	}

	protected void handleReleased(MouseEvent event) {
		node.setCursor(Cursor.DEFAULT);
		draggedZone = Zone.NONE;
	}

	protected void handleKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			resizeHandler.onResize(node, nodeX, nodeY, nodeW, nodeH);
			triggerMouseRelease();
		}
	}

	protected void triggerMouseRelease() {
		Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_RELEASED,
				0, 0, 0, 0, MouseButton.PRIMARY, 1,
				false, false, false, false, true, false, false, false, false, false, null));
	}

	// TODO refactor if upgrading jdk
	protected Cursor getCursorByZone(Zone zone) {
		switch (zone) {
			case TOP_LEFT:
				return Cursor.NW_RESIZE;
			case TOP_CENTER:
				return Cursor.N_RESIZE;
			case TOP_RIGHT:
				return Cursor.NE_RESIZE;
			case BOTTOM_LEFT:
				return Cursor.SW_RESIZE;
			case BOTTOM_RIGHT:
				return Cursor.SE_RESIZE;
			case BOTTOM_CENTER:
				return Cursor.S_RESIZE;
			case CENTER_LEFT:
				return Cursor.W_RESIZE;
			case CENTER_RIGHT:
				return Cursor.E_RESIZE;
			default:
				return Cursor.DEFAULT;
		}
	}

	protected Zone getZoneByEvent(MouseEvent event) {
		if (allowedZones.contains(Zone.NONE)) return Zone.NONE;

		Zone zone = Zone.NONE;
		boolean top = isTopZone(event);
		boolean right = isRightZone(event);
		boolean bottom = isBottomZone(event);
		boolean left = isLeftZone(event);

		if (top && left) zone = Zone.TOP_LEFT;
		else if (top && right) zone = Zone.TOP_RIGHT;
		else if (bottom && left) zone = Zone.BOTTOM_LEFT;
		else if (bottom && right) zone = Zone.BOTTOM_RIGHT;
		else if (top) zone = Zone.TOP_CENTER;
		else if (right) zone = Zone.CENTER_RIGHT;
		else if (bottom) zone = Zone.BOTTOM_CENTER;
		else if (left) zone = Zone.CENTER_LEFT;

		return allowedZones.contains(zone) || allowedZones.contains(Zone.ALL) ? zone : Zone.NONE;
	}

	protected boolean isTopZone(MouseEvent event) {
		return intersect(0, event.getY());
	}

	protected boolean isRightZone(MouseEvent event) {
		return intersect(nodeW(), event.getX());
	}

	protected boolean isBottomZone(MouseEvent event) {
		return intersect(nodeH(), event.getY());
	}

	protected boolean isLeftZone(MouseEvent event) {
		return intersect(0, event.getX());
	}

	protected boolean intersect(double side, double point) {
		return side + margin > point && side - margin < point;
	}

	protected double nodeX() {
		return node.getLayoutX();
	}

	protected double nodeY() {
		return node.getLayoutY();
	}

	protected double nodeW() {
		return node.getLayoutBounds().getWidth();
	}

	protected double nodeH() {
		return node.getLayoutBounds().getHeight();
	}

	public double getMargin() {
		return margin;
	}

	public void setMargin(double margin) {
		this.margin = margin;
	}

	public DragResizeHandler<T> getResizeHandler() {
		return resizeHandler;
	}

	public void setResizeHandler(DragResizeHandler<T> resizeHandler) {
		this.resizeHandler = resizeHandler;
	}

	public EnumSet<Zone> getAllowedZones() {
		return allowedZones;
	}

	public void setAllowedZones(Zone... allowedZones) {
		this.allowedZones.clear();
		this.allowedZones.addAll(Arrays.asList(allowedZones));
	}
}
