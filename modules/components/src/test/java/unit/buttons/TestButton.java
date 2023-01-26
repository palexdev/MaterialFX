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

package unit.buttons;

import com.sun.javafx.tk.Toolkit;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxcore.utils.fx.LayoutUtils.snappedBoundHeight;
import static io.github.palexdev.mfxcore.utils.fx.LayoutUtils.snappedBoundWidth;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class TestButton {
	private MFXButton button;

	@Start
	private void start(Stage stage) {
		button = new MFXButton("MFXButton");
		stage.setScene(new Scene(new StackPane(button), 100, 100));
		stage.show();
	}

	@Test
	void testSizes(FxRobot robot) {
		// Test Pref
		robot.interact(() -> button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE));
		assertEquals(snappedBoundWidth(button), button.getWidth());
		assertEquals(snappedBoundHeight(button), button.getHeight());

		// Test max
		robot.interact(() -> {
			button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			button.applyCss();
			button.layout();
			button.requestLayout();
			Toolkit.getToolkit().requestNextPulse();
		});
		assertEquals(100.0, button.getWidth());
		assertEquals(100.0, button.getHeight());
	}
}
