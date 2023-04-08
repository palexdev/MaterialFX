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

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.IconsProviders;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeBrands;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeRegular;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;

public class FontResourcesController implements Initializable {
	private final ObservableList<IconDescriptor> fontResources;

	@FXML
	private Label header;

	@FXML
	private MFXTableView<IconDescriptor> tableView;

	public FontResourcesController() {
		List<IconDescriptor> icons = new ArrayList<>();
		Collections.addAll(icons, FontAwesomeSolid.values());
		Collections.addAll(icons, FontAwesomeRegular.values());
		Collections.addAll(icons, FontAwesomeBrands.values());
		fontResources = FXCollections.observableArrayList(icons);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MFXTableColumn<IconDescriptor> iconColumn = new MFXTableColumn<>("Icon", false, Comparator.comparing(IconDescriptor::getDescription));
		MFXTableColumn<IconDescriptor> descriptionColumn = new MFXTableColumn<>("Description", false, Comparator.comparing(IconDescriptor::getDescription));
		MFXTableColumn<IconDescriptor> codeColumn = new MFXTableColumn<>("Code", false, Comparator.comparing(IconDescriptor::getCode));

		iconColumn.setRowCellFactory(resource -> new MFXTableRowCell<>(IconDescriptor::getDescription) {
			final MFXFontIcon icon = new MFXFontIcon("", 32);
			Class<? extends IconDescriptor> current;

			private void handleProvider(IconDescriptor desc) {
				if (desc.getClass() == current) return;
				if (desc instanceof FontAwesomeSolid) {
					icon.setIconsProvider(IconsProviders.FONTAWESOME_SOLID);
				} else if (desc instanceof FontAwesomeRegular) {
					icon.setIconsProvider(IconsProviders.FONTAWESOME_REGULAR);
				} else if (desc instanceof FontAwesomeBrands) {
					icon.setIconsProvider(IconsProviders.FONTAWESOME_BRANDS);
				}
				current = desc.getClass();
			}

			@Override
			public void update(IconDescriptor item) {
				handleProvider(item);
				icon.setDescription(item.getDescription());
				setGraphic(icon);
			}
		});
		descriptionColumn.setRowCellFactory(resource -> new MFXTableRowCell<>(IconDescriptor::getDescription) {
			@Override
			public double computePrefWidth(double height) {
				return 300;
			}
		});
		codeColumn.setRowCellFactory(resource -> new MFXTableRowCell<>(IconDescriptor::getCode, character -> Integer.toHexString(character | 0x10000).substring(1).toUpperCase()));

		tableView.setTableRowFactory(resource -> new MFXTableRow<>(tableView, resource) {{
			setPrefHeight(48);
		}});
		tableView.getTableColumns().addAll(iconColumn, descriptionColumn, codeColumn);
		tableView.getFilters().add(new StringFilter<>("Description", IconDescriptor::getDescription));
		tableView.setItems(fontResources);
		tableView.features().enableBounceEffect();
		tableView.features().enableSmoothScrolling(0.7);
		tableView.autosizeColumnsOnInitialization();

		header.setText("MaterialFX Font Resources (" + fontResources.size() + " in total)");
	}
}
