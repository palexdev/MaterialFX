/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.properties.functional;

import io.github.palexdev.mfxcore.base.TriConsumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Simply an {@link ObjectProperty} that wraps a {@link TriConsumer}.
 *
 * @param <T> the consumer's first argument
 * @param <U> the consumer's second argument
 * @param <R> the consumer's third argument
 */
public class TriConsumerProperty<T, U, R> extends ReadOnlyObjectWrapper<TriConsumer<T, U, R>> {

	//================================================================================
	// Constructors
	//================================================================================
	public TriConsumerProperty() {
	}

	public TriConsumerProperty(TriConsumer<T, U, R> initialValue) {
		super(initialValue);
	}

	public TriConsumerProperty(Object bean, String name) {
		super(bean, name);
	}

	public TriConsumerProperty(Object bean, String name, TriConsumer<T, U, R> initialValue) {
		super(bean, name, initialValue);
	}
}
