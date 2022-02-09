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

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;

public class ComboBoxesController implements Initializable {

	@FXML
	private MFXLegacyComboBox<String> lCombo;

	@FXML
	private MFXLegacyComboBox<String> lCustCombo;

	@FXML
	private MFXComboBox<String> nBFCombo;

	@FXML
	private MFXComboBox<String> nCombo;

	@FXML
	private MFXComboBox<String> nCustCombo;

	@FXML
	private MFXComboBox<String> nEditCombo;

	@FXML
	private MFXComboBox<String> nNFCombo;

	@FXML
	private MFXFilterComboBox<Person> filterCombo;

	@FXML
	private MFXFilterComboBox<Person> custFilterCombo;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> strings = Model.strings;
		ObservableList<Person> people = Model.people;

		lCombo.setItems(strings);
		lCustCombo.setItems(strings);

		nCombo.setItems(strings);
		nCustCombo.setItems(strings);
		nEditCombo.setItems(strings);
		nBFCombo.setItems(strings);
		nNFCombo.setItems(strings);

		nEditCombo.setOnCancel(s -> nEditCombo.setText(nEditCombo.getSelectedItem()));
		nEditCombo.setOnCommit(s -> {
			if (!strings.contains(s)) {
				strings.add(s);
			}
			nEditCombo.selectItem(s);
		});

		MFXTooltip.of(
				nEditCombo,
				"""
						This combo box allows you to add new items to the list (no duplicates allowed) when pressing Enter.
						It also allows to restore the previous selected item by pressing Ctrl+Shift+Z.
						Both key strokes are default for all MFXComboBoxes but the action to perform must be configured by the user.
						This combo box is also set to scroll to the selected item when opening the popup.
						"""
		).install();

		StringConverter<Person> converter = FunctionalStringConverter.to(person -> (person == null) ? "" : person.getName() + " " + person.getSurname());
		Function<String, Predicate<Person>> filterFunction = s -> person -> StringUtils.containsIgnoreCase(converter.toString(person), s);
		filterCombo.setItems(people);
		filterCombo.setConverter(converter);
		filterCombo.setFilterFunction(filterFunction);
		custFilterCombo.setItems(people);
		custFilterCombo.setConverter(converter);
		custFilterCombo.setFilterFunction(filterFunction);
		custFilterCombo.setResetOnPopupHidden(false);
	}
}
