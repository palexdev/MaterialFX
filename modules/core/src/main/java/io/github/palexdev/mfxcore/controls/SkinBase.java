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

package io.github.palexdev.mfxcore.controls;

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Control;

/**
 * Extension of {@link javafx.scene.control.SkinBase} used by components that what a seamless integration with the new
 * Behavior API.
 * <p>
 * The skin in responsible for keeping an instance of the behavior object and initializing it.
 * <p>
 * For implementation of this, the flow should be this:
 * <p> - The skin creates the behavior, in case of controls implementing {@link WithBehavior}, it's possible to use {@link WithBehavior#getBehaviorProvider()}
 * <p> - The skin sets the behavior with {@link #setBehavior(BehaviorBase)}
 * <p> - The above method will automatically call {@link #initBehavior(BehaviorBase)}, which is an abstract method every skin
 * should implement, it's responsible for initializing the behavior every time it changes
 */
public abstract class SkinBase<C extends Control, B extends BehaviorBase<C>> extends javafx.scene.control.SkinBase<C> {
	//================================================================================
	// Properties
	//================================================================================
	private B behavior;

	//================================================================================
	// Constructors
	//================================================================================
	protected SkinBase(C control) {
		super(control);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * This is responsible for initializing the behavior every time it changes, the given parameter
	 * is the current uninitialized behavior.
	 */
	protected abstract void initBehavior(B behavior);

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate for {@link BehaviorBase#register(ObservableValue, ChangeListener)}.
	 */
	public <T> void register(ObservableValue<T> observable, ChangeListener<T> listener) {
		behavior.register(observable, listener);
	}

	/**
	 * Delegate for {@link BehaviorBase#register(Observable, InvalidationListener)}.
	 */
	public void register(Observable observable, InvalidationListener listener) {
		behavior.register(observable, listener);
	}

	/**
	 * Delegate for {@link BehaviorBase#handler(Node, EventType, EventHandler)}.
	 */
	public <E extends Event> void handle(Node node, EventType<E> eventType, EventHandler<E> handler) {
		behavior.handler(node, eventType, handler);
	}

	/**
	 * Delegate for {@link BehaviorBase#filter(Node, EventType, EventHandler)}.
	 */
	public <E extends Event> void handleAsFilter(Node node, EventType<E> eventType, EventHandler<E> handler) {
		behavior.filter(node, eventType, handler);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * It's also responsible for disposing the current behavior.
	 */
	@Override
	public void dispose() {
		if (behavior != null) behavior.dispose();
		behavior = null;
		super.dispose();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the current behavior instance used by the control
	 */
	protected B getBehavior() {
		return behavior;
	}

	/**
	 * This should be used by implementations when the behavior needs to change.
	 * Will automatically call {@link #initBehavior(BehaviorBase)}.
	 *
	 * @throws IllegalArgumentException if the given behavior is null
	 */
	protected void setBehavior(B behavior) {
		if (this.behavior != null) this.behavior.dispose();
		if (behavior == null)
			throw new IllegalArgumentException("The behavior cannot be null");
		this.behavior = behavior;
		initBehavior(behavior);
	}
}
