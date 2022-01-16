package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.utils.EnumStringConverter;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension of {@link AbstractFilter} for {@link Enum} fields.
 * <p></p>
 * Offers the following default {@link BiPredicateBean}s:
 * <p> - "is": checks for enums equality
 * <p> - "is not": checks for enums inequality
 * <p></p>
 * This filter is special because to extract the enumerations of a given E enum, it's
 * needed to also pass the type to the constructor. This is necessary for the {@link EnumStringConverter}.
 */
public class EnumFilter<T, E extends Enum<E>> extends AbstractFilter<T, E> {
	//================================================================================
	// Properties
	//================================================================================
	private final Class<E> enumType;

	//================================================================================
	// Constructors
	//================================================================================
	public EnumFilter(String name, Function<T, E> extractor, Class<E> enumType) {
		this(name, extractor, enumType, new EnumStringConverter<>(enumType));
	}

	public EnumFilter(String name, Function<T, E> extractor, Class<E> enumType, StringConverter<E> converter) {
		super(name, extractor, converter);
		this.enumType = enumType;
	}

	//================================================================================
	// Getters
	//================================================================================
	public Class<E> getEnumType() {
		return enumType;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected ObservableList<BiPredicateBean<E, E>> defaultPredicates() {
		return Stream.<BiPredicateBean<E, E>>of(
				new BiPredicateBean<>("is", Enum::equals),
				new BiPredicateBean<>("is not", (anEnum, anEnum2) -> !anEnum.equals(anEnum2))
		).collect(FXCollectors.toList());
	}

	@SafeVarargs
	@Override
	protected final EnumFilter<T, E> extend(BiPredicateBean<E, E>... predicateBeans) {
		Collections.addAll(super.predicates, predicateBeans);
		return this;
	}
}
