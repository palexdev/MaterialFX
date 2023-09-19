package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/**
 * Base behavior for all buttons that are also {@link Selectable}s.
 * <p></p>
 * Extends {@link MFXButtonBehaviorBase} since most of the API is the same, but has an extra method to handle selection,
 * {@link #handleSelection()}, and the mouse click and key event handlers are overridden to make use of it, rather than
 * immediately calling {@link MFXSelectable#fire()}.
 */
public class MFXSelectableBehaviorBase<S extends MFXSelectable<?>> extends MFXButtonBehaviorBase<S> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSelectableBehaviorBase(S button) {
        super(button);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Responsible for switching the selection state of the component.
     * <p>
     * It's worth specifying that the change is not as simple as just flipping the boolean value. Those who implement
     * the {@link Selectable} API may be in a {@link SelectionGroup}.
     * <p>
     * After requesting the new state with {@link Selectable#setSelected(boolean)},
     * we must check if the state was effectively changed, and only if so also trigger {@link MFXSelectable#fire()}.
     */
    protected void handleSelection() {
        S selectable = getNode();
        boolean oldState = selectable.isSelected();
        boolean newState = !oldState;
        selectable.setSelected(newState);

        // This is needed since the new state may not necessarily be the one above
        // For example if in a group and cannot be selected/deselected...
        if (selectable.isSelected() != oldState) selectable.fire();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p>
     * Extended to also call {@link #handleSelection()} on {@link MouseButton#PRIMARY}.
     */
    @Override
    public void mouseClicked(MouseEvent me, Consumer<MouseEvent> callback) {
        if (me.getButton() == MouseButton.PRIMARY) handleSelection();
        super.mouseClicked(me, callback);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Extended to also call {@link #handleSelection()} on {@link KeyCode#ENTER}.
     */
    @Override
    public void keyPressed(KeyEvent ke, Consumer<KeyEvent> callback) {
        if (ke.getCode() == KeyCode.ENTER) handleSelection();
        super.keyPressed(ke, callback);
    }
}
