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

import javafx.geometry.Point2D;

/**
 * Stores the result of a {@link VirtualFlow#hit(double, double)}. Before calling
 * any of the getters, one should determine what kind of hit this object is via {@link #isCellHit()},
 * {@link #isBeforeCells()}, and {@link #isAfterCells()}. Otherwise, calling the wrong getter will throw
 * an {@link UnsupportedOperationException}.
 * <p>
 * ypes of VirtualFlowHit:</p>
 * <ul>
 *     <li>
 *         <em>Cell Hit:</em> a hit occurs on a displayed cell's node. One can call {@link #getCell()},
 *         {@link #getCellIndex()}, and {@link #getCellOffset()}.
 *     </li>
 *     <li>
 *         <em>Hit Before Cells:</em> a hit occurred before the displayed cells. One can call
 *         {@link #getOffsetBeforeCells()}.
 *     </li>
 *     <li>
 *         <em>Hit After Cells:</em> a hit occurred after the displayed cells. One can call
 *         {@link #getOffsetAfterCells()}.
 *     </li>
 * </ul>
 */
public abstract class VirtualFlowHit<C extends Cell<?, ?>> {

    static <C extends Cell<?, ?>> VirtualFlowHit<C> cellHit(
            int cellIndex, C cell, double x, double y) {
        return new CellHit<>(cellIndex, cell, new Point2D(x, y));
    }

    static <C extends Cell<?, ?>> VirtualFlowHit<C> hitBeforeCells(double x, double y) {
        return new HitBeforeCells<>(new Point2D(x, y));
    }

    static <C extends Cell<?, ?>> VirtualFlowHit<C> hitAfterCells(double x, double y) {
        return new HitAfterCells<>(new Point2D(x, y));
    }

    // private constructor to prevent subclassing
    private VirtualFlowHit() {
    }

    public abstract boolean isCellHit();

    public abstract boolean isBeforeCells();

    public abstract boolean isAfterCells();

    public abstract int getCellIndex();

    public abstract C getCell();

    public abstract Point2D getCellOffset();

    public abstract Point2D getOffsetBeforeCells();

    public abstract Point2D getOffsetAfterCells();

    private static class CellHit<C extends Cell<?, ?>> extends VirtualFlowHit<C> {
        private final int cellIdx;
        private final C cell;
        private final Point2D cellOffset;

        CellHit(int cellIdx, C cell, Point2D cellOffset) {
            this.cellIdx = cellIdx;
            this.cell = cell;
            this.cellOffset = cellOffset;
        }

        @Override
        public boolean isCellHit() {
            return true;
        }

        @Override
        public boolean isBeforeCells() {
            return false;
        }

        @Override
        public boolean isAfterCells() {
            return false;
        }

        @Override
        public int getCellIndex() {
            return cellIdx;
        }

        @Override
        public C getCell() {
            return cell;
        }

        @Override
        public Point2D getCellOffset() {
            return cellOffset;
        }

        @Override
        public Point2D getOffsetBeforeCells() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getOffsetAfterCells() {
            throw new UnsupportedOperationException();
        }
    }

    private static class HitBeforeCells<C extends Cell<?, ?>> extends VirtualFlowHit<C> {
        private final Point2D offset;

        HitBeforeCells(Point2D offset) {
            this.offset = offset;
        }

        @Override
        public boolean isCellHit() {
            return false;
        }

        @Override
        public boolean isBeforeCells() {
            return true;
        }

        @Override
        public boolean isAfterCells() {
            return false;
        }

        @Override
        public int getCellIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public C getCell() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getCellOffset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getOffsetBeforeCells() {
            return offset;
        }

        @Override
        public Point2D getOffsetAfterCells() {
            throw new UnsupportedOperationException();
        }
    }

    private static class HitAfterCells<C extends Cell<?, ?>> extends VirtualFlowHit<C> {
        private final Point2D offset;

        HitAfterCells(Point2D offset) {
            this.offset = offset;
        }

        @Override
        public boolean isCellHit() {
            return false;
        }

        @Override
        public boolean isBeforeCells() {
            return false;
        }

        @Override
        public boolean isAfterCells() {
            return true;
        }

        @Override
        public int getCellIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public C getCell() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getCellOffset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getOffsetBeforeCells() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Point2D getOffsetAfterCells() {
            return offset;
        }
    }
}