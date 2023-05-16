package io.github.palexdev.mfxcore.utils.fx;

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.Function;

/**
 * A custom implementation of {@link ObjectBinding} which can be useful for nodes that have text.
 * At the time of writing this, and honestly I don't believe one bit this will change in the future, the only way to
 * measure the width and height of text in JavaFX is to use a dummy Scene with a dummy Text node and make a container layout
 * the text, thus allowing to retrieve its sizes. It sounds like a good workaround for the lack of public APIs from JavaFX
 * (I really don't understand why they keep hiding stuff, it's so stupid), but the reality is that it is a quite expensive
 * operation. Using such technique in layout methods (e.g computePrefWidth(), computePrefHeight(), layoutChildren(),...)
 * can have drastic effects on performance.
 * <p>
 * Leveraging the fact that JavaFX's bindings are lazy and that the sizes of a text node change only when a) the text changes
 * b) the font changes, we can cache the measurements once and only update them when requested and not valid anymore.
 * <p></p>
 * Since computing text sizes will almost always return "raw" values, this cache also offers the possibility of retrieving
 * values that are "pixel snapped". The functions responsible for that are: {@link #setXSnappingFunction(Function)} and
 * {@link #setYSnappingFunction(Function)}.
 */
public class TextMeasurementCache extends ObjectBinding<Size> {
    //================================================================================
    // Properties
    //================================================================================
    private ObservableValue<String> text;
    private ObservableValue<Font> font;
    private Function<Double, Double> xSnappingFunction = v -> v;
    private Function<Double, Double> ySnappingFunction = v -> v;

    //================================================================================
    // Constructors
    //================================================================================

    /**
     * Constructs a new cache from the given {@link Labeled} node, using its {@link Labeled#textProperty()} and
     * {@link Labeled#fontProperty()} as dependencies of this binding. The functions responsible for rounding the
     * computed values are set to: {@link Region#snapSizeX(double)} and {@link Region#snapSizeY(double)}.
     */
    public TextMeasurementCache(Labeled labeled) {
        this(labeled.textProperty(), labeled.fontProperty());
        xSnappingFunction = labeled::snapSizeX;
        ySnappingFunction = labeled::snapSizeY;
    }

    /**
     * Constructs a new cache from the given {@link Text} node, using its {@link Text#textProperty()} and
     * {@link Text#fontProperty()} as dependencies of this binding. The functions responsible for rounding the
     * computed values are not set implicitly since {@link Text} is not a region.
     */
    public TextMeasurementCache(Text text) {
        this(text.textProperty(), text.fontProperty());
    }

    /**
     * Constructs a new cache from the given properties, which will be then set as the dependencies of this binding.
     * In this case  too, the function responsible for rounding the computed values must be set explicitly.
     */
    public TextMeasurementCache(ObservableValue<String> textProperty, ObservableValue<Font> fontProperty) {
        this.text = textProperty;
        this.font = fontProperty;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        bind(text, font);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Size computeValue() {
        return TextUtils.computeTextSizes(font.getValue(), text.getValue());
    }

    @Override
    public void dispose() {
        unbind(text, font);
        text = null;
        font = null;
        super.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /**
     * Applies the {@link #getXSnappingFunction()} on the {@link Size#getWidth()} value of this binding.
     */
    public double getSnappedWidth() {
        return xSnappingFunction.apply(getValue().getWidth());
    }

    /**
     * Applies the {@link #getYSnappingFunction()} on the {@link Size#getHeight()} value of this binding.
     */
    public double getSnappedHeight() {
        return ySnappingFunction.apply(getValue().getHeight());
    }

    /**
     * @return the function responsible for rounding the computed width values.
     * In JavaFX, you usually want to round "raw" values to the closest pixel
     */
    public Function<Double, Double> getXSnappingFunction() {
        return xSnappingFunction;
    }

    /**
     * Sets the function responsible for rounding the computed width values.
     * In JavaFX, you usually want to round "raw" values to the closest pixel.
     */
    public void setXSnappingFunction(Function<Double, Double> xSnappingFunction) {
        this.xSnappingFunction = xSnappingFunction;
    }

    /**
     * @return the function responsible for rounding the computed height values.
     * In JavaFX, you usually want to round "raw" values to the closest pixel
     */
    public Function<Double, Double> getYSnappingFunction() {
        return ySnappingFunction;
    }

    /**
     * Sets the function responsible for rounding the computed height values.
     * In JavaFX, you usually want to round "raw" values to the closest pixel.
     */
    public void setYSnappingFunction(Function<Double, Double> ySnappingFunction) {
        this.ySnappingFunction = ySnappingFunction;
    }
}
