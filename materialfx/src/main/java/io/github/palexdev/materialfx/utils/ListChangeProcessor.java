/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to process changes in {@link ObservableList}s.
 * <p>
 * It's capable of computing additions and removals.
 */
public class ListChangeProcessor {
	//================================================================================
	// Properties
	//================================================================================
	private Set<Integer> indexes;

	//================================================================================
	// Constructors
	//================================================================================
	public ListChangeProcessor(Set<Integer> indexes) {
		this.indexes = indexes;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Computes additions given the number of items added and the index at which the
	 * addition occurred. All items after the given offset must be shifted of addedSize.
	 *
	 * @param addedSize the number of items added
	 * @param offset    the index at which the addition occurred
	 */
	public void computeAddition(int addedSize, int offset) {
		int max = indexes.stream().max(Integer::compare).orElse(-1);
		if (max != -1 && offset > max) return;

		Set<Integer> tmp = new HashSet<>(indexes);
		for (Integer i : indexes) {
			tmp.remove(i);
			int index = i + addedSize;
			tmp.add(index);
		}
		indexes = tmp;
	}

	/**
	 * Computes removals given the Set of removed indexes and the index at which the removal
	 * occurred. All items after the offset must be shifted, to correctly compute this shift,
	 * {@link #findShift(Set, int)} is used.
	 *
	 * @param removed the Set of removed indexes
	 * @param offset  the index at which the removal occurred
	 */
	public void computeRemoval(Set<Integer> removed, int offset) {
		int min = indexes.stream().min(Integer::compare).orElse(-1);
		int max = indexes.stream().max(Integer::compare).orElse(-1);
		if (min == -1 || offset > max) return;

		Set<Integer> tmp = new HashSet<>();
		for (Integer i : indexes) {
			if (i < offset) {
				tmp.add(i);
				continue;
			}
			int index = Math.max(i - findShift(removed, i), 0);
			tmp.add(index);
		}
		indexes = tmp;
	}

	/**
	 * Iterates over the given Set of removed indexes to count the number
	 * of indexes that are lesser or equal to the given index.
	 */
	private int findShift(Set<Integer> removed, int index) {
		return (int) removed.stream().filter(i -> i <= index).count();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the updated indexes
	 */
	public Set<Integer> getIndexes() {
		return indexes;
	}

}
