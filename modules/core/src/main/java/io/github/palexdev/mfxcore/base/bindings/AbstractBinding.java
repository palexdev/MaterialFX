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

import io.github.palexdev.mfxcore.base.bindings.base.IBinding;
import io.github.palexdev.mfxcore.enums.BindingState;
import javafx.beans.value.ObservableValue;

import java.util.Optional;

/**
 * Base class for all types of bindings, implements {@link IBinding}.
 * <p>
 * This abstract class allows to have common properties in one place (such as the {@code Target} and the state) but
 * adds new features as well.
 * <p></p>
 * This defines a map of particular {@code Sources}, {@link ExternalSource}, which can be used to perform any operation
 * when they change. However, they are intended to invalidate the binding when needed, in special occasions.
 * <p></p>
 * This also defines a series of {@link Runnable}s that allow to run operations before and after the aforementioned
 * invalidations.
 *
 * @param <T> the type of the target's observable
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractBinding<T> implements IBinding<T> {
	//================================================================================
	// Properties
	//================================================================================
	protected Target<T> target;
	protected BindingState state = BindingState.NULL;
	protected final WeakLinkedHashMap<ObservableValue, ExternalSource> invalidatingSources = new WeakLinkedHashMap<>();

	protected Runnable beforeTargetInvalidation = () -> {
	};
	protected Runnable beforeSourceInvalidation = () -> {
	};
	protected Runnable afterTargetInvalidation = () -> {
	};
	protected Runnable afterSourceInvalidation = () -> {
	};

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Adds and activates the given {@link ExternalSource} to this binding.
	 */
	public abstract <S> AbstractBinding<T> addInvalidatingSource(ExternalSource<S> source);

	//================================================================================
	// Methods
	//================================================================================
	@Override
	public Target<T> getTarget() {
		return target;
	}

	@Override
	public BindingState state() {
		return state;
	}

	/**
	 * Shortcut for:
	 * <pre>
	 * {@code
	 * addInvalidatingSource(new ExternalSource<>(source)
	 *          .setAction((o, n) -> invalidate());
	 * }
	 * </pre>
	 */
	public <S> AbstractBinding<T> addTargetInvalidatingSource(ObservableValue<? extends S> source) {
		return addInvalidatingSource(new ExternalSource<>(source).setAction((o, n) -> invalidate()));
	}

	/**
	 * Shortcut for:
	 * <pre>
	 * {@code
	 * addInvalidatingSource(new ExternalSource<>(source)
	 *          .setAction((o, n) -> invalidateSource());
	 * }
	 * </pre>
	 */
	public <S> AbstractBinding<T> addSourcesInvalidatingSource(ObservableValue<? extends S> source) {
		return addInvalidatingSource(new ExternalSource<>(source).setAction((o, n) -> invalidateSource()));
	}

	/**
	 * Removes and disposes the given {@link ExternalSource} from this binding.
	 */
	public <S> AbstractBinding<T> removeInvalidatingSource(ObservableValue<? extends S> source) {
		Optional.ofNullable(invalidatingSources.remove(source))
				.ifPresent(ExternalSource::dispose);
		return this;
	}

	/**
	 * Calls {@link #removeInvalidatingSource(ObservableValue)} with {@link ExternalSource#getObservable()}.
	 */
	public <S> AbstractBinding<T> removeInvalidatingSource(ExternalSource<S> source) {
		return removeInvalidatingSource(source.getObservable());
	}

	/**
	 * Disposes and removes all the {@link ExternalSource}s from this binding.
	 */
	public AbstractBinding<T> clearInvalidatingSources() {
		invalidatingSources.values().forEach(ExternalSource::dispose);
		invalidatingSources.clear();
		return this;
	}

	/**
	 * @return the action performed before invalidating the target
	 */
	public Runnable getBeforeTargetInvalidation() {
		return beforeTargetInvalidation;
	}

	/**
	 * Sets the action to perform before invalidating the target.
	 */
	public AbstractBinding<T> setBeforeTargetInvalidation(Runnable beforeTargetInvalidation) {
		this.beforeTargetInvalidation = beforeTargetInvalidation;
		return this;
	}

	/**
	 * @return the action performed before invalidating the source
	 */
	public Runnable getBeforeSourceInvalidation() {
		return beforeSourceInvalidation;
	}

	/**
	 * Sets the action to perform before invalidating the source.
	 */
	public AbstractBinding<T> setBeforeSourceInvalidation(Runnable beforeSourceInvalidation) {
		this.beforeSourceInvalidation = beforeSourceInvalidation;
		return this;
	}

	/**
	 * @return the action performed after invalidating the target
	 */
	public Runnable getAfterTargetInvalidation() {
		return afterTargetInvalidation;
	}

	/**
	 * Sets the action to perform after invalidating the target.
	 */
	public AbstractBinding<T> setAfterTargetInvalidation(Runnable afterTargetInvalidation) {
		this.afterTargetInvalidation = afterTargetInvalidation;
		return this;
	}

	/**
	 * @return the action performed after invalidating the source
	 */
	public Runnable getAfterSourceInvalidation() {
		return afterSourceInvalidation;
	}

	/**
	 * Sets the action to perform after invalidating the source.
	 */
	public AbstractBinding<T> setAfterSourceInvalidation(Runnable afterSourceInvalidation) {
		this.afterSourceInvalidation = afterSourceInvalidation;
		return this;
	}
}
