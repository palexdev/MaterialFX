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

package io.github.palexdev.mfxcore.utils.fx.resize;

import javafx.scene.Cursor;
import javafx.scene.Node;

/// Arbitrates the cursor of a single node between several [Resizer]s sharing it, one instance per node, held in
/// [Node#getProperties()].
///
/// Claiming is last-wins and saves the node's own cursor on the *first* claim only, so a handoff between resizers
/// can't capture another resizer's cursor as the original. Releasing does nothing unless you currently hold the claim,
/// which is what lets every non-owner call [#release(Object)] on every move and correctly do nothing.
class CursorOwner {

    //================================================================================
    // Properties
    //================================================================================

    public static final String PROP_ID = "CURSOR_OWNER";

    private final Node node;
    private Resizer<?> owner;
    private Cursor origin;

    //================================================================================
    // Constructors
    //================================================================================

    private CursorOwner(Node node) {this.node = node;}

    public static CursorOwner forNode(Node node) {
        return (CursorOwner) node.getProperties().computeIfAbsent(PROP_ID, _ -> new CursorOwner(node));
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Sets the cursor and makes `owner` the current claimant. A `null` cursor delegates to [#release(Object)].
    public void claim(Resizer<?> owner, Cursor cursor) {
        if (cursor == null) {
            release(owner);
            return;
        }

        if (this.owner == null) origin = node.getCursor();
        this.owner = owner;
        node.setCursor(cursor);
    }

    /// Restores the node's original cursor, but only if `owner` is the current claimant.
    public void release(Object owner) {
        if (this.owner != owner) return;
        node.setCursor(origin);
        origin = null;
        this.owner = null;
    }
}
