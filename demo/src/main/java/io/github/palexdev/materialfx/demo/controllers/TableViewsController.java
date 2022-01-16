/*
 * Copyright (C) 2022 Parisi Alessandro
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

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.demo.model.Device;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class TableViewsController implements Initializable {

	@FXML
	private MFXTableView<Person> table;

	@FXML
	private MFXPaginatedTableView<Device> paginated;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupTable();
		setupPaginated();

		table.autosizeColumnsOnInitialization();
		paginated.autosizeColumnsOnInitialization();

		When.onChanged(paginated.currentPageProperty())
				.then((oldValue, newValue) -> paginated.autosizeColumns())
				.listen();
	}

	private void setupTable() {
		MFXTableColumn<Person> nameColumn = new MFXTableColumn<>("Name", true, Comparator.comparing(Person::getName));
		MFXTableColumn<Person> surnameColumn = new MFXTableColumn<>("Surname", true, Comparator.comparing(Person::getSurname));
		MFXTableColumn<Person> ageColumn = new MFXTableColumn<>("Age", true, Comparator.comparing(Person::getAge));

		nameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getName));
		surnameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getSurname));
		ageColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getAge) {{
			setAlignment(Pos.CENTER_RIGHT);
		}});
		ageColumn.setAlignment(Pos.CENTER_RIGHT);

		table.getTableColumns().addAll(nameColumn, surnameColumn, ageColumn);
		table.getFilters().addAll(
				new StringFilter<>("Name", Person::getName),
				new StringFilter<>("Surname", Person::getSurname),
				new IntegerFilter<>("Age", Person::getAge)
		);
		table.setItems(Model.people);
	}

	private void setupPaginated() {
		MFXTableColumn<Device> idColumn = new MFXTableColumn<>("ID", false, Comparator.comparing(Device::getID));
		MFXTableColumn<Device> nameColumn = new MFXTableColumn<>("Name", false, Comparator.comparing(Device::getName));
		MFXTableColumn<Device> ipColumn = new MFXTableColumn<>("IP", false, Comparator.comparing(Device::getIP));
		MFXTableColumn<Device> ownerColumn = new MFXTableColumn<>("Owner", false, Comparator.comparing(Device::getOwner));
		MFXTableColumn<Device> stateColumn = new MFXTableColumn<>("State", false, Comparator.comparing(Device::getState));

		idColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getID));
		nameColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getName));
		ipColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getIP) {{
			setAlignment(Pos.CENTER_RIGHT);
		}});
		ownerColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getOwner));
		stateColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getState));
		ipColumn.setAlignment(Pos.CENTER_RIGHT);

		paginated.getTableColumns().addAll(idColumn, nameColumn, ipColumn, ownerColumn, stateColumn);
		paginated.getFilters().addAll(
				new IntegerFilter<>("ID", Device::getID),
				new StringFilter<>("Name", Device::getName),
				new StringFilter<>("IP", Device::getIP),
				new StringFilter<>("Owner", Device::getOwner),
				new EnumFilter<>("State", Device::getState, Device.State.class)
		);
		paginated.setItems(Model.devices);
	}
}
