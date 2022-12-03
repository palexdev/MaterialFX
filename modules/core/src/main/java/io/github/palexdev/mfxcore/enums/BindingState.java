package io.github.palexdev.mfxcore.enums;

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;

/**
 * Enumeration to identify the various states of a {@link IBinding}
 */
public enum BindingState {

	/**
	 * This special state describes bindings that have just been created and have yet to
	 * be activated.
	 */
	NULL,

	/**
	 * This state describes bindings that have been disposed.
	 */
	DISPOSED,

	/**
	 * This state describes bindings that are currently active.
	 */
	BOUND,

	/**
	 * This state describes bindings that have been deactivated.
	 * <p>
	 * This is different from {@link #DISPOSED}!
	 */
	UNBOUND
}
