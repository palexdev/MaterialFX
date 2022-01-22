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
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.NumberRangeProperty;
import io.github.palexdev.materialfx.beans.properties.functional.BiFunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXDatePickerSkin;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.others.ReusableScheduledExecutor;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import io.github.palexdev.materialfx.utils.others.dates.DayOfWeekStringConverter;
import io.github.palexdev.materialfx.utils.others.dates.MonthStringConverter;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

import java.time.*;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A new, completely made from scratch, modern {@code DatePicker} for JavaFX.
 * <p></p>
 * A date picker is basically a text field which shows a popup containing a calendar
 * for a specific month. For this reason, extends {@link MFXTextField}.
 * <p></p>
 * Compared to the previous implementation (that was just a wrapper for the original
 * DatePicker), and the JavaFX's one, the new implementation is much simpler and customizable.
 * <p></p>
 * The main features of this new date picker are:
 * <p> - Floating text (inherited from {@link MFXTextField})
 * <p> - Allows to fully control the popup (offset, alignment)
 * <p> - Has three separate converters to convert {@link LocalDate}, {@link Month} and {@link DayOfWeek} to/from String.
 * Those are specified with {@link Supplier}s. The default converters always take into account the date picker's {@link #localeProperty()}
 * <p> - Has a cell factory function to change the cells
 * <p> - Allows specifying what to do when editing the text on confirm or cancel (by default, specifies in the skin, ENTER to commit
 * and Ctrl+Shift+Z to cancel
 * <p> - Allows to easily change the language by setting the {@link #localeProperty()}
 * <p> - Has a property to get the current date, plus it's possible to automatically or programmatically update it as days pass
 * <p> - Allows to specify the range of years for the date picker
 * <p> - Allows to specify the starting {@link YearMonth} of the calendar
 * <p> - Also adds a new PseudoClass that activates when the popup opens
 * <p> - Since inherits from {@link MFXTextField}, it can be also used as a Label, disabling edit and selection.
 * <p></p>
 * The next one is probably a very unique one. Unlike the old one or the JavaFX's one, this date picker
 * allows you to easily change the way the calendar is filled by setting the {@link #gridAlgorithmProperty()}.
 * By default the date picker uses {@link DateTimeUtils#fullIntMonthMatrix(Locale, YearMonth)} to generate a 6x7
 * grid (rows x columns, it's a bi-dimensional array)
 */
public class MFXDatePicker extends MFXTextField {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-date-picker";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXDatePicker.css");

	// Popup Properties
	private final ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(false);
	private final ObjectProperty<Alignment> popupAlignment = new SimpleObjectProperty<>(Alignment.of(HPos.CENTER, VPos.BOTTOM));
	private final DoubleProperty popupOffsetX = new SimpleDoubleProperty(0);
	private final DoubleProperty popupOffsetY = new SimpleDoubleProperty(3);

	private final ObjectProperty<LocalDate> value = new SimpleObjectProperty<>();
	private final SupplierProperty<StringConverter<LocalDate>> converterSupplier = new SupplierProperty<>();
	private final SupplierProperty<StringConverter<Month>> monthConverterSupplier = new SupplierProperty<>();
	private final SupplierProperty<StringConverter<DayOfWeek>> dayOfWeekConverterSupplier = new SupplierProperty<>();
	private final FunctionProperty<LocalDate, MFXDateCell> cellFactory = new FunctionProperty<>();
	private final ConsumerProperty<String> onCommit = new ConsumerProperty<>(s -> setValue(getConverterSupplier().get().fromString(s)));
	private final ConsumerProperty<String> onCancel = new ConsumerProperty<>(s -> setText(getConverterSupplier().get().toString(getValue())));

	private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.getDefault()) {
		@Override
		public void set(Locale newValue) {
			if (newValue == null) {
				super.set(Locale.getDefault());
				return;
			}
			super.set(newValue);
		}
	};
	private final ReadOnlyObjectWrapper<LocalDate> currentDate = new ReadOnlyObjectWrapper<>(LocalDate.now());
	private final NumberRangeProperty<Integer> yearsRange = new NumberRangeProperty<>(NumberRange.of(1900, 2100)) {
		@Override
		public void set(NumberRange<Integer> newValue) {
			if (newValue == null) {
				super.set(NumberRange.of(1900, 2100));
				return;
			}
			super.set(newValue);
		}
	};
	private final BiFunctionProperty<Locale, YearMonth, Integer[][]> gridAlgorithm = new BiFunctionProperty<>(DateTimeUtils::fullIntMonthMatrix) {
		@Override
		public void set(BiFunction<Locale, YearMonth, Integer[][]> newValue) {
			if (newValue == null) {
				super.set(DateTimeUtils::partialIntMonthMatrix);
				return;
			}
			super.set(newValue);
		}
	};
	private YearMonth startingYearMonth;
	private boolean closePopupOnChange = true;
	private final ReusableScheduledExecutor executor;

	protected static final PseudoClass POPUP_OPEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("popup");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXDatePicker() {
		this(Locale.getDefault());
	}

	public MFXDatePicker(Locale locale) {
		this(locale, YearMonth.now());
	}

	public MFXDatePicker(Locale locale, YearMonth startingYearMonth) {
		setLocale(locale);
		this.startingYearMonth = startingYearMonth;
		this.executor = new ReusableScheduledExecutor(Executors.newScheduledThreadPool(
				1,
				r -> {
					Thread thread = new Thread(r);
					thread.setDaemon(true);
					return thread;
				}
		));
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setPrefWidth(200);
		setFloatMode(FloatMode.DISABLED);

		showing.addListener(invalidated -> pseudoClassStateChanged(POPUP_OPEN_PSEUDO_CLASS, showing.get()));

		defaultCellFactory();
		defaultConverters();
		defaultIcon();
	}

	/**
	 * Sets/Re-sets the default cell factory.
	 */
	public void defaultCellFactory() {
		setCellFactory(date -> new MFXDateCell(this, date));
	}

	/**
	 * Sets/Re-sets the default converters for {@link LocalDate}, {@link Month}, {@link DayOfWeek}.
	 * <p></p>
	 * For:
	 * <p> - LocalDate: uses {@link DateStringConverter} with {@link FormatStyle#MEDIUM}
	 * <p> - Month: uses {@link MonthStringConverter} with the date picker's locale and {@link TextStyle#FULL}
	 * <p> - DayOfWeek: uses {@link DayOfWeekStringConverter} with the date picker's locale and {@link TextStyle#SHORT}
	 */
	public void defaultConverters() {
		setConverterSupplier(() -> new DateStringConverter(FormatStyle.MEDIUM));
		setMonthConverterSupplier(() -> new MonthStringConverter(getLocale(), TextStyle.FULL));
		setDayOfWeekConverterSupplier(() -> new DayOfWeekStringConverter(getLocale(), TextStyle.SHORT));
	}

	/**
	 * Sets/Re-sets the default icon to open the popup.
	 */
	public void defaultIcon() {
		MFXFontIcon calendar = new MFXFontIcon("mfx-calendar-alt-semi-dark", 20);
		calendar.getStyleClass().add("icon");
		MFXIconWrapper wrapped = new MFXIconWrapper(calendar, 30);
		wrapped.rippleGeneratorBehavior(event -> {
			if (event == null) {
				return PositionBean.of(wrapped.getSize() / 2, wrapped.getSize() / 2);
			} else {
				return PositionBean.of(event.getX(), event.getY());
			}
		});
		NodeUtils.makeRegionCircular(wrapped);
		setTrailingIcon(wrapped);
	}

	/**
	 * Shows the popup.
	 */
	public void show() {
		setShowing(true);
	}

	/**
	 * Hides the popup.
	 */
	public void hide() {
		setShowing(false);
	}

	/**
	 * If the date picker is editable and the text has been changed, this method
	 * is responsible for deciding what to do with the new text.
	 * <p></p>
	 * By default this implementation calls the specified {@link #onCommitProperty()} consumer
	 * to perform an action on commit. So, instead of overriding the method you can easily modify
	 * its behavior by changing the consumer.
	 */
	public void commit(String text) {
		if (getOnCommit() != null) {
			getOnCommit().accept(text);
		}
	}

	/**
	 * If the date picker is editable and the text has been changed, this method
	 * is responsible for deciding what to do with the new text.
	 * <p></p>
	 * By default this implementation calls the specified {@link #onCancelProperty()} consumer
	 * to perform an action on cancel. So, instead of overriding the method you can easily modify
	 * its behavior by changing the consumer.
	 */
	public void cancel(String text) {
		if (getOnCancel() != null) {
			getOnCancel().accept(text);
		}
	}

	/**
	 * Starts the executor responsible for updating the current day property
	 * once per day.
	 */
	public void startCurrentDayUpdater() {
		long midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
		executor.scheduleAtFixedRate(this::updateCurrentDate, midnight, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
	}

	/**
	 * Stops the executor responsible for updating the current day property.
	 */
	public void stopCurrentDayUpdater() {
		executor.cancelNow();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXDatePickerSkin(this, boundField);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public boolean isShowing() {
		return showing.get();
	}

	/**
	 * Specifies whether the popup is showing.
	 */
	public ReadOnlyBooleanProperty showingProperty() {
		return showing.getReadOnlyProperty();
	}

	private void setShowing(boolean showing) {
		this.showing.set(showing);
	}

	public Alignment getPopupAlignment() {
		return popupAlignment.get();
	}

	/**
	 * Specifies the popup's alignment.
	 */
	public ObjectProperty<Alignment> popupAlignmentProperty() {
		return popupAlignment;
	}

	public void setPopupAlignment(Alignment popupAlignment) {
		this.popupAlignment.set(popupAlignment);
	}

	public double getPopupOffsetX() {
		return popupOffsetX.get();
	}

	/**
	 * Specifies the popup's x offset.
	 */
	public DoubleProperty popupOffsetXProperty() {
		return popupOffsetX;
	}

	public void setPopupOffsetX(double popupOffsetX) {
		this.popupOffsetX.set(popupOffsetX);
	}

	public double getPopupOffsetY() {
		return popupOffsetY.get();
	}

	/**
	 * Specifies the popup's y offset.
	 */
	public DoubleProperty popupOffsetYProperty() {
		return popupOffsetY;
	}

	public void setPopupOffsetY(double popupOffsetY) {
		this.popupOffsetY.set(popupOffsetY);
	}

	public Locale getLocale() {
		return locale.get();
	}

	/**
	 * Specifies the {@link Locale} used by this date picker.
	 * The Locale is mainly responsible for changing the language and the
	 * grid disposition (different week start for example)
	 */
	public ObjectProperty<Locale> localeProperty() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale.set(locale);
	}

	public LocalDate getValue() {
		return value.get();
	}

	/**
	 * Specifies the current selected date.
	 */
	public ObjectProperty<LocalDate> valueProperty() {
		return value;
	}

	public void setValue(LocalDate value) {
		this.value.set(value);
	}

	public Supplier<StringConverter<LocalDate>> getConverterSupplier() {
		return converterSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to create a {@link StringConverter} capable of converting {@link LocalDate}s.
	 */
	public SupplierProperty<StringConverter<LocalDate>> converterSupplierProperty() {
		return converterSupplier;
	}

	public void setConverterSupplier(Supplier<StringConverter<LocalDate>> converterSupplier) {
		this.converterSupplier.set(converterSupplier);
	}

	public Supplier<StringConverter<Month>> getMonthConverterSupplier() {
		return monthConverterSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to create a {@link StringConverter} capable of converting {@link Month}s.
	 */
	public SupplierProperty<StringConverter<Month>> monthConverterSupplierProperty() {
		return monthConverterSupplier;
	}

	public void setMonthConverterSupplier(Supplier<StringConverter<Month>> monthConverterSupplier) {
		this.monthConverterSupplier.set(monthConverterSupplier);
	}

	public Supplier<StringConverter<DayOfWeek>> getDayOfWeekConverterSupplier() {
		return dayOfWeekConverterSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to create a {@link StringConverter} capable of converting {@link DayOfWeek}s.
	 */
	public SupplierProperty<StringConverter<DayOfWeek>> dayOfWeekConverterSupplierProperty() {
		return dayOfWeekConverterSupplier;
	}

	public void setDayOfWeekConverterSupplier(Supplier<StringConverter<DayOfWeek>> dayOfWeekConverterSupplier) {
		this.dayOfWeekConverterSupplier.set(dayOfWeekConverterSupplier);
	}

	public Consumer<String> getOnCommit() {
		return onCommit.get();
	}

	/**
	 * Specifies the action to perform on {@link #commit(String)}.
	 */
	public ConsumerProperty<String> onCommitProperty() {
		return onCommit;
	}

	public void setOnCommit(Consumer<String> onCommit) {
		this.onCommit.set(onCommit);
	}

	public Consumer<String> getOnCancel() {
		return onCancel.get();
	}

	/**
	 * Specifies the action to perform on {@link #cancel(String)}.
	 */
	public ConsumerProperty<String> onCancelProperty() {
		return onCancel;
	}

	public void setOnCancel(Consumer<String> onCancel) {
		this.onCancel.set(onCancel);
	}

	public Function<LocalDate, MFXDateCell> getCellFactory() {
		return cellFactory.get();
	}

	/**
	 * Specifies the function used to create the day cells in the grid.
	 */
	public ObjectProperty<Function<LocalDate, MFXDateCell>> cellFactoryProperty() {
		return cellFactory;
	}

	public void setCellFactory(Function<LocalDate, MFXDateCell> cellFactory) {
		this.cellFactory.set(cellFactory);
	}

	public LocalDate getCurrentDate() {
		return currentDate.get();
	}

	/**
	 * Specifies the current date.
	 */
	public ReadOnlyObjectProperty<LocalDate> currentDateProperty() {
		return currentDate.getReadOnlyProperty();
	}

	/**
	 * Updates the current date property with {@link LocalDate#now()}
	 */
	public void updateCurrentDate() {
		this.currentDate.set(LocalDate.now());
	}

	public NumberRange<Integer> getYearsRange() {
		return yearsRange.get();
	}

	/**
	 * Specifies the years range of the date picker.
	 */
	public NumberRangeProperty<Integer> yearsRangeProperty() {
		return yearsRange;
	}

	public void setYearsRange(NumberRange<Integer> yearsRange) {
		this.yearsRange.set(yearsRange);
	}

	public BiFunction<Locale, YearMonth, Integer[][]> getGridAlgorithm() {
		return gridAlgorithm.get();
	}

	/**
	 * Specifies the {@link BiFunction} used to generate the month grid which is a bi-dimensional array of
	 * integer values.
	 */
	public BiFunctionProperty<Locale, YearMonth, Integer[][]> gridAlgorithmProperty() {
		return gridAlgorithm;
	}

	public void setGridAlgorithm(BiFunction<Locale, YearMonth, Integer[][]> gridAlgorithm) {
		this.gridAlgorithm.set(gridAlgorithm);
	}

	/**
	 * @return the date picker's starting {@link YearMonth}
	 */
	public YearMonth getStartingYearMonth() {
		return startingYearMonth;
	}

	/**
	 * Sets the {@link YearMonth} at which the date picker will start.
	 * <p>
	 * Note that this will be relevant only for the first initialization. Setting
	 * this afterwards won't take any effect.
	 */
	public void setStartingYearMonth(YearMonth startingYearMonth) {
		this.startingYearMonth = startingYearMonth;
	}

	/**
	 * @return whether the popup should stay open on value change or close
	 */
	public boolean isClosePopupOnChange() {
		return closePopupOnChange;
	}

	public void setClosePopupOnChange(boolean closePopupOnChange) {
		this.closePopupOnChange = closePopupOnChange;
	}
}
