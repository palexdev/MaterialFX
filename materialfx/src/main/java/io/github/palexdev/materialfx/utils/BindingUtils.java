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

package io.github.palexdev.materialfx.utils;

import javafx.beans.binding.*;
import javafx.beans.property.*;

/**
 * Utils class to convert bindings and expressions to properties.
 * <p>
 * Very useful when working with validators since they only accept properties and,
 * in my opinion, it is much easier to create the conditions using the JavaFX {@link Bindings} class.
 */
public class BindingUtils {

	private BindingUtils() {}

	public static <T> ObjectProperty<T> toProperty(ObjectExpression<T> expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		ObjectProperty<T> property = new SimpleObjectProperty<>();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link IntegerProperty} and binds it to the given bindings/expression.
	 */
	public static IntegerProperty toProperty(IntegerExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		IntegerProperty property = new SimpleIntegerProperty();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link LongProperty} and binds it to the given bindings/expression.
	 */
	public static LongProperty toProperty(LongExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		LongProperty property = new SimpleLongProperty();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link FloatProperty} and binds it to the given bindings/expression.
	 */
	public static FloatProperty toProperty(FloatExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		FloatProperty property = new SimpleFloatProperty();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link DoubleProperty} and binds it to the given bindings/expression.
	 */
	public static DoubleProperty toProperty(DoubleExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		DoubleProperty property = new SimpleDoubleProperty();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link BooleanProperty} and binds it to the given bindings/expression.
	 */
	public static BooleanProperty toProperty(BooleanExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		BooleanProperty property = new SimpleBooleanProperty();
		property.bind(expression);
		return property;
	}

	/**
	 * Creates a new {@link StringProperty} and binds it to the given bindings/expression.
	 */
	public static StringProperty toProperty(StringExpression expression) {
		if (expression == null) {
			throw new IllegalArgumentException("The argument cannot be null!");
		}
		StringProperty property = new SimpleStringProperty();
		property.bind(expression);
		return property;
	}
}
