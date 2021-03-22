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

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

/**
 * Helper class that stores a pool of reusable cells that can be updated via {@link Cell#updateItem(Object)} or
 * creates new ones via its {@link #cellFactory} if the pool is empty.
 */
final class CellPool<T, C extends Cell<T, ?>> {
    private final Function<? super T, ? extends C> cellFactory;
    private final Queue<C> pool = new LinkedList<>();

    public CellPool(Function<? super T, ? extends C> cellFactory) {
        this.cellFactory = cellFactory;
    }

    /**
     * Returns a reusable cell that has been updated with the current item if the pool has one, or returns a
     * newly-created one via its {@link #cellFactory}.
     */
    public C getCell(T item) {
        C cell = pool.poll();
        if (cell != null) {
            cell.updateItem(item);
        } else {
            cell = cellFactory.apply(item);
        }
        return cell;
    }

    /**
     * Adds the cell to the pool of reusable cells if {@link Cell#isReusable()} is true, or
     * {@link Cell#dispose() disposes} the cell if it's not.
     */
    public void acceptCell(C cell) {
        cell.reset();
        if (cell.isReusable()) {
            pool.add(cell);
        } else {
            cell.dispose();
        }
    }

    /**
     * Disposes the cell pool and prevents any memory leaks.
     */
    public void dispose() {
        for (C cell : pool) {
            cell.dispose();
        }

        pool.clear();
    }
}