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
    protected ChangeListener<T> listener;
    private BiConsumer<T, T> action;
    private BiFunction<T, T, Boolean> condition = (o, n) -> true;
	private TriConsumer<WeakReference<When<T>>, T, T> otherwise = (w, o, n) -> {};

    //================================================================================
    // Constructors
    //================================================================================
    public OnChanged(ObservableValue<T> observable) {
        super(observable);
    }

    /**
     * Build a "wrapping" {@code OnChanged} construct for the given observable and {@link ChangeListener}.
     * <p>
     * This should be used specifically when several listeners will execute the same action. To improve performance and
     * memory usage, you can build the listener yourself and create the construct with this.
     * <p>
     * Automatically active upon creation!
     * <p></p>
     * <b>Note</b> however that this special construct will not have any of its features working (no action, no condition,
     * no otherwise, etc.) because the listener is not built but the construct, of course.
     */
    public static <T> OnChanged<T> withListener(ObservableValue<T> observable, ChangeListener<T> cl) {
        return new OnChanged<>(observable) {
            {
                listener = cl;
                register();
                observable.addListener(cl);
            }
        };
    }

    //================================================================================
    // Methods
    //================================================================================

    public OnChanged<T> then(BiConsumer<T, T> action) {
        this.action = action;
        return this;
    }

    public OnChanged<T> condition(BiFunction<T, T, Boolean> condition) {
        this.condition = condition;
        return this;
    }

	public OnChanged<T> otherwise(TriConsumer<WeakReference<When<T>>, T, T> otherwise) {
		this.otherwise = otherwise;
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
		// If listener is not null, then this was already registered before and not disposed, exit!
		if (isDisposed() || listener != null) return this;

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
