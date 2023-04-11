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

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;

import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class OnInvalidated<T> extends When<T> {
    //================================================================================
    // Properties
    //================================================================================
    private InvalidationListener listener;
    private Consumer<T> action;
    private BiConsumer<WeakReference<When<T>>, T> otherwise = (w, t) -> {};
    private Function<T, Boolean> condition = t -> true;

    //================================================================================
    // Constructors
    //================================================================================
    public OnInvalidated(ObservableValue<T> observable) {
        super(observable);
    }

    //================================================================================
    // Methods
    //================================================================================

    public OnInvalidated<T> then(Consumer<T> action) {
        this.action = action;
        return this;
    }

    public OnInvalidated<T> otherwise(BiConsumer<WeakReference<When<T>>, T> otherwise) {
        this.otherwise = otherwise;
        return this;
    }

    public OnInvalidated<T> condition(Function<T, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    public OnInvalidated<T> executeNow() {
        action.accept(observable.getValue());
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
            listener = i -> {
                T val = observable.getValue();
                if (condition.apply(val)) {
                    action.accept(val);
                    dispose();
                } else {
                    otherwise.accept(asWeak(), val);
                }
            };
        } else {
            listener = i -> {
                T val = observable.getValue();
                if (condition.apply(val)) {
                    action.accept(val);
                } else {
                    otherwise.accept(asWeak(), val);
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
        if (condition.apply(val)) action.accept(val);
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
