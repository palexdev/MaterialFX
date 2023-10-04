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

package app.popups;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PopupExceptionTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		setupStage(primaryStage);
		MaterialThemes.PURPLE_LIGHT.applyGlobal();
		primaryStage.show();
	}

	void setupStage(Stage stage) {
		MFXIconButton btn = new MFXIconButton(
			new MFXFontIcon(FontAwesomeSolid.XMARK)
		);
		btn.setOnAction(e -> Platform.exit()); // This was causing an exception
		installTooltip(btn, "Close App");

		StackPane pane = new StackPane(btn);
		Scene scene = new Scene(pane, 400, 400);
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
	}

	void installTooltip(Node owner, String text) {
		MFXPlainContent content = new MFXPlainContent(text);
		MFXTooltip tp = new MFXTooltip(owner);
		tp.setContent(content);
		tp.setInDelay(M3Motion.SHORT2);
		tp.setOutDelay(Duration.ZERO);
		tp.install();
	}
}
