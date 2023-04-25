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

import app.ComponentsLauncher;
import app.others.ui.*;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.mfxcomponents.controls.buttons.*;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.CSSFragment;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.List;
import java.util.function.BiFunction;

import static io.github.palexdev.mfxresources.fonts.IconsProviders.FONTAWESOME_SOLID;
import static io.github.palexdev.mfxresources.fonts.IconsProviders.randomIcon;

public class ButtonsPlayground extends Application implements MultipleViewApp<String> {
	//================================================================================
	// Properties
	//================================================================================
	private final ViewSwitcher<String> switcher = new ViewSwitcher<>();
	private final StringProperty themeVariant = new SimpleStringProperty("light");

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void start(Stage stage) {
		CSSFX.start();
		registerViews();

		MFXFab themeSwitcher = MFXFab.lowered();
		MFXFontIcon icon = new MFXFontIcon("fas-moon");
		themeSwitcher.setExtended(true);
		themeSwitcher.textProperty().bind(themeVariant.map(s -> s.equals("light") ? "Dark" : "Light"));
		themeSwitcher.setIcon(icon);

		BorderPane root = new BorderPane();
		ComboBox<String> header = new ComboBox<>(FXCollections.observableArrayList(switcher.views().keySet()));
		header.valueProperty().addListener((observable, oldValue, newValue) -> root.setCenter(switcher.load(newValue)));
		root.setTop(header);
		BorderPane.setAlignment(header, Pos.CENTER);
		BorderPane.setMargin(header, new Insets(30, 0, 60, 0));
		root.getStyleClass().add("container");

		header.getSelectionModel().selectFirst();

		ScrollPane sp = new ScrollPane(root) {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				layoutInArea(themeSwitcher,
					getLayoutX(), getLayoutY(),
					getWidth(), getHeight(), 0,
					InsetsBuilder.of(0, 24, 16, 0), HPos.RIGHT, VPos.BOTTOM
				);
			}
		};
		sp.setFitToWidth(true);
		sp.setFitToHeight(true);
		CSSFragment.Builder.build()
			.addSelector(".scroll-pane, .scroll-pane .viewport")
			.addStyle("-fx-background-color: transparent")
			.closeSelector()
			.applyOn(sp);

		Size ws = UIUtils.getWindowSize();
		Scene scene = new Scene(sp, ws.getWidth(), ws.getHeight());
		loadStyleSheet(scene);
		stage.setScene(scene);
		stage.setTitle("Buttons Playground");
		stage.show();

		themeSwitcher.setOnAction(e -> {
			String newVariant = themeVariant.get().equals("light") ? "dark" : "light";
			String iconDesc = themeVariant.get().equals("light") ? "fas-sun" : "fas-moon";
			themeVariant.set(newVariant);
			loadStyleSheet(scene);
			themeSwitcher.getFabBehavior().ifPresent(b -> b.changeIcon(new MFXFontIcon(iconDesc)));
		});
		sp.getChildren().add(themeSwitcher);

