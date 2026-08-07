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

/// Icon pack for [FontAwesome Brands Icons](https://fontawesome.com/search?o=r&ip=brands).
///
/// Extends [PropertiesFontIconsPack] as icons are loaded from a properties file in `MFXResources` assets.
///
/// There are currently 465 icons in this pack.
public class FontAwesomeBrands extends PropertiesFontIconsPack {
    //================================================================================
    // Singleton
    //================================================================================
    private static final FontAwesomeBrands INSTANCE = new FontAwesomeBrands();

    public static FontAwesomeBrands instance() {
        return INSTANCE;
    }

    //================================================================================
    // Static Properties
    //================================================================================
    public static final int size = 465;

    //================================================================================
    // Constructors
    //================================================================================
    private FontAwesomeBrands() {
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
            p.load(MFXResources.loadFont("FontAwesome/brands/FontAwesomeBrands.properties"));
            return p;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Font loadFont() {
        return Font.loadFont(
            MFXResources.loadFont("FontAwesome/brands/FontAwesomeBrands.ttf"),
            DEFAULT_FONT_SIZE
        );
    }

    @Override
    protected int capacity() {
        return size;
    }
}
