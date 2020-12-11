package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import javafx.scene.control.skin.ButtonSkin;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXButton}.
 */
public class MFXButtonSkin extends ButtonSkin {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonSkin(MFXButton button) {
        super(button);
        setListeners();
        updateButtonType();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners to: depthLevel and buttonType properties.
     */
    private void setListeners() {
        MFXButton button = (MFXButton) getSkinnable();

        button.depthLevelProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && button.getButtonType().equals(ButtonType.RAISED)) {
                button.setEffect(MFXDepthManager.shadowOf(newValue));
            }
        });

        button.buttonTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                updateButtonType();
            }
        });

    }

    /**
     * Changes the button type.
     */
    private void updateButtonType() {
        MFXButton button = (MFXButton) getSkinnable();

        switch (button.getButtonType()) {
            case RAISED: {
                button.setEffect(MFXDepthManager.shadowOf(button.getDepthLevel()));
                button.setPickOnBounds(false);
                break;
            }
            case FLAT: {
                button.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL0));
                button.setPickOnBounds(true);
                break;
            }
        }
    }
}
