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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.SizeBean;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Utils class for JavaFX's {@code Labels} and {@code MFXLabels}.
 */
public class TextUtils {

	private TextUtils() {
	}

	/**
	 * Checks if the text of the specified {@code Label} is truncated.
	 *
	 * @param label The specified label
	 */
	public static boolean isLabelTruncated(Label label) {
		String originalString = label.getText();
		Text textNode = (Text) label.lookup(".text");
		if (textNode != null) {
			String actualString = textNode.getText();
			return (!actualString.isEmpty() && !originalString.equals(actualString));
		}
		return false;
	}

	/**
	 * Registers a listener to the specified {@code Label} which checks if the text
	 * is truncated and updates the specified boolean property accordingly.
	 *
	 * @param isTruncated The boolean property to change
	 * @param label       The specified label
	 */
	public static void registerTruncatedLabelListener(BooleanProperty isTruncated, Label label) {
		label.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
			String originalString = label.getText();
			Text textNode = (Text) label.lookup(".text");
			String actualString = textNode.getText();

			isTruncated.set(!actualString.isEmpty() && !originalString.equals(actualString));
		});
	}

	/**
	 * Computes the min width of a text node so that all the text is visible. Uses {@link NodeUtils#getRegionWidth(Region)}.
	 * <p>
	 * Uses {@link Label} as helper.
	 *
	 * @param font the label font
	 * @param text the label text
	 */
	public static double computeLabelWidth(Font font, String text) {
		Label helper = new Label(text);
		helper.setMaxWidth(Double.MAX_VALUE);
		helper.setFont(font);

		return NodeUtils.getRegionWidth(helper);
	}

	/**
	 * Computes the min height of a text node.
	 * <p>
	 * Uses {@link Label} as helper.
	 *
	 * @param font the node font
	 * @param text the node text
	 */
	public static double computeLabelHeight(Font font, String text) {
		Label helper = new Label(text);
		helper.setMaxWidth(Double.MAX_VALUE);
		helper.setFont(font);
		return NodeUtils.getRegionHeight(helper);
	}

	/**
	 * Computes both the width and the height of a {@link Label}
	 * for the given font and text.
	 *
	 * @return the bean containing the computed values
	 */
	public static SizeBean computeLabelSizes(Font font, String text) {
		Label helper = new Label(text);
		helper.setMaxWidth(Double.MAX_VALUE);
		helper.setFont(font);
		return NodeUtils.getNodeSizes(helper);
	}

	/**
	 * Computes the min width of a text node so that all the text is visible.
	 * <p>
	 * Uses {@link Text} as helper.
	 *
	 * @param font the node font
	 * @param text the node text
	 */
	public static double computeTextWidth(Font font, String text) {
		Text helper = new Text(text);
		helper.setFont(font);
		return NodeUtils.getNodeWidth(helper);
	}

	/**
	 * Computes the min height of a text node.
	 * <p>
	 * Uses {@link Text} as helper.
	 *
	 * @param font the node font
	 * @param text the node text
	 */
	public static double computeTextHeight(Font font, String text) {
		Text helper = new Text(text);
		helper.setFont(font);
		return NodeUtils.getNodeHeight(helper);
	}

	/**
	 * Computes the min width for the specified {@link Label} so that all the text is visible.
	 * <p>
	 * Uses {@link #computeTextWidth(Font, String)}, but also takes into account the label's
	 * graphic bounds (if not null) and the {@link Label#graphicTextGapProperty()}.
	 * <p></p>
	 * Note: this works only after the label has been laid out.
	 */
	public static double computeLabelWidth(Label label) {
		Node graphic = label.getGraphic();
		return label.snappedLeftInset() +
				(graphic != null ? graphic.getBoundsInParent().getWidth() : 0) +
				TextUtils.computeTextWidth(label.getFont(), label.getText()) +
				label.snappedRightInset() +
				label.getGraphicTextGap();
	}
}
