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

package io.github.palexdev.materialfx.skins.base;

import io.github.palexdev.materialfx.controls.BoundLabel;
import io.github.palexdev.materialfx.controls.base.MFXLabeled;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Common skin for all controls that implement both {@link Labeled} and {@link MFXLabeled}.
 */
public abstract class MFXLabeledSkinBase<C extends Labeled & MFXLabeled> extends SkinBase<C> {
	//================================================================================
	// Properties
	//================================================================================
	protected final BorderPane topContainer;
	protected final BoundLabel text;

	//================================================================================
	// Constructors
	//================================================================================
	protected MFXLabeledSkinBase(C labeled) {
		super(labeled);
		topContainer = new BorderPane();
		text = new BoundLabel(labeled);
		if (labeled.isTextExpand()) text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * To use this skin, controls must be organized in a certain way.
	 * <p>
	 * Two main nodes are needed: one is the text of course, the other one is a pane which contains all the
	 * other nodes. This is necessary for the {@link MFXLabeled#contentDispositionProperty()} to work properly.
	 * This base skin defines a top container which is a {@link BorderPane}. At the center of the border pane there's always the
	 * text (exception being when content disposition is CENTER or GRAPHIC_ONLY),
	 * the other pane is the one that rotates depending on the content disposition property.
	 */
	protected abstract Pane getControlContainer();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds some common listeners for all skins extending this.
	 * <p>
	 * Note that this method is not called by the constructor of this class,
	 * this is to allow overriding and thus expand the behavior of this method.
	 * This should be called at the end of the constructor of the implementing skin.
	 */
	protected void addListeners() {
		C labeled = getSkinnable();
		labeled.alignmentProperty().addListener(invalidated -> updateAlignment());
		labeled.contentDispositionProperty().addListener(invalidated -> initContainer());
		labeled.gapProperty().addListener(invalidated -> initContainer());
		labeled.textExpandProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			} else {
				text.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
			}
		});
	}

	/**
	 * This is responsible for managing the {@link BorderPane} when the {@link MFXLabeled#contentDispositionProperty()}
	 * and the {@link MFXLabeled#gapProperty()} change.
	 * <p>
	 * Note that the gap is set by using {@link BorderPane#setMargin(Node, Insets)} on the text node.
	 */
	protected void initContainer() {
		C labeled = getSkinnable();
		Pane controlContainer = getControlContainer();
		ContentDisplay disposition = labeled.getContentDisposition();
		double gap = labeled.getGap();

		topContainer.getChildren().clear();
		topContainer.setCenter(text);
		switch (disposition) {
			case TOP: {
				topContainer.setTop(controlContainer);
				BorderPane.setMargin(text, InsetsFactory.top(gap));
				break;
			}
			case RIGHT: {
				topContainer.setRight(controlContainer);
				BorderPane.setMargin(text, InsetsFactory.right(gap));
				break;
			}
			case BOTTOM: {
				topContainer.setBottom(controlContainer);
				BorderPane.setMargin(text, InsetsFactory.bottom(gap));
				break;
			}
			case TEXT_ONLY:
			case LEFT: {
				topContainer.setLeft(controlContainer);
				BorderPane.setMargin(text, InsetsFactory.left(gap));
				break;
			}
			case GRAPHIC_ONLY:
			case CENTER: {
				topContainer.setCenter(controlContainer);
				BorderPane.setMargin(text, InsetsFactory.none());
				break;
			}
		}
	}

	/**
	 * This method ensures that the control's pane is always correctly aligned with the text.
	 */
	protected void updateAlignment() {
		C labeled = getSkinnable();
		Pane controlContainer = getControlContainer();
		Pos alignment = labeled.getAlignment();

		if (PositionUtils.isTop(alignment)) {
			BorderPane.setAlignment(controlContainer, Pos.TOP_CENTER);
		} else if (PositionUtils.isCenter(alignment)) {
			BorderPane.setAlignment(controlContainer, Pos.CENTER);
		} else if (PositionUtils.isBottom(alignment)) {
			BorderPane.setAlignment(controlContainer, Pos.BOTTOM_CENTER);
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		C labeled = getSkinnable();
		ContentDisplay contentDisposition = labeled.getContentDisposition();

		double minW = 0;
		switch (contentDisposition) {
			case TEXT_ONLY:
			case LEFT:
			case RIGHT: {
				minW = getControlContainer().prefWidth(-1) + labeled.getGap() + text.prefWidth(-1);
				break;
			}
			case TOP:
			case BOTTOM: {
				minW = Math.max(getControlContainer().prefWidth(-1), text.prefWidth(-1));
				break;
			}
			case CENTER:
			case GRAPHIC_ONLY: {
				minW = getControlContainer().prefWidth(-1);
			}
		}
		return leftInset + minW + rightInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
