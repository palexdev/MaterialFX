package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
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
				new BiPredicateBean<>("is", Boolean::equals),
				new BiPredicateBean<>("is not", (aBoolean, aBoolean2) -> !aBoolean.equals(aBoolean2))
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final BooleanFilter<T> extend(BiPredicateBean<Boolean, Boolean>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
