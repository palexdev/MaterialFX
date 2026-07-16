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

import java.net.URL;

import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/// Public API to manage an application's theme: the CSS sources to apply (see [Stylesheet]) and whether
/// the UI should be light, dark, or follow the system preference (see [ThemeMode]).
///
/// The API is intentionally minimal as it describes _what_ a theme engine exposes, not _how_ it applies any of it.
/// Where the stylesheets end up, which nodes are informed about the mode, etc., are all decisions left to the implementation.
///
/// ##### Theme Mode
/// [ThemeMode] has three values, but only two of them are ever "real". [ThemeMode#SYSTEM] means _follow the OS_, so it
/// has to be resolved to either [ThemeMode#LIGHT] or [ThemeMode#DARK] before it's of any use. This is why there are two
/// getters rather than one:
/// 1) [#getThemeMode()] returns the raw value, `SYSTEM` included. Use it to know what was _requested_
/// 2) [#resolveThemeMode()] returns the effective value, never `SYSTEM`. Use it to know what the UI should actually
///    look like right now
///
/// ##### Service
/// Engines are consumed as a service: this module declares `uses ThemeEngine`, so that anything in here can get hold of
/// one through [java.util.ServiceLoader] without depending on whoever implements it.
///
/// Implementors are responsible for registering themselves with a `provides ThemeEngine with ...` directive in their own
/// `module-info`. Since engines tend to be singletons, and [java.util.ServiceLoader] cannot instantiate those, the
/// registered class is typically a small provider exposing a `public static ThemeEngine provider()` method.
public interface ThemeEngine {

    /// @return the [Stylesheets][Stylesheet] currently managed by the engine. Implementations are free to manage this
    /// list however they like (for example, return an unmodifiable list)
    ObservableList<Stylesheet> stylesheets();

    /// @return the effective [ThemeMode]. [ThemeMode#SYSTEM] is resolved from [Platform#getPreferences()] to either
    /// [ThemeMode#LIGHT] or [ThemeMode#DARK].
    default ThemeMode resolveThemeMode() {
        ThemeMode mode = getThemeMode();
        return mode == ThemeMode.SYSTEM ?
            ThemeMode.valueOf(Platform.getPreferences().getColorScheme().name()) :
            mode;
    }

    default ThemeMode getThemeMode() {
        return themeModeProperty().get();
    }

    /// Specifies the requested [ThemeMode].<br >
    /// Beware that this may hold [ThemeMode#SYSTEM], which is not a mode the UI can be rendered in, see
    /// [#resolveThemeMode()].
    ObjectProperty<ThemeMode> themeModeProperty();

    default void setThemeMode(ThemeMode mode) {
        themeModeProperty().set(mode);
    }

    /// Flips the mode between [ThemeMode#LIGHT] and [ThemeMode#DARK].
    default void switchThemeMode() {
        ThemeMode mode = resolveThemeMode();
        setThemeMode(mode == ThemeMode.LIGHT ? ThemeMode.DARK : ThemeMode.LIGHT);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    enum ThemeMode {
        LIGHT, DARK, SYSTEM
    }

    /// Abstraction layer over a "true" CSS source, see [#src()].
    ///
    /// Any type can act as a CSS source by implementing this. The typical case is an enum of presets, each constant
    /// backed by its own `.css` file, so that the constants can be handed to a [ThemeEngine] as they are.
    ///
    /// What [#src()] returns is up to the implementation, as long as JavaFX accepts it. Usually it's the external form
    /// of a [URL], see [URL#toExternalForm()], but it can also be a data URI, which allows for sources generated at
    /// runtime rather than loaded from a file, see [CSSFragment#toDataUri()].
    ///
    /// @see #stylesheet(String)
    /// @see #stylesheet(URL)
    interface Stylesheet {

        /// @return the CSS source, in any form accepted by JavaFX
        String src();

        /// Convenience factory to create a `Stylesheet` object from the given string src (URL, data URI, etc...).
        static Stylesheet stylesheet(String src) {
            return () -> src;
        }

        /// Convenience factory to create a `Stylesheet` object from the given URL which resolves as [URL#toExternalForm()].
        static Stylesheet stylesheet(URL src) {
            return () -> src.toExternalForm();
        }
    }

}
