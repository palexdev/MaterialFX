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
import io.github.palexdev.mfxcore.controls.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxcore.base.beans.Position.position;
import static io.github.palexdev.mfxcore.base.beans.Size.size;
import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TestCustomControls {

    @Start
    void start(Stage stage) {
        stage.show();
    }

    @Test
    void testBehaviorInstalledWithFirstSkin(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        CustomBehavior behavior = control.getBehavior();
        assertNotNull(behavior);
        assertNull(control.getSkin());
        assertEquals(0, behavior.installs);

        robot.interact(() -> Event.fireEvent(control, new ActionEvent()));
        assertEquals(0, behavior.events);

        robot.interact(() -> pane.getChildren().add(control));
        CustomSkin skin = control.getCustomSkin();
        assertNotNull(skin);
        assertSame(behavior, control.getBehavior());
        assertEquals(1, behavior.installs);
        assertEquals(0, behavior.disposes);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(0.5);
        });
        assertEquals(1, behavior.events);
        assertEquals(1, behavior.invalidations);
        assertEquals(1, behavior.skinCalls);
        assertEquals(1, skin.events);
        assertEquals(1, skin.invalidations);
    }

    @Test
    void testBehaviorChangeBeforeSkin(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        CustomBehavior first = control.getBehavior();

        robot.interact(() -> control.setBehaviorFactory(() -> new CustomBehavior(control)));
        CustomBehavior second = control.getBehavior();
        assertNotSame(first, second);
        assertEquals(0, first.installs);
        assertEquals(1, first.disposes);
        assertEquals(0, second.installs);
        assertEquals(0, second.disposes);

        robot.interact(() -> pane.getChildren().add(control));
        assertNotNull(control.getCustomSkin());
        assertSame(second, control.getBehavior());
        assertEquals(0, first.installs);
        assertEquals(1, second.installs);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(0.5);
        });
        assertEquals(0, first.events);
        assertEquals(0, first.invalidations);
        assertEquals(0, first.skinCalls);
        assertEquals(1, second.events);
        assertEquals(1, second.invalidations);
        assertEquals(1, second.skinCalls);
    }

    @Test
    void testPreloadSkin(FxRobot robot) {
        CustomControl control = new CustomControl();
        CustomBehavior behavior = control.getBehavior();

        robot.interact(control::preloadSkin);
        CustomSkin skin = control.getCustomSkin();
        assertNotNull(skin);
        assertEquals(1, behavior.installs);

        robot.interact(control::preloadSkin);
        assertSame(skin, control.getCustomSkin());
        assertEquals(0, skin.disposes);
        assertEquals(1, behavior.installs);

        robot.interact(() -> Event.fireEvent(control, new ActionEvent()));
        assertEquals(1, behavior.events);
        assertEquals(1, behavior.skinCalls);
        assertEquals(1, skin.events);
    }

    @Test
    void testSkinFactoryChangeKeepsBehavior(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        robot.interact(() -> pane.getChildren().add(control));

        CustomBehavior behavior = control.getBehavior();
        CustomSkin first = control.getCustomSkin();
        assertNotNull(first);

        robot.interact(() -> control.setSkinFactory(() -> new CustomSkin(control)));
        CustomSkin second = control.getCustomSkin();
        assertNotSame(first, second);
        assertEquals(1, first.disposes);
        assertEquals(0, second.disposes);
        assertSame(behavior, control.getBehavior());
        assertEquals(1, behavior.installs);
        assertEquals(0, behavior.disposes);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(0.5);
        });
        assertEquals(0, first.events);
        assertEquals(0, first.invalidations);
        assertEquals(1, second.events);
        assertEquals(1, second.invalidations);
        assertEquals(1, behavior.events);
        assertEquals(1, behavior.invalidations);
        assertEquals(1, behavior.skinCalls);
    }

    @Test
    void testSetSkinKeepsBehavior(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        robot.interact(() -> pane.getChildren().add(control));

        CustomBehavior behavior = control.getBehavior();
        CustomSkin first = control.getCustomSkin();
        assertNotNull(first);

        robot.interact(() -> control.setSkin(new CustomSkin(control)));
        CustomSkin second = control.getCustomSkin();
        assertNotSame(first, second);
        assertEquals(1, first.disposes);
        assertEquals(0, second.disposes);
        assertSame(behavior, control.getBehavior());
        assertEquals(1, behavior.installs);
        assertEquals(0, behavior.disposes);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(0.5);
        });
        assertEquals(0, first.events);
        assertEquals(0, first.invalidations);
        assertEquals(1, second.events);
        assertEquals(1, second.invalidations);
        assertEquals(1, behavior.events);
        assertEquals(1, behavior.invalidations);
        assertEquals(1, behavior.skinCalls);
    }

    @Test
    void testBehaviorChangeKeepsSkin(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        robot.interact(() -> pane.getChildren().add(control));

        CustomBehavior first = control.getBehavior();
        CustomSkin skin = control.getCustomSkin();
        assertNotNull(skin);

        robot.interact(() -> control.setBehaviorFactory(() -> new CustomBehavior(control)));
        CustomBehavior second = control.getBehavior();
        assertNotSame(first, second);
        assertEquals(1, first.disposes);
        assertEquals(1, second.installs);
        assertEquals(0, second.disposes);
        assertSame(skin, control.getCustomSkin());
        assertEquals(0, skin.disposes);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(0.5);
        });
        assertEquals(0, first.events);
        assertEquals(0, first.invalidations);
        assertEquals(0, first.skinCalls);
        assertEquals(1, second.events);
        assertEquals(1, second.invalidations);
        assertEquals(1, second.skinCalls);
        assertEquals(1, skin.events);
        assertEquals(1, skin.invalidations);
    }

    @Test
    void testNullBehavior(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();
        robot.interact(() -> pane.getChildren().add(control));

        CustomBehavior first = control.getBehavior();
        CustomSkin skin = control.getCustomSkin();
        assertNotNull(skin);

        robot.interact(() -> control.setBehaviorFactory(null));
        assertNull(control.getBehavior());
        assertEquals(1, first.disposes);

        robot.interact(() -> control.setOpacity(0.5));
        assertEquals(0, first.invalidations);
        assertEquals(1, skin.invalidations);

        robot.interact(control::setDefaultBehaviorFactory);
        CustomBehavior second = control.getBehavior();
        assertNotNull(second);
        assertNotSame(first, second);
        assertEquals(1, second.installs);
        assertSame(skin, control.getCustomSkin());
        assertEquals(0, skin.disposes);

        robot.interact(() -> {
            Event.fireEvent(control, new ActionEvent());
            control.setOpacity(1.0);
        });
        assertEquals(0, first.events);
        assertEquals(1, second.events);
        assertEquals(1, second.invalidations);
        assertEquals(1, second.skinCalls);
        assertEquals(1, skin.events);
        assertEquals(2, skin.invalidations);
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

    //================================================================================
    // Misc
    //================================================================================

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

    static class CustomBehavior extends MFXBehavior<CustomControl> {
        int installs = 0;
        int disposes = 0;
        int events = 0;
        int invalidations = 0;
        int skinCalls = 0;

        public CustomBehavior(CustomControl node) {
            super(node);
        }

        @Override
        protected void install() {
            installs++;
            CustomControl control = getNode();
            onInput(intercept(control, ActionEvent.ACTION).handle(_ -> events++));
            listen(onInvalidated(control.opacityProperty()).then(_ -> invalidations++));
        }

        void onSkinAction() {
            skinCalls++;
        }

        @Override
        public void dispose() {
            disposes++;
            super.dispose();
        }
    }

    static class CustomSkin extends MFXSkinBase<CustomControl> {
        int disposes = 0;
        int events = 0;
        int invalidations = 0;

        public CustomSkin(CustomControl control) {
            super(control);
        }

        @Override
        public void install() {
            CustomControl control = getSkinnable();
            onInput(intercept(control, ActionEvent.ACTION).handle(_ -> {
                events++;
                behaviorAs(CustomBehavior.class).onSkinAction();
            }));
            listen(onInvalidated(control.opacityProperty()).then(_ -> invalidations++));
        }

        @Override
        public void dispose() {
            disposes++;
            super.dispose();
        }
    }

    static class CustomControl extends MFXControl {

        {
            getStyleClass().add("my-control");
        }

        @Override
        public CustomBehavior getBehavior() {
            return (CustomBehavior) super.getBehavior();
        }

        public CustomSkin getCustomSkin() {
            return (CustomSkin) getSkin();
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
}
