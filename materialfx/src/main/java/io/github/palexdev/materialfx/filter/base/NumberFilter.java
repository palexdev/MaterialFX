package io.github.palexdev.materialfx.filter.base;

import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * Extension of {@link AbstractFilter}, still abstract, limits the U parameter to {@link Number}s.
 */
public abstract class NumberFilter<T, U extends Number> extends AbstractFilter<T, U> {

	//================================================================================
	// Constructors
	//================================================================================
	public NumberFilter(String name, Function<T, U> extractor, StringConverter<U> converter) {
		super(name, extractor, converter);
	}
}
