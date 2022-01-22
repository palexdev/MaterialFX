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

package io.github.palexdev.materialfx.dialogs;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.Map;

/**
 * Builder class for {@link MFXGenericDialog}s with fluent api.
 */
public class MFXGenericDialogBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXGenericDialog dialog;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXGenericDialogBuilder() {
		dialog = new MFXGenericDialog();
	}

	public MFXGenericDialogBuilder(MFXGenericDialog dialog) {
		this.dialog = dialog;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Uses the no-arg constructor, starts from a new {@link MFXGenericDialog}.
	 */
	public static MFXGenericDialogBuilder build() {
		return new MFXGenericDialogBuilder();
	}

	/**
	 * Uses the {@link #MFXGenericDialogBuilder(MFXGenericDialog)} constructor, starts from the given dialog.
	 */
	public static MFXGenericDialogBuilder build(MFXGenericDialog dialog) {
		return new MFXGenericDialogBuilder(dialog);
	}

	/**
	 * Sets the dialog's header icon.
	 */
	public MFXGenericDialogBuilder setHeaderIcon(Node headerIcon) {
		dialog.setHeaderIcon(headerIcon);
		return this;
	}

	/**
	 * Sets the dialog's header text.
	 */
	public MFXGenericDialogBuilder setHeaderText(String headerText) {
		dialog.setHeaderText(headerText);
		return this;
	}

	/**
	 * Sets the dialog's content.
	 */
	public MFXGenericDialogBuilder setContent(Node content) {
		dialog.setContent(content);
		return this;
	}

	/**
	 * Sets the dialog's content text.
	 */
	public MFXGenericDialogBuilder setContentText(String contentText) {
		dialog.setContentText(contentText);
		return this;
	}

	/**
	 * Shows/hides the dialog's close button.
	 */
	public MFXGenericDialogBuilder setShowClose(boolean showClose) {
		dialog.setShowClose(showClose);
		return this;
	}

	/**
	 * Shows/hides the dialog's minimize button.
	 */
	public MFXGenericDialogBuilder setShowMinimize(boolean showMinimize) {
		dialog.setShowMinimize(showMinimize);
		return this;
	}

	/**
	 * Shows/hides the dialog's always on top button.
	 */
	public MFXGenericDialogBuilder setShowAlwaysOnTop(boolean showAlwaysOnTop) {
		dialog.setShowAlwaysOnTop(showAlwaysOnTop);
		return this;
	}

	/**
	 * Sets the dialog's actions pane orientation.
	 */
	public MFXGenericDialogBuilder setActionsOrientation(Orientation orientation) {
		dialog.setActionsOrientation(orientation);
		return this;
	}

	/**
	 * Sets the action to perform when the close button is pressed.
	 */
	public MFXGenericDialogBuilder setOnClose(EventHandler<MouseEvent> onClose) {
		dialog.setOnClose(onClose);
		return this;
	}

	/**
	 * Sets the action to perform when the minimize button is pressed.
	 */
	public MFXGenericDialogBuilder setOnMinimize(EventHandler<MouseEvent> onMinimize) {
		dialog.setOnMinimize(onMinimize);
		return this;
	}

	/**
	 * Sets the action to perform when the always on top button is pressed.
	 */
	public MFXGenericDialogBuilder setOnAlwaysOnTop(EventHandler<MouseEvent> onAlwaysOnTop) {
		dialog.setOnAlwaysOnTop(onAlwaysOnTop);
		return this;
	}

	/**
	 * Makes the dialog's content scrollable using {@link MFXGenericDialog#buildScrollableContent(boolean)}.
	 */
	public MFXGenericDialogBuilder makeScrollable(boolean smoothScrolling) {
		dialog.buildScrollableContent(smoothScrolling);
		return this;
	}

	/**
	 * Adds the given style classes to the dialog.
	 */
	public MFXGenericDialogBuilder addStyleClasses(String... styleClasses) {
		dialog.getStyleClass().addAll(styleClasses);
		return this;
	}

	/**
	 * Adds the given stylesheets to the dialog.
	 */
	public MFXGenericDialogBuilder addStylesheets(String... stylesheets) {
		dialog.getStylesheets().addAll(stylesheets);
		return this;
	}

	/**
	 * Adds the given nodes to the dialog's action pane.
	 *
	 * @see MFXGenericDialog#addActions(Node...)
	 */
	public MFXGenericDialogBuilder addActions(Node... actions) {
		dialog.addActions(actions);
		return this;
	}

	/**
	 * Adds the given nodes to the dialog's action pane.
	 *
	 * @see MFXGenericDialog#addActions(Map.Entry[])
	 */
	@SafeVarargs
	public final MFXGenericDialogBuilder addActions(Map.Entry<Node, EventHandler<MouseEvent>>... actions) {
		dialog.addActions(actions);
		return this;
	}

	/**
	 * @return the built dialog
	 */
	public MFXGenericDialog get() {
		return dialog;
	}

	/**
	 * Uses the built dialog as a content for a {@link MFXStageDialog}, passes
	 * to {@link MFXStageDialogBuilder} to customize it.
	 */
	public MFXStageDialogBuilder toStageDialogBuilder() {
		MFXStageDialog stageDialog = new MFXStageDialog(dialog);
		dialog.alwaysOnTopProperty().bind(stageDialog.alwaysOnTopProperty());
		dialog.setOnAlwaysOnTop(event -> stageDialog.setAlwaysOnTop(!dialog.isAlwaysOnTop()));
		dialog.setOnMinimize(event -> stageDialog.setIconified(true));
		dialog.setOnClose(event -> stageDialog.close());
		return MFXStageDialogBuilder.build(stageDialog);
	}
}
