/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.validation;

import javafx.css.PseudoClass;
import javafx.scene.Node;

import java.util.List;

/**
 * Interface that defines the public API every control needing validation
 * should implement.
 * <p>
 * Note that this interface just tells the user that the control already offers
 * a {@link MFXValidator} instance if needed.
 * <p>
 * Also defines a PseudoClass, ":invalid", that can be used in CSS to style
 * the control according to the validator' state. Note that the PseudoClass is not
 * managed automatically but it must be activated/deactivated by the user, you can
 * use {@link #updateInvalid(Node, boolean)} to do this.
 */
public interface Validated {
	PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

	/**
	 * @return the {@link MFXValidator} instance of this control
	 */
	MFXValidator getValidator();

	/**
	 * @return whether the validator instance of this control is not null and valid
	 * @see MFXValidator#validProperty()
	 */
	default boolean isValid() {
		return getValidator() != null && getValidator().isValid();
	}

	/**
	 * @return the list of invalid constraints for the control's validator instance.
	 * <p>
	 * An empty list if null
	 * @see MFXValidator#validate()
	 */
	default List<Constraint> validate() {
		return getValidator() != null ? getValidator().validate() : List.of();
	}

	/**
	 * Convenience method to update the ":invalid" PseudoClass offered by this interface.
	 *
	 * @param node    the node on which apply/remove the ":invalid" PseudoClass
	 * @param invalid the PseudoClass state
	 */
	default void updateInvalid(Node node, boolean invalid) {
		node.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), invalid);
	}
}
