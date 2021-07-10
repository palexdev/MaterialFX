/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXToggleNode;
import io.github.palexdev.materialfx.controls.enums.TextPosition;
import io.github.palexdev.materialfx.skins.MFXCircleToggleNodeSkin;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.StrokeType;

import java.util.List;

/**
 * This is the implementation of a {@link ToggleButton} with a completely different skin, {@link MFXCircleToggleNodeSkin}.
 * <p></p>
 * Extends {@link ToggleButton} and redefines the style class to "mfx-toggle-node" for usage in CSS.
 * <p>
 * Allows to specify up to three icons: one icon for the toggle, and tho other two for the toggle's label.
 */
public class MFXCircleToggleNode extends AbstractMFXToggleNode {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXCircleToggleNode> FACTORY = new StyleablePropertyFactory<>(AbstractMFXToggleNode.getControlCssMetaDataList());
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXCircleToggleNode.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCircleToggleNode() {
        this("");
    }

    public MFXCircleToggleNode(String text) {
        this(text, null);
    }

    public MFXCircleToggleNode(String text, Node icon) {
        super(text, icon);
    }

    public MFXCircleToggleNode(String text, Node icon, Node leadingIcon, Node trailingIcon) {
        super(text, icon);
        setLabelLeadingIcon(leadingIcon);
        setLabelTrailingIcon(trailingIcon);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            this,
            "size",
            32.0
    );

    private final StyleableObjectProperty<TextPosition> textPosition = new SimpleStyleableObjectProperty<>(
            StyleableProperties.TEXT_POSITION,
            this,
            "textPosition",
            TextPosition.BOTTOM
    );

    private final StyleableDoubleProperty strokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.STROKE_WIDTH,
            this,
            "strokeWidth",
            1.5
    );

    private final StyleableObjectProperty<StrokeType> strokeType = new SimpleStyleableObjectProperty<>(
            StyleableProperties.STROKE_TYPE,
            this,
            "strokeType",
            StrokeType.CENTERED
    );

    public double getSize() {
        return size.get();
    }

    /**
     * Specifies the toggle's radius.
     */
    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    public TextPosition getTextPosition() {
        return textPosition.get();
    }

    /**
     * Specifies the position of the label, above or underneath the toggle's circle.
     */
    public StyleableObjectProperty<TextPosition> textPositionProperty() {
        return textPosition;
    }

    public void setTextPosition(TextPosition textPosition) {
        this.textPosition.set(textPosition);
    }

    public double getStrokeWidth() {
        return strokeWidth.get();
    }

    /**
     * Specifies the stroke width of the toggle.
     */
    public StyleableDoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    public StrokeType getStrokeType() {
        return strokeType.get();
    }

    public StyleableObjectProperty<StrokeType> strokeTypeProperty() {
        return strokeType;
    }

    public void setStrokeType(StrokeType strokeType) {
        this.strokeType.set(strokeType);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXCircleToggleNode, Number> SIZE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-size",
                        MFXCircleToggleNode::sizeProperty,
                        32.0
                );

        private static final CssMetaData<MFXCircleToggleNode, TextPosition> TEXT_POSITION =
                FACTORY.createEnumCssMetaData(
                        TextPosition.class,
                        "-mfx-text-position",
                        MFXCircleToggleNode::textPositionProperty,
                        TextPosition.BOTTOM
                );

        private static final CssMetaData<MFXCircleToggleNode, Number> STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-stroke-width",
                        MFXCircleToggleNode::strokeWidthProperty,
                        1.5
                );

        private static final CssMetaData<MFXCircleToggleNode, StrokeType> STROKE_TYPE =
                FACTORY.createEnumCssMetaData(
                        StrokeType.class,
                        "-mfx-stroke-type",
                        MFXCircleToggleNode::strokeTypeProperty,
                        StrokeType.CENTERED
                );

        static {
            cssMetaDataList = List.of(
                    SIZE, TEXT_POSITION,
                    STROKE_WIDTH, STROKE_TYPE
            );
        }
                
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return MFXCircleToggleNode.StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXCircleToggleNodeSkin(this);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getControlCssMetaDataList();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
