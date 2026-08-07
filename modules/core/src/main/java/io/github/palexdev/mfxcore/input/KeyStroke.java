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

import java.util.*;
import java.util.stream.Collectors;

import io.github.palexdev.mfxcore.enums.OS;
import io.github.palexdev.mfxcore.utils.OSUtils;
import io.github.palexdev.mfxcore.utils.fx.KeyCodeUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/// A simple record to represent a keyboard shortcut that is: zero or more [KeyModifiers][KeyModifier] and a generic
/// key pressed together at the same time.
///
/// Compared to the usual JavaFX spaghetti code, which should be [KeyCombination] and related subclasses, this is much
/// simpler, more "open".
///
/// There are two ways to build a shortcut:
/// 1) Via the constructors
/// 2) From a string, see [#fromString(String)]
///
/// Technically, there's also a third one, which is [#fromEvent(KeyEvent)], but that is more of a utility to check if
/// a [KeyEvent] corresponds to a certain [KeyStroke].
///
/// The [Key][KeyCode] for the shortcut can't be a modifier ([KeyCode#isModifierKey()]) and it should be a valid key
/// according to [KeyCodeUtils#isValidShortcutKey(KeyCode)].
///
/// To display the shortcut in the UI don't use `toString()`, but rather [#toDisplayString()].
///
/// #### Implementation Details
///
/// The `KeyStroke` class simplifies the definition of key combinations thanks to its simplicity and [KeyModifier] constants.
/// Unfortunately, there are discrepancies between how a combination is represented in text and which keys actually come
/// from the OS/JavaFX.<br >
/// One example is the [KeyModifier#SHORTCUT] modifier, which technically does not exist, and it's a special virtual key
/// from JavaFX which code depends on the OS. This means that if you register an action for a combination with such modifier
/// (for example, in [KeyMap]), you would never get an event that matches such combination. So, to fix this annoying issue,
/// the class stores the modifers both as [an enum set of `KeyModifiers`][#modifiers()] and [a set of `KeyCodes`][#modifiersAsCodes()].<br >
/// In comparisons (equals/hashCode), only the latter collection is taken into account, while in [#toDisplayString()] only
/// the first is used.
public final class KeyStroke {

    //================================================================================
    // Properties
    //================================================================================

    private final EnumSet<KeyModifier> modifiers;
    private final Set<KeyCode> modifiersToCodes;
    private final KeyCode key;

    //================================================================================
    // Constructors
    //================================================================================

    private KeyStroke(EnumSet<KeyModifier> modifiers, KeyCode key) {
        this.modifiers = modifiers;
        this.modifiersToCodes = modifiers.stream().map(KeyModifier::keyCode).collect(Collectors.toSet());
        this.key = key;
    }

    public KeyStroke(KeyCode key, KeyModifier... modifiers) {
        if (key.isModifierKey())
            throw new IllegalArgumentException("Cannot use modifier key as shortcut key: " + key);
        if (!KeyCodeUtils.isValidShortcutKey(key))
            throw new IllegalArgumentException("Invalid shortcut key: " + key);
        EnumSet<KeyModifier> set = EnumSet.noneOf(KeyModifier.class);
        Collections.addAll(set, modifiers);
        this(set, key);
    }

    /// Creates a new [KeyStroke] using only [KeyCode] objects, expects arrays in this format: `<modifier>...,<key>`<br >
    /// (key is the last in the sequence, modifiers can be zero or more).
    public static KeyStroke keyStroke(KeyCode... keys) {
        if (keys == null || keys.length == 0) throw new IllegalArgumentException("Keys cannot be null or empty");
        if (keys[keys.length - 1].isModifierKey()) throw new IllegalArgumentException("Last key cannot be a modifier");
        EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
        for (int i = 0; i < keys.length - 1; i++) {
            modifiers.add(KeyModifier.fromKeyCode(keys[i]));
        }
        return new KeyStroke(modifiers, keys[keys.length - 1]);
    }

    /// Creates a new [KeyStroke] from the given string. Here's the ideal format for the string:
    /// `<modifier>+<modifier>+...<key>` where:
    /// - Each key/modifier is separated by the sign `+`
    /// - Modifiers can be zero or more
    /// - The key cannot be omitted and must be specified as its display representation, compliant with
    /// [KeyCodeUtils] and [KeyCodeUtils#fromDisplayString(String)]
    ///
    /// @throws IllegalArgumentException if the string is `null` or empty
    /// @throws IllegalArgumentException if the key cannot be parsed from the string
    public static KeyStroke fromString(String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Key combo string cannot be null or empty");
        }
        String[] keys = s.trim().split("\\+");
        EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
        KeyCode code = null;
        for (String key : keys) {
            key = key.trim();
            KeyModifier mod;
            if ((mod = KeyModifier.fromString(key)) != null) {
                modifiers.add(mod);
                continue;
            }
            code = KeyCodeUtils.fromDisplayString(key);
        }
        if (code == null) {
            throw new IllegalArgumentException("Could not parse key from combo string: " + s);
        }
        return new KeyStroke(modifiers, code);
    }

    /// Convenience method to create a new [KeyStroke] object from a JavaFX [KeyEvent].
    /// This is useful if you want to check whether an event corresponds to your desired key combination.
    ///
    /// **Implementation Details**
    ///
    /// The only way to check which modifiers are active on a [KeyEvent] is to use the various query methods.
    /// ([KeyEvent#isAltDown()], [KeyEvent#isShiftDown()], etc...)
    /// This means that to create a [KeyStroke] from an event, there are a series of ifs here to add the right [KeyModifiers][KeyModifier].
    ///
    /// However, we can slightly optimize this by excluding events for which [KeyEvent#getText()] is `null` or empty.
    /// This is possible because [KeyEvents][KeyEvent] are fired for modifiers too. For example, if you press `SHIFT`,
    /// JavaFX will notify of such an event, but these are irrelevant for a keystroke.<br >
    /// We want to create a [KeyStroke] only when a "standard" key is pressed. So, to filter events, we check the
    /// `getText()` value, which apparently is `null` or empty for special keys (did not test in depth though!).
    public static KeyStroke fromEvent(KeyEvent ke) {
        if (ke.getText() == null || ke.getText().isEmpty())
            return null;

        EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
        if (ke.isAltDown()) modifiers.add(KeyModifier.ALT);
        if (ke.isControlDown()) modifiers.add(KeyModifier.CONTROL);
        if (ke.isShiftDown()) modifiers.add(KeyModifier.SHIFT);
        if (ke.isMetaDown()) modifiers.add(KeyModifier.META);
        return new KeyStroke(modifiers, ke.getCode());
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Converts this [KeyStroke] to a string which can be displayed in UI components.
    ///
    /// First it appends all modifiers, then the [KeyCode] converted with [KeyCodeUtils#toDisplayString(KeyCode)].
    /// Everything is separated by `+` signs.
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        String separator = OSUtils.os() == OS.Mac ? "" : "+";
        modifiers.forEach(m -> sb.append(m).append(separator));
        sb.append(KeyCodeUtils.toDisplayString(key));
        return sb.toString();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KeyStroke) obj;
        return Objects.equals(this.modifiersToCodes, that.modifiersToCodes) &&
               Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiersToCodes, key);
    }

    @Override
    public String toString() {
        return "KeyStroke[" +
               "modifiers=" + modifiers + ", " +
               "key=" + key + ']';
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public EnumSet<KeyModifier> modifiers() {
        return EnumSet.copyOf(modifiers);
    }

    public Set<KeyCode> modifiersAsCodes() {
        return modifiersToCodes;
    }

    public KeyCode key() {
        return key;
    }
}
