/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.checkbox.TriState;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TestCheckboxes {

    @Start
    private void start(Stage stage) {
        stage.show();
    }

    @Test
    void testTriState(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox cb = getCheckboxes(1)[0];
        robot.interact(() -> box.getChildren().setAll(cb));

        cb.setSelected(true);
		assertCheckbox(cb, TriState.SELECTED);

        cb.setSelected(false);
		assertCheckbox(cb, TriState.UNSELECTED);

		cb.setState(TriState.SELECTED);
		assertCheckbox(cb, TriState.SELECTED);

		cb.setState(TriState.UNSELECTED);
		assertCheckbox(cb, TriState.UNSELECTED);

        robot.clickOn(cb);
		assertCheckbox(cb, TriState.SELECTED);

        robot.clickOn(cb);
		assertCheckbox(cb, TriState.INDETERMINATE);

        robot.clickOn(cb);
		assertCheckbox(cb, TriState.UNSELECTED);
    }

    @Test
    void testBinding(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(2);
        robot.interact(() -> box.getChildren().setAll(cbs));

        cbs[0].stateProperty().bind(cbs[1].stateProperty());

        robot.clickOn(cbs[1]);
		assertCheckbox(cbs[0], TriState.SELECTED);
		assertCheckbox(cbs[1], TriState.SELECTED);

        robot.clickOn(cbs[1]);
		assertCheckbox(cbs[0], TriState.INDETERMINATE);
		assertCheckbox(cbs[1], TriState.INDETERMINATE);

        robot.clickOn(cbs[1]);
		assertCheckbox(cbs[0], TriState.UNSELECTED);
		assertCheckbox(cbs[1], TriState.UNSELECTED);
    }

    @Test
    void testGroupMultiple(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup(SelectionMode.MULTIPLE);
        sg.addAll(cbs);

        // Consistency check, all unselected, not allowing INDETERMINATE
		for (MFXCheckbox cb : cbs) assertCheckbox(cb, TriState.UNSELECTED);

        cbs[1].setSelected(true);
        cbs[2].setSelected(true);
        cbs[4].setSelected(true);

		assertCheckbox(cbs[1], TriState.SELECTED);
		assertCheckbox(cbs[2], TriState.SELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
        assertEquals(3, sg.getSelection().size());
        assertTrue(sg.getSelection().containsAll(List.of(cbs[1], cbs[2], cbs[4])));
    }

    @Test
    void testGroupMultipleALOS(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup(SelectionMode.MULTIPLE, true);
        sg.addAll(cbs);

        // Consistency check, first selected because of ALOS
		for (int i = 1; i < 5; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[0], TriState.SELECTED);

        cbs[1].setSelected(true);
        cbs[4].setSelected(true);

		assertCheckbox(cbs[0], TriState.SELECTED);
		assertCheckbox(cbs[1], TriState.SELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
        assertEquals(3, sg.getSelection().size());
        assertTrue(sg.getSelection().containsAll(List.of(cbs[0], cbs[1], cbs[4])));

        // Deselect all and check
        for (MFXCheckbox cb : cbs) cb.setSelected(false);
		for (int i = 0; i < 4; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[4]));

        // Select all and check
        for (MFXCheckbox cb : cbs) cb.setSelected(true);
		for (MFXCheckbox cb : cbs) assertCheckbox(cb, TriState.SELECTED);
        assertEquals(5, sg.getSelection().size());
        assertTrue(sg.getSelection().containsAll(List.of(cbs)));
    }

    @Test
    void testGroupSingle(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup();
        sg.addAll(cbs);

        // Consistency check, all unselected, not allowing INDETERMINATE
		for (MFXCheckbox cb : cbs) assertCheckbox(cb, TriState.UNSELECTED);

        cbs[1].setSelected(true);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[1]));

        cbs[0].setSelected(true);
        cbs[4].setSelected(true);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[4]));

        // Consistency check
		for (int i = 0; i < 4; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);

        // Deselect and select all
        cbs[4].setSelected(false);
        for (MFXCheckbox cb : cbs) cb.setSelected(true);

		for (int i = 0; i < 4; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
    }

    @Test
    void testGroupSingle2(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup();

        // Select before adding
        cbs[2].setSelected(true);
        cbs[4].setSelected(true);
        sg.addAll(cbs);

		for (int i = 0; i < 4; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[4]));
    }

    @Test
    void testGroupSingleALOS(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup(SelectionMode.SINGLE, true);
        sg.addAll(cbs);

        // Consistency check, only first selected
		for (int i = 1; i < 5; i++) assertCheckbox(cbs[i], TriState.UNSELECTED);
		assertCheckbox(cbs[0], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[0]));

        cbs[0].setSelected(false);
		assertCheckbox(cbs[0], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[0]));

        cbs[1].setSelected(true);
		assertCheckbox(cbs[0], TriState.UNSELECTED);
		assertCheckbox(cbs[1], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[1]));

        // Deselect all
        for (MFXCheckbox cb : cbs) cb.setSelected(false);
		assertCheckbox(cbs[1], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[1]));

        // Select all
        for (MFXCheckbox cb : cbs) cb.setSelected(true);
		assertCheckbox(cbs[1], TriState.UNSELECTED);
		assertCheckbox(cbs[4], TriState.SELECTED);
        assertEquals(1, sg.getSelection().size());
        assertTrue(sg.getSelection().contains(cbs[4]));
    }

    @Test
    void testGroupMisc(FxRobot robot) {
        HBox box = setupStage();
        MFXCheckbox[] cbs = getCheckboxes(5);
        robot.interact(() -> box.getChildren().setAll(cbs));
        SelectionGroup sg = new SelectionGroup();
        sg.addAll(cbs);

        for (MFXCheckbox cb : cbs) cb.setAllowIndeterminate(true);
        for (MFXCheckbox cb : cbs) {
            assertFalse(cb.isAllowIndeterminate());
			assertCheckbox(cb, TriState.UNSELECTED);
        }
    }

	void assertCheckbox(MFXCheckbox cb, TriState state) {
        assertEquals(state, cb.getState());
		assertEquals(state == TriState.SELECTED, cb.isSelected());
        if (cb.getSelectionGroup() != null) {
            assertFalse(cb.isAllowIndeterminate());
        }

        PseudoClass pc = null;
        switch (state) {
            case SELECTED -> pc = PseudoClasses.SELECTED.getPseudoClass();
            case INDETERMINATE -> pc = PseudoClasses.INDETERMINATE.getPseudoClass();
        }
        if (pc != null) assertTrue(cb.getPseudoClassStates().contains(pc));
    }

    MFXCheckbox[] getCheckboxes(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> {
                    MFXCheckbox cb = new MFXCheckbox("C" + (i + 1));
                    cb.setAllowIndeterminate(true);
                    return cb;
                })
                .toArray(MFXCheckbox[]::new);
    }

    private HBox setupStage() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(InsetsBuilder.all(5));
        try {
            Scene scene = new Scene(box, 400, 200);
            MaterialThemes.PURPLE_LIGHT.applyOn(scene);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return box;
    }
}
