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
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.skins.MFXProgressSpinnerSkin;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.palexdev.materialfx.utils.NodeUtils.isPseudoClassActive;

/**
 * Implementation of a spinning {@code ProgressIndicator}.
 * <p>
 * Extends {@link ProgressIndicator}.
 * <p></p>
 * MFXProgressSpinner introduces three new css pseudo classes:
 * <p> - ":range1", activated when the spinner value is contained in any of the ranges specified in here {@link #getRanges1()}
 * <p> - ":range2", activated when the spinner value is contained in any of the ranges specified in here {@link #getRanges2()}
 * <p> - ":range3", activated when the spinner value is contained in any of the ranges specified in here {@link #getRanges3()}
 * <p>
 * I know this may seem a strange approach, but it is much more flexible and allows for a lot more customization.
 */
public class MFXProgressSpinner extends ProgressIndicator {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXProgressSpinner> FACTORY = new StyleablePropertyFactory<>(ProgressIndicator.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-progress-spinner";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXProgressSpinner.css");

    private final ObservableList<NumberRange<Double>> ranges1 = FXCollections.observableArrayList();
    private final ObservableList<NumberRange<Double>> ranges2 = FXCollections.observableArrayList();
    private final ObservableList<NumberRange<Double>> ranges3 = FXCollections.observableArrayList();
    protected final PseudoClass RANGE1_PSEUDO_CLASS = PseudoClass.getPseudoClass("range1");
    protected final PseudoClass RANGE2_PSEUDO_CLASS = PseudoClass.getPseudoClass("range2");
    protected final PseudoClass RANGE3_PSEUDO_CLASS = PseudoClass.getPseudoClass("range3");

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
        addListeners();
    }

    private void addListeners() {
        ranges1.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
        ranges2.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
        ranges3.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
        progressProperty().addListener((observable, oldValue, newValue) -> handlePseudoClasses());
    }

    /**
     * Handles the ":range1", ":range2" and ":range3" css pseudo classes when these properties change:
     * {@link #progressProperty()}, {@link #getRanges1()}, {@link #getRanges2()}, {@link #getRanges3()}.
     */
    private void handlePseudoClasses() {
        double val = getProgress();
        if (!isPseudoClassActive(this, RANGE1_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges1)) {
            pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, true);
            pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, false);
            pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, false);
        } else if (!isPseudoClassActive(this, RANGE2_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges2)) {
            pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, true);
            pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, false);
            pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, false);
        } else if (!isPseudoClassActive(this, RANGE3_PSEUDO_CLASS) && NumberRange.inRangeOf(val, ranges3)) {
            pseudoClassStateChanged(RANGE3_PSEUDO_CLASS, true);
            pseudoClassStateChanged(RANGE1_PSEUDO_CLASS, false);
            pseudoClassStateChanged(RANGE2_PSEUDO_CLASS, false);
        }
    }

    /**
     * Returns the first list of ranges.
     */
    public ObservableList<NumberRange<Double>> getRanges1() {
        return ranges1;
    }

    /**
     * Returns the second list of ranges.
     */
    public ObservableList<NumberRange<Double>> getRanges2() {
        return ranges2;
    }

    /**
     * Returns the third list of ranges.
     */
    public ObservableList<NumberRange<Double>> getRanges3() {
        return ranges3;
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
            List<CssMetaData<? extends Styleable, ?>> priCssMetaData = new ArrayList<>(ProgressIndicator.getClassCssMetaData());
            Collections.addAll(priCssMetaData, RADIUS, STARTING_ANGLE);
            cssMetaDataList = Collections.unmodifiableList(priCssMetaData);
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
