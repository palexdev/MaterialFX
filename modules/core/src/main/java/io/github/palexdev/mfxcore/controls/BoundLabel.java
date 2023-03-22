/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.controls;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.LabelSkin;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * This is a special Label which has all its main properties bound to
 * another {@link Labeled} control. This is especially useful for custom
 * controls and skins that have text, as the text properties are set on the control
 * and not on the text node itself, and that's why all properties are bound.
 * <p></p>
 * This also adds a new feature/workaround. In JavaFX Labels are composed of two nodes at max: the icon/graphic and the
 * text. For performance reasons probably the text node is not added to the control until the text is not null and not empty.
 * A mechanism to detect the addition and retrieval of such node has been added, allowing custom text based controls to
 * take full control on the text node itself rather than the label as a whole.
 */
public class BoundLabel extends Label {
	//================================================================================
	// Properties
	//================================================================================
	private Node textNode;
	private Consumer<Node> onSetTextNode = null;

	//================================================================================
	// Constructors
	//================================================================================
	public BoundLabel(Labeled labeled) {
		super();

		// Init
		setText(labeled.getText());
		setFont(labeled.getFont());
		setTextFill(labeled.getTextFill());
		setWrapText(labeled.isWrapText());
		setTextAlignment(labeled.getTextAlignment());
		setTextOverrun(labeled.getTextOverrun());
		setEllipsisString(labeled.getEllipsisString());
		setUnderline(labeled.isUnderline());
		setLineSpacing(labeled.getLineSpacing());
		setGraphicTextGap(labeled.getGraphicTextGap());
		setContentDisplay(labeled.getContentDisplay());
		setGraphic(labeled.getGraphic());
		setAlignment(labeled.getAlignment());

		// Bindings
		textProperty().bind(labeled.textProperty());
		fontProperty().bind(labeled.fontProperty());
		textFillProperty().bind(labeled.textFillProperty());
		wrapTextProperty().bind(labeled.wrapTextProperty());
		textAlignmentProperty().bind(labeled.textAlignmentProperty());
		textOverrunProperty().bind(labeled.textOverrunProperty());
		ellipsisStringProperty().bind(labeled.ellipsisStringProperty());
		underlineProperty().bind(labeled.underlineProperty());
		lineSpacingProperty().bind(labeled.lineSpacingProperty());
		graphicTextGapProperty().bind(labeled.graphicTextGapProperty());
		contentDisplayProperty().bind(labeled.contentDisplayProperty());
		graphicProperty().bind(labeled.graphicProperty());
		alignmentProperty().bind(labeled.alignmentProperty());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Null-safe getter for retrieving the instance of the text node for this label.
	 */
	public Optional<Node> getTextNode() {
		return Optional.ofNullable(textNode);
	}

	/**
	 * Responsible for setting the text node instance as well as running the user specified callback,
	 * {@link #onSetTextNode(Consumer)}.
	 */
	private void setTextNode(Node textNode) {
		this.textNode = textNode;
		if (onSetTextNode != null)
			onSetTextNode.accept(textNode);
	}

	/**
	 * Sets the callback that executes when the text node is detected and stored.
	 */
	public void onSetTextNode(Consumer<Node> action) {
		this.onSetTextNode = action;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to detect changes to the children list. This way we can check when the text node is being added
	 * and store its reference. A callback can be specified through {@link #setTextNode(Node)} allowing actions to be
	 * performed as soon as the node is available.
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new LabelSkin(this) {
			@Override
			protected void updateChildren() {
				super.updateChildren();
				if (textNode != null) return;

				if (getChildren().size() == 1 && getGraphic() == null) {
					setTextNode(getChildren().get(0));
				} else if (getChildren().size() > 1) {
					setTextNode(getChildren().get(1));
				}
			}
		};
	}
}
