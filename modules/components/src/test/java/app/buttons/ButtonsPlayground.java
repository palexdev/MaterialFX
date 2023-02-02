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

import app.others.ui.MultipleViewApp;
import app.others.ui.TitledFlowPane;
import app.others.ui.ViewSwitcher;
import io.github.palexdev.materialfx.controls.buttons.*;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.IconsProviders;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.function.BiFunction;

import static io.github.palexdev.mfxresources.fonts.IconsProviders.FONTAWESOME_SOLID;

public class ButtonsPlayground extends Application implements MultipleViewApp<String> {
	//================================================================================
	// Properties
	//================================================================================
	private final ViewSwitcher<String> switcher = new ViewSwitcher<>();

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void start(Stage stage) {
		registerViews();

		BorderPane root = new BorderPane();
		ComboBox<String> header = new ComboBox<>(FXCollections.observableArrayList(switcher.views().keySet()));
		header.valueProperty().addListener((observable, oldValue, newValue) -> root.setCenter(switcher.load(newValue)));
		root.setTop(header);
		BorderPane.setAlignment(header, Pos.CENTER);
		BorderPane.setMargin(header, new Insets(30, 0, 60, 0));
		root.setStyle("-fx-background-color: -md-sys-color-background");

		header.getSelectionModel().selectFirst();

		Scene scene = new Scene(root, 800, 800);
		loadStyleSheet(scene);
		stage.setScene(scene);
		stage.setTitle("Buttons Playground");
		stage.show();

		//ScenicView.show(scene);
	}

	@Override
	public void registerViews() {
		switcher.register(defaultView(), s -> ebView());
		switcher.register("filled-buttons", s -> fbView());
		switcher.register("tonal-filled-buttons", s -> tfbView());
		switcher.register("outlined-buttons", s -> obView());
		switcher.register("text-buttons", s -> tbView());
	}

	@Override
	public String defaultView() {
		return "elevated-buttons";
	}

	@Override
	public String getStylesheet() {
		return MFXResources.load("sass/md3/mfx-light.css");
	}

	//================================================================================
	// Methods
	//================================================================================

	private Node ebView() {
		return createButtonsView(MFXElevatedButton::new);
	}

	private Node fbView() {
		return createButtonsView(MFXFilledButton::new);
	}

	private Node tfbView() {
		return createButtonsView(MFXTonalFilledButton::new);
	}

	private Node obView() {
		return createButtonsView(MFXOutlinedButton::new);
	}

	private Node tbView() {
		return createButtonsView(600, MFXTextButton::new);
	}

	private Node createButtonsView(BiFunction<String, Node, MFXButton> generator) {
		return createButtonsView(700, generator);
	}

	private Node createButtonsView(double length, BiFunction<String, Node, MFXButton> generator) {
		TitledFlowPane tfp = new TitledFlowPane("Filled Buttons");
		tfp.setMaxWidth(length);

		MFXButton btn0 = generator.apply("Enabled", null);
		MFXButton btn1 = generator.apply("Disabled", null);
		MFXButton btn2 = generator.apply("Hovered", null);
		MFXButton btn3 = generator.apply("Focused", null);
		MFXButton btn4 = generator.apply("Pressed", null);
		MFXButton btn5 = generator.apply("Icon Left", IconsProviders.randomIcon(FONTAWESOME_SOLID, 24.0, Color.TRANSPARENT));
		MFXButton btn6 = generator.apply("Icon Right", IconsProviders.randomIcon(FONTAWESOME_SOLID, 24.0, Color.TRANSPARENT));
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
}
