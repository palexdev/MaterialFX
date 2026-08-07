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

package io.github.palexdev.mfxcore.utils.fx;

import javafx.beans.InvalidationListener;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/// An overlay with lowered opacity that serves as a visual cue that the content behind it is not available for interactions.
///
/// ### Implementation Details
///
/// In JavaFX, the effect is implemented as a [Region] node to be added to an "owner" [Pane]. The node is not managed
/// and uses a series of listeners to size and position within the owner. Moreover, it uses the [#viewOrderProperty()]
/// to always appear in front of any other child. For the same reason, if you need to keep some child interactable, you can
/// set their view order to a number that is lesser than the effect. By default, the view order of this effect is set to
/// [Byte#MIN_VALUE].
///
/// By default, the effect's background is set to `rgba(0, 0, 0, 0.2)`. You can easily change it via code or from CSS.
/// The default style class is set to "mfx-backdrop".
public class MFXBackdrop extends Region {
    //================================================================================
    // Properties
    //================================================================================
    private Pane owner;
    private InvalidationListener layoutHandler;

    //================================================================================
    // Constructors
    //================================================================================
    @SuppressWarnings("unchecked")
    public MFXBackdrop() {
        getStyleClass().setAll("mfx-backdrop");
        setManaged(false);
        setViewOrder(Byte.MIN_VALUE);

        // Init default style
        ((StyleableProperty<Background>) backgroundProperty()).applyStyle(
            null,
            Background.fill(Color.rgb(0, 0, 0, 0.2))
        );
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is the method responsible for adding the effect to the owner and thus showing it.
    ///
    /// Note that for convenience this accepts a generic [Node] as the argument, but, to add the effect, we need the node's
    /// children's list, so, we need a [Pane]. Builds the listeners to handle its layout withing the owner and only then
    /// is added.
    ///
    /// @see #preShowCheck(Node)
    public void show(Node owner) {
        Pane pane = preShowCheck(owner);
        layoutHandler = _ -> {
            Bounds b = owner.getLayoutBounds();
            resizeRelocate(0, 0, b.getWidth(), b.getHeight());
        };
        pane.layoutBoundsProperty().addListener(layoutHandler);
        pane.needsLayoutProperty().addListener(layoutHandler);
        pane.getChildren().add(this);
        this.owner = pane;
    }

    /// This is the method responsible for removing the effect from the owner.
    ///
    /// Listeners are automatically cleaned up here.
    public void hide() {
        if (owner != null) {
            owner.layoutBoundsProperty().removeListener(layoutHandler);
            owner.needsLayoutProperty().removeListener(layoutHandler);
            layoutHandler = null;
            owner.getChildren().remove(this);
            owner = null;
        }
    }

    /// Performs some checks before the effect can be effectively shown.
    ///
    /// @throws IllegalArgumentException if the owner is null
    /// @throws IllegalArgumentException if the owner is not a [Pane]
    /// @throws IllegalStateException if the backdrop is already shown
    protected Pane preShowCheck(Node owner) {
        if (owner == null)
            throw new IllegalArgumentException("The owner must not be null");

        if (!(owner instanceof Pane pane))
            throw new IllegalArgumentException("The owner must be a Pane");

        if (this.owner != null)
            throw new IllegalStateException("The backdrop is already shown for the owner: " + this.owner);

        return pane;
    }
}
