package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.AnimationsData;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import javafx.animation.*;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Utility class to easily build animations of any sort. Designed with fluent api.
 */
public class AnimationUtils {

    //================================================================================
    // Constructors
    //================================================================================
    private AnimationUtils() {
    }

    //================================================================================
    // Static Methods
    //================================================================================

    /**
     * Temporarily disables the given node for the specified duration.
     */
    public static void disableTemporarily(Duration duration, Node node) {
        node.setDisable(true);
        AnimationUtils.PauseBuilder.build()
                .setOnFinished(event -> node.setDisable(false))
                .setDuration(duration)
                .getAnimation()
                .play();
    }

    /**
     * Calls {@link #disableTemporarily(Duration, Node)} by converting the given millis value
     * with {@link Duration#millis(double)}.
     */
    public static void disableTemporarily(double millis, Node node) {
        disableTemporarily(Duration.millis(millis), node);
    }

    /**
     * Executes the given onFinished action after the specified duration of time.
     * (Uses a PauseTransition)
     */
    public static void executeLater(Duration duration, EventHandler<ActionEvent> onFinished) {
        PauseBuilder.build().setDuration(duration).setOnFinished(onFinished).getAnimation().play();
    }

    /**
     * Calls {@link #executeLater(Duration, EventHandler)} by converting the given millis value
     * with {@link Duration#millis(double)}.
     */
    public static void executeLater(double millis, EventHandler<ActionEvent> onFinished) {
        executeLater(Duration.millis(millis), onFinished);
    }

    /**
     * Sets the text of the given {@link Labeled} with a fade out/fade in transition.
     *
     * @param labeled  the labeled control to change the text to
     * @param duration the fade in and fade out speed
     * @param nexText  the new text to set
     * @return an instance of {@link AbstractBuilder}
     */
    public static AbstractBuilder transitionText(Labeled labeled, Duration duration, String nexText) {
        return SequentialBuilder.build()
                .hide(AnimationsData.of(labeled, duration, event -> labeled.setText(nexText)))
                .show(AnimationsData.of(labeled, duration));
    }

    /**
     * Calls {@link #transitionText(Labeled, Duration, String)} by converting the given millis value
     * with {@link Duration#millis(double)}.
     */
    public static AbstractBuilder transitionText(Labeled labeled, double millis, String nexText) {
        return transitionText(labeled, Duration.millis(millis), nexText);
    }

    /**
     * Sets the text of the given {@link Text} with a fade out/fade in transition.
     *
     * @param text     the text control to change the text to
     * @param duration the fade in and fade out speed
     * @param nexText  the new text to set
     * @return an instance of {@link AbstractBuilder}
     */
    public static AbstractBuilder transitionText(Text text, Duration duration, String nexText) {
        return SequentialBuilder.build()
                .hide(AnimationsData.of(text, duration, event -> text.setText(nexText)))
                .show(AnimationsData.of(text, duration));
    }

    /**
     * Calls {@link #transitionText(Text, Duration, String)} by converting the given millis value
     * with {@link Duration#millis(double)}.
     */
    public static AbstractBuilder transitionText(Text text, double millis, String nexText) {
        return transitionText(text, Duration.millis(millis), nexText);
    }

    /**
     * @return true if the given animation status is RUNNING, otherwise false
     */
    public static boolean isPlaying(Animation animation) {
        return animation.getStatus() == Status.RUNNING;
    }

    /**
     * @return true if the given animation status is PAUSED, otherwise false
     */
    public static boolean isPaused(Animation animation) {
        return animation.getStatus() == Status.PAUSED;
    }

    //================================================================================
    // Builders
    //================================================================================

    /**
     * Common base class for {@link ParallelBuilder} and {@link SequentialBuilder}.
     * <p></p>
     * This builder, designed with fluent api, allows you to create simple and complex animations with just a few lines of code.
     * <p></p>
     * The builder keeps the reference of the "main" animation (depending on the subclass can be ParallelTransition or SequentialTransition, in
     * the AbstractBuilder the type is a generic {@link Animation}), and defines and abstract method that subclasses must implement
     * to properly add animations to the "main".
     */
    public static abstract class AbstractBuilder {
        //================================================================================
        // Properties
        //================================================================================
        protected Animation animation;

        //================================================================================
        // Abstract Methods
        //================================================================================

        /**
         * Adds the given animation to the "main" animation.
         */
        protected abstract void addAnimation(Animation animation);

