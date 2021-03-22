/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.flowless;

import javafx.scene.Node;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Factory class for wrapping a {@link Cell} and running additional code before/after specific methods
 */
abstract class CellWrapper<T, N extends Node, C extends Cell<T, N>>
        implements Cell<T, N> {

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> beforeDispose(C cell, Runnable action) {
        return new CellWrapper<>(cell) {
            @Override
            public void dispose() {
                action.run();
                super.dispose();
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> afterDispose(C cell, Runnable action) {
        return new CellWrapper<>(cell) {
            @Override
            public void dispose() {
                super.dispose();
                action.run();
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> beforeReset(C cell, Runnable action) {
        return new CellWrapper<>(cell) {
            @Override
            public void reset() {
                action.run();
                super.reset();
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> afterReset(C cell, Runnable action) {
        return new CellWrapper<>(cell) {
            @Override
            public void reset() {
                super.reset();
                action.run();
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> beforeUpdateItem(C cell, Consumer<? super T> action) {
        return new CellWrapper<>(cell) {
            @Override
            public void updateItem(T item) {
                action.accept(item);
                super.updateItem(item);
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> afterUpdateItem(C cell, Consumer<? super T> action) {
        return new CellWrapper<>(cell) {
            @Override
            public void updateItem(T item) {
                super.updateItem(item);
                action.accept(item);
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> beforeUpdateIndex(C cell, IntConsumer action) {
        return new CellWrapper<>(cell) {
            @Override
            public void updateIndex(int index) {
                action.accept(index);
                super.updateIndex(index);
            }
        };
    }

    public static <T, N extends Node, C extends Cell<T, N>>
    CellWrapper<T, N, C> afterUpdateIndex(C cell, IntConsumer action) {
        return new CellWrapper<>(cell) {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                action.accept(index);
            }
        };
    }

    private final C delegate;

    public CellWrapper(C delegate) {
        this.delegate = delegate;
    }

    public C getDelegate() {
        return delegate;
    }

    @Override
    public N getNode() {
        return delegate.getNode();
    }

    @Override
    public boolean isReusable() {
        return delegate.isReusable();
    }

    @Override
    public void updateItem(T item) {
        delegate.updateItem(item);
    }

    @Override
    public void updateIndex(int index) {
        delegate.updateIndex(index);
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
