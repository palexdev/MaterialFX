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

package io.github.palexdev.mfxcomponents.theming;

import io.github.palexdev.mcu.MaterialTheme;
import io.github.palexdev.mfxcore.controls.ThemeEngine.Stylesheet;
import io.github.palexdev.mfxcore.utils.StringUtils;
import io.github.palexdev.mfxresources.MFXResources;

/// The color schemes shipped by `MaterialFX`, each constant is backed by its own `.css` file generated from the Material
/// 3 color system.
///
/// Since this implements [Stylesheet], the constants can be handed to the engine as they are, see
/// [MFXThemeEngine#setColor(MaterialColors)].
///
/// These are just presets, of course. Custom schemes are supported as well, see
/// [MFXThemeEngine#setColor(MaterialTheme)].
public enum MaterialColors implements Stylesheet {
    BLUE,
    DEEP_ORANGE,
    GREEN,
    INDIGO,
    ORANGE,
    PINK,
    PURPLE,
    TEAL,
    YELLOW;

    @Override
    public String src() {
        return MFXResources.loadTheme("material/md-preset-" + name().replace("_", "-").toLowerCase() + ".css");
    }

    /// Overridden to return the constant's name in title case, so that it can be shown in the UI as it is.
    @Override
    public String toString() {
        return StringUtils.titleCase(name(), "_");
    }

    /// @return [#PURPLE], the baseline color scheme defined by the Material Design specs
    public static MaterialColors defaultColor() {
        return PURPLE;
    }
}
