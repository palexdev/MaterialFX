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

import io.github.palexdev.mfxcomponents.controls.base.MFXToggle;
import io.github.palexdev.mfxcomponents.skins.MFXIconButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.WidthVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxresources.icon.IconProperty;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

public class MFXIconButton extends MFXButton {
    //================================================================================
    // Properties
    //================================================================================
    private final IconProperty icon = new IconProperty();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXIconButton() {
        this(new MFXFontIcon("mfx-logo-alt"));
    }

    public MFXIconButton(MFXFontIcon icon) {
        setIcon(icon);
    }

    {
        graphicProperty().bind(icon);
    }

    //================================================================================
    // Config
    //================================================================================

    /// Overridden because icon buttons do not support [StyleVariant#ELEVATED] and [StyleVariant#TEXT].
    /// When any of these is given as the argument, the variant is unset and the button falls back to the standard style.
    @Override
    public MFXIconButton setStyle(StyleVariant style) {
        if (style == StyleVariant.ELEVATED || style == StyleVariant.TEXT) {
            variantsHandler.unsetVariant(StyleVariant.class);
        } else {
            super.setStyle(style);
        }
        return this;
    }

    @Override
    public MFXIconButton setSize(SizeVariant size) {
        super.setSize(size);
        return this;
    }

    @Override
    public MFXIconButton setShape(ShapeVariant shape) {
        super.setShape(shape);
        return this;
    }

    /// Note: similar to the standard style (for which no style class is added), [WidthVariant#DEFAULT] will simply
    /// unset the variant (there's no `.default` style class).
    public MFXIconButton setWidth(WidthVariant width) {
        if (width == WidthVariant.DEFAULT) {
            variantsHandler.unsetVariant(WidthVariant.class);
        } else {
            variantsHandler.setVariant(width);
        }
        return this;
    }

    /// Applies the default variants to the button:
    /// - [SizeVariant#S]
    /// - [ShapeVariant#ROUNDED]
    @Override
    public MFXIconButton defaultVariants() {
        variantsHandler.setVariants(SizeVariant.S, ShapeVariant.ROUNDED);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXIconButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-icon-button");
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public MFXFontIcon getIcon() {
        return icon.get();
    }

    /// Specifies the button's font icon.
    public IconProperty iconProperty() {
        return icon;
    }

    public void setIcon(MFXFontIcon icon) {
        this.icon.set(icon);
    }

    public MFXIconButton setIcon(String name) {
        icon.setIcon(name);
        return this;
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    public static class MFXToggleIconButton extends MFXToggle implements WithVariants {
        private final IconProperty icon = new IconProperty();
        protected final VariantsHandler<MFXToggleIconButton> variantsHandler = new VariantsHandler<>(this);

        public MFXToggleIconButton() {
            this(new MFXFontIcon("mfx-logo-alt"));
        }

        public MFXToggleIconButton(MFXFontIcon icon) {
            setIcon(icon);
        }

        {
            defaultVariants();
            graphicProperty().bind(icon);
        }

        /// Overridden because icon buttons do not support [StyleVariant#ELEVATED] and [StyleVariant#TEXT].
        /// When any of these is given as the argument, the variant is unset and the button falls back to the standard style.
        public MFXToggleIconButton setStyle(StyleVariant style) {
            if (style == StyleVariant.ELEVATED || style == StyleVariant.TEXT) {
                variantsHandler.unsetVariant(StyleVariant.class);
            } else {
                variantsHandler.setVariant(style);
            }
            return this;
        }

        public MFXToggleIconButton setSize(SizeVariant size) {
            variantsHandler.setVariant(size);
            return this;
        }

        public MFXToggleIconButton setShape(ShapeVariant shape) {
            variantsHandler.setVariant(shape);
            return this;
        }

        /// Note: similar to the standard style (for which no style class is added), [WidthVariant#DEFAULT] will simply
        /// unset the variant (there's no `.default` style class).
        public MFXToggleIconButton setWidth(WidthVariant width) {
            if (width == WidthVariant.DEFAULT) {
                variantsHandler.unsetVariant(WidthVariant.class);
            } else {
                variantsHandler.setVariant(width);
            }
            return this;
        }

        /// Applies the default variants to the button:
        /// - [SizeVariant#S]
        /// - [ShapeVariant#ROUNDED]
        @Override
        public MFXToggleIconButton defaultVariants() {
            variantsHandler.setVariants(SizeVariant.S, ShapeVariant.ROUNDED);
            return this;
        }

        @Override
        public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
            return () -> new MFXIconButtonSkin(this);
        }

        @Override
        public List<String> defaultStyleClasses() {
            return styleClasses("mfx-icon-button", "toggle");
        }

        @Override
        public ObservableMap<Class<?>, Variant> getAppliedVariants() {
            return variantsHandler.getAppliedVariantsUnmodifiable();
        }

        public MFXFontIcon getIcon() {
            return icon.get();
        }

        public IconProperty iconProperty() {
            return icon;
        }

        public void setIcon(MFXFontIcon icon) {
            this.icon.set(icon);
        }

        public MFXToggleIconButton setIcon(String name) {
            icon.setIcon(name);
            return this;
        }
    }
}
