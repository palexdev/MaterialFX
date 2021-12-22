package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXContextMenu;

/**
 * Every control offering a {@link MFXContextMenu} by default should
 * implement this interface.
 */
public interface MFXMenuControl {

	/**
	 * @return the context menu of the control
	 */
	MFXContextMenu getMFXContextMenu();

	/**
	 * @see MFXContextMenu#isDisabled()
	 */
	default boolean isContextMenuDisabled() {
		return getMFXContextMenu() != null && getMFXContextMenu().isDisabled();
	}

	/**
	 * @see MFXContextMenu#setDisabled(boolean)
	 */
	default void setContextMenuDisabled(boolean disabled) {
		if (getMFXContextMenu() != null) {
			getMFXContextMenu().setDisabled(disabled);
		}
	}
}
