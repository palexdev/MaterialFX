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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public abstract class AbstractMFXToggleNode extends ToggleButton {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-toggle-node";

	private final ObjectProperty<Node> labelLeadingIcon = new SimpleObjectProperty<>();
	private final ObjectProperty<Node> labelTrailingIcon = new SimpleObjectProperty<>();

	//================================================================================
	// Constructors
	//================================================================================
	public AbstractMFXToggleNode() {
		initialize();
	}

	public AbstractMFXToggleNode(String text) {
		super(text);
		initialize();
	}

	public AbstractMFXToggleNode(String text, Node graphic) {
		super(text, graphic);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	public Node getLabelLeadingIcon() {
		return labelLeadingIcon.get();
	}

	/**
	 * Specifies the label's leading icon.
	 */
	public ObjectProperty<Node> labelLeadingIconProperty() {
		return labelLeadingIcon;
	}

	public void setLabelLeadingIcon(Node labelLeadingIcon) {
		this.labelLeadingIcon.set(labelLeadingIcon);
	}

	public Node getLabelTrailingIcon() {
		return labelTrailingIcon.get();
	}

	/**
	 * Specifies the label's trailing icon.
	 */
	public ObjectProperty<Node> labelTrailingIconProperty() {
		return labelTrailingIcon;
	}

	public void setLabelTrailingIcon(Node labelTrailingIcon) {
		this.labelTrailingIcon.set(labelTrailingIcon);
	}
}
