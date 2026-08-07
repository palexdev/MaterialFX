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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.Map;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;

import static io.github.palexdev.mfxcore.base.beans.Size.size;

/// A specialization of [TextMeasurementCache] that operates specifically on [Labels][Labeled].
///
/// Unlike [TextMeasurementCache], this computes the full size of the label, including: padding, graphic, graphic text gap.
/// It can also handle the various configurations of [Labeled#contentDisplayProperty()].
///
/// To further improve the performance, we still internally use a [TextMeasurementCache] to measure the text size.
public class LabelMeasurementCache extends ObjectBinding<Size> {
    //================================================================================
    // Properties
    //================================================================================
    private Labeled label;
    private Function<Double, Double> xSnappingFunction;
    private Function<Double, Double> ySnappingFunction;

    private TextMeasurementCache tmc;

    //================================================================================
    // Constructors
    //================================================================================
    public LabelMeasurementCache(Labeled label) {
        this.label = label;
        xSnappingFunction = label::snapSizeX;
        ySnappingFunction = label::snapSizeY;
        tmc = new TextMeasurementCache(label);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        bind(
            tmc,
            label.contentDisplayProperty(),
            label.graphicProperty(), label.graphicTextGapProperty(),
            label.paddingProperty()
        );
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Size computeValue() {
        Node graphic = label.getGraphic();
        double hGap = graphic != null ? label.getGraphicTextGap() : 0.0;
        Size gSize = graphic != null ?
            size(LayoutUtils.snappedBoundWidth(graphic), LayoutUtils.snappedBoundHeight(graphic)) :
            Size.zero();
        Size tSize = tmc.getValue();
        double hPadding = label.snappedLeftInset() + label.snappedRightInset();
        double vPadding = label.snappedTopInset() + label.snappedBottomInset();
        return ContentDisplayHandler.handlerFor(label.getContentDisplay())
            .compute(tSize, gSize, hGap, hPadding, vPadding);
    }

    @Override
    public void dispose() {
        unbind(
            tmc,
            label.contentDisplayProperty(),
            label.graphicProperty(), label.graphicTextGapProperty(),
            label.paddingProperty()
        );
        tmc.dispose();
        tmc = null;
        label = null;
        super.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// Applies the [#getXSnappingFunction()] on the [Size#width()] value of this binding.
    public double getSnappedWidth() {
        return xSnappingFunction.apply(getValue().width());
    }

    /// Applies the [#getYSnappingFunction()] on the [Size#height()] value of this binding.
    public double getSnappedHeight() {
        return ySnappingFunction.apply(getValue().height());
    }

    /// @return the function responsible for rounding the computed width values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel
    public Function<Double, Double> getXSnappingFunction() {
        return xSnappingFunction;
    }

    /// Sets the function responsible for rounding the computed width values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel.
    public void setXSnappingFunction(Function<Double, Double> xSnappingFunction) {
        this.xSnappingFunction = xSnappingFunction;
    }

    /// @return the function responsible for rounding the computed height values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel
    public Function<Double, Double> getYSnappingFunction() {
        return ySnappingFunction;
    }

    /// Sets the function responsible for rounding the computed height values.
    /// In JavaFX, you usually want to round "raw" values to the closest pixel.
    public void setYSnappingFunction(Function<Double, Double> ySnappingFunction) {
        this.ySnappingFunction = ySnappingFunction;
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    public enum ContentDisplayHandler {
        VERTICAL {
            @Override
            public Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding) {
                return size(
                    hPadding + Math.max(textSize.width(), graphicSize.width()),
                    vPadding + textSize.height() + gap + graphicSize.height()
                );
            }
        },

        HORIZONTAL {
            @Override
            public Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding) {
                return size(
                    hPadding + textSize.width() + gap + graphicSize.width(),
                    vPadding + Math.max(textSize.height(), graphicSize.height())
                );
            }
        },

        CENTER {
            @Override
            public Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding) {
                return size(
                    hPadding + Math.max(textSize.width(), graphicSize.width()),
                    vPadding + Math.max(textSize.height(), graphicSize.height())
                );
            }
        },

        GRAPHIC_ONLY {
            @Override
            public Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding) {
                return size(
                    hPadding + graphicSize.width(),
                    vPadding + graphicSize.height()
                );
            }
        },

        TEXT_ONLY {
            @Override
            public Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding) {
                return size(
                    hPadding + textSize.width(),
                    vPadding + textSize.height()
                );
            }
        };;

        private static final Map<ContentDisplay, ContentDisplayHandler> HANDLERS = Map.of(
            ContentDisplay.TOP, VERTICAL,
            ContentDisplay.BOTTOM, VERTICAL,
            ContentDisplay.LEFT, HORIZONTAL,
            ContentDisplay.RIGHT, HORIZONTAL,
            ContentDisplay.CENTER, CENTER,
            ContentDisplay.GRAPHIC_ONLY, GRAPHIC_ONLY,
            ContentDisplay.TEXT_ONLY, TEXT_ONLY
        );

        public static ContentDisplayHandler handlerFor(ContentDisplay cd) {
            return HANDLERS.get(cd);
        }

        public abstract Size compute(Size textSize, Size graphicSize, double gap, double hPadding, double vPadding);
    }
}
