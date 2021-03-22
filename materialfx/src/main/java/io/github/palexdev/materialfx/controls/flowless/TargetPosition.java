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

/**
 * Defines where the {@link Navigator} should place the anchor cell's node in the viewport. Its three implementations
 * are {@link StartOffStart}, {@link EndOffEnd}, and {@link MinDistanceTo}.
 */
interface TargetPosition {
    TargetPosition BEGINNING = new StartOffStart(0, 0.0);

    /**
     * When the list of items, those displayed in the viewport, and those that are not, are modified, transforms
     * this change to account for those modifications.
     *
     * @param pos         the cell index where the change begins
     * @param removedSize the amount of cells that were removed, starting from {@code pos}
     * @param addedSize   the amount of cells that were added, starting from {@code pos}
     */
    TargetPosition transformByChange(int pos, int removedSize, int addedSize);


    TargetPosition scrollBy(double delta);

    /**
     * Visitor Pattern: prevents type-checking the implementation
     */
    void accept(TargetPositionVisitor visitor);

    /**
     * Insures this position's item index is between 0 and {@code size}
     */
    TargetPosition clamp(int size);
}

/**
 * Uses the Visitor Pattern, so {@link Navigator} does not need to check the type of the {@link TargetPosition}
 * before using it to determine how to fill the viewport.
 */
interface TargetPositionVisitor {

    void visit(StartOffStart targetPosition);

    void visit(EndOffEnd targetPosition);

    void visit(MinDistanceTo targetPosition);
}

/**
 * A {@link TargetPosition} that instructs its {@link TargetPositionVisitor} to use the cell at {@link #itemIndex}
 * as the anchor cell, showing it at the "top" of the viewport and to offset it by {@link #offsetFromStart}.
 */
final class StartOffStart implements TargetPosition {
    final int itemIndex;
    final double offsetFromStart;

    StartOffStart(int itemIndex, double offsetFromStart) {
        this.itemIndex = itemIndex;
        this.offsetFromStart = offsetFromStart;
    }

    @Override
    public TargetPosition transformByChange(
            int pos, int removedSize, int addedSize) {
        if (itemIndex >= pos + removedSize) {
            // change before the target item, just update item index
            return new StartOffStart(itemIndex - removedSize + addedSize, offsetFromStart);
        } else if (itemIndex >= pos) {
            // target item deleted
            if (addedSize == removedSize) {
                return this;
            } else {
                // show the first inserted at the target offset
                return new StartOffStart(pos, offsetFromStart);
            }
        } else {
            // change after the target item, target position not affected
            return this;
        }
    }

    @Override
    public TargetPosition scrollBy(double delta) {
        return new StartOffStart(itemIndex, offsetFromStart - delta);
    }

    @Override
    public void accept(TargetPositionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public TargetPosition clamp(int size) {
        return new StartOffStart(clamp(itemIndex, size), offsetFromStart);
    }

    static int clamp(int idx, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative: " + size);
        }
        if (idx <= 0) {
            return 0;
        } else if (idx >= size) {
            return size - 1;
        } else {
            return idx;
        }
    }
}

/**
 * A {@link TargetPosition} that instructs its {@link TargetPositionVisitor} to use the cell at {@link #itemIndex}
 * as the anchor cell, showing it at the "bottom" of the viewport and to offset it by {@link #offsetFromEnd}.
 */
final class EndOffEnd implements TargetPosition {
    final int itemIndex;
    final double offsetFromEnd;

    EndOffEnd(int itemIndex, double offsetFromEnd) {
        this.itemIndex = itemIndex;
        this.offsetFromEnd = offsetFromEnd;
    }

    @Override
    public TargetPosition transformByChange(
            int pos, int removedSize, int addedSize) {
        if (itemIndex >= pos + removedSize) {
            // change before the target item, just update item index
            return new EndOffEnd(itemIndex - removedSize + addedSize, offsetFromEnd);
        } else if (itemIndex >= pos) {
            // target item deleted
            if (addedSize == removedSize) {
                return this;
            } else {
                // show the last inserted at the target offset
                return new EndOffEnd(pos + addedSize - 1, offsetFromEnd);
            }
        } else {
            // change after the target item, target position not affected
            return this;
        }
    }

    @Override
    public TargetPosition scrollBy(double delta) {
        return new EndOffEnd(itemIndex, offsetFromEnd - delta);
    }

    @Override
    public void accept(TargetPositionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public TargetPosition clamp(int size) {
        return new EndOffEnd(StartOffStart.clamp(itemIndex, size), offsetFromEnd);
    }
}

final class MinDistanceTo implements TargetPosition {
    final int itemIndex;
    final Offset minY;
    final Offset maxY;

    MinDistanceTo(int itemIndex, Offset minY, Offset maxY) {
        this.itemIndex = itemIndex;
        this.minY = minY;
        this.maxY = maxY;
    }

    public MinDistanceTo(int itemIndex) {
        this(itemIndex, Offset.fromStart(0.0), Offset.fromEnd(0.0));
    }

    @Override
    public TargetPosition transformByChange(
            int pos, int removedSize, int addedSize) {
        if (itemIndex >= pos + removedSize) {
            // change before the target item, just update item index
            return new MinDistanceTo(itemIndex - removedSize + addedSize, minY, maxY);
        } else if (itemIndex >= pos) {
            // target item deleted
            if (addedSize == removedSize) {
                return this;
            } else {
                // show the first inserted
                return new MinDistanceTo(pos, Offset.fromStart(0.0), Offset.fromEnd(0.0));
            }
        } else {
            // change after the target item, target position not affected
            return this;
        }
    }

    @Override
    public TargetPosition scrollBy(double delta) {
        return new MinDistanceTo(itemIndex, minY.add(delta), maxY.add(delta));
    }

    @Override
    public void accept(TargetPositionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public TargetPosition clamp(int size) {
        return new MinDistanceTo(StartOffStart.clamp(itemIndex, size), minY, maxY);
    }
}

/**
 * Helper class: stores an {@link #offset} value, which should either be offset from the start if {@link #fromStart}
 * is true or from the end if false.
 */
class Offset {
    public static Offset fromStart(double offset) {
        return new Offset(offset, true);
    }

    public static Offset fromEnd(double offset) {
        return new Offset(offset, false);
    }

    private final double offset;
    private final boolean fromStart;

    private Offset(double offset, boolean fromStart) {
        this.offset = offset;
        this.fromStart = fromStart;
    }

    public double getValue() {
        return offset;
    }

    public boolean isFromStart() {
        return fromStart;
    }

    public boolean isFromEnd() {
        return !fromStart;
    }

    public Offset add(double delta) {
        return new Offset(offset + delta, fromStart);
    }
}