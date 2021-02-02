package io.github.palexdev.materialfx.utils;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Label;
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
}
