package io.github.palexdev.materialfx.font;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import javafx.scene.text.Font;

/**
 * Handler for MaterialFX font resources.
 */
public class FontHandler {
    private static final Font resources;

    static {
        resources = Font.loadFont(MFXResourcesLoader.loadStream("fonts/materialfx-resources.ttf"), 10);
    }

    private FontHandler() {
    }

    public static Font getResources() {
        return resources;
    }

    public static char getCode(String description) {
        return FontResources.findByDescription(description).getCode();
    }
}
