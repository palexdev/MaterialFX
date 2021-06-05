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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.effects.ripple.RipplePosition;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * This class is the beating heart of every {@link MFXDatePicker}.
 * <p>
 * Extends {@link VBox}, the style class is set to "mfx-datepicker-content" for usage in CSS.
 * <p></p>
 * In JavaFX every {@link DatePicker} has a content like this but the code is a huge mess.
 * <p>
 * To make things even worse the class is part of the com.sun.javafx package which means that
 * jvm arguments are needed to make it accessible... this is BAD.
 * <p>
 * That said, this class has almost nothing to do with that one. The code is simpler and much more organized but most
 * importantly it's well documented.
 */
public class MFXDatePickerContent extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-datepicker-content";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXDatePickerContent.css");

    private final double DEFAULT_WIDTH = 300;
    private final double DEFAULT_HEIGHT = 380;
    private final Insets DEFAULT_INSETS = new Insets(8, 10, 8, 10);

    private final int daysPerWeek = 7;
    private final List<MFXDateCell> days = new ArrayList<>();
    private final Map<String, Integer> dayNameMap = new LinkedHashMap<>();
    private final List<MFXDateCell> dayNameCells = new ArrayList<>();
    private final List<MFXDateCell> yearsList = new ArrayList<>();

    private final ObjectProperty<LocalDate> currentDate = new SimpleObjectProperty<>(LocalDate.now());
    private final ObjectProperty<YearMonth> yearMonth = new SimpleObjectProperty<>(YearMonth.of(getCurrentDate().getYear(), getCurrentDate().getMonth()));

    private final VBox header;
    private final StackPane yearMonthPane;
    private final Separator separator;

    private Label label;
    private Label selectedDate;
    private Label month;
    private Label year;

    private final StackPane holder;
    private GridPane calendar;
    private GridPane years;
    private MFXScrollPane yearsScroll;
    private MFXIconWrapper yearsButton;
    private MFXIconWrapper monthBackButton;
    private MFXIconWrapper monthForwardButton;
    private MFXIconWrapper inputButton;

    private Timeline yearsOpen;
    private Timeline yearsClose;
    private Timeline calendarTransition;

    private final ObjectProperty<MFXDateCell> lastSelectedDayCell = new SimpleObjectProperty<>(null);
    private MFXDateCell lastSelectedYearCell = null;
    private MFXDateCell currYearCell = null;

    private final BooleanProperty validInput = new SimpleBooleanProperty(true);
    private MFXTextField inputField;
    private boolean keyInput = false;

    // Date formatters
    private final ObjectProperty<DateTimeFormatter> dateFormatter = new SimpleObjectProperty<>(DateTimeFormatter.ofPattern("d/M/yyyy"));
    private final DateTimeFormatter weekDayNameFormatter = DateTimeFormatter.ofPattern("ccc");

    private final BooleanProperty animateCalendar = new SimpleBooleanProperty();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDatePickerContent() {
        this(LocalDate.now(), DateTimeFormatter.ofPattern("d/M/yyyy"));
    }

    public MFXDatePickerContent(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
        getStyleClass().add(STYLE_CLASS);
        getStylesheets().setAll(STYLESHEET);
        setPrefSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        setDateFormatter(dateTimeFormatter);

        buildButtons();
        buildField();

        header = buildHeader();
        yearMonthPane = buildYearMonthPane();
        separator = buildSeparator();

        getChildren().addAll(
                header,
                yearMonthPane,
                separator
        );

        holder = new StackPane(buildCalendar(), buildScroll());
        holder.getStyleClass().add("holder");
        getChildren().add(holder);

        initialize();

        if (localDate != null) {
            setCurrentDate(localDate);
            setYearMonth(YearMonth.of(getCurrentDate().getYear(), getCurrentDate().getMonth()));
            lastSelectedDayCell.set(
                    days.stream()
                            .filter(day -> day.getText().equals(Integer.toString(getCurrentDate().getDayOfMonth())))
                            .findFirst()
                            .orElse(null)
            );
            lastSelectedDayCell.get().setSelectedDate(true);
            lastSelectedYearCell = yearsList.stream()
                    .filter(year -> year.getText().equals(Integer.toString(getYearMonth().getYear())))
                    .findFirst()
                    .orElse(null);
            if (lastSelectedYearCell != null) {
                lastSelectedYearCell.setSelectedDate(true);
                goToYear();
            }
        }
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        createYearCells();
        createDayNameCells();
        createDayCells();
        populateYears();
        populateCalendar();

        buildAnimations();
        yearsScroll.setOpacity(0);
        yearsScroll.setVisible(false);

        //selectedDate.setText(getCurrentDate().format(getDateFormatter()));
        month.setText(StringUtils.titleCaseWord(getYearMonth().getMonth().getDisplayName(TextStyle.FULL, getLocale())));
        year.setText(String.valueOf(getYearMonth().getYear()));

        behaviorListeners();
    }

    //================================================================================
    // [Behavior || Create] Methods
    //================================================================================

    /**
     * Creates the MFXDateCells which will populate {@link #years}.
     */
    private void createYearCells() {
        yearsList.clear();

        int currYear = LocalDate.now().getYear();

        int i;
        for (i = currYear - 120; i <= currYear + 120; i++) {
            MFXDateCell cell = new MFXDateCell(Integer.toString(i));
            cell.getStyleClass().add("year-cell");
            cell.setPrefSize(65, 25);
            cell.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            cell.setAlignment(Pos.CENTER);

            if (i == LocalDate.now().getYear()) {
                currYearCell = cell;
                cell.setCurrent(true);
                cell.setSelectedDate(false);
            }

            cell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (lastSelectedYearCell != null) {
                    lastSelectedYearCell.setSelectedDate(false);
                }

                lastSelectedYearCell = cell;
                lastSelectedYearCell.setSelectedDate(true);

                setYearMonth(getYearMonth().withYear(Integer.parseInt(lastSelectedYearCell.getText())));

                if (lastSelectedDayCell.get() == null) {
                    selectDay();
                }
            });

            yearsList.add(cell);
        }
    }

    /**
     * Creates the MFXDateCells which will populate the first row of {@link #calendar}.
     * They contain the first letter of each day name.
     */
    private void createDayNameCells() {
        dayNameCells.clear();

        int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();
        LocalDate date = LocalDate.of(2009, 7, 12 + firstDayOfWeek);

        int i;
        for (i = 0; i < daysPerWeek; i++) {
            MFXDateCell cell = new MFXDateCell();
            cell.getStyleClass().add("day-name-cell");
            cell.setAlignment(Pos.CENTER);

            String name = weekDayNameFormatter.withLocale(getLocale()).format(date.plus(i, DAYS));
            dayNameMap.put(name, i);
            if (weekDayNameFormatter.getLocale() == java.util.Locale.CHINA) {
                name = name.substring(name.length() - 1).toUpperCase();
            } else {
                name = name.substring(0, 1).toUpperCase();
            }
            cell.setText(name);

            dayNameCells.add(cell);
        }
    }

    /**
     * Creates the MFXDateCells that will populate the {@link #calendar}.
     * 42 cells are created because the grid is 6x7 at max.
     * Each cell has its text set by default to "null" then starting from the
     * first day index calculated with {@link #firstDayIndex()} the text is set from 1 to monthLength.
     * The cells which still contains "null" are not visible, that's how the grid is built.
     */
    private void createDayCells() {
        days.clear();

        int day = LocalDate.now().getDayOfMonth();

        int i;
        for (i = 1; i <= 42; i++) {
            MFXDateCell cell = new MFXDateCell("null", true);
            cell.getStyleClass().add("day-cell");
            cell.setPrefSize(46, 46);
            cell.setAlignment(Pos.CENTER);
            NodeUtils.makeRegionCircular(cell, 13);

            cell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (lastSelectedDayCell.get() != null) {
                    lastSelectedDayCell.get().setSelectedDate(false);
                }
                lastSelectedDayCell.set(cell);
                lastSelectedDayCell.get().setSelectedDate(true);

                if (lastSelectedYearCell == null) {
                    selectYear();
                }
            });

            days.add(cell);
        }

        int index = firstDayIndex();
        int monthLength = getYearMonth().getMonth().length(getYearMonth().isLeapYear());
        int cnt = 1;
        for (i = index; i < monthLength + index; i++) {
            MFXDateCell cell = days.get(i);
            cell.setText(Integer.toString(cnt));

            if (day == cnt &&
                    LocalDate.now().getMonth().equals(getYearMonth().getMonth()) &&
                    LocalDate.now().getYear() == getYearMonth().getYear()) {
                cell.setCurrent(true);
            }

            cnt++;
        }
    }

    //================================================================================
    // Behavior || Populate
    //================================================================================

    /**
     * Populates the {@link #years} grid with the previously created years cells.
     */
    private void populateYears() {
        years.getChildren().clear();
        years.getColumnConstraints().clear();

        int nCols = 4;
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        columnConstraints.setHalignment(HPos.CENTER);

        int i;
        for (i = 0; i < nCols; i++) {
            years.getColumnConstraints().add(columnConstraints);
        }

        int col = 0;
        int row = 0;
        for (MFXDateCell cell : yearsList) {
            if (col == 4) {
                col = 0;
                row++;
            }
            years.add(cell, col, row);
            col++;
        }
    }

    /**
     * Populates the {@link #calendar} grid with the previously created days cells.
     */
    private void populateCalendar() {
        calendar.getChildren().clear();
        calendar.getColumnConstraints().clear();

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        columnConstraints.setHalignment(HPos.CENTER);

        int i;
        int j;
        for (i = 0; i < daysPerWeek; i++) {
            calendar.getColumnConstraints().add(columnConstraints);
            calendar.add(dayNameCells.get(i), i, 0);
        }

        for (i = 0; i < 6; i++) {
            for (j = 0; j < 7; j++) {
                MFXDateCell cell = days.get(i * 7 + j);
                if (!cell.getText().equals("null")) {
                    calendar.add(cell, j, i + 1);
                }
            }
        }
    }

    //================================================================================
    // Behavior
    //================================================================================

    /**
     * Core of this class.
     * This method represents the behavior of the picker.
     * <p>
     * Adds listeners to {@link #yearMonth} property so when it changes the calendar grid is refreshed {@link #refresh()},
     * to {@link #lastSelectedDayCell} property so when it changes the {@link #currentDate} is updated,
     * to {@link #currentDate} property so when it changes the text of {@link #selectedDate} is updated,
     * to {@link #dateFormatter} property so when it changes the text of {@link #selectedDate} is reformatted.
     */
    private void behaviorListeners() {
        yearMonth.addListener((observable, oldValue, newValue) -> {
            month.setText(StringUtils.titleCaseWord(newValue.getMonth().getDisplayName(TextStyle.FULL, getLocale())));
            year.setText(String.valueOf(newValue.getYear()));
            setCurrentDate(getCurrentDate().withYear(newValue.getYear()).withMonth(newValue.getMonthValue()));

            refresh();

            if (lastSelectedDayCell.get() != null) {
                MFXDateCell day = days.stream()
                        .filter(cell -> cell.getText().equals(lastSelectedDayCell.get().getText()))
                        .findFirst()
                        .orElse(null);
                if (day != null) {
                    lastSelectedDayCell.set(day);
                    day.setSelectedDate(true);
                }
            }
        });

        lastSelectedDayCell.addListener((observable, oldValue, newValue) -> {
            LocalDate date = getCurrentDate().withDayOfMonth(Integer.parseInt(newValue.getText()));
            if (date.equals(getCurrentDate())) {
                setCurrentDate(LocalDate.EPOCH);
            }
            setCurrentDate(date);
        });

        currentDate.addListener((observable, oldValue, newValue) -> selectedDate.setText(newValue.format(getDateFormatter())));

        dateFormatter.addListener((observable, oldValue, newValue) -> selectedDate.setText(getCurrentDate().format(newValue)));
    }

    /**
     * Recreates the day cells and repopulates the calendar.
     * Called every time the year or the month change.
     */
    public void refresh() {
        createDayCells();
        populateCalendar();
    }

    /**
     * Called when {@link #inputButton} is pressed.
     * Switched between mouse and keyboard input.
     */
    private void changeInput() {
        keyInput = !keyInput;

        if (keyInput) {
            yearMonthPane.setVisible(false);
            separator.setVisible(false);
            setPrefHeight(210);
            holder.getChildren().setAll(inputField);
            label.setText("INPUT DATE");
        } else {
            yearMonthPane.setVisible(true);
            separator.setVisible(true);
            setPrefHeight(DEFAULT_HEIGHT);
            holder.getChildren().setAll(calendar, yearsScroll);
            label.setText("SELECT DATE");
        }
    }

    //================================================================================
    // Layout
    //================================================================================

    /**
     * Creates all the buttons (yearsButton, monthBackButton, monthForwardButton, inputButton).
     */
    private void buildButtons() {
        MFXFontIcon chevronDown = new MFXFontIcon("mfx-chevron-down", 13);
        yearsButton = new MFXIconWrapper(chevronDown, 20).rippleGeneratorBehavior(event ->
                new RipplePosition(yearsButton.getWidth() / 2, yearsButton.getHeight() / 2)
        );
        yearsButton.getStyleClass().add("years-button");
        NodeUtils.makeRegionCircular(yearsButton);
        StackPane.setMargin(chevronDown, new Insets(0.3, 0, 0, 0));
        yearsButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            animateYears();
            goToYear();
        });

        MFXFontIcon chevronLeft = new MFXFontIcon("mfx-chevron-left", 13);
        monthBackButton = new MFXIconWrapper(chevronLeft, 20).rippleGeneratorBehavior(event ->
                new RipplePosition(monthBackButton.getWidth() / 2, monthBackButton.getHeight() / 2)
        );
        monthBackButton.getStyleClass().add("month-back-button");
        NodeUtils.makeRegionCircular(monthBackButton);
        monthBackButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> changeMonth(false));

        MFXFontIcon chevronRight = new MFXFontIcon("mfx-chevron-right", 13);
        monthForwardButton = new MFXIconWrapper(chevronRight, 20).rippleGeneratorBehavior(event ->
                new RipplePosition(monthForwardButton.getWidth() / 2, monthForwardButton.getHeight() / 2)
        );
        monthForwardButton.getStyleClass().add("month-forward-button");
        NodeUtils.makeRegionCircular(monthForwardButton);
        monthForwardButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> changeMonth(true));

        MFXFontIcon calendar = new MFXFontIcon("mfx-calendar-semi-black", 17);
        inputButton = new MFXIconWrapper(calendar, 35).rippleGeneratorBehavior(event ->
                new RipplePosition(inputButton.getWidth() / 2, inputButton.getHeight() / 2)
        );
        inputButton.getStyleClass().add("change-input-button");
        Tooltip tooltip = new Tooltip("Switches between mouse input and keyboard input");
        Tooltip.install(inputButton, tooltip);
        NodeUtils.makeRegionCircular(inputButton);
        MFXCircleRippleGenerator rgIB = inputButton.getRippleGenerator();
        rgIB.setAnimationSpeed(1.5);
        inputButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> changeInput());
    }

    /**
     * Calls {@link #buildButtons()} and then build the header.
     */
    private VBox buildHeader() {
        buildButtons();

        label = new Label("SELECT DATE");
        label.setTextFill(Color.WHITE);
        VBox.setMargin(label, DEFAULT_INSETS);

        selectedDate = new Label();
        selectedDate.setId("selected-date");
        selectedDate.setTextFill(Color.WHITE);

        StackPane.setMargin(selectedDate, DEFAULT_INSETS);
        StackPane.setAlignment(selectedDate, Pos.CENTER_LEFT);
        StackPane.setMargin(inputButton, DEFAULT_INSETS);
        StackPane.setAlignment(inputButton, Pos.CENTER_RIGHT);

        StackPane stackPane = new StackPane(selectedDate, inputButton);

        VBox header = new VBox(label, stackPane);
        header.getStyleClass().add("header");
        return header;
    }

    /**
     * Builds the month-year pane.
     */
    private StackPane buildYearMonthPane() {
        // Month-Years
        month = new Label();
        month.getStyleClass().add("month-label");
        year = new Label();
        year.getStyleClass().add("year-label");

        HBox monthYearBox = new HBox(10, month, year, yearsButton);
        monthYearBox.setPrefSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        monthYearBox.setPrefWidth(150);
        monthYearBox.setMaxWidth(Region.USE_PREF_SIZE);
        monthYearBox.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(monthYearBox, Pos.CENTER_LEFT);

        // Backward-Forward
        HBox bBox = new HBox(36, monthBackButton, monthForwardButton);
        bBox.setPrefSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        bBox.setPrefWidth(150);
        bBox.setMaxWidth(Region.USE_PREF_SIZE);
        bBox.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setAlignment(bBox, Pos.CENTER_RIGHT);

        StackPane monthYearPane = new StackPane(monthYearBox, bBox);
        monthYearPane.getStyleClass().add("month-year-pane");
        monthYearPane.setPadding(DEFAULT_INSETS);
        return monthYearPane;
    }

    /**
     * Builds the separator.
     */
    private Separator buildSeparator() {
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefSize(280, 5);
        separator.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        separator.setTranslateX(10);
        VBox.setMargin(separator, new Insets(0, 0, 10, 0));
        return separator;
    }

    /**
     * Builds the calendar.
     */
    private GridPane buildCalendar() {
        calendar = new GridPane();
        calendar.getStyleClass().add("calendar");
        calendar.setVgap(10);

        return calendar;
    }

    /**
     * Builds the years grid.
     */
    private GridPane buildYears() {
        years = new GridPane();
        years.getStyleClass().add("years");
        years.setPadding(DEFAULT_INSETS);
        years.setHgap(10);
        years.setVgap(10);

        return years;
    }

    /**
     * Builds the scrollpane which holds the years grid.
     */
    private MFXScrollPane buildScroll() {
        yearsScroll = new MFXScrollPane(buildYears());
        yearsScroll.getStyleClass().add("years-scrollpane");
        yearsScroll.setFitToWidth(true);
        MFXScrollPane.smoothVScrolling(yearsScroll);

        return yearsScroll;
    }

    /**
     * Builds the text field used for keyboard input.
     */
    private void buildField() {
        inputField = new MFXTextField();
        inputField.setId("input-field");
        inputField.setFont(Font.loadFont(MFXResourcesLoader.loadStream("fonts/OpenSans/OpenSans-SemiBold.ttf"), 16));
        inputField.prefWidthProperty().bind(this.prefWidthProperty().divide(2.0));
        inputField.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        inputField.setPromptText("dd/M/yyyy");
        inputField.setAlignment(Pos.CENTER);
        inputField.setTextLimit(10);

        inputField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                LocalDate date;
                try {
                    date = LocalDate.parse(inputField.getText(), getDateFormatter());
                    setCurrentDate(date);
                    setYearMonth(YearMonth.of(date.getYear(), date.getMonth()));
                    validInput.set(true);
                    selectedDate.setText(date.format(getDateFormatter()));
                    setCurrentDate(LocalDate.parse(selectedDate.getText(), getDateFormatter()));
                } catch (DateTimeParseException ex) {
                    ex.printStackTrace();
                    inputField.getValidator().add(validInput, ex.getMessage());
                    validInput.set(false);
                }
            }
        });

        inputField.getValidator().add(validInput, "Invalid Date");
        inputField.setValidated(true);
    }

    /**
     * Updated the css main color and input field line color.
     */
    public void updateColor(Color color) {
        setStyle("-mfx-main-color: " + ColorUtils.rgb(color) + ";\n");
        inputField.setLineColor(color);
    }

    //================================================================================
    // Animations
    //================================================================================

    /**
     * Builds the animations played when the years grid is opened/closed.
     */
    private void buildAnimations() {
        yearsOpen = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    calendar.setVisible(false);
                    yearsScroll.setVisible(true);
                }),
                new KeyFrame(Duration.millis(150), new KeyValue(yearsButton.rotateProperty(), -180, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(400), new KeyValue(yearsScroll.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
        );

        yearsClose = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(yearsButton.rotateProperty(), 0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(250), new KeyValue(yearsScroll.opacityProperty(), 0.0, Interpolator.EASE_BOTH))
        );
        yearsClose.setOnFinished(event -> {
            yearsScroll.setVisible(false);
            calendar.setVisible(true);
        });
    }

    /**
     * Plays the animations for when years grid is opened/closed.
     */
    private void animateYears() {
        boolean isOpen = yearsScroll.isVisible();
        if (isOpen) {
            yearsClose.play();
        } else {
            yearsOpen.play();
        }
    }

    /**
     * PLays the animation of month switching.
     */
    private void animateCalendar(boolean forward) {
        int offset = (forward ? -1 : 1);

        MFXSnapshotWrapper screen = new MFXSnapshotWrapper(calendar);
        ImageView img = (ImageView) screen.getGraphic();
        img.fitWidthProperty().bind(calendar.widthProperty().subtract(1));
        img.fitHeightProperty().bind(calendar.heightProperty().subtract(1));
        holder.getChildren().add(img);

        Rectangle clip = new Rectangle(img.getFitWidth(), img.getFitHeight());
        holder.setClip(clip);

        calendarTransition = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(img.translateXProperty(), offset * holder.getWidth(), Interpolator.EASE_OUT))
        );
        calendarTransition.setOnFinished(event -> {
            holder.getChildren().remove(img);
            holder.setClip(null);
        });
        calendarTransition.play();
    }

    //================================================================================
    // Utility Methods
    //================================================================================

    /**
     * Finds the index of the first day of the week from {@link #yearMonth}.
     */
    private int firstDayIndex() {
        DayOfWeek fd = getYearMonth().atDay(1).getDayOfWeek();
        LocalDate date = LocalDate.of(2009, 7, 12 + fd.getValue());
        String name = weekDayNameFormatter.withLocale(getLocale()).format(date.plus(0, DAYS));
        return dayNameMap.get(name);
    }

    /**
     * Switches month back or forward and updates {@link #yearMonth} accordingly;
     */
    private void changeMonth(boolean forward) {
        if (calendarTransition != null && calendarTransition.getStatus() != Animation.Status.STOPPED) {
            return;
        }

        if (!yearsScroll.isVisible() && animateCalendar.get()) {
            animateCalendar(forward);
        }

        if (forward) {
            setYearMonth(getYearMonth().plus(1, MONTHS));
        } else {
            setYearMonth(getYearMonth().minus(1, MONTHS));
        }

        if (lastSelectedDayCell.get() == null) {
            selectDay();
        }
        if (lastSelectedYearCell == null) {
            selectYear();
        }

        if (
                lastSelectedDayCell.get() != null &&
                        getYearMonth().getMonth().length(getYearMonth().isLeapYear()) <
                                Integer.parseInt(lastSelectedDayCell.get().getText())
        ) {
            lastSelectedDayCell.get().setSelectedDate(false);

            MFXDateCell cell = days.stream()
                    .filter(dayCell -> dayCell.getText().equals("1"))
                    .findFirst()
                    .orElse(null);

            if (cell != null) {
                cell.setSelectedDate(true);
                lastSelectedDayCell.set(cell);
            }
        }
    }

    private void selectYear() {
        yearsList.stream()
                .filter(year -> year.getText().equals(Integer.toString(getYearMonth().getYear())))
                .findFirst()
                .ifPresent(year -> {
                    lastSelectedYearCell = year;
                    lastSelectedYearCell.setSelectedDate(true);
                });
    }

    private void selectDay() {
        if (getYearMonth().getMonth().length(getYearMonth().isLeapYear()) < LocalDate.now().getDayOfMonth()) {
            days.stream()
                    .filter(day -> day.getText().equals("1"))
                    .findFirst()
                    .ifPresent(day -> {
                        lastSelectedDayCell.set(day);
                        lastSelectedDayCell.get().setSelectedDate(true);
                    });
        } else {
            days.stream()
                    .filter(day -> day.getText().equals(Integer.toString(getCurrentDate().getDayOfMonth())))
                    .findFirst()
                    .ifPresent(day -> {
                        lastSelectedDayCell.set(day);
                        lastSelectedDayCell.get().setSelectedDate(true);
                    });
        }
    }

    /**
     * Moves the scrollpane to the last selected year if not null otherwise to the current year.
     */
    private void goToYear() {
        MFXDateCell cell;
        if (lastSelectedYearCell != null) {
            cell = lastSelectedYearCell;
        } else {
            cell = currYearCell;
        }

        double contentHeight = yearsScroll.getContent().getBoundsInLocal().getHeight();
        double nodePos = cell.getBoundsInParent().getMinY();
        double vScroll = yearsScroll.getVmax() * (nodePos / contentHeight);
        yearsScroll.setVvalue(vScroll);
    }

    private Locale getLocale() {
        return Locale.getDefault(Locale.Category.FORMAT);
    }

    public MFXTextField getInputField() {
        return inputField;
    }

    public LocalDate getCurrentDate() {
        return currentDate.get();
    }

    public ObjectProperty<LocalDate> currentDateProperty() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate.set(currentDate);
    }

    public YearMonth getYearMonth() {
        return yearMonth.get();
    }

    public ObjectProperty<YearMonth> yearMonthProperty() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth.set(yearMonth);
    }

    public MFXDateCell getLastSelectedDayCell() {
        return lastSelectedDayCell.get();
    }

    public ObjectProperty<MFXDateCell> lastSelectedDayCellProperty() {
        return lastSelectedDayCell;
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

    public BooleanProperty animateCalendarProperty() {
        return animateCalendar;
    }
}
