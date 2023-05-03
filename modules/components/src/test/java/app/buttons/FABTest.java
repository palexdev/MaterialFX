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

package app.buttons;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcore.base.TriConsumer;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.function.Supplier;

import static io.github.palexdev.mfxcomponents.theming.enums.FABVariants.*;

public class FABTest extends Application {
    public static String variant = "light";

    private static final PseudoClass DISABLE = PseudoClass.getPseudoClass("disabled");
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    private static final PseudoClass FOCUS = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass PRESS = PseudoClass.getPseudoClass("pressed");

    @Override
    public void start(Stage primaryStage) {
        VBox pane = new VBox(50);
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color: #FFFBFE");

		pane.getChildren().addAll(
				createFABTestView("Primary", MFXFab::new),
				createFABTestView("Secondary", MFXFab::secondary),
				createFABTestView("Tertiary", MFXFab::tertiary),
				createFABTestView("Surface", MFXFab::surface),

				new Separator(Orientation.HORIZONTAL),

				createFABTestView("Lowered Primary", MFXFab::lowered),
				createFABTestView("Lowered Secondary", () -> MFXFab.secondary().addVariants(LOWERED)),
				createFABTestView("Lowered Tertiary", () -> MFXFab.tertiary().addVariants(LOWERED)),
				createFABTestView("Lowered Surface", () -> MFXFab.surface().addVariants(LOWERED)),

				new Separator(Orientation.HORIZONTAL),

				createFABTestView("Lowered Small Primary", () -> new MFXFab().addVariants(LOWERED, SMALL)),
				createFABTestView("Lowered Small Secondary", () -> MFXFab.secondary().addVariants(LOWERED, SMALL)),
				createFABTestView("Lowered Small Tertiary", () -> MFXFab.tertiary().addVariants(LOWERED, SMALL)),
				createFABTestView("Lowered Small Surface", () -> MFXFab.surface().addVariants(LOWERED, SMALL)),

				new Separator(Orientation.HORIZONTAL),

				createFABTestView("Lowered Large Primary", () -> new MFXFab().addVariants(LOWERED, LARGE)),
				createFABTestView("Lowered Large Secondary", () -> MFXFab.secondary().addVariants(FABVariants.LOWERED, LARGE)),
				createFABTestView("Lowered Large Tertiary", () -> MFXFab.tertiary().addVariants(FABVariants.LOWERED, LARGE)),
				createFABTestView("Lowered Large Surface", () -> MFXFab.surface().addVariants(FABVariants.LOWERED, LARGE))
		);
		String theme = MFXResources.load("themes/material/md-purple-" + variant + ".css");
		//String theme = MFXThemeManager.LIGHT.load();
		pane.getStylesheets().add(theme);
		pane.setPadding(InsetsBuilder.of(15, 5, 15, 5));

		ScrollPane sp = new ScrollPane(pane);
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
		sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		Scene scene = new Scene(sp, 1440, 900);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}

	private Node createButtonTestView(Supplier<MFXButton> factory) {
		TriConsumer<String, PseudoClass, MFXButton> cfg = (s, p, b) -> {
			b.setText(s);
			b.setGraphic(new MFXFontIcon("fas-circle"));
			if (p != null) {
				b.pseudoClassStateChanged(p, true);
				b.setMouseTransparent(true);
			}
		};

		MFXButton enabled = factory.get();
		cfg.accept("Enabled", null, enabled);

		MFXButton disabled = factory.get();
		cfg.accept("Disabled", DISABLE, disabled);

		MFXButton hover = factory.get();
		cfg.accept("Hover", HOVER, hover);

		MFXButton focused = factory.get();
		cfg.accept("Focused", FOCUS, focused);

		MFXButton pressed = factory.get();
		cfg.accept("Pressed", PRESS, pressed);

		MFXButton ripple = factory.get();
		cfg.accept("Ripple", null, ripple);
		automaticRipple(ripple);

		HBox box = new HBox(30, enabled, disabled, hover, focused, pressed, ripple);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private Node createFABTestView(String variant, Supplier<MFXFabBase> factory) {
		TriConsumer<String, PseudoClass, MFXFabBase> cfg = (s, p, b) -> {
			b.setText(s);
			b.setIcon(new MFXFontIcon("fas-circle"));
			if (p != null) {
				b.pseudoClassStateChanged(p, true);
				b.setMouseTransparent(true);
			}
			boolean isStandard = b.getStyleClass().contains("small") || b.getStyleClass().contains("large");
			b.setExtended(!isStandard);
		};

		MFXFabBase enabled = factory.get();
		cfg.accept("Enabled", null, enabled);

		MFXFabBase disabled = factory.get();
		cfg.accept("Disabled", DISABLE, disabled);

		MFXFabBase hover = factory.get();
		cfg.accept("Hover", HOVER, hover);

		MFXFabBase focused = factory.get();
		cfg.accept("Focused", FOCUS, focused);

		MFXFabBase pressed = factory.get();
		cfg.accept("Pressed", PRESS, pressed);

		MFXFabBase ripple = factory.get();
		cfg.accept("Ripple", null, ripple);
		automaticRipple(ripple);

		Label label = new Label(variant);
		label.setMinWidth(200);
		HBox box = new HBox(30, label, enabled, disabled, hover, focused, pressed, ripple);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private void automaticRipple(Node node) {
		TimelineBuilder.build()
				.setCycleCount(Animation.INDEFINITE)
				.add(KeyFrames.of(3000, e -> {
					Bounds sceb = node.localToScene(node.getLayoutBounds());
					Bounds scrb = node.localToScreen(node.getLayoutBounds());
					MouseEvent me = new MouseEvent(
							MouseEvent.MOUSE_PRESSED,
							sceb.getCenterX(), sceb.getCenterY(),
							scrb.getCenterX(), scrb.getCenterY(),
							MouseButton.PRIMARY, 1,
							false, false, false, false,
							false, false, false,
							false, false, false,
							null
					);
					Event.fireEvent(node, me);
				}))
				.getAnimation()
				.play();

		When.onChanged(node.parentProperty())
				.condition((o, n) -> n != null)
				.then((o, n) -> CSSFragment.Builder.build()
						.addSelector(".mfx-ripple-generator")
						.addStyle("-mfx-animation-speed: 0.4")
						.closeSelector()
						.applyOn(n)
				)
				.oneShot()
				.listen();
	}
}
