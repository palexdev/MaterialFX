package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.LabeledControlWrapper;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MFXToggleButtonSkin extends SkinBase<MFXToggleButton> {
    //================================================================================
    // Properties
    //================================================================================
    private final BorderPane container;
    private final StackPane toggleContainer;
    private final Circle circle;
    private final Line line;
    private final LabeledControlWrapper text;

    private final MFXCircleRippleGenerator rippleGenerator;

    public MFXToggleButtonSkin(MFXToggleButton toggleButton) {
        super(toggleButton);

        // Line
        line = new Line();
        line.getStyleClass().add("line");
        line.endXProperty().bind(toggleButton.lengthProperty().subtract(line.strokeWidthProperty()));
        line.strokeWidthProperty().bind(toggleButton.radiusProperty().multiply(1.5));
        line.setSmooth(true);

        // Circle
        circle = new Circle();
        circle.getStyleClass().add("circle");
        circle.radiusProperty().bind(toggleButton.radiusProperty());
        circle.setSmooth(true);
        circle.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL1));

        // Ripple Generator, Line, Circle container
        toggleContainer = new StackPane(line, circle);
        toggleContainer.setAlignment(Pos.CENTER_LEFT);
        toggleContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        toggleContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        toggleContainer.setPickOnBounds(false);

        rippleGenerator = new MFXCircleRippleGenerator(toggleContainer);
        rippleGenerator.setAnimateBackground(false);
        rippleGenerator.setClipSupplier(() -> null);
        rippleGenerator.setRipplePositionFunction(event -> {
            PositionBean position = new PositionBean();
            position.xProperty().bind(Bindings.createDoubleBinding(
                    () -> circle.localToParent(circle.getLayoutBounds()).getCenterX(),
                    circle.translateXProperty()
            ));
            position.yProperty().bind(Bindings.createDoubleBinding(
                    () -> circle.localToParent(circle.getLayoutBounds()).getCenterY(),
                    circle.layoutBoundsProperty()
            ));
            return position;
        });
        toggleContainer.getChildren().add(0, rippleGenerator);

        text = new LabeledControlWrapper(toggleButton);
        if (toggleButton.isTextExpand()) text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Control's top container
        container = new BorderPane();
        initPane();
        updateAlignment();

        getChildren().setAll(container);
        addListeners();
    }

    private void addListeners() {
        MFXToggleButton toggleButton = getSkinnable();

        toggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> buildAndPlayAnimation(newValue));
        toggleButton.alignmentProperty().addListener(invalidated -> updateAlignment());
        toggleButton.contentDispositionProperty().addListener((invalidated) -> initPane());
        toggleButton.gapProperty().addListener(invalidated -> initPane());
        toggleButton.textExpandProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            } else {
                text.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            }
        });

        NodeUtils.waitForSkin(
                toggleButton,
                () -> {
                    double endX = toggleButton.isSelected() ? line.getLayoutBounds().getWidth() - circle.getRadius() * 2 : 0;
                    TimelineBuilder.build().add(KeyFrames.of(150, circle.translateXProperty(), endX, Interpolator.EASE_BOTH)).getAnimation().play();
                },
                false,
                true
        );
    }

    protected void initPane() {
        MFXToggleButton toggleButton = getSkinnable();
        ContentDisplay disposition = toggleButton.getContentDisposition();
        double gap = toggleButton.getGap();

        container.getChildren().clear();
        container.setCenter(text);
        switch (disposition) {
            case TOP: {
                container.setTop(toggleContainer);
                BorderPane.setMargin(text, InsetsFactory.top(gap));
                break;
            }
            case RIGHT: {
                container.setRight(toggleContainer);
                BorderPane.setMargin(text, InsetsFactory.right(gap));
                break;
            }
            case BOTTOM: {
                container.setBottom(toggleContainer);
                BorderPane.setMargin(text, InsetsFactory.bottom(gap));
                break;
            }
            case TEXT_ONLY:
            case LEFT: {
                container.setLeft(toggleContainer);
                BorderPane.setMargin(text, InsetsFactory.left(gap));
                break;
            }
            case GRAPHIC_ONLY:
            case CENTER: {
                container.setCenter(toggleContainer);
                BorderPane.setMargin(text, InsetsFactory.none());
                break;
            }
        }
    }

    protected void updateAlignment() {
        MFXToggleButton toggleButton = getSkinnable();
        Pos alignment = toggleButton.getAlignment();

        if (PositionUtils.isTop(alignment)) {
            BorderPane.setAlignment(toggleContainer, Pos.TOP_CENTER);
        } else if (PositionUtils.isCenter(alignment)) {
            BorderPane.setAlignment(toggleContainer, Pos.CENTER);
        } else if (PositionUtils.isBottom(alignment)) {
            BorderPane.setAlignment(toggleContainer, Pos.BOTTOM_CENTER);
        }
    }

    private void buildAndPlayAnimation(boolean selection) {
        double endX = selection ? line.getBoundsInParent().getMaxX() - circle.getRadius() * 2 : 0;
        TimelineBuilder.build()
                .add(
                        KeyFrames.of(0, event -> rippleGenerator.generateRipple(null)),
                        KeyFrames.of(150, circle.translateXProperty(), endX, Interpolator.EASE_BOTH)
                )
                .getAnimation().play();
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + Math.max(toggleContainer.prefWidth(-1), text.prefWidth(-1)) + rightInset;
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
