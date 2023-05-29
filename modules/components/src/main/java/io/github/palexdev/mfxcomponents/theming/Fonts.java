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

/**
 * Enumeration of all the font stylesheets offered by MaterialFX. Implements {@link Theme}.
 */
public enum Fonts implements Theme {
    COMFORTAA("fonts/Comfortaa/Comfortaa.css"),
    OPEN_SANS("fonts/OpenSans/OpenSans.css"),
    ROBOTO("fonts/Roboto/Roboto.css");

    private final String path;

    Fonts(String path) {
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }
}
