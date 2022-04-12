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

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.selection.ComboBoxSelectionModel;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * Skin associated with every {@link MFXComboBox} by default.
 * <p>
 * Extends {@link MFXTextFieldSkin} since most features are inherited from
 * {@link MFXTextField} and adds the necessary properties/behaviors to add the
 * popup listview.
 * <p></p>
 * The listview used in the popup is not really a listview, but I decided to directly use
 * a {@link SimpleVirtualFlow} to make things easier, so that I don't have to worry about
 * synchronization between the combobox' selection model and the listview' selection model
 */
public class MFXComboBoxSkin<T> extends MFXTextFieldSkin {
	//================================================================================
	// Properties
	//================================================================================
	protected final MFXPopup popup;
	private EventHandler<MouseEvent> popupManager;
	protected SimpleVirtualFlow<T, Cell<T>> virtualFlow;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXComboBoxSkin(MFXComboBox<T> comboBox, BoundTextField boundField) {
		super(comboBox, boundField);

		popup = createPopup();
		setBehavior();

		T selectedItem = comboBox.getSelectedItem();
		if (selectedItem != null) {
			comboBox.setValue(selectedItem);
		}
	}

	//================================================================================
	// Methods
	//================================================================================
	protected void setBehavior() {
		comboBehavior();
		selectionBehavior();
		iconBehavior();
		popupBehavior();
	}

