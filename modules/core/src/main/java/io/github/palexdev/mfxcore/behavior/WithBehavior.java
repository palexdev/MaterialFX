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

package io.github.palexdev.mfxcore.behavior;

import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import javafx.scene.Node;

/// Public API for all components that want to integrate with the new Behavior API.
public interface WithBehavior {

    /// @return the instance of the current behavior object
    MFXBehavior<? extends Node> getBehavior();

    /// @return a [Supplier] that is the factory for the default behavior used by the component.
    Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory();

    default Supplier<MFXBehavior<? extends Node>> getBehaviorFactory() {
        return behaviorFactoryProperty().get();
    }

    /// Specifies the [Supplier] used to produce a behavior object for the component.
    SupplierProperty<MFXBehavior<? extends Node>> behaviorFactoryProperty();

    default void setBehaviorFactory(Supplier<MFXBehavior<? extends Node>> factory) {
        behaviorFactoryProperty().set(factory);
    }

    /// Restores the component's behavior to the default one using [#defaultBehaviorFactory()]
    /// and [#setBehaviorFactory(Supplier)].
    default void setDefaultBehaviorFactory() {
        setBehaviorFactory(defaultBehaviorFactory());
    }
}
