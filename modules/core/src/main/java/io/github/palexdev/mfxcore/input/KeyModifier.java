/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.Map;
import java.util.Optional;

import io.github.palexdev.mfxcore.enums.OS;
import io.github.palexdev.mfxcore.utils.OSUtils;
import javafx.scene.input.KeyCode;

/// Enumeration to represent the four most important modifiers on any keyboard:
/// - `ALT`
/// - `CONTROL`
/// - `META`, which is either the Win key or Cmd key on Mac
/// - `SHIFT`
/// - `SHORTCUT`, which is a virtual OS-dependant modifier (Ctrl on Windows, Cmd on Mac)
///
/// Each modifier is associated with a [KeyCode] from JavaFX.
public enum KeyModifier {
    CONTROL(KeyCode.CONTROL) {
        @Override
        public String toString() {
            return OSUtils.os() == OS.Mac ? "⌃" : "Ctrl";
        }
    },
    ALT(KeyCode.ALT) {
        @Override
        public String toString() {
            return OSUtils.os() == OS.Mac ? "⌥" : "Alt";
        }
    },
    SHIFT(KeyCode.SHIFT) {
        @Override
        public String toString() {
            return OSUtils.os() == OS.Mac ? "⇧" : "Shift";
        }
    },
    META(KeyCode.META) {
        @Override
        public String toString() {
            return switch (OSUtils.os()) {
                case Mac -> "⌘";
                case Windows -> "Win";
                default -> "Meta";
            };
        }
    },
    SHORTCUT(OSUtils.os() == OS.Mac ? KeyCode.META : KeyCode.CONTROL) {
        @Override
        public String toString() {
            return OSUtils.os() == OS.Mac ? "⌘" : "Ctrl";
        }
    },
    ;

    static final Map<KeyCode, KeyModifier> KEY_MODIFIERS = Map.of(
        KeyCode.ALT, ALT,
        KeyCode.CONTROL, CONTROL,
        KeyCode.META, META,
        KeyCode.SHIFT, SHIFT,
        KeyCode.SHORTCUT, SHORTCUT
    );
    final KeyCode keyCode;

    KeyModifier(KeyCode keyCode) {
        this.keyCode = keyCode;
    }

    public KeyCode keyCode() {
        return keyCode;
    }

    /// @return a [KeyModifier] parsed from the given string, or `null` if the input is invalid
    public static KeyModifier fromString(String s) {
        if ("win".equalsIgnoreCase(s) || "cmd".equalsIgnoreCase(s)) return META;
        if ("ctrl".equalsIgnoreCase(s)) return CONTROL;
        if ("shortcut".equalsIgnoreCase(s)) return SHORTCUT;
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /// @return a the [KeyModifier] associated with the given [KeyCode] or throws if the given argument is invalid
    public static KeyModifier fromKeyCode(KeyCode keyCode) {
        return Optional.ofNullable(KEY_MODIFIERS.get(keyCode))
            .orElseThrow(() -> new IllegalArgumentException("No KeyModifier found for KeyCode: " + keyCode));
    }
}
