package it.paprojects.materialfx.effects;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

import static it.paprojects.materialfx.effects.MFXDepthManager.shadowOf;

public class RippleGenerator extends Group {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "ripple-generator";
    private static final StyleablePropertyFactory<RippleGenerator> FACTORY = new StyleablePropertyFactory<>(Group.getClassCssMetaData());

    private final Region region;

    private RippleClipType rippleClipType = RippleClipType.RECTANGLE;
    private DepthLevel level = null;
    private boolean animateBackground = true;
    private final Interpolator rippleInterpolator = Interpolator.SPLINE(0.0825, 0.3025, 0.0875, 0.9975);
    //private final Interpolator rippleInterpolator = Interpolator.SPLINE(0.1, 0.50, 0.3, 0.85);
    private final ObjectProperty<Color> rippleColor = new StyleableObjectProperty<>(Color.ROYALBLUE)
    {
        @Override public CssMetaData getCssMetaData() { return StyleableProperties.RIPPLE_COLOR; }
        @Override public Object getBean() { return this; }
        @Override public String getName() { return "rippleColor"; }
    };
    private final StyleableDoubleProperty rippleRadius = new SimpleStyleableDoubleProperty(
            StyleableProperties.RIPPLE_RADIUS,
            this,
            "rippleRadius",
            10.0
    );
    private final ObjectProperty<Duration> inDuration = new SimpleObjectProperty<>(Duration.millis(700));
    private final ObjectProperty<Duration> outDuration = new SimpleObjectProperty<>(inDuration.get().divide(2));

    private double generatorCenterX = 0.0;
    private double generatorCenterY = 0.0;

