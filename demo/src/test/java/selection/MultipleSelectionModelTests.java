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

package selection;

import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleSelectionModelTests {
	private final ObservableList<String> strings = IntStream.rangeClosed(0, 30)
			.mapToObj(i -> "String " + i)
			.collect(FXCollectors.toList());
	private final MultipleSelectionModel<String> selectionModel = new MultipleSelectionModel<String>(strings);

	@BeforeEach
	public void setUp() {
		selectionModel.clearSelection();
	}

	@Test
	public void testOrder() {
		Integer[] toSelect = {0, 6, 3, 9, 6};
		selectionModel.selectIndexes(List.of(toSelect));

		assertEquals(4, selectionModel.getSelection().size());

		// Indexes
		int i = 0;
		Set<Integer> indexes = selectionModel.getSelection().keySet();
		for (int val : indexes) {
			assertEquals(toSelect[i], val);
			i++;
		}

		// Values
		i = 0;
		String[] expected = {
				"String 0",
				"String 6",
				"String 3",
				"String 9"
		};
		List<String> values = selectionModel.getSelectedValues();
		for (String value : values) {
			assertEquals(expected[i], value);
			i++;
		}
	}
}
