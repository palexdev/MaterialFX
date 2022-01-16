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

package io.github.palexdev.materialfx.beans;

import io.github.palexdev.materialfx.controls.MFXPopup;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;

/**
 * A useful bean which gives info about a {@link MFXPopup}'s position and owner.
 * <p></p>
 * The purpose of this bean is to provide a way to communicate between the popup and its skin.
 * The precise location of a popup cannot be computed when the show methods are called because the content
 * has not been laid out yet, thus its sizes/bounds are 0. This changes when the skin is created, at that moment
 * all info about the content are available so this bean is necessary to properly reposition and animate the popup.
 */
public class PopupPositionBean {
	//================================================================================
	// Properties
	//================================================================================
	private final Node owner;
	private final Bounds ownerBounds;
	private final PositionBean positionBean;
	private final Alignment alignment;
	private final double xOffset;
	private final double yOffset;

	//================================================================================
	// Constructors
	//================================================================================
	public PopupPositionBean(Node owner, PositionBean positionBean, Alignment alignment, double xOffset, double yOffset) {
		this.owner = owner;
		this.ownerBounds = owner.getLayoutBounds();
		this.positionBean = positionBean;
		this.alignment = alignment;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * @return the popup's owner
	 */
	public Node getOwner() {
		return owner;
	}

	/**
	 * @return the popup owner's bounds
	 */
	public Bounds getOwnerBounds() {
		return ownerBounds;
	}

	/**
	 * @return the popup owner's width
	 */
	public double getOwnerWidth() {
		return ownerBounds.getWidth();
	}

	/**
	 * @return the popup owner's height
	 */
	public double getOwnerHeight() {
		return ownerBounds.getHeight();
	}

	/**
	 * You should NOT rely on these coordinates since as of now
	 * they do not take into account the translations made by the skin.
	 *
	 * @return the initial computed coordinates of the popup
	 */
	public PositionBean getPositionBean() {
		return positionBean;
	}

	/**
	 * Delegate for {@link #getPositionBean()}.getX().
	 */
	public double getX() {
		return positionBean.getX();
	}

	/**
	 * Delegate for {@link #getPositionBean()}.setX().
	 */
	public void setX(double xPosition) {
		positionBean.setX(xPosition);
	}

	/**
	 * Delegate for {@link #getPositionBean()}.getY().
	 */
	public double getY() {
		return positionBean.getY();
	}

	/**
	 * Delegate for {@link #getPositionBean()}.setY().
	 */
	public void setY(double yPosition) {
		positionBean.setY(yPosition);
	}

	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @return the specified {@link HPos}
	 */
	public HPos getHPos() {
		return alignment.getHPos();
	}

	/**
	 * @return the specified {@link VPos}
	 */
	public VPos getVPos() {
		return alignment.getVPos();
	}

	/**
	 * @return the specified x offset
	 */
	public double getXOffset() {
		return xOffset;
	}

	/**
	 * @return the specified y offset
	 */
	public double getYOffset() {
		return yOffset;
	}
}
