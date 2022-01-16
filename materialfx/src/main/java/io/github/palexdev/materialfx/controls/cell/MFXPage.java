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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * This is the default cell used by {@link MFXPagination} to show the page indexes.
 * <p></p>
 * It's a very basic cell that show's the page's index as text (since it extends {@link Label}),
 * handles the selection state (according to {@link MFXPagination#currentPageProperty()}).
 * <p>
 * If the cells represents a truncated page, the text is specified by {@link MFXPagination#ellipseStringProperty()}.
 * In this case the cell also allows to show a popup containing the hidden pages, for faster and easier navigation.
 */
public class MFXPage extends Label implements Cell<Integer> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-page";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXPagination.css");

	private final MFXPagination pagination;
	private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();
	private NumberRange<Integer> between;

	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
	protected static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPage(MFXPagination pagination, int index) {
		this.pagination = pagination;
		updateItem(index);
		initialize();
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setAlignment(Pos.CENTER);
	}

	/**
	 * Handles selection, text property and updates the {@link MFXPagination#currentPageProperty()} on click.
	 * <p>
	 * If truncated and enabled, {@link #showPopup()} on click.
	 */
	private void addListeners() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(
				() -> pagination.getCurrentPage() == getIndex(),
				indexProperty(), pagination.currentPageProperty()
		));

		textProperty().bind(Bindings.createStringBinding(
				() -> getIndex() == -1 ? pagination.getEllipseString() : String.valueOf(getIndex()),
				indexProperty()
		));

		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			if (getIndex() != -1) {
				pagination.setCurrentPage(getIndex());
			} else {
				showPopup();
			}
		});
	}

	/**
	 * If the page is truncated, shows a popup containing the hidden pages' indexes.
	 */
	protected void showPopup() {
		if (!pagination.isShowPopupForTruncatedPages() || between == null) return;

		ObservableList<Integer> indexes = FXCollections.observableArrayList(NumberRange.expandRange(between));
		MFXListView<Integer> listView = new MFXListView<>(indexes);

		MFXPopup popup = new MFXPopup(listView);
		popup.getStyleClass().add("pages-popup");
		popup.setPopupStyleableParent(pagination);

		listView.setCellFactory(integer -> {
			MFXListCell<Integer> cell = new MFXListCell<>(listView, integer);
			cell.setOnMouseClicked(event -> {
				pagination.setCurrentPage(cell.getData());
				popup.hide();
			});
			return cell;
		});

		popup.show(this, Alignment.of(HPos.CENTER, VPos.BOTTOM), 0, 5);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void updateItem(Integer index) {
		setIndex(index);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public int getIndex() {
		return index.get();
	}

	/**
	 * Specifies the page's index.
	 */
	public ReadOnlyIntegerProperty indexProperty() {
		return index.getReadOnlyProperty();
	}

	protected void setIndex(int index) {
		this.index.set(index);
	}

	/**
	 * @return the range of hidden pages, if truncated otherwise null
	 */
	public NumberRange<Integer> getBetween() {
		return between;
	}

	public void setBetween(NumberRange<Integer> between) {
		this.between = between;
	}

	public boolean isSelected() {
		return selected.get();
	}

	/**
	 * Specifies the selection state of the page.
	 */
	public ReadOnlyBooleanProperty selectedProperty() {
		return selected.getReadOnlyProperty();
	}

	protected void setSelected(boolean selected) {
		this.selected.set(selected);
	}
}
