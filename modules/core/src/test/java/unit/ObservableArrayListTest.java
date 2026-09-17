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

package unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import io.github.palexdev.mfxcore.collections.ObservableArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObservableArrayListTest {

    @Test
    void testSort() {
        ObservableArrayList<Integer> list = new ObservableArrayList<>(List.of(5, 3, 9, 1, 7));
        Changes changes = Changes.observe(list);

        list.sort(Comparator.naturalOrder());

        assertEquals(List.of(1, 3, 5, 7, 9), list);
        assertEquals(1, changes.notifications);
        assertEquals(1, changes.permutations);
        assertEquals(0, changes.addsOrRemoves);
        assertEquals(0, changes.from);
        assertEquals(5, changes.to);
        assertEquals(0, changes.mismatches);
    }

    @Test
    void testSortNullComparator() {
        ObservableArrayList<Integer> list = new ObservableArrayList<>(List.of(5, 3, 9, 1, 7));
        Changes changes = Changes.observe(list);

        list.sort(null);

        assertEquals(List.of(1, 3, 5, 7, 9), list);
        assertEquals(1, changes.permutations);
        assertEquals(0, changes.mismatches);
    }

    @Test
    void testSortIsStable() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("bb", "a1", "cc", "a2", "dd", "a3"));
        Changes changes = Changes.observe(list);

        list.sort(Comparator.comparingInt(String::length).thenComparing(s -> s.charAt(0)));

        assertEquals(List.of("a1", "a2", "a3", "bb", "cc", "dd"), list);
        assertEquals(0, changes.mismatches);
    }

    @Test
    void testSortMatchesFXList() {
        List<Integer> values = List.of(8, 3, 3, 12, 0, 7, 1, 9, 3, 5, 11, 2, 6, 10, 4);
        Comparator<Integer> comparator = Comparator.comparingInt(i -> i % 4);

        ObservableArrayList<Integer> list = new ObservableArrayList<>(values);
        Changes changes = Changes.observe(list);
        list.sort(comparator);

        ObservableList<Integer> fxList = FXCollections.observableArrayList(values);
        Changes fxChanges = Changes.observe(fxList);
        fxList.sort(comparator);

        assertEquals(fxList, list);
        assertEquals(fxChanges.notifications, changes.notifications);
        assertEquals(fxChanges.permutations, changes.permutations);
        assertEquals(0, changes.mismatches);
        assertEquals(0, fxChanges.mismatches);
    }

    @Test
    void testSortLargeMatchesStableSort() {
        Random random = new Random(42);
        List<int[]> values = new ArrayList<>();
        for (int i = 0; i < 5000; i++) values.add(new int[]{random.nextInt(16), i});
        Comparator<int[]> comparator = Comparator.comparingInt(a -> a[0]);

        ObservableArrayList<int[]> list = new ObservableArrayList<>(values);
        Changes changes = Changes.observe(list);
        list.sort(comparator);

        List<int[]> expected = new ArrayList<>(values);
        expected.sort(comparator);
        for (int i = 0; i < expected.size(); i++) {
            assertSame(expected.get(i), list.get(i));
        }
        assertEquals(1, changes.notifications);
        assertEquals(1, changes.permutations);
        assertEquals(0, changes.mismatches);
    }

    @Test
    void testSortAlreadySorted() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 100; i++) values.add(i);
        ObservableArrayList<Integer> list = new ObservableArrayList<>(values);
        Changes changes = Changes.observe(list);

        list.sort(null);

        assertEquals(values, list);
        assertEquals(1, changes.permutations);
        for (int k = 0; k < changes.perm.length; k++) assertEquals(k, changes.perm[k]);
        assertEquals(0, changes.mismatches);
    }

    @Test
    void testSortTrivial() {
        ObservableArrayList<Integer> list = new ObservableArrayList<>(List.of(1));
        Changes changes = Changes.observe(list);

        list.sort(null);

        assertEquals(0, changes.notifications);
    }

    @Test
    void testClear() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C"));
        Changes changes = Changes.observe(list);

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(1, changes.notifications);
        assertEquals(List.of("A", "B", "C"), changes.removed);
    }

    @Test
    void testClearWithoutListeners() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C"));
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void testRemoveRange() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C", "D", "E"));
        Changes changes = Changes.observe(list);

        list.remove(1, 4);

        assertEquals(List.of("A", "E"), list);
        assertEquals(1, changes.notifications);
        assertEquals(List.of("B", "C", "D"), changes.removed);
    }

    @Test
    void testRemoveAll() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C", "D", "E"));
        Changes changes = Changes.observe(list);

        assertTrue(list.removeAll(List.of("B", "D", "X")));

        assertEquals(List.of("A", "C", "E"), list);
        assertEquals(1, changes.notifications);
        assertEquals(2, changes.removed.size());
        assertTrue(changes.removed.containsAll(List.of("B", "D")));
    }

    @Test
    void testRemoveAllNoMatch() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B"));
        Changes changes = Changes.observe(list);

        assertFalse(list.removeAll(List.of("X")));
        assertFalse(list.removeAll(List.of()));

        assertEquals(0, changes.notifications);
    }

    @Test
    void testRetainAll() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C", "D", "E"));
        Changes changes = Changes.observe(list);

        assertTrue(list.retainAll(List.of("B", "D")));

        assertEquals(List.of("B", "D"), list);
        assertEquals(1, changes.notifications);
        assertEquals(3, changes.removed.size());
    }

    @Test
    void testRetainAllEmpty() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B"));
        Changes changes = Changes.observe(list);

        assertTrue(list.retainAll(List.of()));

        assertTrue(list.isEmpty());
        assertEquals(1, changes.notifications);
        assertFalse(new ObservableArrayList<String>().retainAll(List.of("A")));
    }

    @Test
    void testSetAllIsOneChange() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A", "B", "C"));
        Changes changes = Changes.observe(list);

        list.setAll("C", "B", "A");

        assertEquals(List.of("C", "B", "A"), list);
        assertEquals(1, changes.notifications);
    }

    @Test
    void testAddOutOfBounds() {
        ObservableArrayList<String> list = new ObservableArrayList<>(List.of("A"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "B"));
    }

    private static class Changes {
        private int notifications;
        private int permutations;
        private int addsOrRemoves;
        private int mismatches;
        private int from = -1;
        private int to = -1;
        private int[] perm;
        private final List<Object> removed = new ArrayList<>();

        static <E> Changes observe(ObservableList<E> list) {
            Changes changes = new Changes();
            List<E> snapshot = new ArrayList<>(list);
            list.addListener((ListChangeListener<E>) c -> {
                changes.notifications++;
                while (c.next()) {
                    if (c.wasPermutated()) {
                        changes.permutations++;
                        changes.from = c.getFrom();
                        changes.to = c.getTo();
                        changes.perm = new int[c.getTo() - c.getFrom()];
                        for (int i = c.getFrom(); i < c.getTo(); i++) {
                            changes.perm[i - c.getFrom()] = c.getPermutation(i);
                            if (snapshot.get(i) != c.getList().get(c.getPermutation(i))) changes.mismatches++;
                        }
                    } else if (c.wasAdded() || c.wasRemoved()) {
                        changes.addsOrRemoves++;
                        changes.removed.addAll(c.getRemoved());
                    }
                }
                snapshot.clear();
                snapshot.addAll(c.getList());
            });
            return changes;
        }
    }
}
