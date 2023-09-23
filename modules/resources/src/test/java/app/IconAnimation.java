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

import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IconAnimation extends Application {

	@Override
	public void start(Stage primaryStage) {
		VBox pane = new VBox(20);
		pane.setAlignment(Pos.TOP_CENTER);
		pane.setPadding(new Insets(20));

		MFXFontIcon startIcon = new MFXFontIcon(FontAwesomeSolid.CHECK);
		MFXIconWrapper wrapper = new MFXIconWrapper(startIcon, 64)
			.setAnimated(true)
			.setAnimationProvider(MFXIconWrapper.AnimationPresets.CLIP)
			.enableRippleGenerator(true);
		wrapper.setBorder(Border.stroke(Color.RED));
		//wrapper.setBackground(Background.fill(Color.PURPLE));

		String css = """
			.mfx-icon-wrapper .mfx-ripple-generator {
				-mfx-ripple-color: lightgrey;
				-mfx-ripple-radius: 64px;
			}
			   
			.mfx-icon-wrapper .mfx-font-icon {
				-mfx-size: 64px;
			}
			""";
		String data = "data:base64," + new String(Base64.getEncoder().encode(css.getBytes(UTF_8)), UTF_8);
		wrapper.getStylesheets().add(data);

		Button b1 = new Button("To Check");
		b1.setOnAction(e -> wrapper.setIcon(FontAwesomeSolid.CHECK));
		Button b2 = new Button("To Minus");
		b2.setOnAction(e -> wrapper.setIcon(FontAwesomeSolid.MINUS));
		Button b3 = new Button("To X");
		b3.setOnAction(e -> wrapper.setIcon(FontAwesomeSolid.XMARK));
		HBox box = new HBox(30, b1, b2, b3);
		box.setAlignment(Pos.CENTER);

		pane.getChildren().addAll(wrapper, box);
		Scene scene = new Scene(pane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		ScenicView.show(scene);
	}
}
