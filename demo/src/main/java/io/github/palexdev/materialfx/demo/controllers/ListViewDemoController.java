package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.utils.ColorUtils;
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

    @FXML
    private MFXListView<String> stringView;

    @FXML
    private MFXListView<Label> labelView;

    @FXML
    private MFXListView<HBox> hBoxView;

    @FXML
    private MFXListView<String> cssView;

    @FXML
    private MFXButton depthButton;

    @FXML
    private MFXButton colorsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> stringList = FXCollections.observableArrayList(List.of(
                "String 0",
                "String 1",
                "String 2",
                "String 3",
                "String 4",
                "String 5",
                "String 6",
                "String 7"
        ));
        stringView.setItems(stringList);

        ObservableList<Label> labelsList = FXCollections.observableArrayList(List.of(
                new Label("Label 0", createIcon("fas-home")),
                new Label("Label 1", createIcon("fas-star")),
                new Label("Label 2", createIcon("fas-heart")),
                new Label("Label 3", createIcon("fas-cocktail")),
                new Label("Label 4", createIcon("fas-anchor")),
                new Label("Label 5", createIcon("fas-bolt")),
                new Label("Label 6", createIcon("fas-bug")),
                new Label("Label 7", createIcon("fas-beer"))
        ));
        labelView.setItems(labelsList);

        ObservableList<HBox> hBoxesList = FXCollections.observableArrayList(List.of(
                createHBox(0),
                createHBox(1),
                createHBox(2),
                createHBox(3),
                createHBox(4),
                createHBox(5),
                createHBox(6),
                createHBox(7)
        ));
        hBoxView.setItems(hBoxesList);

        cssView.setItems(stringList);
        depthButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            DepthLevel level = cssView.getDepthLevel();
            if (level.equals(DepthLevel.LEVEL0)) {
                cssView.setDepthLevel(DepthLevel.LEVEL2);
            } else {
                cssView.setDepthLevel(DepthLevel.LEVEL0);
            }
        });
        colorsButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            cssView.setTrackColor(ColorUtils.getRandomColor());
            cssView.setThumbColor(ColorUtils.getRandomColor());
            cssView.setThumbHoverColor(ColorUtils.getRandomColor());
        });
    }

    private FontIcon createIcon(String s) {
        FontIcon icon = new FontIcon(s);
        icon.setIconColor(Color.PURPLE);
        icon.setIconSize(13);
        return icon;
    }

    private HBox createHBox(int index) {
        HBox hBox = new HBox(20);
        hBox.setPadding(new Insets(0, 10, 0, 10));
        hBox.setPrefSize(150, 30);

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

}
