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
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.builders.bindings.DoubleBindingBuilder;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.Cubic;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.animations.motion.Motion;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static javafx.util.Duration.ZERO;

/**
 * One of the available skins for {@link MFXProgressIndicator}. This in particular will display the progress on two
 * bars: one is called "track" and takes all the space, thus representing a progress of 100%, the other is called "bar"
 * and it is sized according to the progress value; for example, a progress of 0.5 will result in the bar taking 50% of the
 * component's length.
 * <p></p>
 * Now, to be precise, the skin is much more complex because the design described by the Material Design 3 guidelines
 * <b>requires</b> this complexity. There are two main issues:
 * <p> 1) to replicate the indeterminate animation we need <b>two</b> bars
 * <p> 2) by design there is a gap of 4px between the bars and the track. This was very hard to implement, I have no idea
 * if there is another way to do it, but what I basically did was to split the track in two segments. When the progress
 * is determinate only one bar and one segment are used. When it's indeterminate things start to get a little tricky,
 * not only we need to use all the nodes, but it's necessary to also translate the segments properly.
 * <p>
 * Because of this complexity, I don't feel like going too much into detail, if you want to know more, read and try to
 * understand the source code.
 * <p></p>
 * Note that specs such as min sizes, animations durations and curves can be changed by extending the skin and overriding
 * the appropriate {@code protected static} fields.
 */
public class MFXLinearProgressIndicatorSkin extends MFXSkinBase<MFXProgressIndicator, BehaviorBase<MFXProgressIndicator>> {
    //================================================================================
    // Properties
    //================================================================================
    private final Region lSegment;
    private final Region rSegment;
    private final Region mBar; // Main bar
    private final Region sBar; // Secondary bar
    private final Region sPoint; // Stop point

    // Transforms
    protected double INIT_SCALE = 0.08;
    private final Scale mScale = new Scale(INIT_SCALE, 1.0);
    private final Scale sScale = new Scale(INIT_SCALE, 1.0);

    // Animations
    protected static Duration INDETERMINATE_DURATION = Duration.millis(1800);
    protected static Interpolator INDETERMINATE_CURVE_MAIN = new Cubic(0.65, 0, 0.35, 1);
    protected static Interpolator INDETERMINATE_CURVE_SECONDARY = new Cubic(0.5, 1, 0.89, 1);
    private Animation iAnimation; // Indeterminate animation

    protected static Duration DETERMINATE_DURATION = M3Motion.MEDIUM1;
    protected static Interpolator DETERMINATE_CURVE = new Cubic(0.4, 0, 0.6, 1);
    private Animation pAnimation;

