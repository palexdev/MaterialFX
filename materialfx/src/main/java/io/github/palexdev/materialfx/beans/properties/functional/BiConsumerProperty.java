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

package io.github.palexdev.materialfx.beans.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.BiConsumer;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link BiConsumer}.
 *
 * @param <T> the consumer's first argument
 * @param <U> the consumer's second argument
 */
public class BiConsumerProperty<T, U> extends SimpleObjectProperty<BiConsumer<T, U>> {

	public BiConsumerProperty() {
	}

	public BiConsumerProperty(BiConsumer<T, U> initialValue) {
		super(initialValue);
	}

	public BiConsumerProperty(Object bean, String name) {
		super(bean, name);
	}

	public BiConsumerProperty(Object bean, String name, BiConsumer<T, U> initialValue) {
		super(bean, name, initialValue);
	}
}
