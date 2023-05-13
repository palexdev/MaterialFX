package io.github.palexdev.mfxeffects.ripple;

import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.ParallelBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.PauseBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.ConsumerTransition;
import io.github.palexdev.mfxeffects.animations.motion.Motion;
import io.github.palexdev.mfxeffects.beans.Offset;
import io.github.palexdev.mfxeffects.beans.Size;
import io.github.palexdev.mfxeffects.ripple.base.Ripple;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxeffects.ripple.base.RippleGeneratorBase;
import io.github.palexdev.mfxeffects.utils.ColorUtils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Implementation of {@link Ripple} and most common type of ripple as it also extends {@link Circle}.
 * This complies with the new behavior and features of the new {@link MFXRippleGenerator}.
 * <p></p>
 * It's responsible for animating the 'generation' phase, the 'release' phase, as well as animating in/out the
 * generator's background color. Most of the properties that customize the animations, such as their durations and
 * interpolation curves, are made protected so that they can be easily changed by extending the class, even inline.
 * When doing so, make sure that the animations are up-to-date with the new parameters by calling {@link #buildAnimations()}.
 * <p></p>
 * <b>Now the complex part: How this works?</b>
 * <p>
 * Well, I want to re-assert here too, that the new implementation tries to stay as close as possible to the original
 * effect shown in Material Design, and accomplishing such accuracy has been really tough.
 * <p>
 * There are <b>4</b> animations in total that are built by {@link #buildAnimations()}:
 * <p> 1) The {@code radIn} animation is responsible for expanding the ripple, as well as translating it towards the
 * generator's center. I'll describe why later on when talking about the ripple sizes.
 * <p> 2) The {@code fadeIn} animation is responsible for increasing the ripple opacity to 1.0 over time, and also
 * for animating the generator's background to the color specified by {@link RippleGenerator#backgroundColorProperty()}
 * <p> 3) The {@code fadeOut} animation is responsible for decreasing the ripple opacity to 0.0 over time, and also
 * for animating the generator's background, the color specified by {@link RippleGenerator#backgroundColorProperty()} will
 * have the {@code alpha} gradually set to 0.0
 * <p> 4) The {@code pause} animation. This is a very important animation of type {@link PauseTransition}.
 * This helps make things smoother and consistent across various input devices (touchscreens, trackpads, mouse).
 * If we don't use this animation you will see an ugly effect because values will change very fast. And this is especially
 * true for touch devices (touchscreens or trackpads) that have a very low latency compared to mouse. To mitigate this,
 * we ideally want the 'in' animations to play for at least some time before actually passing to the 'release' phase,
 * by default that time is of 150ms. So, the actual duration of this 'pause' animations is given by 150 minus the current
 * time of the {@code radIn} animation. Consider the following two examples:
 * <p> 1) Let's say I use my mouse to generate the ripple. In my testings the average latency is between 20ms/50ms.
 * In such case the 'pause'  animation will have a duration between 130ms/100ms
 * <p> 2) Now let's consider an example with a touch device. In my testings the average latency never goes up the 3ms/5ms.
 * Which means that the 'release' phase will occur only after 140ms/150ms (approx.)
 * <p>
 * The effect will always be the same, no matter the input device.
 * <p></p>
 * <b>The next question is: Why separate 'in' animations for the radius and the opacity?</b>
 * <p>
 * Pretty much the same reason as above, to make things smoother. When we have to pass to the 'release' phase, we have
 * to also stop the 'in' animations. However, we don't want to stop the radius animation as this would result in a
 * ugly/strange effect. We just want to stop the {@code fadeIn} animation as it would <b>conflict</b> with the
 * {@code fadeOut} that is going to be played next
 * <p></p>
 * Last but not least:  <b>the ripple sizing and positioning</b>.
 * <p>
 * The algorithm responsible for determining the ripple size has also been updated to match more closely the one shown
 * by Material Design Guidelines. It's quite simple, there are now two sizes:
 * <p> - The {@code initRad} is the radius of the ripple at the start, set just before the 'in' animations are played.
 * This is computed to be tha maximum between the generator's width and height, and then multiplied by the {@link #INIT_RAD_MULTIPLIER}
 * factor that by default makes it the 20% of the found max
 * <p> - The {@code targetRad} is the final radius the ripple will have once the {@code radIn} animation finishes.
 * This is computed as the <b>diagonal</b> size of the generator divided by 2 since we want the radius, and plus 5 to
 * make it smoother. Now as you may guess, if the ripple is generated at the center of the generator than it will cover
 * all of its surface. But if the position is near or at one of the corners than the target size won't be enough.
 * And that's why the devs behind the ripple effect came up with a very nice solution. The ripple is also moved towards the
 * center, but in combination with the growth animation, the user won't perceive the translation, and instead he will
 * perceive it as just a big growth. The implications of such trick are not to be underestimated, because this has a
 * huge impact on the {@code radIn} animation as smaller values also mean slower animation which is balanced by a small
 * duration and an ease interpolator. The other pro of such trick is probably performance, now the framework doesn't need
 * to draw a huge circle anymore, despite this being unconfirmed I strongly believe this will benefit performance!
 */
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
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sets the ripple's fill to {@link RippleGenerator#rippleColorProperty()}, determines the sizes by calling
     * {@link #determineRippleSize()} and finally initializes the animations by calling {@link #buildAnimations()}
     */
    public void init() {
        setFill(generator.getRippleColor());
        determineRippleSize();
        buildAnimations();
    }

    /**
     * Responsible for building all the animations described by {@link CircleRipple}.
     * <p>
     * Note that the background animations will be built and added only if {@link RippleGenerator#animateBackgroundProperty()}
     * is true.
     */
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

    /**
     * Determines the ripple's initial radius and target radius as described by {@link CircleRipple}.
     */
    protected void determineRippleSize() {
        Size pref = generator.getRipplePrefSize();
        if (!Size.invalid().equals(pref)) {
            initRad = 0;
            targetRad = Math.max(pref.getWidth(), pref.getWidth());
            return;
        }

        double w = generator.getWidth();
        double h = generator.getHeight();
        double diag = new Offset(w, h).getDistance();
        initRad = Math.floor(Math.max(w, h) * INIT_RAD_MULTIPLIER);
        targetRad = diag / 2 + 5;
    }

    /**
     * The {@link #position(double, double)} method specified by the public API is automatically called by the generator
     * when a request for a new effect is sent. However, we still don't want to change the position at such time, we first
     * need to stop the 'out' and 'pause' animations.
     * <p>
     * This is responsible for setting the opacity to 0.0, the radius to {@code initRad} computed previously by {@link #determineRippleSize()},
     * and finally set the {@link #centerXProperty()} and {@link #centerYProperty()} properties to the requested position,
     * the two values are stored by {@link #position(double, double)} and then actually used here.
     */
    protected void doPosition() {
        setOpacity(0.0);
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

    /**
     * This just saves the new ripple position as class variables, see {@link #doPosition()}.
     */
    @Override
    public void position(double x, double y) {
        initX = x;
        initY = y;
    }

    /**
     * Responsible for playing the animations for the 'generation' phase.
     * <p></p>
     * First of all we need to stop both the 'pause' and 'out' animations, then we reposition the ripple with
     * {@link #doPosition()} and finally we can start both the {@code fadeIn} and {@code radIn} animations by
     * using {@link Animation#playFromStart()}.
     */
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
}
