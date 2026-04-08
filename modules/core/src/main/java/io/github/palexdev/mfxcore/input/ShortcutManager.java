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

import java.lang.ref.Cleaner;
import java.util.*;

import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import javafx.scene.Node;
import javafx.scene.Scene;

/// A manager for keyboard shortcuts at the [Scene] level. Tries to mimic what JavaFX does with
/// `ControlAcceleratorSupport`, but in a more generic and transparent way, integrating with [KeyStroke] and [KeyMap].
///
/// Shortcuts are managed through [Action] objects which pair a [KeyStroke] with a [Runnable]. A [KeyMap] stores the
/// shortcuts, intercepts events on the scene, and runs the corresponding actions when the shortcut is matched.
///
/// #### Installation Modes
///
/// 1) [#installOn(Scene, Action...)] - Installs shortcuts directly on a scene.<br >
/// This is the recommended approach when you have a reference to the scene.
///
/// 2) [#installOn(Node, Action...)] - Installs shortcuts on a node's scene.<br >
/// When the node's scene changes, shortcuts are automatically migrated.
/// Shortcuts are disposed when the node is garbage collected (via [Cleaner]).
///
/// 3) [#installFor(MFXMenu)] - Convenience method to install shortcuts for all [MFXMenuItem]s that have a shortcut.<br >
/// (the system is similar to JavaFX's menu items with the `accelerator` property)
///
/// **Usage Example**
///
/// ```java
/// ShortcutManager.installOn(scene,
///     Action.action("Ctrl+S", () -> save()),
///     Action.action("Ctrl+Z", () -> undo())
/// );
/// ```
public class ShortcutManager {

    //================================================================================
    // Properties
    //================================================================================

    private static final Map<Scene, ShortcutManager> sceneShortcutManagers = new WeakHashMap<>();
    private static final Cleaner cleaner = Cleaner.create();

    private final KeyMap keyMap = new KeyMap();

    //================================================================================
    // Constructors
    //================================================================================

    private ShortcutManager() {}

    //================================================================================
    // Methods
    //================================================================================

    /// Installs the given [Action]s on the specified [Scene].
    ///
    /// @see #doInstall(Scene, Action...)
    public static void installOn(Scene scene, Action... actions) {
        Objects.requireNonNull(scene);
        doInstall(scene, actions);
    }

    /// Installs shortcuts for all [MFXMenuItems][MFXMenuItem] in the given [MFXMenu] that have a [shortcut][KeyStroke].
    ///
    /// This is a convenience method that extracts actions from menu items and calls [#installOn(Scene, Action...)]
    /// on the menu's root owner scene. The [Runnable] for the [Action] simply calls [MFXMenuItem.MFXMenuItemBehavior#runAction()].
    ///
    /// @throws IllegalArgumentException if the menu is not a root menu
    /// @throws IllegalStateException if the menu is not installed yet
    public static void installFor(MFXMenu menu) {
        if (!menu.isRootMenu()) throw new IllegalArgumentException("Menu is not a root menu!");
        if (!menu.isInstalled()) throw new IllegalStateException("Menu is not installed yet, therefore owner is null!");
        Action[] actions = menu.getAllItems()
            .filter(it -> it.getShortcut() != null)
            .map(it -> new Action(
                it.getShortcut(),
                () -> ((MFXMenuItem.MFXMenuItemBehavior) it.getBehavior()).runAction()
            ))
            .toArray(Action[]::new);
        installOn(menu.getOwner(), actions);
    }