		ScenicView.show(scene);
	}

	@Override
	public void registerViews() {
		switcher.register(defaultView(), s -> ebView());
		switcher.register("filled-buttons", s -> fbView());
		switcher.register("tonal-filled-buttons", s -> tfbView());
		switcher.register("outlined-buttons", s -> obView());
		switcher.register("text-buttons", s -> tbView());
		switcher.register("fabs", s -> fabView());
		switcher.register("extended-fabs", s -> extendedFabView());
	}

	@Override
	public String defaultView() {
		return "elevated-buttons";
	}

	@Override
	public List<String> getStylesheet() {
		String base = ComponentsLauncher.load("AppBase.css");
		String theme = themeVariant.get().equals("light") ?
			MFXThemeManager.PURPLE_LIGHT.load() :
			MFXThemeManager.PURPLE_DARK.load();
		return List.of(base, theme);
	}

	//================================================================================
	// Methods
	//================================================================================

	private Node ebView() {
		return createButtonsView("Elevated Buttons", MFXElevatedButton::new);
	}

	private Node fbView() {
		return createButtonsView("Filled Buttons", MFXFilledButton::new);
	}

	private Node tfbView() {
		return createButtonsView("Tonal Filled Buttons", MFXTonalFilledButton::new);
	}

	private Node obView() {
		return createButtonsView("Outlined Buttons", MFXOutlinedButton::new);
	}

	private Node tbView() {
		return createButtonsView("Text Buttons", 600, MFXTextButton::new);
	}

	private Node fabView() {
		VBox box = new VBox(50);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(InsetsBuilder.all(10));
		Node def = createFabsView("Floating Action Buttons", (s, i) -> new MFXFab(i));
		Node surf = createFabsView("Floating Action Buttons (Surface)", (s, i) -> new MFXFab(i).setVariants(FABVariants.SURFACE));
		Node sdy = createFabsView("Floating Action Buttons (Secondary)", (s, i) -> new MFXFab(i).setVariants(FABVariants.SECONDARY));
		Node tty = createFabsView("Floating Action Buttons (Tertiary)", (s, i) -> new MFXFab(i).setVariants(FABVariants.TERTIARY));
		box.getChildren().addAll(def, surf, sdy, tty);
		return box;
	}

	private Node extendedFabView() {
		BiFunction<String, MFXFontIcon, MFXFab> generator = (s, i) -> {
			MFXFab fab = MFXFab.extended();
			fab.setText(s);
			fab.setIcon(i);
			return fab;
		};
		VBox box = new VBox(50);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(InsetsBuilder.all(10));
		Node def = createExtendedFabView("Extended FABs", generator);
		Node surf = createExtendedFabView("Extended FABs (Surface)", generator.andThen(f -> f.setVariants(FABVariants.SURFACE)));
		Node sdy = createExtendedFabView("Extended FABs (Secondary)", generator.andThen(f -> f.setVariants(FABVariants.SECONDARY)));
		Node tty = createExtendedFabView("Extended FABs (Tertiary)", generator.andThen(f -> f.setVariants(FABVariants.TERTIARY)));
		box.getChildren().addAll(def, surf, sdy, tty);
		return box;
	}

	private Node createButtonsView(String title, BiFunction<String, Node, MFXButton> generator) {
		return createButtonsView(title, 700, generator);
	}

	private Node createButtonsView(String title, double length, BiFunction<String, Node, MFXButton> generator) {
		TitledFlowPane tfp = new TitledFlowPane(title);
		tfp.setMaxWidth(length);

		MFXButton btn0 = generator.apply("Enabled", null);
		MFXButton btn1 = generator.apply("Disabled", null);
		MFXButton btn2 = generator.apply("Hovered", null);
		MFXButton btn3 = generator.apply("Focused", null);
		MFXButton btn4 = generator.apply("Pressed", null);
		MFXButton btn5 = generator.apply("Icon Left", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn6 = generator.apply("Icon Right", randomIcon(FONTAWESOME_SOLID));
		btn6.setContentDisplay(ContentDisplay.RIGHT);

		btn1.setDisable(true);
		btn2.setMouseTransparent(true);
		btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
		btn3.setMouseTransparent(true);
		btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
		btn4.setMouseTransparent(true);
		btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

		tfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6);
		return tfp;
	}

	private Node createFabsView(String title, BiFunction<String, MFXFontIcon, MFXFab> generator) {
		return createFabsView(title, 700, generator);
	}

	private Node createFabsView(String title, double length, BiFunction<String, MFXFontIcon, MFXFab> generator) {
		TitledFlowPane defTfp = new TitledFlowPane(title);
		defTfp.setMaxWidth(length);

		MFXButton btn0 = generator.apply("Enabled", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn1 = generator.apply("Disabled", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn2 = generator.apply("Hovered", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn3 = generator.apply("Focused", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn4 = generator.apply("Pressed", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn5 = generator.apply("Small", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn6 = generator.apply("Large", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn7 = generator.apply("Large Lowered", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn8 = generator.apply("Lowered Large", randomIcon(FONTAWESOME_SOLID));

		btn1.setDisable(true);
		btn2.setMouseTransparent(true);
		btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
		btn3.setMouseTransparent(true);
		btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
		btn4.setMouseTransparent(true);
		btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
		btn5.addVariants(FABVariants.SMALL);
		btn6.addVariants(FABVariants.LARGE);
		btn7.addVariants(FABVariants.LARGE, FABVariants.LOWERED);
		btn8.addVariants(FABVariants.LOWERED, FABVariants.LARGE);

		btn8.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			btn8.getFabBehavior().ifPresent(b -> b.changeIcon(randomIcon(FONTAWESOME_SOLID)));
			e.consume();
		});

		defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
		return defTfp;
	}

	private Node createExtendedFabView(String title, BiFunction<String, MFXFontIcon, MFXFab> generator) {
		return createExtendedFabView(title, 900, generator);
	}

	private Node createExtendedFabView(String title, double length, BiFunction<String, MFXFontIcon, MFXFab> generator) {
		TitledFlowPane defTfp = new TitledFlowPane(title);
		defTfp.setMaxWidth(length);

		MFXButton btn0 = generator.apply("Enabled", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn1 = generator.apply("Disabled", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn2 = generator.apply("Hovered", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn3 = generator.apply("Focused", randomIcon(FONTAWESOME_SOLID));
		MFXButton btn4 = generator.apply("Pressed", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn5 = generator.apply("Text Only", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn6 = generator.apply("Expandable", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn7 = generator.apply("Change Icon", randomIcon(FONTAWESOME_SOLID));
		MFXFab btn8 = generator.apply("Lowered Text Only", randomIcon(FONTAWESOME_SOLID));

		btn1.setDisable(true);
		btn2.setMouseTransparent(true);
		btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
		btn3.setMouseTransparent(true);
		btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
		btn4.setMouseTransparent(true);
		btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

		btn5.setContentDisplay(ContentDisplay.TEXT_ONLY);
		btn5.setAlignment(Pos.CENTER);
		btn8.addVariants(FABVariants.LOWERED);
		btn8.setContentDisplay(ContentDisplay.TEXT_ONLY);
		btn8.setAlignment(Pos.CENTER);

		btn6.setExtended(false);
		btn6.setOnAction(e -> btn6.setExtended(!btn6.isExtended()));
		btn7.setOnAction(e -> btn7.getFabBehavior().ifPresent(b -> b.changeIcon(randomIcon(FONTAWESOME_SOLID))));

		defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
		return defTfp;
	}
}
