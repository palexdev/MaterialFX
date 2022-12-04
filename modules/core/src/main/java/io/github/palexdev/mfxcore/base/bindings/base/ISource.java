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

package io.github.palexdev.mfxcore.base.bindings.base;

import io.github.palexdev.mfxcore.base.bindings.MappingSource;
import javafx.beans.value.ObservableValue;

/**
 * Public API every type of {@code Source} must implement.
 * <p>
 * The interface has two generics as it makes it easier to implement {@link MappingSource}s.
 *
 * @param <S> the type of the source's observable
 * @param <T> the type of the target's observable
 */
public interface ISource<S, T> {

	/**
	 * @return the source's {@link ObservableValue}
	 */
	ObservableValue<? extends S> getObservable();

	/**
	 * Uses the values of the source's observable to update the target.
	 */
	void updateTarget(S oldValue, S newValue);

	/**
	 * Uses the values of the target's observable to update the sources.
	 */
	void updateSource(T oldValue, T newValue);

	/**
	 * Disposes the source.
	 */
	void dispose();

	/**
	 * Shortcut for {@code getObservable().getValue()}.
	 */
	default S getValue() {
		return getObservable().getValue();
	}
}
