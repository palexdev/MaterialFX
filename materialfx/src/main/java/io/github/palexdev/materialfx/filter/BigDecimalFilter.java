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

import java.math.BigDecimal;
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
 * <p></p>
 * Example:
 * <pre>
 * {@code
 *     MFXTableView table = ...;
 *     table.getFilters.addAll(
 *         ...
 *         new BigDecimalFilter<>("A big filter", ...),
 *         ...
 *     );
 * }
 * </pre>
 */
public class BigDecimalFilter<T> extends NumberFilter<T, BigDecimal> {

	//================================================================================
	// Constructors
	//================================================================================
	public BigDecimalFilter(String name, Function<T, BigDecimal> extractor) {
		super(name, extractor, new StringConverter<>() {
			@Override
			public String toString(BigDecimal d) {
				return d.toPlainString();
			}

			@Override
			public BigDecimal fromString(String s) {
				return new BigDecimal(s);
			}
		});
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<BigDecimal, BigDecimal>> defaultPredicates() {
		return Stream.<BiPredicateBean<BigDecimal, BigDecimal>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.is"), BigDecimal::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.isNot"), (aFloat, aFloat2) -> !aFloat.equals(aFloat2)),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greater"), (aFloat, aFloat2) -> aFloat.compareTo(aFloat2) > 0),
				new BiPredicateBean<>(I18N.getOrDefault("filter.greaterEqual"), (aFloat, aFloat2) -> aFloat.compareTo(aFloat2) >= 0),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesser"), (aFloat, aFloat2) -> aFloat.compareTo(aFloat2) < 0),
				new BiPredicateBean<>(I18N.getOrDefault("filter.lesserEqual"), (aFloat, aFloat2) -> aFloat.compareTo(aFloat2) <= 0)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final BigDecimalFilter<T> extend(BiPredicateBean<BigDecimal, BigDecimal>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}