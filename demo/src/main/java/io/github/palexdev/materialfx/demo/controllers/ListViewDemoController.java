package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.*;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class ListViewDemoController implements Initializable {
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
    private MFXListView<String> stringView;

    @FXML
    private MFXListView<Label> labelView;

    @FXML
    private MFXListView<HBox> hBoxView;

    @FXML
    private MFXListView<String> cssView;

    @FXML
    private MFXFlowlessListView<String> stringViewNew;

    @FXML
    private MFXFlowlessListView<MFXLabel> labelViewNew;

    @FXML
    private MFXFlowlessListView<HBox> hBoxViewNew;

    @FXML
    private MFXFlowlessCheckListView<String> checkList;

    @FXML
    private MFXFlowlessListView<String> cssViewNew;

    @FXML
    private MFXButton switchButton;

    @FXML
    private MFXButton depthButton;

    @FXML
    private MFXButton colorsButton;

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
                legacyBox.setVisible(false);
                newBox.setVisible(true);
            } else {
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

        switchButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateState());
        depthButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateDepth());
        colorsButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateColors());

        stringViewNew.getSelectionModel().setAllowsMultipleSelection(true);
    }

    private void initLists() {
        stringList = FXCollections.observableArrayList(List.of(
                "String 0",
                "String 1",
                "String 2",
                "String 3",
                "String 4",
                "String 5",
                "String 6",
                "String 7"
        ));

        // LEGACY //
        labelsList = FXCollections.observableArrayList(List.of(
                createLegacyLabel("Label 0", "fas-home"),
                createLegacyLabel("Label 1", "fas-star"),
                createLegacyLabel("Label 2", "fas-heart"),
                createLegacyLabel("Label 3", "fas-cocktail"),
                createLegacyLabel("Label 4", "fas-anchor"),
                createLegacyLabel("Label 5", "fas-apple-alt"),
                createLegacyLabel("Label 6", "fas-bug"),
                createLegacyLabel("Label 7", "fas-beer")
        ));
        hBoxesList = FXCollections.observableArrayList(List.of(
                createHBox(0),
                createHBox(1),
                createHBox(2),
                createHBox(3),
                createHBox(4),
                createHBox(5),
                createHBox(6),
                createHBox(7)
        ));

        // NEW //
        labelsListNew = FXCollections.observableArrayList(List.of(
                createLabel("Label 0", "fas-home"),
                createLabel("Label 1", "fas-star"),
                createLabel("Label 2", "fas-heart"),
                createLabel("Label 3", "fas-cocktail"),
                createLabel("Label 4", "fas-anchor"),
                createLabel("Label 5", "fas-apple-alt"),
                createLabel("Label 6", "fas-bug"),
                createLabel("Label 7", "fas-beer")
        ));
        hBoxesListNew = FXCollections.observableArrayList(List.of(
                createHBox(0),
                createHBox(1),
                createHBox(2),
                createHBox(3),
                createHBox(4),
                createHBox(5),
                createHBox(6),
                createHBox(7)
        ));
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
        hBox.setPrefSize(200, 30);

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
        switchButton.setText(curr == State.LEGACY ?  "Switch to Legacy" : "Switch to New");
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
}
