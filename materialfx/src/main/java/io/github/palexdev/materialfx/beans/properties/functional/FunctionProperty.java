package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Function;

// TODO use these properties everywhere
/**
 * Simply an {@link ObjectProperty} that wraps a {@link Function}.
 *
 * @param <T> the function's input type
 * @param <R> the function's return type
 */
public class FunctionProperty<T, R> extends SimpleObjectProperty<Function<T, R>> {

	public FunctionProperty() {
	}

	public FunctionProperty(Function<T, R> initialValue) {
		super(initialValue);
	}

	public FunctionProperty(Object bean, String name) {
		super(bean, name);
	}

	public FunctionProperty(Object bean, String name, Function<T, R> initialValue) {
		super(bean, name, initialValue);
	}
}
