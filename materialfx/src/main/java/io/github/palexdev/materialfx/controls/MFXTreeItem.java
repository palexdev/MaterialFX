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
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.controls.cell.MFXSimpleTreeCell;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.selection.TreeSelectionModel;
import io.github.palexdev.materialfx.selection.base.ITreeSelectionModel;
import io.github.palexdev.materialfx.skins.MFXTreeItemSkin;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.css.*;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of an animated tree item.
 * <p>
 * Concrete implementation of {@link AbstractMFXTreeItem<T>} to provide less common functionalities such expand/collapse animations.
 * <p>
 * The default associated {@link Skin} is {@link MFXTreeItemSkin<T>}.
 * <p>
 * Overrides the layoutChildren method to set the {@link #items} margin to 20 by default.
 * To change it you have to override the method inline or by extending this class.
 *
 * @param <T> The type of the data within TreeItem.
 * @see AbstractMFXTreeCell
 * @see MFXTreeView
 * @see ITreeSelectionModel
 */
public class MFXTreeItem<T> extends AbstractMFXTreeItem<T> {
	//================================================================================
	// Properties
	//================================================================================
	private static final StyleablePropertyFactory<MFXTreeItem<?>> FACTORY = new StyleablePropertyFactory<>(MFXTreeItem.getClassCssMetaData());
	private final String STYLE_CLASS = "mfx-tree-item";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTreeItem.css");

	private final BooleanProperty expanded = new SimpleBooleanProperty(false);
	private final ReadOnlyBooleanWrapper animationRunning = new ReadOnlyBooleanWrapper(false);
	private final ReadOnlyDoubleWrapper initialHeight = new ReadOnlyDoubleWrapper(0);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTreeItem(T data) {
		super(data);
		defaultCellFactory();
		initialize();
	}

	public MFXTreeItem(T data, Callback<AbstractMFXTreeItem<T>, AbstractMFXTreeCell<T>> cellFactory) {
		super(data);
		setCellFactory(cellFactory);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the style class to "mfx-tree-view".
	 * <p>
	 * Adds a listener to the items list to update the added/removed item parent accordingly.
	 * <p>
	 * Adds a listener to {@link #selectedProperty()} and the {@link #treeViewProperty()} allowing item selection before the Scene is shown
	 * by calling the TreeSelectionModel {@link TreeSelectionModel#scanTree(AbstractMFXTreeItem)} method.
	 * <p>
	 * Adds a listener to {@link #childrenMarginProperty()} to request layout in case it changes.
	 */
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		items.addListener((ListChangeListener<? super AbstractMFXTreeItem<T>>) change -> {
			List<AbstractMFXTreeItem<T>> tmpRemoved = new ArrayList<>();
			List<AbstractMFXTreeItem<T>> tmpAdded = new ArrayList<>();

			while (change.next()) {
				tmpRemoved.addAll(change.getRemoved());
				tmpAdded.addAll(change.getAddedSubList());
			}

			updateChildrenParent(tmpRemoved, null);
			updateChildrenParent(tmpAdded, this);
		});

		treeViewProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && isRoot()) {
				newValue.getSelectionModel().scanTree(getRoot());
			}
		});

		selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue && getSelectionModel() != null) {
				getSelectionModel().scanTree(this);
			}
		});

		childrenMarginProperty().addListener((observable, oldValue, newValue) -> requestLayout());
	}

	/**
	 * @return the item's expand state
	 */
	public boolean isExpanded() {
		return expanded.get();
	}

	/**
	 * Expand property.
	 * <p>
	 * <b>NOTE: if you want the item to be expanded by default you must use {@link #startExpandedProperty()} instead.</b>
	 */
	public BooleanProperty expandedProperty() {
		return expanded;
	}

	/**
	 * Sets the item's expand state.
	 * <p>
	 * <b>WARNING: THIS METHOD IS FOR INTERNAL USE ONLY, THUS ITS USAGE IS NOT RECOMMENDED.</b>
	 */
	public void setExpanded(boolean expanded) {
		this.expanded.set(expanded);
	}

	/**
	 * @return the item's height when it's first laid out
	 */
	public double getInitialHeight() {
		return initialHeight.get();
	}

	/**
	 * Initial height property.
	 * <p>
	 * We need this to set the fixed initial prefHeight of the control and for the calculation
	 * of the collapse event prefHeight.
	 * <p>
	 * <b>WARNING: THIS PROPERTY IS FOR INTERNAL USE ONLY</b>
	 */
	public ReadOnlyDoubleProperty initialHeightProperty() {
		return initialHeight;
	}

	/**
	 * Sets the initial height property.
	 * <p>
	 * <b>WARNING: THIS METHOD IS FOR INTERNAL USE ONLY, THUS ITS USAGE IS NOT RECOMMENDED.</b>
	 */
	public void setInitialHeight(double height) {
		initialHeight.set(height);
	}

	/**
	 * @return the state of the expand/collapse animation on this item.
	 */
	public boolean isAnimationRunning() {
		return animationRunning.get();
	}

	/**
	 * Property to check if an animation is running on the control. It is bound into the Skin class.
	 *
	 * @see MFXTreeItemSkin
	 */
	public ReadOnlyBooleanWrapper animationRunningProperty() {
		return animationRunning;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================

	/**
	 * Specifies the duration of the expand/collapse animation (milliseconds).
	 * <p>
	 * Too high values are not recommended.
	 */
	private final StyleableDoubleProperty animationDuration = new SimpleStyleableDoubleProperty(
			StyleableProperties.DURATION,
			this,
			"animationDuration",
			250.0
	);

	public double getAnimationDuration() {
		return animationDuration.get();
	}

	public StyleableDoubleProperty animationDurationProperty() {
		return animationDuration;
	}

	public void setAnimationDuration(double animationDuration) {
		this.animationDuration.set(animationDuration);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXTreeItem<?>, Number> DURATION =
				FACTORY.createSizeCssMetaData(
						"-mfx-animation-duration",
						MFXTreeItem::animationDurationProperty,
						250.0
				);

		static {
			cssMetaDataList = List.of(DURATION);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Retrieves the selection model instance from the TreeView which contains the tree.
	 */
	@Override
	public ITreeSelectionModel<T> getSelectionModel() {
		if (getTreeView() != null) {
			return getTreeView().getSelectionModel();
		}
		return null;
	}

	/**
	 * If no cell factory is specified in the constructor then we provide a default one.
	 * <p>
	 * This method is abstract in the superclass because a default cell factory should always be provided.
	 */
	@Override
	protected void defaultCellFactory() {
		super.cellFactory.set(MFXSimpleTreeCell::new);
	}

	/**
	 * Used in the items listener added by the {@link #initialize()} method.
	 * <p>
	 * When an item is added/removed its parent should be updated accordingly.
	 *
	 * @param treeItems the items for which to update the parent
	 * @param newParent the parent to set (or null in case of removed items)
	 */
	@Override
	protected void updateChildrenParent(List<? extends AbstractMFXTreeItem<T>> treeItems, final AbstractMFXTreeItem<T> newParent) {
		treeItems.forEach(item -> item.setItemParent(newParent));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTreeItemSkin<>(this);
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXTreeItem.getControlCssMetaDataList();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	/**
	 * Simple layout strategy. Each item in the {@link #items} list has a left margin defined by {@link #childrenMarginProperty()}.
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		items.forEach(item -> VBox.setMargin(item, InsetsFactory.right(getChildrenMargin())));
	}

	@Override
	public String toString() {
		String className = getClass().getName();
		String simpleName = className.substring(className.lastIndexOf('.') + 1);
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(simpleName);
		sb.append('@');
		sb.append(Integer.toHexString(hashCode()));
		sb.append("]");
		sb.append("[Data:").append(getData()).append("]");
		if (getId() != null) {
			sb.append("[id:").append(getId()).append("]");
		}

		return sb.toString();
	}

	//================================================================================
	// Events
	//================================================================================

	/**
	 * Events class for the items.
	 * <p>
	 * Defines three new EventTypes:
	 * <p>
	 * - ADD_REMOVE_ITEM_EVENT: when an item is added/removed the item and all the parents up to the root should adjust their height. <p></p>
	 * - EXPAND_EVENT: communicates to the item and all the parents up to the root to expand and therefore to adjust their height. <p></p>
	 * - COLLAPSE_EVENT: communicates to the item and all the parents up to the root to collapse and therefore to adjust their height. <p></p>
	 * <p>
	 * Note on constructor: when we fire an event we pass the item reference to consume the event at the proper time.
	 * <p>
	 * Of course these events are for internal use only so they should not be used by users.
	 */
	public static class TreeItemEvent<T> extends Event {
		private final WeakReference<AbstractMFXTreeItem<T>> itemRef;
		private final double value;

		public static final EventType<TreeItemEvent<?>> ADD_REMOVE_ITEM_EVENT = new EventType<>(ANY, "ADD_ITEM_EVENT");
		public static final EventType<TreeItemEvent<?>> EXPAND_EVENT = new EventType<>(ANY, "EXPAND_EVENT");
		public static final EventType<TreeItemEvent<?>> COLLAPSE_EVENT = new EventType<>(ANY, "COLLAPSE_EVENT");

		public TreeItemEvent(EventType<? extends Event> eventType, AbstractMFXTreeItem<T> item, double value) {
			super(eventType);
			this.itemRef = new WeakReference<>(item);
			this.value = value;
		}

		public AbstractMFXTreeItem<T> getItem() {
			return itemRef.get();
		}

		public double getValue() {
			return value;
		}
	}
}
