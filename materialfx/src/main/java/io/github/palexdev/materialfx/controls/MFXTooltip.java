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
    //================================================================================
    // Properties
    //================================================================================
    private double duration = 3600000;
    private final BooleanProperty isHoveringPrimary = new SimpleBooleanProperty(false);

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