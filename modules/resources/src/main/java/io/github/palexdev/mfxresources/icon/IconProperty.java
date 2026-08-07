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

package io.github.palexdev.mfxresources.icon;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/// Simple extension of [SimpleObjectProperty] to be used for [MFXFontIcon] objects.
public class IconProperty extends SimpleObjectProperty<MFXFontIcon> {

    //================================================================================
    // Constructors
    //================================================================================
    public IconProperty() {}

    public IconProperty(MFXFontIcon initialValue) {
        super(initialValue);
    }

    public IconProperty(Object bean, String name) {
        super(bean, name);
    }

    public IconProperty(Object bean, String name, MFXFontIcon initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Setters
    //================================================================================
    public IconProperty setIcon(String name) {
        set(new MFXFontIcon(name));
        return this;
    }

    public IconProperty setSize(double size) {
        get().setSize(size);
        return this;
    }

    public IconProperty setColor(Color color) {
        get().setColor(color);
        return this;
    }
}
