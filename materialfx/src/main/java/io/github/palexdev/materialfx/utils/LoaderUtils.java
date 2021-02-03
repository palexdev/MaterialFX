/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import java.net.URL;

/**
 * Convenience class to avoid duplicated code in {@code MFXHLoader} and {@code MFXVLoader} classes
 */
public class LoaderUtils {

    private LoaderUtils() {
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
