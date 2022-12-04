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
import io.github.palexdev.mfxcore.base.bindings.base.ISource;
import io.github.palexdev.mfxcore.enums.BindingState;
import io.github.palexdev.mfxcore.enums.BindingType;
import javafx.beans.value.ObservableValue;

import java.util.Optional;

/**
 * Concrete implementation of {@link AbstractBinding} to define bidirectional bindings.
 * <p></p>
 * Bidirectional bindings can have multiple sources and multiple other sources, {@link ExternalSource},
 * which should be used to invalidate the binding (either of target or sources) when needed, in special occasions.
 * <p></p>
 * Note that sources can use whatever source type you want as long as the source can correctly:
 * <p> - produce values compatibles with the target when updating the target
 * <p> - produce values compatibles with the source when the target changed
 *
 * @param <T> the binding's target type
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BidirectionalBinding<T> extends AbstractBinding<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final WeakLinkedHashMap<ObservableValue, AbstractSource> sources = new WeakLinkedHashMap<>();

	//================================================================================
	// Constructors
	//================================================================================
	public BidirectionalBinding() {
	}

	public BidirectionalBinding(ObservableValue<? extends T> target) {
		super.target = new Target<>(target);
	}

	public static <T> BidirectionalBinding<T> create() {
		return new BidirectionalBinding<>();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets this binding's target.
	 *
	 * @throws IllegalStateException if the binding' state is {@link BindingState#BOUND}
	 */
	public BidirectionalBinding<T> target(ObservableValue<? extends T> observable) {
		if (mayBeBound()) throw new IllegalStateException("Cannot set target as this binding is currently active");
		super.target = new Target<>(observable);
		return this;
	}

	/**
	 * Adds all the given sources to this binding. If a given source was already present it is not
	 * overwritten.
	 */
	@SafeVarargs
	public final BidirectionalBinding<T> addSources(AbstractSource<?, T>... sources) {
		for (AbstractSource<?, T> source : sources) {
			addSource(source);
		}
		return this;
	}

	/**
	 * Adds the given source to this binding. If the given source was already present it is not
	 * overwritten.
	 */
	public <S> BidirectionalBinding<T> addSource(AbstractSource<S, T> source) {
		if (sources.containsKey(source.getObservable())) return this;
		sources.put(source.getObservable(), source);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Before activating the binding checks if there are already unidirectional or bidirectional bindings registered
	 * for the given target and eventually disposes them.
	 * <p>
	 * Then activates the all the sources with {@link AbstractSource#listen(Target)}, registers the binding in {@link MFXBindings}
	 * and sets the state to {@link BindingState#BOUND}.
	 *
	 * @throws IllegalStateException if the binding has been disposed before OR the target is null
	 *                               OR there are no sources
	 */
	@Override
	public BidirectionalBinding<T> get() {
		if (isDisposed())
			throw new IllegalStateException("This binding has been previously disposed and cannot be used anymore");
		if (target == null) throw new IllegalStateException("Cannot bind as target is null");
		if (sources.isEmpty()) throw new IllegalStateException("No sources specifies for this binding");
		MFXBindings bindings = MFXBindings.instance();

		if (bindings.isBound(target)) {
			bindings.getBinding(target).dispose();
		}

		if (bindings.isBoundBidirectional(target)) {
			bindings.getBiBinding(target).dispose();
		}

		target.bindingType = BindingType.BIDIRECTIONAL;
		sources.values().forEach(s -> s.listen(target));
		bindings.addBinding(this);
		state = BindingState.BOUND;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * For bidirectional bindings the target is updated with the value from the last added
	 * source. This is possible thanks to {@link WeakLinkedHashMap}, {@link WeakLinkedHashMap#getLastKey()}.
	 * <p></p>
	 * Also runs {@link #getBeforeTargetInvalidation()} and {@link #getAfterTargetInvalidation()}.
	 */
	@Override
	public IBinding<T> invalidate() {
		Optional.ofNullable(sources.getLastKey())
				.map(sources::get)
				.ifPresent(s -> {
					beforeTargetInvalidation.run();
					s.updateTarget(s.getValue(), s.getValue());
					afterTargetInvalidation.run();
				});
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Also runs {@link #getBeforeSourceInvalidation()} and {@link #getAfterSourceInvalidation()}.
	 */
	@Override
	public IBinding<T> invalidateSource() {
		beforeSourceInvalidation.run();
		sources.values().forEach(s -> s.updateSource(target.getValue(), target.getValue()));
		afterSourceInvalidation.run();
		return this;
	}

	public <S> BidirectionalBinding<T> addInvalidatingSource(ExternalSource<S> source) {
		source.listen();
		invalidatingSources.put(source.getObservable(), source);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Disposes all the sources and removes them, calls {@link #clearInvalidatingSources()},
	 * sets the state to {@link BindingState#UNBOUND} then un-registers the binding from {@link MFXBindings}.
	 */
	@Override
	public BidirectionalBinding<T> unbind() {
		sources.values().forEach(ISource::dispose);
		sources.clear();
		clearInvalidatingSources();
		state = BindingState.UNBOUND;
		MFXBindings.instance().removeBinding(this);
		return this;
	}

	/**
	 * Disposes only the given source if present.
	 * <p></p>
	 * If that was the last source then the state is set to {@link BindingState#UNBOUND} and
	 * the binding is un-registered from {@link MFXBindings}. Note that unlike {@link #unbind}
	 * this won't dispose and remove the invalidating sources!
	 */
	public <S> BidirectionalBinding<T> unbind(ObservableValue<? extends S> source) {
		AbstractSource s = sources.remove(source);
		if (s != null) {
			s.dispose();
		}
		if (sources.isEmpty()) {
			// Note that invalidating sources will be kept
			state = BindingState.UNBOUND;
			MFXBindings.instance().removeBinding(this);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Calls {@link #unbind()}.
	 * <p>
	 * Then sets the remaining properties to null and the state to {@link BindingState#DISPOSED}.
	 */
	@Override
	public void dispose() {
		unbind();
		target.dispose();
		target = null;
		state = BindingState.DISPOSED;
	}
}
