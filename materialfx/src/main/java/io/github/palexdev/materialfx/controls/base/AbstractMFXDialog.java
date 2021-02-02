package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.MFXScrimEffect;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Base class for a material dialog.
 * <p>
 * Extends {@code BorderPane}.
 * <p>
 * <b>Notice: the dialog is visible so during initialization, before showing it you should
 * use {@code setVisible(false)}</b>
 */
public abstract class AbstractMFXDialog extends BorderPane {
    protected final StringProperty title = new SimpleStringProperty("");
    protected final StringProperty content = new SimpleStringProperty("");
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
    private final EventHandler<MouseEvent> overlayCloseHandler = mouseEvent -> {
        if (!NodeUtils.inHierarchy(mouseEvent.getPickResult().getIntersectedNode(), AbstractMFXDialog.this)) {
            close();
        }
    };
    protected MFXButton closeButton;
    protected boolean centerBeforeShow = true;
    protected MFXAnimationFactory inAnimationType = MFXAnimationFactory.FADE_IN;
    protected MFXAnimationFactory outAnimationType = MFXAnimationFactory.FADE_OUT;
    //================================================================================
    // Event Handlers
    //================================================================================
    protected EventHandler<MouseEvent> closeHandler = event -> close();
    //================================================================================
    // Properties
    //================================================================================
    private DialogType type;
    private double mouseAnchorX;
    private double mouseAnchorY;
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

        this.closeButton = new MFXButton("");
        this.closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    public abstract void show();

    public abstract void close();

    public abstract void computeCenter();

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

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setCenterBeforeShow(boolean centerBeforeShow) {
        this.centerBeforeShow = centerBeforeShow;
    }

    public MFXScrimEffect getScrimEffect() {
        return scrimEffect;
    }

    public MFXButton getCloseButton() {
        return closeButton;
    }

    /**
     * Replaces the dialog's default close button with a new one and adds the close handler to it.
     *
     * @param closeButton The new close button
     */
    public void setCloseButton(MFXButton closeButton) {
        this.closeButton = closeButton;
        this.closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
    }

    /**
     * Replaces the dialog's default close handler with a new one,
     * removes the old one from the button, replaces the handler and then
     * re-adds the handler to the button.
     *
     * @param newHandler The new close handler
     */
    public void setCloseHandler(EventHandler<MouseEvent> newHandler) {
        this.closeButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
        this.closeHandler = newHandler;
        this.closeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
    }

    public double getScrimOpacity() {
        return scrimOpacity.get();
    }

    public void setScrimOpacity(double scrimOpacity) {
        this.scrimOpacity.set(scrimOpacity);
    }

    public DoubleProperty scrimOpacityProperty() {
        return scrimOpacity;
    }

    public boolean isScrimBackground() {
        return scrimBackground.get();
    }

    public void setScrimBackground(boolean scrimBackground) {
        this.scrimBackground.set(scrimBackground);
    }

    public BooleanProperty scrimBackgroundProperty() {
        return scrimBackground;
    }

    public boolean isOverlayClose() {
        return overlayClose.get();
    }

    public void setOverlayClose(boolean overlayClose) {
        this.overlayClose.set(overlayClose);
    }

    public BooleanProperty overlayCloseProperty() {
        return overlayClose;
    }

    public boolean isIsDraggable() {
        return isDraggable.get();
    }

    public void setIsDraggable(boolean isDraggable) {
        this.isDraggable.set(isDraggable);
    }

    public BooleanProperty isDraggableProperty() {
        return isDraggable;
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

    public void setAnimateIn(boolean animateIn) {
        this.animateIn.set(animateIn);
    }

    public BooleanProperty animateInProperty() {
        return animateIn;
    }

    public boolean isAnimateOut() {
        return animateOut.get();
    }

    public void setAnimateOut(boolean animateOut) {
        this.animateOut.set(animateOut);
    }

    public BooleanProperty animateOutProperty() {
        return animateOut;
    }

    public double getAnimationMillis() {
        return animationMillis.get();
    }

    public void setAnimationMillis(double animationMillis) {
        this.animationMillis.set(animationMillis);
    }

    public DoubleProperty animationMillisProperty() {
        return animationMillis;
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
     * When the {@code isDraggable} property is set to true, adds the {@code EventHandler}s for the drag.
     */
    protected void makeDraggable() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
    }

    /**
     * When the {@code isDraggable} property is set to false, removes the {@code EventHandler}s for the drag.
     */
    protected void clearDragHandlers() {
        removeEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
        removeEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
    }
}
