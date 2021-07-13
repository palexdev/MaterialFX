/*
 * Copyright (C) 2021 Parisi Alessandro
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

import io.github.palexdev.materialfx.controls.MFXLabel;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Utils class for JavaFX's {@code Labels} and {@code MFXLabels}.
 */
public class LabelUtils {

    private LabelUtils() {
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

        Group group = new Group(helper);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();
        return helper.getLayoutBounds().getWidth();
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

        Group group = new Group(helper);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();
        return helper.getLayoutBounds().getHeight();
    }

    /**
     * Computes the min width for the specified {@link MFXLabel} so that all the text is visible.
     * <p>
     * Uses {@link #computeTextWidth(Font, String)}, but also takes into account the label's
     * icons bounds (if not null), the {@link MFXLabel#graphicTextGapProperty()} multiplied by 2,
     * and the container's padding {@link MFXLabel#containerPaddingProperty()}.
     * <p></p>
     * Note: this works only after the label has been laid out.
     */
    public static double computeMFXLabelWidth(MFXLabel label) {
        Node leading = label.getLeadingIcon();
        Node trailing = label.getTrailingIcon();
        return label.snappedLeftInset() +
                (leading != null ? leading.getBoundsInParent().getWidth() : 0) +
                LabelUtils.computeTextWidth(label.getFont(), (label.getText().isEmpty() ? label.getPromptText() : label.getText())) +
                (trailing != null ? trailing.getBoundsInParent().getWidth() : 0) +
                label.snappedRightInset() +
                (2 * label.getGraphicTextGap()) +
                label.getContainerPadding().getLeft() + label.getContainerPadding().getLeft();
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
                LabelUtils.computeTextWidth(label.getFont(), label.getText()) +
                label.snappedRightInset() +
                label.getGraphicTextGap();
    }
}
