package io.github.palexdev.materialfx.utils;

import java.net.URL;

/**
 * Convenience class to avoid duplicated code in {@code MFXHLoader} and {@code MFXVLoader} classes
 */
public class Loader {

    private Loader() {
    }

    /**
     * Check if the given URL is an fxml file.
     */
    public static void checkFxmlFile(URL fxmlFile) {
        if (!fxmlFile.toString().endsWith(".fxml")) {
            throw new IllegalArgumentException("The URL is invalid, doesn't end with '.fxml'!!");
        }
    }

    /**
     * If no key is specified when calling 'addItem' then a default key is generated,
     * corresponds to the fxml file name without the extension.
     * @param fxmlFile The given fxml file
     * @return The generated key
     */
    public static String generateKey(URL fxmlFile) {
        String url = fxmlFile.toString();
        int lastSlash = url.lastIndexOf("/");
        int lastDot = url.lastIndexOf(".");
        return url.substring(lastSlash + 1, lastDot);
    }
}
