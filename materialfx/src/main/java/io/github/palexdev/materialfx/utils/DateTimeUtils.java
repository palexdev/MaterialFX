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

package io.github.palexdev.materialfx.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utils class for Java's time API.
 */
public class DateTimeUtils {
	public static final int CALENDAR_ROWS = 6;
	public static final int CALENDAR_COLUMNS = 7;

	private DateTimeUtils() {
	}

	/**
	 * Builds a bi-dimensional array of integers (6 rows x 7 columns) that contains only the days
	 * of the month specified by the given {@link YearMonth}.
	 * <p></p>
	 * That means that at most 31 positions will contain a day, the others will contain null.
	 */
	public static Integer[][] partialIntMonthMatrix(Locale locale, YearMonth yearMonth) {
		Map<DayOfWeek, Integer> weekDays = weekDays(locale);
		Integer[][] matrix = new Integer[CALENDAR_ROWS][CALENDAR_COLUMNS];
		int start = weekDays.get(yearMonth.atDay(1).getDayOfWeek());

		int row = 0;
		int column = start;

		for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
			matrix[row][column] = i;
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
		}

		return matrix;
	}

	/**
	 * Builds a bi-dimensional array of {@link Day}s (6 rows x 7 columns) that contains only the days
	 * of the month specified by the given {@link YearMonth}.
	 * <p></p>
	 * That means that at most 31 positions will contain a day, the others will contain null.
	 */
	public static Day[][] partialDayMonthMatrix(Locale locale, YearMonth yearMonth) {
		Map<DayOfWeek, Integer> weekDays = weekDays(locale);
		Day[][] matrix = new Day[CALENDAR_ROWS][CALENDAR_COLUMNS];
		int start = weekDays.get(yearMonth.atDay(1).getDayOfWeek());

		int row = 0;
		int column = start;

		for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
			DayOfWeek dayOfWeek = yearMonth.atDay(i).getDayOfWeek();
			matrix[row][column] = new Day(yearMonth, dayOfWeek, i);
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
		}

		return matrix;
	}

	/**
	 * Builds a bi-dimensional array of integers (6 rows x 7 columns) that completely
	 * fills the matrix. Empty positions will contain the days belonging the the previous/next
	 * month.
	 */
	public static Integer[][] fullIntMonthMatrix(Locale locale, YearMonth yearMonth) {
		Map<DayOfWeek, Integer> weekDays = weekDays(locale);
		Integer[][] matrix = new Integer[CALENDAR_ROWS][CALENDAR_COLUMNS];
		int start = weekDays.get(yearMonth.atDay(1).getDayOfWeek());

		if (start != 0) {
			int extraStart = start - 1;
			int day = yearMonth.plusMonths(-1).atEndOfMonth().getDayOfMonth();
			while (extraStart >= 0) {
				matrix[0][extraStart] = day;
				day--;
				extraStart--;
			}
		}

		int row = 0;
		int column = start;

		for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
			matrix[row][column] = i;
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
		}

		int end = start + yearMonth.lengthOfMonth() - 1;
		int day = 1;
		while (end < 41) {
			matrix[row][column] = day;
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
			end++;
			day++;
		}

		return matrix;
	}

	/**
	 * Builds a bi-dimensional array of {@link Day}s (6 rows x 7 columns) that completely
	 * fills the matrix. Empty positions will contain the days belonging the the previous/next
	 * month.
	 */
	public static Day[][] fullDayMonthMatrix(Locale locale, YearMonth yearMonth) {
		Map<DayOfWeek, Integer> weekDays = weekDays(locale);
		Day[][] matrix = new Day[CALENDAR_ROWS][CALENDAR_COLUMNS];
		int start = weekDays.get(yearMonth.atDay(1).getDayOfWeek());

		if (start != 0) {
			int extraStart = start - 1;
			YearMonth previous = yearMonth.plusMonths(-1);
			int day = previous.atEndOfMonth().getDayOfMonth();
			while (extraStart >= 0) {
				DayOfWeek dayOfWeek = yearMonth.plusMonths(-1).atDay(day).getDayOfWeek();
				matrix[0][extraStart] = new Day(yearMonth, dayOfWeek, day);
				day--;
				extraStart--;
			}
		}

		int row = 0;
		int column = start;

		for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
			DayOfWeek dayOfWeek = yearMonth.atDay(i).getDayOfWeek();
			matrix[row][column] = new Day(yearMonth, dayOfWeek, i);
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
		}

		YearMonth next = yearMonth.plusMonths(1);
		int end = start + yearMonth.lengthOfMonth() - 1;
		int day = 1;
		while (end < 41) {
			DayOfWeek dayOfWeek = next.atDay(day).getDayOfWeek();
			matrix[row][column] = new Day(next, dayOfWeek, day);
			column++;
			if (column == CALENDAR_COLUMNS) {
				row++;
				column = 0;
			}
			end++;
			day++;
		}

		return matrix;
	}

	/**
	 * Builds a map containing the week days according to the given locale,
	 * see {@link #weekDays(Locale)} for further info.
	 * Then gets the starting {@link DayOfWeek} for the given month,
	 * and queries the map with it.
	 * <p>
	 * The result is the index at which the month starts.
	 */
	public static int startIndexFor(YearMonth yearMonth, Locale locale) {
		Map<DayOfWeek, Integer> weekDays = weekDays(locale);
		return weekDays.get(yearMonth.atDay(1).getDayOfWeek());
	}

	/**
	 * Computes the index at which the month ends, by computing the
	 * starting index, {@link #startIndexFor(YearMonth, Locale)}, and then
	 * adding the length of the month to the result - 1.
	 */
	public static int endIndexFor(YearMonth yearMonth, Locale locale) {
		int start = startIndexFor(yearMonth, locale);
		return start + yearMonth.lengthOfMonth() - 1;
	}

	/**
	 * The {@link DayOfWeek} enumerator assumes that Monday is the first
	 * day of the week. This however depends on the country (locale).
	 * <p>
	 * This method generates a Map associating each {@link DayOfWeek} to
	 * its position in the week.
	 * <p>
	 * So, for example for the US locale, Sunday which is the seventh
	 * {@link DayOfWeek} is associated to 1, since the week starts with Sunday.
	 */
	public static Map<DayOfWeek, Integer> weekDays(Locale locale) {
		DayOfWeek firstDay = WeekFields.of(locale).getFirstDayOfWeek();
		return IntStream.range(0, CALENDAR_COLUMNS)
				.boxed()
				.collect(Collectors.toMap(
						i -> firstDay.plus(i),
						i -> i,
						(anInt, anInt2) -> anInt2,
						LinkedHashMap::new
				));
	}

	/**
	 * Converts the given {@link LocalDate} to a {@link YearMonth}.
	 */
	public static YearMonth dateToYearMonth(LocalDate date) {
		return YearMonth.of(date.getYear(), date.getMonth());
	}

	/**
	 * Simple bean to wrap info about a day of a month such as:
	 * <p> - the referring {@link YearMonth}
	 * <p> - the {@link DayOfWeek}
	 * <p> - the day number in the month
	 */
	public static class Day {
		private final YearMonth yearMonth;
		private final DayOfWeek dayOfWeek;
		private final int monthDay;

		private Day(YearMonth yearMonth, DayOfWeek dayOfWeek, int monthDay) {
			this.yearMonth = yearMonth;
			this.dayOfWeek = dayOfWeek;
			this.monthDay = monthDay;
		}

		public YearMonth getYearMonth() {
			return yearMonth;
		}

		public DayOfWeek getDayOfWeek() {
			return dayOfWeek;
		}

		public int getMonthDay() {
			return monthDay;
		}
	}
}
