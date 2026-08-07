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

package io.github.palexdev.mfxcomponents.controls;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.skins.MFXFabSkin;
import io.github.palexdev.mfxcomponents.variants.FABVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.FABVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.icon.IconProperty;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Implementation of Material Design's 'Floating Action Buttons'. Extends [MFXButtonBase], uses [MFXFabSkin]
/// and [MFXButtonBehavior] as its default skin and behavior. The default CSS style class is `.mfx-fab`.
///
/// As stated by the Material Design specs, FABs are highly emphasized buttons that should be used for the most common
/// or important action on a screen. Because they are intended to be used with icons, it is enforced by the [#iconProperty()]
/// and the default skin.
///
/// Similar to other buttons, this also has different variants which determine its size and look. They are implemented
/// through the [WithVariants] API. These are the available configs:
/// - [StyleVariant] determines the colors, can be set through [#setStyle(StyleVariant)]
/// - [SizeVariant] determines preset sizes, can be set through [#setSize(SizeVariant)] (!warning below!)
/// The defaults are applied by [#defaultVariants()] upon initialization.
///
/// Additionally, FABs can be configured to show only the icon or both by setting the [#extendedProperty()].
///
/// #### Animations, sizing and caveats
///
/// FABs design is complex. The animations shown by the specs are hard to implement correctly in JavaFX.
/// - Other components use `-fx-min-<width | height>` to adhere to the sizes specified by the specs. That can't be done here.
///   To transition the FAB from standard to extended and vice versa, we can't set any bound on it. To overcome this,
///   I added a new CSS property [#minSizeProperty()] which is used by the skin to correctly compute the FAB's sizes.
///   It is driven by the size variants and is read-only from code (see its own docs). The [#prefWidthProperty()] is
///   used for the transition.
/// - The label's truncation mechanism is completely disabled, otherwise the animation looks bad, see [Label]
/// - You can't align the label, it is always positioned at the center. Not only that, it is also translated according to
///   the [#extendedProperty()] state. When the FAB is not extended, we need the icon to be centered!
/// (The default skin automatically handles these issues by the way)
public class MFXFab extends MFXButtonBase implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXFab> variantsHandler = new VariantsHandler<>(this);
    private final IconProperty icon = new IconProperty(new MFXFontIcon());

    //================================================================================
    // Constructors
    //================================================================================

    public MFXFab() {
        this("Action");
    }

    public MFXFab(String text) {
        super(text);
    }

    public MFXFab(String text, MFXFontIcon icon) {
        super(text);
        setIcon(icon);
    }

    {
        defaultVariants();
        graphicProperty().bind(icon);
        setPickOnBounds(false);
    }

    //================================================================================
    // Configs
    //================================================================================

    public MFXFab setStyle(StyleVariant style) {
        variantsHandler.setVariant(style);
        return this;
    }

    public MFXFab setSize(SizeVariant size) {
        variantsHandler.setVariant(size);
        return this;
    }

    /// Applies the default variants to the fab:
    /// - [StyleVariant#PRIMARY]
    /// - [SizeVariant#S]
    @Override
    public MFXFab defaultVariants() {
        variantsHandler.setVariants(StyleVariant.PRIMARY, SizeVariant.S);
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
        return () -> new MFXFabSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-fab");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty extended = new StyleableBooleanProperty(
        StyleableProperties.EXTENDED,
        this,
        "extended",
        false
    ) {
        @Override
        protected void invalidated() {
            PseudoClasses.EXTENDED.setOn(MFXFab.this, get());
        }
    };

    private final StyleableObjectProperty<Size> minSize = SizeProperty.styleableProperty(
        StyleableProperties.MIN_SIZE,
        this,
        "minSize",
        Size.zero()
    );

    public boolean isExtended() {
        return extended.get();
    }

    /// Specifies whether to extend the FAB to also show the text or not.
    ///
    /// This also de-/activated the ':extended' pseudo state on the FAB.
    ///
    /// Can be set from CSS via the property: '-mfx-extended'.
    public StyleableBooleanProperty extendedProperty() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended.set(extended);
    }

    public Size getMinSize() {
        return minSize.get();
    }

    /// Specifies the FAB's minimum sizes.
    ///
    /// This is a workaround to not set the JavaFX's min width and height because that would prevent width animations
    /// from playing.
    ///
    /// (Note on why: even if we override computeMinWidth(...) in the skin, the parent node responsible for laying out
    /// the FAB will still use the value from [#minWidthProperty()] thus interfering with animations)
    ///
    /// This is meant to be driven exclusively by the size variants through CSS ('-mfx-min-size'), which is why it is
    /// exposed as read-only. The skin only re-syncs the FAB's layout on variant changes (see [#setSize(SizeVariant)]),
    /// so bypassing that path would leave the layout stale until the next unrelated CSS/layout pass. Subclasses that
    /// really need to force a value can still use the protected [#setMinSize(Size)].
    ///
    /// @see SizeProperty#styleableProperty(CssMetaData, Object, String, Size)
    public ReadOnlyObjectProperty<Size> minSizeProperty() {
        return minSize;
    }

    protected void setMinSize(Size minSize) {
        this.minSize.set(minSize);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXFab> FACTORY = new StyleablePropertyFactory<>(MFXButtonBase.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXFab, Boolean> EXTENDED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-extended",
                MFXFab::extendedProperty,
                false
            );

        private static final CssMetaData<MFXFab, Size> MIN_SIZE = SizeProperty.cssMetaData(
            "-mfx-min-size",
            fab -> fab.minSize,
            Size.zero()
        );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXButtonBase.getClassCssMetaData(),
                EXTENDED, MIN_SIZE
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

    public MFXFontIcon getIcon() {
        return icon.get();
    }

    /// Specifies the FAB's icon.
    public IconProperty iconProperty() {
        return icon;
    }

    public void setIcon(MFXFontIcon icon) {
        this.icon.set(icon);
    }

    public void setIcon(String name) {
        this.icon.setIcon(name);
    }
}
