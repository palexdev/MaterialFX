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

package io.github.palexdev.mfxcore.popups.menu;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.utils.Functions;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

/// This class represents the base and preset type of content for any [MFXMenu]. Extends [MFXControl], and has [MFXMenuContentBehavior]
/// and [MFXMenuContentSkin] as its default behavior and skin implementations. It also implements [MFXStyleable], the
/// default style class to select this from CSS is: `.menu-content`.
///
/// It also allows you to specify a [Node] to show when the menu is empty through the [#placeholderSupplierProperty()].
public class MFXMenuContent extends MFXControl implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXMenu menu;
    private final SupplierProperty<Node> placeholderSupplier = new SupplierProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContent(MFXMenu menu) {
        this.menu = menu;
        setDefaultStyleClasses();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXMenuContentBehavior(this);
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXMenuContentSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("menu-content");
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty spacing = new StyleableDoubleProperty(
        StyleableProperties.SPACING,
        this,
        "spacing",
        0.0
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    public double getSpacing() {
        return spacing.get();
    }

    /// Specifies the spacing between each entry in the menu.
    ///
    /// Can be set from CSS via the property: '-mfx-spacing'.
    public StyleableDoubleProperty spacingProperty() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing.set(spacing);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXMenuContent> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXMenuContent, Number> SPACING =
            FACTORY.createSizeCssMetaData(
                "-mfx-spacing",
                MFXMenuContent::spacingProperty,
                0.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                SPACING
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the [MFXMenu] instance to which this content is associated to.
    public MFXMenu getMenu() {
        return menu;
    }

    public Supplier<Node> getPlaceholderSupplier() {
        return placeholderSupplier.get();
    }

    /// This property can be used to specify a [Node] to be shown when the menu is empty.<br >
    /// We use a [Supplier] so that the placeholder is created lazily, only when needed.<br >
    /// If your placeholder is always the same (likely to be so), it's recommended to use a caching [Supplier], see
    /// [Functions#cachedSupplier(Supplier)].
    public SupplierProperty<Node> placeholderSupplierProperty() {
        return placeholderSupplier;
    }

    public void setPlaceholderSupplier(Supplier<Node> placeholderSupplier) {
        this.placeholderSupplier.set(placeholderSupplier);
    }
}
