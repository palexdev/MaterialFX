package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXProgressSpinnerSkin;
import javafx.css.*;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.List;

/**
 * Implementation of a spinning {@code ProgressIndicator}.
 * <p>
 * Extends {@link ProgressIndicator}
 */
public class MFXProgressSpinner extends ProgressIndicator {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXProgressSpinner> FACTORY = new StyleablePropertyFactory<>(ProgressIndicator.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-spinner";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-spinner.css").toString();
    /**
     * Specifies the radius of the spinner.
     */
    private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
            StyleableProperties.RADIUS,
            this,
            "radius",
            Region.USE_COMPUTED_SIZE
    );
    /**
     * Specifies the starting angle of the animation.
     */
    private final StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(
            StyleableProperties.STARTING_ANGLE,
            this,
            "startingAngle",
            360 - Math.random() * 720
    );

    //================================================================================
    // Constructors
    //================================================================================
    public MFXProgressSpinner() {
        this(-1);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================

    public MFXProgressSpinner(double progress) {
        super(progress);
        initialize();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    public double getRadius() {
        return radius.get();
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public StyleableDoubleProperty radiusProperty() {
        return radius;
    }

    public double getStartingAngle() {
        return startingAngle.get();
    }

    public void setStartingAngle(double startingAngle) {
        this.startingAngle.set(startingAngle);
    }

    public StyleableDoubleProperty startingAngleProperty() {
        return startingAngle;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXProgressSpinnerSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXProgressSpinner.getControlCssMetaDataList();
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXProgressSpinner, Number> RADIUS =
                FACTORY.createSizeCssMetaData(
                        "-mfx-radius",
                        MFXProgressSpinner::radiusProperty,
                        Region.USE_COMPUTED_SIZE
                );

        private static final CssMetaData<MFXProgressSpinner, Number> STARTING_ANGLE =
                FACTORY.createSizeCssMetaData(
                        "-mfx-starting-angle",
                        MFXProgressSpinner::startingAngleProperty,
                        360 - Math.random() * 720
                );

        static {
            cssMetaDataList = List.of(RADIUS, STARTING_ANGLE);
        }
    }
}
