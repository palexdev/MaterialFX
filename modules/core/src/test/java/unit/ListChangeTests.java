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

package unit;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.utils.fx.FXCollectors;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static io.github.palexdev.mfxcore.utils.fx.ListChangeHelper.shiftOnAdd;
import static io.github.palexdev.mfxcore.utils.fx.ListChangeHelper.shiftOnRemove;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// These tests are based on a hypothetical selection model
public class ListChangeTests {
    private final ObservableList<String> source = IntStream.range(0, 100)
        .mapToObj(i -> "String " + (i + 1))
        .collect(FXCollectors.toList());
    private final Map<Integer, String> selection = getMap(
        0, "String 1",
        8, "String 9",
        9, "String 10",
        3, "String 4",
        5, "String 6",
        23, "String 24",
        24, "String 25",
        68, "String 69",
        99, "String 100",
        98, "String 99"
    );

    {
        new ListChangeHelper<>(source)
            .setOnClear(selection::clear)
            .setOnPermutation(permutations -> {
                Set<Integer> src = new LinkedHashSet<>(selection.keySet());
                selection.clear();
                for (Integer origin : src) {
                    Integer updated = permutations.get(origin);
                    if (updated != null)
                        selection.put(updated, source.get(updated));
                }
            })
            .setOnReplace(replaced -> {
                if (!selection.containsKey(replaced)) return;
                selection.put(replaced, source.get(replaced));
            })
            .setOnRemoved(removed -> {
                List<Integer> updated = shiftOnRemove(selection.keySet(), removed, removed.first());
                selection.clear();
                for (Integer i : updated) selection.put(i, source.get(i));
            })
            .setOnAdded(range -> {
                List<Integer> updated = shiftOnAdd(selection.keySet(), range);
                selection.clear();
                for (Integer i : updated) selection.put(i, source.get(i));
            })
            .init();
    }

    @Test
    void testAdd0() {
        source.addAll(0, List.of("Add0", "Add1", "Add2"));
        Map<Integer, String> expected = getMap(
            3, "String 1",
            11, "String 9",
            12, "String 10",
            6, "String 4",
            8, "String 6",
            26, "String 24",
            27, "String 25",
            71, "String 69",
            102, "String 100",
            101, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testAddMiddle() {
        source.addAll(source.size() / 2, List.of("AddMiddle0", "AddMiddle1", "AddMiddle2", "AddMiddle3"));
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            72, "String 69",
            103, "String 100",
            102, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testAddEnd() {
        source.addAll(source.size() - 1, List.of("AddEnd0", "AddEnd1"));
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            68, "String 69",
            101, "String 100",
            98, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemove0() {
        removeAll(IntegerRange.of(0, 2));
        Map<Integer, String> expected = getMap(
            5, "String 9",
            6, "String 10",
            0, "String 4",
            2, "String 6",
            20, "String 24",
            21, "String 25",
            65, "String 69",
            96, "String 100",
            95, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemoveMiddle() {
        removeAll(IntegerRange.of(48, 52));
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            63, "String 69",
            94, "String 100",
            93, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemoveMiddle2() {
        removeAll(IntegerRange.of(20, 70));
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            48, "String 100",
            47, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemoveEnd() {
        removeAll(IntegerRange.of(70, 99));
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            68, "String 69"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemoveSparse() {
        removeAll(0, 4, 6, 8, 10, 22, 44, 65, 70, 75, 79, 80, 81, 82, 90, 99);
        Map<Integer, String> expected = getMap(
            5, "String 10",
            2, "String 4",
            3, "String 6",
            17, "String 24",
            18, "String 25",
            60, "String 69",
            83, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testRemoveIf() {
        source.removeIf(s -> Integer.parseInt(s.split(" ")[1]) > 70);
        Map<Integer, String> expected = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            68, "String 69"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testSet() {
        source.set(25, "Replaced 25");
        Map<Integer, String> expected1 = getMap(
            0, "String 1",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            68, "String 69",
            99, "String 100",
            98, "String 99"
        );
        assertEquals(expected1, selection);

        source.set(0, "Replaced 0");
        Map<Integer, String> expected2 = getMap(
            0, "Replaced 0",
            8, "String 9",
            9, "String 10",
            3, "String 4",
            5, "String 6",
            23, "String 24",
            24, "String 25",
            68, "String 69",
            99, "String 100",
            98, "String 99"
        );
        assertEquals(expected2, selection);
    }

    @Test
    void testSetAll() {
        List<String> replacement = IntStream.range(0, 50)
            .mapToObj(i -> "Replaced " + (i + 1))
            .toList();
        source.setAll(replacement);
        assertTrue(selection.isEmpty());
    }

    @Test
    void testSetAll2() {
        source.setAll("Replaced All");
        assertTrue(selection.isEmpty());
    }

    @Test
    void testReplaceAll() {
        source.replaceAll(s -> s.split(" ")[1]);
        Map<Integer, String> expected = getMap(
            0, "1",
            8, "9",
            9, "10",
            3, "4",
            5, "6",
            23, "24",
            24, "25",
            68, "69",
            99, "100",
            98, "99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testSort() {
        FXCollections.sort(source, Comparator.comparing(
            s -> Integer.parseInt(s.split(" ")[1]),
            Comparator.reverseOrder()
        ));
        Map<Integer, String> expected = getMap(
            99, "String 1",
            91, "String 9",
            90, "String 10",
            96, "String 4",
            94, "String 6",
            76, "String 24",
            75, "String 25",
            31, "String 69",
            0, "String 100",
            1, "String 99"
        );
        assertEquals(expected, selection);
    }

    @Test
    void testClear() {
        source.clear();
        assertTrue(selection.isEmpty());
    }

    Map<Integer, String> getMap(Object... vals) {
        Map<Integer, String> map = new LinkedHashMap<>();
        assert vals.length % 2 == 0;
        for (int i = 0; i < vals.length; i += 2) {
            map.put((Integer) vals[i], (String) vals[i + 1]);
        }
        return map;
    }

    void removeAll(IntegerRange range) {
        removeAll(IntegerRange.expandRangeToArray(range.getMin(), range.getMax()));
    }

    void removeAll(Integer... indexes) {
        List<String> collect = Arrays.stream(indexes)
            .map(source::get)
            .toList();
        source.removeAll(collect);
    }
}
