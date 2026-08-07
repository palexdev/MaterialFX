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

package io.github.palexdev.mfxcore.validation;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.ReadOnlyBooleanProperty;

/// Interface that defines the public API every control needing validation should implement.
///
/// Note that this interface just tells the user that the control already offers a [MFXValidator] instance if needed.
public interface Validated {

    /// @return the [MFXValidator] instance of this control
    MFXValidator getValidator();

    /// Delegates to [getValidator().isValid()][MFXValidator#isValid()].
    default boolean isValid() {
        return getValidator().isValid();
    }

    /// Delegates to [getValidator().validProperty()][MFXValidator#validProperty()].
    default ReadOnlyBooleanProperty validProperty() {
        return getValidator().validProperty();
    }

    /// Delegates to [getValidator().validate()][MFXValidator#validate()].
    default List<Constraint> validate() {
        return getValidator() != null ? getValidator().validate() : Collections.emptyList();
    }
}
