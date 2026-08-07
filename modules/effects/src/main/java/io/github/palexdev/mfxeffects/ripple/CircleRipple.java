/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxeffects.ripple;

import java.util.List;

import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.PauseBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.ConsumerTransition;
import io.github.palexdev.mfxeffects.animations.motion.Motion;
import io.github.palexdev.mfxeffects.beans.Offset;
import io.github.palexdev.mfxeffects.ripple.base.Ripple;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxeffects.ripple.base.RippleGeneratorBase;
import io.github.palexdev.mfxeffects.utils.ColorUtils;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.css.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/// Implementation of [Ripple] and the most common type of ripple as it also extends [Circle].
/// This complies with the new behavior and features of the new [MFXRippleGenerator].
///
/// It's responsible for animating the 'generation' phase, the 'release' phase, as well as animating in/out the
/// generator's background color. Most of the properties that customize the animations, such as their durations and
/// interpolation curves, are made protected so that they can be easily changed by extending the class, even inline.
/// When doing so, make sure that the animations are up to date with the new parameters by calling [#buildAnimations()].
///
///
/// #### Now the complex part: How does this work?
///
/// Well, I want to re-assert here too that the new implementation tries to stay as close as possible to the original
/// effect shown in Material Design, and achieving such accuracy has been really tough.
///
/// There are **4** animations in total that are built by [#buildAnimations()]:
///  1) The `radIn` animation is responsible for expanding the ripple, as well as translating it towards the
/// generator's center. I'll describe why later on when talking about the ripple sizes.
///  2) The `fadeIn` animation is responsible for increasing the ripple opacity to 1.0 over time and also
/// for animating the generator's background to the color specified by [RippleGenerator#backgroundColorProperty()].
///  3) The `fadeOut` animation is responsible for decreasing the ripple opacity to 0.0 over time, and also
/// for animating the generator's background, the color specified by [RippleGenerator#backgroundColorProperty()] will
/// have the `alpha` gradually set to 0.0.
///  4) The `pause` animation. This is a very important animation of type [PauseTransition].
///  This helps make things smoother and consistent across various input devices (touchscreens, trackpads, mouse).
///  If we don't use this animation, you will see an ugly effect because values will change very fast. And this is especially
///  true for touch devices (touchscreens or trackpads) that have a very low latency compared to the mouse.
///  To mitigate this, we ideally want the 'in' animations to play for at least some time before actually passing to the
///  'release' phase. By default, that time is set to `150ms`.
///  So, the actual duration of the 'pause' animations is given by `150` minus the current
///  time of the `radIn` animation. Consider the following two examples:
///     1) Let's say I use my mouse to generate the ripple. In my testings the average latency is between `20ms/50ms`.
///     In such case the 'pause' animation will have a duration between `130ms/100ms`
///     2) Now let's consider an example with a touch device. In my testings the average latency never goes up the `3ms/5ms`.
///     Which means that the 'release' phase will occur only after `140ms/150ms` (approx.)
///
/// The effect will always be the same, no matter the input device.
///
/// #### The next question is: Why separate 'in' animations for the radius and the opacity?
///
/// Pretty much the same reason as above, to make things smoother. When we have to pass to the 'release' phase, we have
/// to also stop the 'in' animations. However, we don't want to stop the radius animation as this would result in a
/// ugly/strange effect. We just want to stop the `fadeIn` animation as it would **conflict** with the
/// `fadeOut` that is going to be played next
///
/// ### Sizing and positioning
///
/// The algorithm responsible for determining the ripple size has also been updated to match more closely the one shown
/// by Material Design Guidelines. It's quite simple, there are now two sizes:
///
///  - The `initRad` is the radius of the ripple at the start, set just before the 'in' animations are played.
/// This is computed to be the maximum between the generator's width and height, and then multiplied by the [#INIT_RAD_MULTIPLIER]
/// factor that by default makes it the 20% of the found max
///  - The `targetRad` is the final radius the ripple will have once the `radIn` animation finishes.
/// This is computed as the **diagonal** size of the generator divided by 2 since we want the radius and plus 5 to
/// make it smoother. Now as you may guess, if the ripple is generated at the center of the generator, then it will cover
/// all of its surface. But if the position is near or at one of the corners, then the target size won't be enough.
/// And that's why the devs behind the ripple effect came up with a very nice solution. The ripple is also moved towards the
/// center, but in combination with the growth animation, the user won't perceive the translation, and instead he will
/// perceive it as just a big growth. The implications of such a trick are not to be underestimated, because this has a
/// huge impact on the `radIn` animation as smaller values also mean slower animation which is balanced by a small
/// duration and an ease interpolator. The other pro of such a trick is probably performance, now the framework doesn't need
/// to draw a huge circle anymore, despite this being unconfirmed, I strongly believe this will benefit performance!
public class CircleRipple extends Circle implements Ripple<Circle> {
    //================================================================================
    // Properties
    //================================================================================
    private final RippleGeneratorBase generator;

    protected double INIT_RAD_MULTIPLIER = 0.2;
    protected double initRad;
    protected double targetRad;

    protected double initX = 0;
    protected double initY = 0;

    protected Interpolator CURVE = Motion.EASE;
    protected Duration RAD_IN = Duration.millis(300);
    protected Duration FADE_IN = Duration.millis(100);
    protected Duration FADE_OUT = Duration.millis(300);
    protected Duration BG = Duration.millis(300);
    protected double MIN_IN_MILLIS = 150.0;

    private Animation radIn;
    private Animation fadeIn;
    private Animation fadeOut;
    private Animation pause;

