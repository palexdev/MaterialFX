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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXPopup;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Scale;

import java.util.function.BiFunction;

public class PopupBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXPopup popup;

	//================================================================================
	// Constructors
	//================================================================================
	public PopupBuilder() {
		this(new MFXPopup());
	}

	public PopupBuilder(MFXPopup popup) {
		this.popup = popup;
	}

	public static PopupBuilder popup() {
		return new PopupBuilder();
	}

	public static PopupBuilder popup(MFXPopup popup) {
		return new PopupBuilder(popup);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public PopupBuilder show(Node ownerNode, double anchorX, double anchorY) {
		popup.show(ownerNode, anchorX, anchorY);
		return this;
	}

	public PopupBuilder show(Node node) {
		popup.show(node);
		return this;
	}

	public PopupBuilder show(Node node, Alignment alignment) {
		popup.show(node, alignment);
		return this;
	}

	public PopupBuilder show(Node node, Alignment alignment, double xOffset, double yOffset) {
		popup.show(node, alignment, xOffset, yOffset);
		return this;
	}

	public PopupBuilder setPopupStyleableParent(Parent parent) {
		popup.setPopupStyleableParent(parent);
		return this;
	}

	public PopupBuilder addStylesheet(String... stylesheets) {
		popup.getStyleSheets().addAll(stylesheets);
		return this;
	}

	public PopupBuilder setStylesheet(String... stylesheets) {
		popup.getStyleSheets().setAll(stylesheets);
		return this;
	}

	public PopupBuilder setContent(Node content) {
		popup.setContent(content);
		return this;
	}

	public PopupBuilder setAnimationProvider(BiFunction<Node, Scale, Animation> animationProvider) {
		popup.setAnimationProvider(animationProvider);
		return this;
	}

	public PopupBuilder setAnimated(boolean animated) {
		popup.setAnimated(animated);
		return this;
	}
}
