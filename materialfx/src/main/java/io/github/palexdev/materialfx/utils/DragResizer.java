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

package io.github.palexdev.materialfx.utils;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.EnumSet;
import java.util.function.Function;

public class DragResizer {
	//================================================================================
	// Properties
	//================================================================================
	public enum Direction {
		NONE, TOP, RIGHT, BOTTOM, LEFT, ALL
	}

	private final Region region;
	private double resizeMargin = 5.0;
	private Function<Region, Double> widthConstraintFunction = r -> r.prefWidth(-1);
	private Function<Region, Double> heightConstraintFunction = r -> r.prefHeight(-1);
	private boolean initialized = false;

	private Point2D mouseLocation;
	private Direction allowedDirection;
	private Direction draggingDirection = Direction.NONE;
	private boolean dragging;

	private final EventHandler<MouseEvent> mousePressed = this::handlePressed;
	private final EventHandler<MouseEvent> mouseDragged = this::handleDragged;
	private final EventHandler<MouseEvent> mouseMoved = this::handleMoved;
	private final EventHandler<MouseEvent> mouseReleased = this::handleReleased;

	//================================================================================
	// Constructors
	//================================================================================
	public DragResizer(Region region, Direction direction) {
		this.region = region;
		this.allowedDirection = direction;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Installs the necessary Mouse handlers to the specified {@link Region}
	 * to make it resizable.
	 */
	public DragResizer makeResizable() {
		region.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressed);
		region.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragged);
		region.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMoved);
		region.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleased);
		return this;
	}

	/**
	 * Removes all the Mouse handlers from the {@link Region}, also
	 * calls {@link  #handleReleased(MouseEvent)} to reset the {@link Region}
	 * and the DragResizer.
	 */
	public DragResizer uninstall() {
		region.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressed);
		region.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragged);
		region.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMoved);
		region.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleased);
		handleReleased(null);
		return this;
	}

	/**
	 * Intercepts {@link  MouseEvent#MOUSE_PRESSED} events and determines
	 * the drag direction.
	 * <p>
	 * If the direction is valid proceeds with initialization and
	 * stores the intercepted Mouse coordinates.
	 */
	private void handlePressed(MouseEvent event) {
		if (isTopZone(event)) {
			draggingDirection = Direction.TOP;
		} else if (isRightZone(event)) {
			draggingDirection = Direction.RIGHT;
		} else if (isBottomZone(event)) {
			draggingDirection = Direction.BOTTOM;
		} else if (isLeftZone(event)) {
			draggingDirection = Direction.LEFT;
		} else {
			return;
		}

		dragging = true;
		if (!initialized) {
			region.setMinHeight(region.getHeight());
			region.setMinWidth(region.getWidth());
			initialized = true;
		}

		mouseLocation = new Point2D(event.getSceneX(), event.getSceneY());
		event.consume();
	}

	/**
	 * Intercepts {@link MouseEvent#MOUSE_DRAGGED} events. This
	 * is effectively responsible for the node resizing.
	 * <p>
	 * Gets the delta Mouse coordinates and computes the new sizes according to the
	 * detected dragging direction.
	 * <p>
	 * The new sizes are clamped as specified by {@link  #getWidthConstraintFunction()}
	 * and {@link  #getHeightConstraintFunction()}.
	 */
	private void handleDragged(MouseEvent event) {
		if (!dragging) return;

		double deltaX = event.getSceneX() - mouseLocation.getX();
		double deltaY = event.getSceneY() - mouseLocation.getY();

		double newWidth = region.getMinWidth();
		double newHeight = region.getMinHeight();
		switch (draggingDirection) {
			case TOP: {
				newHeight -= deltaY;
				break;
			}
			case RIGHT: {
				newWidth += deltaX;
				break;
			}
			case BOTTOM: {
				newHeight += deltaY;
				break;
			}
			case LEFT: {
				newWidth -= deltaX;
				break;
			}
		}

		double minW = widthConstraintFunction.apply(region);
		double minH = heightConstraintFunction.apply(region);
		if (newWidth < minW) newWidth = minW;
		if (newHeight < minH) newHeight = minH;

		region.setMinHeight(newHeight);
		region.setMinWidth(newWidth);
		mouseLocation = new Point2D(event.getSceneX(), event.getSceneY());
	}

	/**
	 * Intercepts {@link MouseEvent#MOUSE_MOVED} events to determine
	 * on which side of the {@link Region} the Mouse is.
	 * Sets the cursor accordingly.
	 */
	private void handleMoved(MouseEvent event) {
		if (isTopZone(event) || draggingDirection == Direction.TOP) {
			region.setCursor(Cursor.N_RESIZE);
		} else if (isRightZone(event) || draggingDirection == Direction.RIGHT) {
			region.setCursor(Cursor.E_RESIZE);
		} else if (isBottomZone(event) || draggingDirection == Direction.BOTTOM) {
			region.setCursor(Cursor.S_RESIZE);
		} else if (isLeftZone(event) || draggingDirection == Direction.LEFT) {
			region.setCursor(Cursor.W_RESIZE);
		} else {
			region.setCursor(Cursor.DEFAULT);
		}
	}

	/**
	 * Intercepts {@link MouseEvent#MOUSE_RELEASED} events.
	 * <p>
	 * When the Mouse is released the {@link Region} and the DragResizer are reset.
	 * (the event argument is not necessary at the moment, thus it can be also null)
	 */
	private void handleReleased(MouseEvent event) {
		initialized = false;
		dragging = false;
		draggingDirection = Direction.NONE;
		region.setCursor(Cursor.DEFAULT);
	}

	/**
	 * Checks if the {@link Region} can be resized at the TOP.
	 */
	private boolean isTopZone(MouseEvent event) {
		return EnumSet.of(Direction.TOP, Direction.ALL).contains(allowedDirection) && event.getY() < resizeMargin;
	}

	/**
	 * Checks if the {@link Region} can be resized at the RIGHT.
	 */
	private boolean isRightZone(MouseEvent event) {
		return EnumSet.of(Direction.RIGHT, Direction.ALL).contains(allowedDirection) && event.getX() > (region.getWidth() - resizeMargin);
	}

	/**
	 * Checks if the {@link Region} can be resized at the BOTTOM.
	 */
	private boolean isBottomZone(MouseEvent event) {
		return EnumSet.of(Direction.BOTTOM, Direction.ALL).contains(allowedDirection) && event.getY() > (region.getHeight() - resizeMargin);
	}

	/**
	 * Checks if the {@link Region} can be resized at the LEFT.
	 */
	private boolean isLeftZone(MouseEvent event) {
		return EnumSet.of(Direction.LEFT, Direction.ALL).contains(allowedDirection) && event.getX() < resizeMargin;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the margin around the {@link Region} that a user can click in to start resizing it
	 */
	public double getResizeMargin() {
		return resizeMargin;
	}

	public DragResizer setResizeMargin(double resizeMargin) {
		this.resizeMargin = resizeMargin;
		return this;
	}

	/**
	 * @return the function used to compute the minimum width the region can reach,
	 * values won't go below the function's result
	 */
	public Function<Region, Double> getWidthConstraintFunction() {
		return widthConstraintFunction;
	}

	public DragResizer setWidthConstraintFunction(Function<Region, Double> widthConstraintFunction) {
		this.widthConstraintFunction = widthConstraintFunction;
		return this;
	}

	/**
	 * @return the function used to compute the minimum height the region can reach,
	 * values won't go below the function's result
	 */
	public Function<Region, Double> getHeightConstraintFunction() {
		return heightConstraintFunction;
	}

	public DragResizer setHeightConstraintFunction(Function<Region, Double> heightConstraintFunction) {
		this.heightConstraintFunction = heightConstraintFunction;
		return this;
	}

	/**
	 * @return the allowed resize directions
	 */
	public Direction getAllowedDirection() {
		return allowedDirection;
	}

	public DragResizer setAllowedDirection(Direction allowedDirection) {
		this.allowedDirection = allowedDirection;
		return this;
	}
}