        /**
         * @return the "main" animation instance
         */
        public abstract Animation getAnimation();

        //================================================================================
        // Methods
        //================================================================================
        protected void init(Animation animation) {
            this.animation = animation;
        }

        /**
         * Adds the given animation to the "main" animation by calling {@link #addAnimation(Animation)}.
         */
        public AbstractBuilder add(Animation animation) {
            addAnimation(animation);
            return this;
        }

        /**
         * Sets the given onFinished action to the given animation and then adds it to the
         * "main" animation by calling {@link #addAnimation(Animation)}.
         */
        public AbstractBuilder add(Animation animation, EventHandler<ActionEvent> onFinished) {
            animation.setOnFinished(onFinished);
            addAnimation(animation);
            return this;
        }

        /**
         * For each given node builds and adds an animation that disables the node
         * after the given duration of time.
         *
         * @param duration the time after which the nodes are disabled
         */
        public AbstractBuilder disable(Duration duration, Node... nodes) {
            for (Node node : nodes) {
                addAnimation(
                        PauseBuilder.build()
                                .setDuration(duration)
                                .setOnFinished(end -> node.setDisable(true))
                                .getAnimation()
                );
            }
            return this;
        }

        /**
         * For each given node builds and adds an animation that enables the node
         * after the given duration of time.
         *
         * @param duration the duration after which the nodes are enabled
         */
        public AbstractBuilder enable(Duration duration, Node... nodes) {
            for (Node node : nodes) {
                addAnimation(
                        PauseBuilder.build()
                                .setDuration(duration)
                                .setOnFinished(end -> node.setDisable(false))
                                .getAnimation()
                );
            }
            return this;
        }

        /**
         * For each given window builds and adds an animation that hides the window by fading it out.
         *
         * @param duration the fade animation speed
         */
        public AbstractBuilder hide(Duration duration, Window... windows) {
            for (Window window : windows) {
                Timeline timeline = new Timeline(
                        new KeyFrame(duration, new KeyValue(window.opacityProperty(), 0))
                );
                addAnimation(timeline);
            }
            return this;
        }

        /**
         * Calls {@link #hide(Duration, Window...)} by converting the given millis value
         * with {@link Duration#millis(double)}.
         */
        public AbstractBuilder hide(double millis, Window... windows) {
            return hide(Duration.millis(millis), windows);
        }

        /**
         * For each given node builds and adds an animation that hides the node by fading it out.
         *
         * @param duration the fade animation speed
         */
        public AbstractBuilder hide(Duration duration, Node... nodes) {
            for (Node node : nodes) {
                addAnimation(MFXAnimationFactory.FADE_OUT.build(node, duration.toMillis()));
            }
            return this;
        }

        /**
         * Calls {@link #hide(Duration, Node...)} by converting the given millis value
         * with {@link Duration#millis(double)}.
         */
        public AbstractBuilder hide(double millis, Node... nodes) {
            return hide(Duration.millis(millis), nodes);
        }

        /**
         * Creates and adds a fade out animation for each given {@link AnimationsData}.
         */
        public final AbstractBuilder hide(AnimationsData... data) {
            for (AnimationsData animData : data) {
                Animation animation = MFXAnimationFactory.FADE_OUT.build(animData.node(), animData.duration().toMillis());
                animation.setOnFinished(animData.onFinished());
                addAnimation(animation);
            }
            return this;
        }

        /**
         * For each given window builds and adds an animation that shows the window by fading it in.
         *
         * @param duration the fade animation speed
         */
        public AbstractBuilder show(Duration duration, Window... windows) {
            for (Window window : windows) {
                Timeline timeline = new Timeline(
                        new KeyFrame(duration, new KeyValue(window.opacityProperty(), 1.0))
                );
                addAnimation(timeline);
            }
            return this;
        }

        /**
         * Calls {@link #show(Duration, Window...)} by converting the given millis value
         * with {@link Duration#millis(double)}.
         */
        public AbstractBuilder show(double millis, Window... windows) {
            return show(Duration.millis(millis), windows);
        }

        /**
         * For each given node builds and adds an animation that shows the node by fading it in.
         *
         * @param duration the fade animation speed
         */
        public AbstractBuilder show(Duration duration, Node... nodes) {
            for (Node node : nodes) {
                addAnimation(MFXAnimationFactory.FADE_IN.build(node, duration.toMillis()));
            }
            return this;
        }

