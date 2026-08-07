/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.enums;

import io.github.palexdev.mfxcore.utils.fx.loader.MFXLoader;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/// Enumerator to define the level of caching used by [MFXLoader].
///
/// By enabling cache, the switch performance vastly improves, but the views cannot be loaded in parallel
/// (quite an acceptable loss in some cases).
public enum LoaderCacheLevel {
    /// No caching. May lag a little on some occasions
    /// (when the root contains a huge number of nodes, for example).
    NONE,

    /// The root node is added to a dummy pane and [Scene], then [Node#applyCss()] and [Parent#layout()] are called.
    /// This causes all nodes in the scene to create their skin and init the layout,
    /// thus "caching" the scenegraph. Vastly improves view switching performance.
    SCENE_CACHE,

    /// Does what SCENE_CACHE does, plus sets the JavaFX's properties: [Node#cacheProperty()] to `true`
    /// and [Node#cacheHintProperty()] to [CacheHint#SPEED] on the loaded root node.
    ///
    /// (To be honest, I don't know if this truly improves performance since I didn't notice anything notable)
    SCENE_JAVAFX_CACHE
}
