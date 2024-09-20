/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.base.beans.range;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Set;

/**
 * A special extension of {@link IntegerRange} which allows excluding some or all of the values from {@link #getMin()}
 * to {@link #getMax()}.
 * <p>
 * This basically offers a much faster alternative to {@link IntegerRange#expandRangeToSet(IntegerRange)}. Let's see an example:
 * <pre>
 * {@code
 * // Let's say you have a range of [3, 7] and you want to perform some checks on each value in the range
 * // You want to perform a certain operation on every value for which the check fails
 * // Before this class you could do something like this...
 * IntegerRange range = IntegerRange.of(3, 7);
 * Set<Integer> expanded = IntegerRange.expandRangeToSet(range);
 * for (Integer val : range) {
 *     if (check(val)) {
 *         expanded.remove(val);
 *     }
 * }
 * // At the end of the for you have a Set of values for which the check failed, at this point...
 * for (Integer failingVal : expanded) {
 *     process(failingVal)
 * }
 *
 * //____________________________________________________________________________________________________//
 *
 * // Now, the issue here is that the `expandRangeToSet(...)` operation can be costly, and we don't really need it
 * // By using an ExcludingIntegerRange the code becomes like this...
 * IntegerRange range = IntegerRange.of(3, 7);
 * ExcludingIntegerRange eRange = ExcludingIntegerRange.of(range);
 * for (Integer val : range) {
 *     if (check(val)) {
 *         eRange.exclude(val);
 *     }
 * }
 *
 * for (Integer failingVal : eRange) {
 *     process(failingVal);
 * }
 * // There's no need to expand the whole range to a Set anymore and ExcludingIntegerRange also implements Iterable thus offering
 * // a pretty efficient Iterator which allows to use enhanced for loops too.
 * }
 * </pre>
 * <p></p>
 * <b>WARNING!</b>
 * <p>
 * The excluding feature is intended to be useful for iterations. Methods like {@link #sum()} or {@link #diff()} won't
 * take exclusions into account!
 */
public class ExcludingIntegerRange extends IntegerRange {
    //================================================================================
    // Properties
    //================================================================================
    private final Set<Integer> excluded;

    //================================================================================
    // Constructors
    //================================================================================
    public ExcludingIntegerRange(Integer min, Integer max) {
        super(min, max);
        this.excluded = new HashSet<>(diff() + 1);
    }

    public static ExcludingIntegerRange of(IntegerRange range) {
        return new ExcludingIntegerRange(range.getMin(), range.getMax());
    }

    public static ExcludingIntegerRange of(int min, int max) {
        return new ExcludingIntegerRange(min, max);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds the given value to the set of excluded values.
     * <p>
     * If it is out of range, nothing is done.
     *
     * @see IntegerRange#inRangeOf(int, IntegerRange)
     */
    public ExcludingIntegerRange exclude(int val) {
        if (IntegerRange.inRangeOf(val, this)) {
            excluded.add(val);
        }
        return this;
    }

    /**
     * Iterates over the given values and delegates to {@link #exclude(int)}
     */
    public ExcludingIntegerRange excludeAll(Integer... vals) {
        for (int val : vals) exclude(val);
        return this;
    }

    /**
     * Iterates over the given range and delegates to {@link #exclude(int)}
     */
    public ExcludingIntegerRange excludeAll(IntegerRange range) {
        range.forEach(this::exclude);
        return this;
    }

    /**
     * @return whether the given value was excluded or outside the starting range
     */
    public boolean isExcluded(int val) {
        return excluded.contains(val) || !IntegerRange.inRangeOf(val, this);
    }

    /**
     * @return the set of excluded values
     */
    public Set<Integer> getExcluded() {
        return excluded;
    }


    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public PrimitiveIterator.OfInt iterator() {
        return new ExcludingIterator();
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * A very simple iterator loop on the elements of an {@link ExcludingIntegerRange}.
     * <p>
     * Of course, it takes into account the values that were excluded from the range, {@link ExcludingIntegerRange#isExcluded(int)}.
     */
    private class ExcludingIterator implements PrimitiveIterator.OfInt {
        private int current = getMin();

        @Override
        public boolean hasNext() {
            while (current <= getMax() && isExcluded(current)) current++;
            return current <= getMax();
        }

        @Override
        public int nextInt() {
            if (!hasNext()) throw new NoSuchElementException();
            return current++;
        }
    }
}
