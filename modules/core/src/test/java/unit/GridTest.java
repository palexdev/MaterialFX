/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package unit;

import io.github.palexdev.mfxcore.collections.Grid;
import io.github.palexdev.mfxcore.collections.Grid.Coordinates;
import io.github.palexdev.mfxcore.utils.GridUtils;
import io.github.palexdev.mfxcore.utils.RandomUtils;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GridTest {
	private final List<String> data = new ArrayList<>(List.of(
			"A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y"
	));
	private Grid<String> grid;

	@BeforeEach
	public void setUp() {
		grid = Grid.fromList(data, 5);
	}

	@Test
	public void testFromMatrix() {
		Integer[][] matrix = getRandomIntMatrix(3, 8);
		List<Integer> toLinear = matrixToLinear(matrix);
		Grid<Integer> grid = Grid.fromMatrix(matrix);

		assertEquals(3, grid.getRowsNum());
		assertEquals(8, grid.getColumnsNum());
		assertEquals(toLinear, grid.getData());
	}

	@Test
	public void testInit1() {
		int cnt = 0;
		grid.init(3, 3);
		for (int i = 0; i < grid.totalSize(); i++) {
			if (grid.getData().get(i) == null) cnt++;
		}
		assertEquals(9, cnt);
	}

	@Test
	public void testInit2() {
		int cnt = 0;
		grid.init();
		for (int i = 0; i < grid.totalSize(); i++) {
			if (grid.getData().get(i) == null) cnt++;
		}
		assertEquals(25, cnt);
	}

	@Test
	public void testSize() {
		Pair<Integer, Integer> size = grid.size();
		assertEquals(25, grid.totalSize());
		assertEquals(5, size.getKey());
		assertEquals(5, size.getValue());
	}

	@Test
	public void testGetElements() {
		String e1 = grid.getElement(0, 0);
		assertEquals("A", e1);
		String e2 = grid.getElement(1, 1);
		assertEquals("G", e2);
		String e3 = grid.getElement(2, 2);
		assertEquals("M", e3);
		String e4 = grid.getElement(3, 3);
		assertEquals("S", e4);
		String e5 = grid.getElement(4, 4);
		assertEquals("Y", e5);
	}

	@Test
	public void testSetElements() {
		List<String> expected = new ArrayList<>(List.of(
				"0", "B", "C", "D", "E",
				"F", "1", "H", "I", "J",
				"K", "L", "2", "N", "O",
				"P", "Q", "R", "3", "T",
				"U", "V", "W", "X", "4"
		));
		grid.setElement(0, 0, "0");
		grid.setElement(1, 1, "1");
		grid.setElement(2, 2, "2");
		grid.setElement(3, 3, "3");
		grid.setElement(4, 4, "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testSetDiagonal() {
		List<String> expected = new ArrayList<>(List.of(
				"0", "B", "C", "D", "E",
				"F", "1", "H", "I", "J",
				"K", "L", "2", "N", "O",
				"P", "Q", "R", "3", "T",
				"U", "V", "W", "X", "4"
		));
		grid.setDiagonal("0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testGetRow1() {
		List<String> expected = List.of("A", "B", "C", "D", "E");
		assertEquals(expected, grid.getRow(0));
	}

	@Test
	public void testGetRow2() {
		List<String> expected = List.of("U", "V", "W", "X", "Y");
		assertEquals(expected, grid.getRow(4));
	}

	@Test
	public void testGetRow3() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.getRow(5));
	}

	@Test
	public void testGetRow4() {
		List<String> expected = List.of("A", "B", "D");
		assertEquals(expected, grid.getRow(0, 2, 4));
	}

	@Test
	public void testGetRow5() {
		List<String> expected = List.of("A", "B", "C", "D", "E");
		assertEquals(expected, grid.getRow(0, 10, 11, 12));
	}

	@Test
	public void testSetRow1() {
		List<String> expected = List.of(
				"0", "1", "2", "3", "4",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y"
		);
		grid.setRow(0, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testSetRow2() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"0", "1", "2", "3", "4"
		);
		grid.setRow(grid.getRowsNum() - 1, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testSetRow3() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"0", "1", "2", "3", "4",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y"
		);
		grid.setRow(2, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testGetColumn1() {
		List<String> expected = List.of("A", "F", "K", "P", "U");
		assertEquals(expected, grid.getColumn(0));
	}

	@Test
	public void testGetColumn2() {
		List<String> expected = List.of("E", "J", "O", "T", "Y");
		assertEquals(expected, grid.getColumn(4));
	}

	@Test
	public void testGetColumn3() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.getColumn(5));
	}

	@Test
	public void testGetColumn4() {
		List<String> expected = List.of("H", "R");
		assertEquals(expected, grid.getColumn(2, 0, 2, 4));
	}

	@Test
	public void testGetColumn5() {
		List<String> expected = List.of("A", "F", "K", "P", "U");
		assertEquals(expected, grid.getColumn(0, 10, 11, 12));
	}

	@Test
	public void testSetColumn1() {
		List<String> expected = List.of(
				"0", "B", "C", "D", "E",
				"1", "G", "H", "I", "J",
				"2", "L", "M", "N", "O",
				"3", "Q", "R", "S", "T",
				"4", "V", "W", "X", "Y"
		);
		grid.setColumn(0, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testSetColumn2() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "0",
				"F", "G", "H", "I", "1",
				"K", "L", "M", "N", "2",
				"P", "Q", "R", "S", "3",
				"U", "V", "W", "X", "4"
		);
		grid.setColumn(grid.getColumnsNum() - 1, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testSetColumn3() {
		List<String> expected = List.of(
				"A", "0", "C", "D", "E",
				"F", "1", "H", "I", "J",
				"K", "2", "M", "N", "O",
				"P", "3", "R", "S", "T",
				"U", "4", "W", "X", "Y"
		);
		grid.setColumn(1, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testAddRow1() {
		List<String> expected = List.of(
				"0", "1", "2", "3", "4",
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y"
		);
		grid.addRow(0, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getRowsNum());
	}

	@Test
	public void testAddRow2() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y",
				"0", "1", "2", "3", "4"
		);
		grid.addRow("0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getRowsNum());
	}

	@Test
	public void testAddRow3() {
		List<String> expected = Arrays.asList(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"0", "1", null, null, "4",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y"
		);
		grid.addRow(2, "0", "1", null, null, "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getRowsNum());
	}

	@Test
	public void testAddRow4() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.addRow(6, "0"));
	}

	@Test
	public void testAddRow5() {
		assertThrows(IllegalArgumentException.class, () -> grid.addRow(3));
	}

	@Test
	public void testAddRow6() {
		List<String> expected = Arrays.asList("A", "B", null, "D", null);
		grid.clear();
		grid.addRow(expected);
		assertEquals(expected, grid.getData());
		assertEquals(1, grid.getRowsNum());
		assertEquals(5, grid.getColumnsNum());
	}

	@Test
	public void testAddColumn1() {
		List<String> expected = List.of(
				"0", "A", "B", "C", "D", "E",
				"1", "F", "G", "H", "I", "J",
				"2", "K", "L", "M", "N", "O",
				"3", "P", "Q", "R", "S", "T",
				"4", "U", "V", "W", "X", "Y"
		);
		grid.addColumn(0, "0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getColumnsNum());
	}

	@Test
	public void testAddColumn2() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "E", "0",
				"F", "G", "H", "I", "J", "1",
				"K", "L", "M", "N", "O", "2",
				"P", "Q", "R", "S", "T", "3",
				"U", "V", "W", "X", "Y", "4"
		);
		grid.addColumn("0", "1", "2", "3", "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getColumnsNum());
	}

	@Test
	public void testAddColumn3() {
		List<String> expected = Arrays.asList(
				"A", "B", "C", "0", "D", "E",
				"F", "G", "H", "1", "I", "J",
				"K", "L", "M", null, "N", "O",
				"P", "Q", "R", null, "S", "T",
				"U", "V", "W", "4", "X", "Y"
		);
		grid.addColumn(3, "0", "1", null, null, "4");
		assertEquals(expected, grid.getData());
		assertEquals(6, grid.getColumnsNum());
	}

	@Test
	public void testAddColumn4() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.addColumn(6, "0"));
	}

	@Test
	public void testAddColumn5() {
		assertThrows(IllegalArgumentException.class, () -> grid.addColumn(1, "0"));
	}

	@Test
	public void testAddColumn6() {
		List<String> expected = Arrays.asList("A", "B", null, "D", null);
		grid.clear();
		grid.addColumn(expected);
		assertEquals(expected, grid.getData());
		assertEquals(5, grid.getRowsNum());
		assertEquals(1, grid.getColumnsNum());
	}

	@Test
	public void testAddComplex1() {
		List<String> row = Arrays.asList("A", "B", null, "D", null);
		grid.clear();
		grid.addRow(row);
		assertEquals(1, grid.getRowsNum());
		assertEquals(5, grid.getColumnsNum());

		grid.addColumn("0");
		assertEquals(1, grid.getRowsNum());
		assertEquals(6, grid.getColumnsNum());

		assertThrows(IllegalArgumentException.class, () -> grid.addColumn("1", "2", "3", "4", "5"));
	}

	@Test
	public void testAddComplex2() {
		List<String> column = Arrays.asList("A", "B", null, "D", null);
		grid.clear();
		grid.addColumn(column);
		assertEquals(5, grid.getRowsNum());
		assertEquals(1, grid.getColumnsNum());

		grid.addRow("0");
		assertEquals(6, grid.getRowsNum());
		assertEquals(1, grid.getColumnsNum());

		assertThrows(IllegalArgumentException.class, () -> grid.addRow("1", "2", "3", "4", "5"));
	}

	@Test
	public void testRemoveRow1() {
		List<String> expected = new ArrayList<>(List.of(
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y"
		));
		grid.removeFirstRow();
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getRowsNum());
	}

	@Test
	public void testRemoveRow2() {
		List<String> expected = new ArrayList<>(List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"P", "Q", "R", "S", "T"
		));
		grid.removeLastRow();
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getRowsNum());
	}

	@Test
	public void testRemoveRow3() {
		List<String> expected = new ArrayList<>(List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"U", "V", "W", "X", "Y"
		));
		grid.removeRow(3);
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getRowsNum());
	}

	@Test
	public void testRemoveRow4() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.removeRow(10));
	}

	@Test
	public void testRemoveColumn1() {
		List<String> expected = new ArrayList<>(List.of(
				"B", "C", "D", "E",
				"G", "H", "I", "J",
				"L", "M", "N", "O",
				"Q", "R", "S", "T",
				"V", "W", "X", "Y"
		));
		grid.removeFirstColumn();
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getColumnsNum());
	}

	@Test
	public void testRemoveColumn2() {
		List<String> expected = new ArrayList<>(List.of(
				"A", "B", "C", "D",
				"F", "G", "H", "I",
				"K", "L", "M", "N",
				"P", "Q", "R", "S",
				"U", "V", "W", "X"
		));
		grid.removeLastColumn();
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getColumnsNum());
	}

	@Test
	public void testRemoveColumn3() {
		List<String> expected = new ArrayList<>(List.of(
				"A", "C", "D", "E",
				"F", "H", "I", "J",
				"K", "M", "N", "O",
				"P", "R", "S", "T",
				"U", "W", "X", "Y"
		));
		grid.removeColumn(1);
		assertEquals(expected, grid.getData());
		assertEquals(4, grid.getColumnsNum());
	}

	@Test
	public void testRemoveColumn4() {
		assertThrows(IndexOutOfBoundsException.class, () -> grid.removeColumn(10));
	}

	@Test
	public void testTranspose1() {
		List<String> expected = List.of(
				"A", "F", "K", "P", "U",
				"B", "G", "L", "Q", "V",
				"C", "H", "M", "R", "W",
				"D", "I", "N", "S", "X",
				"E", "J", "O", "T", "Y"
		);
		assertEquals(expected, grid.transpose().getData());
	}

	@Test
	public void testTranspose2() {
		String[][] strings = new String[][]{
				{"0", "1", "2", "3", "4"},
				{"5", "6", "7", "8", "9"},
				{"10", "11", "12", "13", "14"}
		};
		grid.init(3, 5, (row, column) -> strings[row][column]);

		List<String> expected = List.of(
				"0", "5", "10",
				"1", "6", "11",
				"2", "7", "12",
				"3", "8", "13",
				"4", "9", "14"
		);
		grid.transpose();
		assertEquals(5, grid.getRowsNum());
		assertEquals(3, grid.getColumnsNum());
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testIterator() {
		int i = 0;
		for (String s : grid) {
			String exp = data.get(i);
			assertEquals(exp, s);
			i++;
		}
	}

	@Test
	public void testRowIterator1() {
		Iterator<List<String>> it = grid.rowIterator();
		int i = 0;
		while (it.hasNext()) {
			List<String> row = it.next();
			assertEquals(row, grid.getRow(i));
			i++;
		}
	}

	@Test
	public void testRowIterator2() {
		Iterator<List<String>> it = grid.rowIterator();
		while (it.hasNext()) {
			it.remove();
		}
		assertTrue(grid.isEmpty());
	}

	@Test
	public void testRowIterator3() {
		List<String> expected = List.of(
				"A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O",
				"U", "V", "W", "X", "Y"
		);
		Iterator<List<String>> it = grid.rowIterator();
		int i = 0;
		while (it.hasNext()) {
			if (i == 3) {
				it.remove();
				return;
			}
			it.next();
			i++;
		}
		assertEquals(4, grid.getRowsNum());
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testColumnIterator1() {
		Iterator<List<String>> it = grid.columnIterator();
		int i = 0;
		while (it.hasNext()) {
			List<String> column = it.next();
			assertEquals(column, grid.getColumn(i));
			i++;
		}
	}

	@Test
	public void testColumnIterator2() {
		Iterator<List<String>> it = grid.columnIterator();
		while (it.hasNext()) {
			it.remove();
		}
		assertTrue(grid.isEmpty());
	}

	@Test
	public void testColumnIterator3() {
		List<String> expected = List.of(
				"A", "B", "C", "D",
				"F", "G", "H", "I",
				"K", "L", "M", "N",
				"P", "Q", "R", "S",
				"U", "V", "W", "X"
		);
		Iterator<List<String>> it = grid.columnIterator();
		int i = 0;
		while (it.hasNext()) {
			if (i == 4) {
				it.remove();
				return;
			}
			it.next();
			i++;
		}
		assertEquals(4, grid.getColumnsNum());
		assertEquals(expected, grid.getData());
	}

	@Test
	public void testLinearToCoordinate() {
		Coordinates p1 = GridUtils.indToSub(grid.getColumnsNum(), 0);
		assertEquals(0, p1.getRow());
		assertEquals(0, p1.getColumn());

		Coordinates p2 = GridUtils.indToSub(grid.getColumnsNum(), 6);
		assertEquals(1, p2.getRow());
		assertEquals(1, p2.getColumn());

		Coordinates p3 = GridUtils.indToSub(grid.getColumnsNum(), 12);
		assertEquals(2, p3.getRow());
		assertEquals(2, p3.getColumn());

		Coordinates p4 = GridUtils.indToSub(grid.getColumnsNum(), 18);
		assertEquals(3, p4.getRow());
		assertEquals(3, p4.getColumn());

		Coordinates p5 = GridUtils.indToSub(grid.getColumnsNum(), 24);
		assertEquals(4, p5.getRow());
		assertEquals(4, p5.getColumn());
	}

	public static Integer[][] getRandomIntMatrix(int rows, int columns) {
		Integer[][] matrix = new Integer[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				matrix[i][j] = RandomUtils.random.nextInt(100);
			}
		}
		return matrix;
	}

	public static <T> List<T> matrixToLinear(T[][] matrix) {
		List<T> tmp = new ArrayList<>();
		for (T[] row : matrix) {
			tmp.addAll(Arrays.asList(row));
		}
		return tmp;
	}
}
