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

import java.util.Arrays;

public class ByteArraySetting extends Setting<byte[]> {

    //================================================================================
    // Constructors
    //================================================================================
    public ByteArraySetting(String name, String description, byte[] defaultValue, Settings container) {
        super(name, description, defaultValue, container);
    }

    public static ByteArraySetting of(String name, String description, byte[] defaultValue, Settings container) {
        return new ByteArraySetting(name, description, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public byte[] get() {
        return container.prefs().getByteArray(name, defaultValue);
    }

    @Override
    protected boolean write(byte[] val) {
        if (Arrays.equals(val, get())) return false;
        container.prefs.putByteArray(name, val);
        return true;
    }
}
