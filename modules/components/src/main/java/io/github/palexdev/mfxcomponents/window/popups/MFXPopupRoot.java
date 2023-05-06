package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.skins.MFXPopupSkin;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleablePositionProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Bounds;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Concrete implementation of {@link IMFXPopupRoot} which extends {@link StackPane} to ensure that popups' content
 * always take all the space required. This is used by the default popups' skin, {@link MFXPopupSkin}, and has various
 * purposes:
 * <p> 1) Contains the popup's content
 * <p> 2) Here properties such as {@link IMFXPopup#animatedProperty()} and {@link IMFXPopup#offsetProperty()} are made
 * styleable, meaning that they can be set via CSS. The reason for this is that for some reason implementing them in
 * the popup class itself was causing a {@code ClassCastException} on the popup's special node, the 'bridge'.
 * <p> 3) Creates the needed bindings, see {@link #createBindings()}
 * <p></p>
 * Since this is intended to be used in popups' skins, there's also a {@link #dispose()} method that must be called on
 * skin disposal, {@link Skin#dispose()}.
 */
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
        setAnimated(popup.isAnimated());
        setOffset(popup.getOffset());
        createBindings();
    }

    /**
     * Creates the following bindings:
     * <p> 1) All the stylesheets added on {@link IMFXPopup#getStylesheets()} are automatically added here as well
     * through {@link Bindings#bindContent(List, ObservableList)}
     * <p> 2) Binds bidirectionally the {@link #animatedProperty()} and {@link #offsetProperty()} to the ones in the popup,
     * note that these two properties here must be initialized before the binding so that if the user has changed their
     * values they won't be overridden. Keep in mind that this class is created only when the popup' skin is built
     * <p> 3) Binds the {@link IMFXPopup#contentBoundsProperty()} to its layout bounds, since this is a {@link StackPane},
     * the content bounds are the same as this
     * <p> 4) Binds the {@link IMFXPopup#hoverProperty()} to its hover state property, thus avoiding the need of event
     * handlers
     */
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

    /**
     * See {@link IMFXPopup#animatedProperty()}.
     * <p>
     * Here implemented as a styleable property, can be set in CSS as: "-mfx-animated".
     */
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public Position getOffset() {
        return offset.get();
    }

    /**
     * See {@link IMFXPopup#offsetProperty()}.
     * <p>
     * Here implemented as a styleable property, can be set in CSS as: "-mfx-offset".
     * <p></p>
     * <b>Important Note:</b> make sure to read {@link StyleablePositionProperty.PositionConverter} documentation for how to use it
     * properly in CSS.
     */
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
