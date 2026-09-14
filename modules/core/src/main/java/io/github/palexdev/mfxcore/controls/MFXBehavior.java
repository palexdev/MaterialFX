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
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;

/// Base class to implement behavioral code for any kind of control. In the MVC pattern, the behavior would be the
/// equivalent of the controller. This offers methods that cover most types of input events.
///
/// The component's view (the skin) adds the necessary [EventHandlers][EventHandler] on its sub-components, and
/// delegates to the behavior class.
///
/// Thanks to the encapsulation offered by this pattern, the skin and the behavior of a component can be easily changed
/// with ease at any time, and it would still be functional.
///
/// A behavior can also register listeners and handlers of its own, through [#listen(When\[\])] and
/// [#onInput(WhenEvent\[\])]. Their scope is the control itself, or anything else that does not come from the skin: the
/// skin's sub-components are the skin's business. The constructs are added into a list owned by the behavior and are
/// deactivated/disposed by invoking [#dispose()]. Skins keep a separate list, so the two never interfere.
///
/// #### Lifecycle
///
/// Behaviors are managed by [MFXControl] and [MFXLabeled] through the behavior factory:
///  - the behavior is **created** as soon as the factory is set, which for the default behavior happens while the control
///  is being constructed
///  - it is **installed** once, by calling [#install()], when the control's first skin is created. If the factory changes
///  after that, the new behavior is installed immediately
///  - it is **disposed** when the factory changes again, and a new one takes its place
///
/// Skin changes do not affect the behavior in any way.
///
/// **Construction rule:** since the default behavior is created during the initialization of [MFXControl] and
/// [MFXLabeled], the fields of the concrete control do not exist yet at that time. For this reason, a behavior's
/// constructor must not access the control's own properties. Any setup of that kind belongs to [#install()].
///
/// #### State
///
/// A behavior is replaced **only** when the control's behavior factory changes, that is, by
/// [WithBehavior#setBehaviorFactory(java.util.function.Supplier)] or [WithBehavior#setDefaultBehaviorFactory()].
/// Nothing else causes a replacement: skin changes, scene changes and CSS never do.
///
/// When that happens, the old instance is disposed and the new one starts from scratch. Behaviors should therefore be as
/// stateless as possible. Those that do need state should be aware of what is lost and act accordingly:
///  - **Interaction state**, such as the starting point of a drag or a running animation. An interaction in progress is
///  interrupted: the registrations are disposed, but anything else the behavior started (animations, timers, scheduled
///  tasks) keeps running unless it is stopped. Override [#dispose()] to stop it, and call `super.dispose()`
///  - **Configuration state**, such as a threshold or a flag set through the behavior's own setters. The new instance
///  comes with its defaults, and whoever replaces the behavior is responsible for configuring it again. If a setting
///  must survive a replacement, it belongs to the control, not the behavior
public abstract class MFXBehavior<N extends Node> implements Disposable {
    //================================================================================
    // Static Properties
    //================================================================================
    public static final Runnable NO_OP_CALLBACK = () -> {};

    //================================================================================
    // Properties
    //================================================================================
    private N node;
    private final List<Disposable> disposables = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================

