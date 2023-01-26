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

package io.github.palexdev.mfxcore.behavior.actions;

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.DisposableAction;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * An {@code EventAction} is a convenience bean which implements {@link DisposableAction}
 * used by {@link BehaviorBase} to register an {@link EventHandler} on a certain {@link Node} for
 * a certain {@link EventType}, and dispose it once it's not needed anymore.
 * <p></p>
 * This bean also has a boolean flag to indicate whether this should be registered as a handler or a filter.
 */
public class EventAction<E extends Event> implements DisposableAction {
	//================================================================================
	// Properties
	//================================================================================
	private Node node;
	private EventType<E> eventType;
	private EventHandler<E> handler;
	protected boolean isFilter = false;

	//================================================================================
	// Constructor
	//================================================================================
	public EventAction(Node node, EventType<E> eventType, EventHandler<E> handler) {
		this.node = node;
		this.eventType = eventType;
		this.handler = handler;
	}

	public EventAction(Node node, EventType<E> eventType, EventHandler<E> handler, boolean isFilter) {
		this.node = node;
		this.eventType = eventType;
		this.handler = handler;
		this.isFilter = isFilter;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Equivalent to {@link #EventAction(Node, EventType, EventHandler)} but this also registers
	 * the action on the node already.
	 */
	public static <E extends Event> EventAction<E> handler(Node node, EventType<E> et, EventHandler<E> handler) {
		EventAction<E> ea = new EventAction<>(node, et, handler);
		node.addEventHandler(et, handler);
		return ea;
	}

	/**
	 * Equivalent to {@link #EventAction(Node, EventType, EventHandler, boolean)} but this also registers
	 * the action on the node already.
	 */
	public static <E extends Event> EventAction<E> filter(Node node, EventType<E> et, EventHandler<E> handler) {
		EventAction<E> ea = new EventAction<>(node, et, handler, true);
		node.addEventFilter(et, handler);
		return ea;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removes the handler from the node, then sets all the fields to null.
	 */
	@Override
	public void dispose() {
		if (isFilter) {
			node.removeEventFilter(eventType, handler);
		} else {
			node.removeEventHandler(eventType, handler);
		}
		eventType = null;
		handler = null;
		node = null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Node getNode() {
		return node;
	}

	public EventType<E> getEventType() {
		return eventType;
	}

	public EventHandler<E> getHandler() {
		return handler;
	}

	public boolean isFilter() {
		return isFilter;
	}
}
