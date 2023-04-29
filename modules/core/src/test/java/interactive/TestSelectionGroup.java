package interactive;

import io.github.palexdev.mfxcore.enums.SelectionMode;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TestSelectionGroup {
    private static Stage stage;

    @Start
    void start(Stage stage) {
        TestSelectionGroup.stage = stage;
        stage.show();
    }

    @Test
    void testAdd(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));
        SelectionGroup group = new SelectionGroup();

        // Test set
        for (int i = 0; i < 3; i++) {
            selectables.get(i).setSelectionGroup(group);
        }

        // Test add
        group.add(selectables.get(3));
        group.add(selectables.get(4));

        // All should be false
        selectables.forEach(s -> assertFalse(s.isSelected()));

        // Group sanity check
        assertEquals(0, group.getSelection().size());
    }

    @Test
    void testAdd2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));
        SelectionGroup group = new SelectionGroup();

        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        group.addAll(selectables);

        // Only 1 should be selected as mode is SINGLE
        // The last per default behavior
        for (int i = 0; i < 4; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(4).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(4)));
    }

    @Test
    void testAdd3(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        selectables.forEach(s -> {
            s.setSelected(true);
            s.setSelectionGroup(group);
        });

        // Only last should be selected
        for (int i = 0; i < 4; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(4).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(4)));
    }

    @Test
    void testRemove(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        group.addAll(selectables);

        group.removeAll(
                selectables.get(0),
                selectables.get(2),
                selectables.get(4)
        );
        assertEquals(2, group.getSelectables().size());
        assertEquals(0, group.getSelection().size());

        for (int i = 0; i < 5; i += 2) {
            assertNull(selectables.get(i).getSelectionGroup());
        }

        selectables.get(0).setSelected(true);
        assertEquals(0, group.getSelection().size());
    }

    @Test
    void testRemove2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        group.remove(selectables.get(0));
        assertNull(selectables.get(0).getSelectionGroup());
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(1)));
    }

    @Test
    void testRemove3(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        selectables.get(3).setSelected(true);
        selectables.get(4).setSelected(true);

        // Sanity check again
        assertEquals(3, group.getSelection().size());
        assertTrue(group.getSelection().containsAll(Set.of(
                selectables.get(0),
                selectables.get(3),
                selectables.get(4)
        )));

        group.removeAll(Set.of(
                selectables.get(0),
                selectables.get(3),
                selectables.get(4)
        ));
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(1)));
    }

    @Test
    void testClear(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        group.addAll(selectables);

        group.clear();
        assertEquals(0, group.getSelectables().size());
        selectables.forEach(s -> assertNull(s.getSelectionGroup()));
    }

    @Test
    void testMultiple(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        selectables.get(0).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(3).setSelected(true);
        selectables.forEach(s -> s.setSelectionGroup(group));

        assertTrue(selectables.get(0).isSelected());
        assertFalse(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());
        assertFalse(selectables.get(4).isSelected());

        // Group sanity check
        assertEquals(3, group.getSelection().size());
        assertTrue(group.getSelection().containsAll(Set.of(
                selectables.get(0),
                selectables.get(2),
                selectables.get(3)
        )));
    }

    @Test
    void testMultiple2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        group.addAll(selectables);
        for (int i = 1; i < 4; i++) {
            selectables.get(i).setSelected(true);
        }

        assertFalse(selectables.get(0).isSelected());
        assertTrue(selectables.get(1).isSelected());
        assertTrue(selectables.get(2).isSelected());
        assertTrue(selectables.get(3).isSelected());
        assertFalse(selectables.get(4).isSelected());

        // Group sanity check
        assertEquals(3, group.getSelection().size());
        assertTrue(group.getSelection().containsAll(Set.of(
                selectables.get(1),
                selectables.get(2),
                selectables.get(3)
        )));
    }

    @Test
    void testAtLeastOne(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        group.addAll(selectables);
        group.setAtLeastOneSelected(true);

        // Check activation of first element
        assertTrue(selectables.get(0).isSelected());
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        // Try select all, only last should remain active
        selectables.forEach(s -> s.setSelected(true));
        for (int i = 0; i < 4; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(4).isSelected());

        // Deselecting last should not work
        selectables.get(4).setSelected(false);
        assertTrue(selectables.get(4).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(4)));
    }

    @Test
    void testAtLeastOne2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup();
        group.addAll(selectables);

        // Activate atLeastOne after
        group.setAtLeastOneSelected(true);
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(0).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));
    }

    @Test
    void testAtLeastOne3(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        // Enable then add
        SelectionGroup group = new SelectionGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Only first is active
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(0).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));
    }

    @Test
    void testAtLeastOne4(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        // Enable then add
        SelectionGroup group = new SelectionGroup(SelectionMode.SINGLE, true);
        group.addAll(selectables);

        // Only first is active
        for (int i = 1; i < 5; i++) {
            assertFalse(selectables.get(i).isSelected());
        }
        assertTrue(selectables.get(0).isSelected());

        // Group sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        assertTrue(selectables.get(0).getPseudoClassStates().contains(DummySelectable.SELECTED));

        // Try disabling it
        selectables.get(0).setSelected(false);
        assertTrue(selectables.get(0).isSelected());
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        // This test is to ensure that the PseudoClass ":selected" is still on...
        assertTrue(selectables.get(0).getPseudoClassStates().contains(DummySelectable.SELECTED));
    }

    @Test
    void testSwitchToSingle(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        selectables.forEach(s -> s.setSelectionGroup(group));

        selectables.get(1).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        // Switch and check again, although, only last will remain active now
        group.setSelectionMode(SelectionMode.SINGLE);
        assertEquals(0, group.getSelection().size());
        selectables.forEach(s -> assertFalse(s.isSelected()));
    }

    @Test
    void testSwitchToSingle2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        selectables.forEach(s -> s.setSelectionGroup(group));
        group.setAtLeastOneSelected(true);

        // Sanity check
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.get(0).isSelected());
        assertTrue(group.getSelection().contains(selectables.get(0)));

        selectables.get(1).setSelected(true);
        selectables.get(2).setSelected(true);
        selectables.get(4).setSelected(true);

        // Switch and check again, although, only last will remain active now
        group.setSelectionMode(SelectionMode.SINGLE);
        assertEquals(1, group.getSelection().size());
        assertTrue(selectables.get(4).isSelected());
        assertTrue(group.getSelection().contains(selectables.get(4)));
    }

    @Test
    void testSwitchToSingle3(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group = new SelectionGroup(SelectionMode.MULTIPLE);
        selectables.forEach(s -> s.setSelectionGroup(group));
        selectables.get(0).setSelected(true);

        // Switch...
        group.setSelectionMode(SelectionMode.SINGLE);

        // When switching, if selection contained only one, won't be cleared
        assertEquals(1, group.getSelection().size());
        assertTrue(group.getSelection().contains(selectables.get(0)));
    }

    @Test
    void testSwitchGroup(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);

        selectables.get(0).setSelected(true);

        // First sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.get(0)));

        // Switch group
        selectables.get(0).setSelectionGroup(group2);
        assertEquals(group2, selectables.get(0).getSelectionGroup());

        // Sanity check on g1
        assertTrue(group1.getSelection().isEmpty());
        assertFalse(group1.getSelectables().contains(selectables.get(0)));

        // Sanity check on g2
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.get(0)));
    }

    @Test
    void testSwitchGroup2(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);
        group1.setAtLeastOneSelected(true);

        // First sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.get(0)));

        // Switch group
        selectables.get(0).setSelectionGroup(group2);
        assertEquals(group2, selectables.get(0).getSelectionGroup());

        // Sanity check on g1
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelectables().contains(selectables.get(1)));

        // Sanity check on g2
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.get(0)));
    }

    @Test
    void testSwitchGroup3(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);
        group1.setAtLeastOneSelected(true);
        group2.setAtLeastOneSelected(true);

        // First sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.get(0)));

        // Switch group
        selectables.get(1).setSelectionGroup(group2);
        assertEquals(group2, selectables.get(1).getSelectionGroup());

        // Sanity check on g1
        assertEquals(1, group1.getSelection().size());
        assertEquals(4, group1.getSelectables().size());
        assertTrue(group1.getSelectables().contains(selectables.get(0)));

        // Sanity check on g2
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.get(1)));
    }

    @Test
    void testSwitchGroup4(FxRobot robot) {
        HBox box = setupStage();
        List<DummySelectable> selectables = buildSelectables(5);
        robot.interact(() -> box.getChildren().addAll(selectables));

        SelectionGroup group1 = new SelectionGroup();
        SelectionGroup group2 = new SelectionGroup();
        group1.addAll(selectables);

        selectables.get(0).setSelected(true);

        // First sanity check
        assertEquals(1, group1.getSelection().size());
        assertTrue(group1.getSelection().contains(selectables.get(0)));

        // Switch group with add(...)
        group2.add(selectables.get(0));
        assertEquals(group2, selectables.get(0).getSelectionGroup());

        // Sanity check on g1
        assertTrue(group1.getSelection().isEmpty());
        assertFalse(group1.getSelectables().contains(selectables.get(0)));

        // Sanity check on g2
        assertEquals(1, group2.getSelectables().size());
        assertEquals(1, group2.getSelection().size());
        assertTrue(group2.getSelection().contains(selectables.get(0)));
    }

    List<DummySelectable> buildSelectables(int cnt) {
        return IntStream.range(0, cnt)
                .mapToObj(i -> new DummySelectable())
                .collect(Collectors.toList());
    }

    HBox setupStage() {
        HBox box = new HBox();
        CSSFragment.Builder.build()
                .addSelector(".selectable")
                .addStyle("-fx-fill: grey")
                .addStyle("-fx-stroke: black")
                .closeSelector()
                .addSelector(".selectable:selected")
                .addStyle("-fx-fill: green")
                .closeSelector()
                .applyOn(box);
        try {
            Scene scene = new Scene(box, 400, 200);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return box;
    }

    public static class DummySelectable extends Rectangle implements Selectable {
        public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
        private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);
        private final SelectionProperty selected = new SelectionProperty(this) {
            @Override
            protected void invalidated() {
                pseudoClassStateChanged(SELECTED, get());
            }
        };

        public DummySelectable() {
            super(40, 40);
            getStyleClass().add("selectable");
        }

        public boolean isSelected() {
            return selected.get();
        }

        @Override
        public SelectionProperty selectedProperty() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        @Override
        public SelectionGroup getSelectionGroup() {
            return selectionGroup.get();
        }

        @Override
        public SelectionGroupProperty selectionGroupProperty() {
            return selectionGroup;
        }

        public void setSelectionGroup(SelectionGroup selectionGroup) {
            this.selectionGroup.set(selectionGroup);
        }
    }
}
