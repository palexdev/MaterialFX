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

package io.github.palexdev.mfxcore.utils;

import io.github.palexdev.mfxcore.base.TriConsumer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

// TODO documentation
public class NodeMover<N extends Node> {
	//================================================================================
	// Properties
	//================================================================================
	protected N node;

	private EventHandler<MouseEvent> draggedHandler = this::handleDragged;
	private EventHandler<MouseEvent> pressedHandler = this::handlePressed;
	private EventHandler<MouseEvent> movedHandler = this::handleMoved;
	private EventHandler<MouseEvent> releasedHandler = this::handleReleased;
	private EventHandler<KeyEvent> keyHandler = this::handleKeyPressed;
	private TriConsumer<N, Double, Double> moveHandler;

	protected double clickedX;
	protected double clickedY;
	protected double translateX;
	protected double translateY;

	//================================================================================
	// Constructors
	//================================================================================
	public NodeMover(N node) {
		this(
			node,
			(n, x, y) -> {
				n.setTranslateX(x);
				n.setTranslateY(y);
			}
		);
	}

	public NodeMover(N node, TriConsumer<N, Double, Double> moveHandler) {
		this.node = node;
		this.moveHandler = moveHandler;
	}

	public static <T extends Node> NodeMover<T> install(T node) {
		return new NodeMover<>(node).install();
	}

	//================================================================================
	// Methods
	//================================================================================
	public NodeMover<N> install() {
		node.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		node.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		node.addEventFilter(MouseEvent.MOUSE_MOVED, movedHandler);
		node.addEventFilter(MouseEvent.MOUSE_RELEASED, releasedHandler);
		node.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
		return this;
	}

	public void uninstall() {
		node.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_MOVED, movedHandler);
		node.removeEventFilter(MouseEvent.MOUSE_RELEASED, releasedHandler);
		node.removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
	}

	public void dispose() {
		uninstall();
		handleReleased(null);
		pressedHandler = null;
		draggedHandler = null;
		movedHandler = null;
		releasedHandler = null;
		keyHandler = null;
		moveHandler = null;
		node = null;
	}

	protected void handleDragged(MouseEvent event) {
		double deltaX = event.getSceneX() - clickedX;
		double deltaY = event.getSceneY() - clickedY;
		moveHandler.accept(node, translateX + deltaX, translateY + deltaY);
	}

	protected void handlePressed(MouseEvent event) {
		node.requestFocus();
		clickedX = event.getSceneX();
		clickedY = event.getSceneY();
		translateX = node.getTranslateX();
		translateY = node.getTranslateY();
		node.setCursor(Cursor.CLOSED_HAND);
		consume(event);
	}

	protected void handleMoved(MouseEvent event) {
		node.setCursor(Cursor.OPEN_HAND);
	}

	protected void handleReleased(MouseEvent event) {
		node.setCursor(Cursor.OPEN_HAND);
	}

	protected void handleKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			moveHandler.accept(node, translateX, translateY);
			triggerMouseRelease();
		}
	}

	protected void triggerMouseRelease() {
		Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_RELEASED,
			0, 0, 0, 0, MouseButton.PRIMARY, 1,
			false, false, false, false, true, false, false, false, false, false, null));
	}

	protected void consume(MouseEvent event) {
		event.consume();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public TriConsumer<N, Double, Double> getMoveHandler() {
		return moveHandler;
	}

	public NodeMover<N> setMoveHandler(TriConsumer<N, Double, Double> moveHandler) {
		this.moveHandler = moveHandler;
		return this;
	}
}
