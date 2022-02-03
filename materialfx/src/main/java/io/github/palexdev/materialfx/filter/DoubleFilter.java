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
import javafx.util.converter.DoubleStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link NumberFilter} for double fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for doubles equality
 * <p> - "is not": checks for doubles inequality
 * <p> - "greater than": checks if a double is greater than another double
 * <p> - "greater or equal to": checks if a double is greater or equal to another double
 * <p> - "lesser than": checks if a double is lesser than another double
 * <p> - "lesser or equal to": checks if a double is lesser or equal to another double
 */
public class DoubleFilter<T> extends NumberFilter<T, Double> {

	//================================================================================
	// Constructors
	//================================================================================
	public DoubleFilter(String name, Function<T, Double> extractor) {
		this(name, extractor, new DoubleStringConverter());
	}

	public DoubleFilter(String name, Function<T, Double> extractor, StringConverter<Double> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<Double, Double>> defaultPredicates() {
		return Stream.<BiPredicateBean<Double, Double>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.is"), Double::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.isNot"), (aDouble, aDouble2) -> !aDouble.equals(aDouble2)),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greater"), (aDouble, aDouble2) -> aDouble > aDouble2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greaterEqual"), (aDouble, aDouble2) -> aDouble >= aDouble2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesser"), (aDouble, aDouble2) -> aDouble < aDouble2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesserEqual"), (aDouble, aDouble2) -> aDouble <= aDouble2)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final DoubleFilter<T> extend(BiPredicateBean<Double, Double>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
