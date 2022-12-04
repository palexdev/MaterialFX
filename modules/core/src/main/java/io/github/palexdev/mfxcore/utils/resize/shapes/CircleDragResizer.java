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

package io.github.palexdev.mfxcore.utils.resize.shapes;

import io.github.palexdev.mfxcore.enums.Zone;
import io.github.palexdev.mfxcore.utils.resize.base.AbstractDragResizer;
import io.github.palexdev.mfxcore.utils.resize.base.DragResizeHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.util.function.Function;

public class CircleDragResizer extends AbstractDragResizer<Circle> {
	private Function<Circle, Double> minRadiusFunction = c -> 0.0;

	public CircleDragResizer(Circle node) {
		this(node, (circle, x, y, w, h) -> {
			circle.setLayoutX(x);
			circle.setLayoutY(y);
			circle.setRadius(w);
		});
	}

	public CircleDragResizer(Circle node, DragResizeHandler<Circle> resizeHandler) {
		super(node, resizeHandler);
		node.setPickOnBounds(true);
	}

	// TODO refactor if upgrading jdk
	@Override
	protected void handleDragged(MouseEvent event) {
		if (draggedZone == Zone.NONE) return;

		double currX = event.getSceneX();
		double currY = event.getSceneY();
		double deltaX = currX - clickedX;
		double deltaY = currY - clickedY;
		double deltaRad = 0;
		double newX = nodeX;
		double newY = nodeY;

		switch (draggedZone) {
			case TOP_LEFT: {
				deltaRad = -deltaY / 2;
				newX -= deltaRad;
				newY -= deltaRad;
				break;
			}
			case TOP_CENTER: {
				deltaRad = -deltaY / 2;
				newY -= deltaRad;
				break;
			}
			case TOP_RIGHT: {
				deltaRad = -deltaY / 2;
				newX += deltaRad;
				newY -= deltaRad;
				break;
			}
			case CENTER_RIGHT: {
				deltaRad = deltaX / 2;
				newX += deltaRad;
				break;
			}
			case BOTTOM_RIGHT: {
				deltaRad = deltaY / 2;
				newX += deltaRad;
				newY += deltaRad;
				break;
			}
			case BOTTOM_CENTER: {
				deltaRad = deltaY / 2;
				newY += deltaRad;
				break;
			}
			case BOTTOM_LEFT: {
				deltaRad = deltaY / 2;
				newX -= deltaRad;
				newY += deltaRad;
				break;
			}
			case CENTER_LEFT: {
				deltaRad = -deltaX / 2;
				newX -= deltaRad;
				break;
			}
		}

		double newRad = Math.round((nodeW / 2) + deltaRad);
		double min = minRadiusFunction.apply(node);
		newRad = Math.max(min, newRad);

		resizeHandler.onResize(node, newX, newY, newRad, newRad);
	}

	@Override
	protected void handleKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			resizeHandler.onResize(node, nodeX, nodeY, nodeW / 2, nodeH / 2);
			triggerMouseRelease();
		}
	}

	@Override
	protected boolean isTopZone(MouseEvent event) {
		return intersect(-node.getRadius() + node.getCenterY(), event.getY());
	}

	@Override
	protected boolean isRightZone(MouseEvent event) {
		return intersect(node.getRadius() + node.getCenterX(), event.getX());
	}

	@Override
	protected boolean isBottomZone(MouseEvent event) {
		return intersect(node.getRadius() + node.getCenterY(), event.getY());
	}

	@Override
	protected boolean isLeftZone(MouseEvent event) {
		return intersect(-node.getRadius() + node.getCenterX(), event.getX());
	}

	public Function<Circle, Double> getMinRadiusFunction() {
		return minRadiusFunction;
	}

	public void setMinRadiusFunction(Function<Circle, Double> minRadiusFunction) {
		this.minRadiusFunction = minRadiusFunction;
	}
}
