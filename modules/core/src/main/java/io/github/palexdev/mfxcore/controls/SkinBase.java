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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Control;

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
	protected abstract void initBehavior(B behavior);

	//================================================================================
	// Delegate Methods
	//================================================================================

	public <T> void register(ObservableValue<T> observable, ChangeListener<T> listener) {
		behavior.register(observable, listener);
	}

	public void register(Observable observable, InvalidationListener listener) {
		behavior.register(observable, listener);
	}

	public <E extends Event> void handle(Node node, EventType<E> eventType, EventHandler<E> handler) {
		behavior.handler(node, eventType, handler);
	}

	public <E extends Event> void handleAsFilter(Node node, EventType<E> eventType, EventHandler<E> handler) {
		behavior.filter(node, eventType, handler);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
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
	protected B getBehavior() {
		return behavior;
	}

	protected void setBehavior(B behavior) {
		if (this.behavior != null) this.behavior.dispose();
		if (behavior == null)
			throw new IllegalArgumentException("The behavior cannot be null");
		this.behavior = behavior;
		initBehavior(behavior);
	}
}
