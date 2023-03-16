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

package io.github.palexdev.materialfx.collections;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

abstract class NonIterableChange<E> extends ListChangeListener.Change<E> {
	private final int from;
	private final int to;
	private boolean invalid = true;
	private static final int[] EMPTY_PERM = new int[0];

	protected NonIterableChange(int from, int to, ObservableList<E> list) {
		super(list);
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		checkState();
		return from;
	}

	public int getTo() {
		checkState();
		return to;
	}

	protected int[] getPermutation() {
		checkState();
		return EMPTY_PERM;
	}

	public boolean next() {
		if (invalid) {
			invalid = false;
			return true;
		} else {
			return false;
		}
	}

	public void reset() {
		invalid = true;
	}

	public void checkState() {
		if (invalid) {
			throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
		}
	}

	public String toString() {
		boolean oldInvalid = invalid;
		invalid = false;
		String ret;
		if (wasPermutated()) {
			ret = ChangeHelper.permChangeToString(getPermutation());
		} else if (wasUpdated()) {
			ret = ChangeHelper.updateChangeToString(from, to);
		} else {
			ret = ChangeHelper.addRemoveChangeToString(from, to, getList(), getRemoved());
		}

		invalid = oldInvalid;
		return "{ " + ret + " }";
	}

	public static class SimpleUpdateChange<E> extends NonIterableChange<E> {
		public SimpleUpdateChange(int position, ObservableList<E> list) {
			this(position, position + 1, list);
		}

		public SimpleUpdateChange(int from, int to, ObservableList<E> list) {
			super(from, to, list);
		}

		public List<E> getRemoved() {
			return Collections.emptyList();
		}

		public boolean wasUpdated() {
			return true;
		}
	}

	public static class SimplePermutationChange<E> extends NonIterableChange<E> {
		private final int[] permutation;

		public SimplePermutationChange(int from, int to, int[] permutation, ObservableList<E> list) {
			super(from, to, list);
			this.permutation = permutation;
		}

		public List<E> getRemoved() {
			checkState();
			return Collections.emptyList();
		}

		protected int[] getPermutation() {
			checkState();
			return permutation;
		}
	}

	public static class SimpleAddChange<E> extends NonIterableChange<E> {
		public SimpleAddChange(int from, int to, ObservableList<E> list) {
			super(from, to, list);
		}

		public boolean wasRemoved() {
			checkState();
			return false;
		}

		public List<E> getRemoved() {
			checkState();
			return Collections.emptyList();
		}
	}

	public static class SimpleRemovedChange<E> extends NonIterableChange<E> {
		private final List<E> removed;

		public SimpleRemovedChange(int from, int to, E removed, ObservableList<E> list) {
			super(from, to, list);
			this.removed = Collections.singletonList(removed);
		}

		public boolean wasRemoved() {
			checkState();
			return true;
		}

		public List<E> getRemoved() {
			checkState();
			return removed;
		}
	}

	public static class GenericAddRemoveChange<E> extends NonIterableChange<E> {
		private final List<E> removed;

		public GenericAddRemoveChange(int from, int to, List<E> removed, ObservableList<E> list) {
			super(from, to, list);
			this.removed = removed;
		}

		public List<E> getRemoved() {
			checkState();
			return removed;
		}
	}
}
