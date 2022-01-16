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
import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.skins.MFXContextMenuItemSkin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;

import java.util.function.Supplier;

/**
 * Implementation of a menu item for {@link MFXContextMenu}.
 * <p>
 * Extends {@link Labeled} and offers a new custom skin.
 * <p></p>
 * This control has three elements:
 * <p> - The icon, specified by the {@link #graphicProperty()}, it is wrapped in a {@link MFXIconWrapper} with a default
 * fixed size of 24 to ensure that all items are perfectly aligned. That means that setting big nodes <b>WON'T WORK</b>
 * and after all as a design principle it's not recommended (which means, just don't do it), can be changed via CSS.
 * <p> - The label showing the item's text
 * <p> - Another label showing the specifies {@link #acceleratorProperty()}. Note that the accelerator is just
 * a String. No handlers will be added to the control. I think this is the best solution since handlers may also have some complex logic defined
 * by the user.
 * <p></p>
 * Allows to easily specify a {@link Tooltip} with a supplier property, {@link #tooltipSupplierProperty()},
 * and an action with {@link #onActionProperty()}.
 * <p></p>
 * A {@link Builder} class is offered to easily build items with fluent api.
 */
public class MFXContextMenuItem extends Labeled {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-menu-item";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXContextMenuItem.css");

	private final StringProperty accelerator = new SimpleStringProperty();
	private final SupplierProperty<Tooltip> tooltipSupplier = new SupplierProperty<>();
	private final EventHandlerProperty<ActionEvent> onAction = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, get());
		}
	};

	//================================================================================
	// Constructors
	//================================================================================
	public MFXContextMenuItem() {
		this("");
	}

	public MFXContextMenuItem(String text) {
		this(text, null);
	}

	public MFXContextMenuItem(String text, Node graphic) {
		super(text, graphic);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXContextMenuItemSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String getAccelerator() {
		return accelerator.get();
	}

	/**
	 * Specifies the accelerator's text.
	 */
	public StringProperty acceleratorProperty() {
		return accelerator;
	}

	public void setAccelerator(String accelerator) {
		this.accelerator.set(accelerator);
	}

	public void setAccelerator(KeyCombination combination) {
		setAccelerator(combination.getDisplayText());
	}

	public Supplier<Tooltip> getTooltipSupplier() {
		return tooltipSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to build the item's tooltip.
	 */
	public SupplierProperty<Tooltip> tooltipSupplierProperty() {
		return tooltipSupplier;
	}

	public void setTooltipSupplier(Supplier<Tooltip> tooltipSupplier) {
		this.tooltipSupplier.set(tooltipSupplier);
	}

	public EventHandler<ActionEvent> getOnAction() {
		return onAction.get();
	}

	/**
	 * Specifies the action to perform when clicked.
	 */
	public EventHandlerProperty<ActionEvent> onActionProperty() {
		return onAction;
	}

	public void setOnAction(EventHandler<ActionEvent> onAction) {
		this.onAction.set(onAction);
	}

	//================================================================================
	// Builder
	//================================================================================

	/**
	 * Builder class that facilitates the creation of items with fluent api.
	 * <p>
	 * An example:
	 * <p></p>
	 * <pre>
	 * {@code
	 * MFXContextMenuItem item = MFXContextMenuItem.Builder.build()
	 *         .setIcon(icon)
	 *         .setText("Menu Item 1)
	 *         .setAccelerator("Ctrl + P)
	 *         .setTooltipSupplier(() -> new Tooltip("Performs action P"))
	 *         .setAction(event -> actionP())
	 *         .get();
	 * }
	 * </pre>
	 */
	public static class Builder {
		private final MFXContextMenuItem item;

		public Builder() {
			this.item = new MFXContextMenuItem();
		}

		/**
		 * @return a new Builder instance
		 */
		public static Builder build() {
			return new Builder();
		}

		/**
		 * Sets the item's icon.
		 */
		public Builder setIcon(Node icon) {
			item.setGraphic(icon);
			return this;
		}

		/**
		 * Sets the item's text.
		 */
		public Builder setText(String text) {
			item.setText(text);
			return this;
		}

		/**
		 * Sets the item's accelerator text.
		 */
		public Builder setAccelerator(String accelerator) {
			item.setAccelerator(accelerator);
			return this;
		}

		/**
		 * Sets the item's accelerator text from the given {@link KeyCombination}.
		 */
		public Builder setAccelerator(KeyCombination combination) {
			item.setAccelerator(combination);
			return this;
		}

		/**
		 * Sets the item's tooltip supplier.
		 */
		public Builder setTooltipSupplier(Supplier<Tooltip> tooltipSupplier) {
			item.setTooltipSupplier(tooltipSupplier);
			return this;
		}

		/**
		 * Sets the item's action to perform on click.
		 */
		public Builder setOnAction(EventHandler<ActionEvent> onAction) {
			item.setOnAction(onAction);
			return this;
		}

		/**
		 * @return the built item
		 */
		public MFXContextMenuItem get() {
			return item;
		}
	}
}
