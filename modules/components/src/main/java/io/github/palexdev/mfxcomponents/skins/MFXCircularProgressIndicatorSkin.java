/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.progress.MFXProgressIndicator;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.Cubic;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/**
 * One of the available skins for {@link MFXProgressIndicator}. This in particular will display the progress on two
 * arcs, {@link Arc}: one is called the "track" and takes all the space (360°), thus representing a progress of 100%,
 * the other is simply called "arc" and it is sized according to the progress value; for example, a progress of 0.5
 * will result in the arc's length to also be 50% of the total, so 180°.
 * <p></p>
 * Now, to be precise, this skin is extremely complex, even more than {@link MFXLinearProgressIndicatorSkin},
 * for three reasons:
 * <p> 1) to replicate the design described by the Material Design 3 guidelines this complexity is <b>needed</b>.
 * The nodes count is actually lesser than the other skin, but their handling is much more intricate.
 * <p> 2) just like the linear design, the circular indicator shows a gap between the arc and the track when the progress
 * is not indeterminate. Arcs are rotated and sized appropriately for the gap to work as intended.
 * <p> 3) when the progress is indeterminate an endless animation is played, the track becomes a regular arc and a series
 * of rotations are applied to replicate the original animation. Guys, you hav absolutely no idea on how complex and hard
 * it was to replicate the animation, it's absolute madness.
 * <p>
 * Because of this complexity, I don't feel like going too much into detail, if you want to know more, read and try to
 * understand the source code.
 * <p></p>
 * Note that specs such as min sizes, animations durations and curves can be changed by extending the skin and overriding
 * the appropriate {@code protected static} fields.
 */
public class MFXCircularProgressIndicatorSkin extends MFXSkinBase<MFXProgressIndicator, BehaviorBase<MFXProgressIndicator>> {
    //================================================================================
    // Properties
    //================================================================================
    private final Region container;
    private final Arc lArc;
    private final Arc rArc;
    private final Node lClip;
    private final Node rClip;
    protected double arcMultiplier = 1.0;

    // Animations
    protected static Duration LINEAR_ROTATE_DURATION = Duration.millis(1568.0);
    protected static Duration CONTAINER_ROTATE_DURATION = Duration.millis(5332.0);
    protected static Duration ARCS_ROTATE_DURATION = Duration.millis(1333.0);
    protected static Duration HALF_ARCS_ROTATE_DURATION = ARCS_ROTATE_DURATION.divide(2);
    protected static Interpolator INDETERMINATE_CURVE = new Cubic(0.4, 0, 0.2, 1);
    protected Animation iAnimation; // Indeterminate animation

    protected static Duration DETERMINATE_DURATION = M3Motion.MEDIUM1;
    protected static Interpolator DETERMINATE_CURVE = new Cubic(0.4, 0, 0.6, 1);
    private Animation pAnimation;

