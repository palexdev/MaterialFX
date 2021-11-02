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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.enums.DialogType;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.MFXScrimEffect;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a material dialog.
 * <p>
 * Extends {@code BorderPane}.
 * <p>
 * <b>Notice: the dialog is visible so during initialization, before showing it you should
 * use {@code setVisible(false)}</b>
 */
public abstract class AbstractMFXDialog extends BorderPane {
    //================================================================================
    // Properties
    //================================================================================
    private DialogType type;

    protected final StringProperty title = new SimpleStringProperty("");
    protected final StringProperty content = new SimpleStringProperty("");
    protected final BooleanProperty centerBeforeShow = new SimpleBooleanProperty(true);
    protected final List<Node> closeButtons = new ArrayList<>();

    protected final MFXScrimEffect scrimEffect = new MFXScrimEffect();

    /**
     * Specifies the opacity/strength of the scrim effect.
     */
    protected final DoubleProperty scrimOpacity = new SimpleDoubleProperty(0.15);

    /**
     * Specifies whether to apply scrim effect to dialog's parent when shown or not.
     */
    protected final BooleanProperty scrimBackground = new SimpleBooleanProperty(true);

    /**
     * Specifies whether the dialog should close when clicking outside of it or not.
     */
    protected final BooleanProperty overlayClose = new SimpleBooleanProperty(false);

    /**
     * Specifies whether the dialog is draggable or not.
     */
    protected final BooleanProperty isDraggable = new SimpleBooleanProperty(false);

    private double mouseAnchorX;
    private double mouseAnchorY;

    protected MFXAnimationFactory inAnimationType = MFXAnimationFactory.FADE_IN;
    protected MFXAnimationFactory outAnimationType = MFXAnimationFactory.FADE_OUT;
    protected final ParallelTransition inAnimation = new ParallelTransition();
    protected final ParallelTransition outAnimation = new ParallelTransition();

    /**
     * Specifies whether to play the inAnimation when {@code show()} is called or not.
     */
    protected final BooleanProperty animateIn = new SimpleBooleanProperty(false);

    /**
     * Specifies whether to play the outAnimation when {@code close()} is called or not.
     */
    protected final BooleanProperty animateOut = new SimpleBooleanProperty(false);

    /**
     * Specifies the duration of in and out animations.
     */
    protected final DoubleProperty animationMillis = new SimpleDoubleProperty(300);

    //================================================================================
    // Event Handlers
    //================================================================================
    protected EventHandler<MouseEvent> closeHandler = event -> close();

    private final EventHandler<MouseEvent> overlayCloseHandler = mouseEvent -> {
        if (!NodeUtils.inHierarchy(mouseEvent, AbstractMFXDialog.this)) {
            close();
        }
    };

