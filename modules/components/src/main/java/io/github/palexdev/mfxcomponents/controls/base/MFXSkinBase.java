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

import io.github.palexdev.mfxcomponents.layout.MFXResizable;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.DisposableAction;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Extension of {@link SkinBase} used by components that want a seamless integration with the new Behavior API.
 * <p>
 * The skin in responsible for initializing the behavior as needed. The new model of MaterialFX components has now three
 * main parts:
 * <p> - the Control, which is the component, the class has all its specs
 * <p> - the View, defines the component's look/layout
 * <p> - the Behavior, defines what the component can do and how
 * <p>
 * So, as you may guess, there must be an 'infrastructure' that makes all these three parts communicate with each other.
 * The behavior may need to be connected with the specs of the component, as well as with the subcomponents defined in
 * its view.
 * <p>
 * {@link MFXControl} and {@link MFXLabeled} are a bridge between these three parts. They retain the reference of the current
 * built behavior object, which can be retrieved via {@link WithBehavior#getBehavior()}. They are responsible for calling
 * {@link #initBehavior(BehaviorBase)} every time the behavior changes, as well as dispose it of course.
 * <p></p>
 * The behavior is specifically responsible for managing user input, in other words event handlers and filters.
 * On the other hand, the skin is responsible for handling listeners related to the control's properties.
 * <p>
 * Essentially, this follows the MVC (Model-View-Controller) pattern applied to UI controls. You have the flexibility to
 * change either the skin or the behavior at any time, and the component will remain functional
 * without requiring extensive code modifications.
 * This high degree of modularity, given by the pattern, allows users to customize my components with ease.
 * <p>
 * In all of this, the skin plays a central role. Because user input originates from UI elements,
 * which are part of the view (the skin), it is responsible for creating the handlers that will invoke behavior methods.
 * Additionally, the view (the skin) must respond to any changes in the control (essentially the model),
 * which means it also adds the necessary listeners to monitor property changes.
 * <p></p>
 * The development flow for controls with the new Behavior and Skin API would be:
 * <p> - Have a components that extends either {@link MFXControl}, {@link MFXLabeled} or any of their subclasses
 * <p> - Having an implementation of this base Skin, either one of the already provided or a custom one
 * <p> - Having a behavior class and set the provider on the component
 * <p> - Override the {@link #initBehavior(BehaviorBase)} to initialize the behavior if needed
 * <p> - Initialization and changes to the behavior provider are automatically handled, hassle-free
 * <p></p>
 * Last but not least, this skin makes all the methods responsible for computing the component' sizes {@code public}, this
 * is for the integration with the {@link MFXResizable} API.
 */
public abstract class MFXSkinBase<C extends Control & WithBehavior<B>, B extends BehaviorBase<C>> extends javafx.scene.control.SkinBase<C> {
	//================================================================================
	// Properties
	//================================================================================
	private List<DisposableAction> listeners = new ArrayList<>();

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

	/**
	 * Delegate for {@link BehaviorBase#register(WhenEvent[])}.
	 * <p>
	 * Note this will do nothing if the return value of {@link #getBehavior()} is {@code null}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void events(WhenEvent... wes) {
		Optional.ofNullable(getBehavior()).ifPresent(b -> b.register(wes));
	}

	/**
	 * While making skins for MaterialFX I always make a great use of {@link When} constructs, simply because they are so
	 * useful and easy to use there is no point in not doing it. This however comes with a little issue, the more
	 * constructs a skin uses the longer is the disposal code. A simple solution is to pass the instances to this method
	 * (just wrap all of them as args), which will store them in a {@code List} so that the disposal can be done
	 * automatically without having every single construct instance in the class.
	 * <p>
	 * Not only that, I'm actually so happy with the work done on {@link When} that I decided to create an equivalent
	 * for {@code Events} too, see {@link WhenEvent}, and a delegate method {@link #events(WhenEvent[])}
	 * <p>
	 * <b>Note: </b> one-shot constructs (see {@link  When#oneShot(boolean)} or {@link WhenEvent#oneShot()})
	 * do not need to be registered as they will be automatically disposed on their first trigger.
	 * Doing so brings no harm, it's just useless.
	 */
	public void listeners(When<?>... listeners) {
		for (When<?> w : listeners) {
			if (!w.isActive()) w.listen();
			this.listeners.add(w);
		}
		Collections.addAll(this.listeners, listeners);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	@Override
	public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMaxWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	public void dispose() {
		listeners.forEach(DisposableAction::dispose);
		listeners.clear();
		listeners = null;
		super.dispose();
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
