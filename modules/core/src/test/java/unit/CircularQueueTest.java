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

import java.util.List;

import io.github.palexdev.mfxcore.collections.CircularQueue;
import io.github.palexdev.mfxcore.collections.CircularQueue.EvictionPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CircularQueueTest {

    @Test
    void testAdd() {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        for (int i = 0; i < 10; i++) {
            assertTrue(queue.add(i));
        }
        assertEquals(List.of(5, 6, 7, 8, 9), queue);
    }

    @Test
    void testAddTail() {
        CircularQueue<Integer> queue = new CircularQueue<>(5, EvictionPolicy.TAIL);
        for (int i = 0; i < 5; i++) {
            assertTrue(queue.add(i));
        }
        for (int i = 5; i < 10; i++) {
            assertFalse(queue.add(i));
        }
        assertEquals(List.of(0, 1, 2, 3, 4), queue);
    }

    @Test
    void testDequePathsRespectCapacity() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.push(0);
        queue.offer(1);
        queue.offerFirst(2);
        queue.offerLast(3);
        queue.addFirst(4);
        queue.addLast(5);
        assertEquals(List.of(1, 3, 5), queue);
    }

    @Test
    void testHeadInsertionIsDroppedWhenFull() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.addAll(List.of(1, 2, 3));
        queue.push(0);
        assertEquals(List.of(1, 2, 3), queue);
    }

    @Test
    void testTailInsertionIsDroppedWhenFull() {
        CircularQueue<Integer> queue = new CircularQueue<>(3, EvictionPolicy.TAIL);
        queue.addAll(List.of(1, 2, 3));
        queue.offerLast(4);
        assertEquals(List.of(1, 2, 3), queue);
    }

    @Test
    void testAddAll() {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        assertTrue(queue.addAll(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertEquals(List.of(5, 6, 7, 8, 9), queue);
    }

    @Test
    void testAddAllAtIndex() {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        queue.addAll(List.of(0, 1, 2, 3, 4));
        assertTrue(queue.addAll(2, List.of(7, 8)));
        assertEquals(List.of(7, 8, 2, 3, 4), queue);
    }

    @Test
    void testAddAllAtHeadWhenFull() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.addAll(List.of(1, 2, 3));
        assertFalse(queue.addAll(0, List.of(7, 8, 9)));
        assertEquals(List.of(1, 2, 3), queue);
    }

    @Test
    void testSetCapacityTrimsFully() {
        CircularQueue<Integer> queue = new CircularQueue<>(10);
        queue.addAll(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        queue.setCapacity(4);
        assertEquals(4, queue.size());
        assertEquals(List.of(6, 7, 8, 9), queue);
    }

    @Test
    void testSetCapacityTrimsFromTail() {
        CircularQueue<Integer> queue = new CircularQueue<>(10, EvictionPolicy.TAIL);
        queue.addAll(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        queue.setCapacity(4);
        assertEquals(List.of(0, 1, 2, 3), queue);
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<>(-1));
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<>(5).setCapacity(0));
    }

    @Test
    void testInvalidIndex() {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        queue.addAll(List.of(0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.add(5, 42));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.addAll(3, List.of(42)));
    }
}
