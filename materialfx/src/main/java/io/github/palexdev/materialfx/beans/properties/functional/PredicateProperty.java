package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Predicate;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Predicate}.
 *
 * @param <T> the predicate's input type
 */
public class PredicateProperty<T> extends SimpleObjectProperty<Predicate<T>> {

	public PredicateProperty() {
	}

	public PredicateProperty(Predicate<T> initialValue) {
		super(initialValue);
	}

	public PredicateProperty(Object bean, String name) {
		super(bean, name);
	}

	public PredicateProperty(Object bean, String name, Predicate<T> initialValue) {
		super(bean, name, initialValue);
	}
}
