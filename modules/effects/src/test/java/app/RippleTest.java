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

package app;

import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class RippleTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();

		Pane target = new Pane();
		target.setPrefSize(600, 600);
		target.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		target.setStyle("-fx-border-color: red; -fx-border-radius: 24px");

		MFXRippleGenerator rg = new MFXRippleGenerator(target);
		rg.setAutoClip(true);
		rg.enable();
		target.getChildren().add(rg);

/*		rg.setAnimateBackground(true);
		rg.setRippleSupplier(() -> new CircleRipple() {
			{
				setVisible(true);
			}
		});
		rg.setBackgroundOpacity(0.7);*/

		root.getChildren().add(target);
		Scene scene = new Scene(root, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
