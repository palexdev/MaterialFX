package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

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

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to not trigger {@link MFXIconButton#fire()} twice as it is already handled by {@link #handleSelection()}
     */
    @Override
    public void mouseClicked(MouseEvent me, Consumer<MouseEvent> callback) {
        if (me.getButton() == MouseButton.PRIMARY) handleSelection();
        if (callback != null) callback.accept(me);
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to not trigger {@link MFXIconButton#fire()} twice as it is already handled by {@link #handleSelection()}
     */
    @Override
    public void keyPressed(KeyEvent ke, Consumer<KeyEvent> callback) {
        if (ke.getCode() == KeyCode.ENTER) handleSelection();
        if (callback != null) callback.accept(ke);
    }

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
