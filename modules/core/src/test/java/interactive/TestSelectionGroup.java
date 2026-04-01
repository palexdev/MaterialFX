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

package interactive;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TestSelectionGroup {

    //================================================================================
    // Setup
    //================================================================================

    private static Stage stage;
    private HBox box;

    private GroupTester groupTester;
    private List<DummySelectable> selectables;

    @Start
    void start(Stage stage) {
        TestSelectionGroup.stage = stage;
        box = new HBox();
        CSSFragment.Builder.build()
            .select(".selectable")
            .style("-fx-fill: grey")
            .style("-fx-stroke: black")
            .select(".selectable:selected")
            .style("-fx-fill: green")
            .applyOn(box);
        try {
            Scene scene = new Scene(box, 400, 200);
            FxToolkit.setupStage(s -> {
                s.setScene(scene);
                s.show();
            });
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setup(FxRobot robot) {
        groupTester = new GroupTester();
        selectables = groupTester.create(5);
        robot.interact(() -> box.getChildren().addAll(selectables));
    }

    @AfterEach
    void tearDown(FxRobot robot) {
        if (groupTester != null) {
            groupTester.dispose();
        }
        robot.interact(() -> box.getChildren().clear());
    }

    //================================================================================
    // Tests: Add
    //================================================================================

    @Test
    @DisplayName("Adds selectables via setSelectionGroup(), all remain unselected")
    void testAdd() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);

        // Action: Add via setSelectionGroup()
        for (int i = 0; i < 3; i++) {
            selectables.get(i).setSelectionGroup(group);
        }

        // Action: Add via group.add()
        group.add(selectables.get(3));
        group.add(selectables.get(4));

        // Assertions: All remain unselected
        selectables.forEach(s -> assertFalse(s.isSelected()));
        assertTrue(group.isSelectionEmpty());

        groupTester.assertPropertiesUpdates(0);
        groupTester.assertSelectionUpdates(0);
    }

    @Test
    @DisplayName("Adds 2 pre-selected to SINGLE group, first wins")
    void testAdd2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        // Action: Add pre-selected selectables
        group.addAll(selectables);

        // Assertions: First wins in SINGLE mode
        for (int i = 0; i < selectables.size(); i++) {
            if (i == 2) continue;
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(2).isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.get(2)));

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Adds all 5 pre-selected via setSelectionGroup(), only first stays selected")
    void testAdd3() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        selectables.forEach(s -> {
            s.setSelected(true);
            s.setSelectionGroup(group);
        });

        // Assertions: Only first stays selected
        assertTrue(selectables.getFirst().isSelected());
        for (int i = 1; i < selectables.size(); i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        groupTester.assertPropertiesUpdates(9);
        groupTester.assertSelectionUpdates(1);
    }

    //================================================================================
    // Tests: Remove
    //================================================================================

    @Test
    @DisplayName("Removes multiple selectables, verifies they're gone from group/selection")
    void testRemove() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);

        // Action: Remove multiple selectables
        group.remove(
            selectables.getFirst(),
            selectables.get(2),
            selectables.get(4)
        );

        // Assertions: Correctly removed from group/selection
        assertEquals(2, group.getSelectables().size());
        assertTrue(group.isSelectionEmpty());
        for (int i = 0; i < 5; i += 2) {
            assertNull(selectables.get(i).getSelectionGroup());
        }

        // Action: Try selecting removed item (should not affect group)
        selectables.getFirst().setSelected(true);
        assertTrue(group.isSelectionEmpty());

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(0);
    }

    @Test
    @DisplayName("Remove selected in SINGLE and atLeastOne, first remaining auto-selected")
    void testRemove2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.getFirst()));
        assertTrue(selectables.getFirst().isSelected());

        // Action: Remove the selected item
        group.remove(selectables.getFirst());

        // Assertions: First remaining is auto-selected
        assertNull(selectables.getFirst().getSelectionGroup());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.get(1)));

        groupTester.assertPropertiesUpdates(2);
        groupTester.assertSelectionUpdates(2);
    }

    @Test
    @DisplayName("Remove selected in MULTIPLE and atLeastOne, first remaining auto-selected")
    void testRemove3() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Select additional items
        selectables.get(3).setSelected(true);
        selectables.get(4).setSelected(true);

        // Sanity check
        assertEquals(3, group.selectionSize());
        assertTrue(group.getSelection().containsAll(Set.of(
            selectables.getFirst(),
            selectables.get(3),
            selectables.get(4)
        )));

        // Action: Remove selected items
        group.removeAll(Set.of(
            selectables.getFirst(),
            selectables.get(3),
            selectables.get(4)
        ));

        // Assertions: First remaining auto-selected
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.get(1)));

        groupTester.assertPropertiesUpdates(4);
        groupTester.assertSelectionUpdates(4);
    }

    //================================================================================
    // Tests: Clear
    //================================================================================

    @Test
    @DisplayName("Clears all selectables from group, verifies group is empty and references are null")
    void testClearGroup() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);

        // Action: Clear group
        group.clearGroup();

        // Assertions: Group is empty, references cleared
        assertTrue(group.isGroupEmpty());
        selectables.forEach(s -> assertNull(s.getSelectionGroup()));

        groupTester.assertPropertiesUpdates(0);
        groupTester.assertSelectionUpdates(0);
    }

    @Test
    @DisplayName("Clears group with atLeastOne, verifies first is auto-selected before clear")
    void testClearGroup2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Sanity check
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Clear group
        group.clearGroup();

        // Assertions: Group is empty
        assertTrue(group.isGroupEmpty());
        selectables.forEach(s -> assertNull(s.getSelectionGroup()));

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(2);
    }

    @Test
    @DisplayName("MULTIPLE mode, selects all, clears selection, verifies all are deselected")
    void testClearSelection() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        group.addAll(selectables);
        selectables.forEach(s -> s.setSelected(true));

        // Sanity check
        assertEquals(5, group.selectionSize());
        assertTrue(group.getSelection().containsAll(selectables));

        // Action: Clear selection
        group.clearSelection();

        // Assertions: Selection is empty, all deselected
        assertEquals(5, group.groupSize());
        assertTrue(group.isSelectionEmpty());
        selectables.forEach(s -> assertFalse(s.isSelected()));

        groupTester.assertPropertiesUpdates(10);
        groupTester.assertSelectionUpdates(6);
    }

    @Test
    @DisplayName("Clears selection when already empty, verifies no changes")
    void testClearSelection2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);

        // Sanity check
        assertTrue(group.isSelectionEmpty());

        // Action: Clear when already empty
        group.clearSelection();

        // Assertions: No changes
        groupTester.assertPropertiesUpdates(0);
        groupTester.assertSelectionUpdates(0);
    }

    //================================================================================
    // Tests: MULTIPLE Mode
    //================================================================================

    @Test
    @DisplayName("MULTIPLE mode, adds pre-selected selectables via setSelectionGroup(), verifies all selections preserved")
    void testMultiple() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        selectables.getFirst().setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(3).setSelected(true);
        selectables.forEach(s -> s.setSelectionGroup(group));

        // Assertions: All selections preserved
        assertTrue(selectables.getFirst().isSelected());
        assertFalse(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());
        assertFalse(selectables.get(4).isSelected());
        assertEquals(3, group.selectionSize());
        assertTrue(group.getSelection().containsAll(Set.of(
            selectables.getFirst(),
            selectables.get(2),
            selectables.get(3)
        )));

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(3);
    }

    @Test
    @DisplayName("MULTIPLE mode, adds all then selects 3, verifies only those 3 are selected")
    void testMultiple2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        group.addAll(selectables);

        // Sanity check
        for (int i = 0; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }

        // Action: Select 3 items
        for (int i = 1; i < 4; i++) {
            selectables.get(i).setSelected(true);
        }

        // Assertions: Only selected items are selected
        assertFalse(selectables.getFirst().isSelected());
        assertTrue(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());
        assertFalse(selectables.get(4).isSelected());
        assertEquals(3, group.selectionSize());
        assertTrue(group.getSelection().containsAll(Set.of(
            selectables.get(1),
            selectables.get(2),
            selectables.get(3)
        )));

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(3);
    }

    @Test
    @DisplayName("MULTIPLE mode with atLeastOne, only first is selected when group has no initial selection")
    void testMultiple3() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, true);
        group.addAll(selectables);

        // Assertions: First is auto-selected
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("MULTIPLE mode with atLeastOne, verifies deselecting last selected is blocked")
    void testMultiple4() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, true);
        group.addAll(selectables);

        // Sanity check: first is auto-selected
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Select another item
        selectables.get(2).setSelected(true);

        // Assertions: both are selected
        assertEquals(2, group.selectionSize());
        assertTrue(selectables.getFirst().isSelected());
        assertTrue(selectables.get(2).isSelected());

        // Action: Try to deselect, last should remain
        selectables.getFirst().setSelected(false);
        selectables.get(2).setSelected(false);

        // Assertions: Deselection blocked, still selected
        assertTrue(selectables.get(2).isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.get(2)));

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(3);
    }

    //================================================================================
    // Tests: atLeastOneSelected
    //================================================================================

    @Test
    @DisplayName("Verifies that with atLeastOne, selecting all deselects all but last, and deselecting last is blocked")
    void testAtLeastOne() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Assertions: First is auto-selected
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Select all
        selectables.forEach(s -> s.setSelected(true));

        // Assertions: Only last remains selected
        for (int i = 0; i < selectables.size() - 1; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.getLast().isSelected());

        // Action: Try to deselect last
        selectables.getLast().setSelected(false);

        // Assertions: Deselection blocked
        assertTrue(selectables.getLast().isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getLast()));

        groupTester.assertPropertiesUpdates(9);
        groupTester.assertSelectionUpdates(5);
    }

    @Test
    @DisplayName("Enables atLeastOne after group already has members, verifies first is auto-selected")
    void testAtLeastOne2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);

        // Action: Enable atLeastOne
        group.setAtLeastOneSelected(true);

        // Assertions: First is auto-selected
        assertTrue(selectables.getFirst().isSelected());
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Enables atLeastOne in constructor, then adds selectables, verifies first is selected")
    void testAtLeastOne3() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Assertions: First is selected
        assertTrue(selectables.getFirst().isSelected());
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Verifies that with atLeastOne, PseudoClass state remains correct and deselecting is blocked")
    void testAtLeastOne4() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Sanity check
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.getFirst().getPseudoClassStates().contains(DummySelectable.SELECTED));

        // Action: Try to deselect
        selectables.getFirst().setSelected(false);

        // Assertions: Still selected, PseudoClass preserved
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.getFirst().getPseudoClassStates().contains(DummySelectable.SELECTED));

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Adds pre-selected selectable to existing group with selection, verifies new one is forced to deselect")
    void testAtLeastOne5() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Sanity check
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Add pre-selected item
        Selectable newSel = groupTester.create();
        newSel.setSelected(true);
        group.add(newSel);

        // Assertions: New item forced to deselect (respect existing selection)
        assertFalse(newSel.isSelected());
        assertEquals(1, group.selectionSize());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Enables atLeastOne on empty group, verifies no auto-selection occurs")
    void testAtLeastOne6() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);

        // Sanity check: group is empty
        assertTrue(group.isGroupEmpty());
        assertTrue(group.isSelectionEmpty());

        // Action: Enable atLeastOne on empty group
        group.setAtLeastOneSelected(true);

        // Assertions: Nothing happens, no auto-selection
        assertTrue(group.isSelectionEmpty());

        // Action: Add selectables
        group.addAll(selectables);

        // Assertions: Now first is auto-selected
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.getFirst().isSelected());

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    @Test
    @DisplayName("Disables atLeastOne with selection present, verifies selections remain as-is")
    void testAtLeastOne7() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);
        selectables.get(2).setSelected(true);

        // Sanity check
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.get(2).isSelected());

        // Action: Enable atLeastOne
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.get(2).isSelected());

        // Action: Disable atLeastOne
        group.setAtLeastOneSelected(false);

        // Assertions: Selection remains as-is, constraint removed
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.get(2).isSelected());

        // Action: Deselect (now allowed)
        selectables.get(2).setSelected(false);

        // Assertions: Deselection succeeds
        assertFalse(selectables.get(2).isSelected());
        assertTrue(group.isSelectionEmpty());

        groupTester.assertPropertiesUpdates(2);
        groupTester.assertSelectionUpdates(2);
    }

    @Test
    @DisplayName("Disables atLeastOne in MULTIPLE mode, verifies all selections remain and can be deselected")
    void testAtLeastOne8() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        group.addAll(selectables);
        selectables.get(1).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(3).setSelected(true);

        // Sanity check
        assertEquals(3, group.selectionSize());

        // Action: Enable atLeastOne
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(3, group.selectionSize());

        // Action: Disable atLeastOne
        group.setAtLeastOneSelected(false);

        // Assertions: All selections remain
        assertEquals(3, group.selectionSize());
        assertTrue(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());

        // Action: Deselect all
        selectables.get(1).setSelected(false);
        selectables.get(2).setSelected(false);
        selectables.get(3).setSelected(false);

        // Assertions: All deselected
        assertTrue(group.isSelectionEmpty());
        for (DummySelectable s : selectables) {
            assertFalse(s.isSelected());
        }

        groupTester.assertPropertiesUpdates(6);
        groupTester.assertSelectionUpdates(6);
    }

    //================================================================================
    // Tests: Mode Switching
    //================================================================================

    @Test
    @DisplayName("Switches from MULTIPLE to SINGLE mode with multiple selections, verifies selection is cleared")
    void testSwitchToSingle() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        selectables.forEach(s -> s.setSelectionGroup(group));

        selectables.get(1).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        // Action: Switch to SINGLE
        group.setSelectionMode(SelectionMode.SINGLE);

        // Assertions: Selection cleared
        assertTrue(group.isSelectionEmpty());

        groupTester.assertPropertiesUpdates(6);
        groupTester.assertSelectionUpdates(4);
    }

    @Test
    @DisplayName("Switches from MULTIPLE to SINGLE with atLeastOne, verifies only first stays selected")
    void testSwitchToSingle2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        selectables.forEach(s -> s.setSelectionGroup(group));
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        selectables.get(1).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        // Action: Switch to SINGLE
        group.setSelectionMode(SelectionMode.SINGLE);

        // Assertions: First stays selected (atLeastOne)
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.getFirst().isSelected());
        assertTrue(group.getSelection().contains(selectables.getFirst()));
        for (int i = 1; i < selectables.size(); i++) {
            assertFalse(selectables.get(i).isSelected());
        }

        groupTester.assertPropertiesUpdates(7);
        groupTester.assertSelectionUpdates(5);
    }

    @Test
    @DisplayName("Switches from MULTIPLE to SINGLE when only one is selected, verifies selection is cleared")
    void testSwitchToSingle3() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.MULTIPLE, false);
        selectables.forEach(s -> s.setSelectionGroup(group));
        selectables.getFirst().setSelected(true);

        // Sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.getFirst().isSelected());
        assertTrue(group.getSelection().contains(selectables.getFirst()));

        // Action: Switch to SINGLE
        group.setSelectionMode(SelectionMode.SINGLE);

        // Assertions: Selection cleared (no atLeastOne)
        assertTrue(group.isSelectionEmpty());

        groupTester.assertPropertiesUpdates(2);
        groupTester.assertSelectionUpdates(2);
    }

    @Test
    @DisplayName("Switches from SINGLE to MULTIPLE mode, verifies selection is preserved")
    void testSwitchToMultiple() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, false);
        group.addAll(selectables);
        selectables.get(2).setSelected(true);

        // Sanity check
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.get(2).isSelected());

        // Action: Switch to MULTIPLE
        group.setSelectionMode(SelectionMode.MULTIPLE);

        // Assertions: Selection preserved
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(group.getSelection().contains(selectables.get(2)));

        // Action: Select additional items
        selectables.get(1).setSelected(true);
        selectables.get(3).setSelected(true);

        // Assertions: Multiple selections now allowed
        assertEquals(3, group.selectionSize());
        assertTrue(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());

        groupTester.assertPropertiesUpdates(3);
        groupTester.assertSelectionUpdates(3);
    }

    @Test
    @DisplayName("Switches from SINGLE to MULTIPLE with atLeastOne, verifies selection is preserved")
    void testSwitchToMultiple2() {
        SelectionGroup group = groupTester.getGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Sanity check
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.getFirst().isSelected());

        // Action: Switch to MULTIPLE
        group.setSelectionMode(SelectionMode.MULTIPLE);

        // Assertions: Selection preserved, atLeastOne still active
        assertEquals(1, group.selectionSize());
        assertTrue(selectables.getFirst().isSelected());

        // Action: Try to deselect last
        selectables.getFirst().setSelected(false);

        // Assertions: Deselection blocked
        assertTrue(selectables.getFirst().isSelected());
        assertEquals(1, group.selectionSize());

        groupTester.assertPropertiesUpdates(1);
        groupTester.assertSelectionUpdates(1);
    }

    //================================================================================
    // Tests: Group Switching
    //================================================================================

    @Test
    @DisplayName("Switches selectable from one group to another via setSelectionGroup(), verifies it's removed from old and added to new")
    void testSwitchGroup() {
        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);

        selectables.getFirst().setSelected(true);

        // Sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.getFirst()));

        // Action: Switch to group2
        selectables.getFirst().setSelectionGroup(group2);

        // Assertions: Correctly moved to group2
        assertEquals(group2, selectables.getFirst().getSelectionGroup());
        assertTrue(group1.getSelection().isEmpty());
        assertFalse(group1.getSelectables().contains(selectables.getFirst()));
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.getFirst()));
    }

    @Test
    @DisplayName("Switches selectable with atLeastOne on both groups, verifies new group auto-selects it")
    void testSwitchGroup2() {
        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);
        group1.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.getFirst()));

        // Action: Switch to group2
        selectables.getFirst().setSelectionGroup(group2);

        // Assertions: group1 maintains invariant, group2 auto-selects
        assertEquals(group2, selectables.getFirst().getSelectionGroup());
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelectables().contains(selectables.get(1)));
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.getFirst()));
    }

    @Test
    @DisplayName("Switches non-selected selectable to group with atLeastOne, verifies it gets selected")
    void testSwitchGroup3() {
        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);
        group1.setAtLeastOneSelected(true);
        group2.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.getFirst()));

        // Action: Switch non-selected to group2
        selectables.get(1).setSelectionGroup(group2);

        // Assertions: group2 auto-selects it
        assertEquals(group2, selectables.get(1).getSelectionGroup());
        assertEquals(1, group1.getSelection().size());
        assertEquals(4, group1.getSelectables().size());
        assertTrue(group1.getSelectables().contains(selectables.getFirst()));
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.get(1)));
    }

    @Test
    @DisplayName("Switches selectable using group.add() instead of setSelectionGroup(), verifies correct behavior")
    void testSwitchGroup4() {
        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);

        selectables.getFirst().setSelected(true);

        // Sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.getFirst()));

        // Action: Switch via group.add()
        group2.add(selectables.getFirst());

        // Assertions: Correctly moved to group2
        assertEquals(group2, selectables.getFirst().getSelectionGroup());
        assertTrue(group1.getSelection().isEmpty());
        assertFalse(group1.getSelectables().contains(selectables.getFirst()));
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.getFirst()));
    }

    //================================================================================
    // Helper Classes
    //================================================================================


    static class DummySelectable extends Rectangle implements Selectable {
        public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
        private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);
        private final SelectionProperty selected = new SelectionProperty(this) {
            @Override
            protected void onInvalidated() {
                pseudoClassStateChanged(SELECTED, get());
            }
        };

        public DummySelectable() {
            super(40, 40);
            getStyleClass().add("selectable");
        }

        @Override
        public SelectionProperty selectedProperty() {
            return selected;
        }

        @Override
        public SelectionGroupProperty selectionGroupProperty() {
            return selectionGroup;
        }
    }

    static class GroupTester {
        private final List<DummySelectable> selectables = new ArrayList<>();

        private final List<Disposable> listeners = new ArrayList<>();
        private int propertiesUpdates = 0;
        private int selectionUpdates = 0;

        SelectionGroup getGroup(SelectionMode mode, boolean atLeastOne) {
            SelectionGroup group = new SelectionGroup(mode, atLeastOne);
            listeners.add(onInvalidated(group.getSelection()).then(_ -> selectionUpdates++).listen());
            return group;
        }

        DummySelectable create() {
            DummySelectable s = new DummySelectable();
            When<?> listener = onInvalidated(s.selectedProperty())
                .then(_ -> propertiesUpdates++)
                .listen();
            listeners.add(listener);
            selectables.add(s);
            return s;
        }

        List<DummySelectable> create(int count) {
            List<DummySelectable> created = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                created.add(create());
            }
            return created;
        }

        void assertPropertiesUpdates(int expected) {
            assertEquals(expected, propertiesUpdates);
        }

        /**
         * Asserts the number of times the selection set was updated.
         * Note: This does not count add/remove operations.
         */
        void assertSelectionUpdates(int expected) {
            assertEquals(expected, selectionUpdates);
        }

        void dispose() {
            listeners.forEach(Disposable::dispose);
            listeners.clear();
        }
    }
}
