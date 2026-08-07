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

package interactive;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.base.properties.SizeProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxcore.base.beans.Position.position;
import static io.github.palexdev.mfxcore.base.beans.Size.size;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TestCustomControls {

    @Start
    void start(Stage stage) {
        stage.show();
    }

    @BeforeEach
    void setup() {
        CustomBehavior.instances = 0;
        CustomSkin.instances = 0;
    }

    @Test
    void testCustomControl(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();

        // The behavior is created with the control
        assertEquals(1, CustomBehavior.instances);
        assertEquals(0, CustomSkin.instances);

        robot.interact(() -> pane.getChildren().add(control));

        assertEquals(1, CustomBehavior.instances);
        assertEquals(1, CustomSkin.instances);
        assertEquals(1, control.getBehavior().initCount);
        Skin<?> skin = control.getSkin();

        robot.interact(() -> {
            pane.getChildren().clear();
            assertNull(control.getScene());
            control.setSkinFactory(() -> new CustomSkin(control) {
                final Label label = new Label("Hello world!");

                {
                    getChildren().setAll(label);
                }
            });
            pane.getChildren().add(control);
        });

        assertNotSame(skin, control.getSkin());
        assertEquals(1, CustomBehavior.instances);
        assertEquals(2, CustomSkin.instances);
        assertEquals(2, control.getBehavior().initCount);
        assertEquals(1, control.getChildrenUnmodifiable().size());

        robot.interact(() ->
            control.setSkinFactory(() -> new CustomSkin(control) {
                final Label label = new Label("Hello custom world!");

                {
                    getChildren().setAll(label);
                }
            })
        );

        assertEquals(1, CustomBehavior.instances);
        assertEquals(3, CustomSkin.instances);
        assertEquals(3, control.getBehavior().initCount);
        assertEquals(1, control.getChildrenUnmodifiable().size());
        Label label = (Label) control.lookup(".label");
        assertEquals("Hello custom world!", label.getText());
    }

    @Test
    void testStyleable(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        robot.interact(() -> pane.getChildren().add(control));

        assertEquals(Size.zero(), control.size.get());
        assertEquals(Position.origin(), control.position.get());

        robot.interact(() ->
            CSSFragment.applyOn("""
                    .my-control {
                      -size: 40px 80px;
                      -position: 25px 10px;
                    }
                    """,
                pane
            )
        );

        assertEquals(size(40.0, 80.0), control.size.get());
        assertEquals(position(25.0, 10.0), control.position.get());
    }

    StackPane setupStage() {
        StackPane pane = new StackPane();
        try {
            Scene scene = new Scene(pane, 240, 240);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return pane;
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    static class CustomControl extends MFXControl {

        {
            getStyleClass().add("my-control");
        }

        @Override
        public CustomBehavior getBehavior() {
            return ((CustomBehavior) super.getBehavior());
        }

        @Override
        public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
            return () -> new CustomBehavior(this);
        }

        @Override
        public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
            return () -> new CustomSkin(this);
        }

        private final StyleableObjectProperty<Size> size = SizeProperty.styleableProperty(
            _SIZE,
            this,
            "size",
            Size.zero()
        );

        private final StyleableObjectProperty<Position> position = PositionProperty.styleableProperty(
            _POSITION,
            this,
            "position",
            Position.origin()
        );

        private static final CssMetaData<CustomControl, Size> _SIZE = SizeProperty.cssMetaData(
            "-size",
            c -> c.size,
            Size.zero()
        );

        private static final CssMetaData<CustomControl, Position> _POSITION = PositionProperty.cssMetaData(
            "-position",
            c -> c.position,
            Position.origin()
        );

        private static final List<CssMetaData<? extends Styleable, ?>> CSS_META = StyleUtils.cssMetaDataList(
            MFXControl.getClassCssMetaData(),
            _SIZE, _POSITION
        );

        public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
            return CSS_META;
        }

        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return getClassCssMetaData();
        }

    }

    static class CustomBehavior extends MFXBehavior<CustomControl> {
        public static int instances = 0;
        public int initCount = 0;

        public CustomBehavior(CustomControl node) {
            super(node);
            instances++;
        }

        @Override
        public void init() {
            initCount++;
        }
    }

    static class CustomSkin extends MFXSkinBase<CustomControl> {
        public static int instances = 0;

        public CustomSkin(CustomControl node) {
            super(node);
            instances++;
        }
    }
}
