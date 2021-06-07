/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.validation.base;

import javafx.scene.Node;

import java.util.function.Supplier;

/**
 * Interface that specifies the methods all validated controls should have.
 *
 * @param <T> The validator type, extends {@link AbstractMFXValidator}
 */
public interface Validated<T extends AbstractMFXValidator> {
    /**
     * @return the validator instance
     */
    T getValidator();

    /**
     * Replaces the control's default validator with a user's specified one.
     * <p></p>
     * <b>
     * N.B: This method must be called before the control is laid out in the scene,
     * otherwise the validation system will most likely be broken.
     * <p>
     * A good usage would be immediately after the constructor or in the initialize block of controllers.
     * </b>
     *
     * @return the control's instance
     */
    Node installValidator(Supplier<T> validatorSupplier);

    /**
     * @return true if the validator is null or its state is valid. False if its state is invalid
     */
    default boolean isValid() {
        return (getValidator() == null || getValidator().isValid());
    }
}
