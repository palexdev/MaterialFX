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
import io.github.palexdev.materialfx.controls.enums.SliderEnum.SliderMode;
import io.github.palexdev.materialfx.controls.enums.SliderEnum.SliderPopupSide;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXSliderSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextBoundsType;

import java.util.List;
import java.util.function.Supplier;

public class MFXSlider extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXSlider> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-slider";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXSlider.css");

    private final DoubleProperty min = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            if (newValue > getMax()) {
                throw new IllegalArgumentException("The minimum value cannot be greater than the max value");
            }
            super.set(newValue);
        }
    };
    private final DoubleProperty max = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            if (newValue < getMin()) {
                throw new IllegalArgumentException("The maximum value cannot be lesser than the min value");
            }
            super.set(newValue);
        }
    };
    private final DoubleProperty value = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            double clamped = NumberUtils.clamp(newValue, getMin(), getMax());
            super.set(NumberUtils.formatTo(clamped, getDecimalPrecision()));
        }
    };
    private final ObjectProperty<Supplier<Node>> thumbSupplier = new SimpleObjectProperty<>() {
        @Override
        public void set(Supplier<Node> newValue) {
            Node node = newValue.get();
            if (node != null) {
                super.set(newValue);
            } else {
                throw new NullPointerException("Thumb supplier not set as the return values was null!");
            }
        }
    };
    private final ObjectProperty<Supplier<Region>> popupSupplier = new SimpleObjectProperty<>();
    private final DoubleProperty popupPadding = new SimpleDoubleProperty(5.0);
    private final IntegerProperty decimalPrecision = new SimpleIntegerProperty(0);

    private final DoubleProperty cssVal = new SimpleDoubleProperty();
    protected final PseudoClass MIN_PSEUDO_CLASS = PseudoClass.getPseudoClass("min");
    protected final PseudoClass MAX_PSEUDO_CLASS = PseudoClass.getPseudoClass("max");
    protected final PseudoClass VAL_PSEUDO_CLASS = PseudoClass.getPseudoClass("val");

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSlider() {
        this(0);
    }

    public MFXSlider(double initialValue) {
        this(0, 100, initialValue);
    }

    public MFXSlider(double min, double max, double initialValue) {
        if (min > max) {
            throw new IllegalArgumentException("The minimum value cannot be greater than the max value");
        }

        setMin(min);
        setMax(max);
        setValue(NumberUtils.clamp(initialValue, min, max));

        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        addListeners();

        defaultThumbSupplier();
        defaultPopupSupplier();
    }

    private void addListeners() {
        min.addListener((observable, oldValue, newValue) -> handlePseudoClasses());
        max.addListener((observable, oldValue, newValue) -> handlePseudoClasses());
        value.addListener((observable, oldValue, newValue) -> handlePseudoClasses());
        cssVal.addListener((observable, oldValue, newValue) -> handlePseudoClasses());
    }

    private void handlePseudoClasses() {
        pseudoClassStateChanged(MIN_PSEUDO_CLASS, false);
        pseudoClassStateChanged(MAX_PSEUDO_CLASS, false);
        pseudoClassStateChanged(VAL_PSEUDO_CLASS, false);

        double val = getValue();
        if (val == getMin()) {
            pseudoClassStateChanged(MIN_PSEUDO_CLASS, true);
        } else if (val == getMax()) {
            pseudoClassStateChanged(MAX_PSEUDO_CLASS, true);
        } else if (val == getCssVal()) {
            pseudoClassStateChanged(VAL_PSEUDO_CLASS, true);
        }
    }

    protected void defaultThumbSupplier() {
        setThumbSupplier(() -> {
            MFXFontIcon thumb = new MFXFontIcon("mfx-circle", 12);
            MFXFontIcon thumbRadius = new MFXFontIcon("mfx-circle", 30);
            thumb.setMouseTransparent(true);
            thumbRadius.setMouseTransparent(true);
            thumb.getStyleClass().setAll("thumb");
            thumbRadius.getStyleClass().setAll("thumb-radius");

            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("thumb-container");
            stackPane.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            stackPane.setPrefSize(NodeUtils.getNodeWidth(thumb), NodeUtils.getNodeHeight(thumb));
            stackPane.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            stackPane.getChildren().setAll(thumb, thumbRadius);

            MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(stackPane);
            rippleGenerator.setAnimateBackground(false);
            rippleGenerator.setAnimationSpeed(2);
            rippleGenerator.setCheckBounds(false);
            rippleGenerator.setClipSupplier(() -> null);
            rippleGenerator.setMouseTransparent(true);
            rippleGenerator.setRadiusMultiplier(2.5);
            rippleGenerator.setRippleRadius(6);
            rippleGenerator.setRipplePositionFunction(mouseEvent -> new RipplePosition(stackPane.getWidth() / 2, stackPane.getHeight() / 2));
            stackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
            stackPane.getChildren().add(rippleGenerator);

            return stackPane;
        });
    }

    protected void defaultPopupSupplier() {
        setPopupSupplier(() -> {
            Label text = new Label();
            text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            text.setAlignment(Pos.CENTER);
            text.setId("popupText");
            text.textProperty().bind(Bindings.createStringBinding(
                    () -> NumberUtils.formatToString(getValue(), getDecimalPrecision()),
                    value
            ));

            text.rotateProperty().bind(Bindings.createDoubleBinding(
                    () -> getPopupSide() == SliderPopupSide.DEFAULT ? 0.0 : 180.0,
                    popupSideProperty()
            ));

            MFXFontIcon caret = new MFXFontIcon("mfx-caret-down", 22);
            caret.setId("popupCaret");
            caret.setBoundsType(TextBoundsType.VISUAL);
            caret.setManaged(false);

            StackPane stackPane = new StackPane(text);
            stackPane.setId("popupContent");
            VBox.setVgrow(stackPane, Priority.ALWAYS);

            VBox container = new VBox(stackPane, caret) {
                @Override
                protected void layoutChildren() {
                    super.layoutChildren();

                    Orientation orientation = getOrientation();
                    double x = orientation == Orientation.HORIZONTAL ? (getWidth() / 2) - (caret.prefWidth(-1) / 2) : getHeight();
                    double y = orientation == Orientation.HORIZONTAL ? getHeight() : -(caret.prefHeight(-1) / 2) + (getHeight() / 2);
                    caret.relocate(snapPositionX(x), snapPositionY(y));
                }
            };
            container.setAlignment(Pos.TOP_CENTER);
            container.setMinSize(45, 40);
            container.getStylesheets().add(STYLESHEET);

            caret.rotateProperty().bind(Bindings.createDoubleBinding(
                    () -> {
                        container.requestLayout();
                        return getOrientation() == Orientation.HORIZONTAL ? 0.0 : -90;
                    },
                    needsLayoutProperty(), rotateProperty(), popupSideProperty()
            ));

            return container;
        });
    }

    public double getMin() {
        return min.get();
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public void setMin(double min) {
        this.min.set(min);
    }

    public double getMax() {
        return max.get();
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public void setMax(double max) {
        this.max.set(max);
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public Supplier<Node> getThumbSupplier() {
        return thumbSupplier.get();
    }

    public ObjectProperty<Supplier<Node>> thumbSupplierProperty() {
        return thumbSupplier;
    }

    public void setThumbSupplier(Supplier<Node> thumbSupplier) {
        this.thumbSupplier.set(thumbSupplier);
    }

    public Supplier<Region> getPopupSupplier() {
        return popupSupplier.get();
    }

    public ObjectProperty<Supplier<Region>> popupSupplierProperty() {
        return popupSupplier;
    }

    public void setPopupSupplier(Supplier<Region> popupSupplier) {
        this.popupSupplier.set(popupSupplier);
    }

    public double getPopupPadding() {
        return popupPadding.get();
    }

    public DoubleProperty popupPaddingProperty() {
        return popupPadding;
    }

    public void setPopupPadding(double popupPadding) {
        this.popupPadding.set(popupPadding);
    }

    public int getDecimalPrecision() {
        return decimalPrecision.get();
    }

    public IntegerProperty decimalPrecisionProperty() {
        return decimalPrecision;
    }

    public void setDecimalPrecision(int decimalPrecision) {
        this.decimalPrecision.set(decimalPrecision);
    }

    public double getCssVal() {
        return cssVal.get();
    }

    public DoubleProperty cssValProperty() {
        return cssVal;
    }

    public void setCssVal(double cssVal) {
        this.cssVal.set(cssVal);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<SliderMode> sliderMode = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SLIDER_MODE,
            this,
            "sliderMode",
            SliderMode.DEFAULT
    );

    private final StyleableDoubleProperty unitIncrement = new SimpleStyleableDoubleProperty(
            StyleableProperties.UNIT_INCREMENT,
            this,
            "unitIncrement",
            10.0
    );

    private final StyleableDoubleProperty alternativeUnitIncrement = new SimpleStyleableDoubleProperty(
            StyleableProperties.ALTERNATIVE_UNIT_INCREMENT,
            this,
            "alternativeUnitIncrement",
            5.0
    );


    private final StyleableDoubleProperty tickUnit = new SimpleStyleableDoubleProperty(
            StyleableProperties.TICK_UNIT,
            this,
            "tickUnit",
            25.0
    );

    private final StyleableBooleanProperty showMajorTicks = new SimpleStyleableBooleanProperty(
            StyleableProperties.SHOW_MAJOR_TICKS,
            this,
            "showMajorTicks",
            false
    );

    private final StyleableBooleanProperty showMinorTicks = new SimpleStyleableBooleanProperty(
            StyleableProperties.SHOW_MINOR_TICKS,
            this,
            "showMinorTicks",
            false
    );

    private final StyleableBooleanProperty showTicksAtEdges = new SimpleStyleableBooleanProperty(
            StyleableProperties.SHOW_TICKS_AT_EDGE,
            this,
            "showTicksAtEdge",
            true
    );

    private final StyleableIntegerProperty minorTicksCount = new SimpleStyleableIntegerProperty(
            StyleableProperties.MINOR_TICKS_COUNT,
            this,
            "minorTicksCount",
            5
    );

    private final StyleableBooleanProperty animateOnPress = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_ON_PRESS,
            this,
            "animateOnPress",
            true
    );

    private final StyleableBooleanProperty bidirectional = new SimpleStyleableBooleanProperty(
            StyleableProperties.BIDIRECTIONAL,
            this,
            "bidirectional",
            true
    );

    private final StyleableObjectProperty<Orientation> orientation = new SimpleStyleableObjectProperty<>(
            StyleableProperties.ORIENTATION,
            this,
            "orientation",
            Orientation.HORIZONTAL
    );

    private final StyleableObjectProperty<SliderPopupSide> popupSide = new SimpleStyleableObjectProperty<>(
            StyleableProperties.POPUP_SIDE,
            this,
            "popupSide",
            SliderPopupSide.DEFAULT
    );

    public SliderMode getSliderMode() {
        return sliderMode.get();
    }

    public StyleableObjectProperty<SliderMode> sliderModeProperty() {
        return sliderMode;
    }

    public void setSliderMode(SliderMode sliderMode) {
        this.sliderMode.set(sliderMode);
    }

    public double getUnitIncrement() {
        return unitIncrement.get();
    }

    public StyleableDoubleProperty unitIncrementProperty() {
        return unitIncrement;
    }

    public void setUnitIncrement(double unitIncrement) {
        this.unitIncrement.set(unitIncrement);
    }

    public double getAlternativeUnitIncrement() {
        return alternativeUnitIncrement.get();
    }

    public StyleableDoubleProperty alternativeUnitIncrementProperty() {
        return alternativeUnitIncrement;
    }

    public void setAlternativeUnitIncrement(double alternativeUnitIncrement) {
        this.alternativeUnitIncrement.set(alternativeUnitIncrement);
    }

    public double getTickUnit() {
        return tickUnit.get();
    }

    public StyleableDoubleProperty tickUnitProperty() {
        return tickUnit;
    }

    public void setTickUnit(double tickUnit) {
        this.tickUnit.set(tickUnit);
    }

    public boolean isShowMajorTicks() {
        return showMajorTicks.get();
    }

    public StyleableBooleanProperty showMajorTicksProperty() {
        return showMajorTicks;
    }

    public void setShowMajorTicks(boolean showMajorTicks) {
        this.showMajorTicks.set(showMajorTicks);
    }

    public boolean isShowMinorTicks() {
        return showMinorTicks.get();
    }

    public StyleableBooleanProperty showMinorTicksProperty() {
        return showMinorTicks;
    }

    public void setShowMinorTicks(boolean showMinorTicks) {
        this.showMinorTicks.set(showMinorTicks);
    }

    public boolean isShowTicksAtEdges() {
        return showTicksAtEdges.get();
    }

    public StyleableBooleanProperty showTicksAtEdgesProperty() {
        return showTicksAtEdges;
    }

    public void setShowTicksAtEdges(boolean showTicksAtEdges) {
        this.showTicksAtEdges.set(showTicksAtEdges);
    }

    public int getMinorTicksCount() {
        return minorTicksCount.get();
    }

    public StyleableIntegerProperty minorTicksCountProperty() {
        return minorTicksCount;
    }

    public void setMinorTicksCount(int minorTicksCount) {
        this.minorTicksCount.set(minorTicksCount);
    }

    public boolean isAnimateOnPress() {
        return animateOnPress.get();
    }

    public StyleableBooleanProperty animateOnPressProperty() {
        return animateOnPress;
    }

    public void setAnimateOnPress(boolean animateOnPress) {
        this.animateOnPress.set(animateOnPress);
    }

    public boolean isBidirectional() {
        return bidirectional.get();
    }

    public StyleableBooleanProperty bidirectionalProperty() {
        return bidirectional;
    }

    public void setBidirectional(boolean bidirectional) {
        this.bidirectional.set(bidirectional);
    }

    public Orientation getOrientation() {
        return orientation.get();
    }

    public StyleableObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    public SliderPopupSide getPopupSide() {
        return popupSide.get();
    }

    public StyleableObjectProperty<SliderPopupSide> popupSideProperty() {
        return popupSide;
    }

    public void setPopupSide(SliderPopupSide popupSide) {
        this.popupSide.set(popupSide);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXSlider, SliderMode> SLIDER_MODE =
                FACTORY.createEnumCssMetaData(
                        SliderMode.class,
                        "-mfx-slider-mode",
                        MFXSlider::sliderModeProperty,
                        SliderMode.DEFAULT
                );

        private static final CssMetaData<MFXSlider, Number> UNIT_INCREMENT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-unit-increment",
                        MFXSlider::unitIncrementProperty,
                        10.0
                );

        private static final CssMetaData<MFXSlider, Number> ALTERNATIVE_UNIT_INCREMENT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-alternative-unit-increment",
                        MFXSlider::alternativeUnitIncrementProperty,
                        5.0
                );

        private static final CssMetaData<MFXSlider, Number> TICK_UNIT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-tick-unit",
                        MFXSlider::tickUnitProperty,
                        25.0
                );


        private static final CssMetaData<MFXSlider, Boolean> SHOW_MAJOR_TICKS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-show-major-ticks",
                        MFXSlider::showMajorTicksProperty,
                        false
                );

        private static final CssMetaData<MFXSlider, Boolean> SHOW_MINOR_TICKS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-show-minor-ticks",
                        MFXSlider::showMinorTicksProperty,
                        false
                );

        private static final CssMetaData<MFXSlider, Boolean> SHOW_TICKS_AT_EDGE =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-show-ticks-at-edge",
                        MFXSlider::showTicksAtEdgesProperty,
                        true
                );

        private static final CssMetaData<MFXSlider, Number> MINOR_TICKS_COUNT =
                FACTORY.createSizeCssMetaData(
                        "-mfx-minor-ticks-count",
                        MFXSlider::minorTicksCountProperty,
                        5
                );

        private static final CssMetaData<MFXSlider, Boolean> ANIMATE_ON_PRESS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-on-press",
                        MFXSlider::animateOnPressProperty,
                        true
                );

        private static final CssMetaData<MFXSlider, Boolean> BIDIRECTIONAL =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-bidirectional",
                        MFXSlider::bidirectionalProperty,
                        true
                );

        private static final CssMetaData<MFXSlider, Orientation> ORIENTATION =
                FACTORY.createEnumCssMetaData(
                        Orientation.class,
                        "-mfx-orientation",
                        MFXSlider::orientationProperty,
                        Orientation.HORIZONTAL
                );

        private static final CssMetaData<MFXSlider, SliderPopupSide> POPUP_SIDE =
                FACTORY.createEnumCssMetaData(
                        SliderPopupSide.class,
                        "-mfx-popup-side",
                        MFXSlider::popupSideProperty,
                        SliderPopupSide.DEFAULT
                );

        static {
            cssMetaDataList = List.of(
                    SLIDER_MODE,UNIT_INCREMENT, ALTERNATIVE_UNIT_INCREMENT,
                    TICK_UNIT, SHOW_MAJOR_TICKS, SHOW_MINOR_TICKS, SHOW_TICKS_AT_EDGE, MINOR_TICKS_COUNT,
                    ANIMATE_ON_PRESS, BIDIRECTIONAL, ORIENTATION, POPUP_SIDE
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXSliderSkin(this);
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXSlider.getControlCssMetaDataList();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
