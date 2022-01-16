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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.selection.base.ISingleSelectionModel;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.util.StringConverter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Public API every combo box must implement.
 */
public interface MFXCombo<T> {

	/**
	 * Shows the popup.
	 */
	void show();

	/**
	 * Hides the popup.
	 */
	void hide();

	T getValue();

	/**
	 * Specifies the combo box's value, which does not necessarily coincides with the
	 * currently selected item.
	 */
	ObjectProperty<T> valueProperty();

	void setValue(T value);

	StringConverter<T> getConverter();

	/**
	 * Specifies the converter used to convert an object of type T
	 * to a String. It is used, for example, to set the combo box text
	 * when an item is selected.
	 */
	ObjectProperty<StringConverter<T>> converterProperty();

	void setConverter(StringConverter<T> converter);

	boolean isEditable();

	/**
	 * Specifies whether the combo box is editable or not.
	 */
	BooleanProperty editableProperty();

	void setEditable(boolean editable);

	/**
	 * If the combo box is editable and the text has been changed, this method
	 * is responsible for deciding what to do with the new text on confirm.
	 */
	void commit(String text);

	/**
	 * If the combo box is editable and the text has been changed, this method
	 * is responsible for deciding what to do with the new text on cancel.
	 */
	void cancel(String text);

	Consumer<String> getOnCommit();

	/**
	 * Specifies the action to perform on {@link #commit(String)}.
	 */
	ConsumerProperty<String> onCommitProperty();

	void setOnCommit(Consumer<String> onCommit);

	Consumer<String> getOnCancel();

	/**
	 * Specifies the action to perform on {@link #cancel(String)}.
	 */
	ConsumerProperty<String> onCancelProperty();

	void setOnCancel(Consumer<String> onCancel);

	String getPromptText();

	/**
	 * Specifies the combo box's prompt text.
	 */
	StringProperty promptTextProperty();

	void setPromptText(String promptText);

	ObservableList<T> getItems();

	/**
	 * Specifies the combo box's items list.
	 */
	ObjectProperty<ObservableList<T>> itemsProperty();

	void setItems(ObservableList<T> items);

	Function<T, Cell<T>> getCellFactory();

	/**
	 * Specifies the function used to create the items cells
	 * in the popup.
	 */
	ObjectProperty<Function<T, Cell<T>>> cellFactoryProperty();

	void setCellFactory(Function<T, Cell<T>> cellFactory);

	/**
	 * @return the combo box' selection model
	 */
	ISingleSelectionModel<T> getSelectionModel();

	EventHandler<ActionEvent> getOnAction();

	/**
	 * Specifies the combo box's action when the value property changes.
	 */
	ObjectProperty<EventHandler<ActionEvent>> onActionProperty();

	void setOnAction(EventHandler<ActionEvent> onAction);

	EventHandler<Event> getOnShowing();

	/**
	 * Specifies the action when the popup is showing.
	 */
	EventHandlerProperty<Event> onShowingProperty();

	void setOnShowing(EventHandler<Event> onShowing);

	EventHandler<Event> getOnShown();

	/**
	 * Specifies the action when the popup is shown.
	 */
	EventHandlerProperty<Event> onShownProperty();

	void setOnShown(EventHandler<Event> onShown);

	EventHandler<Event> getOnHiding();

	/**
	 * Specifies the action when the popup is hiding.
	 */
	EventHandlerProperty<Event> onHidingProperty();

	void setOnHiding(EventHandler<Event> onHiding);

	EventHandler<Event> getOnHidden();

	/**
	 * Specifies the action when the popup is hidden.
	 */
	EventHandlerProperty<Event> onHiddenProperty();

	void setOnHidden(EventHandler<Event> onHidden);
}
