package io.github.palexdev.mfxeffects.enums;

import javafx.scene.Node;

/**
 * Enumeration for ripple generators to allow users to specify the behavior for mouse events.
 */
public enum MouseTransparentMode {

	/**
	 * The generator will detect all mouse events.
	 */
	OFF,

	/**
	 * The generator will ignore mouse events on the "bounds area" and only consider the geometric shape of the node,
	 * better explained here {@link Node#pickOnBoundsProperty()}.
	 */
	DONT_PICK_ON_BOUNDS,

	/**
	 * The generator will ignore all mouse events.
	 */
	MOUSE_TRANSPARENT
}