    // Specs
    protected static double MIN_WIDTH = 100.0;
    protected static double MIN_HEIGHT = 4.0;
    protected static double SEGMENTS_GAP = 4.0;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLinearProgressIndicatorSkin(MFXProgressIndicator indicator) {
        super(indicator);

        // Init regions
        lSegment = createRegion("segment", "left");
        rSegment = createRegion("segment", "right");
        mBar = createRegion("bar", "main");
        mBar.getTransforms().add(mScale);
        sBar = createRegion("bar", "secondary");
        sBar.getTransforms().add(sScale);
        sPoint = createRegion("stop");

        // Make segments move/resize in accordance to the bars positions
        rSegment.translateXProperty().bind(mBar.boundsInParentProperty().map(b -> {
            double val = b.getMaxX() + SEGMENTS_GAP;
            if (val > indicator.getWidth()) {
                return sBar.getBoundsInParent().getMinX() - rSegment.getWidth() - SEGMENTS_GAP;
            }
            return val;
        }));
        lSegment.translateXProperty().bind(DoubleBindingBuilder.build()
            .setMapper(() -> {
                Bounds mBounds = mBar.getBoundsInParent();
                return mBounds.getMinX() - lSegment.getWidth() - SEGMENTS_GAP;
            })
            .addSources(mBar.boundsInParentProperty())
            .addSources(lSegment.widthProperty())
            .get()
        );
        listeners(
            onInvalidated(sBar.boundsInParentProperty())
                .then(sBounds -> {
                    Bounds mBounds = mBar.getBoundsInParent();
                    double val = Math.max(0, mBounds.getMinX() - sBounds.getMaxX() - SEGMENTS_GAP * 2);
                    lSegment.resize(val, indicator.getHeight());
                })
                .invalidating(mBar.boundsInParentProperty())
                .invalidating(indicator.heightProperty())
        ); // I don't usually add listeners right in the constructor for cleanliness, but this is a special case


        // Build and set clip
        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().bind(indicator.clipRadiusProperty());
        clip.arcHeightProperty().bind(indicator.clipRadiusProperty());
        clip.widthProperty().bind(indicator.widthProperty());
        clip.heightProperty().bind(indicator.heightProperty());
        indicator.setClip(clip);

        // Finalize init
        getChildren().addAll(lSegment, rSegment, mBar, sBar, sPoint);
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Creates the following bindings:
     * <p> - binds the left segment's {@link Region#visibleProperty()} to make it visible only if the progress is
     * indeterminate, {@link MFXProgressIndicator#indeterminateProperty()}
     * <p> - binds the secondary bar's {@link Region#visibleProperty()} to make it visible only if the progress is
     * indeterminate {@link MFXProgressIndicator#indeterminateProperty()}
     * <p> - binds the stop point's {@link Region#visibleProperty()} to make it visible only if the progress is not
     * indeterminate, {@link MFXProgressIndicator#indeterminateProperty()}, and the {@link MFXProgressIndicator#showStopPointProperty()}
     * is {@code true}
     * <p></p>
     * <p>
     * Adds the following listeners:
     * <p> - A listener on the {@link MFXProgressIndicator#progressProperty()} to: update the layout ensuring its correctness
     * when the progress switches between determinate/indeterminate, activate/deactivate the ':indeterminate' {@link PseudoClass},
     * run the appropriate animation according to the state, {@link #animateIndeterminate()} or {@link #adjustProgress(boolean)}
     * <p> - A listener on the {@link MFXProgressIndicator#widthProperty()} to re-run the animations, {@link #animateIndeterminate()}
     * or {@link #adjustProgress(boolean)}, because they depend on the component's width
     */
    private void addListeners() {
        MFXProgressIndicator indicator = getSkinnable();

        // Bindings
        lSegment.visibleProperty().bind(indicator.indeterminateProperty());
        sBar.visibleProperty().bind(indicator.indeterminateProperty());
        sPoint.visibleProperty().bind(BooleanBindingBuilder.build()
            .setMapper(() -> !indicator.isIndeterminate() && indicator.isShowStopPoint())
            .addSources(indicator.indeterminateProperty(), indicator.showStopPointProperty())
            .get()
        );

        // Listeners
        listeners(
            onInvalidated(indicator.progressProperty())
                .then(p -> {
                    indicator.requestLayout(); // To resize the left segment
                    boolean indeterminate = indicator.isIndeterminate();
                    if (indeterminate) {
                        PseudoClasses.INDETERMINATE.setOn(indicator, true);
                        animateIndeterminate();
                        return;
                    }
                    if (iAnimation != null) {
                        iAnimation.stop();
                        iAnimation = null;
                        mBar.setTranslateX(-indicator.getWidth());
                        mScale.setX(1);
                    }
                    PseudoClasses.INDETERMINATE.setOn(indicator, false);
                    adjustProgress(true);
                })
                .executeNow(() -> indicator.getWidth() > 0),
            onInvalidated(indicator.widthProperty())
                .then(w -> {
                    boolean indeterminate = indicator.isIndeterminate();
                    if (indeterminate) {
                        animateIndeterminate();
                        return;
                    }
                    adjustProgress(false);
                })
        );
    }

    /**
     * Adjusts the progress bar to represent the value of {@link MFXProgressIndicator#progressProperty()}.
     * <p>
     * You may think the length changes, but actually what changes is the bar's {@link Region#translateXProperty()}.
     * This is how this skin works, the bar is always sized to take all the space, but it's positioned outside the view
     * and translated according to the progress: {@code maxX - width}, where {@code width} is the component's width and
     * {@code maxX} is given by {@code width * progress}.
     * <p></p>
     * Last but not least, the {@code animated} parameter specifies whether to translate the bar with an animation or with
     * the setter method. An example of when this is not animated is when the component's width changes.
     */
    protected void adjustProgress(boolean animated) {
        MFXProgressIndicator indicator = getSkinnable();
        double w = indicator.getWidth();
        double barMaxX = w * indicator.getProgress();
        double barMinX = barMaxX - w;

        if (Animations.isPlaying(pAnimation)) pAnimation.stop();
        if (animated) {
            pAnimation = TimelineBuilder.build()
                .add(KeyFrames.of(DETERMINATE_DURATION, mBar.translateXProperty(), barMinX, DETERMINATE_CURVE))
                .getAnimation();
            pAnimation.play();
        } else {
            mBar.setTranslateX(barMinX);
        }
    }

    /**
     * Builds and plays the endless animation when the progress is indeterminate.
     * <p>
     * Too complex to explain into further details, you can check the source code or this link:
     * <a href="https://github.com/material-components/material-web/blob/main/progress/internal/_linear-progress.scss">Material Web</a>.
     * <p>
     * Note: the animation implemented here is slightly different from the one linked but that was my starting point.
     */
    protected void animateIndeterminate() {
        MFXProgressIndicator indicator = getSkinnable();
        double w = indicator.getWidth();
        double initX = -(w * INIT_SCALE) - SEGMENTS_GAP;

        // Main bar
        Animation mScaleA = TimelineBuilder.build()
            .add(KeyFrames.of(ZERO, mScale.xProperty(), INIT_SCALE))
            .add(KeyFrames.of(INDETERMINATE_DURATION.multiply(0.7), mScale.xProperty(), 0.66, INDETERMINATE_CURVE_MAIN))
            .add(KeyFrames.of(INDETERMINATE_DURATION, mScale.xProperty(), 0.08, Motion.EASE))
            .getAnimation();
        Animation mTranslateA = TimelineBuilder.build()
            .add(KeyFrames.of(ZERO, mBar.translateXProperty(), initX))
            .add(KeyFrames.of(INDETERMINATE_DURATION, mBar.translateXProperty(), w * 2.0, INDETERMINATE_CURVE_MAIN))
            .getAnimation();

        // Secondary bar
        Animation sScaleA = TimelineBuilder.build()
            .add(KeyFrames.of(ZERO, sScale.xProperty(), INIT_SCALE))
            .add(KeyFrames.of(INDETERMINATE_DURATION.multiply(0.45), sScale.xProperty(), 0.08))
            .add(KeyFrames.of(INDETERMINATE_DURATION.multiply(0.75), sScale.xProperty(), 0.9, INDETERMINATE_CURVE_SECONDARY))
            .add(KeyFrames.of(INDETERMINATE_DURATION, sScale.xProperty(), INIT_SCALE, Motion.EASE))
            .getAnimation();
        Animation sTranslateA = TimelineBuilder.build()
            .add(KeyFrames.of(ZERO, sBar.translateXProperty(), initX))
            .add(KeyFrames.of(INDETERMINATE_DURATION.multiply(0.55), sBar.translateXProperty(), initX))
            .add(KeyFrames.of(INDETERMINATE_DURATION, sBar.translateXProperty(), w * 1.2, INDETERMINATE_CURVE_SECONDARY))
            .getAnimation();

        // Parallel
        if (Animations.isPlaying(iAnimation)) iAnimation.stop();
        iAnimation = ParallelBuilder.build()
            .add(mScaleA)
            .add(sScaleA)
            .add(mTranslateA)
            .add(sTranslateA)
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();
        iAnimation.play();
    }

    /**
     * Creates an un-managed {@link Region} with the specified style classes.
     * <p>
     * This is used to build all the segments and bars, as well as the stop point.
     */
    protected Region createRegion(String... sClasses) {
        Region r = new Region();
        r.setManaged(false);
        r.getStyleClass().addAll(sClasses);
        return r;
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
        MFXProgressIndicator indicator = getSkinnable();
        boolean indeterminate = indicator.isIndeterminate();
        rSegment.resize(w * (indeterminate ? 1.5 : 1.0), h);
        mBar.resizeRelocate(0, 0, w, h);
        sBar.resizeRelocate(0, 0, w, h);
        sPoint.resizeRelocate(w - SEGMENTS_GAP, 0, SEGMENTS_GAP, SEGMENTS_GAP);

    }
}
