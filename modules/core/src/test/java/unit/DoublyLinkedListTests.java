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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.github.palexdev.mfxcore.collections.DoublyLinkedList;
import io.github.palexdev.mfxcore.collections.DoublyLinkedList.Elem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoublyLinkedListTests {

    /// Walks the entire backing list and asserts every prev/next pointer is
    /// consistent. This covers the structural invariant that the individual link
    /// tests (testPushLinks, testOfferLinks, etc.) only verify locally.
    private void assertLinksValid(DoublyLinkedList<?> list) {
        List<? extends Elem<?>> backing = list.backingList();
        for (int i = 0; i < backing.size(); i++) {
            Elem<?> elem = backing.get(i);

            if (i == 0)
                assertNull(elem.prev(), "head.prev must be null");
            else
                assertSame(backing.get(i - 1), elem.prev(),
                    "elem[" + i + "].prev must point to elem[" + (i - 1) + "]");

            if (i == backing.size() - 1)
                assertNull(elem.next(), "tail.next must be null");
            else
                assertSame(backing.get(i + 1), elem.next(),
                    "elem[" + i + "].next must point to elem[" + (i + 1) + "]");
        }
    }

    @Test
    void testEmptyList() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertNull(list.head());
        assertNull(list.tail());
        assertFalse(list.contains("a"));
        assertFalse(list.contains(null));
    }

    @Test
    void testHeadAndTail() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        assertEquals("a", list.head().value());
        assertEquals("a", list.tail().value());
        assertSame(list.head(), list.tail());

        list.offer("b");
        assertEquals("a", list.head().value());
        assertEquals("b", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testGet() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        assertEquals("a", list.get(0).value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.get(2).value());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void testContains() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        assertTrue(list.contains("a"));
        assertTrue(list.contains("b"));
        assertFalse(list.contains("c"));
    }

    @Test
    void testContainsNull() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer(null);
        assertTrue(list.contains(null));
        assertLinksValid(list);
    }

    @Test
    void testPush() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.push("a");
        assertEquals(1, list.size());
        assertEquals("a", list.head().value());
        assertEquals("a", list.tail().value());

        list.push("b");
        assertEquals(2, list.size());
        assertEquals("b", list.head().value());
        assertEquals("a", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testPushLinks() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.push("a");
        list.push("b");
        Elem<String> b = list.head();
        Elem<String> a = list.tail();
        assertNull(b.prev());
        assertSame(a, b.next());
        assertSame(b, a.prev());
        assertNull(a.next());
        assertLinksValid(list);
    }

    @Test
    void testOffer() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        assertEquals("a", list.head().value());
        assertEquals("b", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testOfferLinks() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Elem<String> a = list.head();
        Elem<String> b = list.tail();
        assertNull(a.prev());
        assertSame(b, a.next());
        assertSame(a, b.prev());
        assertNull(b.next());
        assertLinksValid(list);
    }

    @Test
    void testOfferOnEmptyIsSameAsPush() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        assertEquals("a", list.head().value());
        assertEquals("a", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testPop() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        assertNull(list.pop());

        list.offer("a");
        list.offer("b");
        assertEquals("a", list.pop().value());
        assertEquals(1, list.size());
        assertEquals("b", list.head().value());
        assertEquals("b", list.tail().value());
        assertLinksValid(list); // one element remains

        assertEquals("b", list.pop().value());
        assertTrue(list.isEmpty());
        // no assertLinksValid: empty list, loop exits immediately
    }

    @Test
    void testPopClearsLinks() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Elem<String> a = list.pop();
        assertNull(a.next());
        assertNull(a.prev());
        assertLinksValid(list); // "b" is the only remaining node
    }

    @Test
    void testPoll() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        assertNull(list.poll());

        list.offer("a");
        list.offer("b");
        assertEquals("b", list.poll().value());
        assertEquals(1, list.size());
        assertEquals("a", list.head().value());
        assertEquals("a", list.tail().value());
        assertLinksValid(list); // one element remains

        assertEquals("a", list.poll().value());
        assertTrue(list.isEmpty());
        // no assertLinksValid: empty list, loop exits immediately
    }

    @Test
    void testPollClearsLinks() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Elem<String> b = list.poll();
        assertNull(b.next());
        assertNull(b.prev());
        assertLinksValid(list); // "a" is the only remaining node
    }

    @Test
    void testAddAtIndexFront() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.add(0, "a");
        list.add(0, "b");
        assertEquals("b", list.head().value());
        assertEquals("a", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testAddAtIndexBack() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.add(0, "a");
        list.add(1, "b");
        assertEquals("a", list.head().value());
        assertEquals("b", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testAddAtIndexMiddle() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("c");
        list.add(1, "b");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0).value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.get(2).value());
        assertLinksValid(list);
    }

    @Test
    void testAddAtIndexLinks() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("c");
        list.add(1, "b");
        Elem<String> a = list.get(0);
        Elem<String> b = list.get(1);
        Elem<String> c = list.get(2);
        assertSame(b, a.next());
        assertSame(c, b.next());
        assertSame(a, b.prev());
        assertSame(b, c.prev());
        assertLinksValid(list);
    }

    @Test
    void testAddAtIndexThrowsOnBadIndex() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, "a"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "a"));
    }

    @Test
    void testAddAllVarargs() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.addAll("a", "b", "c");
        assertEquals(3, list.size());
        assertEquals("a", list.head().value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testAddAllCollection() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.addAll(List.of("a", "b", "c"));
        assertEquals(3, list.size());
        assertEquals("a", list.head().value());
        assertEquals("c", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testAddAllAtIndex() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("d");
        list.addAll(1, "b", "c");
        assertEquals(4, list.size());
        assertEquals("a", list.get(0).value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.get(2).value());
        assertEquals("d", list.get(3).value());
        assertLinksValid(list);
    }

    @Test
    void testAddAllAtIndexPreservesOrder() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.addAll(0, "a", "b", "c");
        assertEquals("a", list.get(0).value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.get(2).value());
        assertLinksValid(list);
    }

    @Test
    void testAddAllAtIndexCollection() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("d");
        list.addAll(1, List.of("b", "c"));
        assertEquals(4, list.size());
        assertEquals("a", list.get(0).value());
        assertEquals("b", list.get(1).value());
        assertEquals("c", list.get(2).value());
        assertEquals("d", list.get(3).value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveByIndex() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        assertEquals("b", list.remove(1).value());
        assertEquals(2, list.size());
        assertEquals("a", list.head().value());
        assertEquals("c", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveByIndexFront() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        assertEquals("a", list.remove(0).value());
        assertEquals(1, list.size());
        assertEquals("b", list.head().value());
        assertEquals("b", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveByIndexBack() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        assertEquals("b", list.remove(1).value());
        assertEquals(1, list.size());
        assertEquals("a", list.head().value());
        assertEquals("a", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveByIndexThrowsOnBadIndex() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1)); // at index 0 returns null because calls pop internally
    }

    @Test
    void testRemoveByValue() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        assertTrue(list.remove("b"));
        assertEquals(2, list.size());
        assertFalse(list.contains("b"));
        assertLinksValid(list);
    }

    @Test
    void testRemoveByValueNotFound() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        assertFalse(list.remove("x"));
        assertEquals(2, list.size());
        assertLinksValid(list);
    }

    @Test
    void testRemoveByValueFirstOccurrence() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("a");
        assertTrue(list.remove("a"));
        assertEquals(2, list.size());
        assertTrue(list.contains("a"));
        assertEquals("b", list.get(0).value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveAllVarargs() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("a");
        list.removeAll("a", "b");
        assertTrue(list.isEmpty());
        // no assertLinksValid: empty list, loop exits immediately
    }

    @Test
    void testRemoveAllCollection() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.removeAll(List.of("a", "c"));
        assertEquals(1, list.size());
        assertEquals("b", list.head().value());
        assertLinksValid(list);
    }

    // removeAll edge cases: the two tests above cover the happy path but do not
    // verify link integrity or boundary conditions, so we add those here.

    @Test
    void testRemoveAllVarargsLinksAreValid() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.offer("b");
        list.offer("d");
        list.removeAll("b", "d");

        assertEquals(2, list.size());
        assertEquals("a", list.head().value());
        assertEquals("c", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveAllCollectionLinksAreValid() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.offer("b");
        list.offer("d");
        list.removeAll(List.of("b", "d"));

        assertEquals(2, list.size());
        assertEquals("a", list.head().value());
        assertEquals("c", list.tail().value());
        assertLinksValid(list);
    }

    @Test
    void testRemoveAllNoMatch() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.removeAll(List.of("x", "y"));

        assertEquals(3, list.size());
        assertLinksValid(list);
    }

    @Test
    void testRemoveAllAllElements() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.removeAll(List.of("a", "b", "c"));

        assertTrue(list.isEmpty());
        // no assertLinksValid: empty list, loop exits immediately
    }

    @Test
    void testRemoveAllEmptyInput() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.removeAll(List.of());

        assertEquals(2, list.size());
        assertLinksValid(list);
    }

    @Test
    void testClear() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertNull(list.head());
        assertNull(list.tail());
    }

    @Test
    void testIterator() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        Iterator<Elem<String>> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next().value());
        assertTrue(it.hasNext());
        assertEquals("b", it.next().value());
        assertTrue(it.hasNext());
        assertEquals("c", it.next().value());
        assertFalse(it.hasNext());
    }

    @Test
    void testIteratorThrowsWhenExhausted() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        Iterator<Elem<String>> it = list.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testDescendingIterator() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        Iterator<Elem<String>> it = list.descendingIterator();
        assertTrue(it.hasNext());
        assertEquals("c", it.next().value());
        assertTrue(it.hasNext());
        assertEquals("b", it.next().value());
        assertTrue(it.hasNext());
        assertEquals("a", it.next().value());
        assertFalse(it.hasNext());
    }

    @Test
    void testDescendingIteratorThrowsWhenExhausted() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        Iterator<Elem<String>> it = list.descendingIterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testToArray() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Object[] arr = list.toArray();
        assertEquals(2, arr.length);
        assertEquals("a", ((Elem<?>) arr[0]).value());
        assertEquals("b", ((Elem<?>) arr[1]).value());
    }

    @Test
    void testToArrayTyped() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Elem<String>[] arr = list.toArray(new Elem[0]);
        assertEquals(2, arr.length);
        assertEquals("a", arr[0].value());
        assertEquals("b", arr[1].value());
    }

    @Test
    void testBackingListIsUnmodifiable() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        List<Elem<String>> bl = list.backingList();
        assertEquals(1, bl.size());
        assertThrows(UnsupportedOperationException.class, () -> bl.add(new Elem<>("x")));
        assertThrows(UnsupportedOperationException.class, () -> bl.remove(0));
    }

    @Test
    void testLinkIntegrityPushPopMix() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.push("z");
        list.pop();
        list.poll();

        assertEquals(1, list.size());
        assertEquals("a", list.head().value());
        assertSame(list.head(), list.tail());
        assertNull(list.head().next());
        assertNull(list.head().prev());
        assertLinksValid(list);
    }

    @Test
    void testLinkIntegrityAfterMiddleRemoval() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.offer("d");

        list.remove(1);
        list.remove(1);

        assertEquals(2, list.size());
        assertEquals("a", list.head().value());
        assertEquals("d", list.tail().value());
        assertSame(list.tail(), list.head().next());
        assertSame(list.head(), list.tail().prev());
        assertNull(list.head().prev());
        assertNull(list.tail().next());
        assertLinksValid(list);
    }

    @Test
    void testLargeNumberOfElements() {
        DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
        int n = 1000;
        for (int i = 0; i < n; i++) {
            list.offer(i);
        }
        assertEquals(n, list.size());
        assertEquals(0, list.head().value());
        assertEquals(n - 1, list.tail().value());
        assertLinksValid(list); // full structural check after population; not repeated inside the pop loop below (would be O(n²))

        for (int i = 0; i < n; i++) {
            assertEquals(i, list.get(i).value());
        }

        for (int i = 0; i < n; i++) {
            assertEquals(i, list.pop().value());
        }
        assertTrue(list.isEmpty());
    }

    @Test
    void testContainsAfterRemoval() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        list.offer("c");
        list.remove("b");
        assertFalse(list.contains("b"));
        assertTrue(list.contains("a"));
        assertTrue(list.contains("c"));
        assertLinksValid(list);
    }

    @Test
    void testIteratorAndDescendingAreIndependent() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.offer("a");
        list.offer("b");
        Iterator<Elem<String>> fwd = list.iterator();
        Iterator<Elem<String>> rev = list.descendingIterator();
        assertEquals("a", fwd.next().value());
        assertEquals("b", fwd.next().value());
        assertEquals("b", rev.next().value());
        assertEquals("a", rev.next().value());
    }
}