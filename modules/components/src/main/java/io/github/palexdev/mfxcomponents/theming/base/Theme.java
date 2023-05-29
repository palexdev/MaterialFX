/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.theming.base;

import io.github.palexdev.mfxresources.MFXResources;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Public API for all MaterialFX themes/stylesheets. Ideally every theme should have: a name that identifies it,
 * and the path at which it is located.
 * <p>
 * Very important is the load mechanism. This interface assumes that stylesheets reside in the MFXResources module, and
 * thus uses {@link MFXResources} to load them. Custom user themes must override {@link #get()}!
 */
public interface Theme {

    /**
     * @return the theme's name
     */
    String name();

    /**
     * @return the path at which the stylesheet is located as a string
     */
    String path();

    /**
     * Responsible for loading the stylesheet specified by {@link #path()}.
     * <p>
     * This assumes that the theme comes from the MFXResources module and thus uses {@link MFXResources} to load it.
     * Custom user themes must override this method!.
     * <p></p>
     * Last but not least, themes loaded through this are automatically cached for faster subsequent loadings.
     */
    default URL get() {
        if (Helper.isCached(this) && Helper.getCachedTheme(this) != null)
            return Helper.getCachedTheme(this);
        return Helper.cacheTheme(this, MFXResources.loadURL(path()));
    }

    /**
     * @return the {@link URL} loaded through {@link #get()} as a string, see {@link URL#toExternalForm()}
     */
    default String toData() {
        return get().toExternalForm();
    }

    /**
     * Adds the loaded theme ({@link #toData()}) to the given {@link Scene}.
     */
    default void applyOn(Scene scene) {
        scene.getStylesheets().add(toData());
    }

    /**
     * Adds the loaded theme ({@link #toData()}) to the given {@link Parent}.
     */
    default void applyOn(Parent parent) {
        parent.getStylesheets().add(toData());
    }

    class Helper {
        private static final Map<Theme, URL> CACHE = new HashMap<>();

        public static boolean isCached(Theme theme) {
            return CACHE.containsKey(theme);
        }

        public static URL cacheTheme(Theme theme, URL url) {
            CACHE.put(theme, url);
            return url;
        }

        public static URL getCachedTheme(Theme theme) {
            return CACHE.get(theme);
        }
    }
}
