/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.observables;

import io.github.palexdev.mfxcore.base.TriConsumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class OnChanged<T> extends When<T> {
    //================================================================================
    // Properties
    //================================================================================
    private ChangeListener<T> listener;
    private BiConsumer<T, T> action;
    private TriConsumer<WeakReference<When<T>>, T, T> otherwise = (w, o, n) -> {};
    private BiFunction<T, T, Boolean> condition = (o, n) -> true;

    //================================================================================
    // Constructors
    //================================================================================
    public OnChanged(ObservableValue<T> observable) {
        super(observable);
    }

    //================================================================================
    // Methods
    //================================================================================

    public OnChanged<T> then(BiConsumer<T, T> action) {
        this.action = action;
        return this;
    }

    public OnChanged<T> otherwise(TriConsumer<WeakReference<When<T>>, T, T> otherwise) {
        this.otherwise = otherwise;
        return this;
    }

    public OnChanged<T> condition(BiFunction<T, T, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    public OnChanged<T> executeNow() {
        action.accept(null, observable.getValue());
        if (oneShot && execNowOneShot) dispose();
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public When<T> listen() {
        // This may happen if executeNow() was executed and this was set to be oneShot
        // for the executeNow methods too
        if (isDisposed()) return this;

        if (oneShot) {
            listener = (ov, o, n) -> {
                if (condition.apply(o, n)) {
                    action.accept(o, n);
                    dispose();
                } else {
                    otherwise.accept(asWeak(), o, n);
                }
            };
        } else {
            listener = (ob, o, n) -> {
                if (condition.apply(o, n)) {
                    action.accept(o, n);
                } else {
                    otherwise.accept(asWeak(), o, n);
                }
            };
        }

        register();
        observable.addListener(listener);
        return this;
    }

    @Override
    protected When<T> invalidate() {
        T val = observable.getValue();
        if (condition.apply(null, val)) action.accept(null, val);
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (observable != null) {
            if (listener != null) {
                observable.removeListener(listener);
                listener = null;
            }
            handleMapDisposal();
            observable = null;
        }
    }
}
