package io.github.palexdev.mfxcomponents.behaviors.base;

import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Base behavior class for implementations of {@link MFXSelectable} that have to deal with selection.
 */
public abstract class MFXSelectableBehavior<S extends MFXSelectable<?>> extends BehaviorBase<S> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSelectableBehavior(S selectable) {
        super(selectable);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Hook to trigger {@link #handleSelection()} from a {@link MouseEvent}.
     */
    public void handleSelection(MouseEvent me) {
        if (me.getButton() == MouseButton.PRIMARY) handleSelection();
    }

    /**
     * Hook to trigger {@link #handleSelection()} from a {@link KeyEvent}
     */
    public void handleSelection(KeyEvent ke) {
        handleSelection();
    }

    /**
     * This is responsible for switching the current selection state of the handled {@link MFXSelectable}.
     * <p>
     * As well as calling {@link MFXSelectable#fire()} but only if the new selection state is not equal to the old one.
     */
    public void handleSelection() {
        S selectable = getNode();
        boolean oldState = selectable.isSelected();
        boolean newState = !oldState;
        selectable.setSelected(newState);

        // This is needed since the new state may not necessarily be the one above
        // For example if in a group and cannot be selected/deselected...
        if (selectable.isSelected() != oldState) selectable.fire();
    }
}
