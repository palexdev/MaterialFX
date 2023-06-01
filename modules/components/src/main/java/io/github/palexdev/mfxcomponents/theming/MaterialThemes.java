/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.theming;

import io.github.palexdev.mfxcomponents.theming.base.Theme;
import io.github.palexdev.mfxresources.MFXResources;

import java.io.InputStream;

/**
 * Enumeration of all the Material Design 3 themes currently offered by MaterialFX. Implements {@link Theme}.
 */
public enum MaterialThemes implements Theme {
    INDIGO_LIGHT("themes/material/md-indigo-light.css"),
    INDIGO_DARK("themes/material/md-indigo-dark.css"),
    PURPLE_LIGHT("themes/material/md-purple-light.css"),
    PURPLE_DARK("themes/material/md-purple-dark.css"),
    ;

    private final String path;

    MaterialThemes(String path) {
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream assets() {
        return MFXResources.loadStream("mfx-assets.zip");
    }

    @Override
    public String deployName() {
        return "mfx-assets";
    }
}
