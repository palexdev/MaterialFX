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

package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/// Base behavior for all MaterialFX buttons which extend from [MFXButtonBase].
///
/// Primarily responsible for acquiring focus and "activating" the button when appropriate (mouse click, space/enter pressed, etc.).
///
/// @see MFXButtonBase#trigger()
public class MFXButtonBehavior<B extends MFXButtonBase> extends MFXBehavior<B> {

    //================================================================================
    // Constructors
    //================================================================================

    public MFXButtonBehavior(B button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void mousePressed(MouseEvent e, Runnable callback) {
        getNode().requestFocus();
        callback.run();
    }

    @Override
    public void mouseClicked(MouseEvent e, Runnable callback) {
        if (e.getButton() == MouseButton.PRIMARY) getNode().trigger();
        callback.run();
    }

    @Override
    public void keyPressed(KeyEvent e, Runnable callback) {
        if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) getNode().trigger();
        callback.run();
    }
}
