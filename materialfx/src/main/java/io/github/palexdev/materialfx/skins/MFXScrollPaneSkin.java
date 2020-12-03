package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.layout.StackPane;

/**
 * Skin used for {@link MFXScrollPane}, this class' purpose is to
 * fix a bug of ScrollPanes' viewport which makes the content blurry.
 * <p>
 * Luckily achieved without reflection :D
 */
public class MFXScrollPaneSkin extends ScrollPaneSkin {

    public MFXScrollPaneSkin(MFXScrollPane scrollPane) {
        super(scrollPane);
        StackPane viewPort = (StackPane) scrollPane.lookup(".viewport");
        viewPort.setCache(false);
    }

}
