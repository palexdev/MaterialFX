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
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import io.github.palexdev.materialfx.demo.model.Machine;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import static io.github.palexdev.materialfx.demo.model.Machine.State.OFFLINE;
import static io.github.palexdev.materialfx.demo.model.Machine.State.ONLINE;

public class TableViewsDemoController implements Initializable {
    private final ObjectProperty<Stage> tableStage = new SimpleObjectProperty<>();
    private final MFXLegacyTableView<Person> legacyTable;
    private final MFXTableView<Machine> tableView;
    private final StackPane pane = new StackPane();
    private final Scene scene = new Scene(pane, 800, 600);

    @FXML
    private MFXButton showLegacy;

    @FXML
    private MFXButton showNew;

    public TableViewsDemoController() {
        tableStage.addListener((observable, oldValue, newValue) -> {
            getTableStage().initOwner(showLegacy.getScene().getWindow());
            getTableStage().initModality(Modality.WINDOW_MODAL);
        });

        Platform.runLater(() -> tableStage.set(new Stage()));

        legacyTable = new MFXLegacyTableView<>();
        tableView = new MFXTableView<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLegacy.setOnAction(event -> {
            getTableStage().close();
            pane.getChildren().setAll(legacyTable);
            getTableStage().setScene(scene);
            getTableStage().setTitle("Legacy TableView - Preview");
            getTableStage().show();
        });

        showNew.setOnAction(event -> {
            getTableStage().close();
            pane.getChildren().setAll(tableView);
            getTableStage().setScene(scene);
            getTableStage().setTitle("New TableView - Preview");
            getTableStage().show();
        });

        populateLegacy();
        populateTable();
    }

    @SuppressWarnings("unchecked")
    private void populateLegacy() {
        ObservableList<Person> people = FXCollections.observableArrayList(
                List.of(
                        new Person("Shaw", "Readdie", "972 Campfire St. Hopkins, MN 55343", 24),
                        new Person("Lonnie", "Dane", "30 Walnut St. Galloway, OH 43119", 27),
                        new Person("Tia", "Pilgrim", "8141 N. Edgewater Street Cumberland, RI 02864", 45),
                        new Person("Liberty", "Ward", "483 East Grand St. Stafford, VA 22554", 34),
                        new Person("Aria", "Watkins", "85 Sunnyslope Dr. Vincentown, NJ 08088", 53),
                        new Person("Jervis", "Kitchens", "813 Oklahoma Street West Roxbury, MA 02132", 77),
                        new Person("Dominick", "Church", "99 E. Alton Ave. Canfield, OH 44406", 29),
                        new Person("Forrest", "Davis", "840 Pilgrim Street Lake Villa, IL 60046", 67),
                        new Person("Nathaniel", "Crewe", "9407 South 10th Road Wenatchee, WA 98801", 19)
                )
        );

        TableColumn<Person, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(fName -> fName.getValue().firstNameProperty());
        TableColumn<Person, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(lName -> lName.getValue().lastNameProperty());
        TableColumn<Person, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(addr -> addr.getValue().addressProperty());
        TableColumn<Person, Number> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(age -> age.getValue().ageProperty());

        legacyTable.setItems(people);
        legacyTable.getColumns().addAll(firstNameColumn, lastNameColumn, addressColumn, ageColumn);
    }

    @SuppressWarnings("unchecked")
    private void populateTable() {
        ObservableList<Machine> people = FXCollections.observableArrayList(
                List.of(
                        new Machine("MainPC", "192.144.1.5", "Me", ONLINE),
                        new Machine("SecondaryPC", "192.144.1.6", "Me", OFFLINE),
                        new Machine("GamingLaptop", "192.144.1.44", "My Sons", OFFLINE),
                        new Machine("OfficeLaptop", "192.144.1.98", "Me", ONLINE),
                        new Machine("OfficeNAS", "192.144.1.2", "Me", ONLINE),
                        new Machine("OfficeAlexa", "192.144.1.34", "", ONLINE),
                        new Machine("OfficeSmartTV", "192.144.1.72", "", OFFLINE),
                        new Machine("KidsTablet", "192.144.1.11", "My Sons", OFFLINE),
                        new Machine("WifeKindle", "192.144.1.35", "My Wife", OFFLINE),
                        new Machine("SmartWasher", "192.144.1.78", "", ONLINE),
                        new Machine("SmartWatch", "192.144.1.18", "", ONLINE),
                        new Machine("GenericSmartphone", "192.144.54", "Me", ONLINE)
                )
        );

        MFXTableColumn<Machine> nameColumn = new MFXTableColumn<>("Name", Comparator.comparing(Machine::getName));
        MFXTableColumn<Machine> ipColumn = new MFXTableColumn<>("IP", Comparator.comparing(Machine::getIp));
        MFXTableColumn<Machine> ownerColumn = new MFXTableColumn<>("Owner", Comparator.comparing(Machine::getOwner));
        MFXTableColumn<Machine> stateColumn = new MFXTableColumn<>("State", Comparator.comparing(Machine::getState));

        nameColumn.setRowCellFunction(machine -> new MFXTableRowCell(machine.nameProperty()));
        ipColumn.setRowCellFunction(machine -> {
            MFXTableRowCell cell = new MFXTableRowCell(machine.ipProperty());
            cell.setRowAlignment(Pos.CENTER_RIGHT);
            return cell;
        });
        ownerColumn.setRowCellFunction(machine -> new MFXTableRowCell(machine.ownerProperty()));
        stateColumn.setRowCellFunction(machine -> {
            MFXTableRowCell rowCell = new MFXTableRowCell(machine.stateProperty().asString().concat(" - Click Me"));
            rowCell.setGraphicTextGap(4);
            MFXFontIcon icon = new MFXFontIcon("mfx-circle", 6);
            icon.colorProperty().bind(Bindings.createObjectBinding(
                    (Callable<Paint>) () -> machine.getState() == ONLINE ? Color.LIMEGREEN : Color.SALMON,
                    machine.stateProperty())
            );
            rowCell.setLeadingGraphic(icon);
            rowCell.borderProperty().bind(Bindings.createObjectBinding(
                    () -> {
                        Color borderColor = machine.getState() == ONLINE ? Color.LIMEGREEN : Color.SALMON;
                        return new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1)));
                    }, machine.stateProperty()
            ));
            rowCell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> machine.setState(machine.getState() == ONLINE ? OFFLINE : ONLINE));
            rowCell.setPadding(new Insets(0, 5, 0, 5));
            return rowCell;
        });

        ipColumn.setColumnAlignment(Pos.CENTER_RIGHT);

        tableView.setItems(people);
        tableView.getTableColumns().addAll(nameColumn, ipColumn, ownerColumn, stateColumn);
    }

    public Stage getTableStage() {
        return tableStage.get();
    }

    public void setTableStage(Stage tableStage) {
        this.tableStage.set(tableStage);
    }
}

