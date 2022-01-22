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

package io.github.palexdev.materialfx.controls.legacy;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.legacy.MFXLegacyTableViewSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

/**
 * This is a restyle of JavaFX's {@link TableView} control.
 * For a table view which more closely follows the guidelines of material design see {@link io.github.palexdev.materialfx.controls.MFXTableView}.
 *
 * @param <S>
 */
public class MFXLegacyTableView<S> extends TableView<S> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-legacy-table-view";
	private final String STYLESHEET = MFXResourcesLoader.load("css/legacy/MFXTableView.css");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLegacyTableView() {
		initialize();
	}

	public MFXLegacyTableView(ObservableList<S> items) {
		super(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setRowFactory(row -> new MFXLegacyTableRow<>());
		setFixedCellSize(27);
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXLegacyTableViewSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
