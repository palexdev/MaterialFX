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

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxresources.MFXResources;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Prototype extends Application {

    @Override
    public void start(Stage stage) {
        MyButton mb = new MyButton();

        MFXCheckbox checkbox = new MFXCheckbox();
        MFXButton mfxb = new MFXButton().setStyle(StyleVariant.TONAL);
        MFXIconButton mfxib = new MFXIconButton().setStyle(StyleVariant.TONAL);

        VBox root = new VBox(20.0, checkbox, mfxib, mfxb, mb);
        root.setAlignment(Pos.CENTER);
        root.setOnMousePressed(_ -> {
            mb.setDisable(!mb.isDisable());
            mfxb.setDisable(!mfxb.isDisable());
            mfxib.setDisable(!mfxib.isDisable());
        });
        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().addAll(
            MFXResources.loadTheme("material/md-preset-blue.css"),
            MFXResources.loadTheme("material/md-theme.css"),
            MFXResources.loadTheme("material/motion/md-motion.css")
        );
        stage.setScene(scene);
        stage.show();
    }

    static class MyButton extends Region {
        {
            setPrefSize(200, 50);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            getStyleClass().add("my-button");

            CSSFragment.applyOn("""
                    .my-button {
                      -md-sys-color-primary: blue;
                      -md-sys-color-outline: red;
                    
                      -fx-background-color: -md-sys-color-primary;
                      -fx-background-radius: 24px;
                      -fx-border-color: -md-sys-color-outline;
                      -fx-border-radius: 24px;
                      -fx-border-width: 2px;
                      transition-property: -fx-background-color, -fx-background-radius, -fx-border-radius;
                      transition-duration: 0.2s;
                      transition-timing-function: ease-in-out;
                    }
                    
                    .my-button:hover {
                      -fx-background-radius: 12px;
                      -fx-border-radius: 12px;
                    }
                    
                    .my-button:disabled {
                      -fx-background-color: null;
                    }
                    """,
                this
            );

        }
    }
}
