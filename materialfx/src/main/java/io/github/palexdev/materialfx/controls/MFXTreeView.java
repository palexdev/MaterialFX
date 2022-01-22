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
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.selection.TreeSelectionModel;
import io.github.palexdev.materialfx.selection.base.ITreeSelectionModel;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * This is the container for a tree made of AbstractMFXTreeItems.
 *
 * @param <T> The type of the data within the items.
 */
public class MFXTreeView<T> extends MFXScrollPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-tree-view";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTreeView.css");

	private final ObjectProperty<AbstractMFXTreeItem<T>> root = new SimpleObjectProperty<>(null);
	private final ObjectProperty<ITreeSelectionModel<T>> selectionModel = new SimpleObjectProperty<>(null);
	private final BooleanProperty showRoot = new SimpleBooleanProperty(true);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTreeView() {
		installSelectionModel();

		initialize();
	}

	public MFXTreeView(MFXTreeItem<T> root) {
		installSelectionModel();

		root.setTreeView(this);
		setRoot(root);
		setContent(root);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the style class, sets the pref size, adds smooth vertical scroll and
	 * binds the root prefWidth to this control width property (minus 10).
	 */
	protected void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setPrefSize(250, 500);

		AbstractMFXTreeItem<T> root = getRoot();
		rootProperty().addListener((observable, oldRoot, newRoot) -> {
			newRoot.setTreeView(this);
			setContent(newRoot);
			setupRoot();
		});
		if (root != null) {
			setupRoot();
		}

		showRoot.addListener((observable, oldValue, newValue) -> {
			if (root != null) {
				root.fireEvent(new TreeViewEvent(TreeViewEvent.HIDE_ROOT_EVENT, newValue));
			}
		});
	}

	/**
	 * Contains common code for when the root is set the first time and when it changes.
	 */
	public void setupRoot() {
		AbstractMFXTreeItem<T> root = getRoot();
		root.prefWidthProperty().bind(widthProperty().subtract(10));
		root.setPadding(InsetsFactory.bottom(5));
		NodeUtils.waitForSkin(root, () -> {
			if (!isShowRoot()) {
				root.fireEvent(new TreeViewEvent(TreeViewEvent.HIDE_ROOT_EVENT, isShowRoot()));
			}
		}, true, true);
	}

	/**
	 * Installs the default selection model to use for the tree.
	 * <p>
	 * By default it is set to allow multiple selection.
	 */
	protected void installSelectionModel() {
		ITreeSelectionModel<T> selectionModel = new TreeSelectionModel<>();
		selectionModel.setAllowsMultipleSelection(true);
		setSelectionModel(selectionModel);
	}

	public AbstractMFXTreeItem<T> getRoot() {
		return root.get();
	}

	public ObjectProperty<AbstractMFXTreeItem<T>> rootProperty() {
		return root;
	}

	public void setRoot(AbstractMFXTreeItem<T> root) {
		this.root.set(root);
	}

	public ITreeSelectionModel<T> getSelectionModel() {
		return selectionModel.get();
	}

	public ObjectProperty<ITreeSelectionModel<T>> selectionModelProperty() {
		return selectionModel;
	}

	public void setSelectionModel(ITreeSelectionModel<T> selectionModel) {
		this.selectionModel.set(selectionModel);
	}

	public boolean isShowRoot() {
		return showRoot.get();
	}

	public void setShowRoot(boolean showRoot) {
		this.showRoot.set(showRoot);
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	/**
	 * Events class for tree views.
	 * <p>
	 * Defines a new EventType:
	 * <p>
	 * - HIDE_ROOT_EVENT: when the tree view's root is set to be hidden/visible we need to fire this event
	 * because we need to communicate with the root's skin.
	 * <p>
	 * Of course these events are for internal use only so they should not be used by users.
	 */
	public static class TreeViewEvent extends Event {
		private final boolean show;

		public static final EventType<TreeViewEvent> HIDE_ROOT_EVENT = new EventType<>(ANY, "HIDE_ROOT_EVENT");

		public TreeViewEvent(EventType<? extends Event> eventType, boolean show) {
			super(eventType);
			this.show = show;
		}

		public boolean isShow() {
			return show;
		}
	}
}
