/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		ImageView iv = new ImageView();

		BorderPane bp = new BorderPane(iv);
		Robot robot = new Robot();
		WritableImage capture = robot.getScreenCapture(null, Screen.getPrimary().getBounds());
		iv.setImage(capture);

		Scene scene = new Scene(bp, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
