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

import io.github.palexdev.mfxcore.selection.model.SelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectionModelTests {

    private SelectionModel<String> model;
    private ObservableList<String> items;

    @BeforeEach
    void setUp() {
        items = FXCollections.observableArrayList("A", "B", "C", "D", "E");
        model = new SelectionModel<>(items);
    }

    @Test
    @DisplayName("Selecting a valid index should select the correct item")
    void selectIndexWithValidIndexSelectsItem() {
        model.selectIndex(1);
        assertEquals(List.of("B"), model.getSelectedItems());
    }

    @Test
    void selectIndexWithInvalidIndexDoesNothing() {
        model.selectIndex(-1);
        model.selectIndex(99);
        assertTrue(model.getSelectedItems().isEmpty());
    }

    @Test
    void selectItemWithExistingItem() {
        model.selectItem("C");
        assertEquals(List.of("C"), model.getSelectedItems());
    }

    @Test
    void selectItemWithNonExistingItemDoesNothing() {
        model.selectItem("Z");
        assertTrue(model.getSelectedItems().isEmpty());
    }

    @Test
    void selectIndexesWithMultipleSelectionEnabled() {
        model.selectIndexes(0, 2, 4);
        assertEquals(List.of("A", "C", "E"), model.getSelectedItems());
    }

    @Test
    void selectIndexesWithSingleSelectionEnabledKeepsLastOnly() {
        model.setAllowsMultipleSelection(false);
        model.selectIndexes(0, 2, 4);
        assertEquals(List.of("E"), model.getSelectedItems());
    }

    @Test
    void replaceSelectionReplacesCompletely() {
        model.selectIndexes(0, 1, 2);
        model.replaceSelection(3, 4);
        assertEquals(List.of("D", "E"), model.getSelectedItems());
    }

    @Test
    void clearSelectionEmptiesAllSelection() {
        model.selectIndexes(0, 1);
        model.clearSelection();
        assertTrue(model.getSelectedItems().isEmpty());
    }

    @Test
    void deselectIndexRemovesThatIndexOnly() {
        model.selectIndexes(0, 1, 2);
        model.deselectIndex(1);
        assertEquals(List.of("A", "C"), model.getSelectedItems());
    }

    @Test
    void switchingSelectionModeClearsSelectionAndUpdatesFlag() {
        model.selectIndexes(0, 1);
        model.setAllowsMultipleSelection(false);
        assertTrue(model.getSelectedItems().isEmpty());
        assertFalse(model.allowsMultipleSelection());
    }

    @Test
    void expandSelectionFromFirstToLast() {
        model.selectIndex(1); // "B"
        model.expandSelection(3, false); // from 1 to 3
        assertEquals(List.of("B", "C", "D"), model.getSelectedItems());
    }

    @Test
    void expandSelectionFromLast() {
        model.selectIndex(1); // "B"
        model.expandSelection(4, true);
        assertEquals(List.of("B", "C", "D", "E"), model.getSelectedItems());
    }

    @Test
    void disposeClearsResources() {
        model.selectIndex(1);
        model.dispose();
        assertTrue(model.getSelectedItems().isEmpty());
        assertNull(model.eventHandler());
        assertFalse(items.isEmpty());
    }
}

