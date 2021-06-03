/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoController implements Initializable {
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

    public InfoController(HostServices hostServices) {
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
