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

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class TestInitSize {
	private static Stage stage;

	@Start
	void start(Stage stage) {
		TestInitSize.stage = stage;
		stage.show();
	}

	@Test
	void testStandardFab(FxRobot robot) {
		StackPane root = setupStage();
		MFXFab fab = new MFXFab(new MFXFontIcon("fas-circle", 24));
		robot.interact(() -> root.getChildren().setAll(fab));
		assertEquals(56, fab.getLayoutBounds().getWidth());
		assertEquals(56, fab.getLayoutBounds().getHeight());

		robot.interact(() -> fab.setPrefSize(70, 70));
		assertEquals(70, fab.getLayoutBounds().getWidth());
		assertEquals(70, fab.getLayoutBounds().getHeight());
	}

	@Test
	void testSmallFab(FxRobot robot) {
		StackPane root = setupStage();
		MFXFab fab = new MFXFab(new MFXFontIcon("fas-circle", 24));
		robot.interact(() -> root.getChildren().setAll(fab));
		assertEquals(56, fab.getLayoutBounds().getWidth());
		assertEquals(56, fab.getLayoutBounds().getHeight());

		robot.interact(() -> fab.setVariants(FABVariants.SMALL));
		assertEquals(40, fab.getLayoutBounds().getWidth());
		assertEquals(40, fab.getLayoutBounds().getHeight());

		robot.interact(() -> fab.setPrefSize(70, 70));
		assertEquals(70, fab.getLayoutBounds().getWidth());
		assertEquals(70, fab.getLayoutBounds().getHeight());
	}

	@Test
	void testLargeFab(FxRobot robot) {
		StackPane root = setupStage();
		MFXFab fab = new MFXFab(new MFXFontIcon("fas-circle", 24.0));
		robot.interact(() -> root.getChildren().setAll(fab));
		assertEquals(56, fab.getLayoutBounds().getWidth());
		assertEquals(56, fab.getLayoutBounds().getHeight());

		robot.interact(() -> fab.setVariants(FABVariants.LARGE));
		assertEquals(96, fab.getLayoutBounds().getWidth());
		assertEquals(96, fab.getLayoutBounds().getHeight());

		robot.interact(() -> fab.setPrefSize(70, 70));
		assertEquals(70, fab.getLayoutBounds().getWidth());
		assertEquals(70, fab.getLayoutBounds().getHeight());
	}

	@Test
	@Disabled
	void testButtons(FxRobot robot) {
		// For some reason this test will always succeed even without
		// using -mfx-init-height in the stylesheet. However, the sizing will
		// result broken in SceneBuilder
		StackPane root = setupStage();
		MFXFilledButton btn = MFXButton.filled();
		robot.interact(() -> root.getChildren().add(btn));
		assertEquals(40, btn.getLayoutBounds().getHeight());

		robot.interact(() -> btn.setPrefHeight(100));
		assertEquals(100, btn.getLayoutBounds().getHeight());
	}

	private StackPane setupStage() {
		try {
			Scene scene = new Scene(new StackPane(), 200, 200);
			MFXThemeManager.PURPLE_LIGHT.addOn(scene);
			FxToolkit.setupStage(s -> s.setScene(scene));
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		return ((StackPane) stage.getScene().getRoot());
	}
}
