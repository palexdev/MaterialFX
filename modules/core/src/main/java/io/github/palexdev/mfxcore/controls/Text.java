package io.github.palexdev.mfxcore.controls;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.text.FontSmoothingType;

import java.util.List;

/**
 * Simple extension of {@link javafx.scene.text.Text} to allow setting the wrapping width property in CSS, as well
 * as setting the default style class to '.text', and the default font smoothing to {@link FontSmoothingType#LCD}.
 */
public class Text extends javafx.scene.text.Text {

    //================================================================================
    // Constructors
    //================================================================================
    public Text() {
        this("");
    }

    public Text(String text) {
        super(text);
        initialize();
    }

    public Text(double x, double y, String text) {
        super(x, y, text);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll("text");
        wrappingWidthProperty().bind(cssWrappingWidthProperty());
        ((StyleableObjectProperty<FontSmoothingType>) fontSmoothingTypeProperty()).applyStyle(null, FontSmoothingType.LCD);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty cssWrappingWidth = new StyleableDoubleProperty(
        StyleableProperties.WRAPPING_WIDTH,
        this,
        "cssWrappingWidth",
        0.0
    );

    public double getCssWrappingWidth() {
        return cssWrappingWidth.get();
    }

    /**
     * Allows to set the wrapping width in CSS with the property: '-fx-wrapping-width'.
     * <p></p>
     * It's named like this to avoid conflicts with {@link #wrappingWidthProperty()}, which won't be settable anymore
     * since it's being bound to this one instead.
     */
    public StyleableDoubleProperty cssWrappingWidthProperty() {
        return cssWrappingWidth;
    }

    public void setCssWrappingWidth(double cssWrappingWidth) {
        this.cssWrappingWidth.set(cssWrappingWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<Text> FACTORY = new StyleablePropertyFactory<>(javafx.scene.text.Text.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<Text, Number> WRAPPING_WIDTH =
            FACTORY.createSizeCssMetaData(
                "-fx-wrapping-width",
                Text::cssWrappingWidthProperty,
                0.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                javafx.scene.text.Text.getClassCssMetaData(),
                WRAPPING_WIDTH
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
