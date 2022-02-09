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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.cell.MFXDateCell;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DatePickerBuilder extends TextFieldBuilder<MFXDatePicker> {

	//================================================================================
	// Constructors
	//================================================================================
	public DatePickerBuilder() {
	}

	public DatePickerBuilder(MFXDatePicker datePicker) {
		super(datePicker);
	}

	public static DatePickerBuilder datePicker() {
		return new DatePickerBuilder();
	}

	public static DatePickerBuilder datePicker(MFXDatePicker datePicker) {
		return new DatePickerBuilder(datePicker);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public DatePickerBuilder startCurrentDayUpdater() {
		node.startCurrentDayUpdater();
		return this;
	}

	public DatePickerBuilder stopCurrentDayUpdater() {
		node.stopCurrentDayUpdater();
		return this;
	}

	public DatePickerBuilder setPopupAlignment(Alignment popupAlignment) {
		node.setPopupAlignment(popupAlignment);
		return this;
	}

	public DatePickerBuilder setPopupOffsetX(double popupOffsetX) {
		node.setPopupOffsetX(popupOffsetX);
		return this;
	}

	public DatePickerBuilder setPopupOffsetY(double popupOffsetY) {
		node.setPopupOffsetY(popupOffsetY);
		return this;
	}

	public DatePickerBuilder setLocale(Locale locale) {
		node.setLocale(locale);
		return this;
	}

	public DatePickerBuilder setValue(LocalDate value) {
		node.setValue(value);
		return this;
	}

	public DatePickerBuilder setConverterSupplier(Supplier<StringConverter<LocalDate>> converterSupplier) {
		node.setConverterSupplier(converterSupplier);
		return this;
	}

	public DatePickerBuilder setMonthConverterSupplier(Supplier<StringConverter<Month>> monthConverterSupplier) {
		node.setMonthConverterSupplier(monthConverterSupplier);
		return this;
	}

	public DatePickerBuilder setDayOfWeekConverterSupplier(Supplier<StringConverter<DayOfWeek>> dayOfWeekConverterSupplier) {
		node.setDayOfWeekConverterSupplier(dayOfWeekConverterSupplier);
		return this;
	}

	public DatePickerBuilder setOnCommit(Consumer<String> onCommit) {
		node.setOnCommit(onCommit);
		return this;
	}

	public DatePickerBuilder setOnCancel(Consumer<String> onCancel) {
		node.setOnCancel(onCancel);
		return this;
	}

	public DatePickerBuilder setCellFactory(Function<LocalDate, MFXDateCell> cellFactory) {
		node.setCellFactory(cellFactory);
		return this;
	}

	public DatePickerBuilder updateCurrentDate() {
		node.updateCurrentDate();
		return this;
	}

	public DatePickerBuilder setYearsRange(NumberRange<Integer> yearsRange) {
		node.setYearsRange(yearsRange);
		return this;
	}

	public DatePickerBuilder setGridAlgorithm(BiFunction<Locale, YearMonth, Integer[][]> gridAlgorithm) {
		node.setGridAlgorithm(gridAlgorithm);
		return this;
	}

	public DatePickerBuilder setStartingYearMonth(YearMonth startingYearMonth) {
		node.setStartingYearMonth(startingYearMonth);
		return this;
	}

	public DatePickerBuilder setClosePopupOnChange(boolean closePopupOnChange) {
		node.setClosePopupOnChange(closePopupOnChange);
		return this;
	}
}
