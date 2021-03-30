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
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.skins.MFXButtonSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;

/**
 * This is the implementation of a button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Button}, redefines the style class to "mfx-button" for usage in CSS and
 * includes a {@code RippleGenerator} to generate ripple effects on click.
 */
public class MFXButton extends Button {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXButton> FACTORY = new StyleablePropertyFactory<>(Button.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-button";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-button.css");
    private final RippleGenerator rippleGenerator = new RippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButton() {
        setText("Button");
        initialize();
    }

    public MFXButton(String text) {
        super(text);
        initialize();
    }

    public MFXButton(String text, double prefWidth, double prefHeight) {
        super(text);
        setPrefSize(prefWidth, prefHeight);
        initialize();
    }

    public MFXButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.CENTER);

        setRippleRadius(25);
        setRippleColor(Color.rgb(190, 190, 190));
    }

    public RippleGenerator getRippleGenerator() {
        return this.rippleGenerator;
    }

    //================================================================================
    // Ripple properties
    //================================================================================
    private final ObjectProperty<Paint> rippleColor = new SimpleObjectProperty<>();
    private final DoubleProperty rippleRadius = new SimpleDoubleProperty();
    private final ObjectProperty<Duration> rippleInDuration = new SimpleObjectProperty<>();
    private final ObjectProperty<Duration> rippleOutDuration = new SimpleObjectProperty<>();

    public final Paint getRippleColor() {
        return rippleColor.get();
    }

    /**
     * Specifies the ripples color of this control.
     */
    public final ObjectProperty<Paint> rippleColorProperty() {
        return this.rippleColor;
    }

    public final void setRippleColor(Paint rippleColor) {
        rippleGenerator.setRippleColor(rippleColor);
    }

    public double getRippleRadius() {
        return rippleRadius.get();
    }

    /**
     * Specifies the ripples radius of this control.
     */
    public DoubleProperty rippleRadiusProperty() {
        return rippleRadius;
    }

    public void setRippleRadius(double rippleRadius) {
        rippleGenerator.setRippleRadius(rippleRadius);
    }

    public Duration getRippleInDuration() {
        return rippleInDuration.get();
    }

    /**
     * Specifies the ripples in animation duration of this control.
     */
    public ObjectProperty<Duration> rippleInDurationProperty() {
        return rippleInDuration;
    }

    public void setRippleInDuration(Duration rippleInDuration) {
        rippleGenerator.setInDuration(rippleInDuration);
    }

    public Duration getRippleOutDuration() {
        return rippleOutDuration.get();
    }

    /**
     * Specifies the ripples out animation duration of this control.
     */
    public ObjectProperty<Duration> rippleOutDurationProperty() {
        return rippleOutDuration;
    }

    public void setRippleOutDuration(Duration rippleOutDuration) {
        rippleGenerator.setOutDuration(rippleOutDuration);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
            StyleableProperties.DEPTH_LEVEL,
            this,
            "depthLevel",
            DepthLevel.LEVEL2
    );

    private final StyleableObjectProperty<ButtonType> buttonType = new SimpleStyleableObjectProperty<>(
            StyleableProperties.BUTTON_TYPE,
            this,
            "buttonType",
            ButtonType.FLAT
    );

    public DepthLevel getDepthLevel() {
        return depthLevel.get();
    }

    /**
     * Specifies how intense is the {@code DropShadow} effect applied to this control.
     * <p>
     * The {@code DropShadow} effect is used to make the control appear {@code RAISED}.
     *
     * @see io.github.palexdev.materialfx.effects.MFXDepthManager
     */
    public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
        return depthLevel;
    }

    public void setDepthLevel(DepthLevel depthLevel) {
        this.depthLevel.set(depthLevel);
    }

    public ButtonType getButtonType() {
        return buttonType.get();
    }

    /**
     * Specifies the appearance of this control. According to material design there are two types of buttons:
     * <p>
     * - {@code FLAT}
     * <p>
     * - {@code RAISED}
     */
    public StyleableObjectProperty<ButtonType> buttonTypeProperty() {
        return buttonType;
    }

    public void setButtonType(ButtonType buttonType) {
        this.buttonType.set(buttonType);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXButton, DepthLevel> DEPTH_LEVEL =
                FACTORY.createEnumCssMetaData(
                        DepthLevel.class,
                        "-mfx-depth-level",
                        MFXButton::depthLevelProperty,
                        DepthLevel.LEVEL2
                );

        private static final CssMetaData<MFXButton, ButtonType> BUTTON_TYPE =
                FACTORY.createEnumCssMetaData(
                        ButtonType.class,
                        "-mfx-button-type",
                        MFXButton::buttonTypeProperty,
                        ButtonType.FLAT);

        static {
            cssMetaDataList = List.of(DEPTH_LEVEL, BUTTON_TYPE);
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
        return new MFXButtonSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXButton.getControlCssMetaDataList();
    }
}
