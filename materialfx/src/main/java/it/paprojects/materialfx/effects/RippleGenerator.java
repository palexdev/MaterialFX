package it.paprojects.materialfx.effects;

import it.paprojects.materialfx.controls.MFXButton;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

import static it.paprojects.materialfx.effects.MFXDepthManager.shadowOf;

public class RippleGenerator extends Group {
    private final String STYLE_CLASS = "rippleGenerator";
    private static final StyleablePropertyFactory<RippleGenerator> FACTORY = new StyleablePropertyFactory<>(Group.getClassCssMetaData());

    private final Control control;

    private final Interpolator rippleInterpolator = Interpolator.SPLINE(0.0825, 0.3025, 0.0875, 0.9975);
    //private final Interpolator rippleInterpolator = Interpolator.SPLINE(0.1, 0.50, 0.3, 0.85);
    private final Interpolator easeIn = Interpolator.EASE_IN;
    private final Interpolator easeOut = Interpolator.EASE_OUT;
    private final Interpolator easeBoth = Interpolator.EASE_BOTH;
    private  ObjectProperty<Color> rippleColor = new StyleableObjectProperty<>(Color.ROYALBLUE)
    {
        @Override public CssMetaData getCssMetaData() { return StyleableProperties.RIPPLE_COLOR; }
        @Override public Object getBean() { return this; }
        @Override public String getName() { return "ledColor"; }
    };
    private final DoubleProperty rippleRadius = new SimpleDoubleProperty(10.0);
    private final ObjectProperty<Duration> inDuration = new SimpleObjectProperty<>(Duration.millis(700));
    private final ObjectProperty<Duration> outDuration = new SimpleObjectProperty<>(inDuration.get().divide(2));

    private double generatorCenterX = 100.0;
    private double generatorCenterY = 100.0;

    public RippleGenerator(Control control) {
        this.control = control;
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

    public void createRipple() {
        final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
        getChildren().add(ripple);

        Rectangle fillRect = new Rectangle(control.getWidth(), control.getHeight());
        fillRect.setFill(rippleColor.get());
        fillRect.setOpacity(0);
        getChildren().add(0, fillRect);

        KeyValue keyValueIn = new KeyValue(fillRect.opacityProperty(), 0.3);
        KeyValue keyValueOut = new KeyValue(fillRect.opacityProperty(), 0);
        KeyFrame keyFrameIn = new KeyFrame(inDuration.get(), keyValueIn);
        KeyFrame keyFrameOut = new KeyFrame(outDuration.get(), keyValueOut);
        ripple.inAnimation.getKeyFrames().add(keyFrameIn);
        ripple.outAnimation.getKeyFrames().add(keyFrameOut);

        ripple.parallelTransition.setOnFinished(event -> getChildren().remove(ripple));
        ripple.parallelTransition.play();

    }

    public void setGeneratorCenterX(double generatorCenterX) {
        this.generatorCenterX = generatorCenterX;
    }

    public void setGeneratorCenterY(double generatorCenterY) {
        this.generatorCenterY = generatorCenterY;
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

    public DoubleProperty rippleRadiusProperty() {
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

    private class Ripple extends Circle {
        private final int shadowDelta = 2;

        private final Timeline inAnimation = new Timeline();
        private final Timeline outAnimation = new Timeline();
        private final Timeline shadowAnimation = new Timeline();
        private final SequentialTransition sequentialTransition = new SequentialTransition();
        private final ParallelTransition parallelTransition = new ParallelTransition();

        private Ripple(double centerX, double centerY) {
            super(centerX, centerY, 0, Color.TRANSPARENT);
            setFill(rippleColor.get());
            Rectangle rectangle = new Rectangle(control.getWidth(), control.getHeight());
            setClip(rectangle);
            buildAnimation();
        }

        private void buildAnimation() {
            DropShadow buttonShadow = (DropShadow) ((MFXButton) control.getSkin().getSkinnable()).getEffect();
            DepthLevel depthLevel = ((MFXButton) control).getDepthLevel();
            DropShadow startShadow = shadowOf(depthLevel);
            DropShadow endShadow = shadowOf(depthLevel, shadowDelta);

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

            // Button shadow
            // Spread
            KeyValue keyValue5 = new KeyValue(buttonShadow.spreadProperty(), endShadow.getSpread(), easeBoth);
            KeyValue keyValue6 = new KeyValue(buttonShadow.spreadProperty(), startShadow.getSpread(), easeBoth);
            //Radius
            KeyValue keyValue7 = new KeyValue(buttonShadow.radiusProperty(), endShadow.getRadius(), easeBoth);
            KeyValue keyValue8 = new KeyValue(buttonShadow.radiusProperty(), startShadow.getRadius(), easeBoth);
            // Offsets
            KeyValue keyValue9 = new KeyValue(buttonShadow.offsetXProperty(), endShadow.getOffsetX(), easeBoth);
            KeyValue keyValue10 = new KeyValue(buttonShadow.offsetXProperty(), startShadow.getOffsetX(), easeBoth);
            KeyValue keyValue11 = new KeyValue(buttonShadow.offsetYProperty(), endShadow.getOffsetY(), easeBoth);
            KeyValue keyValue12 = new KeyValue(buttonShadow.offsetYProperty(), startShadow.getOffsetY(), easeBoth);
            KeyFrame keyFrame5 = new KeyFrame(Duration.ZERO, keyValue5, keyValue7, keyValue9, keyValue11);
            KeyFrame keyFrame6 = new KeyFrame(inDuration.get(), keyValue6, keyValue8, keyValue10, keyValue12);
            shadowAnimation.getKeyFrames().addAll(keyFrame5, keyFrame6);

            sequentialTransition.getChildren().addAll(inAnimation, outAnimation);
            parallelTransition.setInterpolator(rippleInterpolator);
            parallelTransition.getChildren().addAll(shadowAnimation, sequentialTransition);
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

        static {
            cssMetaDataList = List.of(RIPPLE_COLOR);
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