    protected MFXBehavior(N node) {
        this.node = node;
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Behaviors can override this to initialize themselves, for example to register listeners and handlers through
    /// [#listen(When\[\])] and [#onInput(WhenEvent\[\])].
    ///
    /// This is called automatically by the control, exactly once per behavior instance: when the control's first skin is
    /// created, or immediately if the behavior is set after that. See the class documentation for details.
    ///
    /// By default, does nothing.
    protected void install() {}

    /// Registers the given [When] constructs on this behavior. They are added to a list which will be used for disposal,
    /// avoiding memory leaks when calling [#dispose()].
    ///
    /// Also note that if the constructs were not activated before by invoking [When#listen()], this method
    /// will do it for you automatically.
    public final void listen(When<?>... ws) {
        for (When<?> w : ws) {
            if (!w.isActive()) w.listen();
            disposables.add(w);
        }
    }

    /// The behavior API registers input actions in the form of [WhenEvent] constructs. This method adds them
    /// to a list (which will be used for disposal, avoiding memory leaks when calling [#dispose()]).
    ///
    /// Also note that if the constructs were not activated before by invoking [WhenEvent#register()], this method
    /// will do it for you automatically.
    public final void onInput(WhenEvent<?>... ws) {
        for (WhenEvent<?> w : ws) {
            if (!w.isActive()) w.register();
            disposables.add(w);
        }
    }

    /// Disposes and removes all the constructs registered on this behavior through [#listen(When\[\])] and
    /// [#onInput(WhenEvent\[\])], without disposing of the behavior itself.
    public final void clear() {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
    }

    //================================================================================
    // Events Specific Methods
    //================================================================================

    //***** Mouse *****//

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mousePressed(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mousePressed(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mousePressed(MouseEvent e) {
        mousePressed(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseReleased(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseReleased(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseReleased(MouseEvent e) {
        mouseReleased(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_CLICKED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseClicked(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseClicked(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseClicked(MouseEvent e) {
        mouseClicked(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_MOVED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseMoved(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseMoved(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseMoved(MouseEvent e) {
        mouseMoved(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_DRAGGED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseDragged(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseDragged(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseDragged(MouseEvent e) {
        mouseDragged(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_ENTERED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseEntered(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseEntered(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseEntered(MouseEvent e) {
        mouseEntered(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_EXITED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseExited(MouseEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#mouseExited(MouseEvent, Runnable)], invoked with a no-op callback.
    public final void mouseExited(MouseEvent e) {
        mouseExited(e, NO_OP_CALLBACK);
    }

    //***** Keys *****//

    /// Should be used by subclasses to handle [KeyEvent#KEY_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyPressed(KeyEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#keyPressed(KeyEvent,Runnable)], invoked with a no-op callback.
    public final void keyPressed(KeyEvent e) {
        keyPressed(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [KeyEvent#KEY_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyReleased(KeyEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#keyReleased(KeyEvent,Runnable)], invoked with a no-op callback.
    public final void keyReleased(KeyEvent e) {
        keyReleased(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [KeyEvent#KEY_TYPED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyTyped(KeyEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#keyTyped(KeyEvent,Runnable)], invoked with a no-op callback.
    public final void keyTyped(KeyEvent e) {
        keyTyped(e, NO_OP_CALLBACK);
    }

    //***** Touch *****//

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchPressed(TouchEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#touchPressed(TouchEvent, Runnable)], invoked with a no-op callback.
    public final void touchPressed(TouchEvent e) {
        touchPressed(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchReleased(TouchEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#touchReleased(TouchEvent, Runnable)], invoked with a no-op callback.
    public final void touchReleased(TouchEvent e) {
        touchReleased(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_MOVED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchMoved(TouchEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#touchMoved(TouchEvent, Runnable)], invoked with a no-op callback.
    public final void touchMoved(TouchEvent e) {
        touchMoved(e, NO_OP_CALLBACK);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_STATIONARY] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchStationary(TouchEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#touchStationary(TouchEvent, Runnable)], invoked with a no-op callback.
    public final void touchStationary(TouchEvent e) {
        touchStationary(e, NO_OP_CALLBACK);
    }

    //***** Scroll *****//

    /// Should be used by subclasses to handle [ScrollEvent#SCROLL] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void scroll(ScrollEvent e, Runnable callback) {
        callback.run();
    }

    /// Convenience delegate method for [#scroll(ScrollEvent, Runnable)], invoked with a no-op callback.
    public void scroll(ScrollEvent e) {
        scroll(e, NO_OP_CALLBACK);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Calls [#clear()] and then sets the node instance to `null`, making the behavior effectively not usable anymore.
    @Override
    public void dispose() {
        clear();
        node = null;
    }

    //================================================================================
    // Getters
    //================================================================================

    public N getNode() {
        return node;
    }

    protected <N1 extends Node> N1 getNodeAs(Class<N1> klass) {
        return klass.cast(node);
    }
}
