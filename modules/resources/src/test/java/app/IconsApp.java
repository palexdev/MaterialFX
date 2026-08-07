/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import java.util.Arrays;

import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPack;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPacks;
import io.github.palexdev.mfxresources.utils.IconUtils;
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

public class IconsApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox box = new HBox(30);
        box.setPadding(new Insets(10));

        FontIconsPacks.register("win10-", new FontIconsPack() {
            @Override
            public String[] iconNames() {
                return Arrays.stream(Win10.values())
                    .map(Win10::getDescription)
                    .toArray(String[]::new);
            }

            @Override
            public String icon(String name) {
                return String.valueOf((char) Win10.findByDescription(name).getCode());
            }

            @Override
            public Font font() {
                return Font.loadFont(new Win10IkonHandler().getFontResourceAsStream(), 64.0);
            }
        });

        FontIconsPacks.register("fltral-", new FontIconsPack() {
            @Override
            public String[] iconNames() {
                return Arrays.stream(FluentUiRegularAL.values())
                    .map(FluentUiRegularAL::getDescription)
                    .toArray(String[]::new);
            }

            @Override
            public String icon(String name) {
                return String.valueOf((char) FluentUiRegularAL.findByDescription(name).getCode());
            }

            @Override
            public Font font() {
                return Font.loadFont(new FluentUiRegularALIkonHandler().getFontResourceAsStream(), 64.0);
            }
        });

        IconContainer i0 = new IconContainer(
            "FontAwesomeSolid (new default for MaterialFX)",
            "fas-"
        );
        IconContainer i1 = new IconContainer(
            "Ikonli Windows 10 Pack (external dependency)",
            "win10-"
        );
        IconContainer i2 = new IconContainer(
            "Ikonli FluentUI Pack (external dependency)",
            "fltral-"
        );

        box.getChildren().addAll(i0, i1, i2);
        Scene scene = new Scene(box, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo of new MFXFontIcon on steroids");
        primaryStage.show();
    }

    private static class IconContainer extends VBox {

        public IconContainer(String title, String iconPack) {
            Label header = new Label(title);
            MFXFontIcon icon = new MFXFontIcon("", 64.0, Color.web("#6750a4"));
            getChildren().setAll(header, icon);
            setAlignment(Pos.CENTER);
            setSpacing(30);
            setPadding(new Insets(10));

            TimelineBuilder.build()
                .add(KeyFrames.of(500.0, _ -> {
                    String name = IconUtils.randomIconName(iconPack).getValue();
                    icon.setIconName(name);
                }))
                .setCycleCount(-1)
                .getAnimation()
                .play();
        }
    }
}
