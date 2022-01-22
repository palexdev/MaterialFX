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

package io.github.palexdev.materialfx.beans;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Simple bean that has a node reference, a duration for the animation and
 * an action to perform when the animation ends.
 */
public class AnimationsData {
	//================================================================================
	// Properties
	//================================================================================
	private final Node node;
	private final Duration duration;
	private final EventHandler<ActionEvent> onFinished;

	//================================================================================
	// Constructors
	//================================================================================
	public AnimationsData(Node node, Duration duration, EventHandler<ActionEvent> onFinished) {
		this.node = node;
		this.duration = duration;
		this.onFinished = onFinished;
	}

	//================================================================================
	// Methods
	//================================================================================
	public Node node() {
		return node;
	}

	public Duration duration() {
		return duration;
	}

	public EventHandler<ActionEvent> onFinished() {
		return onFinished;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Builds a new AnimationsData object with the given node and duration, the action is set to null.
	 */
	public static AnimationsData of(Node node, Duration duration) {
		return of(node, duration, null);
	}

	/**
	 * Builds a new AnimationsData object with the given node and duration and action.
	 */
	public static AnimationsData of(Node node, Duration duration, EventHandler<ActionEvent> onFinished) {
		return new AnimationsData(node, duration, onFinished);
	}
}
