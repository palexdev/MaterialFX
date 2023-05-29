package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;

/**
 * This is the default behavior used by all {@link MFXIconButton} components.
 * <p>
 * Extends {@link MFXSelectableBehaviorBase} since most of the API is the same, but the {@link #handleSelection()} method
 * is overridden to also take into account the special property: {@link MFXIconButton#selectableProperty()}.
 */
public class MFXIconButtonBehavior extends MFXSelectableBehaviorBase<MFXIconButton> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconButtonBehavior(MFXIconButton button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void handleSelection() {
        MFXIconButton btn = getNode();
        if (!btn.isSelectable()) {
            // If the button is not a toggle it still acts like a normal button!
            btn.fire();
            return;
        }
        super.handleSelection();
    }
}
