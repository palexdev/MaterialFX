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

package app;

import app.others.ui.*;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXSegmentedButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.checkbox.TriState;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.IconButtonVariants;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Showcase extends Application implements MultipleViewApp<String> {
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

        MFXFab themeSwitcher = new MFXFab().lowered();
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
			themeSwitcher.setIcon(new MFXFontIcon(iconDesc));
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
        switcher.register("icon-buttons", s -> iconButtonsView());
        switcher.register("segmented-buttons", s -> segmentedButtonsView());
        switcher.register("checkboxes", s -> checkBoxesView());
    }

    @Override
    public String defaultView() {
        return "elevated-buttons";
    }

    @Override
    public List<String> getStylesheet() {
        String base = ComponentsLauncher.load("AppBase.css");
        String theme = themeVariant.get().equals("light") ?
            MaterialThemes.PURPLE_LIGHT.toData() :
            MaterialThemes.PURPLE_DARK.toData();
        return List.of(base, theme);
    }

    //================================================================================
    // Methods
    //================================================================================

    private Node ebView() {
        return createButtonsView("Elevated Buttons", MFXButton::new);
    }

    private Node fbView() {
        return createButtonsView("Filled Buttons", (s, node) -> new MFXButton(s, node).filled());
    }

    private Node tfbView() {
        return createButtonsView("Tonal Filled Buttons", (s, node) -> new MFXButton(s, node).tonal());
    }

    private Node obView() {
        return createButtonsView("Outlined Buttons", (s, node) -> new MFXButton(s, node).outlined());
    }

    private Node tbView() {
        return createButtonsView("Text Buttons", 600, (s, node) -> new MFXButton(s, node).text());
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
            MFXFab fab = new MFXFab().extended();
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

    private Node iconButtonsView() {
        BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator = (s, i) -> {
            MFXIconButton btn = new MFXIconButton(i);
            btn.setSelectable(s);
            return btn;
        };

        VBox box = new VBox(50);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(InsetsBuilder.all(10));
        Node standard = createIconButtonsView("Standard IconButtons", generator);
        Node filled = createIconButtonsView("Filled IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.FILLED)));
        Node filledTonal = createIconButtonsView("Filled Tonal IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.FILLED_TONAL)));
        Node outlined = createIconButtonsView("Outlined IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.OUTLINED)));
        box.getChildren().addAll(standard, filled, filledTonal, outlined);
        return box;
    }

    private Node segmentedButtonsView() {
        return createSegmentedButtonsView("Segmented Buttons");
    }

    private Node checkBoxesView() {
        return createCheckboxesView("Checkboxes");
    }

    // Creators
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
		MFXButton btn5 = generator.apply("Icon Left", FontAwesomeSolid.random());
		MFXButton btn6 = generator.apply("Icon Right", FontAwesomeSolid.random());
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

		MFXFab btn0 = generator.apply("Enabled", FontAwesomeSolid.random());
		MFXFab btn1 = generator.apply("Disabled", FontAwesomeSolid.random());
		MFXFab btn2 = generator.apply("Hovered", FontAwesomeSolid.random());
		MFXFab btn3 = generator.apply("Focused", FontAwesomeSolid.random());
		MFXFab btn4 = generator.apply("Pressed", FontAwesomeSolid.random());
		MFXFab btn5 = generator.apply("Small", FontAwesomeSolid.random());
		MFXFab btn6 = generator.apply("Large", FontAwesomeSolid.random());
		MFXFab btn7 = generator.apply("Large Lowered", FontAwesomeSolid.random());
		MFXFab btn8 = generator.apply("Lowered Large", FontAwesomeSolid.random());

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
			btn8.setIcon(FontAwesomeSolid.random());
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

		MFXFab btn0 = generator.apply("Enabled", FontAwesomeSolid.random());
		MFXFab btn1 = generator.apply("Disabled", FontAwesomeSolid.random());
		MFXFab btn2 = generator.apply("Hovered", FontAwesomeSolid.random());
		MFXFab btn3 = generator.apply("Focused", FontAwesomeSolid.random());
		MFXFab btn4 = generator.apply("Pressed", FontAwesomeSolid.random());
		MFXFab btn5 = generator.apply("Text Only", FontAwesomeSolid.random());
		MFXFab btn6 = generator.apply("Expandable", FontAwesomeSolid.random());
		MFXFab btn7 = generator.apply("Change Icon", FontAwesomeSolid.random());
		MFXFab btn8 = generator.apply("Lowered Text Only", FontAwesomeSolid.random());

        btn1.setDisable(true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn4.setMouseTransparent(true);
        btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

        btn5.setContentDisplay(ContentDisplay.TEXT_ONLY);
        btn5.setAlignment(Pos.CENTER_RIGHT);
        btn8.addVariants(FABVariants.LOWERED);
        btn8.setContentDisplay(ContentDisplay.TEXT_ONLY);
        btn8.setAlignment(Pos.CENTER);

        btn6.setExtended(false);
        btn6.setOnAction(e -> btn6.setExtended(!btn6.isExtended()));
		btn7.setOnAction(e -> btn7.setIcon(FontAwesomeSolid.random()));

        defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
        return defTfp;
    }

    private Node createIconButtonsView(String title, BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator) {
        return createIconButtonsView(title, 400, generator);
    }

    private Node createIconButtonsView(String title, double length, BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator) {
        TitledFlowPane defTfp = new TitledFlowPane(title);
        defTfp.setMaxWidth(length);

        // As toggles
		MFXIconButton btn0 = generator.apply(false, FontAwesomeSolid.random());
		MFXIconButton btn1 = generator.apply(false, FontAwesomeSolid.random());
		MFXIconButton btn2 = generator.apply(false, FontAwesomeSolid.random());
		MFXIconButton btn3 = generator.apply(false, FontAwesomeSolid.random());
		MFXIconButton btn4 = generator.apply(false, FontAwesomeSolid.random());
        btn1.setMouseTransparent(true);
        btn1.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        btn4.setDisable(true);

        // Standard
		MFXIconButton btn5 = generator.apply(true, FontAwesomeSolid.random());
		MFXIconButton btn6 = generator.apply(true, FontAwesomeSolid.random());
		MFXIconButton btn7 = generator.apply(true, FontAwesomeSolid.random());
		MFXIconButton btn8 = generator.apply(true, FontAwesomeSolid.random());
		MFXIconButton btn9 = generator.apply(true, FontAwesomeSolid.random());
        btn6.setMouseTransparent(true);
        btn6.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn7.setMouseTransparent(true);
        btn7.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn8.setMouseTransparent(true);
        btn8.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        btn9.setDisable(true);

        if (btn5.getStyleClass().contains(IconButtonVariants.OUTLINED.variantStyleClass()) ||
            btn5.getStyleClass().size() == 1) {
            btn5.setSelected(true);
            btn6.setSelected(true);
            btn7.setSelected(true);
            btn8.setSelected(true);
            btn9.setSelected(true);
        }

        defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9);
        return defTfp;
    }

    private Node createSegmentedButtonsView(String title) {
        TitledFlowPane defTP = new TitledFlowPane(title);

        MFXSegmentedButton woIcons = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
            woIcons.addSegment(null, "Segment " + i);
        }

        MFXSegmentedButton wIcons = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
			wIcons.addSegment(FontAwesomeSolid.random(), "Segment " + i);
        }

        MFXSegmentedButton wDisabled = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
			wDisabled.addSegment(FontAwesomeSolid.random(), "Segment " + i);
        }
        wDisabled.getSegments().get(1).setDisable(true);
        wDisabled.getSegments().get(2).setDisable(true);

        defTP.add(woIcons, wIcons, wDisabled);
        return defTP;
    }

    private Node createCheckboxesView(String title) {
        TitledFlowPane defTP = new TitledFlowPane(title);
        defTP.setMaxWidth(350);
        List<Supplier<MFXCheckbox>> generators = new ArrayList<>(List.of(
                MFXCheckbox::new,
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setDisable(true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.HOVER.setOn(c, true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.FOCUSED.setOn(c, true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.PRESSED.setOn(c, true);
                return c;
            }
        ));

        // Unchecked
        for (Supplier<MFXCheckbox> g : generators) {
            defTP.add(g.get());
        }

        // Indeterminate
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setAllowIndeterminate(true);
			c.setState(TriState.INDETERMINATE);
            defTP.add(c);
        }

        // Selected
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setSelected(true);
            defTP.add(c);
        }

        // Error
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setAllowIndeterminate(true);
            PseudoClasses.ERROR.setOn(c, true);
            defTP.add(c);
        }

        // Group
        SelectionGroup sg = new SelectionGroup(SelectionMode.SINGLE, true);
        for (int i = 0; i < 4; i++) {
            MFXCheckbox c = new MFXCheckbox("C" + (i + 1));
            sg.add(c);
            defTP.add(c);
        }

        return defTP;
    }
}
