/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

/// Extension of [SkinBase] used by components that want a seamless integration with the new Behavior API.
/// The skin is responsible for initializing the behavior as needed.
///
/// This integration defines a specific and recommended strategy to develop UI components. There are three main parts:
///  - the Control, which is the component, the class has all its specs
///  - the View, defines the component's look/layout
///  - the Behavior, defines what the component can do and how
///
/// So, as you may guess, there must be an 'infrastructure' that makes all these three parts communicate with each other.
/// The behavior may need to be connected with the specs of the component, as well as with the subcomponents defined in
/// its view.
///
/// [MFXControl] and [MFXLabeled] are a bridge between these three parts. They retain the reference of the current
/// built behavior object, which can be retrieved via [#getBehavior()]. They are responsible for calling
/// [#registerBehavior()] every time the behavior changes, as well as dispose it, of course.
///
/// The behavior is specifically responsible for managing user input, in other words, event handlers and filters.
/// On the other hand, the skin is responsible for handling listeners related to the control's properties.
///
/// Essentially, this follows the MVC (Model-View-Controller) pattern applied to UI controls. You have the flexibility to
/// change either the skin or the behavior at any time, and the component will remain functional
/// without requiring extensive code modifications.
/// This high degree of modularity, given by the pattern, allows users to customize such components with ease.
///
/// In all of this, the skin plays a central role. Because user input originates from UI elements,
/// which are part of the view (the skin), it is responsible for creating the handlers that will invoke behavior methods.
/// Additionally, the view (the skin) must respond to any changes in the control (essentially the model),
/// which means it also adds the necessary listeners to monitor property changes.
///
///
/// The development flow for controls with the new Behavior and Skin API would be:
///  - Have a component that extends either [MFXControl], [MFXLabeled] or any of their subclasses
///  - Having an implementation of this base Skin, either one of the already provided or a custom one
///  - Having a behavior class and set the factory on the component, or using [MFXBehavior] if you don't need it
///  - Override the [#registerBehavior()] to initialize the behavior if needed
///  - Initialization and changes to the behavior factory are automatically handled, hassle-free
public abstract class MFXSkinBase<C extends javafx.scene.control.Control & WithBehavior> extends SkinBase<C> {
    //================================================================================
    // Properties
    //================================================================================
    private List<Disposable> listeners = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSkinBase(C control) {super(control);}

    //================================================================================
    // Methods
    //================================================================================

    /// This should be overridden when needed to register additional behavior logic onto the control's behavior class.
    ///
    /// By default, calls [MFXBehavior#init()]
    protected void registerBehavior() {
        getBehavior().init();
    }

    //================================================================================
    // Delegate Methods
    //================================================================================

    /// Delegate for [#register(WhenEvent[])].
    ///
    /// Note this will do nothing if the return value of [#getBehavior()] is `null`.
    public void events(WhenEvent<?>... wes) {
        Optional.ofNullable(getBehavior()).ifPresent(b -> b.register(wes));
    }

    /// While making skins for MaterialFX, I always make a great use of [When] constructs, simply because they are so
    /// useful and easy to use, there is no point in not doing it. This, however, comes with a little issue, the more
    /// constructs a skin uses, the longer is the disposal code. A simple solution is to pass the instances to this method
    /// (just wrap all of them as args). They are stored in a `List` so that the disposal can be done
    /// automatically without having every single construct instance in the class.
    ///
    /// Not only that, I'm actually so happy with the work done on [When] that I decided to create an equivalent
    /// for `Events` too, see [WhenEvent], and a delegate method [#events(WhenEvent\[\])]
    ///
    /// **Note:** one-shot constructs (see [When#oneShot(boolean)] or [When#oneShot()])
    /// do not need to be registered as they will be automatically disposed on their first trigger.
    /// Doing so brings no harm, it's just useless.
    public void listeners(When<?>... listeners) {
        for (When<?> w : listeners) {
            if (!w.isActive()) w.listen();
            this.listeners.add(w);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void dispose() {
        listeners.forEach(Disposable::dispose);
        listeners.clear();
        listeners = null;
        Optional.ofNullable(getBehavior()).ifPresent(MFXBehavior::clear);
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================

    /// Delegate for [#getSkinnable()] that can be overridden!
    protected C getControl() {
        return getSkinnable();
    }

    /// Delegate for [WithBehavior#getBehavior()].
    ///
    /// Since this is called on the component, the return value could also be `null` if the behavior
    /// factory was not set or produces `null` references.
    protected MFXBehavior<? extends Node> getBehavior() {
        return getSkinnable().getBehavior();
    }

    /// Convenience method to get and cast the control's behavior to the given class.
    protected <B extends MFXBehavior<? extends Node>> B getBehaviorAs(Class<B> klass) {
        return klass.cast(getBehavior());
    }
}
