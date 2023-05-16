/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls.buttons;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.theming.base.WithVariants;
import io.github.palexdev.mfxcomponents.theming.enums.ButtonVariants;
import javafx.scene.Node;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom implementation of a button which extends {@link MFXButtonBase}, has its own skin
 * {@link MFXButtonSkin}.
 * <p></p>
 * Material 3 guidelines show 5 variants for common buttons: elevated, filled, tonal filled, outlined and text.
 * These are implemented through the {@link WithVariants} API. Every new button will be by default an elevated button,
 * {@link ButtonVariants#ELEVATED}.
 * <p>
 * The default style class of this component is: '.mfx-button'.
 */
public class MFXButton extends MFXButtonBase<MFXButtonBehaviorBase<MFXButton>> implements WithVariants<MFXButton, ButtonVariants> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButton() {
        initialize();
    }

    public MFXButton(String text) {
        super(text);
        initialize();
    }

    public MFXButton(String text, Node icon) {
        super(text, icon);
        initialize();
    }

    //================================================================================
    // Variants
    //================================================================================
    public MFXButton elevated() {
        setVariants(ButtonVariants.ELEVATED);
        return this;
    }

    public MFXButton filled() {
        setVariants(ButtonVariants.FILLED);
        return this;
    }

    public MFXButton outlined() {
        setVariants(ButtonVariants.OUTLINED);
        return this;
    }

    public MFXButton text() {
        setVariants(ButtonVariants.TEXT);
        return this;
    }

    public MFXButton tonal() {
        setVariants(ButtonVariants.FILLED_TONAL);
        return this;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        elevated();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXButtonSkin<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-button");
    }

    @Override
    public Supplier<MFXButtonBehaviorBase<MFXButton>> defaultBehaviorProvider() {
        return () -> new MFXButtonBehaviorBase<>(this);
    }

    @Override
    public MFXButton addVariants(ButtonVariants... variants) {
        return WithVariants.addVariants(this, variants);
    }

    @Override
    public MFXButton setVariants(ButtonVariants... variants) {
        return WithVariants.setVariants(this, variants);
    }

    @Override
    public MFXButton removeVariants(ButtonVariants... variants) {
        return WithVariants.removeVariants(this, variants);
    }
}
