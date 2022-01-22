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

import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Builder class for {@link MFXStageDialog}s with fluent api.
 */
public class MFXStageDialogBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXStageDialog stageDialog;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXStageDialogBuilder() {
		this.stageDialog = new MFXStageDialog();
	}

	public MFXStageDialogBuilder(MFXStageDialog stageDialog) {
		this.stageDialog = stageDialog;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Uses the no-arg constructor, starts from a new {@link MFXStageDialog}.
	 */
	public static MFXStageDialogBuilder build() {
		return new MFXStageDialogBuilder();
	}

	/**
	 * Uses the {@link #MFXStageDialogBuilder(MFXStageDialog)} constructor, starts from the given dialog.
	 */
	public static MFXStageDialogBuilder build(MFXStageDialog stageDialog) {
		return new MFXStageDialogBuilder(stageDialog);
	}

	/**
	 * Sets the dialog's content.
	 */
	public MFXStageDialogBuilder setContent(AbstractMFXDialog content) {
		stageDialog.setContent(content);
		return this;
	}

	/**
	 * Sets the dialog's owner node.
	 */
	public MFXStageDialogBuilder setOwnerNode(Pane ownerNode) {
		stageDialog.setOwnerNode(ownerNode);
		return this;
	}

	/**
	 * Sets whether the dialog should be centered on the owner node
	 * when shown.
	 */
	public MFXStageDialogBuilder setCenterInOwnerNode(boolean centerInOwnerNode) {
		stageDialog.setCenterInOwnerNode(centerInOwnerNode);
		return this;
	}

	/**
	 * Sets whether to scrim the dialog's owner node on showing.
	 */
	public MFXStageDialogBuilder setScrimOwner(boolean scrimOwner) {
		stageDialog.setScrimOwner(scrimOwner);
		return this;
	}

	/**
	 * Sets the scrim effect strength(opacity).
	 */
	public MFXStageDialogBuilder setScrimStrength(double scrimStrength) {
		stageDialog.setScrimStrength(scrimStrength);
		return this;
	}

	/**
	 * Sets the enum constant used to specify how to apply the scrim effect.
	 * You can have two owners, one is the stage owner(Window) and the other is the dialog owner(Pane).
	 * Sometimes it's better to apply the scrim to the window (for example the owner node would not allow to apply the
	 * scrim effect, for example AnchorPanes, VBoxes, HBoxes...), but you still want to center the dialog in the owner node.
	 * Setting this to {@link ScrimPriority#WINDOW} will tell the dialog to apply the effect to owner window,
	 * setting this to {@link ScrimPriority#NODE} will tell the dialog to apply the effect to owner node.
	 */
	public MFXStageDialogBuilder setScrimPriority(ScrimPriority scrimPriority) {
		stageDialog.setScrimPriority(scrimPriority);
		return this;
	}

	/**
	 * Sets whether the dialog is draggable.
	 */
	public MFXStageDialogBuilder setDraggable(boolean draggable) {
		stageDialog.setDraggable(draggable);
		return this;
	}

	/**
	 * Sets whether to close the dialog when pressing on its owner node.
	 */
	public MFXStageDialogBuilder setOverlayClose(boolean overlayClose) {
		stageDialog.setOverlayClose(overlayClose);
		return this;
	}

	/**
	 * Sets the dialog's modality.
	 */
	public MFXStageDialogBuilder initModality(Modality modality) {
		stageDialog.initModality(modality);
		return this;
	}

	/**
	 * Sets the dialog's owner window.
	 */
	public MFXStageDialogBuilder initOwner(Window owner) {
		stageDialog.initOwner(owner);
		return this;
	}

	/**
	 * Sets the dialog's title.
	 */
	public MFXStageDialogBuilder setTitle(String title) {
		stageDialog.setTitle(title);
		return this;
	}

	/**
	 * Sets whether the dialog should stay always on top.
	 */
	public MFXStageDialogBuilder setAlwaysOnTop(boolean alwaysOnTop) {
		stageDialog.setAlwaysOnTop(alwaysOnTop);
		return this;
	}

	/**
	 * Sets tha action to perform on a close request.
	 */
	public MFXStageDialogBuilder setOnCloseRequest(EventHandler<WindowEvent> handler) {
		stageDialog.setOnCloseRequest(handler);
		return this;
	}

	/**
	 * Sets tha action to perform on showing.
	 */
	public MFXStageDialogBuilder setOnShowing(EventHandler<WindowEvent> handler) {
		stageDialog.setOnShowing(handler);
		return this;
	}

	/**
	 * Sets tha action to perform on shown.
	 */
	public MFXStageDialogBuilder setOnShown(EventHandler<WindowEvent> handler) {
		stageDialog.setOnShown(handler);
		return this;
	}

	/**
	 * Sets tha action to perform on hiding.
	 */
	public MFXStageDialogBuilder setOnHiding(EventHandler<WindowEvent> handler) {
		stageDialog.setOnHiding(handler);
		return this;
	}

	/**
	 * Sets tha action to perform on hidden.
	 */
	public MFXStageDialogBuilder setOnHidden(EventHandler<WindowEvent> handler) {
		stageDialog.setOnHidden(handler);
		return this;
	}

	/**
	 * Delegate for {@link Stage#addEventHandler(EventType, EventHandler)}.
	 */
	public <T extends Event> MFXStageDialogBuilder addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		stageDialog.addEventHandler(eventType, eventHandler);
		return this;
	}

	/**
	 * Delegate for {@link Stage#removeEventHandler(EventType, EventHandler)}.
	 */
	public <T extends Event> MFXStageDialogBuilder removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		stageDialog.removeEventHandler(eventType, eventHandler);
		return this;
	}

	/**
	 * Delegate for {@link Stage#addEventFilter(EventType, EventHandler)}.
	 */
	public <T extends Event> MFXStageDialogBuilder addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		stageDialog.addEventFilter(eventType, eventFilter);
		return this;
	}

	/**
	 * Delegate for {@link Stage#removeEventFilter(EventType, EventHandler)}.
	 */
	public <T extends Event> MFXStageDialogBuilder removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		stageDialog.removeEventFilter(eventType, eventFilter);
		return this;
	}

	/**
	 * @return the built stage dialog
	 */
	public MFXStageDialog get() {
		return stageDialog;
	}
}
