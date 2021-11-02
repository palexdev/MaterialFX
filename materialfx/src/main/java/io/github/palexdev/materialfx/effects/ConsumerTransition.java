package io.github.palexdev.materialfx.effects;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * A simple implementation of {@link Transition} that allows to specify
 * what to do when the {@link #interpolate(double)} method is called by using
 * a {@link Consumer}.
 */
public class ConsumerTransition extends Transition {
    //================================================================================
    // Properties
    //================================================================================
    private Consumer<Double> interpolateConsumer;

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sets the transition duration.
     */
    public ConsumerTransition setDuration(Duration duration) {
        this.setCycleDuration(duration);
        return this;
    }

    /**
     * Sets the transition duration in milliseconds.
     */
    public ConsumerTransition setDuration(double millis) {
        this.setCycleDuration(Duration.millis(millis));
        return this;
    }

    /**
     * Sets the consumer used by the {@link #interpolate(double)} method.
     */
    public ConsumerTransition setInterpolateConsumer(Consumer<Double> interpolateConsumer) {
        this.interpolateConsumer = interpolateConsumer;
        return this;
    }

    /**
     * Sets the transition's interpolator.
     */
    public ConsumerTransition setInterpolatorFluent(Interpolator interpolator) {
        this.setInterpolator(interpolator);
        return this;
    }

    /**
     * Sets the transition's delay.
     */
    public ConsumerTransition setDelayFluent(Duration duration) {
        this.setDelay(duration);
        return this;
    }

    /**
     * Calls {@link #setInterpolateConsumer(Consumer)} and then starts the animation.
     */
    public void playWithConsumer(Consumer<Double> interpolateConsumer) {
        setInterpolateConsumer(interpolateConsumer);
        this.play();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p></p>
     * Implementation to make use of a {@link Consumer}.
     */
    @Override
    protected void interpolate(double frac) {
        this.interpolateConsumer.accept(frac);
    }

    //================================================================================
    // Static Methods
    //================================================================================

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer);
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer and duration.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, Duration duration) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration);
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer and duration in milliseconds.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, double duration) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration);
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer, duration and interpolator.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, Duration duration, Interpolator interpolator) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator);
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer, duration in milliseconds and interpolator.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, double duration, Interpolator interpolator) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator);
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer, duration and interpolator.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, Duration duration, Interpolators interpolator) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator.toInterpolator());
    }

    /**
     * Creates a new {@code ConsumerTransition} with the given consumer, duration in milliseconds and interpolator.
     */
    public static ConsumerTransition of(Consumer<Double> interpolateConsumer, double duration, Interpolators interpolator) {
        return (new ConsumerTransition()).setInterpolateConsumer(interpolateConsumer).setDuration(duration).setInterpolatorFluent(interpolator.toInterpolator());
    }
}
