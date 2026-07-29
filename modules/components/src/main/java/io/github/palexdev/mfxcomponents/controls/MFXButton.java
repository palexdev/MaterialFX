/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXToggle;
import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Custom implementation of a button which extends [MFXButtonBase], uses [MFXButtonSkin] and [MFXButtonBehavior] as its
/// default skin and behavior. The default CSS style class is `.mfx-button`.
///
/// Material 3 specs show a vast number of configurations for standard buttons. Most of them determine the look of the
/// component and are implemented through the [WithVariants] API. These are the available configs:
/// - [StyleVariant] defines the colors, can be set through [#setStyle(StyleVariant)]
/// - [SizeVariant] defines preset sizes, can be set through [#setSize(SizeVariant)]
/// - [ShapeVariant] defines the shape/radius, can be set through [#setShape(ShapeVariant)]
/// The defaults are applied by [#defaultVariants()] upon initialization.
///
/// If you want the toggleable variant, use [MFXToggleButton].
// TODO add support for emphasized text through Variants
public class MFXButton extends MFXButtonBase implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    protected final VariantsHandler<MFXButton> variantsHandler = new VariantsHandler<>(this);

    //================================================================================
    // Constructors
    //================================================================================

    public MFXButton() {
        this("Button");
    }

    public MFXButton(String text) {
        super(text);
    }

    public MFXButton(String text, Node graphic) {
        super(text, graphic);
    }

    {
        defaultVariants();
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPickOnBounds(false);
    }

    //================================================================================
    // Config
    //================================================================================

    public MFXButton setStyle(StyleVariant style) {
        variantsHandler.setVariant(style);
        return this;
    }

    public MFXButton setSize(SizeVariant size) {
        variantsHandler.setVariant(size);
        return this;
    }

    public MFXButton setShape(ShapeVariant shape) {
        variantsHandler.setVariant(shape);
        return this;
    }

    /// Applies the default variants to the button:
    /// - [StyleVariant#ELEVATED]
    /// - [SizeVariant#S]
    /// - [ShapeVariant#ROUNDED]
    @Override
    public MFXButton defaultVariants() {
        variantsHandler.setVariants(StyleVariant.ELEVATED, SizeVariant.S, ShapeVariant.ROUNDED);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXButtonBehavior<>(this);
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-button");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /// Extension of [MFXToggle] which uses the same behavior and skin as [MFXButton]. It even has the same variants:
    /// [ButtonVariants].
    public static class MFXToggleButton extends MFXToggle implements WithVariants {
        protected final VariantsHandler<MFXToggleButton> variantsHandler = new VariantsHandler<>(this);

        public MFXToggleButton() {
            this("Toggle Button");
        }

        public MFXToggleButton(String text) {
            super(text);
        }

        public MFXToggleButton(String text, Node graphic) {
            super(text, graphic);
        }

        {
            defaultVariants();
            setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setPickOnBounds(false);
        }

        public MFXToggleButton setStyle(StyleVariant style) {
            variantsHandler.setVariant(style);
            return this;
        }

        public MFXToggleButton setSize(SizeVariant size) {
            variantsHandler.setVariant(size);
            return this;
        }

        public MFXToggleButton setShape(ShapeVariant shape) {
            variantsHandler.setVariant(shape);
            return this;
        }

        /// Applies the default variants to the button:
        /// - [StyleVariant#ELEVATED]
        /// - [SizeVariant#S]
        /// - [ShapeVariant#ROUNDED]
        public MFXToggleButton defaultVariants() {
            variantsHandler.setVariants(StyleVariant.ELEVATED, SizeVariant.S, ShapeVariant.ROUNDED);
            return this;
        }

        @Override
        public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
            return () -> new MFXButtonSkin(this);
        }

        @Override
        public List<String> defaultStyleClasses() {
            return styleClasses("mfx-button", "toggle");
        }

        @Override
        public ObservableMap<Class<?>, Variant> getAppliedVariants() {
            return variantsHandler.getAppliedVariantsUnmodifiable();
        }
    }
}
