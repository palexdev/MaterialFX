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

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Sandbox extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox pane = new VBox(30);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(InsetsBuilder.all(10));

        MFXFab fab = new MFXFab("Floating Action Button", new MFXFontIcon(FontAwesomeSolid.CALCULATOR));
        fab.setExtended(true);

        MFXButton btn = new MFXButton("Extend");
        btn.setOnAction(e -> fab.setExtended(!fab.isExtended()));

        MFXButton btn2 = new MFXButton("Change Icon");
        btn2.setOnAction(e -> fab.setIcon(FontAwesomeSolid.random()));

        HBox box = new HBox(30, btn, btn2);
        box.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(fab, box);
        Scene scene = new Scene(pane, 600, 600);
        MaterialThemes.INDIGO_LIGHT.applyOn(scene);
        stage.setScene(scene);
        stage.show();
        ScenicView.show(scene);
    }
}
