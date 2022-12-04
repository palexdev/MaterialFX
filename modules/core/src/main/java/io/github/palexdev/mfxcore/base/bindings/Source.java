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
import io.github.palexdev.mfxcore.enums.BindingType;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.value.ObservableValue;

/**
 * Concrete implementation of {@link AbstractSource} the most basic type of source.
 *
 * @param <S> both the source's and target's observables type
 */
public class Source<S> extends AbstractSource<S, S> {
	//================================================================================
	// Properties
	//================================================================================
	protected Updater<S> targetUpdater;
	protected Updater<S> sourceUpdater;

	//================================================================================
	// Constructors
	//================================================================================
	protected Source() {
	}

	public Source(ObservableValue<? extends S> observable) {
		super(observable);
	}

	public static <S> Source<S> of(ObservableValue<? extends S> observable) {
		return new Source<>(observable);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operates differently depending on which binding activated this.
	 * <p>
	 * For unidirectional bindings the target instance is null so the {@link #getTargetUpdater()} is called
	 * then exits.
	 * <p>
	 * For bidirectional bindings the target instance is not null. Before calling the {@link #getTargetUpdater()}
	 * we first must check that the update is not has not been invoked because of a "bounce" effect, {@link Target#isFromSource()}.
	 */
	@Override
	public void updateTarget(S oldValue, S newValue) {
		if (target.bindingType() == BindingType.UNIDIRECTIONAL) {
			try {
				target.ignoreBinding = true;
				targetUpdater.update(oldValue, newValue);
			} finally {
				target.ignoreBinding = false;
			}
			return;
		}

		if (target.isFromSource()) return;
		targetUpdater.update(oldValue, newValue);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operates differently depending on which binding activated this.
	 * <p>
	 * For unidirectional bindings the target instance is null so the {@link #getTargetUpdater()} is called
	 * then exits.
	 * <p>
	 * For bidirectional bindings the target instance is not null. The call to {@link #getSourceUpdater()}
	 * is surrounded by a try-finally block in which we also set {@link Target#isFromSource()} to true, if anything goes wrong
	 * the finally block ensures to reset {@link Target#isFromSource()} back to false.
	 */
	@Override
	public void updateSource(S oldValue, S newValue) {
		if (target == null) {
			sourceUpdater.update(oldValue, newValue);
			return;
		}

		try {
			target.fromSource = true;
			sourceUpdater.update(oldValue, newValue);
		} finally {
			target.fromSource = false;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For unidirectional bindings. The listener to this source's observable is added by using
	 * {@link When#onChanged(ObservableValue)} and is responsible for triggering {@link #updateTarget(Object, Object)}.
	 */
	@Override
	protected void listen() {
		When.onChanged(observable)
				.then(this::updateTarget)
				.listen();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For bidirectional bindings. The source's target is set to the given one.
	 * Then {@link #listen()} is called. Then a listener to the given target is added by using
	 * {@link When#onChanged(ObservableValue)} and is responsible for triggering {@link #updateSource(Object, Object)}.
	 */
	@Override
	protected void listen(Target<S> target) {
		listen();

		this.target = target;
		When.onChanged(target.getObservable())
				.then(this::updateSource)
				.listen();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Uses {@link When#disposeFor(ObservableValue)} to remove the listeners added to this source's observable
	 * and the target's observable. Then sets all its properties to null.
	 */
	@Override
	public void dispose() {
		When.disposeFor(observable);
		if (target != null) When.disposeFor(target.getObservable());
		observable = null;
		target = null;
		targetUpdater = null;
		sourceUpdater = null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the {@link Updater} responsible for updating the target
	 */
	public Updater<S> getTargetUpdater() {
		return targetUpdater;
	}

	/**
	 * Sets the target {@link Updater}.
	 */
	public Source<S> setTargetUpdater(Updater<S> targetUpdater) {
		this.targetUpdater = targetUpdater;
		return this;
	}

	/**
	 * @return the {@link Updater} responsible for updating the source
	 */
	public Updater<S> getSourceUpdater() {
		return sourceUpdater;
	}

	/**
	 * Sets the source {@link Updater}.
	 */
	public Source<S> setSourceUpdater(Updater<S> sourceUpdater) {
		this.sourceUpdater = sourceUpdater;
		return this;
	}

	/**
	 * Attempts set the target updater by using {@link Updater#implicit(ObservableValue)}
	 * on the given target.
	 */
	public Source<S> implicit(ObservableValue<? extends S> target) {
		targetUpdater = Updater.implicit(target);
		return this;
	}

	/**
	 * Attempts to set the target and sources updater by using {@link Updater#implicit(ObservableValue)}
	 * on both the given target and source.
	 */
	public Source<S> implicit(ObservableValue<? extends S> target, ObservableValue<? extends S> source) {
		targetUpdater = Updater.implicit(target);
		sourceUpdater = Updater.implicit(source);
		return this;
	}

	//================================================================================
	// Builder
	//================================================================================
	public static class Builder<S> {
		private final Source<S> source = new Source<>();

		public Builder<S> observable(ObservableValue<? extends S> observable) {
			source.observable = observable;
			return this;
		}

		public Builder<S> targetUpdater(ObservableValue<? extends S> target) {
			source.targetUpdater = Updater.implicit(target);
			return this;
		}

		public Builder<S> targetUpdater(Updater<S> targetUpdater) {
			source.targetUpdater = targetUpdater;
			return this;
		}

		public Builder<S> sourceUpdater(Updater<S> sourceUpdater) {
			source.sourceUpdater = sourceUpdater;
			return this;
		}

		public Source<S> get() {
			if (source.observable == null) throw new NullPointerException("Source is invalid as observable is null");
			return source;
		}
	}
}
