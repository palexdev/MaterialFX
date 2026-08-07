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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.Collection;
import java.util.Objects;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;

/// Utilities for JavaFX collections.
public class FXCollectionsUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private FXCollectionsUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================

    /// Unlike classes such as [ListProperty], the ones in [FXCollections] do not allow to observe whether the collection
    /// is empty or not.<br >
    /// This method creates an observable [BooleanExpression] for that purpose, and can be used on any
    /// collection implementing [Observable].
    public static <C extends Collection<?> & Observable> BooleanExpression isEmpty(C collection) {
        Objects.requireNonNull(collection);
        return new BooleanBinding() {
            {
                bind(collection);
            }

            @Override
            protected boolean computeValue() {
                return collection.isEmpty();
            }
        };
    }

    /// Unlike classes such as [ListProperty], the ones in [FXCollections] do not allow to observe the size of a collection.<br >
    /// This method creates an observable [IntegerExpression] for that purpose, and can be used on any
    /// collection implementing [Observable].
    public static <C extends Collection<?> & Observable> IntegerExpression size(C collection) {
        Objects.requireNonNull(collection);
        return new IntegerBinding() {
            {
                bind(collection);
            }

            @Override
            protected int computeValue() {
                return collection.size();
            }
        };
    }
}
