package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import javafx.scene.layout.Region;

public interface IMFXPopupRoot extends MFXStyleable {

    Region toNode();

    IMFXPopup getPopup();

    void dispose();
}
