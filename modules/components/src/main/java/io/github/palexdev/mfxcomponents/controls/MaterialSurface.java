package io.github.palexdev.mfxcomponents.controls;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Material Design 3 components are stratified. Different layers have different purposes. Among the various layers, two
 * are quite important: the state layer and the focus ring layer.
 * <p>
 * <p> - The {@code state layer} is a region on which a color, which is in contrast with the main layer color, is applied
 * at specific levels of opacity according to the various interaction states. Hover -> 8%; Press and Focus -> 12%;
 * Dragged -> 16%. Additionally on this layer ripple effect can be generated to further emphasize press/click interactions.
 * <p> - The {@code focus ring layer} is an effect that is applied only when the component is being focused by a keyboard
 * event, so {@link Node#focusVisibleProperty()} is true. A border is applied around the component.
 * <p></p>
 * There are also components that may also need a shadow effect to further separate themselves from other UI elements,
 * making them appear 3D. This is implemented with some caveats through the {@link #elevationProperty()}.
 * <p></p>
 * The goal of this region is to replicate such effects while still keeping the nodes count as low as possible.
 * Like the name suggests, this is intended to be used like an extra background on top of another region. For this reason,
 * it needs the instance of the region, called 'owner', on which this will act as an overlay.
 * <p></p>
 * The overlay is carried by a separate region that can be selected in CSS with the ".bg" style class. Despite having
 * another node just for the overlay, it is still more performant than animating the background color because
 * using {@link Region#setBackground(Background)} is much more expensive than just animating a node's opacity.
 * <p>
 * There are pros and cons deriving from this:
 * <p> Pros:
 * <p> - Implementing the {@code state layer} is much easier, as it's enough to specify the surface background color,
 * which will then just change in opacity as needed
 * <p> - The transition between the different states is a short animation, which makes component look prettier
 * <p> Cons:
 * <p> - I expect a slight impact on performance since we use two extra nodes now. And also because of the animations, however
 * they can be disabled globally via a public static flag, or per component via {@link #animateBackgroundProperty()}
 * (more convenient to set it through CSS since most of the time the MaterialSurface is part of a skin)
 * <p> - I expect another very slight impact on performance because to change the opacity according to the current interaction state,
 * a listener is added on the owner's {@link Node#getPseudoClassStates()}. Being a Set, the lookup will still be
 * pretty fast though.
 * <p></p>
 * This is intended to be used in skins of components that need to visually distinguish between the various interaction
 * states. When the skin is being disposed, {@link #dispose()} should be called too. Also, always make sure that this
 * is and remains the first child of the component to avoid this from covering the other children.
 */
// TODO dragged state is not implemented yet
public class MaterialSurface extends Region implements MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================
    public static boolean animated = true;

    //================================================================================
    // Properties
    //================================================================================
    private Region owner;
    private MFXRippleGenerator rippleGenerator;
    private InvalidationListener stateListener;

    private final Region bg;
    private Animation animation;
    private double lastOpacity;

    //================================================================================
    // Constructors
    //================================================================================
    public MaterialSurface(Region owner) {
        this.owner = owner;

        bg = new Region();
        bg.getStyleClass().add("bg");
        bg.setOpacity(0.0);

        this.rippleGenerator = new MFXRippleGenerator(bg);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        setManaged(false);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        getStyleClass().setAll(defaultStyleClasses());
        getChildren().addAll(bg, rippleGenerator);

        stateListener = i -> handleBackground();
        owner.getPseudoClassStates().addListener(stateListener);
    }

    /**
     * Fluent way to set up the surface's {@link MFXRippleGenerator}.
     */
    public MaterialSurface initRipple(Consumer<MFXRippleGenerator> config) {
        config.accept(rippleGenerator);
        return this;
    }

    /**
     * This is the core method responsible for setting the surface's background opacity.
     * <p>
     * The opacity is determined by the current interaction state on the owner. The values are specified by:
     * {@link #hoverOpacityProperty()}, {@link #focusOpacityProperty()} and {@link #pressOpacityProperty()}.
     * <p></p>
     * The state check are delegated to: {@link #isOwnerDisabled()}, {@link #isOwnerPressed()},
     * {@link #isOwnerFocused()} and {@link #isOwnerHover()}, listed in order of priority.
     * <p></p>
     * The opacity is set immediately or through an animation started by {@link #animateBackground(double)}.
     */
    // TODO is there a way to remove at least a portion of all these ifs?
    public void handleBackground() {
        final double target;
        if (isOwnerDisabled()) {
            target = 0.0;
        } else if (isOwnerPressed()) {
            target = getPressOpacity();
        } else if (isOwnerFocused()) {
            target = getFocusOpacity();
        } else if (isOwnerHover()) {
            target = getHoverOpacity();
        } else {
            target = 0.0;
        }

        if (lastOpacity == target) return;
        if (animated && isAnimateBackground()) {
            animateBackground(target);
        } else {
            bg.setOpacity(target);
        }
        lastOpacity = target;
    }

    /**
     * @return whether {@link Node#isDisabled()} is true or the pseudo-class ':disabled' is active
     */
    public boolean isOwnerDisabled() {
        return owner.isDisabled() || PseudoClasses.DISABLED.isActiveOn(owner);
    }

    /**
     * @return whether {@link Node#isHover()} is true or the pseudo-class ':hover' is active
     */
    public boolean isOwnerHover() {
        return owner.isFocused() || PseudoClasses.HOVER.isActiveOn(owner);
    }

    /**
     * @return whether {@link Node#isFocused()} or {@link Node#isFocusWithin()} are ture or either the pseudo-classes
     * ':focused' or ':focused-within' are active
     */
    public boolean isOwnerFocused() {
        return owner.isFocused() || owner.isFocusWithin() ||
            PseudoClasses.FOCUSED.isActiveOn(owner) || PseudoClasses.FOCUS_WITHIN.isActiveOn(owner);
    }

    /**
     * @return whether {@link Node#isPressed()} is true or the pseudo-class ':focused' is active
     */
    public boolean isOwnerPressed() {
        return owner.isPressed() || PseudoClasses.PRESSED.isActiveOn(owner);
    }

    /**
     * Stops any previous animation, then creates a new one and transitions the background opacity to the target value.
     *
     * @param target the opacity needed by the new state
     */
    protected void animateBackground(double target) {
        if (Animations.isPlaying(animation)) animation.stop();
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(M3Motion.SHORT4, bg.opacityProperty(), target))
            .getAnimation();
        animation.play();
    }

    /**
     * Removes any added listener, disposes the {@link MFXRippleGenerator}.
     */
    public void dispose() {
        getChildren().clear();
        rippleGenerator.dispose();
        rippleGenerator = null;
        owner.getPseudoClassStates().removeListener(stateListener);
        stateListener = null;
        owner = null;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("surface");
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (rippleGenerator != null)
            rippleGenerator.resizeRelocate(0, 0, w, h);
        bg.resizeRelocate(0, 0, w, h);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animateBackground = new StyleableBooleanProperty(
        StyleableProperties.ANIMATE_BACKGROUND,
        this,
        "animateBackground",
        true
    );

    private final StyleableDoubleProperty hoverOpacity = new StyleableDoubleProperty(
        StyleableProperties.HOVER_OPACITY,
        this,
        "hoverOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) handleBackground();
        }
    };

    private final StyleableDoubleProperty focusOpacity = new StyleableDoubleProperty(
        StyleableProperties.FOCUS_OPACITY,
        this,
        "focusOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) handleBackground();
        }
    };

    private final StyleableDoubleProperty pressOpacity = new StyleableDoubleProperty(
        StyleableProperties.PRESS_OPACITY,
        this,
        "pressOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) handleBackground();
        }
    };

    private final StyleableObjectProperty<ElevationLevel> elevation = new StyleableObjectProperty<>(
        StyleableProperties.ELEVATION,
        this,
        "elevation",
        ElevationLevel.LEVEL0
    ) {
        @Override
        public void set(ElevationLevel newValue) {
            if (newValue == ElevationLevel.LEVEL0) {
                owner.setEffect(null);
                super.set(newValue);
                return;
            }

            Effect effect = owner.getEffect();
            if (effect == null) {
                owner.setEffect(newValue.toShadow());
                super.set(newValue);
                return;
            }
            if (!(effect instanceof DropShadow)) {
                return;
            }


            ElevationLevel oldValue = get();
            if (oldValue != null && newValue != null && oldValue != newValue)
                oldValue.animateTo((DropShadow) effect, newValue);
            super.set(newValue);
        }
    };

    public boolean isAnimateBackground() {
        return animateBackground.get();
    }

    /**
     * Specifies whether to animate the background's opacity when the interaction state changes,
     * see {@link #handleBackground()} and {@link #animateBackground(double)}.
     * <p>
     * Can be set in CSS via the property: '-mfx-animate-background'.
     */
    public StyleableBooleanProperty animateBackgroundProperty() {
        return animateBackground;
    }

    public void setAnimateBackground(boolean animateBackground) {
        this.animateBackground.set(animateBackground);
    }

    public double getHoverOpacity() {
        return hoverOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is hovered.
     * <p>
     * Can be set in CSS via the property: '-mfx-hover-opacity'.
     */
    public StyleableDoubleProperty hoverOpacityProperty() {
        return hoverOpacity;
    }

    public void setHoverOpacity(double hoverOpacity) {
        this.hoverOpacity.set(hoverOpacity);
    }

    public double getFocusOpacity() {
        return focusOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is focused.
     * <p>
     * Can be set in CSS via the property: '-mfx-focus-opacity'.
     */
    public StyleableDoubleProperty focusOpacityProperty() {
        return focusOpacity;
    }

    public void setFocusOpacity(double focusOpacity) {
        this.focusOpacity.set(focusOpacity);
    }

    public double getPressOpacity() {
        return pressOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is pressed.
     * <p>
     * Can be set in CSS via the property: '-mfx-press-opacity'.
     */
    public StyleableDoubleProperty pressOpacityProperty() {
        return pressOpacity;
    }

    public void setPressOpacity(double pressOpacity) {
        this.pressOpacity.set(pressOpacity);
    }

    public ElevationLevel getElevation() {
        return elevation.get();
    }

    /**
     * Specifies the elevation level of the owner, not the surface! Each level corresponds to a different {@link DropShadow}
     * effect. {@link ElevationLevel#LEVEL0} corresponds to {@code null}.
     * <p>
     * Unfortunately since the crap that is JavaFX, handles the effects in strange ways, the shadow cannot be applied to the
     * surface for various reasons. So, the effect will be applied on the owner instead.
     * <p>
     * Can be set in CSS via the property: '-mfx-elevation'.
     */
    public StyleableObjectProperty<ElevationLevel> elevationProperty() {
        return elevation;
    }

    public void setElevation(ElevationLevel elevation) {
        this.elevation.set(elevation);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MaterialSurface> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MaterialSurface, Boolean> ANIMATE_BACKGROUND =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animate-background",
                MaterialSurface::animateBackgroundProperty,
                true
            );

        private static final CssMetaData<MaterialSurface, Number> HOVER_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-hover-opacity",
                MaterialSurface::hoverOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, Number> FOCUS_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-focus-opacity",
                MaterialSurface::focusOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, Number> PRESS_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-press-opacity",
                MaterialSurface::pressOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, ElevationLevel> ELEVATION =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-elevation",
                MaterialSurface::elevationProperty,
                ElevationLevel.LEVEL0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Region.getClassCssMetaData(),
                ANIMATE_BACKGROUND,
                HOVER_OPACITY, FOCUS_OPACITY, PRESS_OPACITY,
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
    // Getters
    //================================================================================

    /**
     * @return the instance of the {@link MFXRippleGenerator} used by the surface
     */
    public MFXRippleGenerator getRippleGenerator() {
        return rippleGenerator;
    }
}
