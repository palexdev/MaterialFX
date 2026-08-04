/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package apps;

import java.util.Arrays;

import io.github.palexdev.mcu.MaterialTheme;
import io.github.palexdev.mcu.MaterialThemeBuilder;
import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXButtonsGroup;
import io.github.palexdev.mfxcomponents.controls.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.theming.MFXThemeEngine;
import io.github.palexdev.mfxcomponents.theming.MaterialColors;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static io.github.palexdev.mcu.Colors.argbToWeb;
import static io.github.palexdev.mfxcore.utils.fx.ColorUtils.getRandomColor;
import static io.github.palexdev.mfxcore.utils.fx.ColorUtils.toArgb;

public class Playground extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(30.0);
        root.setAlignment(Pos.CENTER);

        MFXCheckbox checkbox = new MFXCheckbox();
        //checkbox.setAllowIndeterminate(true);

        MFXButton b1 = new MFXButton().setStyle(ButtonVariants.StyleVariant.TONAL);
        b1.disableProperty().bind(checkbox.selectedProperty());

        MFXIconButton ib = new MFXIconButton().setStyle(ButtonVariants.StyleVariant.TONAL);
        ib.disableProperty().bind(checkbox.selectedProperty());

        MFXButtonsGroup bg = new MFXButtonsGroup();
        bg.setSelectionMode(SelectionMode.SINGLE);
        Arrays.stream(MaterialColors.values()).forEach(c -> {
            MFXButton.MFXToggleButton t = new MFXButton.MFXToggleButton(c.toString());
            t.setOnAction(_ -> MFXThemeEngine.instance().setColor(c));
            bg.addButtons(t);
        });

        MFXButton randBtn = new MFXButton("Random");
        randBtn.setOnAction(_ -> {
            int argb = toArgb(getRandomColor());
            System.out.println(argbToWeb(argb));
            MaterialTheme theme = MaterialThemeBuilder.theme(argb).generate();
            MFXThemeEngine.instance().setColor(theme);
        });

        MFXButton modeBtn = new MFXButton("Mode");
        modeBtn.setOnAction(_ -> MFXThemeEngine.instance().switchThemeMode());

        root.getChildren().addAll(checkbox, ib, b1, bg, randBtn, modeBtn);

        root.setStyle("-fx-background-color: -md-sys-color-surface");
        Scene scene = new Scene(root, 400, 600);
        stage.setScene(scene);
        MFXThemeEngine.instance()
            .init(stage, true)
            .animateApply(true);
        stage.show();
    }
}