        /**
         * Calls {@link #show(Duration, Node...)} by converting the given millis value
         * with {@link Duration#millis(double)}.
         */
        public AbstractBuilder show(double millis, Node... nodes) {
            return show(Duration.millis(millis), nodes);
        }

        /**
         * Creates and adds a fade in animation for each given {@link AnimationsData}.
         */
        public final AbstractBuilder show(AnimationsData... data) {
            for (AnimationsData animData : data) {
                Animation animation = MFXAnimationFactory.FADE_IN.build(animData.node(), animData.duration().toMillis());
                animation.setOnFinished(animData.onFinished());
                addAnimation(animation);
            }
            return this;
        }

        /**
         * Sets the action to perform when the "main" animation ends.
         */
        public AbstractBuilder setOnFinished(EventHandler<ActionEvent> onFinished) {
            animation.setOnFinished(onFinished);
            return this;
        }

        /**
         * Sets the "main" animation delay.
         */
        public AbstractBuilder setDelay(Duration delay) {
            animation.setDelay(delay);
            return this;
        }
    }

    /**
     * Implementation of {@link AbstractBuilder} that uses a {@link SequentialTransition} as "main" animation.
     */
    public static class SequentialBuilder extends AbstractBuilder {
        //================================================================================
        // Properties
        //================================================================================
        private final SequentialTransition sequentialTransition = new SequentialTransition();

        //================================================================================
        // Constructors
        //================================================================================
        public SequentialBuilder() {
            init(sequentialTransition);
        }

        //================================================================================
        // Static Methods
        //================================================================================

        /**
         * @return a new SequentialBuilder instance. Equivalent to calling the constructor,
         * it's just a way to omit the new keyword
         */
        public static SequentialBuilder build() {
            return new SequentialBuilder();
        }

        //================================================================================
        // Override Methods
        //================================================================================
        @Override
        protected void addAnimation(Animation animation) {
            sequentialTransition.getChildren().add(animation);
        }

        @Override
        public SequentialTransition getAnimation() {
            return sequentialTransition;
        }
    }

    /**
     * Implementation of {@link AbstractBuilder} that uses a {@link ParallelTransition} as "main" animation.
     */
    public static class ParallelBuilder extends AbstractBuilder {
        //================================================================================
        // Properties
        //================================================================================
        private final ParallelTransition parallelTransition = new ParallelTransition();

        //================================================================================
        // Constructors
        //================================================================================
        public ParallelBuilder() {
            init(parallelTransition);
        }

        //================================================================================
        // Static Methods
        //================================================================================

        /**
         * @return a new ParallelBuilder instance. Equivalent to calling the constructor,
         * it's just a way to omit the new keyword
         */
        public static ParallelBuilder build() {
            return new ParallelBuilder();
        }

        //================================================================================
        // Override Methods
        //================================================================================
        @Override
        protected void addAnimation(Animation animation) {
            parallelTransition.getChildren().add(animation);
        }

        @Override
        public ParallelTransition getAnimation() {
            return parallelTransition;
        }
    }

    /**
     * Builder class to easily create a {@link PauseTransition} with fluent api.
     */
    public static class PauseBuilder {
        //================================================================================
        // Properties
        //================================================================================
        private final PauseTransition pauseTransition = new PauseTransition();

        //================================================================================
        // Static Methods
        //================================================================================

        /**
         * @return a new PauseBuilder instance. Equivalent to calling the constructor,
         * it's just a way to omit the new keyword
         */
        public static PauseBuilder build() {
            return new PauseBuilder();
        }

        //================================================================================
        // Methods
        //================================================================================

        /**
         * Sets the pause transition duration.
         */
        public PauseBuilder setDuration(Duration value) {
            pauseTransition.setDuration(value);
            return this;
        }

        /**
         * Calls {@link #setDuration(Duration)} by converting the given millis value
         * with {@link Duration#millis(double)}.
         */
        public PauseBuilder setDuration(double millis) {
            pauseTransition.setDuration(Duration.millis(millis));
            return this;
        }

        /**
         * Sets the action to perform when the pause transition ends.
         */
        public PauseBuilder setOnFinished(EventHandler<ActionEvent> value) {
            pauseTransition.setOnFinished(value);
            return this;
        }

        /**
         * @return the instance of the PauseTransition
         */
        public PauseTransition getAnimation() {
            return pauseTransition;
        }
    }
}
