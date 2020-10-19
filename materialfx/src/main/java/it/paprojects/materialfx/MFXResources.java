package it.paprojects.materialfx;

import java.net.URL;

/**
 * Utility class which manages the access to this project's assets.
 * Helps keeping the assets files structure organized.
 */
public class MFXResources {

    private MFXResources() {}

    public static URL load(String path) {
        return MFXResources.class.getResource(path);
    }
}
