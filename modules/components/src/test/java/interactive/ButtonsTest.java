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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXButton.MFXElevatedButton;
import io.github.palexdev.mfxcore.builders.bindings.StringBindingBuilder;
import io.github.palexdev.mfxresources.MFXResources;
import javafx.application.Application;
import javafx.css.SizeUnits;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Arrays;

public class ButtonsTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		String theme = MFXResources.load("sass/md3/mfx-light.css");

		MFXButton button = new MFXElevatedButton("Button");
		Button btn = new Button();
		btn.textProperty().bind(StringBindingBuilder.build()
				.setMapper(() -> Arrays.toString(button.getPseudoClassStates().toArray()))
				.addSources(button.getPseudoClassStates())
				.get()
		);

		VBox pane = new VBox(30, button, btn);
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color: white");
		Scene scene = new Scene(pane, 800, 800);
		scene.getStylesheets().add(theme);

		double px = SizeUnits.PX.points(14, 1, Font.font("Roboto Medium"));
		System.out.println("Size: " + px);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
