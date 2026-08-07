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

package io.github.palexdev.mfxresources.icon.packs;

import java.util.Properties;

import javafx.scene.text.Font;

/// Specialization of [FontIconsPack] that automatically loads both the icon pack and the relative [Font]
/// as specified by the abstract methods [#loadIcons()] and [#loadFont()].
///
/// The peculiarity here is that icons are expected to be loaded from a `.properties` file into a [Properties] object
/// which is just a map in disguise. This means that we get super fast lookups.
public abstract class PropertiesFontIconsPack implements FontIconsPack {
    //================================================================================
    // Properties
    //================================================================================
    protected final Properties map;
    private final Font font;

    //================================================================================
    // Constructors
    //================================================================================
    protected PropertiesFontIconsPack() {
        map = loadIcons();
        font = loadFont();
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    protected abstract Properties loadIcons();

    protected abstract Font loadFont();

    /// Specifies the initial capacity of the [Properties]'s map. Since icons are loaded from a file, we suppose that their
    /// number is known at compile-time. Setting the map's capacity to such a number beforehand is a nice optimization,
    /// especially for big packs.
    protected abstract int capacity();

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public String[] iconNames() {
        return map.keySet().toArray(String[]::new);
    }

    @Override
    public String icon(String name) {
        return (String) map.get(name);
    }

    @Override
    public Font font() {
        return font;
    }
}
