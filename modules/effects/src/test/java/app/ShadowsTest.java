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

import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ShadowsTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		HBox box = new HBox(30);
		box.setAlignment(Pos.CENTER);

		for (ElevationLevel level : ElevationLevel.values()) {
			box.getChildren().add(createRegion(level));
		}

		Scene scene = new Scene(box, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Region createRegion(ElevationLevel elevation) {
		Region r = new Region();
		r.setPrefSize(100, 50);
		r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		r.setPickOnBounds(false);
		r.setEffect(elevation.toShadow());
		r.setStyle("-fx-background-color: red");
		return r;
	}
}