    //================================================================================
    // Constructors
    //================================================================================
    public RippleGenerator(Region region) {
        this.region = region;
        getStyleClass().add(STYLE_CLASS);
        inDuration.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                outDuration.set(newValue.divide(2));
            }
        });
        outDuration.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                inDuration.set(newValue.multiply(2));
            }
        });
    }

    public RippleGenerator(Region region, DepthLevel shadowLevel) {
        this(region);
        this.level = shadowLevel;
    }

    public RippleGenerator(Region region, RippleClipType rippleClipType) {
        this(region);
        this.rippleClipType = rippleClipType;
    }

    public RippleGenerator(Region region, DepthLevel shadowLevel, RippleClipType rippleClipType) {
        this(region, shadowLevel);
        this.rippleClipType = rippleClipType;
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Creates a new {@code Ripple} at the specified coordinates.
     * <p>
     * Each {@code Ripple} is a new instance, this allows multiple ripples to be generated at the same time.
     */
    public void createRipple() {
        final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
        getChildren().add(ripple);

        if (animateBackground) {
            Rectangle fillRect = new Rectangle(region.getWidth(), region.getHeight());
            fillRect.setFill(rippleColor.get());
            fillRect.setOpacity(0);
            getChildren().add(0, fillRect);

            KeyValue keyValueIn = new KeyValue(fillRect.opacityProperty(), 0.3);
            KeyValue keyValueOut = new KeyValue(fillRect.opacityProperty(), 0);
            KeyFrame keyFrameIn = new KeyFrame(inDuration.get(), keyValueIn);
            KeyFrame keyFrameOut = new KeyFrame(outDuration.get(), keyValueOut);
            ripple.inAnimation.getKeyFrames().add(keyFrameIn);
            ripple.outAnimation.getKeyFrames().add(keyFrameOut);
        }

        ripple.parallelTransition.setOnFinished(event -> getChildren().remove(ripple));
        ripple.parallelTransition.play();

    }

    public void setGeneratorCenterX(double generatorCenterX) {
        this.generatorCenterX = generatorCenterX;
    }

    public void setGeneratorCenterY(double generatorCenterY) {
        this.generatorCenterY = generatorCenterY;
    }

    public void setAnimateBackground(boolean animateBackground) {
        this.animateBackground = animateBackground;
    }

    public void setRippleClipType(RippleClipType rippleClipType) {
        this.rippleClipType = rippleClipType;
    }

    public Color getRippleColor() {
        return rippleColor.get();
    }

    public final ObjectProperty<Color> rippleColorProperty() {
        return rippleColor;
    }

    public void setRippleColor(Color rippleColor) {
        this.rippleColor.set(rippleColor);
    }

    public double getRippleRadius() {
        return rippleRadius.get();
    }

    public StyleableDoubleProperty rippleRadiusProperty() {
        return rippleRadius;
    }

    public void setRippleRadius(double rippleRadius) {
        this.rippleRadius.set(rippleRadius);
    }

    public Duration getInDuration() {
        return inDuration.get();
    }

    public ObjectProperty<Duration> inDurationProperty() {
        return inDuration;
    }

    public void setInDuration(Duration inDuration) {
        this.inDuration.set(inDuration);
    }

    public Duration getOutDuration() {
        return outDuration.get();
    }

    public ObjectProperty<Duration> outDurationProperty() {
        return outDuration;
    }

    public void setOutDuration(Duration outDuration) {
        this.outDuration.set(outDuration);
    }

    /**
     * This class defines a ripple as a {@code Circle} and contains all it's properties, mainly
     * it builds the animation of the ripple.
     */
    private class Ripple extends Circle {
        //================================================================================
        // Properties
        //================================================================================
        private final int shadowDelta = 1;

        private final Timeline inAnimation = new Timeline();
        private final Timeline outAnimation = new Timeline();
        private final Timeline shadowAnimation = new Timeline();
        private final SequentialTransition sequentialTransition = new SequentialTransition();
        private final ParallelTransition parallelTransition = new ParallelTransition();

        //================================================================================
        // Constructors
        //================================================================================
        private Ripple(double centerX, double centerY) {
            super(centerX, centerY, 0, Color.TRANSPARENT);
            setFill(rippleColor.get());
            setClip(rippleClipType.buildClip(region));
            buildAnimation();
        }

        //================================================================================
        // Methods
        //================================================================================

        /**
         * Build the ripple's animation
         */
        private void buildAnimation() {
            KeyValue keyValue1 = new KeyValue(radiusProperty(), rippleRadius.get());
            KeyValue keyValue2 = new KeyValue(opacityProperty(), 1.0);
            KeyFrame keyFrame1 = new KeyFrame(inDuration.get(), keyValue1);
            KeyFrame keyFrame2 = new KeyFrame(inDuration.get(), keyValue2);
            inAnimation.getKeyFrames().addAll(keyFrame1, keyFrame2);

            KeyValue keyValue3 = new KeyValue(radiusProperty(), rippleRadius.get() * 2);
            KeyValue keyValue4 = new KeyValue(opacityProperty(), 0.0);
            KeyFrame keyFrame3 = new KeyFrame(outDuration.get(), keyValue3);
            KeyFrame keyFrame4 = new KeyFrame(outDuration.get(), keyValue4);
            outAnimation.getKeyFrames().addAll(keyFrame3, keyFrame4);

            if (level != null) {
                Control control = (Control) region;
                DropShadow shadowEffect = (DropShadow) ((Control) control.getSkin().getSkinnable()).getEffect();
                DropShadow startShadow = shadowOf(level);
                DropShadow endShadow = shadowOf(level, shadowDelta);

                // Spread
                KeyValue keyValue5 = new KeyValue(shadowEffect.spreadProperty(), endShadow.getSpread(), Interpolator.LINEAR);
                KeyValue keyValue6 = new KeyValue(shadowEffect.spreadProperty(), startShadow.getSpread(), Interpolator.LINEAR);
                //Radius
                KeyValue keyValue7 = new KeyValue(shadowEffect.radiusProperty(), endShadow.getRadius(), Interpolator.LINEAR);
                KeyValue keyValue8 = new KeyValue(shadowEffect.radiusProperty(), startShadow.getRadius(), Interpolator.LINEAR);
                // Offsets
                KeyValue keyValue9 = new KeyValue(shadowEffect.offsetXProperty(), endShadow.getOffsetX(), Interpolator.LINEAR);
                KeyValue keyValue10 = new KeyValue(shadowEffect.offsetXProperty(), startShadow.getOffsetX(), Interpolator.LINEAR);
                KeyValue keyValue11 = new KeyValue(shadowEffect.offsetYProperty(), endShadow.getOffsetY(), Interpolator.LINEAR);
                KeyValue keyValue12 = new KeyValue(shadowEffect.offsetYProperty(), startShadow.getOffsetY(), Interpolator.LINEAR);
                KeyFrame keyFrame5 = new KeyFrame(Duration.ZERO, keyValue5, keyValue7, keyValue9, keyValue11);
                KeyFrame keyFrame6 = new KeyFrame(inDuration.get(), keyValue6, keyValue8, keyValue10, keyValue12);
                shadowAnimation.getKeyFrames().addAll(keyFrame5, keyFrame6);
                parallelTransition.getChildren().add(0, shadowAnimation);
            }

            sequentialTransition.getChildren().addAll(inAnimation, outAnimation);
            parallelTransition.setInterpolator(rippleInterpolator);
            parallelTransition.getChildren().add(sequentialTransition);
        }
    }

    //================================================================================
    // Stylesheet properties
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<RippleGenerator, Color> RIPPLE_COLOR =
                FACTORY.createColorCssMetaData(
                        "-mfx-ripple-color",
                        rippleGenerator -> (StyleableProperty<Color>) rippleGenerator.rippleColorProperty()
                );

        private static final CssMetaData<RippleGenerator, Number> RIPPLE_RADIUS =
                FACTORY.createSizeCssMetaData(
                        "-mfx-ripple-radius",
                        RippleGenerator::rippleRadiusProperty,
                        10.0
                );

        static {
            cssMetaDataList = List.of(RIPPLE_COLOR, RIPPLE_RADIUS);
        }

    }

    public List<CssMetaData<? extends Styleable, ?>> getGroupCssMetaDataList() {
        return RippleGenerator.StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return this.getGroupCssMetaDataList();
    }
}