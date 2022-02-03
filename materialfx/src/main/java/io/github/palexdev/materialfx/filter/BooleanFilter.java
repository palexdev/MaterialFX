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
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link AbstractFilter} for boolean fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for booleans equality
 * <p> - "is not": checks for booleans inequality
 */
public class BooleanFilter<T> extends AbstractFilter<T, Boolean> {

	//================================================================================
	// Constructors
	//================================================================================
	public BooleanFilter(String name, Function<T, Boolean> extractor) {
		this(name, extractor, new BooleanStringConverter());
	}

	public BooleanFilter(String name, Function<T, Boolean> extractor, StringConverter<Boolean> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<Boolean, Boolean>> defaultPredicates() {
		return Stream.<BiPredicateBean<Boolean, Boolean>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.is"), Boolean::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.isNot"), (aBoolean, aBoolean2) -> !aBoolean.equals(aBoolean2))
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final BooleanFilter<T> extend(BiPredicateBean<Boolean, Boolean>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
