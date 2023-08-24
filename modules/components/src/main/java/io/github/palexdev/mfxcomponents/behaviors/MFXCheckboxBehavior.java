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

package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.skins.MFXCheckboxSkin;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import java.util.Optional;

/**
 * This is the default behavior used by all {@link MFXCheckbox} components.
 * <p>
 * Extends {@link MFXSelectableBehaviorBase} since most of the API is the same, but the {@link #handleSelection()} method
 * is overridden to also take into account the special {@code indeterminate} state of checkboxes.
 * <p>
 * The {@link #keyPressed(KeyEvent)} method has been overridden too, to correctly handle the ripple effect.
 * As also explained by {@link MFXCheckboxSkin}, the effect is generated on the node containing the box and the check
 * mark. When we click outside the container, or press ENTER, the effect should be generated at the center of the container
 * (as if it was a 'fallback'). For this reason, there's also a mechanism to retrieve the container, see {@link #getIcon()}.
 */
public class MFXCheckboxBehavior extends MFXSelectableBehaviorBase<MFXCheckbox> {
    //================================================================================
    // Properties
    //================================================================================
    private Node icon;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckboxBehavior(MFXCheckbox button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void init() {
        super.init();
        getIcon();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        Node icon = getIcon().orElse(null);
        if (ke.getCode() == KeyCode.ENTER) {
            if (icon == null) return;
            Bounds b = icon.getBoundsInParent();
            getRippleGenerator().ifPresent(rg -> rg.generate(b.getCenterX(), b.getCenterY()));
            handleSelection();
        }
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * For checkboxes, the mechanism is even more complex since they also have the {@code indeterminate} state.
     * <p>
     * Here's all the possible cases:
     * <p> 1) The checkbox doesn't allow the {@code indeterminate} state, this is the simplest case. The selection state
     * is flipped (see {@link MFXCheckbox#allowIndeterminateProperty()})
     * <p> 2) The checkbox is {@code indeterminate}, sets the state to {@code selected}
     * <p> 3) The checkbox is not selected, sets the state to {@code indeterminate}
     * <p>
     * In short, the cycle is: UNSELECTED -> INDETERMINATE (if allowed) -> SELECTED
     * <p></p>
     * <b>Note:</b> this method will not invoke {@link MFXCheckbox#fire()}, as it is handled by the checkbox' {@link SelectionProperty},
     * this is done to make {@link ActionEvent}s work also when the property is bound. I've not yet decided if this will
     * be the final behavior, if you have issues/opinions on this please let me know.
     */
    @Override
    protected void handleSelection() {
        MFXCheckbox checkBox = getNode();
        if (checkBox.stateProperty().isBound()) return;

        MFXCheckbox.TriState oldState = checkBox.getState();
        if (checkBox.isAllowIndeterminate()) {
            if (oldState == MFXCheckbox.TriState.INDETERMINATE) {
                checkBox.setState(MFXCheckbox.TriState.SELECTED);
                return;
            }
            if (oldState == MFXCheckbox.TriState.UNSELECTED) {
                checkBox.setState(MFXCheckbox.TriState.INDETERMINATE);
                return;
            }
        }
        checkBox.setState(oldState == MFXCheckbox.TriState.UNSELECTED ? MFXCheckbox.TriState.SELECTED : MFXCheckbox.TriState.UNSELECTED);
        // fire() is handled by the state property, to make bindings work too
    }

    @Override
    public void dispose() {
        icon = null;
        super.dispose();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Since the behavior doesn't have access to the checkbox' skin, this is responsible for retrieving the node which
     * contains the box and the check mark in some way. This node is necessary to correctly generate the ripple effect
     * when events occur outside of it.
     * <p></p>
     * The list returned by {@link Region#getChildrenUnmodifiable()} may be unmodifiable, but nothing prevents us
     * from still taking values from it. A stream searches for instances of {@link MFXIconWrapper} and takes the first found
     * node.
     * This is protected so that other components may specify a different search algorithm.
     * The good thing about this, is that it's way faster than {@link Node#lookup(String)}.
     * <p></p>
     * Note that this returns an {@link Optional}. It may be empty in two cases!
     * <p> 1) In case the component' skin is still null
     * <p> 2) In case the node was not found
     * <p></p>
     * After the first search, (performed by {@link #init()}), if the container was found, the instance is cached locally
     * so that we don't need to perform the search anymore. An additional step to clear the reference is needed in the
     * {@link #dispose()} method.
     */
    protected Optional<Node> getIcon() {
        if (icon == null) {
            MFXCheckbox checkBox = getNode();
            Skin<?> skin = checkBox.getSkin();
            if (skin == null) return Optional.empty();
            Optional<Node> opt = checkBox.getChildrenUnmodifiable().stream()
                .filter(n -> n instanceof MFXIconWrapper)
                .findFirst();
            icon = opt.orElse(null);
            return opt;
        }
        return Optional.of(icon);
    }
}
