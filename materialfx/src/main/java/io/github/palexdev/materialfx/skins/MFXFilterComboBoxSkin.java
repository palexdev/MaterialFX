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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.BoundTextField;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXFilterComboBoxCell;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Skin associated with every {@link MFXFilterComboBox} by default.
 * <p>
 * Extends {@link MFXComboBoxSkin}.
 * <p>
 * This skin mainly overrides the {@link #createPopupContent()} and implements the
 * method responsible for filtering the popup's listview.
 */
public class MFXFilterComboBoxSkin<T> extends MFXComboBoxSkin<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterComboBoxSkin(MFXFilterComboBox<T> comboBox, BoundTextField boundField) {
		super(comboBox, boundField);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXFilterComboBox<T> comboBox = getComboBox();

		comboBox.searchTextProperty().addListener((observable, oldValue, newValue) -> filter(newValue));
		popup.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && comboBox.isResetOnPopupHidden()) comboBox.setSearchText("");
		});
	}

	/**
	 * Responsible for filtering the popup's listview.
	 * <p></p>
	 * What it really does is to use the {@link MFXFilterComboBox#filterFunctionProperty()}
	 * to produce a {@link Predicate}, which is then set on the {@link MFXFilterComboBox#getFilterList()}.
	 * <p></p>
	 * This means that since it is not bound you can even set your own predicate on that list,
	 * but everytime the text is changed in the search field it will be replaced.
	 */
	protected void filter(String text) {
		MFXFilterComboBox<T> comboBox = getComboBox();
		Function<String, Predicate<T>> filterFunction = comboBox.getFilterFunction();
		if (filterFunction == null) return;

		Predicate<T> filter = filterFunction.apply(text);
		comboBox.getFilterList().setPredicate(filter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The content is slightly different from the {@link MFXComboBoxSkin} one.
	 * <p>
	 * In the previous combo box skin, a text field was positioned on top of the combo's field
	 * to input the search text.
	 * <p>
	 * This time I decided to do it another way. The popup contains both the search field
	 * and the listview, contained in a VBox, this way the control is easier to maintain,
	 * and also more appealing.
	 */
	@Override
	protected Node createPopupContent() {
		MFXFilterComboBox<T> comboBox = getComboBox();
		TransformableList<T> filterList = comboBox.getFilterList();

		MFXTextField searchField = new MFXTextField("", I18N.getOrDefault("filterCombo.search")) {
			@Override
			public String getUserAgentStylesheet() {
				return comboBox.getUserAgentStylesheet();
			}
		};
		searchField.getStyleClass().add("search-field");
		searchField.textProperty().bindBidirectional(comboBox.searchTextProperty());
		searchField.setMaxWidth(Double.MAX_VALUE);

		SimpleVirtualFlow<T, Cell<T>> virtualFlow = new SimpleVirtualFlow<>(
				filterList,
				t -> new MFXFilterComboBoxCell<>(comboBox, filterList, t),
				Orientation.VERTICAL
		);
		virtualFlow.cellFactoryProperty().bind(comboBox.cellFactoryProperty());
		virtualFlow.prefWidthProperty().bind(comboBox.widthProperty());
		virtualFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (popup.isShowing()) {
				popup.hide();
			}
		});

		VBox container = new VBox(10, searchField, virtualFlow);
		container.getStyleClass().add("search-container");
		container.setAlignment(Pos.TOP_CENTER);
		return container;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to cast to {@code MFXFilterComboBox}.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MFXFilterComboBox<T> getComboBox() {
		return (MFXFilterComboBox<T>) getSkinnable();
	}
}
