package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Supplier;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Supplier}.
 *
 * @param <T> the supplier's return type
 */
public class SupplierProperty<T> extends SimpleObjectProperty<Supplier<T>> {

	public SupplierProperty() {
	}

	public SupplierProperty(Supplier<T> initialValue) {
		super(initialValue);
	}

	public SupplierProperty(Object bean, String name) {
		super(bean, name);
	}

	public SupplierProperty(Object bean, String name, Supplier<T> initialValue) {
		super(bean, name, initialValue);
	}
}
