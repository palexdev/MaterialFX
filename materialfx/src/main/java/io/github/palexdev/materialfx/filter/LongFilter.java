package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.NumberFilter;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link NumberFilter} for long fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for longs equality
 * <p> - "is not": checks for longs inequality
 * <p> - "greater than": checks if a long is greater than another long
 * <p> - "greater or equal to": checks if a long is greater or equal to another long
 * <p> - "lesser than": checks if a long is lesser than another long
 * <p> - "lesser or equal to": checks if a long is lesser or equal to another long
 */
public class LongFilter<T> extends NumberFilter<T, Long> {

	//================================================================================
	// Constructors
	//================================================================================
	public LongFilter(String name, Function<T, Long> extractor) {
		this(name, extractor, new LongStringConverter());
	}

	public LongFilter(String name, Function<T, Long> extractor, StringConverter<Long> converter) {
		super(name, extractor, converter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<Long, Long>> defaultPredicates() {
		return Stream.<BiPredicateBean<Long, Long>>of(
				new BiPredicateBean<>("is", Long::equals),
				new BiPredicateBean<>("is not", (aLong, aLong2) -> !aLong.equals(aLong2)),
				new BiPredicateBean<>("greater than", (aLong, aLong2) -> aLong > aLong2),
				new BiPredicateBean<>("greater or equal to", (aLong, aLong2) -> aLong >= aLong2),
				new BiPredicateBean<>("lesser than", (aLong, aLong2) -> aLong < aLong2),
				new BiPredicateBean<>("lesser or equal to", (aLong, aLong2) -> aLong <= aLong2)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final LongFilter<T> extend(BiPredicateBean<Long, Long>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
