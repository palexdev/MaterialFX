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

package interactive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.palexdev.mfxcore.collections.RefineList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;

import static org.junit.jupiter.api.Assertions.*;

class RefineListTests {

    private RefineList<Integer> list;

    @BeforeAll
    static void init() {
        if (!FxToolkit.isFXApplicationThreadRunning())
            Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        list = new RefineList<>(FXCollections.observableArrayList(5, 3, 1, 4, 2));
    }

    //================================================================================
    // Read/Write Contract
    //================================================================================

    @Test
    void readsDelegateToView() {
        list.setPredicate(n -> n > 2);
        // src has 5 elements, view should expose only 3
        assertEquals(3, list.size());
    }

    @Test
    void writesGoToSource() {
        list.setPredicate(n -> n > 2);
        list.add(10);
        // src now has 6 elements, view should expose 4 (> 2)
        assertEquals(4, list.size());
        assertTrue(list.getSource().contains(10));
    }

    @Test
    void sizeReflectsViewNotSource() {
        assertEquals(5, list.getSource().size());
        list.setPredicate(n -> n % 2 == 0);
        assertEquals(2, list.size());         // view: [2, 4]
        assertEquals(5, list.getSource().size()); // src untouched
    }

    @Test
    void getReflectsView() {
        list.setComparator(Comparator.naturalOrder());
        assertEquals(1, list.get(0));
        assertEquals(5, list.get(4));
    }

    @Test
    void removeByObjectDelegatesToSource() {
        list.remove(Integer.valueOf(3));
        assertFalse(list.getSource().contains(3));
        assertEquals(4, list.getSource().size());
    }

    //================================================================================
    // Filtering
    //================================================================================

    @Test
    void predicateFiltersView() {
        list.setPredicate(n -> n > 3);
        assertEquals(2, list.size());
        assertTrue(list.containsAll(List.of(4, 5)));
    }

    @Test
    void clearingPredicateRestoresAll() {
        list.setPredicate(n -> n > 3);
        list.setPredicate(null);
        assertEquals(5, list.size());
    }

    @Test
    void predicateDoesNotMutateSource() {
        list.setPredicate(n -> n > 3);
        assertEquals(5, list.getSource().size());
        assertTrue(list.getSource().containsAll(List.of(1, 2, 3, 4, 5)));
    }

    //================================================================================
    // Sorting
    //================================================================================

    @Test
    void comparatorSortsView() {
        list.setComparator(Comparator.naturalOrder());
        assertEquals(List.of(1, 2, 3, 4, 5), new ArrayList<>(list));
    }

    @Test
    void reverseComparatorSortsDescending() {
        list.setComparator(Comparator.reverseOrder());
        assertEquals(List.of(5, 4, 3, 2, 1), new ArrayList<>(list));
    }

    @Test
    void clearingComparatorRestoresSourceOrder() {
        list.setComparator(Comparator.naturalOrder());
        list.setComparator(null);
        assertEquals(new ArrayList<>(list.getSource()), new ArrayList<>(list));
    }

    @Test
    void comparatorDoesNotMutateSource() {
        list.setComparator(Comparator.naturalOrder());
        assertEquals(List.of(5, 3, 1, 4, 2), list.getSource());
    }

    //================================================================================
    // Filter + Sort Combined
    //================================================================================

    @Test
    void filterAndSortCombined() {
        list.setPredicate(n -> n > 2);
        list.setComparator(Comparator.naturalOrder());
        assertEquals(List.of(3, 4, 5), new ArrayList<>(list));
    }

    @Test
    void filterAndSortSizeIsCorrect() {
        list.setPredicate(n -> n % 2 != 0);  // odd: 1, 3, 5
        list.setComparator(Comparator.reverseOrder());
        assertEquals(3, list.size());
        assertEquals(List.of(5, 3, 1), new ArrayList<>(list));
    }

    //================================================================================
    // Index Mapping
    //================================================================================

    @Test
    void sourceToViewWithSortOnly() {
        // src: [5, 3, 1, 4, 2], sorted asc: [1, 2, 3, 4, 5]
        // src index 2 = value 1 → view index 0
        list.setComparator(Comparator.naturalOrder());
        assertEquals(0, list.sourceToView(2));
    }

    @Test
    void viewToSourceWithSortOnly() {
        // sorted asc: [1, 2, 3, 4, 5]
        // view index 0 = value 1 → src index 2
        list.setComparator(Comparator.naturalOrder());
        assertEquals(2, list.viewToSource(0));
    }

    @Test
    void sourceToViewWithFilterUsesFilteredIndex() {
        // src: [5, 3, 1, 4, 2], predicate n > 2 → filtered: [5, 3, 4]
        // src index 1 = value 3 → filtered view index 1
        list.setPredicate(n -> n > 2);
        assertEquals(1, list.sourceToView(1));
    }

    @Test
    void sourceToViewFilteredOutElementReturnsNegative() {
        // src index 2 = value 1 → filtered out
        list.setPredicate(n -> n > 2);
        assertTrue(list.sourceToView(2) < 0);
    }

    @Test
    void sourceToViewWithFilterAndSort() {
        // src: [5, 3, 1, 4, 2]
        // filter n > 2 → filtered: [5(src0), 3(src1), 4(src3)]
        // naturalOrder   → view:    [3,       4,       5      ]
        list.setPredicate(n -> n > 2);
        list.setComparator(Comparator.naturalOrder());

        assertEquals(0, list.sourceToView(1)); // 3 → view 0
        assertEquals(1, list.sourceToView(3)); // 4 → view 1
        assertEquals(2, list.sourceToView(0)); // 5 → view 2
    }

    @Test
    void sourceToViewFilteredOutElementIgnoresSortWhenBothActive() {
        list.setPredicate(n -> n > 2);
        list.setComparator(Comparator.naturalOrder());
        assertTrue(list.sourceToView(2) < 0); // 1 is filtered out
        assertTrue(list.sourceToView(4) < 0); // 2 is filtered out
    }

    @Test
    void sourceToViewAndViewToSourceAreInversesWithFilterAndSort() {
        // For every view index i, sourceToView(viewToSource(i)) must equal i.
        list.setPredicate(n -> n > 2);
        list.setComparator(Comparator.naturalOrder());

        for (int viewIdx = 0; viewIdx < list.size(); viewIdx++) {
            int srcIdx = list.viewToSource(viewIdx);
            assertEquals(viewIdx, list.sourceToView(srcIdx),
                "round-trip failed at view index " + viewIdx);
        }
    }

    //================================================================================
    // Observability
    //================================================================================

    @Test
    void listenersFireOnAdd() {
        AtomicInteger changeCount = new AtomicInteger();
        list.addListener((ListChangeListener<Integer>) c -> changeCount.incrementAndGet());
        list.add(6);
        assertEquals(1, changeCount.get());
    }

    @Test
    void listenersReflectViewAfterFilter() {
        List<Integer> observed = new ArrayList<>();
        list.setPredicate(n -> n > 2);
        list.addListener((ListChangeListener<Integer>) c -> {
            while (c.next()) {
                if (c.wasAdded()) observed.addAll(c.getAddedSubList());
            }
        });
        list.add(4);  // passes filter, should appear in change
        list.add(1);  // filtered out, should NOT appear in change
        assertEquals(List.of(4), observed);
    }

    @Test
    void removeListenerStopsNotifications() {
        AtomicInteger changeCount = new AtomicInteger();
        ListChangeListener<Integer> listener = c -> changeCount.incrementAndGet();
        list.addListener(listener);
        list.add(6);
        list.removeListener(listener);
        list.add(7);
        assertEquals(1, changeCount.get());
    }
}