	/**
	 * Handles the commit event (on ENTER pressed and if editable), the cancel event
	 * (on Ctrl+Shift+Z pressed and if editable), and the update of the
	 * combo's value, {@link #updateValue(Object)}.
	 */
	private void comboBehavior() {
		MFXComboBox<T> comboBox = getComboBox();
		comboBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (!comboBox.isEditable()) return;
			switch (event.getCode()) {
				case ENTER: {
					comboBox.commit(comboBox.getText());
					break;
				}
				case Z: {
					if (event.isShiftDown() && event.isControlDown()) {
						comboBox.cancel(comboBox.getText());
					}
					break;
				}
			}
		});

		comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			updateValue(newValue);
			popup.hide();
		});
		comboBox.valueProperty().addListener(invalidated -> Event.fireEvent(comboBox, new ActionEvent()));

		comboBox.delegateSelectionProperty().addListener((observable, oldValue, newValue) -> {
			if (!comboBox.isAllowEdit() && !comboBox.isSelectable()) comboBox.selectRange(0, 0);
		});
		comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && !comboBox.isSelectable()) comboBox.selectRange(0, 0);
		});
	}

	/**
	 * Handles the selection to update the combo's value.
	 */
	private void selectionBehavior() {
		MFXComboBox<T> comboBox = getComboBox();
		ComboBoxSelectionModel<T> selectionModel = comboBox.getSelectionModel();

		selectionModel.selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			if (!comboBox.valueProperty().isBound()) {
				comboBox.setValue(selectionModel.getSelectedItem());
			}
		});
	}

	/**
	 * Handles the trailing icon, responsible for opening the popup.
	 */
	private void iconBehavior() {
		MFXComboBox<T> comboBox = getComboBox();
		Node trailingIcon = comboBox.getTrailingIcon();
		if (trailingIcon != null) {
			trailingIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
		}

		comboBox.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
			}
			if (newValue != null) {
				newValue.addEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
			}
		});

		popup.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				comboBox.hide();
				if (trailingIcon instanceof MFXIconWrapper) {
					MFXIconWrapper icon = (MFXIconWrapper) trailingIcon;
					icon.getRippleGenerator().generateRipple(null);
				}
				animateIcon(comboBox.getTrailingIcon(), false);
			}
		});
	}

	/**
	 * Handles the popup events and the combo' {@link MFXComboBox#showingProperty()}.
	 */
	private void popupBehavior() {
		MFXComboBox<T> comboBox = getComboBox();
		popup.setOnShowing(event -> Event.fireEvent(comboBox, new Event(popup, comboBox, MFXComboBox.ON_SHOWING)));
		popup.setOnShown(event -> Event.fireEvent(comboBox, new Event(popup, comboBox, MFXComboBox.ON_SHOWN)));
		popup.setOnHiding(event -> Event.fireEvent(comboBox, new Event(popup, comboBox, MFXComboBox.ON_HIDING)));
		popup.setOnHidden(event -> Event.fireEvent(comboBox, new Event(popup, comboBox, MFXComboBox.ON_HIDDEN)));

		comboBox.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				popup.show(comboBox, comboBox.getPopupAlignment(), comboBox.getPopupOffsetX(), comboBox.getPopupOffsetY());
				animateIcon(comboBox.getTrailingIcon(), true);
			}
		});

		popup.addEventFilter(WindowEvent.WINDOW_SHOWING, event ->
				AnimationUtils.PauseBuilder.build()
						.setDuration(20)
						.setOnFinished(end -> {
							if (comboBox.isScrollOnOpen()) {
								int selectedIndex = comboBox.getSelectedIndex();
								if (selectedIndex >= 0) virtualFlow.scrollTo(selectedIndex);
							}
						})
						.getAnimation()
						.play());
	}

	/**
	 * Responsible for updating the combo's text with the given item.
	 * <p>
	 * The item is converted using the combo's {@link MFXComboBox#converterProperty()}.
	 * In case it's null uses toString().
	 * <p>
	 * The caret is always positioned at the end of the text after the update.
	 */
	protected void updateValue(T item) {
		MFXComboBox<T> comboBox = getComboBox();
		String s = "";
		if (item != null) {
			StringConverter<T> converter = comboBox.getConverter();
			s = converter != null ? converter.toString(item) : item.toString();
		}
		comboBox.setText(s);
		comboBox.positionCaret(s.length());
	}

	/**
	 * Animates the trailing icon using the {@link MFXComboBox#animationProviderProperty()}.
	 */
	protected void animateIcon(Node icon, boolean showing) {
		MFXComboBox<T> comboBox = getComboBox();
		if (icon == null || comboBox.getAnimationProvider() == null) return;
		comboBox.getAnimationProvider().apply(icon, showing).play();
	}

	/**
	 * Responsible for creating the combo box's popup.
	 */
	protected MFXPopup createPopup() {
		MFXComboBox<T> comboBox = getComboBox();
		MFXPopup popup = new MFXPopup() {
			@Override
			public String getUserAgentStylesheet() {
				return comboBox.getUserAgentStylesheet();
			}
		};
		popup.getStyleClass().add("combo-popup");
		popup.setPopupStyleableParent(comboBox);
		popup.setAutoHide(true);
		popup.setConsumeAutoHidingEvents(true);
		popupManager = event -> {
			if (comboBox.getItems().isEmpty()) return;
			comboBox.show();
		};
		popup.setContent(createPopupContent());
		return popup;
	}

	/**
	 * Responsible for creating the popup's content.
	 */
	protected Node createPopupContent() {
		MFXComboBox<T> comboBox = getComboBox();
		if (virtualFlow == null) {
			virtualFlow = new SimpleVirtualFlow<>(
					comboBox.itemsProperty(),
					comboBox.getCellFactory(),
					Orientation.VERTICAL
			) {
				@Override
				public String getUserAgentStylesheet() {
					return comboBox.getUserAgentStylesheet();
				}
			};
			virtualFlow.cellFactoryProperty().bind(comboBox.cellFactoryProperty());
			virtualFlow.prefWidthProperty().bind(comboBox.widthProperty());
		}
		return virtualFlow;
	}

	/**
	 * Convenience method to cast {@link #getSkinnable()} to {@code MFXComboBox}.
	 */
	@SuppressWarnings("unchecked")
	public MFXComboBox<T> getComboBox() {
		return (MFXComboBox<T>) getSkinnable();
	}


	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public void dispose() {
		super.dispose();
		MFXComboBox<T> comboBox = getComboBox();
		if (comboBox.getTrailingIcon() != null) {
			comboBox.getTrailingIcon().removeEventHandler(MouseEvent.MOUSE_PRESSED, popupManager);
		}
		popupManager = null;
		virtualFlow = null;
	}
}
