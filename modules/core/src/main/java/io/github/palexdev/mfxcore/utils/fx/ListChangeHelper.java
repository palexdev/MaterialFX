/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to process {@link ListChangeListener.Change}s in a list of items of type T.
 */
public class ListChangeHelper {
	//================================================================================
	// Singleton
	//================================================================================
	private static final ListChangeHelper instance = new ListChangeHelper();

	public static ListChangeHelper instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private IntegerRange range = IntegerRange.of(0, Integer.MAX_VALUE);

	//================================================================================
	// Constructors
	//================================================================================
	private ListChangeHelper() {
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Given a {@link ListChangeListener.Change} occurred in a {@link ObservableList} processes the change
	 * computing all the permutations, replacements, additions and removals in one go.
	 * <p>
	 * Each {@link Change} in the returned list represents one and only one of the above cases.
	 * <p></p>
	 * This conversion is useful if you want to process changes as a whole. Meaning that all the indexes
	 * represent the items BEFORE the change occurred.
	 * <p>
	 * For example. If you have multiple sparse removals the JavaFX's {@link ListChangeListener.Change} computes the indexes
	 * in steps...easier to understand with an example:
	 * <pre>
	 * {@code
	 * // Let's assume we have a list on Strings
	 * // [S0 S1 S2 S3 S4 S5 S6 S7 S8 S9 S10]
	 * // Now let's assume the change is a removal of these indexes (0, 3)
	 * // Since JavaFX processes indexes with "steps", the change will return the index 0 and !2! as
	 * // the removed indexes
	 * }
	 * </pre>
	 * I hate this behavior, also in some occasions for some reason the {@link ListChangeListener.Change#getFrom()} and
	 * {@link ListChangeListener.Change#getTo()} methods return invalid values.
	 * <p>
	 * Pissed of by such implementation I made this converter :)
	 * <p></p>
	 * <p>
	 * Let's also not forget that replacements are treated by JavaFX as a removal and then an addition.
	 * Imagine doing a "setAll" operation...what a mess.
	 * <p></p>
	 * <p>
	 * Last but not least this is also capable of filtering the indexes if you specify a bound range with
	 * {@link #setRange(IntegerRange)}. At the end od the processing the range is ALWAYS reset.
	 */
	public <T> List<Change> processChange(ListChangeListener.Change<? extends T> change) {
		List<Change> changes = new ArrayList<>();

		int removedSize = 0;
		int removeFrom = -1;
		int removeTo = -1;
		Set<Integer> removedAccumulator = new HashSet<>();

		while (change.next()) {
			if (change.wasPermutated()) {
				Set<Integer> indexes = (range.getMax() == Integer.MAX_VALUE) ? Set.of() : IntegerRange.expandRangeToSet(range);
				changes.add(new Change(ChangeType.PERMUTATION, range, indexes));
				continue;
			}

			if (change.wasReplaced()) {
				IntegerRange repRange = IntegerRange.of(change.getFrom(), change.getTo() - 1);
				Set<Integer> changed = IntegerRange.expandRangeToSet(repRange).stream()
						.filter(i -> IntegerRange.inRangeOf(i, range))
						.collect(Collectors.toSet());
				changes.add(new Change(ChangeType.REPLACE, repRange, changed));
				continue;
			}

			if (change.wasAdded()) {
				IntegerRange addRange = IntegerRange.of(change.getFrom(), change.getTo() - 1);
				changes.add(new Change(ChangeType.ADD, addRange, IntegerRange.expandRangeToSet(addRange)));
				continue;
			}

			if (change.wasRemoved()) {
				if (!IntegerRange.inRangeOf(change.getFrom(), range)) continue;

				IntegerRange remRange = computeRemovedIndexes(change, removedSize);
				if (removeFrom == -1) removeFrom = remRange.getMin();
				removeTo = remRange.getMax();
				removedAccumulator.addAll(IntegerRange.expandRangeToSet(remRange));
				removedSize += change.getRemovedSize();
			}
		}

		if (!removedAccumulator.isEmpty())
			changes.add(new Change(ChangeType.REMOVE, IntegerRange.of(removeFrom, removeTo), removedAccumulator));

		resetRange();
		return changes;
	}

	/**
	 * Sets the range used to limit the indexes computed by {@link #processChange(ListChangeListener.Change)}.
	 */
	public ListChangeHelper setRange(IntegerRange range) {
		this.range = range;
		return this;
	}

	/**
	 * Resets the range set with {@link #setRange(IntegerRange)}, replaced by {@code [0, Integer.MAX_VALUE]}.
	 */
	public ListChangeHelper resetRange() {
		this.range = IntegerRange.of(0, Integer.MAX_VALUE);
		return this;
	}

	/**
	 * Helper method to correctly compute the index of one or multiple removal
	 * changes.
	 */
	private <T> IntegerRange computeRemovedIndexes(ListChangeListener.Change<? extends T> change, int toOffset) {
		int size = change.getList().size();
		if (size == 0) {
			return IntegerRange.of(0, change.getRemovedSize() - 1);
		}

		int from = change.getTo() + toOffset;
		int to = change.getFrom() + (change.getRemovedSize() - 1) + toOffset;
		return IntegerRange.of(from, to);
	}

	/**
	 * Bean that contains:
	 * <p> - The type of change, see {@link ChangeType}
	 * <p> - The range of changed indexes
	 * <p> - The changed indexes as a {@link Set} of integers
	 */
	public static class Change {
		private final ChangeType type;
		private final IntegerRange range;
		private final Set<Integer> indexes = new HashSet<>();

		public Change(ChangeType type, IntegerRange range) {
			this.type = type;
			this.range = range;
		}

		public Change(ChangeType type, IntegerRange range, Collection<Integer> indexes) {
			this.type = type;
			this.range = range;
			this.indexes.addAll(indexes);
		}

		public Set<Integer> getIndexes() {
			return indexes;
		}

		public int size() {
			return indexes.size();
		}

		public boolean isEmpty() {
			return indexes.isEmpty();
		}

		public boolean hasChanged(int index) {
			return indexes.contains(index);
		}

		public ChangeType getType() {
			return type;
		}

		public int getFrom() {
			return range.getMin();
		}

		public int getTo() {
			return range.getMax();
		}
	}

	/**
	 * Enumerator to represent the various types of {@link ListChangeListener.Change}s
	 */
	public enum ChangeType {
		/**
		 * In case of {@link ListChangeListener.Change#wasPermutated()}
		 */
		PERMUTATION,

		/**
		 * In case of {@link ListChangeListener.Change#wasReplaced()}
		 */
		REPLACE,

		/**
		 * In case of {@link ListChangeListener.Change#wasAdded()}
		 */
		ADD,

		/**
		 * In case of {@link ListChangeListener.Change#wasRemoved()}
		 */
		REMOVE
	}
}
