/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class ListViewsDemoController implements Initializable {
    private final Random random = new Random(System.currentTimeMillis());

    private enum State {
        LEGACY, NEW
    }

    private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.NEW);

    @FXML
    private HBox legacyBox;

    @FXML
    private HBox newBox;

    @FXML
    private MFXLegacyListView<String> stringView;

    @FXML
    private MFXLegacyListView<Label> labelView;

    @FXML
    private MFXLegacyListView<HBox> hBoxView;

    @FXML
    private MFXLegacyListView<String> cssView;

    @FXML
    private MFXListView<String> stringViewNew;

    @FXML
    private MFXListView<MFXLabel> labelViewNew;

    @FXML
    private MFXListView<HBox> hBoxViewNew;

    @FXML
    private MFXCheckListView<String> checkList;

    @FXML
    private MFXListView<String> cssViewNew;

    @FXML
    private MFXButton swapButton;

    @FXML
    private MFXButton depthButton;

    @FXML
    private MFXButton colorsButton;

    @FXML
    private Label mulLabel;

    private ObservableList<String> stringList;
    private ObservableList<Label> labelsList;
    private ObservableList<HBox> hBoxesList;

    private ObservableList<MFXLabel> labelsListNew;
    private ObservableList<HBox> hBoxesListNew;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLists();

        state.addListener((observable, oldValue, newValue) -> {
            if (newValue == State.NEW) {
                mulLabel.setVisible(true);
                legacyBox.setVisible(false);
                newBox.setVisible(true);
            } else {
                mulLabel.setVisible(false);
                legacyBox.setVisible(true);
                newBox.setVisible(false);
            }
        });

        //  LEGACY //
        stringView.setItems(stringList);
        labelView.setItems(labelsList);
        hBoxView.setItems(hBoxesList);
        cssView.setItems(stringList);

        // NEW //
        stringViewNew.setItems(stringList);
        labelViewNew.setItems(labelsListNew);
        hBoxViewNew.setItems(hBoxesListNew);
        checkList.setItems(stringList);
        cssViewNew.setItems(stringList);

        swapButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateState());
        depthButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateDepth());
        colorsButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateColors());

        stringViewNew.getSelectionModel().setAllowsMultipleSelection(true);
    }

    private void initLists() {
        stringList = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 100_000).forEach(i -> stringList.add("String " + i));

        // LEGACY //
        labelsList = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 100).forEach(i -> labelsList.add(createLegacyLabel("Label " + i, getRandomIcon())));

        hBoxesList = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 100).forEach(i -> hBoxesList.add(createHBox(i)));

        // NEW //
        labelsListNew = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 100).forEach(i -> labelsListNew.add(createLabel("Label " + i, getRandomIcon())));

        hBoxesListNew = FXCollections.observableArrayList();
        IntStream.rangeClosed(0, 100).forEach(i -> hBoxesListNew.add(createHBox(i)));
    }

    private MFXLabel createLabel(String text, String iconDescription) {
        FontIcon icon = new FontIcon(iconDescription);
        icon.setIconColor(Color.PURPLE);
        icon.setIconSize(14);

        MFXLabel label = new MFXLabel(text);
        label.setLineColor(Color.TRANSPARENT);
        label.setUnfocusedLineColor(Color.TRANSPARENT);
        label.setStyle("-fx-background-color: transparent");
        label.setLeadingIcon(icon);
        label.setGraphicTextGap(10);
        return label;
    }

    private Label createLegacyLabel(String text, String iconDescription) {
        FontIcon icon = new FontIcon(iconDescription);
        icon.setIconColor(Color.PURPLE);
        icon.setIconSize(14);

        Label label = new Label(text);
        label.setStyle("-fx-background-color: transparent");
        label.setGraphic(icon);
        label.setGraphicTextGap(10);
        return label;
    }

    private HBox createHBox(int index) {
        HBox hBox = new HBox(20);
        hBox.setPadding(new Insets(0, 10, 0, 10));

        FontIcon city = new FontIcon("fas-city");
        city.setIconColor(Color.GOLD);
        city.setIconSize(12);
        Label label1 = new Label("City " + index, city);

        FontIcon people = new FontIcon("fas-users");
        people.setIconColor(Color.GOLD);
        people.setIconSize(12);
        Label label2 = new Label("Count: " + random.nextInt(2000000), people);

        hBox.getChildren().addAll(label1, label2);
        return hBox;
    }

    private void updateState() {
        State curr = state.get();
        swapButton.setText(curr == State.LEGACY ?  "Switch to Legacy" : "Switch to New");
        state.set(curr == State.LEGACY ? State.NEW : State.LEGACY);
    }

    private void updateDepth() {
        if (state.get() == State.LEGACY) {
            DepthLevel level = cssView.getDepthLevel();
            cssView.setDepthLevel(level.equals(DepthLevel.LEVEL0) ? DepthLevel.LEVEL2 : DepthLevel.LEVEL0);
        } else {
            DepthLevel level = cssViewNew.getDepthLevel();
            cssViewNew.setDepthLevel(level.equals(DepthLevel.LEVEL0) ? DepthLevel.LEVEL2 : DepthLevel.LEVEL0);
        }
    }

    private void updateColors() {
        if (state.get() == State.LEGACY) {
            cssView.setTrackColor(ColorUtils.getRandomColor());
            cssView.setThumbColor(ColorUtils.getRandomColor());
            cssView.setThumbHoverColor(ColorUtils.getRandomColor());
        } else {
            cssViewNew.setTrackColor(ColorUtils.getRandomColor());
            cssViewNew.setThumbColor(ColorUtils.getRandomColor());
            cssViewNew.setThumbHoverColor(ColorUtils.getRandomColor());
        }
    }

    private String getRandomIcon() {
        FontAwesomeSolid[] resources = FontAwesomeSolid.values();
        int random = (int) (Math.random() * resources.length);
        return resources[random].getDescription();
    }
}
