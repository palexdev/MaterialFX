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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepper.MFXStepperEvent;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXStepperToggle.MFXStepperToggleEvent;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXStepper}.
 * <p>
 * It is basically a {@link BorderPane} with three sections: top, center, bottom.
 * <p>
 * At the top there is the {@link HBox} that contains the {@code MFXStepperToggles} and the progress bar
 * which is realized by using a group and two rectangles. One rectangle is for the background and the other is for the progress.
 * The first one is manually adjusted both for x property and width property.
 * <p>
 * At the center there is a {@link StackPane} with a minimum size of {@code 400x400}, it is the content pane namely the node that
 * will contain the content specifies by each stepper toggle. The style class is set to "content-pane".
 * <p>
 * At the bottom there is the {@link HBox} that contains the previous and next buttons. The style class is set to "buttons-box".
 * <p></p>
 * The stepper skin is rather delicate because the progress bar is quite hard to manage since every layout change can
 * potentially break. The skin updates the layout by adding a listener to the {@link MFXStepper#needsLayoutProperty()}.
 * When it changes the progress must be computed again with {@link #computeProgress()}.
 * A workaround is also needed in case the progress bar is animated and the layout changes. Without the workaround the
 * progress bar layout is re-computed by using the animation so the reposition process is not instantaneous.
 * To fix this annoying UI issue a boolean flag (buttonWasPressed) is set to true only when buttons are pressed and then set to false when the animation finishes,
 * so every layout change is done without playing the animation.
 *
 * @see MFXStepperToggle
 */
public class MFXStepperSkin extends SkinBase<MFXStepper> {
    private final StackPane contentPane;
    private final HBox stepperBar;
    private final HBox buttonsBox;
    private final MFXButton nextButton;
    private final MFXButton previousButton;
    private ChangeListener<Boolean> parentSizeListener;
    private ChangeListener<Window> windowListener;

    // Progressbar
    private final Group progressBar;
    private final double height = 7;
    private final Rectangle progressRect;
    private final Rectangle backgroundRect;
    private ParallelTransition progressAnimation;
    private boolean buttonWasPressed = false;

    public MFXStepperSkin(MFXStepper stepper) {
        super(stepper);

        progressRect = new Rectangle(0, 0, 0, height);
        progressRect.fillProperty().bind(stepper.progressColorProperty());
        progressRect.strokeProperty().bind(stepper.progressBarBackgroundProperty());
        progressRect.widthProperty().bind(stepper.widthProperty());
        progressRect.arcWidthProperty().bind(stepper.progressBarBorderRadiusProperty());
        progressRect.arcHeightProperty().bind(stepper.progressBarBorderRadiusProperty());
        progressRect.getStyleClass().add("bar-progress");

        backgroundRect = new Rectangle(0, height);
        backgroundRect.fillProperty().bind(stepper.progressBarBackgroundProperty());
        backgroundRect.arcWidthProperty().bind(stepper.progressBarBorderRadiusProperty());
        backgroundRect.arcHeightProperty().bind(stepper.progressBarBorderRadiusProperty());
        backgroundRect.getStyleClass().add("bar-background");

        progressAnimation = new ParallelTransition();
        progressAnimation.setInterpolator(MFXAnimationFactory.getInterpolatorV1());
        progressAnimation.setOnFinished(event -> buttonWasPressed = false);

        progressBar = new Group(progressRect, backgroundRect);
        progressBar.setManaged(false);

        stepperBar = new HBox(progressBar);
        stepperBar.spacingProperty().bind(stepper.spacingProperty());
        stepperBar.alignmentProperty().bind(stepper.alignmentProperty());
        stepperBar.getChildren().addAll(stepper.getStepperToggles());
        stepperBar.setMinHeight(100);
        stepperBar.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        nextButton = new MFXButton("Next");
        nextButton.setManaged(false);
        nextButton.getRippleGenerator().setClipSupplier(() ->
                new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE, 34, 34).build(nextButton)
        );

        previousButton = new MFXButton("Previous");
        previousButton.setManaged(false);
        previousButton.getRippleGenerator().setClipSupplier(() ->
                new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE, 34, 34).build(previousButton)
        );

        buttonsBox = new HBox(64, previousButton, nextButton);
        buttonsBox.getStyleClass().setAll("buttons-box");
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setMinHeight(50);

        contentPane = new StackPane();
        contentPane.getStyleClass().setAll("content-pane");

        BorderPane container = new BorderPane();
        container.getStylesheets().setAll(stepper.getUserAgentStylesheet());
        container.setTop(stepperBar);
        container.setCenter(contentPane);
        container.setBottom(buttonsBox);
        getChildren().add(container);

        parentSizeListener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                computeProgress();
            }
        };
        windowListener = (observable, oldValue, newValue) -> computeProgress();

        setListeners();
    }

    /**
     * Adds the following listeners and handlers/filters.
     * <p>
     * <p> - Adds a filter for FORCE_LAYOUT_UPDATE_EVENT events to updated the layout and the progress bar.
     * <p> - Adds a filter for MOUSE_PRESSED events to acquire the focus.
     * <p> - Adds a filter for STATE_CHANGES events to re-compute the progress, {@link #computeProgress()}.
     * <p> - Adds a listener to the stepper's toggles list, when it is invalidated the stepper state is reset with
     * {@link MFXStepper#reset()} and the new toggles are placed. For some reason the progress bar width may be
     * miscalculated so a workaround is needed. A {@link PauseTransition} is played, after 250ms it is requested to
     * re-compute the layout.
     * <p> - Adds a listener to the {@link MFXStepper#currentContentProperty()} to update the content pane children.
     * <p> - Adds a listener to the {@link MFXStepper#lastToggleProperty()} to call {@link #computeProgress()}.
     * <p> - Specifies the buttons behavior to set the buttonWasPressed to true and call the {@link MFXStepper#next()} and
     * {@link MFXStepper#previous()} methods.
     * <p></p>
     * Calls {@link #manageScene()}.
     */
    private void setListeners() {
        MFXStepper stepper = getSkinnable();

        stepper.addEventFilter(MFXStepperEvent.FORCE_LAYOUT_UPDATE_EVENT, event -> {
            stepper.requestLayout();
            computeProgress();
        });

        stepper.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> stepper.requestFocus());
        stepper.addEventFilter(MFXStepperToggleEvent.STATE_CHANGED, event -> computeProgress());

        stepper.getStepperToggles().addListener((InvalidationListener) invalidated -> {
            stepper.reset();
            stepperBar.getChildren().setAll(stepper.getStepperToggles());
            stepperBar.getChildren().add(0, progressBar);
            stepper.next();

            PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));
            pauseTransition.setOnFinished(event -> stepper.requestLayout());
            pauseTransition.play();
        });
        stepper.currentContentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                contentPane.getChildren().setAll(newValue);
            } else {
                contentPane.getChildren().clear();
            }
        });
        stepper.lastToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                computeProgress();
            }
        });

        nextButton.setOnAction(event -> {
            buttonWasPressed = true;
            stepper.next();
        });
        previousButton.setOnAction(event -> {
            buttonWasPressed = true;
            stepper.previous();
        });

        manageScene();
    }

    /**
     * Responsible for managing the stepper' scene. Adds listeners to the scene to initialize the stepper
     * by calling {@link MFXStepper#next()} the first time thus selecting the first toggle, to the
     * {@link MFXStepper#needsLayoutProperty()} to updated the layout and the progress bar.
     */
    private void manageScene() {
        MFXStepper stepper = getSkinnable();

        Scene scene = stepper.getScene();
        if (scene != null) {
            stepper.next();
        }

        stepper.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null && stepper.getCurrentIndex() == -1) {
                stepper.next();
            }
        });

        stepper.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            if (!buttonWasPressed) {
                stepper.requestLayout();
            }
            computeProgress();
        });
    }

    /**
     * Responsible for computing the position and size of the rectangle used to show the progress.
     * <p>
     * Keep in mind that the rectangle which is moved is the background rectangle not the progress one.
     * Think about it as the background rectangle covers the progress one and when some progress is made you want to
     * uncover the progress one by moving the background one.
     * <p></p>
     * Three cases are evaluated:
     * <p> - The stepper {@link MFXStepper#lastToggleProperty()} is true, so the background rectangle width will be 0.
     * <p> - The current stepper toggle is not null, so the background rectangle width will be computed as follows.
     * The toggle's circle bounds are retrieved using {@link MFXStepperToggle#getGraphicBounds()}. The X is computed
     * as the minX of those Bounds converted from local to parent using {@link Node#localToParent(Bounds)}. The width
     * is computed as the stepper's width minus the previously calculated X.
     * <p> - The current stepper toggle is null so the X is 0 and the width is equal to the stepper's width.
     * <p></p>
     * The computed values are used by {@link #updateProgressBar(double, double)}
     * <p></p>
     * It can be tricky to understand but with the given information it should be understandable, maybe draw it, it will
     * be easier.
     *
     */
    private void computeProgress() {
        MFXStepper stepper = getSkinnable();

        if (stepper.isLastToggle()) {
            updateProgressBar(stepper.getWidth(), 0);
            return;
        }

        MFXStepperToggle stepperToggle = stepper.getCurrentStepperNode();
        if (stepperToggle != null) {
            Bounds bounds = stepperToggle.getGraphicBounds();
            if (bounds != null) {
                double minX = snapSizeX(stepperToggle.localToParent(bounds).getMinX());
                double width = snapSizeX(stepper.getWidth() - minX);
                updateProgressBar(minX, width);
            }
        } else {
            updateProgressBar(0, stepper.getWidth());
        }
    }

    /**
     * Sets the background rectangle x and width properties to the given values.
     * If the {@link MFXStepper#animatedProperty()} or the buttonWasPressed flag are false
     * then the properties are updated immediately. Otherwise they are updated by two separate timelines
     * played at the same time using a {@link ParallelTransition}.
     */
    private void updateProgressBar(double x, double width) {
        MFXStepper stepper = getSkinnable();
        if (!stepper.isAnimated() || !buttonWasPressed) {
            backgroundRect.setX(x);
            backgroundRect.setWidth(width);
            buttonWasPressed = false;
            return;
        }

        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(stepper.getAnimationDuration()), new KeyValue(backgroundRect.xProperty(), x));
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(stepper.getAnimationDuration()), new KeyValue(backgroundRect.widthProperty(), width));
        Timeline timeline1 = new Timeline(keyFrame1);
        Timeline timeline2 = new Timeline(keyFrame2);
        progressAnimation.getChildren().setAll(timeline1, timeline2);
        progressAnimation.playFromStart();
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinWidth(height, topInset, leftInset, bottomInset, rightInset) + (getSkinnable().getExtraSpacing() * 2), 300);
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + stepperBar.getHeight() + buttonsBox.getHeight() + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (progressAnimation.getStatus() != Animation.Status.STOPPED) {
            progressAnimation.stop();
        }
        progressAnimation = null;
        parentSizeListener = null;
        windowListener = null;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        double barY = snapPositionY((stepperBar.getHeight() / 2.0) - (height / 2.0));
        progressBar.resizeRelocate(0.0, barY, w, height);

        double bw = 125;
        double bh = 34;
        double pbx = snapPositionX(15);
        double nbx = snapPositionX(w - bw - 15);
        double by = snapPositionY((buttonsBox.getHeight() / 2.0) - (bh / 2.0));

        previousButton.resizeRelocate(pbx, by, bw, bh);
        nextButton.resizeRelocate(nbx, by, bw, bh);
    }
}
