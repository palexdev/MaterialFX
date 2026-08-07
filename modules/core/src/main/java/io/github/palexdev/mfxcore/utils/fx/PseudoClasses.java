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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.css.PseudoClass;
import javafx.scene.Node;

/// This enumerator makes it easier to work with [PseudoClass], especially when custom ones are needed.
public enum PseudoClasses {
    DISABLED("disabled"),
    ERROR("error"),
    EXTENDED("extended"),
    FIRST("first"),
    FOCUSED("focused"),
    FOCUS_VISIBLE("focus-visible"),
    FOCUS_WITHIN("focus-within"),
    HOVER("hover"),
    INDETERMINATE("indeterminate"),
    LAST("last"),
    OPEN("open"),
    PRESSED("pressed"),
    SELECTABLE("selectable"),
    SELECTED("selected"),
    WITH_ICON_LEFT("with-icon-left"),
    WITH_ICON_RIGHT("with-icon-right"),
    ;

    private static final Map<String, PseudoClass> CACHE = new HashMap<>();

    private final String pseudoClassName;

    PseudoClasses(String pseudoClassName) {
        this.pseudoClassName = pseudoClassName;
    }

    /// Activates or deactivates the PseudoClass on the given node.
    public void setOn(Node node, boolean state) {
        node.pseudoClassStateChanged(
            CACHE.computeIfAbsent(pseudoClassName, PseudoClass::getPseudoClass),
            state
        );
    }

    /// Gets the [PseudoClass] associated with the given name and de-/activates it on the given node.
    public static void setOn(Node node, String pseudoClassName, boolean state) {
        node.pseudoClassStateChanged(
            CACHE.computeIfAbsent(pseudoClassName, PseudoClass::getPseudoClass),
            state
        );
    }

    /// @return true if the [PseudoClass] is active on the given node.
    public boolean isActiveOn(Node node) {
        return node.getPseudoClassStates().contains(
            CACHE.computeIfAbsent(pseudoClassName, PseudoClass::getPseudoClass)
        );
    }

    /// @return true if the [PseudoClass] associated with the given name is active on the given node.
    public static boolean isActiveOn(Node node, String pseudoClassName) {
        return node.getPseudoClassStates().contains(
            CACHE.computeIfAbsent(pseudoClassName, PseudoClass::getPseudoClass)
        );
    }

    /// @return true if any of the given [PseudoClasses][PseudoClass] are active on the given node.
    public static boolean isActiveOn(Node node, String... pseudoClassNames) {
        return Arrays.stream(pseudoClassNames)
            .anyMatch(name -> isActiveOn(node, name));
    }

    /// @return the name of the [PseudoClass].
    public String getPseudoClassName() {
        return pseudoClassName;
    }
}
