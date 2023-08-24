package io.github.palexdev.mfxcomponents.controls.buttons;

import io.github.palexdev.mfxcomponents.behaviors.MFXIconButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXIconButtonSkin;
import io.github.palexdev.mfxcomponents.theming.base.WithVariants;
import io.github.palexdev.mfxcomponents.theming.enums.IconButtonVariants;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.IconProvider;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.text.Font;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Custom and particular implementation of a button which extends {@link MFXSelectable}, has its own skin
 * {@link MFXIconButtonSkin} and its own behavior {@link MFXIconButtonBehavior}.
 * <p></p>
 * {@code MFXIconButton} presents in 4 variants, expressed through the {@link WithVariants} API.
 * <p> 1) The 'standard' variant (any new instance without any {@link IconButtonVariants} applied)
 * <p> 2) The {@link IconButtonVariants#FILLED} variant
 * <p> 3) The {@link IconButtonVariants#FILLED_TONAL} variant
 * <p> 4) The {@link IconButtonVariants#OUTLINED} variant
 * <p></p>
 * The default style class of this component is: '.mfx-icon-button'.
 * <p></p>
 * As already stated above, this extends {@link MFXSelectable} and it's a special type of button.
 * This can behave both as a simple button or as a toggle. The working mode can be specified through the
 * {@link #selectableProperty()}.
 * <p></p>
 * M3 guidelines highly suggest to set a tooltip on this kind of buttons, to describe in short what's its purpose
 * and action.
 * <p></p>
 * Last but not least, I want to specify that: as the name suggests this button/toggle is intended to use with icons only,
 * despite extending {@link MFXSelectable} this is particular also for this detail, the default skin has no node to display
 * the set text!
 */
public class MFXIconButton extends MFXSelectable<MFXIconButtonBehavior> implements WithVariants<MFXIconButton, IconButtonVariants> {
    //================================================================================
    // Properties
    //================================================================================
    private final IconProperty icon = new IconProperty();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconButton() {
        this(new MFXFontIcon());
    }

    public MFXIconButton(MFXFontIcon icon) {
        setIcon(icon);
        initialize();
    }

    /**
     * Fluent way to set {@link #selectableProperty()} to true.
     */
    public MFXIconButton asToggle() {
        setSelectable(true);
        return this;
    }

    //================================================================================
    // Variants
    //================================================================================
    public MFXIconButton filled() {
        setVariants(IconButtonVariants.FILLED);
        return this;
    }

    public MFXIconButton tonal() {
        setVariants(IconButtonVariants.FILLED_TONAL);
        return this;
    }

    public MFXIconButton outlined() {
        setVariants(IconButtonVariants.OUTLINED);
        return this;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        graphicProperty().bind(iconProperty());
        setSelectable(false);
        selectionGroupProperty().addListener(i -> {
            if (getSelectionGroup() != null) asToggle();
        });
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXIconButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-icon-button");
    }

    @Override
    public Supplier<MFXIconButtonBehavior> defaultBehaviorProvider() {
        return () -> new MFXIconButtonBehavior(this);
    }

    @Override
    public MFXIconButton addVariants(IconButtonVariants... variants) {
        return WithVariants.addVariants(this, variants);
    }

    @Override
    public MFXIconButton setVariants(IconButtonVariants... variants) {
        return WithVariants.setVariants(this, variants);
    }

    @Override
    public MFXIconButton removeVariants(IconButtonVariants... variants) {
        return WithVariants.removeVariants(this, variants);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty selectable = new StyleableBooleanProperty(
        StyleableProperties.SELECTABLE,
        this,
        "selectable",
        false
    ) {
        @Override
        protected void invalidated() {
            PseudoClasses.SELECTABLE.setOn(MFXIconButton.this, get());
        }
    };

    private final StyleableDoubleProperty size = new StyleableDoubleProperty(
        StyleableProperties.SIZE,
        this,
        "size",
        40.0
    );

    public boolean isSelectable() {
        return selectable.get();
    }

    /**
     * Specifies the working mode of the icon button.
     * <p>
     * When false, this will act as a normal button, otherwise as a toggle.
     */
    public StyleableBooleanProperty selectableProperty() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    public double getSize() {
        return size.get();
    }

    /**
     * Specifies the size of the icon button.
     * <p>
     * M3 guidelines show that icon buttons are always round containers with the same width and height.
     * I recommend following these guidelines, if you don't want a circle you can still change the component' style
     * to be more squared, however the sizes should always stay equal. This is also enforced by the default skin.
     */
    public StyleableDoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXIconButton> FACTORY = new StyleablePropertyFactory<>(MFXSelectable.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXIconButton, Boolean> SELECTABLE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-selectable",
                MFXIconButton::selectableProperty,
                false
            );

        private static final CssMetaData<MFXIconButton, Number> SIZE =
            FACTORY.createSizeCssMetaData(
                "-mfx-size",
                MFXIconButton::sizeProperty,
                40.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXSelectable.getClassCssMetaData(),
                SELECTABLE, SIZE
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public MFXFontIcon getIcon() {
        return icon.get();
    }

    /**
     * Specifies the button's icon.
     */
    public IconProperty iconProperty() {
        return icon;
    }

    public void setIcon(MFXFontIcon icon) {
        this.icon.set(icon);
    }

    /**
     * Delegate of {@link IconProperty#setDescription(String)}.
     */
    public IconProperty setIconDescription(String description) {
        return icon.setDescription(description);
    }

    /**
     * Delegate of {@link IconProperty#setProvider(IconProvider)}.
     *
     * @see MFXFontIcon#setIconsProvider(IconProvider)
     */
    public IconProperty setIconProvider(IconProvider provider) {
        return icon.setProvider(provider);
    }

    /**
     * Delegate of {@link IconProperty#setProvider(Font, Function)}.
     *
     * @see MFXFontIcon#setIconsProvider(Font, Function)
     */
    public IconProperty setIconProvider(Font font, Function<String, Character> converter) {
        return icon.setProvider(font, converter);
    }

    /**
     * Delegate of {@link IconProperty#setProvider(IconProvider, String)}.
     */
    public IconProperty setIconProvider(IconProvider provider, String description) {
        return icon.setProvider(provider, description);
    }
}
