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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.collections.TransformableListWrapper;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.controls.cell.MFXFilterComboBoxCell;
import io.github.palexdev.materialfx.skins.MFXFilterComboBoxSkin;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Extends {@link MFXComboBox} and changes the popup's content slightly to
 * allow filtering the items list.
 * <p>
 * In addition to the base class futures this adds:
 * <p> - Uses a {@link TransformableListWrapper} to filter the items. You could even sort them by
 * retrieving the list instance and setting a comparator, {@link TransformableList#comparatorProperty()}.
 * Beware to this though, {@link TransformableList#setReversed(boolean)}, it's really important to specify that
 * the comparator is in reverse order otherwise indexes will be inconsistent.
 * <p> - A function that takes the typed search text as an input and builds a {@link Predicate} as a result to
 * filter the list. This means that the user can fully customize how the list is filtered.
 * <p></p>
 * Note: this combo box do not uses {@link MFXComboBoxCell} and while it does allow it it should never be used.
 * Use {@link MFXFilterComboBoxCell} instead for consistent selection behavior.
 */
public class MFXFilterComboBox<T> extends MFXComboBox<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLECLASS = "mfx-filter-combo-box";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFilterComboBox.css");

	private final StringProperty searchText = new SimpleStringProperty();
	private final TransformableListWrapper<T> filterList = new TransformableListWrapper<>(FXCollections.observableArrayList());
	private final FunctionProperty<String, Predicate<T>> filterFunction = new FunctionProperty<>(s -> t -> StringUtils.containsIgnoreCase(t.toString(), s));
	private boolean resetOnPopupHidden = true;

	private final InvalidationListener itemsChanged = invalidated -> filterList.setAll(getItems());

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterComboBox() {
		super();
		initialize();
	}

	public MFXFilterComboBox(ObservableList<T> items) {
		super(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLECLASS);
		setCellFactory(t -> new MFXFilterComboBoxCell<>(this, getFilterList(), t));

		filterList.setAll(getItems());
		itemsProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) oldValue.removeListener(itemsChanged);
			if (newValue != null) {
				newValue.addListener(itemsChanged);
				filterList.setAll(newValue);
			}
		});
		getItems().addListener(itemsChanged);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String getSearchText() {
		return searchText.get();
	}

	/**
	 * Specifies the text used to filter the items list.
	 * <p></p>
	 * By default this text is bound bidirectionally with the text-field's
	 * used in the popup
	 */
	public StringProperty searchTextProperty() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText.set(searchText);
	}

	public TransformableList<T> getFilterList() {
		return filterList.getTransformableList();
	}

	public Function<String, Predicate<T>> getFilterFunction() {
		return filterFunction.get();
	}

	/**
	 * Specifies the function used to build a {@link Predicate} from the typed search text, the
	 * predicate is then used to filter the list.
	 */
	public FunctionProperty<String, Predicate<T>> filterFunctionProperty() {
		return filterFunction;
	}

	public void setFilterFunction(Function<String, Predicate<T>> filterFunction) {
		this.filterFunction.set(filterFunction);
	}

	/**
	 * @return whether to reset the filter state, such as the {@link #searchTextProperty()}
	 * when the popup is closed
	 */
	public boolean isResetOnPopupHidden() {
		return resetOnPopupHidden;
	}

	/**
	 * Sets whether to reset the filter state, such as the {@link #searchTextProperty()}
	 * when the popup is closed
	 */
	public void setResetOnPopupHidden(boolean resetOnPopupHidden) {
		this.resetOnPopupHidden = resetOnPopupHidden;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXFilterComboBoxSkin<>(this, boundField);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
