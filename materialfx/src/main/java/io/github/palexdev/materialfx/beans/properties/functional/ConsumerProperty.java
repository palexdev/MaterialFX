package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Consumer;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Consumer}.
 *
 * @param <T> the consumer's input type
 */
public class ConsumerProperty<T> extends SimpleObjectProperty<Consumer<T>> {

	public ConsumerProperty() {
	}

	public ConsumerProperty(Consumer<T> initialValue) {
		super(initialValue);
	}

	public ConsumerProperty(Object bean, String name) {
		super(bean, name);
	}

	public ConsumerProperty(Object bean, String name, Consumer<T> initialValue) {
		super(bean, name, initialValue);
	}
}
