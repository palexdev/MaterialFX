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

import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/// Default skin implementation for [MFXMenuContent]. Extends [MFXSkinBase] and expects behaviors of type
/// [MFXMenuContentBehavior].
///
/// The layout is manual but very simple:
/// 1. Entries are positioned one below the other, spaced evenly by the [MFXMenuContent#spacingProperty()]
/// 2. The width is the maximum between the entries. The height is the sum of all the entries' heights including the gap.
/// 3. If the menu is empty, and the [MFXMenuContent#placeholderSupplierProperty()] produces a valid result, a placeholder
/// node is shown instead. This node can be selected from CSS via the style class: '.placeholder'
public class MFXMenuContentSkin extends MFXSkinBase<MFXMenuContent> {

    //================================================================================
    // Properties
    //================================================================================

    // Keep a reference to it for layout
    private Node placeholder;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXMenuContentSkin(MFXMenuContent mc) {
        super(mc);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Updates the [MFXMenuContent]'s children list with the items retrieved from [MFXMenuContent#getMenu()].
    ///
    /// If the menu contains no items, this method sets a placeholder node, if provided by [MFXMenuContent#placeholderSupplierProperty()].
    ///
    /// **NOTE:** this method is also responsible for setting the menu instance on its items, [MFXMenuItem#setMenu(MFXMenu)].
    /// The items do not carry the object at construction time by a design choice, as that would make their creation much
    /// more inconvenient ([MenuBuilder]).
    protected void updateChildren() {
        MFXMenuContent mc = getSkinnable();
        ObservableList<MFXMenuItem> items = getMenuItems();

        if (items.isEmpty()) {
            Optional.ofNullable(mc.getPlaceholderSupplier())
                .map(Supplier::get)
                .ifPresentOrElse(
                    n -> {
                        placeholder = n;
                        placeholder.getStyleClass().add("placeholder");
                        getChildren().setAll(n);
                    },
                    () -> {
                        placeholder = null;
                        getChildren().clear();
                    }
                );
            return;
        }

        placeholder = null;
        items.forEach(it -> it.setMenu(mc.getMenu()));
        getChildren().setAll(items);
    }

    protected ObservableList<MFXMenuItem> getMenuItems() {
        return getSkinnable().getMenu().getItems();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void install() {
        MFXMenuContent mc = getSkinnable();

        // Listeners
        listen(
            When.observe(this::updateChildren, getMenuItems(), mc.placeholderSupplierProperty()).executeNow()
        );

        // Input
        onInput(
            WhenEvent.intercept(mc, KeyEvent.KEY_PRESSED).handle(e -> behavior().keyPressed(e))
        );
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (placeholder != null) return LayoutUtils.snappedBoundWidth(placeholder);
        return getChildren().stream()
                   .mapToDouble(LayoutUtils::snappedBoundWidth)
                   .max()
                   .orElse(0.0) + rightInset + leftInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (placeholder != null) return LayoutUtils.snappedBoundHeight(placeholder);
        double gap = getSkinnable().getSpacing();
        return getChildren().stream()
                   .mapToDouble(node -> LayoutUtils.snappedBoundHeight(node) + gap)
                   .sum() + topInset + bottomInset - gap;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXMenuContent mc = getSkinnable();
        MFXMenu menu = mc.getMenu();
        double gap = mc.getSpacing();
        double advanceY = y;
        ObservableList<MFXMenuItem> items = getMenuItems();
        double maxTextW = -1;
        double maxIconW = -1;
        for (MFXMenuItem item : items) {
            double textW = item.textWidth();
            double iconW = item.iconWidth();
            if (textW > maxTextW) maxTextW = textW;
            if (iconW > maxIconW) maxIconW = iconW;
            double iH = LayoutUtils.snappedBoundHeight(item);
            item.resizeRelocate(x, advanceY, w, iH);
            advanceY += iH + gap;
        }
        menu.setIconColumnWidth(maxIconW);
        menu.setTextColumnWidth(maxTextW);

        if (placeholder != null) {
            layoutInArea(placeholder, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
        }
    }

    @Override
    public void dispose() {
        placeholder = null;
        getChildren().clear();
        super.dispose();
    }
}
