package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ComboBoxesDemoController implements Initializable {

    @FXML
    private MFXLegacyComboBox<String> standard;

    @FXML
    private MFXLegacyComboBox<String> lineColors;

    @FXML
    private MFXLegacyComboBox<String> editable;

    @FXML
    private MFXLegacyComboBox<Label> labels;

    @FXML
    private MFXLegacyComboBox<String> validated;

    @FXML
    private MFXLegacyComboBox<String> customized;

    @FXML
    private MFXCheckbox checkbox;

    @FXML
    private MFXComboBox<String> style1;

    @FXML
    private MFXComboBox<String> style2;

    @FXML
    private MFXComboBox<Label> newCustomized;

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

        standard.setItems(stringList);
        lineColors.setItems(stringList);
        labels.setItems(labelsList);
        editable.setItems(stringList);
        validated.setItems(stringList);
        customized.setItems(stringList);

        editable.setEditable(true);
        validated.getValidator().add(checkbox.selectedProperty(), "Checkbox is not selected!");

        style1.setItems(stringList);
        style2.setItems(stringList);
        newCustomized.setItems(labelsList);
    }

    private FontIcon createIcon(String s) {
        FontIcon icon = new FontIcon(s);
        icon.setIconColor(Color.PURPLE);
        icon.setIconSize(13);
        return icon;
    }
}
