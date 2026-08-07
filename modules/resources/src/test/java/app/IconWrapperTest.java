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

import java.util.concurrent.ThreadLocalRandom;

import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.AnimationPresets;
import io.github.palexdev.mfxresources.icon.IconClip;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPack;
import io.github.palexdev.mfxresources.utils.IconUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class IconWrapperTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        // Set up like this, the ripple is in front of icon
        // But the set view order should fix it
        // Expect the effect behind the icon
        MFXIconWrapper icon = new MFXIconWrapper(IconUtils.randomIcon("fas-", 64.0, FontIconsPack.DEFAULT_COLOR), 128)
            .setAnimated(true)
            .setAnimationProvider(AnimationPresets.FADING)
            .enableRipple(true)
            .setIconClip(IconClip.of(IconClip.ClipShape.ROUNDED, -1.0));
        icon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String[] packs = {"fas-", "fab-", "far-"};
            String prefix = packs[ThreadLocalRandom.current().nextInt(0, packs.length)];
            String name = IconUtils.randomIconName(prefix).getValue();
            // This is to also test icon switch with CSS
            icon.setStyle("-mfx-icon: " + name);
        });
        icon.iconProperty().addListener(i -> icon.getIcon().setSize(64.0));

        // Check
        ObservableList<Node> children = icon.getChildrenUnmodifiable();
        assert children.get(0) instanceof MFXFontIcon;
        assert children.get(1) instanceof MFXRippleGenerator;

        pane.getChildren().add(icon);
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        //ScenicView.show(scene);
    }
}
