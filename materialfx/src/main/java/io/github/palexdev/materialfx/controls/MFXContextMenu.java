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
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.skins.MFXContextMenuSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link MFXContextMenu} is a special case of {@link MFXPopup}.
 * The content is determined by its skin, and also has a userAgentStylesheet to define
 * the default style for all {@code MFXContextMenus}.
 * <p></p>
 * You can easily manage on what condition to show the popup and where to show it by changing these
 * two properties:
 * <p> - {@link #showConditionProperty()}: by default checks if the pressed mouse button was the SECONDARY button
 * <p> - {@link #showActionProperty()}: by default calls {@link MFXPopup#show(Node, double, double)} with the mouse screen coordinates
 * <p></p>
 * The new implementation doesn't allow to change the owner anymore, to keep things simple.
 * <p>
 * The new implementation allows adding generic separators, not only lines, {@link #addSeparator(Node)},
 * usually this is a Label to categorize the menu items.
 * The new implementation also allows to disable the popup. The disabled state is used by the default
 * {@link #showConditionProperty()}, so keep in mind that if you change that the disabled state
 * will be ignored (unless specified by your logic of course).
 * <p></p>
 * It is highly suggested using the {@link Builder} class to create a context menu.
 * <p></p>
 * <b>NOTE</b> that since the content of the context menu is entirely determined by its skin, the {@link #contentProperty()}
 * will always be null. As a result methods involving {@link Alignment}, {@link HPos} or {@link VPos} will fail with a
 * NullPointerException.
 */
public class MFXContextMenu extends MFXPopup {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-context-menu";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXContextMenu.css");

	private final ObservableList<Node> items = FXCollections.observableArrayList();
	private Node owner;
	private EventHandler<MouseEvent> ownerHandler;

	private boolean disabled = false;
	private final FunctionProperty<MouseEvent, Boolean> showCondition = new FunctionProperty<>(event -> !disabled && event.getButton() == MouseButton.SECONDARY);
	private final ConsumerProperty<MouseEvent> showAction = new ConsumerProperty<>(event -> show(owner, event.getScreenX(), event.getScreenY()));

	//================================================================================
	// Constructors
	//================================================================================
	public MFXContextMenu(Node owner) {
		if (owner == null) {
			throw new NullPointerException("Owner node cannot be null!");
		}

		this.owner = owner;
		ownerHandler = event -> {
			if (isShowing()) {
				hide();
				return;
			}

			if (getShowCondition().apply(event)) {
				getShowAction().accept(event);
			}
		};

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	/**
	 * Adds the needed handlers on the owner node.
	 */
	public void install() {
		owner.addEventFilter(MouseEvent.MOUSE_CLICKED, ownerHandler);
	}

	/**
	 * Removes any added handler from the owner node.
	 */
	public void uninstall() {
		owner.removeEventFilter(MouseEvent.MOUSE_CLICKED, ownerHandler);
	}

	/**
	 * Calls {@link #uninstall()} but also sets all the handlers and the owner
	 * node to null, making this context menu not usable anymore.
	 */
	public void dispose() {
		if (owner != null) {
			uninstall();
			ownerHandler = null;
			owner = null;
		}
	}

	/**
	 * Adds the giver menu item to the items list.
	 */
	public void addItem(MFXContextMenuItem item) {
		items.add(item);
	}

	/**
	 * Adds the given menu items to the items list.
	 */
	public void addItems(MFXContextMenuItem... items) {
		this.items.addAll(items);
	}

	/**
	 * Adds the given node which acts as a separator to the items list.
	 */
	public void addSeparator(Node separator) {
		separator.getStyleClass().add("separator");
		items.add(separator);
	}

	/**
	 * Adds the given line which acts as a separator to the items list.
	 * <p></p>
	 * It's suggested to use {@link Builder#getLineSeparator()} or {@link Builder#getLineSeparator(Insets)}
	 * to generate the Line.
	 */
	public void addLineSeparator(Line separator) {
		separator.getStyleClass().add("line-separator");
		items.add(separator);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXContextMenuSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return whether the context menu is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Enables/Disables the context menu.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the list containing the context menu's items
	 */
	public ObservableList<Node> getItems() {
		return items;
	}

	/**
	 * @return this context menu's owner
	 */
	public Node getOwner() {
		return owner;
	}

	public Function<MouseEvent, Boolean> getShowCondition() {
		return showCondition.get();
	}

	/**
	 * Specifies the function used to determine if a MouseEvent should trigger
	 * the {@link #showActionProperty()}.
	 * <p>
	 * By default, checks if the SECONDARY mouse button was pressed.
	 */
	public FunctionProperty<MouseEvent, Boolean> showConditionProperty() {
		return showCondition;
	}

	public void setShowCondition(Function<MouseEvent, Boolean> showCondition) {
		this.showCondition.set(showCondition);
	}

	public Consumer<MouseEvent> getShowAction() {
		return showAction.get();
	}

	/**
	 * Specifies the action to perform when a valid MouseEvent occurs.
	 * <p>
	 * By default, calls {@link #show(Node, double, double)} with the MouseEvent screen coordinates.
	 */
	public ConsumerProperty<MouseEvent> showActionProperty() {
		return showAction;
	}

	public void setShowAction(Consumer<MouseEvent> showAction) {
		this.showAction.set(showAction);
	}

	//================================================================================
	// Builder
	//================================================================================

	/**
	 * Builder class that facilitates the creation of context menus with fluent api.
	 * <p>
	 * An example:
	 * <p></p>
	 * <pre>
	 * {@code
	 *
	 * MFXContextMenuItem item1 = ...;
	 * MFXContextMenuItem item2 = ...;
	 * MFXContextMenuItem item3 = ...;
	 * MFXContextMenuItem item4 = ...;
	 * MFXContextMenuItem item5 = ...;
	 * MFXContextMenuItem item6 = ...;
	 *
	 * MFXContextMenu.Builder.build(owner)
	 *         .addItems(item1, item2)
	 *         .addLineSeparator()
	 *         .addItems(item3, item4)
	 *         .addLineSeparator()
	 *         .addItems(item5, item6)
	 *         .installAndGet();
	 * }
	 * </pre>
	 *
	 * @see MFXContextMenuItem
	 */
	public static class Builder {
		private final MFXContextMenu contextMenu;

		private Builder(Node owner) {
			contextMenu = new MFXContextMenu(owner);
		}

		/**
		 * @return a new Builder instance with the given owner for the MFXContextMenu
		 */
		public static Builder build(Node owner) {
			return new Builder(owner);
		}

		/**
		 * Adds the giver menu item to the items list.
		 */
		public Builder addItem(MFXContextMenuItem item) {
			contextMenu.addItem(item);
			return this;
		}

		/**
		 * Adds the given menu items to the items list.
		 */
		public Builder addItems(MFXContextMenuItem... items) {
			contextMenu.addItems(items);
			return this;
		}

		/**
		 * Sets the given action on the given item, then adds it to the items list.
		 */
		public Builder addItem(MFXContextMenuItem item, EventHandler<ActionEvent> action) {
			item.setOnAction(action);
			contextMenu.addItem(item);
			return this;
		}

		/**
		 * Adds the given node which acts as a separator to the items list.
		 */
		public Builder addSeparator(Node node) {
			contextMenu.addSeparator(node);
			return this;
		}

		/**
		 * Adds the given line which acts as a separator to the items list.
		 * <p>
		 * The line is generated using {@link #getLineSeparator()}.
		 */
		public Builder addLineSeparator() {
			contextMenu.addLineSeparator(getLineSeparator());
			return this;
		}

		/**
		 * Adds the given line which acts as a separator to the items list.
		 * <p>
		 * The line is generated using {@link #getLineSeparator(Insets)}.
		 */
		public Builder addLineSeparator(Insets insets) {
			contextMenu.addLineSeparator(getLineSeparator(insets));
			return this;
		}

		/**
		 * Sets the condition on which the given MouseEvent should trigger
		 * the {@link MFXContextMenu#showActionProperty()}.
		 */
		public Builder setShowCondition(Function<MouseEvent, Boolean> showCondition) {
			contextMenu.setShowCondition(showCondition);
			return this;
		}

		/**
		 * Sets the action to perform when a valid MouseEvent occurs.
		 *
		 * @see MFXContextMenu#showActionProperty()
		 */
		public Builder setShowAction(Consumer<MouseEvent> showAction) {
			contextMenu.setShowAction(showAction);
			return this;
		}

		/**
		 * Sets the node that has the necessary stylesheets to customize the popup.
		 */
		public Builder setPopupStyleableParent(Parent parent) {
			contextMenu.setPopupStyleableParent(parent);
			return this;
		}

		/**
		 * @return the built context menu
		 */
		public MFXContextMenu get() {
			return contextMenu;
		}

		/**
		 * Installs the built context menu then returns it.
		 */
		public MFXContextMenu installAndGet() {
			contextMenu.install();
			return contextMenu;
		}

		/**
		 * Calls {@link #getLineSeparator(Insets)} with top-bottom insets of 4.
		 */
		public static Line getLineSeparator() {
			return getLineSeparator(InsetsFactory.of(4, 0, 4, 0));
		}

		/**
		 * Builds a {@link Line} separator with startX of 0 and the given {@link Insets}, which are used
		 * as {@link VBox} margins, {@link VBox#setMargin(Node, Insets)}.
		 */
		public static Line getLineSeparator(Insets insets) {
			Line separator = new Line();
			separator.setStartX(0);
			VBox.setMargin(separator, InsetsFactory.of(4, 0, 4, 0));
			return separator;
		}
	}
}
