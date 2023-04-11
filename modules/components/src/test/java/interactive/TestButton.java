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

package interactive;

import com.sun.javafx.tk.Toolkit;
import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static io.github.palexdev.mfxcore.utils.fx.LayoutUtils.snappedBoundHeight;
import static io.github.palexdev.mfxcore.utils.fx.LayoutUtils.snappedBoundWidth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class TestButton {
    private static Stage stage;

    @Start
    private void start(Stage stage) {
        TestButton.stage = stage;
        //stage.show();
    }

    @Test
    void testSizes(FxRobot robot) {
        StackPane root = setupStage();
        MFXButton button = new MFXButton("MFXButton");
        robot.interact(() -> {
            stage.show();
            root.getChildren().setAll(button);
        });

        // Test Pref
        robot.interact(() -> button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE));
        assertEquals(snappedBoundWidth(button), button.getWidth());
        assertEquals(snappedBoundHeight(button), button.getHeight());

        // Test max
        robot.interact(() -> {
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.applyCss();
            button.layout();
            button.requestLayout();
            Toolkit.getToolkit().requestNextPulse();
        });
        assertEquals(200.0, button.getWidth());
        assertEquals(200.0, button.getHeight());
    }

    @Test
    void testInitBehavior(FxRobot robot) {
        StackPane root = setupStage();
        MFXButton filled = new MFXFilledButton() {
            @Override
            public Supplier<MFXButtonBehavior> defaultBehaviorProvider() {
                return () -> new InitBehavior(this);
            }
        };
        filled.setText("Init Button");
        filled.setSkin(new InitSkin(filled));
        robot.interact(() -> root.getChildren().setAll(filled));

        InitBehavior b = (InitBehavior) filled.getBehavior();
        assertTrue(b.isInit);
    }

    private StackPane setupStage() {
        StackPane sp = new StackPane();
        try {
            Scene scene = new Scene(sp, 200, 200);
            MFXThemeManager.PURPLE_LIGHT.addOn(scene);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return sp;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private static class InitBehavior extends MFXButtonBehavior {
        private boolean isInit = false;

        public InitBehavior(MFXButton node) {
            super(node);
        }

        public void init() {
            if (isInit) throw new IllegalStateException("Behavior already initialized!");
            isInit = true;
        }
    }

    private static class InitSkin extends MFXButtonSkin {

        public InitSkin(MFXButton button) {
            super(button);
        }

        @Override
        protected void initBehavior(MFXButtonBehavior behavior) {
            super.initBehavior(behavior);
            ((InitBehavior) behavior).init();
        }
    }
}
