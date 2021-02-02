package io.github.palexdev.materialfx.demo;

import java.net.URL;

/**
 * Utility class which manages the access to this project's assets.
 * Helps keeping the assets files structure organized.
 */
public class ResourcesLoader {

    private ResourcesLoader() {
    }

    public static URL load(String path) {
        return ResourcesLoader.class.getResource(path);
    }
}
