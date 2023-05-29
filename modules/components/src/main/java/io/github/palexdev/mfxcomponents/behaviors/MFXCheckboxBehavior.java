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

import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcomponents.skins.MFXCheckBoxSkin;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import java.util.Optional;

/**
 * This is the default behavior used by all {@link MFXCheckBox} components.
 * <p>
 * Extends {@link MFXSelectableBehaviorBase} since most of the API is the same, but the {@link #handleSelection()} method
 * is overridden to also take into account the special {@code indeterminate} state of checkboxes.
 * <p>
 * The {@link #keyPressed(KeyEvent)} method has been overridden too, to correctly handle the ripple effect.
 * As also explained by {@link MFXCheckBoxSkin}, the effect is generated on the node containing the box and the check
 * mark. When we click outside the container, or press ENTER, the effect should be generated at the center of the container
 * (as if it was a 'fallback'). For this reason, there's also a mechanism to retrieve the container, see {@link #getIcon()}.
 */
public class MFXCheckBoxBehavior extends MFXSelectableBehaviorBase<MFXCheckBox> {
    //================================================================================
    // Properties
    //================================================================================
    private Node icon;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckBoxBehavior(MFXCheckBox button) {
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
     * For checkboxes, the mechanism is even more complex since they also have the {@code 'indeterminate'} state.
     * <p>
     * Here's all the possible cases:
     * <p> 1) The checkbox doesn't allow the {@code indeterminate} state, this is the simplest case. The selection state
     * is flipped
     * <p> 2) The checkbox is not selected and neither {@code indeterminate}, sets the {@code indeterminate} state to true
     * <p> 3) The checkbox is selected and not {@code indeterminate}, sets the selection state to false
     * <p> 4) The checkbox is not selected but {@code indeterminate}, sets the selection state to true and the
     * {@code indeterminate} state to false
     * <p></p>
     * Last but not least, if either the selection or {@code indeterminate} states have changed,
     * triggers {@link MFXCheckBox#fire()}.
     */
    @Override
    protected void handleSelection() {
        MFXCheckBox checkBox = getNode();
        boolean oldIndeterminate = checkBox.isIndeterminate();
        boolean oldState = checkBox.isSelected();

        if (checkBox.isAllowIndeterminate()) {
            if (!oldState && !oldIndeterminate) {
                checkBox.setIndeterminate(true);
            } else if (oldState && !oldIndeterminate) {
                checkBox.setSelected(false);
            } else {
                checkBox.setSelected(true);
                checkBox.setIndeterminate(false);
            }
        } else {
            checkBox.setSelected(!oldState);
        }

        // This is needed since the new state may not necessarily be the one above
        // For example if in a group and cannot be selected/deselected...
        if (checkBox.isSelected() != oldState ||
            checkBox.isIndeterminate() != oldIndeterminate)
            checkBox.fire();
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
            MFXCheckBox checkBox = getNode();
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