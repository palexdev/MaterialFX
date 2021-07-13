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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * This is the implementation of a popup notification in JavaFX.
 * <p>
 * Extends {@code Popup}, provides animations for showing and closing and allows to
 * close automatically the notification after some specified time.
 */
public class MFXNotification extends Popup {
    //================================================================================
    // Properties
    //================================================================================
    private Region content;
    private boolean animate = false;
    private final BooleanProperty hideAfter = new SimpleBooleanProperty(false);

    private Timeline inAnimation;
    private Timeline outAnimation;
    private PauseTransition hideAfterTransition;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXNotification(Region content) {
        setAutoFix(false);
        this.content = content;
        this.getContent().add(content);
        initialize();
    }

    public MFXNotification(Region content, boolean animate) {
        this(content);
        this.animate = animate;
    }

    public MFXNotification(Region content, boolean animate, boolean hideAfter) {
        this(content, animate);
        this.hideAfter.set(hideAfter);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        this.inAnimation = MFXAnimationFactory.FADE_IN.build(content, 600);
        this.outAnimation = MFXAnimationFactory.FADE_OUT.build(content, 600);
        this.hideAfterTransition = new PauseTransition(Duration.seconds(4));

        this.hideAfter.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                addMouseHandlers();
            } else {
                removeMouseHandlers();
            }
        });
    }

    /**
     * Closes the notification, plays out animation if requested.
     * <p>
     * <b>Note: this method should be used rather than Popup's hide() method</b>
     */
    public void hideNotification() {
        if (animate) {
            outAnimation.setOnFinished(event -> super.hide());
            outAnimation.play();
        } else {
            super.hide();
        }
    }

    /**
     * If the notification is set to hide automatically this method is called.
     * <p>
     * Adds MouseEntered and MouseExited handlers to stop/restart the
     * close countdown when mouse is on/off the notification content.
     */
    private void addMouseHandlers() {
        this.content.setOnMouseEntered(event -> {
            outAnimation.stop();
            hideAfterTransition.stop();
            this.content.setOpacity(1.0);
        });
        this.content.setOnMouseExited(event -> {
            if (hideAfterTransition.getStatus().equals(Animation.Status.STOPPED)) {
                hideAfterTransition.playFromStart();
            }
        });
    }

    /**
     * If the notification is set to not hide automatically this method is called.
     * Removes the handlers for MouseEntered and MouseExited
     */
    private void removeMouseHandlers() {
        this.content.setOnMouseEntered(null);
        this.content.setOnMouseExited(null);
    }

    /**
     * Returns the notification's content
     */
    public Region getNotificationContent() {
        return content;
    }

    /**
     * Sets the notification's content and re-initializes the object.
     */
    public void setContent(Region content) {
        this.content = content;
        this.getContent().add(content);
        initialize();
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void setHideAfter(boolean hideAfter) {
        this.hideAfter.set(hideAfter);
    }

    public void setHideAfterDuration(Duration hideAfterDuration) {
        this.hideAfterTransition.setDuration(hideAfterDuration);
    }

    public void setInAnimation(Timeline inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Timeline outAnimation) {
        this.outAnimation = outAnimation;
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Shows the notification on screen, plays in animation if requested,
     * starts the close countdown if it's set to hide automatically.
     *
     * @param ownerWindow The owner of the popup. This must not be null.
     * @param anchorX     The x position of the popup anchor in screen coordinates
     * @param anchorY     The y position of the popup anchor in screen coordinates
     * @throws NullPointerException if content is null
     */
    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        if (content == null) {
            throw new NullPointerException("Notification content is null!!");
        }

        if (animate) {
            inAnimation.play();
        }
        super.show(ownerWindow, anchorX, anchorY);

        if (hideAfter.get()) {
            hideAfterTransition.setOnFinished(event -> hideNotification());
            hideAfterTransition.play();
        }
    }
}
