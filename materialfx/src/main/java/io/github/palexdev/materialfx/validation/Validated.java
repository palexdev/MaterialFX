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
