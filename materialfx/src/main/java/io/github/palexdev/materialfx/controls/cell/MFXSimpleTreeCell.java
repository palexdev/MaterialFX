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

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeCell;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Simple implementation of a MFXTreeItem cell.
 * <p>
 * Concrete implementation of {@link AbstractMFXTreeCell<T>}.
 *
 * @param <T>
 */
public class MFXSimpleTreeCell<T> extends AbstractMFXTreeCell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-tree-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTreeCell.css");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXSimpleTreeCell(AbstractMFXTreeItem<T> item) {
		super(item);
		initialize();
	}

	public MFXSimpleTreeCell(AbstractMFXTreeItem<T> item, double fixedHeight) {
		super(item, fixedHeight);
		initialize();
	}

	//================================================================================
	// Override Methods
	//================================================================================

	/**
	 * Sets the cell style class,build and set the default disclosure node {@link MFXSimpleTreeCell#defaultDisclosureNode()}
	 * and adds a listener to the disclosure node property in case the user changes it.
	 */
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);

		defaultDisclosureNode();
		getChildren().add(0, getDisclosureNode());

		disclosureNode.addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				getChildren().set(0, (Node) newValue);
			}
		});
	}

	/**
	 * Specifies how to build the default disclosure node.
	 */
	@Override
	protected void defaultDisclosureNode() {
		MFXIconWrapper disclosureNode = new MFXIconWrapper().defaultRippleGeneratorBehavior();
		disclosureNode.getStyleClass().setAll("disclosure-node");
		disclosureNode.setSize(22);
		NodeUtils.makeRegionCircular(disclosureNode, 9.5);

		setDisclosureNode(disclosureNode);
	}

	/**
	 * {@inheritDoc}
	 * Overrides the return type of the super class according to {@link MFXSimpleTreeCell#defaultDisclosureNode()}
	 *
	 * @return this cell's disclosure node instance
	 */
	@Override
	public MFXIconWrapper getDisclosureNode() {
		return (MFXIconWrapper) disclosureNode.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <N extends Node> void setDisclosureNode(N node) {
		disclosureNode.set(node);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the data is a Node then it is added to the box.
	 * <p>
	 * If it is not a Node than a label is created, the label has style class: "data-label",
	 * sets its text to the calling toString on the data and then adds the label to the box.
	 *
	 * @param data the item's data
	 */
	@Override
	protected void render(T data) {
		if (data instanceof Node) {
			getChildren().add((Node) data);
		} else {
			Label label = new Label(data.toString());
			label.getStyleClass().add("data-label");
			getChildren().add(label);
		}
	}

	/**
	 * {@inheritDoc}
	 * Updates the cell when needed. When the items list changes adds or removes the disclosure node's
	 * icon accordingly. Also checks if the item has the {@link MFXTreeItem#startExpandedProperty()} set to true,
	 * in this case the disclosure node must be rotated by 90°.
	 */
	@Override
	public void updateCell(MFXTreeItem<T> item) {
		MFXIconWrapper disclosureNode = getDisclosureNode();

		if (!item.getItems().isEmpty()) {
			MFXFontIcon icon = new MFXFontIcon("mfx-chevron-right", 12.5);
			icon.getStyleClass().add("disclosure-icon");
			disclosureNode.setIcon(icon);
		} else {
			disclosureNode.removeIcon();
		}

		if (item.isStartExpanded()) {
			disclosureNode.setRotate(90);
		}
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
