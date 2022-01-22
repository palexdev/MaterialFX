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

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.ParallelBuilder;
import io.github.palexdev.virtualizedfx.cell.Cell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Implementation of a {@link Cell} for usage with {@link MFXNotificationCenter}.
 * <p></p>
 * Includes a checkbox to allow selecting notifications.
 */
public class MFXNotificationCell extends HBox implements Cell<INotification> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-notification-cell";
	private final MFXNotificationCenter notificationCenter;
	private final ReadOnlyObjectWrapper<INotification> notification = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper();
	private final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();

	protected final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
	protected final StackPane container;
	protected final MFXCheckbox checkbox;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXNotificationCell(MFXNotificationCenter notificationCenter, INotification notification) {
		this.notificationCenter = notificationCenter;
		setNotification(notification);

		setPrefHeight(80);
		setMaxHeight(USE_PREF_SIZE);
		setAlignment(Pos.CENTER_LEFT);

		checkbox = new MFXCheckbox("");
		checkbox.setId("check");

		container = new StackPane(checkbox);
		container.setMinWidth(USE_PREF_SIZE);
		container.setPrefWidth(0);
		container.setMaxWidth(USE_PREF_SIZE);

		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(container.widthProperty());
		clip.heightProperty().bind(container.heightProperty());
		container.setClip(clip);

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the style class, calls {@link #setBehavior()} then {@link #render(INotification)}
	 * for the first time.
	 */
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setBehavior();
		render(getNotification());
		if (notificationCenter.isSelectionMode()) expand(true);
	}

	/**
	 * Sets the following behaviors:
	 * <p>
	 * - Binds the selected property to the notification center' selection model (checks for index). <p>
	 * - Updates the selected PseudoClass state when selected property changes. <p>
	 * - Adds a listener to the checkbox' selection state to call {@link #updateSelection(boolean)}. <p>
	 * - Adds a listener to the notification center's {@link MFXNotificationCenter#selectionModeProperty()} to call {@link #expand(boolean)}.
	 */
	protected void setBehavior() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(() -> {
			boolean contained = notificationCenter.getSelectionModel().getSelection().containsKey(getIndex());
			checkbox.setSelected(contained);
			return contained;
		}, notificationCenter.getSelectionModel().selectionProperty(), index));

		checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> updateSelection(newValue));
		notificationCenter.selectionModeProperty().addListener((observable, oldValue, newValue) -> expand(newValue));
	}

	/**
	 * Responsible for rendering the cell's content.
	 */
	protected void render(INotification notification) {
		if (notificationCenter.isSelectionMode()) {
			checkbox.setOpacity(1.0);
			checkbox.setPrefWidth(45);
		}
		getChildren().setAll(container, notification.getContent());
	}

	/**
	 * Responsible for updating the selection state according to the checkbox' state.
	 * <p>
	 * If checked is true then the cell should be selected, otherwise it is deselected.
	 */
	protected void updateSelection(boolean checked) {
		int index = getIndex();
		if (checked) {
			notificationCenter.getSelectionModel().selectIndex(index);
		} else {
			notificationCenter.getSelectionModel().deselectIndex(index);
		}
	}

	/**
	 * Responsible for showing/hiding the checkbox.
	 */
	protected void expand(boolean selectionMode) {
		double width = selectionMode ? 45 : 0;
		double opacity = selectionMode ? 1 : 0;
		if (notificationCenter.isAnimated()) {
			ParallelBuilder.build()
					.add(
							KeyFrames.of(150, checkbox.opacityProperty(), opacity, Interpolators.EASE_OUT),
							KeyFrames.of(250, container.prefWidthProperty(), width, Interpolators.EASE_OUT_SINE)
					).getAnimation().play();
		} else {
			container.setPrefWidth(width);
			checkbox.setOpacity(opacity);
		}
		if (!selectionMode) {
			notificationCenter.getSelectionModel().clearSelection();
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	@Override
	public Node getNode() {
		return this;
	}

	/**
	 * Updates the notification property of the cell, then calls {@link #render(INotification)}.
	 * <p>
	 * This is called after {@link #updateIndex(int)}.
	 */
	@Override
	public void updateItem(INotification notification) {
		setNotification(notification);
		render(notification);
	}

	/**
	 * Updates the index property of the cell.
	 * <p>
	 * This is called before {@link #updateItem(INotification)}.
	 */
	@Override
	public void updateIndex(int index) {
		setIndex(index);
	}

	/**
	 * Ensures that the combobox container is properly expanded
	 * after the cell has been laid out.
	 */
	@Override
	public void afterLayout() {
		expand(notificationCenter.isSelectionMode());
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	public INotification getNotification() {
		return this.notification.get();
	}

	/**
	 * Specifies the current shown notification (in other words the cell's content).
	 */
	public ReadOnlyObjectProperty<INotification> notificationProperty() {
		return this.notification.getReadOnlyProperty();
	}

	protected void setNotification(INotification notification) {
		this.notification.set(notification);
	}

	public int getIndex() {
		return this.index.get();
	}

	/**
	 * Specifies the cell's index.
	 */
	protected ReadOnlyIntegerProperty indexProperty() {
		return this.index.getReadOnlyProperty();
	}

	protected void setIndex(int index) {
		this.index.set(index);
	}

	public boolean isSelected() {
		return this.selected.get();
	}

	/**
	 * Specifies the selection state of the cell.
	 */
	public ReadOnlyBooleanProperty selectedProperty() {
		return this.selected.getReadOnlyProperty();
	}

	protected void setSelected(boolean selected) {
		this.selected.set(selected);
	}
}
