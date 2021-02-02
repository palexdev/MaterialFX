package io.github.palexdev.materialfx.controls;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Workaround class to make JavaFX's Tooltips remain open as long as the mouse
 * in on the Tooltip's node.
 */
public class MFXTooltip extends Tooltip {
    private final BooleanProperty isHoveringPrimary = new SimpleBooleanProperty(false);
    //================================================================================
    // Properties
    //================================================================================
    private double duration = 3600000;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTooltip() {
        initialize();
    }

    public MFXTooltip(String text) {
        super(text);
        initialize();
    }

    public MFXTooltip(String text, Node node) {
        this(text);
        isHoveringTargetPrimary(node);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        isHoveringPrimary.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hide();
            }
        });
    }

    /**
     * Registers the MouseEntered and MouseExited handlers on the given node.
     *
     * @param node The Tooltip's node
     */
    public void isHoveringTargetPrimary(Node node) {
        node.setOnMouseEntered(e -> isHoveringPrimary.set(true));
        node.setOnMouseExited(e -> isHoveringPrimary.set(false));
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public BooleanProperty isHoveringPrimaryProperty() {
        return isHoveringPrimary;
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Override of Tooltip's hide method. If the mouse is on the Tooltip's node
     * then a {@code PauseTransition} with a very long duration is started,
     * on finish it hides the Tooltip. As soon as the mouse exit the node
     * the Tooltip is being hidden.
     */
    @Override
    public void hide() {
        if (isHoveringPrimary.get()) {
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(duration));
            pauseTransition.setOnFinished(event -> super.hide());
            pauseTransition.play();
        } else {
            super.hide();
        }
    }
}