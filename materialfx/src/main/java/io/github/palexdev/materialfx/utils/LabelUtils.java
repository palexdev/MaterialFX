/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utils class for JavaFX's {@code Label}s.
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
        AtomicBoolean isTruncated = new AtomicBoolean(false);

        label.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            String originalString = label.getText();
            Text textNode = (Text) label.lookup(".text");
            String actualString = textNode.getText();
            isTruncated.set(!actualString.isEmpty() && !originalString.equals(actualString));
        });
        return isTruncated.get();
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
     * Computes the min width of a label to show all the text. Uses {@link NodeUtils#getNodeWidth(Region)}.
     *
     * @param font the label font
     * @param text the label text
     */
    public static double computeTextWidth(Font font, String text) {
        Label helper = new Label(text);
        helper.setMaxWidth(Double.MAX_VALUE);
        helper.setFont(font);

        return NodeUtils.getNodeWidth(helper);
    }
}
