package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.LabeledControlWrapper;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.KeyValue;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class MFXRadioButtonSkin extends SkinBase<MFXRadioButton> {
    //================================================================================
    // Properties
    //================================================================================
    private final StackPane container;
    private final Circle radio;
    private final Circle dot;
    private final LabeledControlWrapper text;

    private final MFXCircleRippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRadioButtonSkin(MFXRadioButton radioButton) {
        super(radioButton);

        radio = new Circle();
        radio.getStyleClass().add("radio");
        radio.radiusProperty().bind(radioButton.radiusProperty());
        radio.setSmooth(true);

        dot = new Circle();
        dot.getStyleClass().add("dot");
        dot.radiusProperty().bind(radioButton.radiusProperty());
        dot.setScaleX(0);
        dot.setScaleY(0);
        dot.setSmooth(true);

        text = new LabeledControlWrapper(radioButton);

        container = new StackPane(radio, dot);
        container.setManaged(false);

        rippleGenerator = new MFXCircleRippleGenerator(radioButton);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setClipSupplier(() -> null);
        rippleGenerator.setRipplePositionFunction(event -> {
            PositionBean position = new PositionBean();
            position.setX(container.getBoundsInParent().getCenterX());
            position.setY(container.getBoundsInParent().getCenterY());
            return position;
        });

        getChildren().setAll(rippleGenerator, container, text);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a listener to the selected property for animations, colors and ripples.
     * Also handles {@link RadioButton#arm()}
     */
    private void addListeners() {
        MFXRadioButton radioButton = getSkinnable();

        radioButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> radioButton.fire());
        radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            animate(newValue);
            rippleGenerator.generateRipple(null);
        });

        NodeUtils.waitForSkin(radioButton, () -> {
            if (radioButton.isSelected()) animate(true);
        }, false, false);
    }

    private void animate(boolean selected) {
        TimelineBuilder.build()
                .add(KeyFrames.of(
                        100,
                        new KeyValue(dot.scaleXProperty(), selected ? 0.55 : 0, Interpolators.EASE_OUT.toInterpolator()),
                        new KeyValue(dot.scaleYProperty(), selected ? 0.55 : 0, Interpolators.EASE_OUT.toInterpolator())
                ))
                .getAnimation()
                .play();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXRadioButton radioButton = getSkinnable();
        ContentDisplay disposition = radioButton.getContentDisposition();
        double gap = radioButton.getGap();

        double minW;
        switch (disposition) {
            case LEFT:
            case RIGHT:
            case TEXT_ONLY:
                minW = leftInset + container.prefWidth(-1) + gap + text.prefWidth(-1) + rightInset;
                break;
            case TOP:
            case BOTTOM:
                minW = leftInset + Math.max(container.prefWidth(-1), text.prefWidth(-1)) + rightInset;
                break;
            case CENTER:
            case GRAPHIC_ONLY:
                minW = leftInset + container.prefWidth(-1) + rightInset;
                break;
            default:
                minW = super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        return minW;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        MFXRadioButton radioButton = getSkinnable();
        ContentDisplay disposition = radioButton.getContentDisposition();
        double gap = radioButton.getGap();

        double minH;
        switch (disposition) {
            case LEFT:
            case RIGHT:
            case TEXT_ONLY:
                minH = topInset + Math.max(container.prefHeight(-1), text.prefHeight(-1)) + bottomInset;
                break;
            case TOP:
            case BOTTOM:
                minH = topInset + container.prefHeight(-1) + gap + text.prefHeight(-1) + bottomInset;
                break;
            case CENTER:
            case GRAPHIC_ONLY:
                minH = leftInset + container.prefHeight(-1) + rightInset;
                break;
            default:
                minH = super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
        }
        return minH;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(-1);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(-1);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        MFXRadioButton radioButton = getSkinnable();
        ContentDisplay disposition = radioButton.getContentDisposition();
        Insets padding = radioButton.getPadding();
        double gap = radioButton.getGap();

        double rcW = container.prefWidth(-1);
        double rcH = container.prefHeight(-1);
        double rcX = 0;
        double rcY = 0;


        double txW = text.prefWidth(-1);
        double txH = text.prefHeight(-1);
        double txX = 0;
        double txY = 0;

        switch (disposition) {
            case TOP: {
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = 0;
                txX = (contentWidth / 2) - (txW / 2);
                txY = rcH + gap;
                break;
            }
            case RIGHT: {
                rcX = contentWidth - rcW;
                rcY = (contentHeight / 2) - (rcH / 2);
                txX = rcX - txW - gap;
                txY = (contentHeight / 2) - (txH / 2);
                break;
            }
            case BOTTOM: {
                txX = (contentWidth / 2) - (txW / 2);
                txY = 0;
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = txH + gap;
                break;
            }
            case TEXT_ONLY:
            case LEFT: {
                rcX = 0;
                rcY = (contentHeight / 2) - (rcH / 2);
                txX = rcW + gap;
                txY = (contentHeight / 2) - (txH / 2);
                break;
            }
            case CENTER:
            case GRAPHIC_ONLY: {
                rcX = (contentWidth / 2) - (rcW / 2);
                rcY = (contentHeight / 2) - (rcH / 2);
                txW = 0;
                txH = 0;
                break;
            }
        }

        container.resizeRelocate(
                snapPositionX(rcX + padding.getLeft()),
                snapPositionY(rcY + padding.getTop()),
                rcW,
                rcH
        );
        text.resizeRelocate(
                snapPositionX(txX + padding.getLeft()),
                snapPositionY(txY + padding.getTop()),
                txW,
                txH
        );
    }
}
