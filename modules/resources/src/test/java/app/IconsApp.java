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
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import io.github.palexdev.mfxresources.utils.EnumUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularALIkonHandler;
import org.kordamp.ikonli.win10.Win10;
import org.kordamp.ikonli.win10.Win10IkonHandler;

import java.util.Optional;

public class IconsApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		HBox box = new HBox(30);
		box.setPadding(new Insets(10));

		Color color = Color.web("#6750a4");
		IconContainer i0 = new IconContainer(
				"FontAwesomeSolid (new default for MaterialFX)",
				new MFXFontIcon(EnumUtils.randomEnum(FontAwesomeSolid.class).getDescription(), 64.0, color)
		);
		IconContainer i1 = new IconContainer(
				"Ikonli Windows 10 Pack (external dependency)",
				new MFXFontIcon("", 64.0, color)
						.setIconsProvider(
								Font.loadFont(new Win10IkonHandler().getFontResourceAsStream(), 64.0),
								s -> Optional.ofNullable(Win10.findByDescription(s)).map(w -> (char) w.getCode())
										.orElse('\0')
						)
						.setDescription(EnumUtils.randomEnum(Win10.class).getDescription())
		);
		IconContainer i2 = new IconContainer(
				"Ikonli FluentUI Pack (external dependency)",
				new MFXFontIcon("", 64.0, color)
						.setIconsProvider(
								Font.loadFont(new FluentUiRegularALIkonHandler().getFontResourceAsStream(), 64.0),
								s -> Optional.ofNullable(FluentUiRegularAL.findByDescription(s)).map(w -> (char) w.getCode())
										.orElse('\0')
						)
						.setDescription(EnumUtils.randomEnum(FluentUiRegularAL.class).getDescription())
		);

		box.getChildren().addAll(i0, i1, i2);
		Scene scene = new Scene(box, 1024, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Demo of new MFXFontIcon on steroids");
		primaryStage.show();
	}

	private static class IconContainer extends VBox {

		public IconContainer(String title, MFXFontIcon icon) {
			Label header = new Label(title);
			getChildren().setAll(header, icon);
			setAlignment(Pos.CENTER);
			setSpacing(30);
			setPadding(new Insets(10));
		}
	}
}
