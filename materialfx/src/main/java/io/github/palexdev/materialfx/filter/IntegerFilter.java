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

package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.NumberFilter;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link NumberFilter} for integer fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for integers equality
 * <p> - "is not": checks for integers inequality
 * <p> - "greater than": checks if a integer is greater than another integer
 * <p> - "greater or equal to": checks if a integer is greater or equal to another integer
 * <p> - "lesser than": checks if a integer is lesser than another integer
 * <p> - "lesser or equal to": checks if a integer is lesser or equal to another integer
 */
public class IntegerFilter<T> extends NumberFilter<T, Integer> {

	//================================================================================
	// Constructors
	//================================================================================
	public IntegerFilter(String name, Function<T, Integer> extractor) {
		this(name, extractor, new IntegerStringConverter());
	}

	public IntegerFilter(String name, Function<T, Integer> extractor, StringConverter<Integer> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<Integer, Integer>> defaultPredicates() {
		return Stream.<BiPredicateBean<Integer, Integer>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.is"), Integer::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.isNot"), (anInteger, anInteger2) -> !anInteger.equals(anInteger2)),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greater"), (anInteger, anInteger2) -> anInteger > anInteger2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greaterEqual"), (anInteger, anInteger2) -> anInteger >= anInteger2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesser"), (anInteger, anInteger2) -> anInteger < anInteger2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesserEqual"), (anInteger, anInteger2) -> anInteger <= anInteger2)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final IntegerFilter<T> extend(BiPredicateBean<Integer, Integer>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