    //================================================================================
    // Constructors
    //================================================================================
    public CircleRipple(RippleGeneratorBase generator) {
        this.generator = generator;
        getStyleClass().add("ripple");
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Determines the sizes by calling [#determineRippleSize()] and finally initializes the animations by calling [#buildAnimations()].
    public void init() {
        determineRippleSize();
        buildAnimations();
    }

    /// Responsible for building all the animations described by [CircleRipple].
    ///
    /// Note that the background animations will be built and added only if [RippleGenerator#animateBackgroundProperty()] is true.
    protected void buildAnimations() {
        KeyFrame radInKF = KeyFrames.of(RAD_IN, radiusProperty(), targetRad, CURVE);
        KeyFrame xInKF = KeyFrames.of(RAD_IN, centerXProperty(), generator.getLayoutBounds().getCenterX(), CURVE);
        KeyFrame yInKF = KeyFrames.of(RAD_IN, centerYProperty(), generator.getLayoutBounds().getCenterY(), CURVE);
        radIn = TimelineBuilder.build()
            .add(radInKF)
            .add(xInKF)
            .add(yInKF)
            .getAnimation();

        boolean animateBackground = generator.doAnimateBackground();
        Color bgColor = generator.getBackgroundColor();
        KeyFrame fadeInKF = KeyFrames.of(FADE_IN, opacityProperty(), 1.0);
        KeyFrame fadeOutKF = KeyFrames.of(FADE_OUT, opacityProperty(), 0.0);
        if (animateBackground) {
            fadeIn = ParallelBuilder.build()
                .add(fadeInKF)
                .add(() -> ConsumerTransition.of(dt -> {
                    double alpha = dt * bgColor.getOpacity();
                    Color color = ColorUtils.atAlpha(bgColor, alpha);
                    generator.setBackground(Background.fill(color));
                }, BG).setInterpolatorFluent(CURVE))
                .getAnimation();

            fadeOut = ParallelBuilder.build()
                .add(fadeOutKF)
                .add(() -> ConsumerTransition.of(dt -> {
                    double bgAlpha = bgColor.getOpacity();
                    double alpha = bgAlpha - (dt * bgAlpha);
                    Color color = ColorUtils.atAlpha(bgColor, alpha);
                    generator.setBackground(Background.fill(color));
                }, BG).setInterpolatorFluent(CURVE))
                .getAnimation();
        } else {
            fadeIn = TimelineBuilder.build()
                .add(fadeInKF)
                .getAnimation();

            fadeOut = TimelineBuilder.build()
                .add(fadeOutKF)
                .getAnimation();
        }
    }

    /// Determines the ripple's initial radius and target radius as described by [CircleRipple].
    protected void determineRippleSize() {
        double prefRadius = getPrefRadius();
        if (prefRadius > 0.0) {
            initRad = prefRadius * INIT_RAD_MULTIPLIER;
            targetRad = prefRadius;
            return;
        }

        double w = generator.getWidth();
        double h = generator.getHeight();
        double diag = new Offset(w, h).getDistance();
        initRad = Math.floor(Math.max(w, h) * INIT_RAD_MULTIPLIER);
        targetRad = diag / 2 + 5;
    }

    /// The generator automatically calls the [#position(double, double)] method specified by the public API
    /// when a request for a new effect is sent. However, we still don't want to change the position at such a time.
    /// We first need to stop the 'out' and 'pause' animations.
    ///
    /// This is responsible for setting the opacity to `0.0`, the fill to [MFXRippleGenerator#rippleColorProperty()],
    /// the radius to `initRad` computed previously by [#determineRippleSize()], and finally set the [#centerXProperty()]
    /// and [#centerYProperty()] properties to the requested position.
    /// The two values are stored by [#position(double, double)] and then actually used here.
    protected void doPosition() {
        setOpacity(0.0);
        setFill(generator.getRippleColor());
        setRadius(initRad);
        setCenterX(initX);
        setCenterY(initY);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Circle toNode() {
        return this;
    }

    /// This just saves the new ripple position as class variables, see [#doPosition()].
    @Override
    public void position(double x, double y) {
        initX = x;
        initY = y;
    }

    /// Responsible for playing the animations for the 'generation' phase.
    ///
    /// First, we need to stop both the 'pause' and 'out' animations, then we reposition the ripple with
    /// [#doPosition()], and finally, we can start both the `fadeIn` and `radIn` animations by
    /// using [Animation#playFromStart()].
    @Override
    public void playIn() {
        if (Animations.isPlaying(pause)) pause.stop();
        fadeOut.stop();
        doPosition();
        fadeIn.playFromStart();
        radIn.playFromStart();
    }

    @Override
    public void playOut() {
        double ct = radIn.getCurrentTime().toMillis();
        double delay = MIN_IN_MILLIS - ct;
        if (delay > 0) {
            if (pause != null) pause.stop();
            pause = PauseBuilder.build()
                .setDuration(delay)
                .setOnFinished(e -> {
                    fadeIn.stop();
                    fadeOut.playFromStart();
                })
                .getAnimation();
            pause.play();
            return;
        }
        fadeOut.playFromStart();
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty prefRadius = new SimpleStyleableDoubleProperty(
        StyleableProperties.RADIUS,
        this,
        "prefRadius",
        0.0
    ) {
        @Override
        protected void invalidated() {
            init();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    public double getPrefRadius() {
        return prefRadius.get();
    }

    public StyleableDoubleProperty prefRadiusProperty() {
        return prefRadius;
    }

    public void setPrefRadius(double prefRadius) {
        this.prefRadius.set(prefRadius);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<CircleRipple> FACTORY = new StyleablePropertyFactory<>(Circle.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<CircleRipple, Number> RADIUS =
            FACTORY.createSizeCssMetaData(
                "-mfx-radius",
                CircleRipple::prefRadiusProperty,
                0.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Circle.getClassCssMetaData(),
                RADIUS
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
