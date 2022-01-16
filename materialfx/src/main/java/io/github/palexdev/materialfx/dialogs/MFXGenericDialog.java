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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * Basic implementation of a modern generic dialog.
 * <p>
 * It is composed of three main parts:
 * <p> - the header: which is an {@link HBox} containing an icon(optional) and a {@link Label}
 * to show the {@link #headerTextProperty()}. It also contains three buttons to minimize, close and
 * set always on top. All three of them can be hidden. By default they don't do anything, they have been
 * added since this dialog in indented to be used in conjunction with {@link MFXStageDialog}, see
 * {@link MFXGenericDialogBuilder#toStageDialogBuilder()} for an example on how those buttons are used
 * <p> - the content: which is just a {@link Label} to show the {@link #contentTextProperty()}, the
 * content can be easily changed in many ways: you can set the property {@link #contentProperty()},
 * you can override {@link #buildContent()} or you can override {@link #buildScrollableContent(boolean)}.
 * By default the dialog uses the content built by {@link #buildContent()}. The other method, {@link #buildScrollableContent(boolean)},
 * by default is just wrapping the {@link #buildContent()} result in a {@link MFXScrollPane}
 * <p> - the footer/actions: which is a {@link HBox} or {@link VBox} (depending on the {@link #actionsOrientationProperty()}),
 * which contains a list of arbitrary nodes specified by the user through: {@link #addActions(Node...)} or {@link #addActions(Map.Entry[])},
 * you can also clear them with {@link #clearActions()}. Note that despite being generic {@link Node}s, it is intended to be used
 * with buttons and the like
 * <p></p>
 * This dialog also offers a property to indicate whether the dialog is set to be always on top or not,
 * {@link #alwaysOnTopProperty()}, also has a new related {@link PseudoClass}: ":always-on-top".
 */
public class MFXGenericDialog extends AbstractMFXDialog {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXDialogs.css");

	protected HBox header;
	protected Label headerLabel;
	protected MFXIconWrapper alwaysOnTopIcon;
	protected MFXIconWrapper minimizeIcon;
	protected MFXIconWrapper closeIcon;
	protected Pane actions;

	private final ObjectProperty<Node> headerIcon = new SimpleObjectProperty<>();
	private final StringProperty headerText = new SimpleStringProperty();
	private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
	private final StringProperty contentText = new SimpleStringProperty();

	private final BooleanProperty showClose = new SimpleBooleanProperty(true);
	private final BooleanProperty showMinimize = new SimpleBooleanProperty(true);
	private final BooleanProperty showAlwaysOnTop = new SimpleBooleanProperty(true);
	private final ObjectProperty<Orientation> actionsOrientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);

	private final EventHandlerProperty<MouseEvent> onClose = new EventHandlerProperty<>();
	private final EventHandlerProperty<MouseEvent> onMinimize = new EventHandlerProperty<>();
	private final EventHandlerProperty<MouseEvent> onAlwaysOnTop = new EventHandlerProperty<>();

	private final BooleanProperty alwaysOnTop = new SimpleBooleanProperty();
	protected static final PseudoClass ALWAYS_ON_TOP_PSEUDO_CLASS = PseudoClass.getPseudoClass("always-on-top");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXGenericDialog() {
		this("", "");
	}

	public MFXGenericDialog(String headerText, String contentText) {
		setHeaderText(headerText);
		setContentText(contentText);

		buildHeader();
		buildContent();
		buildActionsPane();

		setTop(header);
		setCenter(getContent());
		setBottom(actions);

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds handlers for the following changes/events:
	 * <p> - updates the ":always-on-top" PseudoClass when {@link #alwaysOnTopProperty()} changes
	 * <p> - updates the center node when {@link #contentProperty()} changes
	 * <p> - re-initializes the header when one of these properties change: {@link #showAlwaysOnTopProperty()},
	 * {@link #showMinimizeProperty()}, {@link #showCloseProperty()}
	 * <p> - adds/removes event handlers for the always on top, minimize and close icons, specified by:
	 * {@link #onAlwaysOnTopProperty()}, {@link #onMinimizeProperty()}, {@link #onCloseProperty()}
	 * <p> - re-builds the actions pane when {@link #actionsOrientationProperty()} changes
	 */
	private void initialize() {
		pseudoClassStateChanged(ALWAYS_ON_TOP_PSEUDO_CLASS, isAlwaysOnTop());
		alwaysOnTopProperty().addListener(invalidated -> pseudoClassStateChanged(ALWAYS_ON_TOP_PSEUDO_CLASS, isAlwaysOnTop()));

		contentProperty().addListener(invalidated -> setCenter(getContent()));

		showAlwaysOnTopProperty().addListener(invalidated -> initHeader());
		showMinimizeProperty().addListener(invalidated -> initHeader());
		showCloseProperty().addListener(invalidated -> initHeader());

		onAlwaysOnTopProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) alwaysOnTopIcon.removeEventHandler(MouseEvent.MOUSE_CLICKED, oldValue);
			if (newValue != null) alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, newValue);
		});
		onMinimizeProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) minimizeIcon.removeEventHandler(MouseEvent.MOUSE_CLICKED, oldValue);
			if (newValue != null) minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, newValue);
		});

		onCloseProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) closeIcon.removeEventHandler(MouseEvent.MOUSE_CLICKED, oldValue);
			if (newValue != null) closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, newValue);
		});

		actionsOrientationProperty().addListener(invalidated -> buildActionsPane());
	}

	/**
	 * Builds the default header.
	 */
	protected void buildHeader() {
		headerLabel = new Label();
		headerLabel.graphicProperty().bind(headerIcon);
		headerLabel.getStyleClass().add("header-label");
		headerLabel.textProperty().bind(headerTextProperty());
		headerLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(headerLabel, Priority.ALWAYS);

		alwaysOnTopIcon = new MFXIconWrapper("mfx-caret-up", 12, 24);
		minimizeIcon = new MFXIconWrapper("mfx-minus", 12, 24);
		closeIcon = new MFXIconWrapper("mfx-x-alt", 12, 24);

		alwaysOnTopIcon.setId("alwaysOnTop");
		minimizeIcon.setId("minimize");
		closeIcon.setId("close");

		alwaysOnTopIcon.visibleProperty().bind(showAlwaysOnTopProperty());
		minimizeIcon.visibleProperty().bind(showMinimizeProperty());
		closeIcon.visibleProperty().bind(showCloseProperty());

		NodeUtils.makeRegionCircular(alwaysOnTopIcon);
		NodeUtils.makeRegionCircular(minimizeIcon);
		NodeUtils.makeRegionCircular(closeIcon);

		header = new HBox(10, headerLabel);
		header.setAlignment(Pos.CENTER_LEFT);
		initHeader();
	}

	/**
	 * Initializes the header icons.
	 */
	private void initHeader() {
		header.getChildren().removeAll(alwaysOnTopIcon, minimizeIcon, closeIcon);
		if (isShowAlwaysOnTop()) header.getChildren().add(alwaysOnTopIcon);
		if (isShowMinimize()) header.getChildren().add(minimizeIcon);
		if (isShowClose()) header.getChildren().add(closeIcon);
	}

	/**
	 * Builds the default dialog's content.
	 */
	protected void buildContent() {
		Label content = new Label();
		content.setWrapText(true);
		content.getStyleClass().add("content");
		content.textProperty().bind(contentTextProperty());

		StackPane contentContainer = new StackPane(content);
		contentContainer.getStyleClass().add("content-container");
		contentContainer.setAlignment(Pos.TOP_LEFT);

		setContent(contentContainer);
	}

	/**
	 * Builds the same nodes as {@link #buildContent()} but wrapped in
	 * a {@link MFXScrollPane}.
	 *
	 * @param smoothScrolling to specify whether to use smooth scrolling on the {@link MFXScrollPane}
	 */
	protected void buildScrollableContent(boolean smoothScrolling) {
		Label content = new Label();
		content.setWrapText(true);
		content.getStyleClass().add("content");
		content.textProperty().bind(contentTextProperty());

		MFXScrollPane scrollPane = new MFXScrollPane(content);
		scrollPane.getStyleClass().add("content-container");
		scrollPane.setFitToWidth(true);
		if (smoothScrolling) ScrollUtils.addSmoothScrolling(scrollPane, 0.5);

		setContent(scrollPane);
	}

	/**
	 * Builds the actions pane according to the {@link #actionsOrientationProperty()}.
	 * <p></p>
	 * Note that actions are preserved when changing the Orientation.
	 */
	protected void buildActionsPane() {
		ObservableList<Node> children = FXCollections.observableArrayList();
		if (actions != null) {
			getChildren().remove(actions);
			children.addAll(actions.getChildren());
		}

		if (getActionsOrientation() == Orientation.HORIZONTAL) {
			HBox actions = new HBox(10);
			actions.setAlignment(Pos.CENTER_RIGHT);
			this.actions = actions;
		} else {
			VBox actions = new VBox(10);
			actions.setAlignment(Pos.TOP_RIGHT);
			this.actions = actions;
		}
		actions.getChildren().setAll(children);
		actions.getStyleClass().add("actions-pane");
	}

	/**
	 * Adds the specified nodes to the actions pane.
	 */
	public void addActions(Node... actions) {
		this.actions.getChildren().addAll(actions);
	}

	/**
	 * Each entry has a {@link Node} that will trigger the given action on {@link MouseEvent#MOUSE_CLICKED}.
	 * <p>
	 * For each entry, adds the given {@link EventHandler} to the given {@link Node}, than
	 * adds it to the actions pane.
	 */
	@SafeVarargs
	public final void addActions(Map.Entry<Node, EventHandler<MouseEvent>>... actions) {
		for (Map.Entry<Node, EventHandler<MouseEvent>> action : actions) {
			action.getKey().addEventHandler(MouseEvent.MOUSE_CLICKED, action.getValue());
			this.actions.getChildren().add(action.getKey());
		}
	}

	/**
	 * Removes all the nodes from the actions pane.
	 */
	public void clearActions() {
		actions.getChildren().clear();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Node getHeaderIcon() {
		return headerIcon.get();
	}

	/**
	 * Specifies the header's label icon
	 */
	public ObjectProperty<Node> headerIconProperty() {
		return headerIcon;
	}

	public void setHeaderIcon(Node headerIcon) {
		this.headerIcon.set(headerIcon);
	}

	public String getHeaderText() {
		return headerText.get();
	}

	/**
	 * Specifies the header's label text.
	 */
	public StringProperty headerTextProperty() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText.set(headerText);
	}

	public Node getContent() {
		return content.get();
	}

	/**
	 * Specifies the dialog's content.
	 */
	public ObjectProperty<Node> contentProperty() {
		return content;
	}

	public void setContent(Node content) {
		this.content.set(content);
	}

	public String getContentText() {
		return contentText.get();
	}

	/**
	 * Specifies the dialog's content text.
	 */
	public StringProperty contentTextProperty() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText.set(contentText);
	}

	public boolean isShowClose() {
		return showClose.get();
	}

	/**
	 * Specifies whether to show the close button in the header.
	 */
	public BooleanProperty showCloseProperty() {
		return showClose;
	}

	public void setShowClose(boolean showClose) {
		this.showClose.set(showClose);
	}

	public boolean isShowMinimize() {
		return showMinimize.get();
	}

	/**
	 * Specifies whether to show the minimize button in the header.
	 */
	public BooleanProperty showMinimizeProperty() {
		return showMinimize;
	}

	public void setShowMinimize(boolean showMinimize) {
		this.showMinimize.set(showMinimize);
	}

	public boolean isShowAlwaysOnTop() {
		return showAlwaysOnTop.get();
	}

	/**
	 * Specifies whether to show the always on top button in the header.
	 */
	public BooleanProperty showAlwaysOnTopProperty() {
		return showAlwaysOnTop;
	}

	public void setShowAlwaysOnTop(boolean showAlwaysOnTop) {
		this.showAlwaysOnTop.set(showAlwaysOnTop);
	}

	public Orientation getActionsOrientation() {
		return actionsOrientation.get();
	}

	/**
	 * Specifies the {@link Orientation} of the actions pane.
	 */
	public ObjectProperty<Orientation> actionsOrientationProperty() {
		return actionsOrientation;
	}

	public void setActionsOrientation(Orientation actionsOrientation) {
		this.actionsOrientation.set(actionsOrientation);
	}

	public EventHandler<MouseEvent> getOnClose() {
		return onClose.get();
	}

	/**
	 * Specifies the action to perform when the close button is pressed.
	 */
	public EventHandlerProperty<MouseEvent> onCloseProperty() {
		return onClose;
	}

	public void setOnClose(EventHandler<MouseEvent> onClose) {
		this.onClose.set(onClose);
	}

	public EventHandler<MouseEvent> getOnMinimize() {
		return onMinimize.get();
	}

	/**
	 * Specifies the action to perform when the minimize button is pressed.
	 */
	public EventHandlerProperty<MouseEvent> onMinimizeProperty() {
		return onMinimize;
	}

	public void setOnMinimize(EventHandler<MouseEvent> onMinimize) {
		this.onMinimize.set(onMinimize);
	}

	public EventHandler<MouseEvent> getOnAlwaysOnTop() {
		return onAlwaysOnTop.get();
	}

	/**
	 * Specifies the action to perform when the always on top button is pressed.
	 */
	public EventHandlerProperty<MouseEvent> onAlwaysOnTopProperty() {
		return onAlwaysOnTop;
	}

	public void setOnAlwaysOnTop(EventHandler<MouseEvent> onAlwaysOnTop) {
		this.onAlwaysOnTop.set(onAlwaysOnTop);
	}

	public boolean isAlwaysOnTop() {
		return alwaysOnTop.get();
	}

	/**
	 * Specifies whether the dialog should be always on top.
	 * (see class documentation for further details)
	 */
	public BooleanProperty alwaysOnTopProperty() {
		return alwaysOnTop;
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop.set(alwaysOnTop);
	}
}
