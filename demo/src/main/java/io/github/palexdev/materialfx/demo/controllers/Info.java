package io.github.palexdev.materialfx.demo.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Info implements Initializable {
    private final HostServices hostServices;

    @FXML
    private Label githubL;

    @FXML
    private Hyperlink githubH;

    @FXML
    private Label mavenL;

    @FXML
    private Hyperlink mavenH;

    @FXML
    private Label contactL;

    @FXML
    private Hyperlink contactH;

    @FXML
    private Label emailL;

    @FXML
    private Hyperlink emailH;

    @FXML
    private Label paypalL;

    @FXML
    private Hyperlink paypalH;

    public Info(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        githubL.getGraphic().setOnMousePressed(event -> hostServices.showDocument(githubH.getTooltip().getText()));
        mavenL.getGraphic().setOnMousePressed(event -> hostServices.showDocument(mavenH.getTooltip().getText()));
        contactL.getGraphic().setOnMousePressed(event -> hostServices.showDocument(contactH.getTooltip().getText()));
        emailL.getGraphic().setOnMousePressed(event -> hostServices.showDocument(emailH.getTooltip().getText()));
        paypalL.getGraphic().setOnMousePressed(event -> hostServices.showDocument(paypalH.getTooltip().getText()));
    }
}
