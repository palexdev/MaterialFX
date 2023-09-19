package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/**
 * Generic base behavior for all buttons extending from {@link MFXButtonBase}.
 */
public class MFXButtonBehaviorBase<B extends MFXButtonBase<?>> extends BehaviorBase<B> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBehaviorBase(B button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * Responsible for acquiring the focus.
     */
    @Override
    public void mousePressed(MouseEvent e, Consumer<MouseEvent> callback) {
        getNode().requestFocus();
        super.mousePressed(e, callback);
    }

    /**
     * Responsible for calling {@link MFXButtonBase#fire()} if the clicked mouse button was {@link MouseButton#PRIMARY}.
     */
    @Override
    public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
        if (e.getButton() == MouseButton.PRIMARY) getNode().fire();
        super.mouseClicked(e, callback);
    }

    /**
     * Responsible for calling {@link MFXButtonBase#fire()} if the pressed key was {@link KeyCode#ENTER}.
     */
    @Override
    public void keyPressed(KeyEvent e, Consumer<KeyEvent> callback) {
        if (e.getCode() == KeyCode.ENTER) getNode().fire();
        super.keyPressed(e, callback);
    }
}