    // Specs
    protected static double MIN_WIDTH = 48.0;
    protected static double MIN_HEIGHT = 48.0;
    protected static double BASE_ARCS_GAP = 4.0;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCircularProgressIndicatorSkin(MFXProgressIndicator indicator) {
        super(indicator);

        // Init arcs
        lArc = createArc("arc", "left", "track"); // The left arc is going to be used for the track too
        lClip = lArc.getClip();
        rArc = createArc("arc", "right");
        rClip = rArc.getClip();

        // Init container
        container = new Region() {
            {
                getChildren().addAll(lArc, rArc);
            }

            @Override
            protected void layoutChildren() {
                // Get sizes
                double width = getWidth();
                double height = getHeight();
                double lStroke = lArc.getStrokeWidth(); // Ideally these value should be the same for both the arcs
                double rStroke = rArc.getStrokeWidth();

                // Arcs
                lArc.setRadiusX(width / 2);
                lArc.setRadiusY(height / 2);
                lArc.setCenterX(width / 2);
                lArc.setCenterY(height / 2);

                rArc.setRadiusX(width / 2);
                rArc.setRadiusY(height / 2);
                rArc.setCenterX(width / 2);
                rArc.setCenterY(height / 2);

                // Clips
                if (lClip != null) {
                    double totalW = width + lStroke;
                    double totalH = height + lStroke;
                    lClip.resizeRelocate(
                        snapPositionX((width - totalW) / 2),
                        snapPositionY(-lStroke / 2),
                        snapSizeX((totalW + lStroke) / 2),
                        snapSizeY(totalH)
                    );
                }
                if (rClip != null) {
                    double totalW = width + rStroke;
                    double totalH = height + rStroke;
                    rClip.resizeRelocate(
                        snapPositionX(width / 2),
                        snapPositionY(-rStroke / 2),
                        snapSizeX(totalW / 2),
                        snapSizeY(totalH)
                    );
                }
            }
        };
        container.getStyleClass().add("container");

        // Finalize init
        getChildren().addAll(container);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds the following listeners:
     * <p> - A listener on the {@link MFXProgressIndicator#progressProperty()} to: update the layout when
     * the progress switches between determinate/indeterminate, activate/deactivate the ':indeterminate' {@link PseudoClass},
     * run the appropriate animation according to the state, {@link #animateIndeterminate()} or {@link #adjustProgress()}
     */
    private void addListeners() {
        MFXProgressIndicator indicator = getSkinnable();

        // Listeners
        listeners(
            onInvalidated(indicator.progressProperty())
                .then(p -> {
                    boolean indeterminate = indicator.isIndeterminate();
                    if (indeterminate) {
                        PseudoClasses.INDETERMINATE.setOn(indicator, true);
                        if (lClip != null) lArc.setClip(lClip);
                        if (rClip != null) rArc.setClip(rClip);
                        lArc.setLength(135.0);
                        rArc.setLength(100.0);
                        animateIndeterminate();
                        return;
                    }
                    if (iAnimation != null) {
                        iAnimation.stop();
                        iAnimation = null;
                        indicator.setRotate(0.0);
                        container.setRotate(0.0);
                        rArc.setStartAngle(0.0);
                        rArc.setLength(0.0);
                        rArc.setClip(null);
                        lArc.setStartAngle(90.0 + BASE_ARCS_GAP * arcMultiplier);
                        lArc.setLength(360.0 - BASE_ARCS_GAP * 2 * arcMultiplier);
                        lArc.setClip(null);
                    }
                    PseudoClasses.INDETERMINATE.setOn(indicator, false);
                    adjustProgress();
                })
                .executeNow()
        );
    }

    /**
     * Adjusts the progress bar to represent the value of {@link MFXProgressIndicator#progressProperty()}.
     * <p>
     * There are mainly three properties changing: the progress arc and the track lengths are changed, however,
     * the progress arc also needs to be rotated properly, otherwise it ends un being misaligned.
     * <p>
     * The three values are computed as follows:
     * <p> - the progress arc's length: {@code Math.max(1.0, 360.0 * progress)}
     * <p> - the progress arc's rotation: {@code 90.0 - newLength}
     * <p> - the track's length: {@code 360.0 - ARCS_GAP * 2 - progressArcLength}
     */
    protected void adjustProgress() {
        MFXProgressIndicator indicator = getSkinnable();
        double progress = indicator.getProgress();
        double arcLen = progress == 0.0 ? 0 : Math.max(1.0, 360.0 * progress);
        double arcDeg = progress == 0.0 ? 0 : 90.0 - arcLen;
        double trackLen = progress == 0.0 ? 360.0 : 360.0 - BASE_ARCS_GAP * 2 * arcMultiplier - arcLen;

        if (Animations.isPlaying(pAnimation)) pAnimation.stop();
        pAnimation = TimelineBuilder.build()
            .add(KeyFrames.of(Duration.ONE, lArc.visibleProperty(), trackLen > 0))
            .add(KeyFrames.of(DETERMINATE_DURATION, rArc.lengthProperty(), arcLen, DETERMINATE_CURVE))
            .add(KeyFrames.of(DETERMINATE_DURATION, rArc.startAngleProperty(), arcDeg, DETERMINATE_CURVE))
            .add(KeyFrames.of(DETERMINATE_DURATION, lArc.lengthProperty(), trackLen, DETERMINATE_CURVE))
            .getAnimation();
        pAnimation.play();
    }

    /**
     * Builds and plays the endless animation when the progress is indeterminate.
     * <p>
     * Too complex to explain into further details, this animation was by far the most complex I've ever coded, there is
     * a LOT going on. You can check the source code or: check its original source code
     * <a href="https://github.com/material-components/material-web/blob/main/progress/internal/_circular-progress.scss">here</a>,
     * or analyze with DevTools <a href="https://material-web.dev/components/progress/stories/">here</a>
     * (which is what I did).
     */
    protected void animateIndeterminate() {
        // Linear rotation
        Timeline linearRotation = TimelineBuilder.build()
            .add(KeyFrames.of(LINEAR_ROTATE_DURATION, getSkinnable().rotateProperty(), 360.0))
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();

        // Container rotation
        Timeline containerRotation = TimelineBuilder.build()
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.125), container.rotateProperty(), 135, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.250), container.rotateProperty(), 270, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.375), container.rotateProperty(), 405, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.500), container.rotateProperty(), 540, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.625), container.rotateProperty(), 675, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.750), container.rotateProperty(), 810, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION.multiply(0.875), container.rotateProperty(), 945, INDETERMINATE_CURVE))
            .add(KeyFrames.of(CONTAINER_ROTATE_DURATION, container.rotateProperty(), 1080, INDETERMINATE_CURVE))
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();

        // Arcs rotation
        Timeline lArcRotation = TimelineBuilder.build()
            .add(KeyFrames.of(Duration.ZERO, lArc.startAngleProperty(), 135.0, INDETERMINATE_CURVE))
            .add(KeyFrames.of(HALF_ARCS_ROTATE_DURATION, lArc.startAngleProperty(), 265.0, INDETERMINATE_CURVE))
            .add(KeyFrames.of(ARCS_ROTATE_DURATION, lArc.startAngleProperty(), 135.0, INDETERMINATE_CURVE))
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();
        Timeline rArcRotation = TimelineBuilder.build()
            .add(KeyFrames.of(Duration.ZERO, rArc.startAngleProperty(), 135.0, INDETERMINATE_CURVE))
            .add(KeyFrames.of(HALF_ARCS_ROTATE_DURATION, rArc.startAngleProperty(), 265.0, INDETERMINATE_CURVE))
            .add(KeyFrames.of(ARCS_ROTATE_DURATION, rArc.startAngleProperty(), 135.0, INDETERMINATE_CURVE))
            .setDelay(HALF_ARCS_ROTATE_DURATION)
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();

        if (Animations.isPlaying(iAnimation)) iAnimation.stop();
        iAnimation = ParallelBuilder.build()
            .add(linearRotation)
            .add(containerRotation)
            .add(lArcRotation)
            .add(rArcRotation)
            .getAnimation();
        iAnimation.play();
    }

    /**
     * Creates an un-managed {@link Arc} with the specified style classes.
     * <p>
     * Also, for the indeterminate animation to work properly, the arcs are clipped by using a {@link RectangleClip}.
     * <p>
     * Last but not least, this method also adds a listener with {@link #listeners(When[])} on the
     * {@link Arc#strokeWidthProperty()} to update the layout when it changes, because the clips sizes depend on the
     * stroke size.
     */
    protected Arc createArc(String... classes) {
        Arc a = new Arc();
        listeners(onInvalidated(a.strokeWidthProperty()).then(v -> container.requestLayout()));
        a.setClip(new RectangleClip());
        a.setManaged(false);
        a.getStyleClass().setAll(classes);
        return a;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return MIN_WIDTH;
    }

    @Override
    public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return MIN_HEIGHT;
    }

    @Override
    public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        arcMultiplier = 100.0 / (Math.max(w, h) / 2);
        container.resizeRelocate(x, y, w, h);
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * A simple extension of {@link Rectangle} which for convenience implements the
     * {@link Node#resizeRelocate(double, double, double, double)} method (yes, shapes do not define a behavior for it).
     */
    protected static class RectangleClip extends Rectangle {
        @Override
        public void resizeRelocate(double x, double y, double w, double h) {
            setX(x);
            setY(y);
            setWidth(w);
            setHeight(h);
        }
    }
}
