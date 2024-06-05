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

import io.github.palexdev.mfxeffects.enums.RippleState;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxresources.builders.IconWrapperBuilder;
import io.github.palexdev.mfxresources.fonts.IconsProviders;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeBrands;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeRegular;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import io.github.palexdev.mfxresources.utils.EnumUtils;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
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

import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IconsTests {
	private static final long sleep = 500L;
	private static Stage stage;

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

		icon.set(FontAwesomeSolid.random(Color.RED, 64.0));
		robot.interact(() -> root.getChildren().setAll(icon.get()));
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		Thread.sleep(sleep);

		icon.set(FontAwesomeSolid.random(Color.RED, 64.0));
		robot.interact(() -> root.getChildren().setAll(icon.get()));
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		Thread.sleep(sleep);
	}

	@Test
	void testProviders(FxRobot robot) throws InterruptedException {
		StackPane root = setupStage();
		final AtomicReference<MFXFontIcon> icon = new AtomicReference<>();
		icon.set(new MFXFontIcon()
			.setIconsProvider(IconsProviders.FONTAWESOME_BRANDS)
			.setDescription("fab-google")
			.setSize(64.0));
		robot.interact(() -> root.getChildren().setAll(icon.get()));
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		Thread.sleep(sleep);

		icon.set(new MFXFontIcon()
			.setIconsProvider(IconsProviders.FONTAWESOME_REGULAR)
			.setDescription("far-compass")
			.setSize(64.0));
		robot.interact(() -> root.getChildren().setAll(icon.get()));
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		Thread.sleep(sleep);

		AtomicReference<Exception> exRef = new AtomicReference<>();
		icon.set(new MFXFontIcon("fas-circle", 64.0) {
			{
				textProperty().bind(Bindings.createStringBinding(
					() -> {
						try {
							String desc = getDescription();
							return (desc != null && !desc.isBlank()) ? descToCode(desc) : "";
						} catch (Exception ex) {
							exRef.set(ex);
							ex.printStackTrace();
							return "";
						}
					}, descriptionProperty(), fontProperty()
				));
			}
		});
		robot.interact(() -> root.getChildren().setAll(icon.get()));
		Thread.sleep(sleep);
		icon.get().setIconsProvider(IconsProviders.FONTAWESOME_BRANDS);
		icon.get().setDescription("fab-google");
		assertNull(exRef.get());
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		Thread.sleep(sleep);
	}

	@Test
	void testCustomProviders(FxRobot robot) throws InterruptedException {
		StackPane root = setupStage();
		final AtomicReference<MFXFontIcon> icon = new AtomicReference<>(new MFXFontIcon());
		robot.interact(() -> root.getChildren().setAll(icon.get()));

		icon.get().setIconsProvider(
			Font.loadFont(new Win10IkonHandler().getFontResourceAsStream(), 64.0),
			s -> Optional.ofNullable(Win10.findByDescription(s)).map(w -> (char) w.getCode())
				.orElse('\0')
		);
		icon.get().setDescription(EnumUtils.randomEnum(Win10.class).getDescription());
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		assertNotEquals("\0", icon.get().symbolToCode());
		Thread.sleep(sleep);

		icon.get().setIconsProvider(
			Font.loadFont(new FluentUiRegularALIkonHandler().getFontResourceAsStream(), 64.0),
			s -> Optional.ofNullable(FluentUiRegularAL.findByDescription(s)).map(f -> (char) f.getCode())
				.orElse('\0')
		);
		icon.get().setDescription(EnumUtils.randomEnum(FluentUiRegularAL.class).getDescription());
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		assertNotEquals("\0", icon.get().symbolToCode());
		Thread.sleep(sleep);

		icon.get().setIconsProvider(MFXFontIcon.defaultProvider());
		icon.get().setDescription(EnumUtils.randomEnum(FontAwesomeSolid.class).getDescription());
		assertEquals(64.0, icon.get().getFont().getSize());
		assertEquals(64.0, icon.get().getSize());
		assertNotEquals("\0", icon.get().symbolToCode());
	}

	@Test
	void testWrap(FxRobot robot) throws InterruptedException {
		StackPane root = setupStage();
		MFXIconWrapper wrapper = new MFXFontIcon(FontAwesomeSolid.CIRCLE.getDescription(), 64.0).wrap();
		robot.interact(() -> root.getChildren().setAll(wrapper));
		assertEquals(64.0, wrapper.getSize());
		assertEquals(64.0, wrapper.getWidth());
		assertEquals(64.0, wrapper.getHeight());
		Thread.sleep(sleep);

		wrapper.getIcon()
			.setIconsProvider(IconsProviders.FONTAWESOME_BRANDS)
			.setDescription(EnumUtils.randomEnum(FontAwesomeBrands.class).getDescription());
		Thread.sleep(sleep);

		robot.interact(() -> wrapper.setIcon(FontAwesomeSolid.random(Color.web("#454545"), 64.0)));
		Thread.sleep(sleep);

		robot.interact(() -> {
			wrapper.getIcon().setIconsProvider(IconsProviders.FONTAWESOME_REGULAR);
			wrapper.getIcon().setDescription(FontAwesomeRegular.SQUARE.getDescription());
			wrapper.setStyle("-mfx-enable-ripple: true;\n-mfx-round: true;\n-mfx-ripple-pref-size: \"128.0 128.0\"");
		});
		assertNotNull(wrapper.getClip());
		assertEquals(2, wrapper.getChildren().size());
		robot.clickOn(wrapper);
		Thread.sleep(1000);
		assertEquals(RippleState.INACTIVE, wrapper.getRippleGenerator().getRippleState());
		assertTrue(wrapper.getRippleGenerator().getChildrenUnmodifiable().get(0).getOpacity() < 1.0);

		robot.interact(() -> wrapper.setStyle(null));
		assertNull(wrapper.getClip());
		assertEquals(1, wrapper.getChildren().size());

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
		for (Node child : icon.getChildren()) {
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
		for (Node child : icon.getChildren()) {
			if (child instanceof RippleGenerator) {
				assertEquals(0, child.getViewOrder());
				continue;
			}
			assertEquals(1, child.getViewOrder());
		}

		robot.interact(() -> icon.enableRippleGenerator(false));
		assertTrue(icon.getChildren().size() == 1 && !(icon.getChildren().get(0) instanceof MFXRippleGenerator));
	}

	@Test
	void testSizes(FxRobot robot) {
		StackPane root = setupStage();
		MFXIconWrapper icon = new MFXIconWrapper()
			.setIcon("fas-circle")
			.makeRound(true)
			.setSize(32);
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
}