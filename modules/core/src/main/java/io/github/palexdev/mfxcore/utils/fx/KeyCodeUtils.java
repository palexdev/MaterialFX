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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.HashMap;
import java.util.Map;

import io.github.palexdev.mfxcore.input.KeyModifier;
import javafx.scene.input.KeyCode;

public class KeyCodeUtils {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final Map<KeyCode, String> KEY_TO_NAME = new HashMap<>();
    private static final Map<String, KeyCode> NAME_TO_KEY = new HashMap<>();

    //================================================================================
    // Constructors
    //================================================================================
    static {
        // Special keys
        KEY_TO_NAME.put(KeyCode.ENTER, "Enter");
        KEY_TO_NAME.put(KeyCode.BACK_SPACE, "Backspace");
        KEY_TO_NAME.put(KeyCode.TAB, "Tab");
        KEY_TO_NAME.put(KeyCode.ESCAPE, "Esc");
        KEY_TO_NAME.put(KeyCode.SPACE, "Space");
        KEY_TO_NAME.put(KeyCode.DELETE, "Delete");
        KEY_TO_NAME.put(KeyCode.INSERT, "Insert");
        KEY_TO_NAME.put(KeyCode.PRINTSCREEN, "Print Screen");
        KEY_TO_NAME.put(KeyCode.CONTEXT_MENU, "Context Menu");

        // Navigation keys
        KEY_TO_NAME.put(KeyCode.PAGE_UP, "Page Up");
        KEY_TO_NAME.put(KeyCode.PAGE_DOWN, "Page Down");
        KEY_TO_NAME.put(KeyCode.END, "End");
        KEY_TO_NAME.put(KeyCode.HOME, "Home");
        KEY_TO_NAME.put(KeyCode.LEFT, "Left");
        KEY_TO_NAME.put(KeyCode.UP, "Up");
        KEY_TO_NAME.put(KeyCode.RIGHT, "Right");
        KEY_TO_NAME.put(KeyCode.DOWN, "Down");

        // Symbol keys
        KEY_TO_NAME.put(KeyCode.COMMA, ",");
        KEY_TO_NAME.put(KeyCode.MINUS, "-");
        KEY_TO_NAME.put(KeyCode.PERIOD, ".");
        KEY_TO_NAME.put(KeyCode.SLASH, "/");
        KEY_TO_NAME.put(KeyCode.SEMICOLON, ";");
        KEY_TO_NAME.put(KeyCode.EQUALS, "=");
        KEY_TO_NAME.put(KeyCode.OPEN_BRACKET, "[");
        KEY_TO_NAME.put(KeyCode.BACK_SLASH, "\\");
        KEY_TO_NAME.put(KeyCode.CLOSE_BRACKET, "]");
        KEY_TO_NAME.put(KeyCode.BACK_QUOTE, "`");
        KEY_TO_NAME.put(KeyCode.QUOTE, "'");

        KEY_TO_NAME.put(KeyCode.AMPERSAND, "&");
        KEY_TO_NAME.put(KeyCode.ASTERISK, "*");
        KEY_TO_NAME.put(KeyCode.QUOTEDBL, "\"");
        KEY_TO_NAME.put(KeyCode.LESS, "<");
        KEY_TO_NAME.put(KeyCode.GREATER, ">");
        KEY_TO_NAME.put(KeyCode.BRACELEFT, "{");
        KEY_TO_NAME.put(KeyCode.BRACERIGHT, "}");
        KEY_TO_NAME.put(KeyCode.AT, "@");
        KEY_TO_NAME.put(KeyCode.COLON, ":");
        KEY_TO_NAME.put(KeyCode.CIRCUMFLEX, "^");
        KEY_TO_NAME.put(KeyCode.DOLLAR, "$");
        KEY_TO_NAME.put(KeyCode.EURO_SIGN, "€");
        KEY_TO_NAME.put(KeyCode.EXCLAMATION_MARK, "!");
        KEY_TO_NAME.put(KeyCode.LEFT_PARENTHESIS, "(");
        KEY_TO_NAME.put(KeyCode.NUMBER_SIGN, "#");
        KEY_TO_NAME.put(KeyCode.PLUS, "+");
        KEY_TO_NAME.put(KeyCode.RIGHT_PARENTHESIS, ")");
        KEY_TO_NAME.put(KeyCode.UNDERSCORE, "_");

        // Digits
        KEY_TO_NAME.put(KeyCode.DIGIT0, "0");
        KEY_TO_NAME.put(KeyCode.DIGIT1, "1");
        KEY_TO_NAME.put(KeyCode.DIGIT2, "2");
        KEY_TO_NAME.put(KeyCode.DIGIT3, "3");
        KEY_TO_NAME.put(KeyCode.DIGIT4, "4");
        KEY_TO_NAME.put(KeyCode.DIGIT5, "5");
        KEY_TO_NAME.put(KeyCode.DIGIT6, "6");
        KEY_TO_NAME.put(KeyCode.DIGIT7, "7");
        KEY_TO_NAME.put(KeyCode.DIGIT8, "8");
        KEY_TO_NAME.put(KeyCode.DIGIT9, "9");

        // Letters
        KEY_TO_NAME.put(KeyCode.A, "A");
        KEY_TO_NAME.put(KeyCode.B, "B");
        KEY_TO_NAME.put(KeyCode.C, "C");
        KEY_TO_NAME.put(KeyCode.D, "D");
        KEY_TO_NAME.put(KeyCode.E, "E");
        KEY_TO_NAME.put(KeyCode.F, "F");
        KEY_TO_NAME.put(KeyCode.G, "G");
        KEY_TO_NAME.put(KeyCode.H, "H");
        KEY_TO_NAME.put(KeyCode.I, "I");
        KEY_TO_NAME.put(KeyCode.J, "J");
        KEY_TO_NAME.put(KeyCode.K, "K");
        KEY_TO_NAME.put(KeyCode.L, "L");
        KEY_TO_NAME.put(KeyCode.M, "M");
        KEY_TO_NAME.put(KeyCode.N, "N");
        KEY_TO_NAME.put(KeyCode.O, "O");
        KEY_TO_NAME.put(KeyCode.P, "P");
        KEY_TO_NAME.put(KeyCode.Q, "Q");
        KEY_TO_NAME.put(KeyCode.R, "R");
        KEY_TO_NAME.put(KeyCode.S, "S");
        KEY_TO_NAME.put(KeyCode.T, "T");
        KEY_TO_NAME.put(KeyCode.U, "U");
        KEY_TO_NAME.put(KeyCode.V, "V");
        KEY_TO_NAME.put(KeyCode.W, "W");
        KEY_TO_NAME.put(KeyCode.X, "X");
        KEY_TO_NAME.put(KeyCode.Y, "Y");
        KEY_TO_NAME.put(KeyCode.Z, "Z");

        // Numpad
        KEY_TO_NAME.put(KeyCode.NUMPAD0, "Num 0");
        KEY_TO_NAME.put(KeyCode.NUMPAD1, "Num 1");
        KEY_TO_NAME.put(KeyCode.NUMPAD2, "Num 2");
        KEY_TO_NAME.put(KeyCode.NUMPAD3, "Num 3");
        KEY_TO_NAME.put(KeyCode.NUMPAD4, "Num 4");
        KEY_TO_NAME.put(KeyCode.NUMPAD5, "Num 5");
        KEY_TO_NAME.put(KeyCode.NUMPAD6, "Num 6");
        KEY_TO_NAME.put(KeyCode.NUMPAD7, "Num 7");
        KEY_TO_NAME.put(KeyCode.NUMPAD8, "Num 8");
        KEY_TO_NAME.put(KeyCode.NUMPAD9, "Num 9");
        KEY_TO_NAME.put(KeyCode.MULTIPLY, "Num *");
        KEY_TO_NAME.put(KeyCode.ADD, "Num +");
        KEY_TO_NAME.put(KeyCode.SUBTRACT, "Num -");
        KEY_TO_NAME.put(KeyCode.DECIMAL, "Num .");
        KEY_TO_NAME.put(KeyCode.DIVIDE, "Num /");

        // Function keys
        KEY_TO_NAME.put(KeyCode.F1, "F1");
        KEY_TO_NAME.put(KeyCode.F2, "F2");
        KEY_TO_NAME.put(KeyCode.F3, "F3");
        KEY_TO_NAME.put(KeyCode.F4, "F4");
        KEY_TO_NAME.put(KeyCode.F5, "F5");
        KEY_TO_NAME.put(KeyCode.F6, "F6");
        KEY_TO_NAME.put(KeyCode.F7, "F7");
        KEY_TO_NAME.put(KeyCode.F8, "F8");
        KEY_TO_NAME.put(KeyCode.F9, "F9");
        KEY_TO_NAME.put(KeyCode.F10, "F10");
        KEY_TO_NAME.put(KeyCode.F11, "F11");
        KEY_TO_NAME.put(KeyCode.F12, "F12");

        // Lock keys
        KEY_TO_NAME.put(KeyCode.CAPS, "Caps Lock");
        KEY_TO_NAME.put(KeyCode.NUM_LOCK, "Num Lock");
        KEY_TO_NAME.put(KeyCode.SCROLL_LOCK, "Scroll Lock");

        // Media keys
        KEY_TO_NAME.put(KeyCode.PLAY, "Play");
        KEY_TO_NAME.put(KeyCode.PAUSE, "Pause");
        KEY_TO_NAME.put(KeyCode.STOP, "Stop");
        KEY_TO_NAME.put(KeyCode.VOLUME_UP, "Volume +");
        KEY_TO_NAME.put(KeyCode.VOLUME_DOWN, "Volume -");
        KEY_TO_NAME.put(KeyCode.MUTE, "Mute");

        // Edit keys
        KEY_TO_NAME.put(KeyCode.CUT, "Cut");
        KEY_TO_NAME.put(KeyCode.COPY, "Copy");
        KEY_TO_NAME.put(KeyCode.PASTE, "Paste");
        KEY_TO_NAME.put(KeyCode.UNDO, "Undo");
        KEY_TO_NAME.put(KeyCode.FIND, "Find");

        // REVERSE MAP
        KEY_TO_NAME.forEach((key, value) -> NAME_TO_KEY.put(value, key));
    }

    private KeyCodeUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// Retrieves the [KeyCode] associated with the given string or `null` if not found.
    public static KeyCode fromDisplayString(String s) {
        return NAME_TO_KEY.get(s);
    }

    /// Gets the display name for the given [KeyCode], or `null` if the key should not be displayed.
    ///
    /// Modifier keys are first parsed to [KeyModifier] and then converted to a string.
    public static String toDisplayString(KeyCode key) {
        if (key.isModifierKey()) {
            KeyModifier mod = KeyModifier.fromString(key.getName());
            return mod != null ? mod.toString() : "null";
        }
        return KEY_TO_NAME.getOrDefault(key, key.toString());
    }

    /// Checks the given [KeyCode] can be used as a shortcut key
    public static boolean isValidShortcutKey(KeyCode keyCode) {
        return KEY_TO_NAME.containsKey(keyCode);
    }
}
