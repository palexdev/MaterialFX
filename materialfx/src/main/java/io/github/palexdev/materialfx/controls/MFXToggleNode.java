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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.enums.ToggleNodeShape;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;

/**
 * This control is basically a {@code ToggleButton} but it is mostly used to contain graphic rather than text.
 * It's also possible to make it appear circular for a modern like design.
 * <p>
 * Extends {@code ToggleButton}, redefines the style class to "mfx-toggle-node" for usage in CSS and
 * includes a {@code RippleGenerator} to generate ripple effects on click.
 */
public class MFXToggleNode extends ToggleButton {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXToggleNode> FACTORY = new StyleablePropertyFactory<>(ToggleButton.getClassCssMetaData());
    private final String STYLECLASS = "mfx-toggle-node";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-togglenode.css");
    protected final RippleGenerator rippleGenerator = new RippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXToggleNode() {
        setText("");
        initialize();
    }

    public MFXToggleNode(String text) {
        super(text);
        initialize();
    }

    public MFXToggleNode(Node graphic) {
        super("", graphic);
        initialize();
    }

    public MFXToggleNode(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLECLASS);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setupRippleGenerator();

        prefWidthProperty().bind(size);
        prefHeightProperty().bind(size);
        clip();

        addListeners();
        setSize(40);
    }

    /**
     * Adds listener for toggleShapeProperty, sizeProperty and selectedProperty
     */
    private void addListeners() {
        toggleShapeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(ToggleNodeShape.CIRCLE)) {
                prefWidthProperty().bind(size);
                prefHeightProperty().bind(size);
                clip();
            } else {
                setClip(null);
                prefWidthProperty().bind(size.multiply(3.5));
                prefHeightProperty().bind(size);
            }
        });

        sizeProperty().addListener((sObservable, sOldValue, sNewValue) -> {
            if (sNewValue.doubleValue() != sOldValue.doubleValue()) {
                setSize(sNewValue.doubleValue());
            }
        });

        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setBackground(new Background(new BackgroundFill(selectedColor.get(), CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                setBackground(new Background(new BackgroundFill(unSelectedColor.get(), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
    }

    /**
     * Resets the clip
     */
    private void clip() {
        setClip(null);
        NodeUtils.makeRegionCircular(this);
    }

    protected void setupRippleGenerator() {
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setRippleColor(Color.GRAY);
        rippleGenerator.setInDuration(Duration.millis(350));

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies the size (both width and height) of the control.
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            this,
            "size",
            40.0
    );

    /**
     * Specifies the shape of the control
     */
    private final StyleableObjectProperty<ToggleNodeShape> toggleShape = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SHAPE,
            this,
            "toggleShape",
            ToggleNodeShape.CIRCLE
    );

    /**
     * Specifies the background color when selected.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_COLOR,
            this,
            "selectedColor",
            Color.rgb(190, 190, 190, 0.5)
    );

    /**
     * Specifies the background color when unselected.
     *
     * @see Color
     */
    private final StyleableObjectProperty<Paint> unSelectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_COLOR,
            this,
            "unSelectedColor",
            Color.TRANSPARENT
    );

    public double getSize() {
        return size.get();
    }

    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    public ToggleNodeShape getToggleShape() {
        return toggleShape.get();
    }

    public StyleableObjectProperty<ToggleNodeShape> toggleShapeProperty() {
        return toggleShape;
    }

    public void setToggleShape(ToggleNodeShape toggleShape) {
        this.toggleShape.set(toggleShape);
    }

    public Paint getSelectedColor() {
        return selectedColor.get();
    }

    public StyleableObjectProperty<Paint> selectedColorProperty() {
        return selectedColor;
    }

    public void setSelectedColor(Paint selectedColor) {
        this.selectedColor.set(selectedColor);
    }

    public Paint getUnSelectedColor() {
        return unSelectedColor.get();
    }

    public StyleableObjectProperty<Paint> unSelectedColorProperty() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(Paint unSelectedColor) {
        this.unSelectedColor.set(unSelectedColor);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXToggleNode, Number> SIZE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-size",
                        MFXToggleNode::sizeProperty,
                        40
                );

        private static final CssMetaData<MFXToggleNode, ToggleNodeShape> SHAPE =
                FACTORY.createEnumCssMetaData(
                        ToggleNodeShape.class,
                        "-mfx-shape",
                        MFXToggleNode::toggleShapeProperty,
                        ToggleNodeShape.CIRCLE
                );

        private static final CssMetaData<MFXToggleNode, Paint> SELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-color",
                        MFXToggleNode::selectedColorProperty,
                        Color.rgb(0, 0, 0, 0.2)
                );

        private static final CssMetaData<MFXToggleNode, Paint> UNSELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-color",
                        MFXToggleNode::unSelectedColorProperty,
                        Color.TRANSPARENT
                );

        static {
            cssMetaDataList = List.of(SIZE, SHAPE, SELECTED_COLOR, UNSELECTED_COLOR);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        ToggleButtonSkin skin = new ToggleButtonSkin(this);
        getChildren().add(0, rippleGenerator);
        return skin;
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXToggleNode.getControlCssMetaDataList();
    }
}
