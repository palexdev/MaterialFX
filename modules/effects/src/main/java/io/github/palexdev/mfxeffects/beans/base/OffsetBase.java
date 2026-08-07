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

package io.github.palexdev.mfxeffects.beans.base;

import java.util.Objects;

public abstract class OffsetBase {
    protected final double dx;
    protected final double dy;

    public OffsetBase(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean lesser(OffsetBase other) {
        return dx < other.dx && dy < other.dy;
    }

    public boolean lesserEq(OffsetBase other) {
        return dx <= other.dx && dy <= other.dy;
    }

    public boolean greater(OffsetBase other) {
        return dx > other.dx && dy > other.dy;
    }

    public boolean greaterEq(OffsetBase other) {
        return dx >= other.dx && dy >= other.dy;
    }

    public boolean isInfinite() {
        return Double.isInfinite(dx) || Double.isInfinite(dy);
    }

    public boolean isFinite() {
        return !isInfinite();
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetBase that = (OffsetBase) o;
        return Double.compare(that.dx, dx) == 0 && Double.compare(that.dy, dy) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dx, dy);
    }

    @Override
    public String toString() {
        return String.format("%s(%f, %f)", getClass().getSimpleName(), dx, dy);
    }
}
