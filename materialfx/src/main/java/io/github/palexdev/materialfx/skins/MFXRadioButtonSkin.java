package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.LabeledControlWrapper;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.animation.KeyValue;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class MFXRadioButtonSkin extends SkinBase<MFXRadioButton> {
    //================================================================================
    // Properties
    //================================================================================
    private final BorderPane container;
    private final StackPane radioContainer;
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
        if (radioButton.isTextExpand()) text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        radioContainer = new StackPane(radio, dot);

        rippleGenerator = new MFXCircleRippleGenerator(radioContainer);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setClipSupplier(() -> null);
        rippleGenerator.setRipplePositionFunction(event -> {
            PositionBean position = new PositionBean();
            position.setX(radio.getBoundsInParent().getCenterX());
            position.setY(radio.getBoundsInParent().getCenterY());
            return position;
        });
        radioContainer.getChildren().add(0, rippleGenerator);
        rippleGenerator.setManaged(false);

        container = new BorderPane();
        initPane();
        updateAlignment();

        getChildren().setAll(container);
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

        radioButton.alignmentProperty().addListener(invalidated -> updateAlignment());
        radioButton.contentDispositionProperty().addListener(invalidated -> initPane());
        radioButton.gapProperty().addListener(invalidated -> initPane());
        radioButton.radioGapProperty().addListener(invalidated -> {
            if (radioButton.isSelected()) {
                double radius = radioButton.getRadius();
                double scale = (radius - radioButton.getRadioGap()) / radius;
                dot.setScaleX(scale);
                dot.setScaleY(scale);
            }
        });
        radioButton.textExpandProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            } else {
                text.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            }
        });

        NodeUtils.waitForSkin(radioButton, () -> {
            if (radioButton.isSelected()) animate(true);
        }, false, false);
    }

    private void animate(boolean selected) {
        MFXRadioButton radioButton = getSkinnable();
        double radius = radioButton.getRadius();
        double scale = (radius - radioButton.getRadioGap()) / radius;
        TimelineBuilder.build()
                .add(KeyFrames.of(
                        100,
                        new KeyValue(dot.scaleXProperty(), selected ? scale : 0, Interpolators.EASE_OUT.toInterpolator()),
                        new KeyValue(dot.scaleYProperty(), selected ? scale : 0, Interpolators.EASE_OUT.toInterpolator())
                ))
                .getAnimation()
                .play();
    }

    protected void initPane() {
        MFXRadioButton radioButton = getSkinnable();
        ContentDisplay disposition = radioButton.getContentDisposition();
        double gap = radioButton.getGap();

        container.getChildren().clear();
        container.setCenter(text);
        switch (disposition) {
            case TOP: {
                container.setTop(radioContainer);
                BorderPane.setMargin(text, InsetsFactory.top(gap));
                break;
            }
            case RIGHT: {
                container.setRight(radioContainer);
                BorderPane.setMargin(text, InsetsFactory.right(gap));
                break;
            }
            case BOTTOM: {
                container.setBottom(radioContainer);
                BorderPane.setMargin(text, InsetsFactory.bottom(gap));
                break;
            }
            case TEXT_ONLY:
            case LEFT: {
                container.setLeft(radioContainer);
                BorderPane.setMargin(text, InsetsFactory.left(gap));
                break;
            }
            case GRAPHIC_ONLY:
            case CENTER: {
                container.setCenter(radioContainer);
                BorderPane.setMargin(text, InsetsFactory.none());
                break;
            }
        }
    }

    protected void updateAlignment() {
        MFXRadioButton radioButton = getSkinnable();
        Pos alignment = radioButton.getAlignment();

        if (PositionUtils.isTop(alignment)) {
            BorderPane.setAlignment(radioContainer, Pos.TOP_CENTER);
        } else if (PositionUtils.isCenter(alignment)) {
            BorderPane.setAlignment(radioContainer, Pos.CENTER);
        } else if (PositionUtils.isBottom(alignment)) {
            BorderPane.setAlignment(radioContainer, Pos.BOTTOM_CENTER);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + Math.max(radioContainer.prefWidth(-1), text.prefWidth(-1)) + rightInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(-1);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(-1);
    }
}
