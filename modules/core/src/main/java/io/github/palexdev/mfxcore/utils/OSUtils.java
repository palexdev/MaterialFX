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

package io.github.palexdev.mfxcore.utils;

import io.github.palexdev.mfxcore.enums.OS;

public class OSUtils {
    //================================================================================
    // Static Properties
    //================================================================================
    private static OS os;

    //================================================================================
    // Constructors
    //================================================================================
    private OSUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// Detects the OS on which the program is running by looking at the system's `os.name` property.
    ///
    /// The first request caches the value, making further calls immediate.
    public static OS os() {
        if (os == null) {
            String name = System.getProperty("os.name");
            os = switch (name) {
                case String s when s.contains("win") || s.contains("Win") -> OS.Windows;
                case String s when s.contains("nix") || s.contains("nux") -> OS.Linux;
                case String s when s.contains("mac") || s.contains("Mac") -> OS.Mac;
                default -> OS.Other;
            };
        }
        return os;
    }

}
