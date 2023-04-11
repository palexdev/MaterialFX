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

package io.github.palexdev.mfxcore.base.properties.synced;

import io.github.palexdev.mfxcore.base.bindings.MFXBindings;
import io.github.palexdev.mfxcore.base.bindings.Source;
import io.github.palexdev.mfxcore.base.properties.base.SynchronizedProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;

/**
 * Implementation of {@link SynchronizedProperty} for double values.
 */
public class SynchronizedDoubleProperty extends ReadOnlyDoubleWrapper implements SynchronizedProperty<Number> {
	//================================================================================
	// Properties
	//================================================================================
	private final ReadOnlyBooleanWrapper waiting = new ReadOnlyBooleanWrapper() {
		@Override
		public void set(boolean newValue) {
			super.set(newValue);
			if (!newValue) SynchronizedDoubleProperty.this.fireValueChangedEvent();
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public SynchronizedDoubleProperty() {}

	public SynchronizedDoubleProperty(double initialValue) {
		super(initialValue);
	}

	public SynchronizedDoubleProperty(Object bean, String name) {
		super(bean, name);
	}

	public SynchronizedDoubleProperty(Object bean, String name, double initialValue) {
		super(bean, name, initialValue);
	}

	//================================================================================
	// Implemented/Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAndWait(Number value, ObservableValue<?> observable) {
		if (!Helper.check(this, value, observable)) return;

		waiting.set(true);
		When.onChanged(observable)
			.then((oldValue, newValue) -> awake())
			.oneShot()
			.listen();
		set(value.doubleValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWaiting() {
		return waiting.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadOnlyBooleanProperty waiting() {
		return waiting.getReadOnlyProperty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void awake() {
		waiting.set(false);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to not fire a change event if {@link #waiting()} is true.
	 */
	@Override
	protected void fireValueChangedEvent() {
		if (isWaiting()) return;
		super.fireValueChangedEvent();
	}

	//================================================================================
	// Binding
	//================================================================================

	/**
	 * Creates a unidirectional bindings with the given observable.
	 * <p>
	 * The binding is created using the new {@link MFXBindings} mechanism.
	 * <p></p>
	 * If the property is already bound it is automatically unbound before bindings to the new observable.
	 *
	 * @throws IllegalArgumentException if the given observable is the property itself
	 * @see MFXBindings
	 */
	@Override
	public void bind(ObservableValue<? extends Number> source) {
		if (this == source) {
			throw new IllegalArgumentException("Cannot bind to itself!");
		}

		if (isBound()) unbind();
		MFXBindings.instance().bind(this).source(source).get();
	}

	/**
	 * Creates a bidirectional bindings between this property and the given property.
	 * <p>
	 * The binding is created using the new {@link MFXBindings} mechanism.
	 * <p></p>
	 * If the property is already bound unidirectionally it is automatically unbound.
	 * <p>
	 * If the property is already bound bidirectionally it won't be automatically unbound, just like JavaFX,
	 * this way you can have multiple bidirectional bindings
	 *
	 * @throws IllegalArgumentException if the given observable is the property itself
	 * @see MFXBindings
	 */
	@Override
	public void bindBidirectional(Property<Number> other) {
		if (this == other) {
			throw new IllegalArgumentException("Cannot bind to itself!");
		}

		if (isBound()) unbind();
		MFXBindings.instance().bindBidirectional(this)
			.addSource(new Source<>(other).implicit(this, other)).get();
	}

	/**
	 * Overridden to call {@link MFXBindings#unbind(ObservableValue)}.
	 */
	@Override
	public void unbind() {
		MFXBindings.instance().unbind(this);
	}

	/**
	 * Overridden to call {@link MFXBindings#unbindBidirectional(ObservableValue, ObservableValue)}.
	 */
	@Override
	public void unbindBidirectional(Property<Number> other) {
		MFXBindings.instance().unbindBidirectional(this, other);
	}

	/**
	 * Delegate method for {@link MFXBindings#disposeBidirectional(ObservableValue)}.
	 */
	public void clearBidirectional() {
		MFXBindings.instance().disposeBidirectional(this);
	}

	/**
	 * Overridden to check the {@link MFXBindings#isBound(ObservableValue)} and {@link MFXBindings#isIgnoreBinding(ObservableValue)}.
	 *
	 * @return true only if `MFXBindings.isBound()` is true and `isIgnoreBound()` is false
	 */
	@Override
	public boolean isBound() {
		return MFXBindings.instance().isBound(this) && !MFXBindings.instance().isIgnoreBinding(this);
	}
}
