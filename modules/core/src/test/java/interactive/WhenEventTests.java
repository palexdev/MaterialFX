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

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.input.WhenEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(ApplicationExtension.class)
public class WhenEventTests {

    private Stage stage;
    private static final Set<WhenEvent<?>> whens = new WeakHashSet<>();

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.show();
    }

    @BeforeEach
    void tearDown() {
        whens.forEach(Disposable::dispose);
        whens.clear();
    }

    @Test
    void testMultiple(FxRobot robot) {
        Button btn = setupStage();
        AtomicInteger cnt = new AtomicInteger();

        WhenEvent<MouseEvent> w1 = intercept(btn, MouseEvent.MOUSE_CLICKED)
            .condition(e -> e.getButton() == MouseButton.PRIMARY)
            .handle(e -> cnt.incrementAndGet())
            .register();

        robot.clickOn(btn);
        assertEquals(1, cnt.get());

        robot.clickOn(btn, MouseButton.SECONDARY);
        assertEquals(1, cnt.get());

        WhenEvent<KeyEvent> w2 = intercept(btn, KeyEvent.KEY_PRESSED)
            .condition(e -> e.getCode() == KeyCode.ENTER)
            .handle(e -> cnt.incrementAndGet())
            .register();

        if (!btn.isFocused()) robot.interact(btn::requestFocus);
        robot.press(KeyCode.ENTER);
        assertEquals(2, cnt.get());

        WhenEvent<MouseEvent> w3 = intercept(btn, MouseEvent.MOUSE_PRESSED)
            .handle(e -> cnt.incrementAndGet())
            .register();

        robot.clickOn(btn);
        assertEquals(4, cnt.get());

        robot.rightClickOn(btn);
        assertEquals(5, cnt.get());

        assertEquals(3, WhenEvent.totalSize());
        WhenEvent.dispose(w1, w2, w3);
        assertEquals(0, WhenEvent.totalSize());

        robot.clickOn(btn);
        assertEquals(5, cnt.get());
    }

    @Test
    void testOneShot(FxRobot robot) {
        Button btn = setupStage();
        AtomicInteger cnt = new AtomicInteger();

        WhenEvent<?> w = intercept(btn, ActionEvent.ACTION)
            .handle(e -> cnt.incrementAndGet())
            .oneShot()
            .register();

        robot.interact(btn::fire);
        assertEquals(1, cnt.get());
        robot.interact(btn::fire);
        assertEquals(1, cnt.get());
        assertTrue(w.isDisposed());
        assertEquals(0, WhenEvent.totalSize());
    }

    @Test
    void testAsFilter1(FxRobot robot) {
        Button btn = setupStage();
        AtomicInteger cnt = new AtomicInteger();

        WhenEvent<MouseEvent> w1 = intercept(btn, MouseEvent.MOUSE_CLICKED)
            .handle(e -> {
                cnt.incrementAndGet();
                e.consume();
            })
            .asFilter()
            .register();

        WhenEvent<MouseEvent> w2 = intercept(btn, MouseEvent.MOUSE_CLICKED)
            .handle(e -> cnt.incrementAndGet())
            .register();

        robot.clickOn(btn);
        assertEquals(1, cnt.get());

        WhenEvent<MouseEvent> w3 = intercept(btn, MouseEvent.MOUSE_PRESSED)
            .handle(e -> cnt.incrementAndGet())
            .register();

        robot.clickOn(btn);
        assertEquals(3, cnt.get());

        assertEquals(3, WhenEvent.totalSize());
        WhenEvent.dispose(w1, w2, w3);
        assertEquals(0, WhenEvent.totalSize());
    }

    @Test
    void testAsFilter2(FxRobot robot) {
        Button btn = setupStage();
        AtomicBoolean filterFailed = new AtomicBoolean(false);
        AtomicInteger cnt = new AtomicInteger();

        WhenEvent<MouseEvent> w1 = intercept(btn, MouseEvent.MOUSE_CLICKED)
            .condition(e -> e.getButton() == MouseButton.SECONDARY)
            .handle(e -> {
                cnt.incrementAndGet();
                e.consume();
            })
            .otherwise((w, e) -> {
                filterFailed.set(true);
                Optional.ofNullable(w.get()).ifPresent(Disposable::dispose);
            })
            .register();

        WhenEvent<MouseEvent> w2 = intercept(btn, MouseEvent.MOUSE_CLICKED)
            .handle(e -> cnt.incrementAndGet())
            .register();

        robot.clickOn(btn);
        assertTrue(filterFailed.get());
        assertEquals(1, cnt.get());
        assertTrue(w1.isDisposed());
        assertEquals(1, WhenEvent.totalSize());
        w2.dispose();
    }

    @Test
    void testGC(FxRobot robot) throws Exception {
        Button btn = setupStage();
        WeakReference<WhenEvent<?>> ref;
        WhenEvent<MouseEvent> when = intercept(btn, MouseEvent.MOUSE_CLICKED).register();

        ref = new WeakReference<>(when);
        robot.interact(() -> ((Pane) stage.getScene().getRoot()).getChildren().clear());
        when = null;
        btn = null;
        awaitGC(ref);
        assumeTrue(ref.get() == null, "GC did not collect the WhenEvent - skipping");
        assertEquals(0, WhenEvent.totalSize());
    }

    //================================================================================
    // Helpers
    //================================================================================

    Button setupStage() {
        Button btn = new Button("A button");
        StackPane sp = new StackPane(btn);
        try {
            Scene scene = new Scene(sp, 200, 200);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return btn;
    }

    private <T extends Event> WhenEvent<T> intercept(EventTarget target, EventType<T> evt) {
        WhenEvent<T> when = WhenEvent.intercept(target, evt);
        whens.add(when);
        return when;
    }

    /// Hints GC in a short loop until the referent is collected or the attempt limit is reached.
    /// GC is non-deterministic; callers must guard with assumeTrue(ref.get() == null).
    private static void awaitGC(WeakReference<?> ref) throws InterruptedException {
        for (int i = 0; i < 50 && ref.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }
}
