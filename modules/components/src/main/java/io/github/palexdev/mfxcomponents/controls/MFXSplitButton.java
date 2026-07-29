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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.skins.MFXSplitButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.properties.base.ExtendedProperty;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu.MenuConfig;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.popups.menu.MenuBuilder;
import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;

import static io.github.palexdev.mfxcore.base.beans.Position.PositionBuilder.y;
import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Implementation of Material Design's 'Split Buttons'. Extends [MFXButton], uses [MFXSplitButtonSkin] and
/// [MFXButtonBehavior] as its default skin and behavior. The default CSS style class is `.mfx-split-button`.
///
/// Split buttons pair a primary action with a set of related, secondary ones. The component is visually made of two
/// halves, both built and managed by the default skin: the leading one behaves like a standard button and carries the
/// [#textProperty()], the [#graphicProperty()] and the [#onActionProperty()]; the trailing one shows an icon and opens
/// a menu with the other options.
///
/// Like other buttons, the look is determined by variants, implemented through the [WithVariants] API. These are the
/// available configs:
/// - [StyleVariant] defines the colors, can be set through [#setStyle(StyleVariant)]
/// - [SizeVariant] defines preset sizes, can be set through [#setSize(SizeVariant)]
/// The defaults are applied by [#defaultVariants()] upon initialization. Since the two halves are standard buttons,
/// keeping them in sync with the variants applied here is the skin's job, see [MFXSplitButtonSkin].
///
/// **Note:** [ButtonVariants.ShapeVariant] is not supported at all, as the two halves must keep the split shape, which
/// is why [#setShape(ButtonVariants.ShapeVariant)] throws an [UnsupportedOperationException]. [StyleVariant#TEXT] is not
/// supported either, but rather than failing it silently falls back to [StyleVariant#ELEVATED].
///
/// The menu opened by the trailing half is a [MFXMenu]. Its entries are specified by the [#getMenuItems()] list, or more
/// conveniently by [#addMenuItems(MFXMenuItem...)] and [#addMenuItems(MenuBuilder...)], while everything else about it
/// (trigger, placement, offset, ...) is specified by the [#menuConfigProperty()].
public class MFXSplitButton extends MFXButton {

    //================================================================================
    // Properties
    //================================================================================

    private final ExtendedProperty<MenuConfig> menuConfig = PropUtils.<MenuConfig>objectProperty().extended(
        MenuConfig.builder()
            .triggerButton(MouseButton.PRIMARY)
            .enableKeyTrigger(true)
            .offset(y(2.0))
            .styleableParent(this)
            .build()).reset();
    private final ObservableList<MFXMenuItem> menuItems = FXCollections.observableArrayList();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSplitButton() {
    }

    public MFXSplitButton(String text) {
        super(text);
    }

    public MFXSplitButton(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Config
    //================================================================================

    /// [StyleVariant#TEXT] is not supported and will fall back to [StyleVariant#ELEVATED].
    public MFXSplitButton setStyle(StyleVariant style) {
        if (style == StyleVariant.TEXT) style = StyleVariant.ELEVATED;
        return (MFXSplitButton) super.setStyle(style);
    }

    /// Shape variants are not supported, always throws an [UnsupportedOperationException].
    @Override
    public MFXButton setShape(ButtonVariants.ShapeVariant shape) {
        throw new UnsupportedOperationException("Shape variants are not supported for split buttons.");
    }

    /// Applies the default variants to the button:
    /// - [StyleVariant#ELEVATED]
    /// - [SizeVariant#S]
    @Override
    public MFXSplitButton defaultVariants() {
        variantsHandler.setVariants(StyleVariant.ELEVATED, SizeVariant.S);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Overridden to be a no-op. The component itself never fires action events, the leading button built by the skin
    /// does, since the [#onActionProperty()] is bound to it.
    @Override
    public void trigger() {
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXSplitButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-split-button");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public MenuConfig getMenuConfig() {
        return menuConfig.getValue();
    }

    /// Specifies the configuration of the [MFXMenu] built and installed on the trailing button by the default skin.
    ///
    /// Contrary to the stock [MenuConfig], the default value here uses [MouseButton#PRIMARY] as the trigger (menus
    /// anchored to a button are opened with a left click, not a right one), enables the keyboard trigger, offsets the
    /// menu by 2px on the y-axis, and sets this button as the menu's 'styleable parent', so that the theme's lookups
    /// are inherited correctly.
    ///
    /// This is an [ExtendedProperty], the default config can be restored at any time with [ExtendedProperty#reset()].
    public ExtendedProperty<MenuConfig> menuConfigProperty() {
        return menuConfig;
    }

    public void setMenuConfig(MenuConfig menuConfig) {
        this.menuConfig.set(menuConfig);
    }

    /// @return the list of secondary actions displayed by the menu
    public ObservableList<MFXMenuItem> getMenuItems() {
        return menuItems;
    }

    public MFXSplitButton addMenuItems(MFXMenuItem... items) {
        menuItems.addAll(items);
        return this;
    }

    /// Convenience method to add menu items from a series of [MenuBuilders][MenuBuilder]. Each of them is built and then
    /// the results are delegated to [#addMenuItems(MFXMenuItem...)].
    public MFXSplitButton addMenuItems(MenuBuilder... items) {
        return addMenuItems(Arrays.stream(items).map(MenuBuilder::build).toArray(MFXMenuItem[]::new));
    }
}
