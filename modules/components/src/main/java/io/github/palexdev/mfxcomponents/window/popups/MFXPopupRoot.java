package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleablePositionProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MFXPopupRoot extends StackPane implements IMFXPopupRoot {
    //================================================================================
    // Properties
    //================================================================================
    private IMFXPopup popup;
    private When<?> contentWhen;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopupRoot(IMFXPopup popup) {
        this.popup = popup;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setOffset(popup.getOffset());
        createBindings();
    }

    protected void createBindings() {
        Bindings.bindContent(getStylesheets(), popup.getStylesheets());
        popup.animatedProperty().bindBidirectional(animatedProperty());
        popup.offsetProperty().bindBidirectional(offsetProperty());
        if (popup.contentBoundsProperty() instanceof ObjectProperty)
            ((ObjectProperty<Bounds>) popup.contentBoundsProperty()).bind(layoutBoundsProperty());
        if (popup.hoverProperty() instanceof BooleanProperty)
            ((BooleanProperty) popup.hoverProperty()).bind(hoverProperty());
        contentWhen = When.onChanged(popup.contentProperty())
            .then((o, n) -> {
                getChildren().clear();
                if (n != null) getChildren().add(n);
            })
            .executeNow()
            .listen();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Region toNode() {
        return this;
    }

    @Override
    public IMFXPopup getPopup() {
        return popup;
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("content");
    }

    @Override
    public void dispose() {
        Bindings.unbindContent(getStylesheets(), popup.getStylesheets());
        popup.animatedProperty().unbindBidirectional(animatedProperty());
        popup.offsetProperty().unbindBidirectional(offsetProperty());
        if (popup.contentBoundsProperty() instanceof ObjectProperty)
            ((ObjectProperty<Bounds>) popup.contentBoundsProperty()).unbind();
        if (popup.hoverProperty() instanceof BooleanProperty)
            ((BooleanProperty) popup.hoverProperty()).unbind();
        contentWhen.dispose();
        contentWhen = null;
        getChildren().clear();
        popup = null;
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
        StyleableProperties.ANIMATED,
        this,
        "animated",
        true
    );

    private final StyleablePositionProperty offset = new StyleablePositionProperty(
        StyleableProperties.OFFSET,
        this,
        "offset",
        Position.origin()
    );

    public boolean isAnimated() {
        return animated.get();
    }

    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public Position getOffset() {
        return offset.get();
    }

    public StyleablePositionProperty offsetProperty() {
        return offset;
    }

    public void setOffset(Position offset) {
        this.offset.set(offset);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXPopupRoot> FACTORY = new StyleablePropertyFactory<>(StackPane.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXPopupRoot, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MFXPopupRoot::animatedProperty,
                true
            );

        private static final CssMetaData<MFXPopupRoot, Position> OFFSET = StyleablePositionProperty.metaDataFor(
            "-mfx-offset", MFXPopupRoot::offsetProperty, Position.origin()
        );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                StackPane.getClassCssMetaData(),
                ANIMATED, OFFSET
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
