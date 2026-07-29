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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.controls.MFXButtonsGroup;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/// Default skin implementation for all [MFXButtonsGroups][MFXButtonsGroup].
///
/// The layout is simple: every button is [MFXButtonsGroup#getButtons()] is positioned in a row spaced by
/// the [MFXButtonsGroup#spacingProperty()]'s value, just like a [HBox].
public class MFXButtonsGroupSkin extends MFXSkinBase<MFXButtonsGroup> {

    //================================================================================
    // Constructors
    //================================================================================

    public MFXButtonsGroupSkin(MFXButtonsGroup group) {
        super(group);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    protected void addListeners() {
        MFXButtonsGroup group = getSkinnable();
        Bindings.bindContent(getChildren(), group.getButtons());
        listeners(
            When.observe(group::requestLayout, group.spacingProperty())
        );
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double spacing = getSkinnable().getSpacing();
        return getChildren().stream()
                   .mapToDouble(LayoutUtils::snappedBoundWidth)
                   .sum() + leftInset + rightInset + (spacing * (getChildren().size() - 1));
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getChildren().stream()
                   .mapToDouble(LayoutUtils::snappedBoundHeight)
                   .max()
                   .orElse(0.0) + topInset + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXButtonsGroup group = getSkinnable();
        double advance = 0.0;
        for (Node child : getChildren()) {
            layoutInArea(child, x + advance, y, w, h, 0.0, HPos.LEFT, VPos.CENTER);
            advance += child.getLayoutBounds().getWidth() + group.getSpacing();
        }
    }

    @Override
    public void dispose() {
        MFXButtonsGroup group = getSkinnable();
        Bindings.unbindContent(getChildren(), group.getButtons());
        getChildren().clear();
        super.dispose();
    }
}
