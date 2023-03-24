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

import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.utils.fx.ColorUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class TestAlignment {
	private static Stage stage;

	private static final String text = "Alignment tests";
	private static final Supplier<MFXFontIcon> iconGenerator = () -> new MFXFontIcon(
			"fas-circle",
			24,
			ColorUtils.getRandomColor()
	);

	@Start
	void start(Stage stage) {
		TestAlignment.stage = stage;
		stage.show();
	}

	@Test
	void testFabLeft(FxRobot robot) {
		VBox box = setupStage();

		MFXFab fab = MFXFab.extended();
		fab.setText(text);
		fab.setIcon(iconGenerator.get());
		fab.setLayoutStrategy(fab.getLayoutStrategy()
				.setPrefWidthFunction(
						fab.getLayoutStrategy().getPrefWidthFunction().andThen(
								r -> fab.isExtended() ? Math.max(r, 200) : r)
				)
		);
		fab.setAlignment(Pos.CENTER_LEFT);

		Button btn = new Button(text, iconGenerator.get());
		btn.setAlignment(Pos.CENTER_LEFT);
		btn.setMinSize(200, 56);
		btn.setPadding(InsetsBuilder.of(0, 20, 0, 16));
		btn.fontProperty().bind(fab.fontProperty());
		btn.graphicTextGapProperty().bind(fab.graphicTextGapProperty());

		robot.interact(() -> box.getChildren().setAll(fab, btn));

		// Get FAB label
		Node label = fab.getChildrenUnmodifiable().get(1);
		// Get Btn icon
		Node icon = btn.getGraphic();

		assertEquals(icon.getBoundsInParent().getMinX(), label.getBoundsInParent().getMinX());
	}

	@Test
	void testFabCenter(FxRobot robot) {
		VBox box = setupStage();

		MFXFab fab = MFXFab.extended();
		fab.setText(text);
		fab.setIcon(iconGenerator.get());
		fab.setLayoutStrategy(fab.getLayoutStrategy()
				.setPrefWidthFunction(
						fab.getLayoutStrategy().getPrefWidthFunction().andThen(
								r -> fab.isExtended() ? Math.max(r, 200) : r)
				)
		);
		fab.setAlignment(Pos.CENTER);

		Button btn = new Button(text, iconGenerator.get());
		btn.setAlignment(Pos.CENTER);
		btn.setMinSize(200, 56);
		btn.setPadding(InsetsBuilder.all(16));
		btn.fontProperty().bind(fab.fontProperty());
		btn.graphicTextGapProperty().bind(fab.graphicTextGapProperty());

		robot.interact(() -> box.getChildren().setAll(fab, btn));

		// Get FAB label
		Node label = fab.getChildrenUnmodifiable().get(1);
		// Get Btn icon
		Node icon = btn.getGraphic();

		assertEquals(icon.getBoundsInParent().getMinX(), label.getBoundsInParent().getMinX());
	}

	@Test
	void testFabRight(FxRobot robot) {
		VBox box = setupStage();

		MFXFab fab = MFXFab.extended();
		fab.setText(text);
		fab.setIcon(iconGenerator.get());
		fab.setLayoutStrategy(fab.getLayoutStrategy()
				.setPrefWidthFunction(
						fab.getLayoutStrategy().getPrefWidthFunction().andThen(
								r -> fab.isExtended() ? Math.max(r, 200) : r)
				)
		);
		fab.setAlignment(Pos.CENTER_RIGHT);

		Button btn = new Button(text, iconGenerator.get());
		btn.setAlignment(Pos.CENTER_RIGHT);
		btn.setMinSize(200, 56);
		btn.setPadding(InsetsBuilder.all(16));
		btn.fontProperty().bind(fab.fontProperty());
		btn.graphicTextGapProperty().bind(fab.graphicTextGapProperty());

		robot.interact(() -> box.getChildren().setAll(fab, btn));

		// Get FAB label
		Node label = fab.getChildrenUnmodifiable().get(1);
		// Get Btn icon
		Node icon = btn.getGraphic();

		assertEquals(icon.getBoundsInParent().getMinX(), label.getBoundsInParent().getMinX());
	}

	@Test
	void testStandardFabCenter(FxRobot robot) {
		VBox box = setupStage();

		MFXFab fab = new MFXFab();
		fab.setText(text);
		fab.setIcon(iconGenerator.get());
		fab.setLayoutStrategy(fab.getLayoutStrategy()
				.setPrefWidthFunction(
						fab.getLayoutStrategy().getPrefWidthFunction().andThen(
								r -> Math.max(r, 100))
				)
		);
		fab.setAlignment(Pos.CENTER);

		Button btn = new Button(text, iconGenerator.get());
		btn.setAlignment(Pos.CENTER);
		btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		btn.setMinSize(100, 56);
		btn.setPadding(InsetsBuilder.of(0, 0, 0, 0));
		btn.fontProperty().bind(fab.fontProperty());
		btn.graphicTextGapProperty().bind(fab.graphicTextGapProperty());

		robot.interact(() -> box.getChildren().setAll(fab, btn));

		// Get FAB label
		Node label = fab.getChildrenUnmodifiable().get(1);
		// Get Btn icon
		Node icon = btn.getGraphic();

		assertEquals(icon.getBoundsInParent().getMinX(), label.getBoundsInParent().getMinX());
	}

	private VBox setupStage() {
		VBox box = new VBox(10);
		box.setAlignment(Pos.CENTER);
		try {
			Scene scene = new Scene(box, 400, 400);
			MFXThemeManager.LIGHT.addOn(scene);
			FxToolkit.setupStage(s -> s.setScene(scene));
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		return box;
	}
}
