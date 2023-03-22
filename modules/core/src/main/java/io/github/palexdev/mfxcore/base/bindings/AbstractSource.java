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

package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.ISource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Base class for all types of {@code Sources}, implements {@link ISource}.
 * <p></p>
 * This class allows to have common properties in one place (such as the source's observable, the target, and listeners),
 * but also implements common methods and defines methods that need to be used only internally.
 *
 * @param <S> the type of the source's observable
 * @param <T> the type of the target's observable
 */
public abstract class AbstractSource<S, T> implements ISource<S, T> {
	//================================================================================
	// Properties
	//================================================================================
	protected ObservableValue<? extends S> observable;
	protected Target<T> target;

	protected ChangeListener<? super S> obvListener;
	protected ChangeListener<? super T> tgtListener;

	//================================================================================
	// Constructors
	//================================================================================
	protected AbstractSource() {
	}

	public AbstractSource(ObservableValue<? extends S> observable) {
		this.observable = observable;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Activates the source by adding the needed listeners.
	 */
	protected abstract void listen();

	/**
	 * Activates the source by adding the needed listeners, unlike {@link #listen()} this
	 * is used by bidirectional biding, in fact a listener is also added to the given target
	 * to trigger the target update when this changes.
	 */
	protected abstract void listen(Target<T> target);

	//================================================================================
	// Methods
	//================================================================================
	@Override
	public ObservableValue<? extends S> getObservable() {
		return observable;
	}

	@Override
	public S getValue() {
		return observable.getValue();
	}
}
