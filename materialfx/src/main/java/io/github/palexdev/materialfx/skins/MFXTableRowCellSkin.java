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

import io.github.palexdev.materialfx.controls.BoundLabel;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 * Default skin implementation for {@link MFXTableRowCell}.
 * <p>
 * Simply an HBox which contains a label used to show the cell's text,
 * the leading and the trailing nodes specified by {@link MFXTableRowCell#leadingGraphicProperty()},
 * {@link MFXTableRowCell#trailingGraphicProperty()}
 */
public class MFXTableRowCellSkin<T, E> extends SkinBase<MFXTableRowCell<T, E>> {
	//================================================================================
	// Properties
	//================================================================================
	private final HBox container;
	private final BoundLabel label;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableRowCellSkin(MFXTableRowCell<T, E> rowCell) {
		super(rowCell);

		label = new BoundLabel(rowCell);

		container = new HBox(rowCell.getGraphicTextGap(), label);
		container.alignmentProperty().bind(rowCell.alignmentProperty());

		Node leading = rowCell.getLeadingGraphic();
		Node trailing = rowCell.getTrailingGraphic();

		if (leading != null) container.getChildren().add(0, leading);
		if (trailing != null) container.getChildren().add(trailing);

		clip();
		getChildren().setAll(container);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Specifies the behavior for the following changes/events:
	 * <p> - leading graphic
	 * <p> - trailing graphic
	 */
	private void addListeners() {
		MFXTableRowCell<T, E> rowCell = getSkinnable();

		rowCell.leadingGraphicProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) container.getChildren().remove(oldValue);
			if (newValue != null) container.getChildren().add(0, newValue);
		});
		rowCell.trailingGraphicProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) container.getChildren().remove(oldValue);
			if (newValue != null) container.getChildren().add(newValue);
		});
	}

	/**
	 * Responsible for clipping the cell.
	 * This is needed for nodes that are not text nodes, to also hide graphic.
	 */
	protected void clip() {
		MFXTableRowCell<T, E> rowCell = getSkinnable();
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(rowCell.widthProperty());
		clip.heightProperty().bind(rowCell.heightProperty());
		rowCell.setClip(clip);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXTableRowCell<T, E> rowCell = getSkinnable();
		double leading = rowCell.getLeadingGraphic() != null ? rowCell.getLeadingGraphic().prefWidth(-1) : 0;
		double trailing = rowCell.getTrailingGraphic() != null ? rowCell.getTrailingGraphic().prefWidth(-1) : 0;
		return Math.max(
				super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset),
				leftInset + leading + label.prefWidth(-1) + trailing + rightInset
		);
	}
}
