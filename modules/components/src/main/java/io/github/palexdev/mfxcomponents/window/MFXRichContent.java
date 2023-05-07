package io.github.palexdev.mfxcomponents.window;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.Text;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * An extended variant of {@link MFXPlainContent}. Other than display some text, this allows to set a header text
 * and up to two actions as {@link MFXButton}s.
 * <p>
 * As specified by M3 guidelines, for contrast by default a {@link DropShadow} effect is applied, can be disabled
 * by setting {@link #elevationProperty()} to {@link ElevationLevel#LEVEL0}.
 */
public class MFXRichContent extends VBox implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty header = new SimpleStringProperty();
    private final StringProperty text = new SimpleStringProperty();

    protected Text lHeader;
    protected Text lText;
    protected HBox actionsBox;
    protected MFXButton primaryAction;
    protected MFXButton secondaryAction;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRichContent() {
        this("", "");
    }

    public MFXRichContent(String text) {
        this("", text);
    }

    public MFXRichContent(String header, String text) {
        setHeader(header);
        setText(text);
        getStyleClass().setAll(defaultStyleClasses());
        build();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        lHeader = new Text();
        lHeader.getStyleClass().setAll("header");
        lHeader.textProperty().bind(headerProperty());

        lText = new Text();
        lText.textProperty().bind(textProperty());

        actionsBox = new HBox();
        actionsBox.getStyleClass().add("actions");
        super.getChildren().addAll(lHeader, lText, actionsBox);
    }

    /**
     * Ensures that the set buttons for the actions are added to the pane.
     */
    protected void updateActions() {
        actionsBox.getChildren().clear();
        if (primaryAction != null) actionsBox.getChildren().add(primaryAction);
        if (secondaryAction != null) actionsBox.getChildren().add(secondaryAction);
    }

    /**
     * Specifies what to do when the {@link #elevationProperty()} changes.
     * <p>
     * By default, enables cache and disables the pick on bounds when the level is greater than 0.
     * Otherwise, the properties are reset.
     */
    protected void onElevationChanged() {
        ElevationLevel level = getElevation();
        if (level != null && level != ElevationLevel.LEVEL0) {
            setPickOnBounds(false);
            setCache(true);
            setCacheHint(CacheHint.SCALE);
        } else {
            setPickOnBounds(true);
            setCache(false);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ObservableList<Node> getChildren() {
        return getChildrenUnmodifiable();
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("rich");
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    protected final StyleableObjectProperty<ElevationLevel> elevation = new StyleableObjectProperty<>(
        StyleableProperties.ELEVATION,
        this,
        "elevation",
        ElevationLevel.LEVEL0
    ) {
        @Override
        public void set(ElevationLevel newValue) {
            if (newValue == ElevationLevel.LEVEL0) {
                setEffect(null);
                super.set(newValue);
                return;
            }

            Effect effect = getEffect();
            if (effect == null) {
                setEffect(newValue.toShadow());
                super.set(newValue);
                return;
            }
            if (!(effect instanceof DropShadow)) {
                return;
            }

            setEffect(newValue.toShadow());
            super.set(newValue);
        }

        @Override
        protected void invalidated() {
            onElevationChanged();
        }
    };

    public ElevationLevel getElevation() {
        return elevation.get();
    }

    /**
     * Specifies the emphasis of the content' shadow. In other words, this property will apply a {@link DropShadow}
     * to the content root when the specified level is greater than 0.
     * <p>
     * Can be set in CSS via the property: '-mfx-elevation'
     */

    public StyleableObjectProperty<ElevationLevel> elevationProperty() {
        return elevation;
    }

    public void setElevation(ElevationLevel elevation) {
        this.elevation.set(elevation);
    }

    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXRichContent> FACTORY = new StyleablePropertyFactory<>(VBox.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXRichContent, ElevationLevel> ELEVATION =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-elevation",
                MFXRichContent::elevationProperty,
                ElevationLevel.LEVEL0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                VBox.getClassCssMetaData(),
                ELEVATION
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

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getHeader() {
        return header.get();
    }

    /**
     * Specifies the header's text.
     */
    public StringProperty headerProperty() {
        return header;
    }

    public void setHeader(String header) {
        this.header.set(header);
    }

    public String getText() {
        return text.get();
    }

    /**
     * Specifies the main text.
     */
    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    /**
     * @return the button responsible for executing the main action
     */
    public MFXButton getPrimaryAction() {
        return primaryAction;
    }

    /**
     * Sets the button responsible for executing the main action.
     */
    public void setPrimaryAction(MFXButton primaryAction) {
        this.primaryAction = primaryAction;
        updateActions();
    }

    /**
     * @return the button responsible for executing the secondary action
     */
    public MFXButton getSecondaryAction() {
        return secondaryAction;
    }

    /**
     * Sets the button responsible for executing the secondary action.
     */
    public void setSecondaryAction(MFXButton secondaryAction) {
        this.secondaryAction = secondaryAction;
        updateActions();
    }
}
