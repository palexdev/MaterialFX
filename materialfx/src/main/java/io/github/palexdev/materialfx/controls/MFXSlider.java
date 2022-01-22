/*
 * Copyright (C) 2022 Parisi Alessandro
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
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.enums.SliderEnums.SliderMode;
import io.github.palexdev.materialfx.enums.SliderEnums.SliderPopupSide;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXSliderSkin;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextBoundsType;

import java.util.List;
import java.util.function.Supplier;

import static io.github.palexdev.materialfx.utils.NodeUtils.isPseudoClassActive;

/**
 * This is the implementation of a Slider following Google's material design guidelines.
 * <p></p>
 * Extends {@code Control} rather than {@link Slider}, this has been made completely from scratch,
 * the code is much more clean, documented, and implements many new features.
 * <p></p>
 * The thumb and the popup can be changed by setting the corresponding suppliers
 * (the popup can also be removed by setting a null supplier or by returning null).
 * You can also specify the extra gap between the popup and the thumb, see {@link #popupPaddingProperty()},
 * and the popup position, see {@link #popupSideProperty()}.
 * <p>
 * MFXSlider can operate on decimal values too, up to two decimal places. By default it is set to 0,
 * see {@link #decimalPrecisionProperty()}.
 * <p></p>
 * Just like the JavaFX' slider, MFXSlider has two working modes:
 * <p> - DEFAULT, the thumb can be moved freely
 * <p> - SNAP_TO_TICKS, the thumb always snaps to the closest tick (even if they're hidden)
 * <p>
 * Note that the snapping is ignored if the value is adjusted with the keyboard.
 * <p>
 * The properties to customize the ticks are: {@link #tickUnitProperty()}, {@link #showMajorTicksProperty()},
 * {@link #showMinorTicksProperty()}, {@link #showTicksAtEdgesProperty()}, {@link #minorTicksCountProperty()}.
 * <p>
 * Also note that by default (implemented in the skin), the major ticks have two different style classes according to their
 * index position, "tick-even" or "tick-odd", just to add an extra customization.
 * <p></p>
 * The {@link #unitIncrementProperty()} and {@link #alternativeUnitIncrementProperty()} properties specify the
 * value increment on arrow keys press (left/right when slider is Horizontal, up/down when slider is Vertical).
 * The alternate unit increment is used when Shift or Ctrl are pressed too.
 * <p>
 * The keyboard behavior can be also disabled by setting {@link #enableKeyboardProperty()} to false.
 * <p></p>
 * When you press on the slider's track the value is adjusted accordingly to where you pressed, the adjusting
 * animation can also be disabled by setting {@link #animateOnPressProperty()}.
 * <p></p>
 * MFXSlider offers a brand new feature: the progress bar is bidirectional (can be disabled). This means
 * that if the minimum value is negative the bar will progress on the opposite side to zero.
 * <p></p>
 * MFXSlider introduces three new css pseudo classes:
 * <p> - ":range1", activated when the slider value is contained in any of the ranges specified in here {@link #getRanges1()}
 * <p> - ":range2", activated when the slider value is contained in any of the ranges specified in here {@link #getRanges2()}
 * <p> - ":range3", activated when the slider value is contained in any of the ranges specified in here {@link #getRanges3()}
 * <p>
 * I know this may seem a strange approach, but it is much more flexible and allows for a lot more customization.
 * <p></p>
 * <b>WARNING!</b>
 * <p>
 * If you are changing the min, max, and initial value properties of the slider programmatically be sure
 * to <b>respect this order</b>, setMin(...), setMax(...), setValue(...).
 * This is needed for several reasons: min cannot be greater than max otherwise an exception is thrown;
 * max cannot be lesser than min otherwise an exception is thrown; the slider's value never throws an exception if
 * it is invalid but rather the value is clamped between the specified min and max values using {@link NumberUtils#clamp(double, double, double)}.
 * If you don't respect the order you'll end with an inconsistent state and most likely with a messed layout.
 */
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
	private final SupplierProperty<Node> thumbSupplier = new SupplierProperty<>() {
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
	private final SupplierProperty<Region> popupSupplier = new SupplierProperty<>();
	private final DoubleProperty popupPadding = new SimpleDoubleProperty(5.0);
	private final IntegerProperty decimalPrecision = new SimpleIntegerProperty(0);
	private final BooleanProperty enableKeyboard = new SimpleBooleanProperty(true);

	private final ObservableList<NumberRange<Double>> ranges1 = FXCollections.observableArrayList();
	private final ObservableList<NumberRange<Double>> ranges2 = FXCollections.observableArrayList();
	private final ObservableList<NumberRange<Double>> ranges3 = FXCollections.observableArrayList();
	protected final PseudoClass RANGE1_PSEUDO_CLASS = PseudoClass.getPseudoClass("range1");
	protected final PseudoClass RANGE2_PSEUDO_CLASS = PseudoClass.getPseudoClass("range2");
	protected final PseudoClass RANGE3_PSEUDO_CLASS = PseudoClass.getPseudoClass("range3");

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
		ranges1.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		ranges2.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		ranges3.addListener((ListChangeListener<? super NumberRange<Double>>) c -> handlePseudoClasses());
		value.addListener((observable, oldValue, newValue) -> handlePseudoClasses());
	}

	/**
	 * Handles the ":range1", ":range2" and ":range3" css pseudo classes when these properties change:
	 * {@link #valueProperty()}, {@link #getRanges1()}, {@link #getRanges2()}, {@link #getRanges3()}.
	 */
	private void handlePseudoClasses() {
		double val = getValue();
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
	 * Sets the default thumb supplier.
	 * <p></p>
	 * It is basically a StackPane which contains two MFXFontIcons (both are circles).
	 * The innermost is the thumb and the outermost is the circle that indicates if the mouse
	 * is hover or pressed on the thumb.
	 * <p>
	 * <b>Note:</b> since the outermost circle is larger that the thumb, the StackPane's layout bounds
	 * are set to be at most the thumb's width and height, otherwise it would cause layout and behavior issues.
	 * <p>
	 * Also, both the thumb and the other circle are transparent to mouse events as the node returned by the supplier
	 * is the StackPane and this is the node that should respond to events.
	 */
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
			rippleGenerator.setRipplePositionFunction(mouseEvent -> PositionBean.of(stackPane.getWidth() / 2, stackPane.getHeight() / 2));
			stackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, rippleGenerator::generateRipple);
			stackPane.getChildren().add(rippleGenerator);

			return stackPane;
		});
	}

	/**
	 * Sets the default popup supplier.
	 * <p></p>
	 * It is basically a VBox which contains a Label for the slider's value and a MFXFontIcon which is the caret.
	 * <p></p>
	 * <b>Note:</b> The supplier should also deal with changes of {@link #popupSideProperty()} as the text and the caret
	 * should be rotated and positioned accordingly.
	 * <p></p>
	 * Also note that the so called "popup" is not really a JavaFX popup but a node (Region to be precise)
	 * because this makes handling it's position way easier (with a real popup we must deal with screen coordinates
	 * and it's a real pita).
	 */
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

			VBox.setVgrow(text, Priority.ALWAYS);

			MFXFontIcon caret = new MFXFontIcon("mfx-caret-down", 22);
			caret.setId("popupCaret");
			caret.setBoundsType(TextBoundsType.VISUAL);
			caret.setManaged(false);

			VBox container = new VBox(text, caret) {
				@Override
				protected void layoutChildren() {
					super.layoutChildren();

					Orientation orientation = getOrientation();
					double x = orientation == Orientation.HORIZONTAL ? (getWidth() / 2) - (caret.prefWidth(-1) / 2) : getHeight();
					double y = orientation == Orientation.HORIZONTAL ? getHeight() : -(caret.prefHeight(-1) / 2) + (getHeight() / 2);
					caret.relocate(snapPositionX(x), snapPositionY(y));
				}
			};
			container.setId("popupContent");
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

	/**
	 * Specifies the minimum value the slider can reach.
	 */
	public DoubleProperty minProperty() {
		return min;
	}

	public void setMin(double min) {
		this.min.set(min);
	}

	public double getMax() {
		return max.get();
	}

	/**
	 * Specifies the maximum value the slider can reach.
	 */
	public DoubleProperty maxProperty() {
		return max;
	}

	public void setMax(double max) {
		this.max.set(max);
	}

	public double getValue() {
		return value.get();
	}

	/**
	 * Specifies the slider's actual value.
	 */
	public DoubleProperty valueProperty() {
		return value;
	}

	public void setValue(double value) {
		this.value.set(value);
	}

	public Supplier<Node> getThumbSupplier() {
		return thumbSupplier.get();
	}

	/**
	 * Specifies the supplier used to build the slider's thumb.
	 * <p>
	 * Attempting to set or return a null value will fallback to the {@link #defaultThumbSupplier()}.
	 */
	public SupplierProperty<Node> thumbSupplierProperty() {
		return thumbSupplier;
	}

	public void setThumbSupplier(Supplier<Node> thumbSupplier) {
		this.thumbSupplier.set(thumbSupplier);
	}

	public Supplier<Region> getPopupSupplier() {
		return popupSupplier.get();
	}

	/**
	 * Specifies the supplier used to build the slider's popup.
	 * <p>
	 * You can also set or return null to remove the popup.
	 */
	public SupplierProperty<Region> popupSupplierProperty() {
		return popupSupplier;
	}

	public void setPopupSupplier(Supplier<Region> popupSupplier) {
		this.popupSupplier.set(popupSupplier);
	}

	public double getPopupPadding() {
		return popupPadding.get();
	}

	/**
	 * Specifies the extra gap between the thumb and the popup.
	 */
	public DoubleProperty popupPaddingProperty() {
		return popupPadding;
	}

	public void setPopupPadding(double popupPadding) {
		this.popupPadding.set(popupPadding);
	}

	public int getDecimalPrecision() {
		return decimalPrecision.get();
	}

	/**
	 * Specifies the number of decimal places for the slider's value.
	 */
	public IntegerProperty decimalPrecisionProperty() {
		return decimalPrecision;
	}

	public void setDecimalPrecision(int decimalPrecision) {
		this.decimalPrecision.set(decimalPrecision);
	}

	public boolean isEnableKeyboard() {
		return enableKeyboard.get();
	}

	/**
	 * Specifies if the value can be adjusted with the keyboard or not.
	 */
	public BooleanProperty enableKeyboardProperty() {
		return enableKeyboard;
	}

	public void setEnableKeyboard(boolean enableKeyboard) {
		this.enableKeyboard.set(enableKeyboard);
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

	/**
	 * Specifies the slider mode. Can be DEFAULT (freely adjust the thumb) or SNAP_TO_TICKS
	 * (the thumb will always snap to ticks).
	 */
	public StyleableObjectProperty<SliderMode> sliderModeProperty() {
		return sliderMode;
	}

	public void setSliderMode(SliderMode sliderMode) {
		this.sliderMode.set(sliderMode);
	}

	public double getUnitIncrement() {
		return unitIncrement.get();
	}

	/**
	 * Specifies the value to add/subtract to the slider's value when an arrow key is pressed.
	 * <p></p>
	 * The arrow keys depend on the slider orientation:
	 * <p> - HORIZONTAL: right, left
	 * <p> - VERTICAL: up, down
	 */
	public StyleableDoubleProperty unitIncrementProperty() {
		return unitIncrement;
	}

	public void setUnitIncrement(double unitIncrement) {
		this.unitIncrement.set(unitIncrement);
	}

	public double getAlternativeUnitIncrement() {
		return alternativeUnitIncrement.get();
	}

	/**
	 * Specifies the value to add/subtract to the slider's value when an arrow key and Shift or Ctrl are pressed.
	 * <p></p>
	 * The arrow keys depend on the slider orientation:
	 * <p> - HORIZONTAL: right, left
	 * <p> - VERTICAL: up, down
	 */
	public StyleableDoubleProperty alternativeUnitIncrementProperty() {
		return alternativeUnitIncrement;
	}

	public void setAlternativeUnitIncrement(double alternativeUnitIncrement) {
		this.alternativeUnitIncrement.set(alternativeUnitIncrement);
	}

	public double getTickUnit() {
		return tickUnit.get();
	}

	/**
	 * The value between each major tick mark in data units.
	 */
	public StyleableDoubleProperty tickUnitProperty() {
		return tickUnit;
	}

	public void setTickUnit(double tickUnit) {
		this.tickUnit.set(tickUnit);
	}

	public boolean isShowMajorTicks() {
		return showMajorTicks.get();
	}

	/**
	 * Specifies if the major ticks should be displayed or not.
	 */
	public StyleableBooleanProperty showMajorTicksProperty() {
		return showMajorTicks;
	}

	public void setShowMajorTicks(boolean showMajorTicks) {
		this.showMajorTicks.set(showMajorTicks);
	}

	public boolean isShowMinorTicks() {
		return showMinorTicks.get();
	}

	/**
	 * Specifies if the minor ticks should be displayed or not.
	 */
	public StyleableBooleanProperty showMinorTicksProperty() {
		return showMinorTicks;
	}

	public void setShowMinorTicks(boolean showMinorTicks) {
		this.showMinorTicks.set(showMinorTicks);
	}

	public boolean isShowTicksAtEdges() {
		return showTicksAtEdges.get();
	}

	/**
	 * Specifies if the major ticks at the edge of the slider should be displayed or not.
	 * <p>
	 * The ticks at the edge are those ticks which represent the min and max values.
	 */
	public StyleableBooleanProperty showTicksAtEdgesProperty() {
		return showTicksAtEdges;
	}

	public void setShowTicksAtEdges(boolean showTicksAtEdges) {
		this.showTicksAtEdges.set(showTicksAtEdges);
	}

	public int getMinorTicksCount() {
		return minorTicksCount.get();
	}

	/**
	 * Specifies how many minor ticks should be added between two major ticks.
	 */
	public StyleableIntegerProperty minorTicksCountProperty() {
		return minorTicksCount;
	}

	public void setMinorTicksCount(int minorTicksCount) {
		this.minorTicksCount.set(minorTicksCount);
	}

	public boolean isAnimateOnPress() {
		return animateOnPress.get();
	}

	/**
	 * When pressing on the slider's track the value is adjusted according to the mouse event
	 * coordinates. This property specifies if the progress bar adjustment should be animated or not.
	 */
	public StyleableBooleanProperty animateOnPressProperty() {
		return animateOnPress;
	}

	public void setAnimateOnPress(boolean animateOnPress) {
		this.animateOnPress.set(animateOnPress);
	}

	public boolean isBidirectional() {
		return bidirectional.get();
	}

	/**
	 * If the slider is set to be bidirectional the progress bar will always start from 0.
	 * When the value is negative the progress bar grows in the opposite direction to 0.
	 * <p></p>
	 * This works only if min is negative and max is positive, otherwise this option in ignored
	 * during layout. See the warning in the control documentation.
	 */
	public StyleableBooleanProperty bidirectionalProperty() {
		return bidirectional;
	}

	public void setBidirectional(boolean bidirectional) {
		this.bidirectional.set(bidirectional);
	}

	public Orientation getOrientation() {
		return orientation.get();
	}

	/**
	 * Specifies the slider's orientation.
	 */
	public StyleableObjectProperty<Orientation> orientationProperty() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}

	public SliderPopupSide getPopupSide() {
		return popupSide.get();
	}

	/**
	 * Specifies the popup side.
	 * <p>
	 * DEFAULT is above for horizontal orientation and left for vertical orientation.
	 * <p>
	 * OTHER_SIDE is below for horizontal orientation and right for vertical orientation.
	 */
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
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Control.getClassCssMetaData(),
					SLIDER_MODE, UNIT_INCREMENT, ALTERNATIVE_UNIT_INCREMENT,
					TICK_UNIT, SHOW_MAJOR_TICKS, SHOW_MINOR_TICKS, SHOW_TICKS_AT_EDGE, MINOR_TICKS_COUNT,
					ANIMATE_ON_PRESS, BIDIRECTIONAL, ORIENTATION, POPUP_SIDE
			);
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
