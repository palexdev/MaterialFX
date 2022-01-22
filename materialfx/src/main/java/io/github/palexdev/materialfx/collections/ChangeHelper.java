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

import java.util.Arrays;
import java.util.List;

class ChangeHelper {
	ChangeHelper() {
	}

	public static String addRemoveChangeToString(int from, int to, List<?> list, List<?> removed) {
		StringBuilder sb = new StringBuilder();
		if (removed.isEmpty()) {
			sb.append(list.subList(from, to));
			sb.append(" added at ").append(from);
		} else {
			sb.append(removed);
			if (from == to) {
				sb.append(" removed at ").append(from);
			} else {
				sb.append(" replaced by ");
				sb.append(list.subList(from, to));
				sb.append(" at ").append(from);
			}
		}

		return sb.toString();
	}

	public static String permChangeToString(int[] permutation) {
		return "permutated by " + Arrays.toString(permutation);
	}

	public static String updateChangeToString(int from, int to) {
		return "updated at range [" + from + ", " + to + ")";
	}
}