    /// Removes shortcuts for all [MFXMenuItem]s in the given [MFXMenu] that have a shortcut.<br >
    /// Basically the inverse of [#installFor(MFXMenu)].
    ///
    /// @throws IllegalArgumentException if the menu is not a root menu
    /// @throws IllegalStateException if the menu is not installed yet
    public static void uninstallFor(MFXMenu menu) {
        if (!menu.isRootMenu()) throw new IllegalArgumentException("Menu is not a root menu!");
        if (!menu.isInstalled()) throw new IllegalStateException("Menu is not installed yet, therefore owner is null!");
        Action[] actions = menu.getAllItems()
            .filter(it -> it.getShortcut() != null)
            .map(it -> new Action(
                it.getShortcut(),
                null // since we don's include the Runnable in equality checks, we don't need to instantiate them here
            ))
            .toArray(Action[]::new);
        uninstallFrom(menu.getOwner(), actions);
    }

    /// Installs the given [Action]s on the scene of the specified [Node].
    ///
    /// Unlike [#installOn(Scene, Action...)], this method automatically handles scene changes.
    /// When the node's scene changes, shortcuts are removed from the old scene and installed on the new one.
    ///
    /// Shortcuts are automatically disposed when the node is garbage collected.
    public static void installOn(Node node, Action... actions) {
        When<Scene> listener = When.onChanged(node.sceneProperty())
            .then((o, n) -> {
                if (o != null) doUninstall(o, actions);
                if (n != null) doInstall(n, actions);
            })
            .executeNow(() -> node.getScene() != null)
            .listen();
        cleaner.register(node, listener::dispose);
    }

    /// Completely uninstalls the [ShortcutManager] for the given [Scene] (all actions are removed).
    ///
    /// @see #doUninstall(Scene, Action...)
    public static void uninstallFrom(Scene scene) {
        doUninstall(scene, (Action[]) null);
    }

    /// Removes the given [Action]s from the scene of the specified [Node].<br >
    /// Basically the inverse of [#installOn(Node, Action...)].
    public static void uninstallFrom(Node node, Action... actions) {
        When<Scene> listener = When.onInvalidated(node.sceneProperty())
            .condition(Objects::nonNull)
            .then((s) -> doUninstall(s, actions))
            .executeNow(() -> node.getScene() != null)
            .oneShot(true)
            .listen();
        if (!listener.isDisposed())
            cleaner.register(node, listener::dispose);
    }

    /// Creates or retrieves the [ShortcutManager] for the given scene, adds all actions to its [KeyMap],
    /// and installs the [KeyMap] if not already installed.
    private static void doInstall(Scene scene, Action... actions) {
        ShortcutManager manager = sceneShortcutManagers.computeIfAbsent(scene, _ -> new ShortcutManager());
        for (Action action : actions) {
            manager.keyMap.put(action.shortcut(), (_, _) -> action.action().run());
        }
        if (!manager.keyMap.isInstalled()) {
            cleaner.register(scene, manager.keyMap::dispose);
            manager.keyMap.install(scene, false);
        }
    }

    /// Removes the given actions from the scene's [KeyMap].<br >
    /// If the [KeyMap] becomes empty, it is disposed and the [ShortcutManager] is removed from the internal registry.
    private static void doUninstall(Scene scene, Action... actions) {
        Optional.ofNullable(sceneShortcutManagers.get(scene))
            .ifPresent(sm -> {
                if (actions == null) {
                    sm.keyMap.dispose();
                } else {
                    Arrays.stream(actions).map(Action::shortcut).forEach(sm.keyMap::remove);
                    if (sm.keyMap.isEmpty()) sm.keyMap.dispose();
                }
                sceneShortcutManagers.remove(scene);
            });
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// A record that pairs a [KeyStroke] with a [Runnable] action, used by [ShortcutManager] to define keyboard shortcuts.
    ///
    /// _Note: the action is excluded from equality checks and hashCode_
    public record Action(KeyStroke shortcut, Runnable action) {

        public static Action action(KeyStroke shortcut, Runnable action) {return new Action(shortcut, action);}

        /// Uses [KeyStroke#fromString(String)] to resolve from a string.
        public static Action action(String shortcut, Runnable action) {
            return action(KeyStroke.fromString(shortcut), action);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Action action = (Action) o;
            return Objects.equals(shortcut(), action.shortcut());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(shortcut());
        }
    }
}
