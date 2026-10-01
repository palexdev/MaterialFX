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

package io.github.palexdev.mfxcore.settings;

import io.github.palexdev.mfxcore.utils.EnumUtils;

import static java.util.Objects.requireNonNull;

/// A [Setting] for enum values, stored by [Enum#name()]. Lookup is case-insensitive, see
/// [EnumUtils#valueOfIgnoreCase(Class, String)]. If the stored value matches no constant, the default value is returned.
public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    //================================================================================
    // Constructors
    //================================================================================

    public EnumSetting(String name, String description, E defaultValue, Settings container) {
        requireNonNull(defaultValue);
        super(name, description, defaultValue, container);
    }

    public static <E extends Enum<E>> EnumSetting<E> of(String name, String description, E defaultValue, Settings container) {
        return new EnumSetting<>(name, description, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public E get() {
        String sVal = container.prefs().get(name, defaultValue.name()).strip();
        try {
            return EnumUtils.valueOfIgnoreCase(defaultValue.getDeclaringClass(), sVal);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    @Override
    protected boolean write(E val) {
        if (val == get()) return false;
        container.prefs().put(name, val.name());
        return true;
    }
}
