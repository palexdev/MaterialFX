package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.NumberFilter;
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
				new BiPredicateBean<>("is", Integer::equals),
				new BiPredicateBean<>("is not", (anInteger, anInteger2) -> !anInteger.equals(anInteger2)),
				new BiPredicateBean<>("greater than", (anInteger, anInteger2) -> anInteger > anInteger2),
				new BiPredicateBean<>("greater or equal to", (anInteger, anInteger2) -> anInteger >= anInteger2),
				new BiPredicateBean<>("lesser than", (anInteger, anInteger2) -> anInteger < anInteger2),
				new BiPredicateBean<>("lesser or equal to", (anInteger, anInteger2) -> anInteger <= anInteger2)
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final IntegerFilter<T> extend(BiPredicateBean<Integer, Integer>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
