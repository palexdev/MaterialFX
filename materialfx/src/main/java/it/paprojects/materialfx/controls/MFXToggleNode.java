package it.paprojects.materialfx.controls;

import it.paprojects.materialfx.MFXResources;
import it.paprojects.materialfx.skins.MFXToggleNodeSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * This control allows to create a toggle button with any {@code Node} set as graphic.
 * <p>
 * For example you can see in the demo this is used with Ikonli icons.
 * <p>
 * Extends {@code ToggleButton}, redefines the style class to "mfx-toggle-node" for usage in CSS and
 * includes a {@code RippleGenerator}(in the Skin) to generate ripple effect when toggled/untoggled.
 */
public class MFXToggleNode extends ToggleButton {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<MFXToggleNode> FACTORY = new StyleablePropertyFactory<>(ToggleButton.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-toggle-node";
    private final String STYLESHEET = MFXResources.load("css/mfx-togglenode.css").toString();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXToggleNode() {
        initialize();
    }

    public MFXToggleNode(Node graphic) {
        super("", graphic);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    //================================================================================
    // Styleable properties
    //================================================================================

    /**
     * Specifies the background color when selected.
     * @see Color
     */
    private final StyleableObjectProperty<Paint> selectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.SELECTED_COLOR,
            this,
            "selectedColor",
            Color.rgb(0, 0, 0, 0.2)
    );

    /**
     * Specifies the background color when unselected.
     * @see Color
     */
    private final StyleableObjectProperty<Paint> unSelectedColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNSELECTED_COLOR,
            this,
            "unSelectedColor",
            Color.TRANSPARENT
    );

    /**
     * Specifies the border width of the control when selected.
     */
    private final DoubleProperty strokeWidth = new SimpleDoubleProperty(3.0);

    public Paint getSelectedColor() {
        return selectedColor.get();
    }

    public StyleableObjectProperty<Paint> selectedColorProperty() {
        return selectedColor;
    }

    public void setSelectedColor(Paint selectedColor) {
        this.selectedColor.set(selectedColor);
    }

    public Paint getUnSelectedColor() {
        return unSelectedColor.get();
    }

    public StyleableObjectProperty<Paint> unSelectedColorProperty() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(Paint unSelectedColor) {
        this.unSelectedColor.set(unSelectedColor);
    }

    public double getStrokeWidth() {
        return strokeWidth.get();
    }

    public DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXToggleNode, Paint> SELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-selected-color",
                        MFXToggleNode::selectedColorProperty,
                        Color.rgb(0, 0, 0, 0.2)
                );

        private static final CssMetaData<MFXToggleNode, Paint> UNSELECTED_COLOR =
                FACTORY.createPaintCssMetaData(
                        "-mfx-unselected-color",
                        MFXToggleNode::unSelectedColorProperty,
                        Color.TRANSPARENT
                );

        static {
            cssMetaDataList = List.of(SELECTED_COLOR, UNSELECTED_COLOR);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    //================================================================================
    // Override Methods
    //================================================================================

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXToggleNodeSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
