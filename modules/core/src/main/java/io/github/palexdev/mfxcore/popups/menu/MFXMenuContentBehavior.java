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

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/// Default behavior implementation for [MFXMenuContent].
///
/// There isn't much going on here except for a focus handling workaround in [#keyPressed(KeyEvent, Runnable)].
public class MFXMenuContentBehavior extends MFXBehavior<MFXMenuContent> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContentBehavior(MFXMenuContent mc) {
        super(mc);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// For some reason the JavaFX focus API transfers focus to the second item in the menu when using the arrow keys.
    /// The solution for this crap is to catch [KeyCode#DOWN] request the focus traversal to the next node ONLY when the
    /// content is focused.
    /// (When the items are focused, the content loses focus, therefore, the workaround here is not needed anymore.)
    ///
    /// @see Node#requestFocusTraversal(TraversalDirection)
    /// @see TraversalDirection#NEXT
    @Override
    public void keyPressed(KeyEvent ke, Runnable callback) {
        MFXMenuContent mc = getNode();
        if (ke.getCode() == KeyCode.DOWN && mc.isFocused()) {
            mc.requestFocusTraversal(TraversalDirection.NEXT);
        }
        if (ke.getCode() == KeyCode.ESCAPE) {
            mc.getMenu().hide();
        }
        callback.run();
    }
}
