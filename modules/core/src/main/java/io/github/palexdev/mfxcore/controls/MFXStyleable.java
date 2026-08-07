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

package io.github.palexdev.mfxcore.controls;

import java.util.Collections;
import java.util.List;

import javafx.css.Styleable;

import static io.github.palexdev.mfxcore.utils.CollectionUtils.list;

/// An extension of [Styleable] to provide any implementing component with a list of default style classes.<br >
/// MaterialFX components inheriting from [MFXControl] or [MFXLabeled] make use of this API.
public interface MFXStyleable extends Styleable {

    /// @return the list of default style classes for this component
    default List<String> defaultStyleClasses() {
        return Collections.emptyList();
    }

    /// Resets this [Styleable]'s style classes to the defaults, [#defaultStyleClasses()].
    default void setDefaultStyleClasses() {
        getStyleClass().setAll(defaultStyleClasses());
    }

    /// Convenience method to create a modifiable list of style classes.
    static List<String> styleClasses(String... styleClasses) {
        return list(styleClasses);
    }

    /// Convenience method to extend a list of style classes.
    static List<String> extend(List<String> base, String... styleClasses) {
        Collections.addAll(base, styleClasses);
        return base;
    }
}
