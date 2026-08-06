/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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
import java.util.List;

import io.github.palexdev.mfxcore.collections.CircularQueue.EvictionPolicy;
import io.github.palexdev.mfxcore.collections.ObservableCircularQueue;
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ObservableCircularQueueTest {
    private ObservableCircularQueue<Integer> queue;

    @BeforeEach
    void setup() {
        queue = new ObservableCircularQueue<>(5);
    }

    @Test
    void testAdd() {
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        assertQueue();
    }

    @Test
    void testAddAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll3() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(0, vals);
        assertQueue();
    }

    @Test
    void testSetAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.setAll(vals);
        assertQueue();
    }

    @Test
    void testSetAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.setAll(vals);
        assertQueue();
    }

    @Test
    void testEvictionIsNotified() {
        queue.addAll(0, 1, 2, 3, 4);
        Changes changes = Changes.observe(queue);

        assertTrue(queue.add(5));

        assertEquals(1, changes.notifications);
        assertEquals(List.of(0), changes.removed);
        assertEquals(List.of(5), changes.added);
        assertEquals(List.of(1, 2, 3, 4, 5), queue);
    }

    @Test
    void testNoEvictionWhenNotFull() {
        queue.addAll(0, 1);
        Changes changes = Changes.observe(queue);

        assertTrue(queue.add(2));

        assertEquals(1, changes.notifications);
        assertTrue(changes.removed.isEmpty());
        assertEquals(List.of(2), changes.added);
    }

    @Test
    void testBulkOverflowIsOneChange() {
        Changes changes = Changes.observe(queue);

        assertTrue(queue.addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        assertEquals(1, changes.notifications);
        assertTrue(changes.removed.isEmpty());
        assertEquals(List.of(5, 6, 7, 8, 9), changes.added);
        assertQueue();
    }

    @Test
    void testTailPolicy() {
        queue.setPolicy(EvictionPolicy.TAIL);
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        assertEquals(List.of(0, 1, 2, 3, 4), queue);
    }

    @Test
    void testTailPolicyRejectsWhenFull() {
        queue = new ObservableCircularQueue<>(5, EvictionPolicy.TAIL);
        queue.addAll(0, 1, 2, 3, 4);
        Changes changes = Changes.observe(queue);

        assertFalse(queue.add(5));

        assertEquals(0, changes.notifications);
        assertEquals(List.of(0, 1, 2, 3, 4), queue);
    }

    @Test
    void testTailPolicyEvictsForHeadInsertion() {
        queue = new ObservableCircularQueue<>(5, EvictionPolicy.TAIL);
        queue.addAll(0, 1, 2, 3, 4);
        Changes changes = Changes.observe(queue);

        queue.addFirst(-1);

        assertEquals(1, changes.notifications);
        assertEquals(List.of(4), changes.removed);
        assertEquals(List.of(-1), changes.added);
        assertEquals(List.of(-1, 0, 1, 2, 3), queue);
    }

    @Test
    void testHeadPolicyDropsOwnHeadInsertionWhenFull() {
        queue.addAll(0, 1, 2, 3, 4);
        Changes changes = Changes.observe(queue);

        assertFalse(queue.addAll(0, List.of(-1)));

        assertEquals(0, changes.notifications);
        assertEquals(List.of(0, 1, 2, 3, 4), queue);
    }

    @Test
    void testSetCapacityTrims() {
        queue.addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Changes changes = Changes.observe(queue);

        queue.setCapacity(2);

        assertEquals(2, queue.size());
        assertEquals(List.of(8, 9), queue);
        assertEquals(1, changes.notifications);
        assertEquals(List.of(5, 6, 7), changes.removed);
    }

    @Test
    void testSetCapacityTrimsFromTail() {
        queue = new ObservableCircularQueue<>(5, EvictionPolicy.TAIL);
        queue.addAll(0, 1, 2, 3, 4);

        queue.setCapacity(2);

        assertEquals(List.of(0, 1), queue);
    }

    @Test
    void testSetCapacityGrows() {
        queue.addAll(0, 1, 2, 3, 4);
        queue.setCapacity(7);
        queue.addAll(5, 6);
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), queue);
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ObservableCircularQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ObservableCircularQueue<>(-1));
        assertThrows(IllegalArgumentException.class, () -> queue.setCapacity(0));
    }

    @Test
    void testInvalidIndex() {
        queue.addAll(0, 1);
        assertThrows(IndexOutOfBoundsException.class, () -> queue.add(5, 42));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.add(-1, 42));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.addAll(3, List.of(42)));
    }

    @Test
    void testAddAtIndexWhenNotFull() {
        queue.addAll(0, 1);
        queue.add(1, 42);
        assertEquals(List.of(0, 42, 1), queue);
    }

    @Test
    void testEmptyBulkOps() {
        assertFalse(queue.addAll(List.of()));
        assertFalse(queue.addAll(0, List.of()));
    }

    void assertQueue() {
        assertEquals(5, queue.size());
        for (int i = 5; i < 10; i++) {
            assertEquals(i, queue.get(i - 5));
        }
    }

    private static class Changes {
        private int notifications;
        private final List<Integer> removed = new ArrayList<>();
        private final List<Integer> added = new ArrayList<>();

        static Changes observe(ObservableCircularQueue<Integer> queue) {
            Changes changes = new Changes();
            queue.addListener((ListChangeListener<Integer>) c -> {
                changes.notifications++;
                while (c.next()) {
                    if (c.wasRemoved()) changes.removed.addAll(c.getRemoved());
                    if (c.wasAdded()) changes.added.addAll(c.getAddedSubList());
                }
            });
            return changes;
        }
    }
}
