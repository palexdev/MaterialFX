package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.enums.ButtonType;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.skins.MFXButtonSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;

/**
 * This is the implementation of a button following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code Button}, redefines the style class to "mfx-button" for usage in CSS and
 * includes a {@code RippleGenerator} to generate ripple effects on click.
 */
public class MFXButton extends Button {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXButton> FACTORY = new StyleablePropertyFactory<>(Button.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-button";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-button.css").toString();
    private final RippleGenerator rippleGenerator = new RippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButton() {
        setText("Button");
        initialize();
    }

    public MFXButton(String text) {
        super(text);
        initialize();
    }

    public MFXButton(String text, double prefWidth, double prefHeight) {
        super(text);
        setPrefSize(prefWidth, prefHeight);
        initialize();
    }

    public MFXButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.CENTER);
        setBindings();

        setRippleRadius(25);
        setRippleColor(Color.rgb(190, 190, 190));
    }

    //================================================================================
    // Ripple properties
    //================================================================================

    /**
     * Specifies the ripples color of this control.
     * @see Color
     */
    private final ObjectProperty<Paint> rippleColor = new SimpleObjectProperty<>();

    /**
     * Specifies the ripples radius of this control.
     */
    private final DoubleProperty rippleRadius = new SimpleDoubleProperty();

    /**
     * Specifies the ripples in animation duration of this control.
     * @see Duration
     */
    private final ObjectProperty<Duration> rippleInDuration = new SimpleObjectProperty<>();

    /**
     * Specifies the ripples out animation duration of this control.
     * @see Duration
     */
    private final ObjectProperty<Duration> rippleOutDuration = new SimpleObjectProperty<>();

    public final ObjectProperty<Paint> rippleColorProperty() {
        return this.rippleColor;
    }

    public final Paint getRippleColor() {
        return rippleColor.get();
    }

    public final void setRippleColor(Paint rippleColor) {
        rippleGenerator.setRippleColor((Color) rippleColor);
    }

    public double getRippleRadius() {
        return rippleRadius.get();
    }

    public DoubleProperty rippleRadiusProperty() {
        return rippleRadius;
    }

    public void setRippleRadius(double rippleRadius) {
        rippleGenerator.setRippleRadius(rippleRadius);
    }

    public Duration getRippleInDuration() {
        return rippleInDuration.get();
    }

    public ObjectProperty<Duration> rippleInDurationProperty() {
        return rippleInDuration;
    }

    public void setRippleInDuration(Duration rippleInDuration) {
        rippleGenerator.setInDuration(rippleInDuration);
    }

    public Duration getRippleOutDuration() {
        return rippleOutDuration.get();
    }

    public ObjectProperty<Duration> rippleOutDurationProperty() {
        return rippleOutDuration;
    }

    public void setRippleOutDuration(Duration rippleOutDuration) {
        rippleGenerator.setOutDuration(rippleOutDuration);
    }

    public RippleGenerator getRippleGenerator() {
        return rippleGenerator;
    }

    protected void setupRippleGenerator() {
        this.getChildren().add(0, rippleGenerator);
        this.setOnMousePressed(event -> {
            rippleGenerator.setGeneratorCenterX(event.getX());
            rippleGenerator.setGeneratorCenterY(event.getY());
            rippleGenerator.createRipple();
        });
    }

    private void setBindings() {
        rippleColor.bind(rippleGenerator.rippleColorProperty());
        rippleRadius.bind(rippleGenerator.rippleRadiusProperty());
        rippleInDuration.bind(rippleGenerator.inDurationProperty());
        rippleOutDuration.bind(rippleGenerator.outDurationProperty());
    }
//================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies how intense is the {@code DropShadow} effect applied to this control.
     * <p>
     * The {@code DropShadow} effect is used to make the control appear {@code RAISED}.
     */
    private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
            StyleableProperties.DEPTH_LEVEL,
            this,
            "depthLevel",
            DepthLevel.LEVEL2
    );

    /**
     * Specifies the appearance of this control. According to material design there are two types of buttons:
     * <p>
     * - {@code FLAT}
     * <p>
     * - {@code RAISED}
     */
    private final StyleableObjectProperty<ButtonType> buttonType = new SimpleStyleableObjectProperty<>(
            StyleableProperties.BUTTON_TYPE,
            this,
            "buttonType",
            ButtonType.FLAT
    );

    public DepthLevel getDepthLevel() {
        return depthLevel.get();
    }

    public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
        return depthLevel;
    }

    public void setDepthLevel(DepthLevel depthLevel) {
        this.depthLevel.set(depthLevel);
    }

    public ButtonType getButtonType() {
        return buttonType.get();
    }

    public StyleableObjectProperty<ButtonType> buttonTypeProperty() {
        return buttonType;
    }

    public void setButtonType(ButtonType buttonType) {
        this.buttonType.set(buttonType);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXButton, DepthLevel> DEPTH_LEVEL =
                FACTORY.createEnumCssMetaData(
                        DepthLevel.class,
                        "-mfx-depth-level",
                        MFXButton::depthLevelProperty,
                        DepthLevel.LEVEL2
                );

        private static final CssMetaData<MFXButton, ButtonType> BUTTON_TYPE =
                FACTORY.createEnumCssMetaData(
                        ButtonType.class,
                        "-mfx-button-type",
                        MFXButton::buttonTypeProperty,
                        ButtonType.FLAT);

        static {
            cssMetaDataList = List.of(DEPTH_LEVEL, BUTTON_TYPE);
        }

    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        MFXButtonSkin skin = new MFXButtonSkin(this);
        setupRippleGenerator();
        return skin;
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXButton.getControlCssMetaDataList();
    }
}
