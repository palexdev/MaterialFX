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
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link AbstractFilter} for String fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "contains": checks if a String is contained in another String
 * <p> - "contains ignore case": checks if a String is contained in another String, case insensitive
 * <p> - "contains any": checks if any of the given words are contained in a String. Words are specified as a
 * single String split by ", ". Like this: "A, B, C, DEF GHI, E, F"
 * <p> - "contains all": checks if all the given words are contained in a String. Words are specified as a
 * single String split by ", ". Like this: "A, B, C, DEF GHI, E, F"
 * <p> - "ends with": checks if a String ends with another String
 * <p> - "ends with ignore case": checks if a String ends with another String, case insensitive
 * <p> - "starts with": checks if a String starts with another String
 * <p> - "starts with ignore case": checks if a String starts with another String, case insensitive
 * <p> - "equals": checks for Strings equality
 * <p> - "equals ignore case": checks for Strings equality, case insensitive
 * <p> - "is not equal to": checks for Strings inequality
 */
public class StringFilter<T> extends AbstractFilter<T, String> {

	//================================================================================
	// Constructors
	//================================================================================
	public StringFilter(String name, Function<T, String> extractor) {
		this(name, extractor, new StringConverter<>() {
			@Override
			public String toString(String object) {
				return object;
			}

			@Override
			public String fromString(String string) {
				return string;
			}
		});
	}

	public StringFilter(String name, Function<T, String> extractor, StringConverter<String> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<String, String>> defaultPredicates() {
		return Stream.<BiPredicateBean<String, String>>of(
				new BiPredicateBean<>(I18N.getOrDefault("filter.contains"), String::contains),
				new BiPredicateBean<>(I18N.getOrDefault("filter.containsIgnCase"), StringUtils::containsIgnoreCase),
				new BiPredicateBean<>(I18N.getOrDefault("filter.containsAny"), StringUtils::containsAny),
				new BiPredicateBean<>(I18N.getOrDefault("filter.containsAll"), StringUtils::containsAll),
				new BiPredicateBean<>(I18N.getOrDefault("filter.endsWith"), String::endsWith),
				new BiPredicateBean<>(I18N.getOrDefault("filter.endsWithIgnCase"), StringUtils::endsWithIgnoreCase),
				new BiPredicateBean<>(I18N.getOrDefault("filter.equals"), String::equals),
				new BiPredicateBean<>(I18N.getOrDefault("filter.equalsIgnCase"), String::equalsIgnoreCase),
				new BiPredicateBean<>(I18N.getOrDefault("filter.notEqual"), (aString, aString2) -> !aString.equals(aString2)),
				new BiPredicateBean<>(I18N.getOrDefault("filter.startsWith"), String::startsWith),
				new BiPredicateBean<>(I18N.getOrDefault("filter.startsWithIgnCase"), StringUtils::startsWithIgnoreCase)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final StringFilter<T> extend(BiPredicateBean<String, String>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
