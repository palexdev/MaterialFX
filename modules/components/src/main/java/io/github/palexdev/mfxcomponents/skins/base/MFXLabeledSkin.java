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

package io.github.palexdev.mfxcomponents.skins.base;

import java.util.function.BiConsumer;

import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.MFXLabeled;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.fx.TextMeasurementCache;
import javafx.scene.text.Text;

/// Base skin for all labeled control extending from [MFXLabeled], helps reduce code duplication by:
/// - Creating the label node which displays the control's text and allows customizing it by overriding the
/// [#buildLabelNode()]
/// - Allows using a cache to compute the label's sizes. This is optional and must be initialized by calling
/// [#initTextMeasurementCache()]
public abstract class MFXLabeledSkin extends MFXSkinBase<MFXLabeled> {
    //================================================================================
    // Properties
    //================================================================================
    protected final BoundLabel label;
    protected TextMeasurementCache tmCache;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabeledSkin(MFXLabeled labeled) {
        super(labeled);
        label = buildLabelNode();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Initializes the [TextMeasurementCache] instance of this skin.
    /// Implementations that heavily rely on such computations should call this and use [#textWidth()]
    /// and [#textHeight()] to retrieve the text sizes when needed.
    protected void initTextMeasurementCache() {
        if (tmCache == null) tmCache = new TextMeasurementCache(getSkinnable());
    }

    /// Creates the [BoundLabel] which will display the component's text.
    ///
    /// By default, also sets the [BoundLabel#onSetTextNode(BiConsumer)] action to bind the text node opacity property
    /// to [MFXLabeled#textOpacityProperty()].
    protected BoundLabel buildLabelNode() {
        MFXLabeled labeled = getSkinnable();
        BoundLabel bl = new BoundLabel(labeled);
        bl.setMouseTransparent(true);
        bl.onSetTextNode((o, n) -> {
            if (o != null) o.opacityProperty().unbind();
            if (n != null) n.opacityProperty().bind(labeled.textOpacityProperty());
        });
        return bl;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void dispose() {
        if (tmCache != null) {
            tmCache.dispose();
            tmCache = null;
        }
        Text ltn = label.getTextNode();
        if (ltn != null) ltn.opacityProperty().unbind();
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================

    /// Delegate for [TextMeasurementCache#getSnappedWidth()]. If the cache was not initialized before, returns -1.
    protected double textWidth() {
        return (tmCache != null) ? tmCache.getSnappedWidth() : -1.0;
    }

    /// Delegate for [TextMeasurementCache#getSnappedHeight()]. If the cache was not initialized before, returns -1.
    protected double textHeight() {
        return (tmCache != null) ? tmCache.getSnappedHeight() : -1.0;
    }
}
