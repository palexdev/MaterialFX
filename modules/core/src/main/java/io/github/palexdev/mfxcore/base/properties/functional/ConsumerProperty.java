/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.properties.functional;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.function.Consumer;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link Consumer}.
 *
 * @param <T> the consumer's input type
 */
public class ConsumerProperty<T> extends ReadOnlyObjectWrapper<Consumer<T>> {

	//================================================================================
	// Constructors
	//================================================================================
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
