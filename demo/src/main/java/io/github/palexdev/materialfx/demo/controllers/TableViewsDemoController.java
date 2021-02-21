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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumnCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import io.github.palexdev.materialfx.demo.model.FilterablePerson;
import io.github.palexdev.materialfx.demo.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class TableViewsDemoController implements Initializable {

    @FXML
    private MFXButton switchButton;

    @FXML
    private MFXLegacyTableView<Person> legacyTable;

    @FXML
    private MFXTableView<FilterablePerson> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switchButton.setOnAction(event -> {
            if (legacyTable.isVisible()) {
                legacyTable.setVisible(false);
                table.setVisible(true);
            } else {
                legacyTable.setVisible(true);
                table.setVisible(false);
            }
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
        ObservableList<FilterablePerson> people = FXCollections.observableArrayList(
                List.of(
                        new FilterablePerson("Ashley", "Vance", "566 Inverness Court Miami Beach, FL 33139", 19),
                        new FilterablePerson("Midge", "Phillips", "7983 Honey Creek Ave. Bemidji, MN 56601", 44),
                        new FilterablePerson("Joella", "Kendall", "640 Bay St. Astoria, NY 11102", 22),
                        new FilterablePerson("Cletis", "Bryson", "992 Rose Lane Glen Allen, VA 23059", 46),
                        new FilterablePerson("Minty", "Joyner", "81 South Central Street Millington, TN 38053", 18),
                        new FilterablePerson("Rupert", "Patton", "5 Shirley St. Niagara Falls, NY 14304", 59),
                        new FilterablePerson("Missie", "Ecclestone", "392 Galvin Lane Blackwood, NJ 08012", 75),
                        new FilterablePerson("Aydan", "Avery", "8726 Wilson Drive Asheville, NC 28803", 89),
                        new FilterablePerson("Cass", "Robert", "631 West Beaver Ridge Ave. Gallatin, TN 37066", 101),
                        new FilterablePerson("Alyssa", "Parish", "881 West Mayflower St. Bay City, MI 48706", 24),
                        new FilterablePerson("Brennan", "Woodham", "7957A Garden Street Rocklin, CA 95677", 68),
                        new FilterablePerson("Etta", "Low", "127 South Kirkland Road Glenview, IL 60025", 18)
                )
        );

        MFXTableColumnCell<FilterablePerson> firstNameColumn = new MFXTableColumnCell<>("First Name", Comparator.comparing(FilterablePerson::getFirstName));
        firstNameColumn.setRowCellFactory(person -> new MFXTableRowCell(person.firstNameProperty()));
        MFXTableColumnCell<FilterablePerson> lastNameColumn = new MFXTableColumnCell<>("Last Name", Comparator.comparing(FilterablePerson::getLastName));
        lastNameColumn.setRowCellFactory(person -> new MFXTableRowCell(person.lastNameProperty()));
        MFXTableColumnCell<FilterablePerson> addressColumn = new MFXTableColumnCell<>("Address", Comparator.comparing(FilterablePerson::getAddress));
        addressColumn.setRowCellFactory(person -> new MFXTableRowCell(person.addressProperty()));
        MFXTableColumnCell<FilterablePerson> ageColumn = new MFXTableColumnCell<>("Age", Comparator.comparing(FilterablePerson::getAge));
        ageColumn.setRowCellFactory(person -> new MFXTableRowCell(person.ageProperty().asString()) {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        ageColumn.setAlignment(Pos.CENTER_RIGHT);

        table.setItems(people);
        table.getColumns().addAll(firstNameColumn, lastNameColumn, addressColumn, ageColumn);
    }
}

