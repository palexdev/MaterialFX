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

package io.github.palexdev.mfxcomponents.controls.base;

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
import javafx.scene.control.SkinBase;

import java.util.Optional;

/**
 * Extension of {@link SkinBase} used by components that what a seamless integration with the new Behavior API.
 * <p>
 * The skin in responsible for initializing the behavior as needed. The new model of MaterialFX components has now three
 * main parts:
 * <p> - the Control, which is the component, the class has all its specs
 * <p> - the View, defines the component's look/layout
 * <p> - the Behavior, defines what the component can do and how
 * <p>
 * So, as you may guess, there must be an 'infrastructure' that makes all these three parts communicate with each other.
 * The behavior may need to be connected with the specs of the component, as well as with its subcomponents defined in
 * its view.
 * <p>
 * {@link MFXControl} and {@link MFXLabeled} are a bridge between these three parts. They retain the reference of the current
 * built behavior object, which can be retrieved via {@link WithBehavior#getBehavior()}. This way skins that need to register
 * handlers or listeners that will then depend on the behavior can do it very easily. The two aforementioned base classes
 * are responsible for calling {@link #initBehavior(BehaviorBase)} every time the behavior changes, as well as dispose it of course.
 * <p>
 * The development flow for controls with the new Behavior and Skin API would be:
 * <p> - Have a components that extends either {@link MFXControl}, {@link MFXLabeled} or any of their subclasses
 * <p> - Having an implementation of this base Skin, either one of the already provided or a custom one
 * <p> - Having a behavior class and set the provider on the component
 * <p> - Override the {@link #initBehavior(BehaviorBase)} to initialize the behavior if needed
 * <p> - Initialization and changes to the behavior provider are automatically handled, hassle-free
 */
public abstract class MFXSkinBase<C extends Control & WithBehavior<B>, B extends BehaviorBase<C>> extends javafx.scene.control.SkinBase<C> {

	//================================================================================
	// Constructors
	//================================================================================
	protected MFXSkinBase(C control) {
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

	// TODO behaviors should also integrate with the new When constructs if possible

	/**
	 * Delegate for {@link BehaviorBase#register(ObservableValue, ChangeListener)}.
	 * <p>
	 * Note this will do nothing if the return value of {@link #getBehavior()} is 'null'.
	 */
	public <T> void register(ObservableValue<T> observable, ChangeListener<T> listener) {
		Optional.ofNullable(getBehavior()).ifPresent(b -> b.register(observable, listener));
	}

	/**
	 * Delegate for {@link BehaviorBase#register(Observable, InvalidationListener)}.
	 * <p>
	 * Note this will do nothing if the return value of {@link #getBehavior()} is 'null'.
	 */
	public void register(Observable observable, InvalidationListener listener) {
		Optional.ofNullable(getBehavior()).ifPresent(b -> b.register(observable, listener));
	}

	/**
	 * Delegate for {@link BehaviorBase#handler(Node, EventType, EventHandler)}.
	 * <p>
	 * Note this will do nothing if the return value of {@link #getBehavior()} is 'null'.
	 */
	public <E extends Event> void handle(Node node, EventType<E> eventType, EventHandler<E> handler) {
		Optional.ofNullable(getBehavior()).ifPresent(b -> b.handler(node, eventType, handler));
	}

	/**
	 * Delegate for {@link BehaviorBase#filter(Node, EventType, EventHandler)}.
	 * <p>
	 * Note this will do nothing if the return value of {@link #getBehavior()} is 'null'.
	 */
	public <E extends Event> void handleAsFilter(Node node, EventType<E> eventType, EventHandler<E> handler) {
		Optional.ofNullable(getBehavior()).ifPresent(b -> b.filter(node, eventType, handler));
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Delegate for {@link WithBehavior#getBehavior()}.
	 * <p>
	 * Since this is called on the component, the return value could also be null if the behavior
	 * provider was not set, or produces null references.
	 */
	protected B getBehavior() {
		return getSkinnable().getBehavior();
	}
}