    private final EventHandler<MouseEvent> pressHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseAnchorX = mouseEvent.getX();
            mouseAnchorY = mouseEvent.getY();
        }
    };

    private final EventHandler<MouseEvent> dragHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Pane dialogParent = (Pane) getParent();

            double maxX = dialogParent.getWidth() - getWidth();
            double maxY = dialogParent.getHeight() - getHeight();
            double computedX = mouseEvent.getX() + getLayoutX() - mouseAnchorX;
            double computedY = mouseEvent.getY() + getLayoutY() - mouseAnchorY;

            setManaged(false);
            // Check X bounds
            if (getLayoutX() >= 0 && getLayoutX() <= maxX) {
                setLayoutX(computedX);
            } else if (computedX > 0 && computedX < maxX) {
                setLayoutX(computedX);
            }

            // Check Y bounds
            if (getLayoutY() >= 0 && getLayoutY() <= maxY) {
                setLayoutY(computedY);
            } else if (computedY > 0 && computedY < maxY) {
                setLayoutY(computedY);
            }

            /*
             * Since this handler is not called for every pixel translate properties may end
             * a little out of bounds (> maxX || < -maxX ; > maxY || < -maxY) if this happens
             * translateX/Y are set to the max possible value
             */
            if (getLayoutX() > maxX) {
                setLayoutX(maxX);
            } else if (getLayoutX() < 0) {
                setLayoutX(0);
            }
            if (getLayoutY() > maxY) {
                setLayoutY(maxY);
            } else if (getLayoutY() < 0) {
                setLayoutY(0);
            }

            mouseEvent.consume();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================

    /**
     * <b>Notice: the dialog is visible so during initialization, before showing it you should
     * use {@code setVisible(false)}</b>
     */
    public AbstractMFXDialog() {
        setPrefSize(400, 300);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        MFXButton button = new MFXButton("");
        closeButtons.add(button);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    public abstract void show();

    public abstract void close();

    public abstract AbstractMFXDialog setActions(HBox actionsBox);

    public abstract void computeSizeAndPosition();

    //================================================================================
    // Methods
    //================================================================================
    public DialogType getType() {
        return type;
    }

    public void setType(DialogType type) {
        this.type = type;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public boolean isCenterBeforeShow() {
        return centerBeforeShow.get();
    }

    public BooleanProperty centerBeforeShowProperty() {
        return centerBeforeShow;
    }

    public void setCenterBeforeShow(boolean centerBeforeShow) {
        this.centerBeforeShow.set(centerBeforeShow);
    }

    public MFXScrimEffect getScrimEffect() {
        return scrimEffect;
    }

    public void addCloseButton(Node button) {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
        closeButtons.add(button);
    }

    public List<Node> getCloseButtons() {
        return closeButtons;
    }

    /**
     * Replaces the dialog's default close button with a new one and adds the close handler to it.
     *
     * @param buttons The new close button
     */
    public void setCloseButtons(Node... buttons) {
        closeButtons.forEach(btn -> btn.removeEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler));
        closeButtons.clear();
        closeButtons.addAll(List.of(buttons));
        closeButtons.forEach(btn -> btn.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler));
    }

    public EventHandler<MouseEvent> getCloseHandler() {
        return this.closeHandler;
    }

    /**
     * Replaces the dialog's default close handler with a new one,
     * removes the old one from the button, replaces the handler and then
     * re-adds the handler to the button.
     *
     * @param newHandler The new close handler
     */
    public void setCloseHandler(EventHandler<MouseEvent> newHandler) {
        closeButtons.forEach(closeButton -> {
            closeButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
            closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, newHandler);
        });
        this.closeHandler = newHandler;
    }

    public double getScrimOpacity() {
        return scrimOpacity.get();
    }

    public DoubleProperty scrimOpacityProperty() {
        return scrimOpacity;
    }

    public void setScrimOpacity(double scrimOpacity) {
        this.scrimOpacity.set(scrimOpacity);
    }

    public boolean isScrimBackground() {
        return scrimBackground.get();
    }

    public BooleanProperty scrimBackgroundProperty() {
        return scrimBackground;
    }

    public void setScrimBackground(boolean scrimBackground) {
        this.scrimBackground.set(scrimBackground);
    }

    public boolean isOverlayClose() {
        return overlayClose.get();
    }

    public BooleanProperty overlayCloseProperty() {
        return overlayClose;
    }

    public void setOverlayClose(boolean overlayClose) {
        this.overlayClose.set(overlayClose);
    }

    public boolean isDraggable() {
        return isDraggable.get();
    }

    public BooleanProperty isDraggableProperty() {
        return isDraggable;
    }

    public void setIsDraggable(boolean isDraggable) {
        this.isDraggable.set(isDraggable);
    }

    public void setInAnimationType(MFXAnimationFactory inAnimationType) {
        this.inAnimationType = inAnimationType;
    }

    public void setOutAnimationType(MFXAnimationFactory outAnimationType) {
        this.outAnimationType = outAnimationType;
    }

    public ParallelTransition getInAnimation() {
        return inAnimation;
    }

    public ParallelTransition getOutAnimation() {
        return outAnimation;
    }

    public boolean isAnimateIn() {
        return animateIn.get();
    }

    public BooleanProperty animateInProperty() {
        return animateIn;
    }

    public void setAnimateIn(boolean animateIn) {
        this.animateIn.set(animateIn);
    }

    public boolean isAnimateOut() {
        return animateOut.get();
    }

    public BooleanProperty animateOutProperty() {
        return animateOut;
    }

    public void setAnimateOut(boolean animateOut) {
        this.animateOut.set(animateOut);
    }

    public double getAnimationMillis() {
        return animationMillis.get();
    }

    public DoubleProperty animationMillisProperty() {
        return animationMillis;
    }

    public void setAnimationMillis(double animationMillis) {
        this.animationMillis.set(animationMillis);
    }


    /**
     * When the {@code overlayClose} property is set to true, adds the {@code EventHandler} for the close.
     */
    protected void addOverlayHandler() {
        getParent().addEventHandler(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
    }

    /**
     * When the {@code overlayClose} property is set to false, removes the {@code EventHandler} for the close.
     */
    protected void removeOverlayHandler() {
        getParent().removeEventHandler(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
    }

    /**
     * When the {@code isDraggable} property is set to true, adds the {@code EventHandlers} for the drag.
     */
    protected void makeDraggable() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
    }

    /**
     * When the {@code isDraggable} property is set to false, removes the {@code EventHandlers} for the drag.
     */
    protected void clearDragHandlers() {
        removeEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
        removeEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
    }

    //================================================================================
    // Events
    //================================================================================

    /**
     * Events class for MFXDialogs.
     * <p>
     * Defines four new EvenTypes:
     * <p>
     * - BEFORE_OPEN_EVENT: should be fired at the start of the {@link #show()} method or even before. <p></p>
     * - ON_OPENED_EVENT : should be fired when the dialog is set to visible or at the end of the show animation. <p></p>
     * - BEFORE_CLOSE_EVENT: should be fired at the start of the {@link #close()} methods or even before. <p></p>
     * - ON_CLOSED_EVENT: should be fired when the dialog is se to not be visible or at the end of the hide animation. <p></p>
     * <p></p>
     * Note that if the dialog is not animated, or the animation are really short, the BEFORE events will "overlap" with the ON events.
     * These events are automatically fired by {@link MFXDialog}, if you wan to change the behavior you must override the
     * {@link MFXDialog#show()} and {@link MFXDialog#close()} methods, for any other custom implementation that extends {@link AbstractMFXDialog}
     * you have to define the behavior yourself.
     */
    public static class MFXDialogEvent extends Event {

        public static final EventType<MFXDialogEvent> BEFORE_OPEN_EVENT = new EventType<>(ANY, "BEFORE_OPEN_EVENT");
        public static final EventType<MFXDialogEvent> ON_OPENED_EVENT = new EventType<>(ANY, "ON_OPENED_EVENT");
        public static final EventType<MFXDialogEvent> BEFORE_CLOSE_EVENT = new EventType<>(ANY, "BEFORE_CLOSE_EVENT");
        public static final EventType<MFXDialogEvent> ON_CLOSED_EVENT = new EventType<>(ANY, "ON_CLOSED_EVENT");

        public MFXDialogEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }
    }

    private final ObjectProperty<EventHandler<MFXDialogEvent>> onBeforeOpen = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(MFXDialogEvent.BEFORE_OPEN_EVENT, get());
        }
    };

    private final ObjectProperty<EventHandler<MFXDialogEvent>> onOpened = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(MFXDialogEvent.ON_OPENED_EVENT, get());
        }
    };

    private final ObjectProperty<EventHandler<MFXDialogEvent>> onBeforeClose = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(MFXDialogEvent.BEFORE_CLOSE_EVENT, get());
        }
    };

    private final ObjectProperty<EventHandler<MFXDialogEvent>> onClosed = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(MFXDialogEvent.ON_CLOSED_EVENT, get());
        }
    };

    public EventHandler<MFXDialogEvent> getOnBeforeOpen() {
        return onBeforeOpen.get();
    }

    public ObjectProperty<EventHandler<MFXDialogEvent>> onBeforeOpenProperty() {
        return onBeforeOpen;
    }

    public void setOnBeforeOpen(EventHandler<MFXDialogEvent> onBeforeOpen) {
        this.onBeforeOpen.set(onBeforeOpen);
    }

    public EventHandler<MFXDialogEvent> getOnOpened() {
        return onOpened.get();
    }

    public ObjectProperty<EventHandler<MFXDialogEvent>> onOpenedProperty() {
        return onOpened;
    }

    public void setOnOpened(EventHandler<MFXDialogEvent> onOpened) {
        this.onOpened.set(onOpened);
    }

    public EventHandler<MFXDialogEvent> getOnBeforeClose() {
        return onBeforeClose.get();
    }

    public ObjectProperty<EventHandler<MFXDialogEvent>> onBeforeCloseProperty() {
        return onBeforeClose;
    }

    public void setOnBeforeClose(EventHandler<MFXDialogEvent> onBeforeClose) {
        this.onBeforeClose.set(onBeforeClose);
    }

    public EventHandler<MFXDialogEvent> getOnClosed() {
        return onClosed.get();
    }

    public ObjectProperty<EventHandler<MFXDialogEvent>> onClosedProperty() {
        return onClosed;
    }

    public void setOnClosed(EventHandler<MFXDialogEvent> onClosed) {
        this.onClosed.set(onClosed);
    }

    public void fireDialogEvent(EventType<MFXDialogEvent> eventType) {
        fireEvent(new MFXDialogEvent(eventType));
    }
}
