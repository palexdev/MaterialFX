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

import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.value.ObservableValue;

import java.util.Objects;

/**
 * Special type of {@code Source} whose function should be to invalidate a binding when this changes.
 * <p>
 * It's called {@code ExternalSource} because technically it has nothing to do with a binding, and the action performed
 * can be anything. However, they are part of a {@link AbstractBinding} as they should perform operations that can
 * influence the binding.
 * <p>
 * To be precise, this source allows you to define any action, but the intended way to use this is to
 * trigger bindings invalidation (either of target or sources).
 *
 * @param <S> the type of the source's observable
 */
public class ExternalSource<S> extends AbstractSource<S, S> {
	//================================================================================
	// Properties
	//================================================================================
	private Updater<S> action = (oldValue, newValue) -> {
	};

	//================================================================================
	// Constructors
	//================================================================================
	protected ExternalSource() {
	}

	public ExternalSource(ObservableValue<? extends S> observable) {
		super(observable);
	}

	public ExternalSource(ObservableValue<? extends S> observable, Updater<S> action) {
		super(observable);
		this.action = action;
	}

	public static <S> ExternalSource<S> of(ObservableValue<? extends S> observable) {
		return new ExternalSource<>(observable);
	}

	public static <S> ExternalSource<S> of(ObservableValue<? extends S> observable, Updater<S> action) {
		return new ExternalSource<>(observable, action);
	}

	//================================================================================
	// Methods
	//================================================================================
	// TODO check this documentation

	/**
	 * Activates this invalidating source by adding a listener to it that will trigger the specified {@link #getAction()}.
	 */
	@Override
	protected void listen() {
		When.onChanged(observable)
				.then(action::update)
				.listen();
	}

	/**
	 * Unsupported.
	 *
	 * @throws UnsupportedOperationException {@code InvalidatingSources} do not operate on a target
	 */
	@Override
	protected void listen(Target<S> target) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.
	 *
	 * @throws UnsupportedOperationException {@code InvalidatingSources} are not directly responsible for updating
	 *                                       a target
	 */
	@Override
	public void updateTarget(S oldValue, S newValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.
	 *
	 * @throws UnsupportedOperationException {@code InvalidatingSources} are not directly responsible for
	 *                                       updating sources
	 */
	@Override
	public void updateSource(S oldValue, S newValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Disposes the source by using {@link When#disposeFor(ObservableValue)},
	 * then sets the observable to null.
	 */
	@Override
	public void dispose() {
		When.disposeFor(observable);
		observable = null;
	}

	/**
	 * @return the action performed by this source when it changes
	 */
	public Updater<S> getAction() {
		return action;
	}

	/**
	 * Sets the action performed by this source whe int changes.
	 */
	public ExternalSource<S> setAction(Updater<S> action) {
		this.action = action;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExternalSource<?> that = (ExternalSource<?>) o;
		return observable.equals(that.observable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(observable);
	}
}
