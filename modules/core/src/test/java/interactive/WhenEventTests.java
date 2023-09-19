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

import io.github.palexdev.mfxcore.behavior.DisposableAction;
import io.github.palexdev.mfxcore.events.WhenEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class WhenEventTests {

	@Start
	void start(Stage stage) {
		stage.show();
	}

	@Test
	void testMultiple(FxRobot robot) {
		Button btn = setupStage();
		AtomicInteger cnt = new AtomicInteger();

		WhenEvent<MouseEvent> w1 = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.condition(e -> e.getButton() == MouseButton.PRIMARY)
			.process(e -> cnt.incrementAndGet())
			.register();

		robot.clickOn(btn);
		assertEquals(1, cnt.get());

		robot.clickOn(btn, MouseButton.SECONDARY);
		assertEquals(1, cnt.get());

		WhenEvent<KeyEvent> w2 = WhenEvent.intercept(btn, KeyEvent.KEY_PRESSED)
			.condition(e -> e.getCode() == KeyCode.ENTER)
			.process(e -> cnt.incrementAndGet())
			.register();

		if (!btn.isFocused()) robot.interact(btn::requestFocus);
		robot.press(KeyCode.ENTER);
		assertEquals(2, cnt.get());

		WhenEvent<MouseEvent> w3 = WhenEvent.intercept(btn, MouseEvent.MOUSE_PRESSED)
			.process(e -> cnt.incrementAndGet())
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

		WhenEvent<MouseEvent> w = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.process(e -> cnt.incrementAndGet())
			.oneShot()
			.register();

		robot.clickOn(btn);
		assertEquals(1, cnt.get());
		robot.clickOn(btn);
		assertEquals(1, cnt.get());
		assertTrue(w.isDisposed());
		assertEquals(0, WhenEvent.totalSize());
	}

	@Test
	void testAsFilter1(FxRobot robot) {
		Button btn = setupStage();
		AtomicInteger cnt = new AtomicInteger();

		WhenEvent<MouseEvent> w1 = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.process(e -> {
				cnt.incrementAndGet();
				e.consume();
			})
			.asFilter()
			.register();

		WhenEvent<MouseEvent> w2 = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.process(e -> cnt.incrementAndGet())
			.register();

		robot.clickOn(btn);
		assertEquals(1, cnt.get());

		WhenEvent<MouseEvent> w3 = WhenEvent.intercept(btn, MouseEvent.MOUSE_PRESSED)
			.process(e -> cnt.incrementAndGet())
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

		WhenEvent<MouseEvent> w1 = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.condition(e -> e.getButton() == MouseButton.SECONDARY)
			.process(e -> {
				cnt.incrementAndGet();
				e.consume();
			})
			.otherwise((w, e) -> {
				filterFailed.set(true);
				Optional.ofNullable(w.get()).ifPresent(DisposableAction::dispose);
			})
			.register();

		WhenEvent<MouseEvent> w2 = WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
			.process(e -> cnt.incrementAndGet())
			.register();

		robot.clickOn(btn);
		assertTrue(filterFailed.get());
		assertEquals(1, cnt.get());
		assertTrue(w1.isDisposed());
		assertEquals(1, WhenEvent.totalSize());
		w2.dispose();
	}

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
}
