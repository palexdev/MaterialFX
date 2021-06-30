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

package io.github.palexdev.materialfx.controls.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMFXToggleNode extends ToggleButton {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<AbstractMFXToggleNode> FACTORY = new StyleablePropertyFactory<>(ToggleButton.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-toggle-node";

    private final ObjectProperty<Node> labelLeadingIcon = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> labelTrailingIcon = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public AbstractMFXToggleNode() {
        initialize();
    }

    public AbstractMFXToggleNode(String text) {
        super(text);
        initialize();
    }

    public AbstractMFXToggleNode(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public Node getLabelLeadingIcon() {
        return labelLeadingIcon.get();
    }

    /**
     * Specifies the label's leading icon.
     */
    public ObjectProperty<Node> labelLeadingIconProperty() {
        return labelLeadingIcon;
    }

    public void setLabelLeadingIcon(Node labelLeadingIcon) {
        this.labelLeadingIcon.set(labelLeadingIcon);
    }

    public Node getLabelTrailingIcon() {
        return labelTrailingIcon.get();
    }

    /**
     * Specifies the label's trailing icon.
     */
    public ObjectProperty<Node> labelTrailingIconProperty() {
        return labelTrailingIcon;
    }

    public void setLabelTrailingIcon(Node labelTrailingIcon) {
        this.labelTrailingIcon.set(labelTrailingIcon);
    }


    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Paint> unselectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_COLOR,
            this,
            "unselectedColor",
            Color.web("#F5F5F5")
    );

    private final StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_COLOR,
            this,
            "selectedColor",
            Color.web("#EDEDED")
    );

    private final StyleableObjectProperty<Paint> unselectedBorderColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_BORDER_COLOR,
            this,
            "unselectedBorderColor",
            Color.web("#E1E1E1")
    );

    private final StyleableObjectProperty<Paint> selectedBorderColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_BORDER_COLOR,
            this,
            "selectedBorderColor",
            Color.web("#E1E1E1")
    );

    private final StyleableDoubleProperty labelTextGap = new SimpleStyleableDoubleProperty(
            StyleableProperties.LABEL_TEXT_GAP,
            this,
            "labelTextGap",
            10.0
    );


    public Paint getUnselectedColor() {
        return unselectedColor.get();
    }

    /**
     * Specifies the toggle's color when it's not selected.
     */
    public StyleableObjectProperty<Paint> unselectedColorProperty() {
        return unselectedColor;
    }

    public void setUnselectedColor(Paint unselectedColor) {
        this.unselectedColor.set(unselectedColor);
    }

    public Paint getSelectedColor() {
        return selectedColor.get();
    }

    /**
     * Specifies the toggle's color when it's selected.
     */
    public StyleableObjectProperty<Paint> selectedColorProperty() {
        return selectedColor;
    }

    public void setSelectedColor(Paint selectedColor) {
        this.selectedColor.set(selectedColor);
    }

    public Paint getUnselectedBorderColor() {
        return unselectedBorderColor.get();
    }

    /**
     * Specifies the toggle borders color when not selected.
     */
    public StyleableObjectProperty<Paint> unselectedBorderColorProperty() {
        return unselectedBorderColor;
    }

    public void setUnselectedBorderColor(Paint unselectedBorderColor) {
        this.unselectedBorderColor.set(unselectedBorderColor);
    }

    public Paint getSelectedBorderColor() {
        return selectedBorderColor.get();
    }

    /**
     * Specifies the toggle borders color when selected.
     */
    public StyleableObjectProperty<Paint> selectedBorderColorProperty() {
        return selectedBorderColor;
    }

    public void setSelectedBorderColor(Paint selectedBorderColor) {
        this.selectedBorderColor.set(selectedBorderColor);
    }

    public double getLabelTextGap() {
        return labelTextGap.get();
    }

    /**
     * Specifies the gap between the toggle's circle and the label.
     */
    public StyleableDoubleProperty labelTextGapProperty() {
        return labelTextGap;
    }

    public void setLabelTextGap(double labelTextGap) {
        this.labelTextGap.set(labelTextGap);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<AbstractMFXToggleNode, Paint> UNSELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-color",
                        AbstractMFXToggleNode::unselectedColorProperty,
                        Color.web("#F5F5F5")
                );

        private static final CssMetaData<AbstractMFXToggleNode, Paint> SELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-color",
                        AbstractMFXToggleNode::selectedColorProperty,
                        Color.web("#EDEDED")
                );

        private static final CssMetaData<AbstractMFXToggleNode, Paint> UNSELECTED_BORDER_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-border-color",
                        AbstractMFXToggleNode::unselectedBorderColorProperty,
                        Color.web("#E1E1E1")
                );

        private static final CssMetaData<AbstractMFXToggleNode, Paint> SELECTED_BORDER_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-border-color",
                        AbstractMFXToggleNode::selectedBorderColorProperty,
                        Color.web("#E1E1E1")
                );

        private static final CssMetaData<AbstractMFXToggleNode, Number> LABEL_TEXT_GAP =
                FACTORY.createSizeCssMetaData(
                        "-mfx-label-text-gap",
                        AbstractMFXToggleNode::labelTextGapProperty,
                        10.0
                );

        static {
            List<CssMetaData<? extends Styleable, ?>> tonCssMetaData = new ArrayList<>(ToggleButton.getClassCssMetaData());
            Collections.addAll(tonCssMetaData, UNSELECTED_COLOR, SELECTED_COLOR, UNSELECTED_BORDER_COLOR, SELECTED_BORDER_COLOR, LABEL_TEXT_GAP);
            cssMetaDataList = Collections.unmodifiableList(tonCssMetaData);
        }

    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return AbstractMFXToggleNode.StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getControlCssMetaDataList();
    }
}
