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
import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.MFXButton.MFXToggleButton;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton.MFXToggleIconButton;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXToggle;
import io.github.palexdev.mfxcomponents.skins.MFXButtonsGroupSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.*;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Implementation of the button group described in the Material 3 Expressive specification.<br>
/// Extends [MFXControl], the default skin is [MFXButtonsGroupSkin], and the default CSS style class is `.mfx-buttons-group`.
///
/// This is essentially a container for a collection of [buttons][MFXButtonBase]. Although nothing prevents you from mixing
/// different button types, the intended and recommended usage is to use either only standard buttons or only toggle buttons.
///
/// For all [toggle buttons][MFXToggle] added to the group, selection is managed by a [SelectionGroup], which can be
/// configured through delegate methods. By default, the selection mode is [SelectionMode#SINGLE], and
/// [SelectionGroup#atLeastOneSelectedProperty()] is `false`.
///
/// This class also implements [WithVariants]. The specification defines several configuration options, which fall into two categories:
/// - Configurations that apply to the group itself, such as [GroupVariant] and [SizeVariant] (inherited from [ButtonsConfig]).
/// - Configurations that apply to individual buttons, defined by [ButtonVariants].
///
/// For convenience, the latter are grouped into [ButtonsConfig], which can be set through
/// [#setButtonsConfig(ButtonsConfig)]. Whenever buttons are added to the group, the configuration is automatically
/// applied by [#updateGroup(ListChangeListener.Change)].
public class MFXButtonsGroup extends MFXControl implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXButtonsGroup> variantsHandler = new VariantsHandler<>(this);
    private ButtonsConfig buttonsConfig;

    private final ObservableList<MFXButtonBase> buttons = FXCollections.observableArrayList();
    private final SelectionGroup selectionGroup = new SelectionGroup();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXButtonsGroup() {
        defaultVariants();
        buttons.addListener(this::updateGroup);
    }

    //================================================================================
    // Config
    //================================================================================

    public MFXButtonsGroup setGroupType(GroupVariant type) {
        variantsHandler.setVariant(type);
        return this;
    }

    public ButtonsConfig getButtonsConfig() {
        return buttonsConfig;
    }

    public MFXButtonsGroup setButtonsConfig(ButtonsConfig config) {
        buttons.forEach(config::apply);
        variantsHandler.setVariant(config.size()); // We need this for the spacing
        this.buttonsConfig = config;
        return this;
    }

    /// Applies the default variants to the group and its buttons:
    /// - [GroupVariant#STANDARD]
    /// - [ButtonsConfig#DEFAULT]
    @Override
    public MFXButtonsGroup defaultVariants() {
        variantsHandler.setVariant(GroupVariant.STANDARD);
        setButtonsConfig(ButtonsConfig.DEFAULT);
        return this;
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to add all the given buttons to the group.
    public MFXButtonsGroup addButtons(MFXButtonBase... buttons) {
        this.buttons.addAll(buttons);
        return this;
    }

    /// This is responsible for updating the [SelectionGroup] by adding/removing buttons from it when the buttons' list changes.
    ///
    /// Note that for added buttons the [ButtonsConfig] is automatically applied.
    protected void updateGroup(ListChangeListener.Change<? extends MFXButtonBase> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(b -> {
                    if (b instanceof MFXToggle t)
                        t.setSelectionGroup(null);
                });
            } else if (change.wasAdded()) {
                change.getAddedSubList().forEach(b -> {
                    if (b instanceof MFXToggle t)
                        t.setSelectionGroup(selectionGroup);
                    buttonsConfig.apply(b);
                });
            }
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXBehavior<>(this) {};
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXButtonsGroupSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-buttons-group");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty spacing = new StyleableDoubleProperty(
        StyleableProperties.SPACING,
        this,
        "spacing",
        0.0
    );

    public double getSpacing() {return spacing.get();}

    /// Specifies the gap between each button in the group.
    ///
    /// Can be set from CSS via the property: `-mfx-spacing`.
    public StyleableDoubleProperty spacingProperty() {return spacing;}

    public void setSpacing(double spacing) {this.spacing.set(spacing);}

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXButtonsGroup> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXButtonsGroup, Number> SPACING =
            FACTORY.createSizeCssMetaData(
                "-mfx-spacing",
                MFXButtonsGroup::spacingProperty,
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

    public ObservableList<MFXButtonBase> getButtons() {
        return buttons;
    }

    /// Returns the currently selected button.
    ///
    /// Note that this is more of a convenience for when the selection mode is [SelectionMode#SINGLE].
    /// If the selection mode is [SelectionMode#MULTIPLE], then this will return the first selected button.
    public Optional<Selectable> getSelected() {return selectionGroup.getFirstSelected();}

    /// @return all the currently selected buttons in an observable collection
    public ReadOnlySetProperty<Selectable> getSelection() {return selectionGroup.getSelection();}

    /// @return all the currently selected buttons as a list
    public List<Selectable> getSelectionList() {return selectionGroup.getSelectionList();}

    /// @see SelectionGroup#getSelectedIndexes()
    public List<Integer> getSelectedIndexes() {return selectionGroup.getSelectedIndexes();}

    /// @see SelectionGroup#getSelectedIndex()
    public Integer getSelectedIndex() {return selectionGroup.getSelectedIndex();}

    public SelectionMode getSelectionMode() {return selectionGroup.getSelectionMode();}

    /// @see SelectionGroup#selectionModeProperty()
    public ObjectProperty<SelectionMode> selectionModeProperty() {return selectionGroup.selectionModeProperty();}

    public void setSelectionMode(SelectionMode mode) {selectionGroup.setSelectionMode(mode);}

    public boolean isAtLeastOneSelected() {return selectionGroup.isAtLeastOneSelected();}

    /// @see SelectionGroup#atLeastOneSelectedProperty()
    public BooleanProperty atLeastOneSelectedProperty() {return selectionGroup.atLeastOneSelectedProperty();}

    public void setAtLeastOneSelected(boolean atLeastOneSelected) {selectionGroup.setAtLeastOneSelected(atLeastOneSelected);}

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Convenience record for applying a combination of variants to all the buttons in the group.
    public record ButtonsConfig(
        ShapeVariant shape,
        SizeVariant size,
        StyleVariant style,
        WidthVariant width
    ) {
        /// The default buttons configuration:
        /// - [ShapeVariant#ROUNDED]
        /// - [SizeVariant#S]
        /// - [StyleVariant#FILLED]
        /// - [WidthVariant#DEFAULT] (for [MFXIconButton] only)
        public static final ButtonsConfig DEFAULT = ButtonsConfig.of(
            ShapeVariant.ROUNDED,
            SizeVariant.S,
            StyleVariant.FILLED,
            WidthVariant.DEFAULT
        );

        public static ButtonsConfig of(ShapeVariant shape, SizeVariant size, StyleVariant style, WidthVariant width) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// Applies the configuration to the given button.
        public void apply(MFXButtonBase btn) {
            switch (btn) {
                case MFXIconButton ib -> {
                    ib.setShape(shape);
                    ib.setSize(size);
                    ib.setStyle(style);
                    ib.setWidth(width);
                }
                case MFXToggleIconButton tib -> {
                    tib.setShape(shape);
                    tib.setSize(size);
                    tib.setStyle(style);
                    tib.setWidth(width);
                }
                case MFXButton b -> {
                    b.setShape(shape);
                    b.setSize(size);
                    b.setStyle(style);
                }
                case MFXToggleButton tb -> {
                    tb.setShape(shape);
                    tb.setSize(size);
                    tb.setStyle(style);
                }
                default -> {}
            }
        }

        /// @return a new configuration with the same values as this one but the given shape.
        public ButtonsConfig withShape(ShapeVariant shape) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given size.
        public ButtonsConfig withSize(SizeVariant size) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given style.
        public ButtonsConfig withStyle(StyleVariant style) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given width.
        public ButtonsConfig withWidth(WidthVariant width) {
            return new ButtonsConfig(shape, size, style, width);
        }
    }
}
