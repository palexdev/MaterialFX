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

package io.github.palexdev.mfxcore.behavior;

import io.github.palexdev.mfxcore.behavior.actions.ChangeAction;
import io.github.palexdev.mfxcore.behavior.actions.EventAction;
import io.github.palexdev.mfxcore.behavior.actions.InvalidationAction;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class that manages all the actions registered on a specified {@code Node}, they
 * are stored in a list which can be retrieved by {@link #getActions()}.
 * <p></p>
 * Typically, behaviors are used and initialized by the controls' skin in which there usually
 * are several components which should register a certain action.
 * <p>
 * In fact, for {@link EventAction}s it's also needed to specify the node on which the handler
 * will be registered because of this.
 */
public abstract class BehaviorBase<N extends Node> {
	//================================================================================
	// Properties
	//================================================================================
	private N node;
	private final List<DisposableAction> actions = new ArrayList<>();

	//================================================================================
	// Constructors
	//================================================================================
	public BehaviorBase(N node) {
		this.node = node;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Behaviors can specify a set of actions to initialize themselves if needed.
	 */
	public void init() {
	}

	/**
	 * Creates a new {@link EventAction} using {@link EventAction#handler(Node, EventType, EventHandler)}
	 * and adds it to the actions list.
	 *
	 * @param <E> the event's type
	 */
	public <E extends Event> void handler(Node node, EventType<E> eventType, EventHandler<E> handler) {
		actions.add(EventAction.handler(node, eventType, handler));
	}

	/**
	 * Creates a new {@link EventAction} using {@link EventAction#filter(Node, EventType, EventHandler)}
	 * and adds it to the actions list.
	 *
	 * @param <E> the event's type
	 */
	public <E extends Event> void filter(Node node, EventType<E> eventType, EventHandler<E> handler) {
		actions.add(EventAction.filter(node, eventType, handler));
	}

	/**
	 * Creates a new {@link ChangeAction} using {@link ChangeAction#of(ObservableValue, ChangeListener)}
	 * and adds it to the actions list.
	 *
	 * @param <T> the observable's type
	 */
	public <T> void register(ObservableValue<T> observable, ChangeListener<T> listener) {
		actions.add(ChangeAction.of(observable, listener));
	}

	/**
	 * Creates a new {@link InvalidationAction} using {@link InvalidationAction#of(Observable, InvalidationListener)}
	 * and adds it to the actions list.
	 */
	public void register(Observable observable, InvalidationListener listener) {
		actions.add(InvalidationAction.of(observable, listener));
	}

	/**
	 * Calls {@link DisposableAction#dispose()} on all the registered actions, then clears
	 * the list and sets the node field to null making this behavior not usable anymore.
	 */
	public void dispose() {
		actions.forEach(DisposableAction::dispose);
		actions.clear();
		node = null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the node on which this behavior is applied
	 */
	public N getNode() {
		return node;
	}

	/**
	 * @return the list of registered actions as an unmodifiable list
	 */
	public List<DisposableAction> getActions() {
		return Collections.unmodifiableList(actions);
	}
}
