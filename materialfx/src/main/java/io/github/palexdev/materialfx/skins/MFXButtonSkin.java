package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXButton;
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
    public MFXButtonSkin(MFXButton button, DepthLevel depthLevel) {
        super(button);

        button.buttonTypeProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonType(button, depthLevel));

        updateButtonType(button, depthLevel);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Changes the button type
     */
    private void updateButtonType(MFXButton button, DepthLevel depthLevel) {
        switch (button.getButtonType()) {
            case RAISED: {
                getSkinnable().setEffect(MFXDepthManager.shadowOf(depthLevel));
                getSkinnable().setPickOnBounds(false);
                break;
            }
            case FLAT: {
                getSkinnable().setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL0));
                getSkinnable().setPickOnBounds(true);
                break;
            }
        }
    }
}
