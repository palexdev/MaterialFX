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

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

/// Extension of [SkinBase] used by components that want a seamless integration with the new Behavior API.
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
/// built behavior object, which can be retrieved via [#behavior()], and they manage its entire lifecycle: creation,
/// installation and disposal. The skin never drives the behavior's lifecycle, and the behavior never drives the skin's.
///
/// Essentially, this follows the MVC (Model-View-Controller) pattern applied to UI controls. You have the flexibility to
/// change either the skin or the behavior at any time, and the component will remain functional
/// without requiring extensive code modifications.
/// This high degree of modularity, given by the pattern, allows users to customize such components with ease.
///
/// #### Responsibilities
///
/// Both the skin and the behavior can register listeners and handlers, what differs is their scope:
///  - the **skin** operates on its sub-components. It listens to the control's properties to keep the view up to date,
///  and it registers handlers on the nodes it creates, which delegate to the behavior's methods
///  - the **behavior** hosts the methods that react to user input. Optionally, it can register listeners and handlers
///  of its own on the control directly, or on anything else that does not come from the skin
///
/// #### Registrations and ownership
///
/// Every listener and handler is owned by whoever registers it, and only its owner disposes it. A skin registers its
/// constructs through [#listen(When\[\])] and [#onInput(WhenEvent\[\])], and they are all disposed together with the
/// skin, see [#dispose()]. The same applies to behaviors, which have their own list.
///
/// Since a handler registered by the skin outlives any behavior swap, it must look up the behavior **when the event
/// fires**, never before:
/// ```java
/// // Correct, the behavior is resolved on every event
/// onInput(intercept(node, MouseEvent.MOUSE_CLICKED).handle(e -> behavior().mouseClicked(e)));
///
/// // Wrong, both capture the behavior at registration time and keep calling it after it has been replaced
/// MFXBehavior<?> behavior = behavior();
/// onInput(intercept(node, MouseEvent.MOUSE_CLICKED).handle(behavior::mouseClicked));
/// onInput(intercept(node, MouseEvent.MOUSE_CLICKED).handle(behavior()::mouseClicked));
/// ```
/// Note that the last form is subtle: the receiver of a method reference is evaluated once, when the reference is created.
///
/// #### Development flow
///
/// The development flow for controls with the new Behavior and Skin API would be:
///  - Have a component that extends either [MFXControl], [MFXLabeled] or any of their subclasses
///  - Having an implementation of this base Skin, either one of the already provided or a custom one
///  - Having a behavior class and set the factory on the component, or using [MFXBehavior] if you don't need it
///  - Override [#install()] to register the skin's listeners and handlers. JavaFX calls it automatically when the skin
///  is set on the control, after the previous skin (if any) has been disposed. Registering there rather than in the
///  constructor also guarantees that a skin which is built but never set on the control registers nothing
///  - Initialization and changes to the behavior factory are automatically handled, hassle-free
public abstract class MFXSkinBase<C extends javafx.scene.control.Control & WithBehavior> extends SkinBase<C> {

    //================================================================================
    // Properties
    //================================================================================

    private List<Disposable> disposables = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSkinBase(C control) {super(control);}

    //================================================================================
    // Delegate Methods
    //================================================================================

    /// While making skins for MaterialFX, I always make a great use of [When] constructs, simply because they are so
    /// useful and easy to use, there is no point in not doing it. This, however, comes with a little issue, the more
    /// constructs a skin uses, the longer is the disposal code. A simple solution is to pass the instances to this method
    /// (just wrap all of them as args). They are stored in a `List` so that the disposal can be done
    /// automatically without having every single construct instance in the class.
    ///
    /// Not only that, I'm actually so happy with the work done on [When] that I decided to create an equivalent
    /// for `Events` too, see [WhenEvent], and a delegate method [#onInput(WhenEvent\[\])]
    ///
    /// **Note:** one-shot constructs (see [When#oneShot(boolean)] or [When#oneShot()])
    /// do not need to be registered as they will be automatically disposed on their first trigger.
    /// Doing so brings no harm, it's just useless.
    public void listen(When<?>... ws) {
        for (When<?> w : ws) {
            if (!w.isActive()) w.listen();
            disposables.add(w);
        }
    }

    /// The equivalent of [#listen(When\[\])] for [WhenEvent] constructs. They are stored in the same list and disposed
    /// together with the skin, see [#dispose()].
    ///
    /// If the constructs were not activated before by invoking [WhenEvent#register()], this method will do it for you
    /// automatically.
    ///
    /// Handlers that call into the behavior must resolve it through [#behavior()] every time they run, see the class
    /// documentation.
    public void onInput(WhenEvent<?>... ws) {
        for (WhenEvent<?> w : ws) {
            if (!w.isActive()) w.register();
            disposables.add(w);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Disposes every construct registered through [#listen(When\[\])] and [#onInput(WhenEvent\[\])].
    ///
    /// Only what this skin registered is released, the behavior and its registrations are never touched.
    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        disposables = null;
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
    protected MFXBehavior<? extends Node> behavior() {
        return getSkinnable().getBehavior();
    }

    /// Convenience method to get and cast the control's behavior to the given class.
    protected <B extends MFXBehavior<? extends Node>> B behaviorAs(Class<B> klass) {
        return klass.cast(behavior());
    }
}
