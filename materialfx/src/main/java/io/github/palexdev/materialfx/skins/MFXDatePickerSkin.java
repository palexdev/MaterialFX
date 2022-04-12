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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Skin associated with every {@link MFXDatePicker} by default.
 * <p>
 * Extends {@link MFXTextFieldSkin} and adds the necessary properties, components, listeners
 * and bindings to build the date picker.
 * <p></p>
 * Compared to the old implementation (it was the MFXDatePickerContent class) this is much
 * smaller, easier to read/understand, organized and efficient.
 * <p></p>
 * The structure is pretty simple. The popup contains a {@link GridPane} which shows
 * the days of the current selected month. The month/year selection is controlled by a pair of {@link MFXComboBox}
 * and arrows.
 * <p>
 * The grid is 6x7 (rows x columns). The first row contains the week days, the other rows contain the days.
 * To make it as efficient as possible both the week days and the days cells are cached. The cells are just
 * updated when needed.
 * <p>
 * When the {@link MFXDatePicker#cellFactoryProperty()} or the {@link MFXDatePicker#localeProperty()} change
 * a full/partial reset of the cache is needed.
 */
public class MFXDatePickerSkin extends MFXTextFieldSkin {
	//================================================================================
	// Properties
	//================================================================================

	// Cache
	private Map<DayOfWeek, Integer> weekDays;
	private List<Label> weekDaysLabels = new ArrayList<>();
	private MFXDateCell[][] cells;
	private boolean cellsInitialized = false;
	private boolean weekDaysChanged = false;

	// Components
	private final GridPane grid;
	private final MFXPopup popup;
	private EventHandler<MouseEvent> popupManager;

	// State
	private YearMonth currentYearMonth;
	private Integer[][] monthMatrix;
	private final ObservableList<Integer> years = FXCollections.observableArrayList();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXDatePickerSkin(MFXDatePicker datePicker, BoundTextField boundField) {
		super(datePicker, boundField);

		popup = new MFXPopup() {
			@Override
			public String getUserAgentStylesheet() {
				return datePicker.getUserAgentStylesheet();
			}
		};
		popup.getStyleClass().add("date-picker-popup");
		popup.setPopupStyleableParent(datePicker);
		popup.setAutoHide(true);
		popupManager = event -> datePicker.show();

		weekDays = DateTimeUtils.weekDays(datePicker.getLocale());
		currentYearMonth = datePicker.getStartingYearMonth() != null ? datePicker.getStartingYearMonth() : DateTimeUtils.dateToYearMonth(datePicker.getCurrentDate());

		NumberRange<Integer> yearsRange = datePicker.getYearsRange();
		years.setAll(
				IntStream.rangeClosed(yearsRange.getMin(), yearsRange.getMax())
						.boxed()
						.collect(Collectors.toList())
		);

		monthMatrix = datePicker.getGridAlgorithm().apply(datePicker.getLocale(), currentYearMonth);
		grid = new GridPane();
		grid.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(grid, Priority.ALWAYS);

		initialize();
		setBehavior();
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void initialize() {
		MFXDatePicker datePicker = getDatePicker();
		popup.setContent(createPopupContent());

		LocalDate date = datePicker.getValue();
		if (date != null) {
			updateValue(date);
			ExecutionUtils.executeWhen(
					datePicker.delegateSelectedTextProperty(),
					(oldValue, newValue) -> datePicker.positionCaret(newValue.length()),
					false,
					(oldValue, newValue) -> !newValue.isEmpty(),
					true
			);
		}
	}

	protected void setBehavior() {
		datePickerBehavior();
		iconBehavior();
		popupBehavior();
	}

	/**
	 * Handles the commit event (on ENTER pressed and if editable), the cancel event
	 * (on Ctrl+Shift+Z pressed and if editable), the update of the date picker's text when the
	 * value changes (using {@link #updateValue(LocalDate)}).
	 * Handles the cache and the state when the following properties change:
	 * <p> - {@link MFXDatePicker#converterSupplierProperty()} (update the text)
	 * <p> - {@link MFXDatePicker#dayOfWeekConverterSupplierProperty()} (update the week days)
	 * <p> - {@link MFXDatePicker#cellFactoryProperty()} (full reset of the cache)
	 * <p> - {@link MFXDatePicker#localeProperty()} (partial reset of the cache)
	 * <p> - {@link MFXDatePicker#yearsRangeProperty()}
	 * <p> - {@link MFXDatePicker#gridAlgorithmProperty()}
	 */
	private void datePickerBehavior() {
		MFXDatePicker datePicker = getDatePicker();
		datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (!datePicker.isEditable()) return;
			switch (event.getCode()) {
				case ENTER: {
					datePicker.commit(datePicker.getText());
					break;
				}
				case Z: {
					if (event.isShiftDown() && event.isControlDown()) {
						datePicker.cancel(datePicker.getText());
					}
					break;
				}
			}
		});

		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			updateValue(newValue);
			if (datePicker.isClosePopupOnChange()) popup.hide();
		});
		datePicker.valueProperty().addListener(invalidated -> Event.fireEvent(datePicker, new ActionEvent()));

		datePicker.converterSupplierProperty().addListener((observable, oldValue, newValue) -> updateValue(datePicker.getValue()));
		datePicker.dayOfWeekConverterSupplierProperty().addListener((observable, oldValue, newValue) -> updateWeekDays());
		datePicker.cellFactoryProperty().addListener((observable, oldValue, newValue) -> {
			monthMatrix = null;
			cells = null;
			cellsInitialized = false;
			grid.getChildren().clear();
			weekDaysLabels.clear();
			updateWeekDays();
			updateGrid();
		});
		datePicker.localeProperty().addListener((observable, oldValue, newValue) -> {
			monthMatrix = null;
			weekDays = DateTimeUtils.weekDays(newValue);
			weekDaysChanged = true;
			updateWeekDays();
			updateGrid();
		});
		datePicker.yearsRangeProperty().addListener((observable, oldValue, newValue) ->
				years.setAll(
						IntStream.rangeClosed(newValue.getMin(), newValue.getMax())
								.boxed()
								.collect(Collectors.toList())
				));
		datePicker.gridAlgorithmProperty().addListener((observable, oldValue, newValue) -> {
			monthMatrix = null;
			updateGrid();
		});
	}

	/**
	 * Handles the trailing icon, responsible for opening the popup.
	 */
	private void iconBehavior() {
		MFXDatePicker datePicker = getDatePicker();
		Node trailingIcon = datePicker.getTrailingIcon();
		if (trailingIcon != null) {
			trailingIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
		}

		datePicker.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
			}
			if (newValue != null) {
				newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
			}
		});

		popup.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				datePicker.hide();
				if (trailingIcon instanceof MFXIconWrapper) {
					MFXIconWrapper icon = (MFXIconWrapper) trailingIcon;
					icon.getRippleGenerator().generateRipple(null);
				}
			}
		});
	}

	/**
	 * Handles the popup events and the date picker's {@link MFXDatePicker#showingProperty()}.
	 */
	private void popupBehavior() {
		MFXDatePicker datePicker = getDatePicker();
		popup.setOnShowing(event -> Event.fireEvent(datePicker, new Event(popup, datePicker, MFXComboBox.ON_SHOWING)));
		popup.setOnShown(event -> Event.fireEvent(datePicker, new Event(popup, datePicker, MFXComboBox.ON_SHOWN)));
		popup.setOnHiding(event -> Event.fireEvent(datePicker, new Event(popup, datePicker, MFXComboBox.ON_HIDING)));
		popup.setOnHidden(event -> Event.fireEvent(datePicker, new Event(popup, datePicker, MFXComboBox.ON_HIDDEN)));

		datePicker.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				popup.show(datePicker, datePicker.getPopupAlignment(), datePicker.getPopupOffsetX(), datePicker.getPopupOffsetY());
			}
		});
	}

	/**
	 * Responsible for updating the date picker's text with the given date.
	 * <p>
	 * The date is converted using the date picker's {@link MFXDatePicker#converterSupplierProperty()}.
	 * In case it's null uses toString().
	 * <p>
	 * The caret is always positioned at the end of the text after the update.
	 */
	protected void updateValue(LocalDate date) {
		MFXDatePicker datePicker = getDatePicker();
		String s = "";
		if (date != null) {
			StringConverter<LocalDate> converter = datePicker.getConverterSupplier().get();
			s = converter != null ? converter.toString(date) : date.toString();
		}
		datePicker.setText(s);
		datePicker.positionCaret(s.length());
	}

	/**
	 * Responsible for creating the popup's content.
	 */
	protected Node createPopupContent() {
		MFXDatePicker datePicker = getDatePicker();

		MFXComboBox<Month> monthCombo = new MFXComboBox<>(FXCollections.observableArrayList(Month.values())) {
			@Override
			public String getUserAgentStylesheet() {
				return datePicker.getUserAgentStylesheet();
			}
		};
		monthCombo.getStyleClass().add("months-combo");
		monthCombo.converterProperty().bind(Bindings.createObjectBinding(
				() -> datePicker.getMonthConverterSupplier().get(),
				datePicker.monthConverterSupplierProperty()
		));
		monthCombo.selectItem(currentYearMonth.getMonth());
		monthCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			currentYearMonth = currentYearMonth.withMonth(newValue.getValue());
			monthMatrix = datePicker.getGridAlgorithm().apply(datePicker.getLocale(), currentYearMonth);
			updateGrid();
		});

		MFXComboBox<Integer> yearCombo = new MFXComboBox<>(years) {
			@Override
			public String getUserAgentStylesheet() {
				return datePicker.getUserAgentStylesheet();
			}
		};
		yearCombo.getStyleClass().add("years-combo");
		yearCombo.selectItem(currentYearMonth.getYear());
		yearCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			currentYearMonth = currentYearMonth.withYear(newValue);
			monthMatrix = datePicker.getGridAlgorithm().apply(datePicker.getLocale(), currentYearMonth);
			updateGrid();
		});

		MFXIconWrapper leftArrow = new MFXIconWrapper("mfx-arrow-back", 14, 30).defaultRippleGeneratorBehavior();
		MFXIconWrapper rightArrow = new MFXIconWrapper("mfx-arrow-forward", 14, 30).defaultRippleGeneratorBehavior();

		leftArrow.getStyleClass().add("left-arrow");
		rightArrow.getStyleClass().add("right-arrow");

		leftArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			currentYearMonth = currentYearMonth.plusMonths(-1);
			yearCombo.selectItem(currentYearMonth.getYear());
			monthCombo.selectItem(currentYearMonth.getMonth());
		});
		leftArrow.disableProperty().bind(Bindings.createBooleanBinding(
				() -> Objects.equals(yearCombo.getSelectedItem(), datePicker.getYearsRange().getMin()) && currentYearMonth.getMonth() == Month.JANUARY,
				datePicker.yearsRangeProperty(), yearCombo.selectedItemProperty(), monthCombo.selectedItemProperty()
		));

		rightArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			currentYearMonth = currentYearMonth.plusMonths(1);
			yearCombo.selectItem(currentYearMonth.getYear());
			monthCombo.selectItem(currentYearMonth.getMonth());
		});
		rightArrow.disableProperty().bind(Bindings.createBooleanBinding(
				() -> Objects.equals(yearCombo.getSelectedItem(), datePicker.getYearsRange().getMax()) && currentYearMonth.getMonth() == Month.DECEMBER,
				datePicker.yearsRangeProperty(), yearCombo.selectedItemProperty(), monthCombo.selectedItemProperty()
		));

		NodeUtils.makeRegionCircular(leftArrow);
		NodeUtils.makeRegionCircular(rightArrow);

		HBox comboContainer = new HBox(10, leftArrow, monthCombo, yearCombo, rightArrow);
		comboContainer.setAlignment(Pos.CENTER);

		grid.getColumnConstraints().clear();
		for (int i = 0; i < DateTimeUtils.CALENDAR_COLUMNS; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHalignment(HPos.CENTER);
			cc.setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().add(cc);
		}

		grid.getRowConstraints().clear();
		for (int i = 0; i < DateTimeUtils.CALENDAR_ROWS; i++) {
			RowConstraints rc = new RowConstraints();
			rc.setVgrow(Priority.ALWAYS);
			grid.getRowConstraints().add(rc);
		}

		updateWeekDays();
		updateGrid();

		VBox container = new VBox(20, comboContainer, grid);
		container.getStyleClass().add("content");
		container.setAlignment(Pos.TOP_CENTER);
		return container;
	}

	/**
	 * Responsible for generating the week days cache or updating it if a reset was
	 * not needed.
	 */
	private void updateWeekDays() {
		MFXDatePicker datePicker = getDatePicker();
		StringConverter<DayOfWeek> dayOfWeekConverter = datePicker.getDayOfWeekConverterSupplier().get();
		if (weekDaysLabels.isEmpty()) {
			for (DayOfWeek dayOfWeek : weekDays.keySet()) {
				Label label = new Label(dayOfWeekConverter.toString(dayOfWeek));
				label.getStyleClass().add("week-day");
				label.setAlignment(Pos.CENTER);
				weekDaysLabels.add(label);
			}
			grid.addRow(0, weekDaysLabels.toArray(Node[]::new));
			return;
		}

		if (weekDaysChanged) {
			int i = 0;
			for (DayOfWeek dayOfWeek : weekDays.keySet()) {
				Label label = weekDaysLabels.get(i);
				label.setText(dayOfWeekConverter.toString(dayOfWeek));
				i++;
			}
			weekDaysChanged = false;
		}
	}

	/**
	 * Responsible for updating the days grid, also builds the cells cache or
	 * updates it if a reset was not needed.
	 * <p></p>
	 * This is also responsible for marking/un-marking some cells as "extra" cells, {@link MFXDateCell#markAsExtra()},
	 * {@link MFXDateCell#unmarkAsExtra()}. Extra cells are those cells that contains days belonging to the previous/next month.
	 */
	private void updateGrid() {
		MFXDatePicker datePicker = getDatePicker();

		if (monthMatrix == null) {
			monthMatrix = datePicker.getGridAlgorithm().apply(datePicker.getLocale(), currentYearMonth);
		}
		if (cells == null) {
			cells = new MFXDateCell[monthMatrix.length][monthMatrix[0].length];
		}

		List<Node> children = new ArrayList<>();
		int row = 1;
		int index;
		int startIndex = DateTimeUtils.startIndexFor(currentYearMonth, datePicker.getLocale());
		int endIndex = DateTimeUtils.endIndexFor(currentYearMonth, datePicker.getLocale());

		for (int i = 0; i < monthMatrix.length; i++) {
			Integer[] matrixRow = monthMatrix[i];
			for (int j = 0; j < matrixRow.length; j++) {
				index = (i * DateTimeUtils.CALENDAR_COLUMNS) + j;
				Integer day = matrixRow[j];
				LocalDate date;
				MFXDateCell cell;

				if (day == null) {
					cell = getCell(i, j, null);
					cell.updateItem(null);
					cells[i][j] = cell;
					children.add(cell.getNode());
					continue;
				}

				if (index < startIndex) {
					YearMonth previous = currentYearMonth.plusMonths(-1);
					date = LocalDate.of(previous.getYear(), previous.getMonth(), day);
					cell = getCell(i, j, date);
					cell.markAsExtra();
				} else if (index > endIndex) {
					YearMonth next = currentYearMonth.plusMonths(1);
					date = LocalDate.of(next.getYear(), next.getMonth(), day);
					cell = getCell(i, j, date);
					cell.markAsExtra();
				} else {
					date = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), day);
					cell = getCell(i, j, date);
					cell.unmarkAsExtra();
				}

				if (!cellsInitialized) {
					cells[i][j] = cell;
				}
				cell.updateItem(date);
				children.add(cell.getNode());
			}

			if (!cellsInitialized) {
				grid.addRow(row, children.toArray(Node[]::new));
			}

			children.clear();
			row++;
		}
		cellsInitialized = true;
	}

	/**
	 * If the cells cache has already been built returns the cell at the given [row][column],
	 * otherwise uses the {@link MFXDatePicker#cellFactoryProperty()} to create a new cell with the
	 * given date.
	 */
	private MFXDateCell getCell(int row, int column, LocalDate date) {
		MFXDatePicker datePicker = (MFXDatePicker) getSkinnable();
		Function<LocalDate, MFXDateCell> cellFactory = datePicker.getCellFactory();
		if (!cellsInitialized) {
			return cellFactory.apply(date);
		}
		return cells[row][column];
	}

	/**
	 * Convenience method to cast {@link #getSkinnable()} to {@code MFXDatePicker}.
	 */
	public MFXDatePicker getDatePicker() {
		return (MFXDatePicker) getSkinnable();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void dispose() {
		super.dispose();
		MFXDatePicker datePicker = getDatePicker();
		if (datePicker.getTrailingIcon() != null) {
			datePicker.getTrailingIcon().removeEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
		}
		popupManager = null;
		weekDaysLabels.clear();
		weekDaysLabels = null;
		cells = null;
	}
}
