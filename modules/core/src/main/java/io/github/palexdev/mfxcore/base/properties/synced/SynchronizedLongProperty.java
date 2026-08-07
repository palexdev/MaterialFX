/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.base.properties.base.SynchronizedProperty;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.value.ObservableValue;

/// Implementation of [SynchronizedProperty] for long values.
public class SynchronizedLongProperty extends ReadOnlyLongWrapper implements SynchronizedProperty<Number> {
    //================================================================================
    // Properties
    //================================================================================
    private final ReadOnlyBooleanWrapper waiting = new ReadOnlyBooleanWrapper() {
        @Override
        public void set(boolean newValue) {
            super.set(newValue);
            if (!newValue) SynchronizedLongProperty.this.fireValueChangedEvent();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public SynchronizedLongProperty() {}

    public SynchronizedLongProperty(long initialValue) {
        super(initialValue);
    }

    public SynchronizedLongProperty(Object bean, String name) {
        super(bean, name);
    }

    public SynchronizedLongProperty(Object bean, String name, long initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Implemented/Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    @Override
    public void setAndWait(Number value, ObservableValue<?> observable) {
        if (!Helper.check(this, value, observable)) return;

        waiting.set(true);
        When.onChanged(observable)
            .then((oldValue, newValue) -> awake())
            .oneShot()
            .listen();
        set(value.longValue());
    }

    /// {@inheritDoc}
    @Override
    public boolean isWaiting() {
        return waiting.get();
    }

    /// {@inheritDoc}
    @Override
    public ReadOnlyBooleanProperty waiting() {
        return waiting.getReadOnlyProperty();
    }

    /// {@inheritDoc}
    @Override
    public void awake() {
        waiting.set(false);
    }

    /// {@inheritDoc}
    ///
    /// Overridden to not fire a change event if [#waiting()] is true.
    @Override
    protected void fireValueChangedEvent() {
        if (isWaiting()) return;
        super.fireValueChangedEvent();
    }
}
