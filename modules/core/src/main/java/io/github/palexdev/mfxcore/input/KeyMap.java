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

package io.github.palexdev.mfxcore.input;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

import io.github.palexdev.mfxcore.base.TriConsumer;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

/// Convenient extension of a [HashMap] to associate a [KeyStroke] with a specific action.
///
/// _This map does not allow duplicates and will throw an exception in such case! To override a key binding, use the
/// explicit [#override(KeyStroke , BiConsumer)] method instead._
///
/// **How to use & How it works**
///
/// A `KeyMap` has to be installed on a certain [EventTarget]. In JavaFX, it could be a [javafx.scene.Node], a [Scene]
/// or a [Window]. To do that, use [#install(EventTarget, boolean)].
///
/// After that, a [EventHandler] will intercept all [KeyEvent#KEY_PRESSED] events on the given target and send them to
/// [#handleEvent(KeyEvent)] for processing.
///
/// In general, after building the [KeyStroke] from the event, if there's an action associated with that shortcut, it
/// is run with the event and the shortcut as inputs.
///
/// - _Note 1: if you want to stop the `KeyMap`, use [#uninstall()]_
/// - _Note 2: if you install the `KeyMap` when already installed on a target, it is first uninstalled_
/// - _Note 3: for a complete cleanup (if and when you don't need it anymore) you can use [#dispose()]_
public class KeyMap extends HashMap<KeyStroke, BiConsumer<KeyEvent, KeyStroke>> {
    //================================================================================
    // Properties
    //================================================================================
    private WeakReference<EventTarget> target;
    private Handler handler;

    //================================================================================
    // Methods
    //================================================================================

    @Override
    public BiConsumer<KeyEvent, KeyStroke> put(KeyStroke key, BiConsumer<KeyEvent, KeyStroke> value) {
        if (key == null)
            throw new IllegalArgumentException("Shortcut cannot be null");
        if (containsKey(key))
            throw new IllegalArgumentException("An action for combination " + key + " already exists");
        return super.put(key, value);
    }

    /// Same as [#put(KeyStroke, BiConsumer)] but bypasses the duplicates check. This is an explicit way for overriding
    /// the behavior of a `KeyMap`.
    public void override(KeyStroke shortcut, BiConsumer<KeyEvent, KeyStroke> action) {
        if (shortcut == null)
            throw new IllegalArgumentException("Shortcut cannot be null");
        super.put(shortcut, action);
    }

    /// Installs the `KeyMap` on the given [EventTarget] by adding a [EventHandler] on it. Events are processed by
    /// [#handleEvent(KeyEvent)].
    ///
    /// If the `KeyMap` is already installed, [#uninstall()] is called first.
    ///
    /// @param asFilter whether to add the [EventHandler] as a standard handler or as a filter
    public void install(EventTarget target, boolean asFilter) {
        if (this.target != null) uninstall();
        this.target = new WeakReference<>(target);
        handler = new Handler(asFilter);
        handler.register();
    }

    /// Disables the `KeyMap` by removing and disposing the [EventHandler] from the target.
    public void uninstall() {
        if (handler != null) {
            handler.unregister();
            handler = null;
        }
        this.target = null;
    }

    /// On every significant event, creates a new [KeyStroke] object, retrieves the associated action from the map,
    /// and if present, runs it with the event and the shortcut that triggered it as inputs.
    ///
    /// @see KeyStroke#fromEvent(KeyEvent)
    protected void handleEvent(KeyEvent ke) {
        KeyStroke ks = KeyStroke.fromEvent(ke);
        Optional.ofNullable(get(ks)).ifPresent(c -> c.accept(ke, ks));
    }

    /// Calls [#uninstall()] and also removes all entries from the map.
    public void dispose() {
        uninstall();
        clear();
    }

    /// @return whether the `KeyMap` is currently installed on a target
    public boolean isInstalled() {
        return target != null;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    class Handler {
        private final TriConsumer<EventTarget, EventType<KeyEvent>, EventHandler<KeyEvent>> reg;
        private final TriConsumer<EventTarget, EventType<KeyEvent>, EventHandler<KeyEvent>> unReg;
        private final EventHandler<KeyEvent> handler = KeyMap.this::handleEvent;

        public Handler(boolean asFilter) {
            if (asFilter) {
                reg = EventTarget::addEventFilter;
                unReg = EventTarget::removeEventFilter;
            } else {
                reg = EventTarget::addEventHandler;
                unReg = EventTarget::removeEventHandler;
            }
        }

        public void register() {
            reg.accept(target.get(), KeyEvent.KEY_PRESSED, handler);
        }

        public void unregister() {
            if (target.get() != null)
                unReg.accept(target.get(), KeyEvent.KEY_PRESSED, handler);
        }
    }
}
