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
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXDatePickerContent;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This is the implementation of a date picker following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code VBox}, redefines the style class to "mfx-date-picker" for usage in CSS.
 * <p>A few notes:</p>
 * <p>
 * Extends {@code VBox} rather than extending {@code DatePicker} because JavaFX's date picker code is a huge mess
 * and also bad designed.
 * Rather than using a {@code ComboBox} this control uses a simple {@code Label} with a {@code MFXFontIcon}.
 * <p>
 * The {@code Label} value is bound to the {@code DatePicker} value and it's formatted using the set {@link #dateFormatter}.
 * <p>
 * To get the selected date use {@link #getDate()}.
 * <p>
 * You can also retrieve the instance of the {@code DatePicker} by using {@link #getDatePicker()},
 * however I don't recommend it since this control doesn't use anything other than its value.
 */
public class MFXDatePicker extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXDatePicker> FACTORY = new StyleablePropertyFactory<>(VBox.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-date-picker";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-datepicker.css");

    private final DatePicker datePicker;
    private final ObjectProperty<DateTimeFormatter> dateFormatter = new SimpleObjectProperty<>(DateTimeFormatter.ofPattern("dd/M/yyyy"));

    private StackPane stackPane;
    private Label value;
    private MFXFontIcon calendar;
    private Line line;
    private PopupControl popup;
    private MFXDatePickerContent datePickerContent;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDatePicker() {
        this.datePicker = new DatePicker();
        initialize();
    }

    public MFXDatePicker(LocalDate localDate) {
        this.datePicker = new DatePicker(localDate);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        setMinWidth(92);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        value = new Label("");
        value.getStyleClass().setAll("value");
        value.setMinWidth(64);
        calendar = new MFXFontIcon("mfx-calendar-semi-black");
        calendar.getStyleClass().add("calendar-icon");
        calendar.setColor(getPickerColor());
        calendar.setSize(20);
        stackPane = new StackPane(value, calendar);
        stackPane.setPadding(new Insets(5, -2.5, 5, 5));
        stackPane.setAlignment(Pos.BOTTOM_LEFT);
        StackPane.setAlignment(calendar, Pos.BOTTOM_RIGHT);

        line = new Line();
        line.getStyleClass().add("line");
        line.setManaged(false);
        line.setSmooth(true);
        line.strokeWidthProperty().bind(lineStrokeWidth);
        line.strokeLineCapProperty().bind(lineStrokeCap);
        line.setStroke(getLineColor());
        line.endXProperty().bind(widthProperty().add(10));

        popup = new PopupControl();
        datePickerContent = new MFXDatePickerContent(datePicker.getValue(), getDateFormatter());
        popup.getScene().setRoot(datePickerContent);
        popup.setAutoHide(true);

        getChildren().addAll(stackPane, line);
        addListeners();

        if (datePicker.getValue() != null) {
            value.setText(datePicker.getValue().format(getDateFormatter()));
        }

        datePickerContent.updateColor((Color) getPickerColor());
    }

    /**
     * Adds listeners to date picker content currentDateProperty, to {@link #dateFormatter}, to {@link #pickerColor},
     * to {@link #lineColor}, to calendar icon's {@link MFXFontIcon#colorProperty()}, to {@link #colorText} and disabled property.
     * <p>
     * Adds event handler to calendar icon.
     * <p>
     * Binds date picker content animateCalendarProperty to {@link #animateCalendar}
     */
    private void addListeners() {
        calendar.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (!popup.isShowing()) {
                Point2D point = NodeUtils.pointRelativeTo(this, datePickerContent, HPos.CENTER, VPos.BOTTOM, 0, 0, true);
                popup.show(this, snapPositionX(point.getX() - 4), snapPositionY(point.getY() + 2));
            } else {
                popup.hide();
            }
        });

        datePickerContent.currentDateProperty().addListener((observable, oldValue, newValue) -> {
            datePicker.setValue(newValue);
            value.setText(newValue.format(datePickerContent.getDateFormatter()));
        });

        datePickerContent.currentDateProperty().addListener((observable, oldValue, newValue) -> {
            if (!isCloseOnDaySelected()) {
                return;
            }

            if (oldValue.getYear() == newValue.getYear() ||
                    oldValue.getMonth() == newValue.getMonth()) {
                if (oldValue.getDayOfMonth() != newValue.getDayOfMonth()) {
                    popup.hide();
                }
            }
        });

        dateFormatter.addListener((observable, oldValue, newValue) -> {
            LocalDate date = LocalDate.parse(value.getText(), oldValue);
            value.setText(date.format(newValue));
            datePickerContent.setDateFormatter(newValue);
        });

        pickerColor.addListener((observable, oldValue, newValue) -> {
            Color color;
            if (newValue instanceof Color) {
                color = (Color) newValue;
            } else {
                throw new IllegalStateException("Paint values are not supported, change it to Color");
            }

            calendar.setColor(color);
            datePickerContent.updateColor(color);
            if (isColorText()) {
                value.setTextFill(newValue);
            } else {
                value.setTextFill(Color.BLACK);
            }
        });
        lineColor.addListener((observable, oldValue, newValue) -> {
            if (!isDisabled()) {
                line.setStroke(newValue);
            }
        });
        calendar.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (!isDisabled()) {
                calendar.setColor(newValue);
            } else {
                calendar.setColor(Color.LIGHTGRAY);
            }
        });
        colorText.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                value.setTextFill(getPickerColor());
            } else {
                value.setTextFill(Color.BLACK);
            }
        });

        datePickerContent.animateCalendarProperty().bind(animateCalendar);

        disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                line.setStroke(Color.LIGHTGRAY);
                calendar.setColor(Color.LIGHTGRAY);
            } else {
                line.setStroke(getLineColor());
                calendar.setColor(getPickerColor());
            }
        });
    }

    public MFXDatePickerContent getContent() {
        return datePickerContent;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter.get();
    }

    public ObjectProperty<DateTimeFormatter> dateFormatterProperty() {
        return dateFormatter;
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter.set(dateFormatter);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Paint> pickerColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.PICKER_COLOR,
            this,
            "pickerColor",
            Color.rgb(98, 0, 238)
    );

    private final StyleableObjectProperty<Paint> lineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_COLOR,
            this,
            "lineColor",
            Color.rgb(98, 0, 238, 0.7)
    );

    private final StyleableDoubleProperty lineStrokeWidth = new SimpleStyleableDoubleProperty(
            StyleableProperties.LINE_STROKE_WIDTH,
            this,
            "lineStrokeWidth",
            2.0
    );

    private final StyleableObjectProperty<StrokeLineCap> lineStrokeCap = new SimpleStyleableObjectProperty<>(
            StyleableProperties.LINE_STROKE_CAP,
            this,
            "lineStrokeCap",
            StrokeLineCap.ROUND
    );

    private final StyleableBooleanProperty colorText = new SimpleStyleableBooleanProperty(
            StyleableProperties.COLOR_TEXT,
            this,
            "colorText",
            false
    );

    private final StyleableBooleanProperty closeOnDaySelected = new SimpleStyleableBooleanProperty(
            StyleableProperties.CLOSE_ON_DAY_SELECTED,
            this,
            "closeOnDaySelected",
            true
    );

    private final StyleableBooleanProperty animateCalendar = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_CALENDAR,
            this,
            "animateCalendar",
            true
    );

    public Paint getPickerColor() {
        return pickerColor.get();
    }

    /**
     * Specifies the main color of the date picker and its content.
     */
    public StyleableObjectProperty<Paint> pickerColorProperty() {
        return pickerColor;
    }

    public void setPickerColor(Paint pickerColor) {
        this.pickerColor.set(pickerColor);
    }

    public Paint getLineColor() {
        return lineColor.get();
    }

    /**
     * Specifies the line color of the date picker.
     */
    public StyleableObjectProperty<Paint> lineColorProperty() {
        return lineColor;
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor.set(lineColor);
    }

    public double getLineStrokeWidth() {
        return lineStrokeWidth.get();
    }

    /**
     * Specifies the line's stroke width.
     */
    public StyleableDoubleProperty lineStrokeWidthProperty() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(double lineStrokeWidth) {
        this.lineStrokeWidth.set(lineStrokeWidth);
    }

    public StrokeLineCap getLineStrokeCap() {
        return lineStrokeCap.get();
    }

    /**
     * Specifies the line's stroke cap.
     */
    public StyleableObjectProperty<StrokeLineCap> lineStrokeCapProperty() {
        return lineStrokeCap;
    }

    public void setLineStrokeCap(StrokeLineCap lineStrokeCap) {
        this.lineStrokeCap.set(lineStrokeCap);
    }

    public boolean isColorText() {
        return colorText.get();
    }

    /**
     * Specifies if the date picker text should be colored too.
     */
    public StyleableBooleanProperty colorTextProperty() {
        return colorText;
    }

    public void setColorText(boolean colorText) {
        this.colorText.set(colorText);
    }

    public boolean isCloseOnDaySelected() {
        return closeOnDaySelected.get();
    }

    /**
     * Specifies if the date picker popup should close on day selected.
     */
    public StyleableBooleanProperty closeOnDaySelectedProperty() {
        return closeOnDaySelected;
    }

    public void setCloseOnDaySelected(boolean closeOnDaySelected) {
        this.closeOnDaySelected.set(closeOnDaySelected);
    }

    public boolean isAnimateCalendar() {
        return animateCalendar.get();
    }

    /**
     * Specifies if the month change should be animated.
     */
    public StyleableBooleanProperty animateCalendarProperty() {
        return animateCalendar;
    }

    public void setAnimateCalendar(boolean animateCalendar) {
        this.animateCalendar.set(animateCalendar);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXDatePicker, Paint> PICKER_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-main-color",
                        MFXDatePicker::pickerColorProperty,
                        Color.rgb(98, 0, 238)
                );

        private static final CssMetaData<MFXDatePicker, Paint> LINE_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-line-color",
                        MFXDatePicker::lineColorProperty,
                        Color.rgb(90, 0, 238, 0.7)
                );

        private static final CssMetaData<MFXDatePicker, Number> LINE_STROKE_WIDTH =
                FACTORY.createSizeCssMetaData(
                        "-mfx-line-stroke-width",
                        MFXDatePicker::lineStrokeWidthProperty,
                        2.0
                );

        private static final CssMetaData<MFXDatePicker, StrokeLineCap> LINE_STROKE_CAP =
                FACTORY.createEnumCssMetaData(
                        StrokeLineCap.class,
                        "-mfx-line-stroke-cap",
                        MFXDatePicker::lineStrokeCapProperty,
                        StrokeLineCap.ROUND
                );

        private static final CssMetaData<MFXDatePicker, Boolean> COLOR_TEXT =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-color-text",
                        MFXDatePicker::colorTextProperty,
                        false
                );

        private static final CssMetaData<MFXDatePicker, Boolean> CLOSE_ON_DAY_SELECTED =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-close-on-day-selected",
                        MFXDatePicker::closeOnDaySelectedProperty,
                        true
                );

        private static final CssMetaData<MFXDatePicker, Boolean> ANIMATE_CALENDAR =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-calendar",
                        MFXDatePicker::animateCalendarProperty,
                        true
                );

        static {
            cssMetaDataList = List.of(
                    PICKER_COLOR, COLOR_TEXT, CLOSE_ON_DAY_SELECTED, ANIMATE_CALENDAR,
                    LINE_COLOR, LINE_STROKE_WIDTH, LINE_STROKE_CAP
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
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return MFXDatePicker.getControlCssMetaDataList();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double ly = snapPositionY(stackPane.getBoundsInParent().getMaxY() + (line.getStrokeWidth() / 2.5));
        line.relocate(-3, ly);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public DatePicker getDatePicker() {
        return datePicker;
    }

    public LocalDate getDate() {
        return datePicker.getValue();
    }
}
