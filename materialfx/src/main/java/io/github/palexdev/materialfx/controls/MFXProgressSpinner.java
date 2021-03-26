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
import io.github.palexdev.materialfx.skins.MFXProgressSpinnerSkin;
import javafx.css.*;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.List;

/**
 * Implementation of a spinning {@code ProgressIndicator}.
 * <p>
 * Extends {@link ProgressIndicator}
 */
public class MFXProgressSpinner extends ProgressIndicator {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXProgressSpinner> FACTORY = new StyleablePropertyFactory<>(ProgressIndicator.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-progress-spinner";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-progress-spinner.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXProgressSpinner() {
        this(-1);
    }

    public MFXProgressSpinner(double progress) {
        super(progress);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies the radius of the spinner.
     */
    private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
            StyleableProperties.RADIUS,
            this,
            "radius",
            Region.USE_COMPUTED_SIZE
    );

    /**
     * Specifies the starting angle of the animation.
     */
    private final StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(
            StyleableProperties.STARTING_ANGLE,
            this,
            "startingAngle",
            360 - Math.random() * 720
    );

    public double getRadius() {
        return radius.get();
    }

    public StyleableDoubleProperty radiusProperty() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public double getStartingAngle() {
        return startingAngle.get();
    }

    public StyleableDoubleProperty startingAngleProperty() {
        return startingAngle;
    }

    public void setStartingAngle(double startingAngle) {
        this.startingAngle.set(startingAngle);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXProgressSpinner, Number> RADIUS =
                FACTORY.createSizeCssMetaData(
                        "-mfx-radius",
                        MFXProgressSpinner::radiusProperty,
                        Region.USE_COMPUTED_SIZE
                );

        private static final CssMetaData<MFXProgressSpinner, Number> STARTING_ANGLE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-starting-angle",
                        MFXProgressSpinner::startingAngleProperty,
                        360 - Math.random() * 720
                );

        static {
            cssMetaDataList = List.of(RADIUS, STARTING_ANGLE);
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
        return new MFXProgressSpinnerSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXProgressSpinner.getControlCssMetaDataList();
    }
}
