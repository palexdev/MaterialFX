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

import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.selection.TreeSelectionModel;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.github.palexdev.materialfx.controls.MFXTreeItem.TreeItemEvent;
import static io.github.palexdev.materialfx.controls.MFXTreeView.TreeViewEvent;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTreeItem}.
 * <p>
 * Extends {@link SkinBase}.
 * <p>
 * This class is important because it's what defines the layout of any MFXTreeItem, from the cell to the container
 * to the expand/collapse animations.
 * <p></p>
 * The base container is a VBox. It contains the item's cell and (N.B!!) other items so if you look at the children you
 * will see something like this:
 * <pre>
 *     {@code
 *     SimpleTreeCell@b91283f
 *     MFXTreeItem@77adfda
 *     MFXTreeItem@58b4c4ce
 *     MFXTreeItem@49d9b6c6
 *     }
 * </pre>
 * The container has its max and min heights set to use PREF_SIZE. The prefHeight is adjusted programmatically
 * when an EXPAND/COLLAPSE event occurs or when a ADD/REMOVE event occurs and the item is expanded.
 * <p>
 * To create the expand/collapse effect the container is clipped with a Rectangle which height and width
 * are bound to the container ones.
 * <p>
 * Since the view (this skin) is separated from the control (the MFXTreeItem) and the items list is part of the latter
 * we add a listener to the list here so that when one or more items are added to the list and this item is expanded or
 * set to start expanded then we can add/remove the items from the container and adjust its height accordingly by firing
 * an ADD_REMOVE_ITEM_EVENT.
 * <p>
 * This separation though has a problem. If you add an item to a precise index the listener doesn't carry the index so
 * the only way to keep the desired order is to sort the container children list accordingly to the items list:
 * <pre>
 *     {@code FXCollections.sort(box.getChildren(), Comparator.comparingInt(item.getItems()::indexOf));}
 * </pre>
 * Also in the constructor we check if the {@link MFXTreeItem#startExpandedProperty()} is set to true.
 * In that case for avoiding issues with events and layout we set a special flag {@link #forcedUpdate} to true,
 * the whe add all the items to the container, apply css, ask to layout so the height updates, then we set
 * its prefHeight, update the cell, set the item expand state to true (that's why the expand property is for internal use only)
 * and after all that we reset the {@link #forcedUpdate} flag to false.
 */
public class MFXTreeItemSkin<T> extends SkinBase<MFXTreeItem<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final VBox box;
	//private final ContextMenu menu;
	private final AbstractMFXTreeCell<T> cell;
	private final ListChangeListener<AbstractMFXTreeItem<T>> itemsListener;

	private ParallelTransition animation;

	private boolean forcedUpdate = false;

	//================================================================================
	// Constructors
	//================================================================================
	@SuppressWarnings("SuspiciousMethodCalls")
	public MFXTreeItemSkin(MFXTreeItem<T> item) {
		super(item);

		cell = createCell();
		box = new VBox(cell);
		box.setMinHeight(Region.USE_PREF_SIZE);
		box.setMaxHeight(Region.USE_PREF_SIZE);

		item.setInitialHeight(NodeUtils.getRegionHeight(box));
		getChildren().add(box);
		box.setPrefHeight(item.getInitialHeight());

		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(box.widthProperty());
		clip.heightProperty().bind(box.heightProperty());
		box.setClip(clip);

/*        // TODO refactor, testing purpose
        menu = new ContextMenu();
        MenuItem mItemAdd = new MenuItem("ADD ITEM");
        MenuItem mItemRemove = new MenuItem("REMOVE ITEM");
        menu.getItems().addAll(mItemAdd, mItemRemove);
        item.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                menu.show(
                        item,
                        Side.RIGHT,
                        0, 0
                );
            }
        });
        mItemAdd.setOnAction(event -> item.getItems().add((AbstractMFXTreeItem<T>) new MFXTreeItem<>("DATA")));
        mItemRemove.setOnAction(event -> {
            if (item.getItemParent() != null) {
                item.getItemParent().getItems().remove(item);
            }
        });*/

		itemsListener = change -> {
			List<AbstractMFXTreeItem<T>> tmpRemoved = new ArrayList<>();
			List<AbstractMFXTreeItem<T>> tmpAdded = new ArrayList<>();

			while (change.next()) {
				tmpRemoved.addAll(change.getRemoved());
				tmpAdded.addAll(change.getAddedSubList());
			}

			if (!tmpRemoved.isEmpty() && (item.isExpanded() || item.isStartExpanded())) {
				double value = tmpRemoved.stream().mapToDouble(Region::getHeight).sum();
				box.getChildren().removeAll(tmpRemoved);
				item.fireEvent(new TreeItemEvent<>(TreeItemEvent.ADD_REMOVE_ITEM_EVENT, item, -value));
			}
			if (!tmpAdded.isEmpty() && (item.isExpanded() || item.isStartExpanded())) {
				double value = tmpAdded.stream().mapToDouble(NodeUtils::getRegionHeight).sum();
				box.getChildren().addAll(tmpAdded);
				FXCollections.sort(box.getChildren(), Comparator.comparingInt(item.getItems()::indexOf));
				item.fireEvent(new TreeItemEvent<>(TreeItemEvent.ADD_REMOVE_ITEM_EVENT, item, value));
			}
			cell.updateCell(item);
		};
		setListeners();

		if (item.isStartExpanded()) {
			forceUpdate();
		}
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds listeners to the following properties:
	 * <p>
	 * - the items observable list <p>
	 * - item's expanded property to call {@link #updateDisplay()} <p>
	 * </p>
	 * Adds handlers for the following events:
	 * <p>
	 * - ADD_REMOVE_ITEM_EVENT: when one or more items are added/removed we fire an event and adjust
	 * the container height accordingly. <p></p>
	 * <p>
	 * - EXPAND_EVENT: when the item is asked to expand itself we fire an event, build the expand animation, and
	 * if the event is on the item on which is fired build a fade in animation for each of its items.
	 * The event then "travels" up to the root and if the item's parent is null (so the item is the root) the event is consumed. <p></p>
	 * <p>
	 * - COLLAPSE_EVENT: when the item is asked to collapse itself we fire an event, build the expand animation, and
	 * if the event is on the item on which is fired build a fade out animation for each of its items. <p></p>
	 * <p>
	 * - HIDE_ROOT_EVENT: if the tree view is set to hide the root node then we need force update the root and expand it, hide its cell
	 * and reposition its children by setting its top and left padding. Otherwise the cell is set to be visible and the padding is reset.
	 * <p>
	 * - MOUSE_PRESSED: when the mouse is pressed on the item we check if the button was the primary button and if
	 * it was a double click. If that is the case than we fire a dummy MOUSE_PRESSED event on the cell's disclosure node because
	 * the behavior is to expand/collapse the item only if the mouse was pressed on the disclosure node.
	 * <p>
	 * If that is not the case then we trigger the selection, retrieve the selection model and select the item.
	 *
	 * @see TreeSelectionModel
	 */
	private void setListeners() {
		MFXTreeItem<T> item = getSkinnable();

		item.getItems().addListener(itemsListener);
		item.expandedProperty().addListener((observable, oldValue, newValue) -> {
			if (!forcedUpdate) {
				updateDisplay();
			}
		});

		item.addEventHandler(TreeItemEvent.ADD_REMOVE_ITEM_EVENT, addItemEvent -> {
			NodeUtils.addPrefHeight(box, addItemEvent.getValue());
			if (item.getItemParent() == null) {
				addItemEvent.consume();
			}
		});

		item.addEventHandler(TreeItemEvent.EXPAND_EVENT, expandEvent -> {
			buildAnimation((item.getHeight() + expandEvent.getValue()));
			if (expandEvent.getItem() == item) {
				item.getItems().forEach(treeItem -> animation.getChildren().add(MFXAnimationFactory.FADE_IN.build(treeItem, item.getAnimationDuration() * 2)));
			}
			animation.play();

			if (item.getItemParent() == null) {
				expandEvent.consume();
			}
		});

		item.addEventHandler(TreeItemEvent.COLLAPSE_EVENT, collapseEvent -> {
			buildAnimation((item.getHeight() - collapseEvent.getValue()));
			if (collapseEvent.getItem() == item) {
				item.getItems().forEach(treeItem -> animation.getChildren().add(MFXAnimationFactory.FADE_OUT.build(treeItem, item.getAnimationDuration() / 2)));
				animation.setOnFinished(event -> box.getChildren().subList(1, box.getChildren().size()).clear());
			}
			animation.play();
		});

		item.addEventHandler(TreeViewEvent.HIDE_ROOT_EVENT, hideRootEvent -> {
			if (!hideRootEvent.isShow()) {
				if (!item.isExpanded()) {
					forceUpdate();
				}
				cell.setVisible(false);
				item.setPadding(InsetsFactory.of(-(item.getInitialHeight() * 2), 0, 0, -item.getChildrenMargin()));
			} else {
				cell.setVisible(true);
				item.setPadding(Insets.EMPTY);
			}
		});

		item.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY &&
					event.getClickCount() == 2
			) {
				NodeUtils.fireDummyEvent(cell.getDisclosureNode());
				return;
			}

			if (!NodeUtils.inHierarchy(event, cell.getDisclosureNode())) {
				item.getSelectionModel().select(item, event);
			}
		});
	}

	/**
	 * This method is called when the item is about to expand/collapse.
	 * <p>
	 * If the item needs to be expanded then we add all the items to the container, apply css, ask to layout so the height updates,
	 * the we fire an EXPAND_EVENT on the item to handle the resize and the animation.
	 * <p>
	 * If the item needs to be collapsed then we fire a COLLAPSE_EVENT on the item to handle the resize and animation.
	 * Note that the items are not yet removed but they are removed at the end of the animation.
	 */
	protected void updateDisplay() {
		MFXTreeItem<T> item = getSkinnable();

		if (item.isExpanded()) {
			box.getChildren().addAll(item.getItems());
			box.applyCss();
			box.layout();
			item.fireEvent(new TreeItemEvent<>(TreeItemEvent.EXPAND_EVENT, item, computeExpandCollapse()));
		} else {
			item.fireEvent(new TreeItemEvent<>(TreeItemEvent.COLLAPSE_EVENT, item, computeExpandCollapse()));
		}
	}

	/**
	 * Build the expand/collapse animation setting the container prefHeight property to the fHeight parameter.
	 * Also build the rotate animation for the cell's disclosure node.
	 * <p>
	 * Last but not least so N.B! the item's {@link MFXTreeItem#animationRunningProperty()} is bound to this animation
	 * status property and used in {@link #animationIsRunning()} method.
	 *
	 * @param fHeight the final height of the container. The value is usually calculated by {@link #computeExpandCollapse()}
	 */
	protected void buildAnimation(double fHeight) {
		MFXTreeItem<T> item = getSkinnable();

		animation = (ParallelTransition) AnimationUtils.ParallelBuilder.build()
				.add(
						KeyFrames.of(item.getAnimationDuration(), box.prefHeightProperty(), fHeight, MFXAnimationFactory.INTERPOLATOR_V2),
						KeyFrames.of(250, cell.getDisclosureNode().rotateProperty(), (item.isExpanded() ? 90 : 0), MFXAnimationFactory.INTERPOLATOR_V2)
				).getAnimation();

		item.animationRunningProperty().bind(animation.statusProperty().isEqualTo(Animation.Status.RUNNING));
	}

	/**
	 * Check if the animation is running on the item or its parent up to the root.
	 * This is used in {@link #createCell()} when adding the event handler to the cell's disclosure node.
	 */
	protected boolean animationIsRunning() {
		MFXTreeItem<T> item = getSkinnable();
		List<MFXTreeItem<T>> tmp = new ArrayList<>();
		while (item != null) {
			tmp.add(item);
			item = (MFXTreeItem<T>) item.getItemParent();
		}

		for (MFXTreeItem<T> i : tmp) {
			if (i != null && i.isAnimationRunning()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is responsible for calculating the final height the item should have after
	 * an expand/collapse event. It's also used in the constructor in case the start expanded property is set to true.
	 *
	 * @return the computed height as the sum of all items height
	 */
	protected double computeExpandCollapse() {
		MFXTreeItem<T> item = getSkinnable();
		double value = item.getItems().stream().mapToDouble(AbstractMFXTreeItem::getHeight).sum();

		if (item.isRoot() && !forcedUpdate && !item.isExpanded()) {
			value = item.getHeight() - item.getInitialHeight();
		}
		return value;
	}

	/**
	 * Contains common code for forcing an item to expand.
	 */
	private void forceUpdate() {
		MFXTreeItem<T> item = getSkinnable();

		forcedUpdate = true;
		box.getChildren().addAll(item.getItems());
		box.applyCss();
		box.layout();
		box.setPrefHeight(item.getInitialHeight() + computeExpandCollapse());
		cell.updateCell(item);
		item.setExpanded(true);
		forcedUpdate = false;
	}

	/**
	 * This method is responsible for calling the MFXTreeItem's {@link MFXTreeItem#cellFactoryProperty()} thus creating the cell
	 * and adding an event handler for MOUSE_PRESSED on its disclosure node. If the items list is empty we consume the event and return.
	 * If the {@link #animationIsRunning()} method returns true we return too and the {@link MFXTreeItem#expandedProperty()} is not updated.
	 * So we avoid playing multiple animations at the same time because it could mess up the layout, also that's why it's recommended to
	 * not use too high values for {@link MFXTreeItem#animationDurationProperty()}.
	 * <p>
	 * At the end if all is ok, we update the {@link MFXTreeItem#expandedProperty()} thus calling updateDisplay and firing the proper events.
	 * <p></p>
	 * We also update the cell.
	 *
	 * @return the created cell
	 * @see io.github.palexdev.materialfx.controls.cell.MFXSimpleTreeCell
	 */
	protected AbstractMFXTreeCell<T> createCell() {
		MFXTreeItem<T> item = getSkinnable();

		AbstractMFXTreeCell<T> cell = item.getCellFactory().call(item);
		Node disclosureNode = cell.getDisclosureNode();
		disclosureNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (item.getItems().isEmpty()) {
				event.consume();
				return;
			}

			if (animationIsRunning()) {
				event.consume();
				return;
			}

			item.setExpanded(!item.isExpanded());
		});
		cell.updateCell(item);

		return cell;
	}

	@Override
	public void dispose() {
		if (getSkinnable() == null) return;
		getSkinnable().getItems().removeListener(itemsListener);

		if (animation != null) {
			animation.getChildren().clear();
			animation = null;
		}
		super.dispose();
	}
}

