package io.github.palexdev.materialfx.demo;

import java.net.URL;

/**
 * Utility class which manages the access to this project's assets.
 * Helps keeping the assets files structure organized.
 */
public class MFXResourcesLoader {

    private MFXResourcesLoader() {
    }

    public static URL load(String path) {
        return MFXResourcesLoader.class.getResource(path);
    }
}
