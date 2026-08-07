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

import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;

/// Public API for all components that want to integrate with the new Skin API.
public interface MFXSkinnable extends Skinnable {

    /// Shortcut for `setSkin(getSkinFactory().get())`.
    ///
    /// Sometimes, you may want to initialize the skin of a control before it is shown in a Scene.
    /// For example, for performance reasons, you may want to create the skin of a popup's content before the popup shows,
    /// to avoid any possible delay due to the skin instantiation and first layout.
    default void preloadSkin() {
        Skin<?> skin = getSkin();
        if (skin == null) setSkin(getSkinFactory().get());
    }

    /// @return a [Supplier] that is the factory for the default skin used by the component.
    Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory();

    default Supplier<MFXSkinBase<? extends Node>> getSkinFactory() {
        return skinFactoryProperty().get();
    }

    /// Specifies the [Supplier] used to produce a skin object for the component.
    SupplierProperty<MFXSkinBase<? extends Node>> skinFactoryProperty();

    default void setSkinFactory(Supplier<MFXSkinBase<? extends Node>> factory) {
        skinFactoryProperty().set(factory);
    }

    /// Restores the component's skin to the default one using [#defaultSkinFactory()] and [#setSkinFactory(Supplier)].
    default void setDefaultSkinFactory() {
        setSkinFactory(defaultSkinFactory());
    }
}
