package it.paprojects.materialfx.skins;

import it.paprojects.materialfx.controls.MFXButton;
import it.paprojects.materialfx.effects.DepthLevel;
import it.paprojects.materialfx.effects.MFXDepthManager;
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
