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

import io.github.palexdev.mfxcomponents.controls.base.MFXToggle;
import io.github.palexdev.mfxcomponents.skins.MFXRadioButtonSkin;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

/// Custom implementation of a radio button which extends [MFXToggle] and has its own skin [MFXRadioButtonSkin].<br >
/// The default style class of this component is: '.mfx-radio-button'.
///
/// `Radios` are the simplest kind of toggle. Contrary to [MFXCheckbox], which also has to deal with the `indeterminate`
/// state, a radio is either selected or not. Which is why this class adds nothing on top of the API already offered
/// by [MFXToggle].
// TODO introduce validator and properly handle error state (it's only visual for now)
public class MFXRadioButton extends MFXToggle {

    //================================================================================
    // Constructors
    //================================================================================

    public MFXRadioButton() {
        this("Radio Button");
    }

    public MFXRadioButton(String text) {
        super(text);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXRadioButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-radio-button");
    }
}
