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
import javafx.util.converter.FloatStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link NumberFilter} for float fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for floats equality
 * <p> - "is not": checks for floats inequality
 * <p> - "greater than": checks if a float is greater than another float
 * <p> - "greater or equal to": checks if a float is greater or equal to another float
 * <p> - "lesser than": checks if a float is lesser than another float
 * <p> - "lesser or equal to": checks if a float is lesser or equal to another float
 */
public class FloatFilter<T> extends NumberFilter<T, Float> {

	//================================================================================
	// Constructors
	//================================================================================
	public FloatFilter(String name, Function<T, Float> extractor) {
		this(name, extractor, new FloatStringConverter());
	}

	public FloatFilter(String name, Function<T, Float> extractor, StringConverter<Float> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<Float, Float>> defaultPredicates() {
		return Stream.<BiPredicateBean<Float, Float>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.is"), Float::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.isNot"), (aFloat, aFloat2) -> !aFloat.equals(aFloat2)),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greater"), (aFloat, aFloat2) -> aFloat > aFloat2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greaterEqual"), (aFloat, aFloat2) -> aFloat >= aFloat2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesser"), (aFloat, aFloat2) -> aFloat < aFloat2),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesserEqual"), (aFloat, aFloat2) -> aFloat <= aFloat2)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final FloatFilter<T> extend(BiPredicateBean<Float, Float>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
