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
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckBox;
import io.github.palexdev.mfxcomponents.theming.Fonts;
import io.github.palexdev.mfxcomponents.theming.JavaFXThemes;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcomponents.theming.UserAgentBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.application.Application;
import javafx.css.Styleable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.scenicview.ScenicView;

import java.util.List;
import java.util.stream.IntStream;

import static io.github.palexdev.mfxresources.fonts.IconsProviders.FONTAWESOME_SOLID;

public class Sandbox extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane pane = new StackPane();

/*        MFXIconWrapper iw = new MFXIconWrapper()
            .setSize(18)
            .setIcon("fas-check");
        CSSFragment.Builder.build()
            .addSelector(".mfx-icon-wrapper")
            .background("#6750A4").backgroundRadius("2px")
            .closeSelector()
            .addSelector(".mfx-icon-wrapper > .mfx-font-icon")
            .addStyle("-mfx-size: 12px")
            .addStyle("-mfx-color: white")
            .closeSelector()
            .applyOn(iw);*/

        MFXCheckBox cb = new MFXCheckBox("Remember choice");
        cb.setAllowIndeterminate(true);
        cb.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) cb.setAllowIndeterminate(!cb.isAllowIndeterminate());
        });
        //cb.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
/*        CSSFragment.Builder.build()
            .addSelector(".mfx-checkbox > .surface")
            .prefWidth(40).prefHeight(40)
            .closeSelector()
            .addSelector(".mfx-checkbox > .mfx-icon-wrapper")
            .addStyle("-mfx-size: 18px")
            .border("gray").borderRadius("2px")
            .background("#6750A4").backgroundRadius("2px")
            .closeSelector()
            .addSelector(".mfx-checkbox > .mfx-icon-wrapper > .mfx-font-icon")
            .addStyle("-mfx-description: \"fas-check\"")
            .addStyle("-mfx-size: 12px")
            .closeSelector()
            .applyOn(cb);*/

        List<MFXIconButton> btns = IntStream.range(0, 5)
            .mapToObj(i -> {
                MFXIconButton btn = new MFXIconButton();
                btn.setIcon(FONTAWESOME_SOLID.randomIcon());
                createTooltip(btn, "Tooltip " + i);
                return btn;
            })
            .toList();
        HBox box = new HBox(20, btns.toArray(Node[]::new));
        box.setAlignment(Pos.CENTER);

        CustomDialog cd = CustomDialog.example(pane);
        MFXButton show = new MFXButton("Show").filled();
        show.setOnAction(e -> cd.show());
        MFXButton close = new MFXButton("Close").filled();
        close.setOnAction(e -> cd.hide());
        HBox aBox = new HBox(20, show, close);
        aBox.setAlignment(Pos.CENTER);

        pane.getChildren().add(box);
        Scene scene = new Scene(pane, 600, 600);

        // Style app
        Fonts.COMFORTAA.applyOn(scene);
        Fonts.ROBOTO.applyOn(scene);
        UserAgentBuilder.builder()
            .themes(JavaFXThemes.MODENA, MaterialThemes.INDIGO_DARK)
            .build()
            .setGlobal();
        pane.setStyle("-fx-background-color: -md-sys-color-background");

        primaryStage.setScene(scene);
        primaryStage.show();
        //ScenicView.show(scene);
    }

    void createTooltip(Node node, String text) {
        MFXTooltip tp = new MFXTooltip(node);
        tp.setContent(new MFXPlainContent());
        tp.setText(text);
        tp.setInDelay(M3Motion.SHORT2);
        tp.setOutDelay(Duration.ZERO);
        tp.install(node);
    }

    public static class CustomDialog extends Stage {
        public CustomDialog(Node content, Parent styleableParent) {
            StackPane wrapper = new StackPane(content) {
                @Override
                public Styleable getStyleableParent() {
                    return styleableParent;
                }
            };
            wrapper.setPrefSize(300, 300);
            wrapper.setStyle("-fx-background-color: -md-sys-color-background");

            Scene scene = new Scene(wrapper);
            scene.setFill(Color.TRANSPARENT);
            setScene(scene);
            setOnShown(e -> ScenicView.show(scene));
        }

        public static CustomDialog example(Parent styleableParent) {
            MFXCheckBox checkBox = new MFXCheckBox("Choice dialog");
            checkBox.setStyle("-fx-font-family: 'Comfortaa SemiBold'");
            return new CustomDialog(checkBox, styleableParent);
        }
    }
}
