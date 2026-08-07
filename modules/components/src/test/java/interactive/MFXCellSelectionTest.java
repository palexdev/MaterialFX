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

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.mfxcomponents.controls.cells.MFXCell;
import io.github.palexdev.mfxcore.collections.RefineList;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel;
import io.github.palexdev.mfxcore.selection.model.SelectionModel;
import io.github.palexdev.virtualizedfx.base.VFXContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;

import static org.junit.jupiter.api.Assertions.*;

/// Regression tests for the `selected` binding of [MFXCell].
///
/// A predicate or comparator change never touches the selection map (indices are tracked in source-space), and a
/// recycled cell may keep its index while its item changes. The binding must therefore observe the item too, otherwise
/// the cell keeps the selection state of whatever it displayed before.
@TestMethodOrder(MethodOrderer.DisplayName.class)
class MFXCellSelectionTest {

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

    @Test
    @DisplayName("1. Cell deselects when a widening predicate moves another item under its index")
    void cellDeselects_whenPredicateWidens() throws Exception {
        onFx(() -> {
            // view: [apple(src0), cherry(src2), date(src3), elderberry(src4)]
            list.setPredicate(s -> s.contains("e"));
            model.selectIndex(2); // "date", stored at source 3
            assertTrue(model.selection().containsKey(3));

            MFXCell<String> cell = cellAt(2, "date");
            assertTrue(cell.isSelected());

            // view: [apple, banana, cherry, date, elderberry] — "date" moves to view 3
            list.setPredicate(null);

            // The cell is recycled: same index, different item
            cell.updateItem("cherry");

            assertFalse(cell.isSelected(), "cell now displays 'cherry' (src 2), which is not selected");
            assertTrue(model.selection().containsKey(3), "selection itself must be untouched");
        });
    }

    @Test
    @DisplayName("2. Cell deselects when a comparator change moves another item under its index")
    void cellDeselects_whenComparatorChanges() throws Exception {
        onFx(() -> {
            // view: [elderberry(src4), date(src3), cherry(src2), banana(src1), apple(src0)]
            list.setComparator(Comparator.reverseOrder());
            model.selectIndex(3); // "banana", stored at source 1
            assertTrue(model.selection().containsKey(1));

            MFXCell<String> cell = cellAt(3, "banana");
            assertTrue(cell.isSelected());

            // back to source order: view 3 is now "date"
            list.setComparator(null);
            cell.updateItem("date");

            assertFalse(cell.isSelected(), "cell now displays 'date' (src 3), which is not selected");
            assertTrue(model.selection().containsKey(1), "selection itself must be untouched");
        });
    }

    @Test
    @DisplayName("3. Cell stays selected when its item keeps the selection across a predicate change")
    void cellStaysSelected_whenItemSurvives() throws Exception {
        onFx(() -> {
            list.setPredicate(s -> s.contains("e"));
            model.selectIndex(2); // "date"

            MFXCell<String> cell = cellAt(2, "date");
            assertTrue(cell.isSelected());

            list.setPredicate(null);

            // "date" is now at view 3
            cell.updateIndex(3);
            cell.updateItem("date");

            assertTrue(cell.isSelected());
        });
    }

    private MFXCell<String> cellAt(int index, String item) {
        MFXCell<String> cell = new MFXCell<>(item);
        VFXContext<String> context = new VFXContext<>(null);
        context.set(ISelectionModel.class, model);
        cell.onCreated(context);
        cell.updateIndex(index);
        cell.updateItem(item);
        return cell;
    }

    private static void onFx(Runnable action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) throw new AssertionError("Timed out waiting for the FX thread");
        if (error.get() != null) throw new AssertionError(error.get());
    }
}
