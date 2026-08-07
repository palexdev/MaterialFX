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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton.MFXToggleIconButton;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;

/// Default skin implementation for all [MFXIconButtons][MFXIconButton] and [MFXToggleIconButtons][MFXToggleIconButton].<br >
/// It's an extension and simplification of [MFXButtonSkin].<br >
/// Icon buttons are designed to only show an icon, no text. Therefore, we can optimize the skin to not have a label node.
///
/// The icon specified by the [MFXIconButton#iconProperty()] is wrapped in a [MFXIconWrapper]. There is a bidirectional
/// binding between the two. You can also set the icon from CSS on the wrapper by doing this:
/// ```css
/// .mfx-icon-button > .mfx-icon-wrapper {
/// /* These are optional and enable icons switching animations (by default are already enabled in MaterialFX themes) */
///   -mfx-animated: true;
///   -mfx-animation: SCALE;
///
///   -mfx-icon: 'fa-check';
/// }
/// ```
///
/// @see MFXIconWrapper
public class MFXIconButtonSkin extends MFXButtonSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXIconWrapper icon;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXIconButtonSkin(MFXButtonBase button) {
        icon = new MFXIconWrapper(); // thank you Java 25 :)
        super(button);

        if (button instanceof MFXIconButton ib) {
            icon.iconProperty().bindBidirectional(ib.iconProperty());
        } else if (button instanceof MFXToggleIconButton tib) {
            icon.iconProperty().bindBidirectional(tib.iconProperty());
        } else {
            throw new IllegalArgumentException("Button must be of type MFXIconButton or MFXToggleIconButton!");
        }
    }

    @Override
    protected void updateChildren() {
        getChildren().setAll(surface, rg, icon);
    }

    @Override
    protected BoundLabel buildLabelNode() {
        return null;
    }

    @Override
    protected void initTextMeasurementCache() {}

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + icon.prefSize() + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + icon.prefSize() + bottomInset;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXButtonBase button = getControl();
        surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        rg.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        layoutInArea(icon, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
    }
}
