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
import io.github.palexdev.mfxcomponents.controls.checkbox.TriState;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.EnumUtils;
import javafx.event.ActionEvent;

/**
 * This is the default behavior used by all {@link MFXCheckbox} components.
 * <p>
 * Extends {@link MFXSelectableBehaviorBase} since most of the API is the same, but the {@link #handleSelection()} method
 * is overridden to also take into account the special {@code indeterminate} state of checkboxes.
 */
public class MFXCheckboxBehavior extends MFXSelectableBehaviorBase<MFXCheckbox> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckboxBehavior(MFXCheckbox button) {
		super(button);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

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
	 * In short, the cycle is: UNSELECTED -> SELECTED -> INDETERMINATE (if allowed)
	 * <p></p>
	 * <b>Note:</b> this method will not invoke {@link MFXCheckbox#fire()}, as it is handled by the checkbox' {@link SelectionProperty},
	 * this is done to make {@link ActionEvent}s work also when the property is bound. I've not yet decided if this will
	 * be the final behavior, if you have issues/opinions on this please let me know.
	 */
	@Override
	protected void handleSelection() {
		MFXCheckbox checkBox = getNode();
		if (checkBox.stateProperty().isBound()) return;

		TriState oldState = checkBox.getState();
		TriState newState = EnumUtils.next(TriState.class, oldState);
		if (newState == TriState.INDETERMINATE && !checkBox.isAllowIndeterminate())
			newState = EnumUtils.next(TriState.class, newState);
		checkBox.setState(newState);
		// fire() is handled by the state property, to make bindings work too
	}
}
