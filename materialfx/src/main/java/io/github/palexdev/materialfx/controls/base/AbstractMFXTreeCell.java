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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXTreeItem;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

// TODO implement StringConverter (low priority)

/**
 * Base class for every cell used by {@code MFXTreeItems}.
 * Specifies properties and methods that should be common to all cells.
 * <p>
 * It's common for TreeViews to display the data in an horizontal container and that's why
 * this class extends HBox rather than Control and define its skin.
 * <p>
 * To build a cell the item data should be sufficient however the constructors have been
 * refactored to pass th AbstractMFXTreeItem as parameter because to implement cell selection
 * it is needed to bound the selected property of the cell to the item selected property.
 * It's kinda tricky because one needs to select the item but if you select the item and update its background
 * accordingly you set the background color of the entire container not the item's cell. So we
 * select the item in the model but in the view the cell appears selected.
 * The same concept applies to {@code MFXCheckTreeCells}.
 * <p>
 * Also, note that to build a cell the height must be fixed for layout reasons, by default it's 27.
 * When the cell is created the {@link #render(Object)} method is called.
 *
 * @param <T> The type of the data within TreeItem.
 */
public abstract class AbstractMFXTreeCell<T> extends HBox {
	//================================================================================
	// Properties
	//================================================================================
	protected final ObjectProperty<? super Node> disclosureNode = new SimpleObjectProperty<>();
	private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

	private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
	private final BooleanProperty selected = new SimpleBooleanProperty(false);

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXTreeCell(AbstractMFXTreeItem<T> item) {
		this(item, 27);
	}

	public AbstractMFXTreeCell(AbstractMFXTreeItem<T> item, double fixedHeight) {
		this.fixedCellSize.set(fixedHeight);

		setMinHeight(USE_PREF_SIZE);
		setMaxHeight(USE_PREF_SIZE);
		prefHeightProperty().bind(fixedCellSize);

		initialize(item);
		render(item.getData());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the alignment to CENTER_LEFT, the spacing to 5,
	 * adds the needed listeners and binds the {@link #selectedProperty()} to the item's
	 * {@link MFXTreeItem#selectedProperty()}
	 */
	private void initialize(AbstractMFXTreeItem<T> item) {
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(5);

		addListeners();
		selected.bind(item.selectedProperty());
	}

	/**
	 * Adds a listener to the selected property to change the PseudoClass state.
	 */
	private void addListeners() {
		selected.addListener(invalidate -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
	}

	public double getFixedCellSize() {
		return fixedCellSize.get();
	}

	public DoubleProperty fixedCellSizeProperty() {
		return fixedCellSize;
	}

	public void setFixedCellSize(double fixedCellSize) {
		this.fixedCellSize.set(fixedCellSize);
	}

	public boolean isSelected() {
		return selected.get();
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Each cell must have a disclosure node (the node to expand/collapse the item), therefore
	 * it needs to specify how to create it.
	 */
	protected abstract void defaultDisclosureNode();

	/**
	 * @return the cell's disclosure node instance
	 */
	public abstract Node getDisclosureNode();

	/**
	 * Sets the cell's disclosure node to the specified node.
	 *
	 * @param <N> the specified parameter N should be a subclass of Node
	 */
	public abstract <N extends Node> void setDisclosureNode(N node);

	/**
	 * Specifies how the cell should represent the item's data, whether it is a node,
	 * a primitive type or something else.
	 *
	 * @param data the item's data
	 */
	protected abstract void render(T data);

	/**
	 * This methods is needed for updating the cell when the item state changes.
	 * For example the disclosure node is added to all cells and should have the same size in each
	 * cell for layout reasons (think at how the HBox works and what would happen if you don't have the
	 * disclosure node) but by default it has not the icon because it is added only when the items list
	 * is not empty, when it changes from empty to full or vice versa the icon is added/removed.
	 */
	public abstract void updateCell(MFXTreeItem<T> item);
}
