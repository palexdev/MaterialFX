package io.github.palexdev.materialfx.utils;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

/**
 * Utils class for {@code ToggleButton}s.
 */
public class ToggleButtonsUtil {

    private static final EventHandler<MouseEvent> consumeMouseEventFilter = (MouseEvent mouseEvent) -> {
        if (((Toggle) mouseEvent.getSource()).isSelected()) {
            mouseEvent.consume();
        }
    };

    private static void addConsumeMouseEventFilter(Toggle toggle) {
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_PRESSED, consumeMouseEventFilter);
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_RELEASED, consumeMouseEventFilter);
        ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_CLICKED, consumeMouseEventFilter);
    }

    /**
     * Adds a handler to the given {@code ToggleGroup} to make sure there's always at least
     * one {@code ToggleButton} selected.
     *
     * @param toggleGroup The given ToggleGroup
     */
    public static void addAlwaysOneSelectedSupport(final ToggleGroup toggleGroup) {
        toggleGroup.getToggles().addListener((ListChangeListener.Change<? extends Toggle> c) -> {
            while (c.next()) {
                for (final Toggle addedToggle : c.getAddedSubList()) {
                    addConsumeMouseEventFilter(addedToggle);
                }
            }
        });
        toggleGroup.getToggles().forEach(ToggleButtonsUtil::addConsumeMouseEventFilter);
    }
}
