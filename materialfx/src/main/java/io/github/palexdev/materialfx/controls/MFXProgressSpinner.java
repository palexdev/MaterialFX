package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXProgressSpinnerSkin;
import javafx.css.*;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

import java.util.List;

public class MFXProgressSpinner extends ProgressIndicator {
    private static final StyleablePropertyFactory<MFXProgressSpinner> FACTORY = new StyleablePropertyFactory<>(ProgressIndicator.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-spinner";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-spinner.css").toString();

    public MFXProgressSpinner() {
        this(-1);
    }

    public MFXProgressSpinner(double progress) {
        super(progress);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    private final StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(
            StyleableProperties.RADIUS,
            this,
            "radius",
            Region.USE_COMPUTED_SIZE
    );

    private final StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(
            StyleableProperties.STARTING_ANGLE,
            this,
            "startingAngle",
            360 - Math.random() * 720
    );

    public double getRadius() {
        return radius.get();
    }

    public StyleableDoubleProperty radiusProperty() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public double getStartingAngle() {
        return startingAngle.get();
    }

    public StyleableDoubleProperty startingAngleProperty() {
        return startingAngle;
    }

    public void setStartingAngle(double startingAngle) {
        this.startingAngle.set(startingAngle);
    }

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

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

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
}
