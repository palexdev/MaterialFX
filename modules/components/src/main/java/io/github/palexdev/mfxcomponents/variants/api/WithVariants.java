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

package io.github.palexdev.mfxcomponents.variants.api;


import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableMap;

/// A simple interface indicating that a component supports variants.
///
/// They are handled internally by the [VariantsHandler] class.
public interface WithVariants {

    /// A quick way to apply the default variants. How and what are defined by implementations!
    default WithVariants defaultVariants() {
        return this;
    }

    /// @return an [ObservableMap] containing all the applied variants
    ObservableMap<Class<?>, Variant> getAppliedVariants();

    /// @return an applied [Variant] for the given enum class, or `null` if the variant is not applied.
    default <E extends Enum<?> & Variant> E getAppliedVariant(Class<E> klass) {
        return klass.cast(getAppliedVariants().get(klass));
    }

    /// @return whether the given variant is contained in [#getAppliedVariants()]
    default boolean isVariantApplied(Variant variant) {
        return Optional.ofNullable(getAppliedVariants().get(variant.getClass()))
            .map(v -> Objects.equals(v, variant))
            .orElse(false);
    }
}
