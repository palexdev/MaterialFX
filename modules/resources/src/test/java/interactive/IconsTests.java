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

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import io.github.palexdev.mfxeffects.enums.RippleState;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxresources.builders.IconBuilder;
import io.github.palexdev.mfxresources.builders.IconWrapperBuilder;
import io.github.palexdev.mfxresources.icon.IconClip;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPack;
import io.github.palexdev.mfxresources.icon.packs.FontIconsPacks;
import io.github.palexdev.mfxresources.utils.EnumUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularALIkonHandler;
import org.kordamp.ikonli.win10.Win10;
import org.kordamp.ikonli.win10.Win10IkonHandler;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static io.github.palexdev.mfxresources.utils.IconUtils.randomIcon;
import static io.github.palexdev.mfxresources.utils.IconUtils.randomIconName;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IconsTests {
    private static final long sleep = 500L;
    private static Stage stage;

    static {
        FontIconsPacks.register("win10-", new FontIconsPack() {
            @Override
            public String name() {
                return FontIconsPack.super.name();
            }

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
    }

    @Start
    void start(Stage stage) {
        IconsTests.stage = stage;
        stage.show();
    }

    @Test
    void testConstructors(FxRobot robot) throws InterruptedException {
        StackPane root = setupStage();
        final AtomicReference<MFXFontIcon> icon = new AtomicReference<>(new MFXFontIcon());
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertTrue(icon.get().getText().isBlank());

        icon.set(new MFXFontIcon(null, 32.0, Color.RED));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertNull(icon.get().getIconsPack());
        assertTrue(icon.get().getText().isBlank());

        icon.set(new MFXFontIcon(""));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertTrue(icon.get().getText().isBlank());

        icon.set(new MFXFontIcon("fas-0"));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(String.valueOf('\uE900'), icon.get().getText());

        icon.set(new MFXFontIcon("fas-circle", 32.0));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(32.0, icon.get().getLayoutBounds().getWidth());
        assertEquals(32.0, icon.get().getLayoutBounds().getHeight());
        assertEquals(32.0, icon.get().getFont().getSize());
        assertEquals(32.0, icon.get().getSize());

        icon.set(new MFXFontIcon("fas-icons", 64.0, Color.RED));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(Color.RED, icon.get().getFill());
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        Thread.sleep(sleep);

        icon.set(randomIcon("fas-", 64.0, Color.RED));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        Thread.sleep(sleep);

        icon.set(randomIcon("fas-", 64.0, Color.RED));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        Thread.sleep(sleep);
    }

    @Test
    void testPacks(FxRobot robot) throws InterruptedException {
        StackPane root = setupStage();
        final AtomicReference<MFXFontIcon> icon = new AtomicReference<>();
        icon.set(new MFXFontIcon()
            .setIconName("fab-google")
            .setSize(64.0));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        Thread.sleep(sleep);

        icon.set(new MFXFontIcon()
            .setIconName("far-compass")
            .setSize(64.0));
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        Thread.sleep(sleep);

        AtomicReference<Exception> exRef = new AtomicReference<>();
        icon.set(new MFXFontIcon("null", 64.0) {
            @Override
            protected void update() {
                try {
                    super.update();
                    exRef.set(null);
                } catch (Exception ex) {
                    exRef.set(ex);
                    ex.printStackTrace();
                }
            }
        });
        robot.interact(() -> root.getChildren().setAll(icon.get()));
        assertNotNull(exRef.get());
        assertSame(IllegalStateException.class, exRef.get().getClass());

        Thread.sleep(sleep);
        icon.get().setIconName("fab-google");
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        assertNull(exRef.get());
        Thread.sleep(sleep);
    }

    @Test
    void testCustomPacks(FxRobot robot) throws InterruptedException {
        StackPane root = setupStage();
        final AtomicReference<MFXFontIcon> icon = new AtomicReference<>(new MFXFontIcon());
        icon.get().setSize(64.0);
        robot.interact(() -> root.getChildren().setAll(icon.get()));

        robot.interact(() -> icon.get().setIconName(EnumUtils.randomEnum(Win10.class).getDescription()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        assertNotEquals("\0", FontIconsPack.textToUnicode(icon.get().getText()));
        Thread.sleep(sleep);

        robot.interact(() -> icon.get().setIconName(EnumUtils.randomEnum(FluentUiRegularAL.class).getDescription()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        assertNotEquals("\0", FontIconsPack.textToUnicode(icon.get().getText()));
        Thread.sleep(sleep);

        robot.interact(() -> icon.get().setIconName(randomIconName("fas-").getValue()));
        assertEquals(64.0, icon.get().getFont().getSize());
        assertEquals(64.0, icon.get().getSize());
        assertNotEquals("\0", FontIconsPack.textToUnicode(icon.get().getText()));
    }

    @Test
    void testWrap(FxRobot robot) throws InterruptedException {
        StackPane root = setupStage();
        MFXIconWrapper wrapper = IconBuilder.build(new MFXFontIcon("fas-circle", 64.0)).wrap();
        robot.interact(() -> root.getChildren().setAll(wrapper));
        assertEquals(-1.0, wrapper.getSize());
        assertEquals(64.0, wrapper.getWidth());
        assertEquals(64.0, wrapper.getHeight());
        Thread.sleep(sleep);

        wrapper.getIcon()
            .setIconName(randomIconName("fab-").getValue());
        Thread.sleep(sleep);

        robot.interact(() -> wrapper.setIcon(randomIcon("fas-", 64.0, Color.web("#454545"))));
        Thread.sleep(sleep);

        robot.interact(() -> {
            wrapper.getIcon().setIconName("far-square");
            wrapper.setStyle("-mfx-enable-ripple: true;\n-mfx-clip: \"rounded\";");
        });
        assertNotNull(wrapper.getClip());
        assertEquals(2, wrapper.getChildrenUnmodifiable().size());
        robot.clickOn(wrapper);
        Thread.sleep(1000);
        assertEquals(RippleState.INACTIVE, wrapper.getRippleGenerator().getRippleState());
        assertTrue(wrapper.getRippleGenerator().getChildrenUnmodifiable().getFirst().getOpacity() < 1.0);

        robot.interact(() -> wrapper.setStyle(null));
        assertNull(wrapper.getClip());
        assertEquals(1, wrapper.getChildrenUnmodifiable().size());

        new MFXIconWrapper();
        new MFXIconWrapper(null);
        new MFXIconWrapper(null, 32.0);
    }

    @Test
    void testWrapperEnableRipple(FxRobot robot) {
        StackPane root = setupStage();
        MFXIconWrapper icon = IconWrapperBuilder.build()
            .setIcon("fas-circle")
            .enableRippleGenerator(true)
            .get();
        robot.interact(() -> root.getChildren().setAll(icon));
        for (Node child : icon.getChildrenUnmodifiable()) {
            if (child instanceof RippleGenerator) {
                assertEquals(0, child.getViewOrder());
                continue;
            }
            assertEquals(1, child.getViewOrder());
        }
    }

    @Test
    void testWrapperEnableDisableRipple(FxRobot robot) {
        StackPane root = setupStage();
        MFXIconWrapper icon = IconWrapperBuilder.build()
            .setIcon("fas-circle")
            .enableRippleGenerator(true)
            .get();
        robot.interact(() -> root.getChildren().setAll(icon));
        for (Node child : icon.getChildrenUnmodifiable()) {
            if (child instanceof RippleGenerator) {
                assertEquals(0, child.getViewOrder());
                continue;
            }
            assertEquals(1, child.getViewOrder());
        }

        robot.interact(() -> icon.enableRipple(false));
        assertTrue(icon.getChildrenUnmodifiable().size() == 1 && !(icon.getChildrenUnmodifiable().getFirst() instanceof MFXRippleGenerator));
    }

    @Test
    void testSizes(FxRobot robot) {
        StackPane root = setupStage();
        MFXIconWrapper icon = new MFXIconWrapper()
            .setIcon("fas-circle")
            .setIconClip(IconClip.of(IconClip.ClipShape.ROUNDED, -1.0))
            .setSize(32.0);
        robot.interact(() -> root.getChildren().setAll(icon));

        assertEquals(32.0, icon.getWidth());
        assertEquals(32.0, icon.getHeight());
    }

    private StackPane setupStage() {
        try {
            FxToolkit.setupStage(s -> s.setScene(new Scene(new StackPane(), 100, 100)));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return ((StackPane) stage.getScene().getRoot());
    }

    @Test
    void testRapidChangeWithDifferentPacks(FxRobot robot) {
        StackPane root = setupStage();
        MFXIconWrapper icon = new MFXIconWrapper()
            .setIcon("fas-circle")
            .setIconClip(IconClip.of(IconClip.ClipShape.ROUNDED, -1.0))
            .setSize(32);
        robot.interact(() -> root.getChildren().setAll(icon));

        String[] packs = {"fas-", "fab-", "far-", "fltral-", "win10-"};

        for (int i = 0; i < 100; i++) {
            String prefix = packs[ThreadLocalRandom.current().nextInt(0, packs.length)];
            Pair<FontIconsPack, String> pair = randomIconName(prefix);
            robot.interact(() -> icon.setIcon(pair.getValue()));
            assertNotNull(pair.getKey().icon(icon.getIcon().getIconName()));
        }
    }
}
