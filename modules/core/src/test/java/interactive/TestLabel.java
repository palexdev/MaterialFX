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

import io.github.palexdev.mfxcore.controls.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class TestLabel {

	@Start
	void start(Stage stage) {
		stage.show();
	}

	@Test
	void testLabel(FxRobot robot) {
		StackPane pane = setupStage();
		pane.setAlignment(Pos.CENTER_LEFT);
		Label label = new Label();

		robot.interact(() -> {
			label.setGraphic(new Button("BTN"));
			label.setStyle("-fx-border-color: red"); // To better visualize the label's bounds
			pane.getChildren().add(label);
		});

		assertTrue(label.getTextNode().isEmpty());
		assertFalse(label.isTruncated());

		robot.interact(() -> label.setText("This text should be long enough to test the label properly"));
		assertTrue(label.getTextNode().isPresent());
		assertFalse(label.isTruncated());

		robot.interact(() -> label.setMaxWidth(100));
		assertTrue(label.isTruncated());

		robot.interact(() -> label.setForceDisableTextEllipsis(true));
		assertFalse(label.isTruncated());
	}

	StackPane setupStage() {
		StackPane pane = new StackPane();
		try {
			Scene scene = new Scene(pane, 400, 200);
			FxToolkit.setupStage(s -> s.setScene(scene));
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		return pane;
	}
}
