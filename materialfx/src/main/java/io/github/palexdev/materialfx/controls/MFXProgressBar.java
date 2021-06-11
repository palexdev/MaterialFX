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
import io.github.palexdev.materialfx.skins.MFXProgressBarSkin;
import javafx.css.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * This is the implementation of a progress bar following Google's material design guidelines.
 * <p>
 * Extends {@code ProgressBar} and redefines the style class to "mfx-progress-bar" for usage in CSS.
 */
public class MFXProgressBar extends ProgressBar {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXProgressBar> FACTORY = new StyleablePropertyFactory<>(ProgressBar.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-progress-bar";
    private final String STYLESHEETS = MFXResourcesLoader.load("css/MFXProgressBar.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXProgressBar() {
        initialize();
    }

    public MFXProgressBar(double progress) {
        super(progress);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setPrefWidth(200);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty animationSpeed = new SimpleStyleableDoubleProperty(
            StyleableProperties.ANIMATION_SPEED,
            this,
            "animationSpeed",
            1.0
    );

    public double getAnimationSpeed() {
        return animationSpeed.get();
    }

    /**
     * Specifies the indeterminate animation speed.
     */
    public StyleableDoubleProperty animationSpeedProperty() {
        return animationSpeed;
    }

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed.set(animationSpeed);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXProgressBar, Number> ANIMATION_SPEED =
                FACTORY.createSizeCssMetaData(
                        "-mfx-animation-speed",
                        MFXProgressBar::animationSpeedProperty,
                        1.0
                );

        static {
            cssMetaDataList = List.of(ANIMATION_SPEED);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXProgressBarSkin(this);
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEETS;
    }
}
