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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.popups.MFXTooltip;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;

import static java.util.Optional.ofNullable;

/// Base class that can be used as a starting point to implement text-based UI components that perfectly integrate with the
/// new Behavior and Skin APIs, see [WithBehavior] and [MFXSkinnable]. It also implements the [MFXStyleable] interface.
///
/// The integration with the new Behavior API is achieved by having a specific property, [#behaviorFactoryProperty()],
/// which allows changing at any time the component's behavior. The property automatically handles creation, installation
/// and disposal of behaviors. A reference to the current built behavior object is kept to be retrieved via [#getBehavior()].
///
/// A behavior is created as soon as the factory is set, but it is installed ([MFXBehavior#install()]) only when the
/// first skin is created, see [#createDefaultSkin()]. When the factory changes after that, the old behavior is disposed,
/// and the new one is created and installed immediately. Skin changes never affect the behavior.
///
/// Enforces the use of [MFXSkinBase] instances as Skin implementations and makes the [#createDefaultSkin()] method final,
/// thus denying users to override it. Similar to the behavior, to set custom skins, you can:
///  - Use the factory property, [#skinFactoryProperty()]
///  - Override [#buildSkin()] **(not recommended)**
///  - Call [#setSkin(Skin)] directly **(absolutely not recommended)**. If the very first skin is set this way, the
///  behavior is never installed, since [#createDefaultSkin()] is bypassed
///
/// The skin factory is more of a convenience to the user that does not need to inline-override the method responsible for
/// creating the skin. The new mechanism is much more flexible and automatically integrates with the behavior API.<br >
/// As a consequence, components that inherit from this do not support the "-fx-skin" CSS property. You'll have to do it in code.
public abstract class MFXLabeled extends Labeled implements WithBehavior, MFXSkinnable, MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private MFXBehavior<? extends Node> behavior;
    private final SupplierProperty<MFXBehavior<? extends Node>> behaviorFactory = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            if (behavior != null) behavior.dispose();
            behavior = ofNullable(get()).map(Supplier::get).orElse(null);
            if (getSkin() != null && behavior != null) behavior.install();
        }
    };
    private final SupplierProperty<MFXSkinBase<? extends Node>> skinFactory = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            // Do not run if createDefaultSkin() has not been called yet.
            // The downside of this is that if setSkin(...) is called before, then the factory is ignored
            if (getSkin() != null)
                setSkin(buildSkin());

        }
    };

    private final ObjectProperty<MFXTooltip> tooltip = new SimpleObjectProperty<>() {
        @Override
        public void set(MFXTooltip newValue) {
            MFXTooltip old = get();
            if (old != null) old.uninstall();
            if (newValue != null) newValue.install(MFXLabeled.this);
            super.set(newValue);
        }

        @Override
        public MFXTooltip get() {
            return Optional.ofNullable(super.get())
                .orElseGet(() -> ((MFXTooltip) getProperties().get(MFXTooltip.PROP_KEY)));
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabeled() {}

    public MFXLabeled(String text) {
        super(text);
    }

    public MFXLabeled(String text, Node graphic) {
        super(text, graphic);
    }

    {
        setDefaultBehaviorFactory();
        setDefaultSkinFactory();
        setDefaultStyleClasses();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is the core method responsible for creating the component's skin when the [#skinFactoryProperty()] changes.
    ///
    /// Note that the very first skin instance is created by JavaFX with the usual [#createDefaultSkin()].
    ///
    /// @throws NullPointerException if either the skin factory or the produced skin are `null`
    protected MFXSkinBase<?> buildSkin() {
        return ofNullable(getSkinFactory())
            .map(Supplier::get)
            .orElseThrow(() -> new NullPointerException("Either the skin factory or the produced skin are null!"));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to go through [#createDefaultSkin()], so that the behavior is installed as it would be for a skin
    /// created by JavaFX. Does nothing if the control already has a skin.
    @Override
    public void preloadSkin() {
        if (getSkin() == null)
            setSkin(createDefaultSkin());
    }

    /// {@inheritDoc}
    ///
    /// Overridden to be final and to delegate to [#buildSkin()]. We still need this to initialize the component.
    ///
    /// This is also where the current behavior is installed. JavaFX calls this method only when the control has no skin,
    /// which guarantees that the behavior is installed once, together with the first skin. Later skin changes go through
    /// [#buildSkin()] only.
    @Override
    protected final MFXSkinBase<?> createDefaultSkin() {
        MFXSkinBase<?> skin = buildSkin();
        if (behavior != null) behavior.install();
        return skin;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty textOpacity = new StyleableDoubleProperty(
        StyleableProperties.TEXT_OPACITY,
        this,
        "textOpacity",
        1.0
    );

    public double getTextOpacity() {
        return textOpacity.get();
    }

    /// Specifies the text node's opacity (not the label itself!).
    ///
    /// _This will work only if the skin uses labels of type [Label]!_
    ///
    /// Can be set from CSS via the property: '-mfx-text-opacity'.
    public StyleableDoubleProperty textOpacityProperty() {
        return textOpacity;
    }

    public void setTextOpacity(double textOpacity) {
        this.textOpacity.set(textOpacity);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXLabeled> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXLabeled, Number> TEXT_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-text-opacity",
                MFXLabeled::textOpacityProperty,
                1.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Labeled.getClassCssMetaData(),
                TEXT_OPACITY
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

    @Override
    public MFXBehavior<? extends Node> getBehavior() {
        return behavior;
    }

    @Override
    public SupplierProperty<MFXBehavior<? extends Node>> behaviorFactoryProperty() {
        return behaviorFactory;
    }

    @Override
    public SupplierProperty<MFXSkinBase<? extends Node>> skinFactoryProperty() {
        return skinFactory;
    }

    public MFXTooltip tooltip() {
        return tooltip.get();
    }

    public ObjectProperty<MFXTooltip> tooltipProp() {
        return tooltip;
    }

    public void setTooltip(MFXTooltip tooltip) {
        this.tooltip.set(tooltip);
    }
}
