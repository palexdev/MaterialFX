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

import io.github.palexdev.mfxresources.MFXResources;
import javafx.scene.text.Font;

/// Icon pack for [FontAwesome Solid Icons](https://fontawesome.com/search?ip=classic&s=solid&o=r).
///
/// Extends [PropertiesFontIconsPack] as icons are loaded from a properties file in `MFXResources` assets.
///
/// There are currently 1390 icons in this pack.
public class FontAwesomeSolid extends PropertiesFontIconsPack {
    //================================================================================
    // Singleton
    //================================================================================
    private static final FontAwesomeSolid INSTANCE = new FontAwesomeSolid();

    public static FontAwesomeSolid instance() {
        return INSTANCE;
    }

    //================================================================================
    // Static Properties
    //================================================================================
    public static final int size = 1390;

    //================================================================================
    // Constructors
    //================================================================================
    private FontAwesomeSolid() {
        if (map.size() != size)
            throw new RuntimeException(
                "Size does not match capacity, expected: %s, is: %s"
                    .formatted(map.size(), size)
            );
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Properties loadIcons() {
        try {
            Properties p = new Properties();
            p.load(MFXResources.loadFont("FontAwesome/solid/FontAwesomeSolid.properties"));
            return p;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Font loadFont() {
        return Font.loadFont(
            MFXResources.loadFont("FontAwesome/solid/FontAwesomeSolid.ttf"),
            DEFAULT_FONT_SIZE
        );
    }

    @Override
    protected int capacity() {
        return size;
    }
}
