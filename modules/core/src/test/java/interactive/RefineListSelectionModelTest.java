package interactive;

import java.util.Comparator;
import java.util.List;

import io.github.palexdev.mfxcore.collections.RefineList;
import io.github.palexdev.mfxcore.selection.model.SelectionModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class RefineListSelectionModelTest {

    private RefineList<String> list;
    private SelectionModel<String> model;

    @BeforeAll
    static void initJfx() {
        if (!FxToolkit.isFXApplicationThreadRunning())
            Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        ObservableList<String> src = FXCollections.observableArrayList(
            "apple", "banana", "cherry", "date", "elderberry"
        );
        list = new RefineList<>(src);
        model = new SelectionModel<>(list);
    }

    @AfterEach
    void tearDown() {
        model.dispose();
    }

    // -------------------------------------------------------------------------
    // Baseline: no filter, no comparator
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("1. selectIndex - no filter, no sort")
    void selectIndex_noTransformation() {
        model.selectIndex(2); // "cherry"
        assertTrue(model.contains(2));
        assertTrue(model.contains("cherry"));
        assertEquals(List.of("cherry"), model.getSelectedItems());
    }

    @Test
    @DisplayName("2. selectItem - no filter, no sort")
    void selectItem_noTransformation() {
        model.selectItem("date"); // src index 3
        assertTrue(model.contains(3));
        assertTrue(model.contains("date"));
    }

    // -------------------------------------------------------------------------
    // Filter active
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("3. Filter hides some items - view indices differ from source indices")
    void filter_viewAndSourceIndicesDiffer() {
        // contains("e") → view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        list.setPredicate(s -> s.contains("e"));

        assertEquals(4, list.size());
        assertEquals("apple", list.get(0));
        assertEquals("cherry", list.get(1));
        assertEquals("date", list.get(2));
        assertEquals("elderberry", list.get(3));

        // Select "elderberry" by its SOURCE index (4)
        model.selectIndex(4);
        assertTrue(model.contains(4));
        assertTrue(model.contains("elderberry"));
    }

    @Test
    @DisplayName("4. sourceToView correctly maps source index when filter is active")
    void sourceToView_withFilter() {
        // view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        list.setPredicate(s -> s.contains("e"));

        assertEquals(0, list.sourceToView(0)); // apple  → view 0
        assertEquals(1, list.sourceToView(2)); // cherry → view 1
        assertEquals(2, list.sourceToView(3)); // date   → view 2
        assertEquals(3, list.sourceToView(4)); // elderberry → view 3
    }

    @Test
    @DisplayName("5. sourceToView returns negative for filtered-out element")
    void sourceToView_filteredOutElement() {
        // "banana" (src index 1) is the only excluded item
        list.setPredicate(s -> s.contains("e"));

        assertTrue(list.sourceToView(1) < 0, "'banana' is filtered out, view index must be negative");
    }

    @Test
    @DisplayName("6. viewToSource correctly maps view index back to source")
    void viewToSource_withFilter() {
        // view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        list.setPredicate(s -> s.contains("e"));

        assertEquals(0, list.viewToSource(0)); // apple
        assertEquals(2, list.viewToSource(1)); // cherry
        assertEquals(3, list.viewToSource(2)); // date
        assertEquals(4, list.viewToSource(3)); // elderberry
    }

    @Test
    @DisplayName("7. Selecting via view index requires explicit translation")
    void selectViaViewIndex_requiresTranslation() {
        // view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        list.setPredicate(s -> s.contains("e"));

        // Caller sees "date" at view index 2 and wants to select it
        int viewIdx = 2;
        int sourceIdx = list.viewToSource(viewIdx);
        assertEquals(3, sourceIdx);

        model.selectIndex(sourceIdx);

        assertTrue(model.contains(3));
        assertEquals("date", list.get(viewIdx));
        assertTrue(model.contains("date"));
    }

    // -------------------------------------------------------------------------
    // Comparator active
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("8. Sort changes view order - source indices unchanged")
    void sort_viewOrderChanges_sourceIndicesStable() {
        // reverseOrder → view: [elderberry(src4), date(src3), cherry(src2), banana(src1), apple(src0)]
        list.setComparator(Comparator.reverseOrder());

        assertEquals("elderberry", list.get(0));
        assertEquals("apple", list.get(4));

        // "banana" is still at source index 1
        model.selectIndex(1);
        assertTrue(model.contains("banana"));

        // In reverse order "banana" is at view index 3
        assertEquals(3, list.sourceToView(1));
    }

    @Test
    @DisplayName("9. viewToSource with comparator")
    void viewToSource_withComparator() {
        // reverseOrder → view[0] = elderberry(src4), view[4] = apple(src0)
        list.setComparator(Comparator.reverseOrder());

        assertEquals(4, list.viewToSource(0)); // elderberry
        assertEquals(0, list.viewToSource(4)); // apple
    }

    // -------------------------------------------------------------------------
    // Mutation while selection is active
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("10. Removing a non-selected item before selection shifts index down")
    void remove_nonSelectedItem_shiftsSelection() {
        model.selectIndex(3); // "date"
        assertTrue(model.contains("date"));

        // Remove "banana" (src index 1) — indices > 1 shift down by 1
        list.remove("banana");

        assertFalse(model.contains(3), "old index 3 should be vacated");
        assertTrue(model.contains(2), "selection should have shifted to index 2");
        assertTrue(model.contains("date"));
    }

    @Test
    @DisplayName("11. Removing the selected item clears that entry from selection")
    void remove_selectedItem_clearsSelection() {
        model.selectIndex(2); // "cherry"
        assertTrue(model.contains("cherry"));

        list.remove("cherry");

        assertFalse(model.contains("cherry"));
        assertFalse(model.contains(2));
    }

    @Test
    @DisplayName("12. Adding an item before selection shifts selection index up")
    void add_beforeSelection_shiftsSelectionUp() {
        model.selectIndex(2); // "cherry"

        // Insert "avocado" at source index 0
        list.add(0, "avocado");

        assertFalse(model.contains(2), "old index 2 should be vacated");
        assertTrue(model.contains(3), "selection should have shifted to index 3");
        assertTrue(model.contains("cherry"));
    }

    @Test
    @DisplayName("13. Adding an item after selection does not affect selection index")
    void add_afterSelection_noShift() {
        model.selectIndex(1); // "banana"

        list.add("fig"); // appended at the end

        assertTrue(model.contains(1));
        assertTrue(model.contains("banana"));
    }

    // -------------------------------------------------------------------------
    // Filter + mutation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("14. Changing predicate - selected item filtered out stays in selection map")
    void changePredicate_selectedItemFilteredOut_remainsInMap() {
        model.selectIndex(1); // "banana"
        assertTrue(model.contains("banana"));

        // Apply filter that excludes "banana" (it has no 'e')
        list.setPredicate(s -> s.contains("e"));

        assertFalse(list.contains("banana"), "'banana' must not appear in the view");
        // The model retains the entry at source index 1 — no auto-deselect on filter change.
        assertTrue(model.contains(1));
        assertTrue(model.contains("banana"));
    }

    @Test
    @DisplayName("15. Multi-select with filter active - all indices in source space")
    void multiSelect_withFilter() {
        // view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        list.setPredicate(s -> s.contains("e"));
        model.setAllowsMultipleSelection(true);

        // Select "apple" and "elderberry" by their source indices
        model.selectIndexes(0, 4);

        assertTrue(model.contains(0));
        assertTrue(model.contains(4));
        assertEquals(2, model.selection().size());
        assertTrue(model.contains("apple"));
        assertTrue(model.contains("elderberry"));
    }

    // -------------------------------------------------------------------------
    // Single-selection mode
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("16. Single-selection mode - new select replaces old")
    void singleSelection_replacesExisting() {
        model.setAllowsMultipleSelection(false);

        model.selectIndex(0);
        assertTrue(model.contains(0));

        model.selectIndex(2);
        assertFalse(model.contains(0));
        assertTrue(model.contains(2));
        assertEquals(1, model.selection().size());
    }

    // -------------------------------------------------------------------------
    // expandSelection
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("17. expandSelection extends range from first selected")
    void expandSelection_fromFirst() {
        model.selectIndex(1); // anchor at "banana"
        model.expandSelection(3, false); // should select [1..3]

        assertTrue(model.contains(1));
        assertTrue(model.contains(2));
        assertTrue(model.contains(3));
        assertEquals(3, model.selection().size());
    }

    @Test
    @DisplayName("18. expandSelection from last selected")
    void expandSelection_fromLast() {
        model.selectIndexes(1, 2);
        model.expandSelection(4, true); // extend from last (2) to 4

        assertTrue(model.contains(1));
        assertTrue(model.contains(2));
        assertTrue(model.contains(3));
        assertTrue(model.contains(4));
    }

    // -------------------------------------------------------------------------
    // Dispose
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("19. dispose - model no longer reacts to list changes")
    void dispose_noLongerReacts() {
        model.selectIndex(0);
        model.dispose();

        assertDoesNotThrow(() -> list.add(0, "zucchini"));
    }

    // -------------------------------------------------------------------------
    // Filter + Sort combined
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("20. sourceToView correctly locates a selected item in the view with filter+sort active")
    void sourceToView_withFilterAndSort() {
        // filter contains("e"): [apple(src0), cherry(src2), date(src3), elderberry(src4)]
        // reverseOrder:  view = [elderberry,  date,         cherry,      apple         ]
        list.setPredicate(s -> s.contains("e"));
        list.setComparator(Comparator.reverseOrder());

        // Select "apple" and "date" by their source indices
        model.setAllowsMultipleSelection(true);
        model.selectIndexes(0, 3);
        assertTrue(model.contains("apple"));
        assertTrue(model.contains("date"));

        // sourceToView must give the actual position in the current view
        assertEquals(3, list.sourceToView(0)); // apple → view 3
        assertEquals(1, list.sourceToView(3)); // date  → view 1
        assertEquals("apple", list.get(list.sourceToView(0)));
        assertEquals("date", list.get(list.sourceToView(3)));
    }
}