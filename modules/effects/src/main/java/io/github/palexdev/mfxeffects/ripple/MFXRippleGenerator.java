package io.github.palexdev.mfxeffects.ripple;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.beans.Size;
import io.github.palexdev.mfxeffects.beans.properties.styleable.StyleableSizeProperty;
import io.github.palexdev.mfxeffects.enums.MouseMode;
import io.github.palexdev.mfxeffects.enums.RippleState;
import io.github.palexdev.mfxeffects.ripple.base.Ripple;
import io.github.palexdev.mfxeffects.ripple.base.RippleGenerator;
import io.github.palexdev.mfxeffects.ripple.base.RippleGeneratorBase;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import javafx.beans.binding.Bindings;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Component capable of generating the ripple effect described by the Material Design Guidelines.
 * Adapted to JavaFX, this generator is capable, in combination with {@link CircleRipple}, of creating an
 * effect that is very close to the one seen in Material 3.
 * <p>
 * Extends {@link RippleGeneratorBase} and thus also implementing {@link RippleGenerator}, with some minor additions.
 * <p></p>
 * The generator is a contained surface, meaning that all the animations/effects are actuated on itself or in contained
 * nodes. Yet, it is not an independent component. A {@link Region} that will be the generator's {@code owner} is a must
 * requirement. Not only that, it's important to also notice that the owner must add the generator among its children
 * and properly layout it (mostly resize it, location will usually always be at [0, 0]).
 * There are alternative ways (for example wrapping both the generator and the node on which the user wants the effect
 * in a StackPane or similar, the owner will be the wrapping container), but the one described is the most usual way.
 * <p>
 * A generator and this is part of the base API, also needs to know where to generate the ripple effect. Most of the time,
 * the position will come from a {@link MouseEvent} occurred on the owner. However, consider this example, you have a button
 * with the generator incorporated, the button can be triggered by pressing the ENTER key too, in such case you probably still
 * want to communicate the interaction to the user through a ripple effect. In such cases it's enough to add a handler for
 * the desired event type, and generate the ripple at the center of the component.
 * <p>
 * Ideally the generator should be 'de-/activated' through the methods {@link #enable()} and {@link #disable()}, if disposal
 * is needed instead of just disable, call {@link #dispose()}. However, nothing prevents you from specifying your own
 * handlers.
 * <p>
 * <p></p>
 * I want to spend some lines describing the generator's working too.
 * <p>
 * Compared to old implementations this is more efficient, simpler but also a bit more complex.
 * <p> - It's more efficient because now everything is cached, from the ripple to the clip to all the animations. Everything is
 * re-built only when needed and this is huge for performance, great. Everything needed by the generator is kept in a
 * 'wrapping' class, that represents its state, {@link GeneratorState}.
 * <p> - It's simpler because many properties and methods have been removed, the generator has not been stripped of its features,
 * they were just useless
 * <p> - It's a bit more complex because now the effect generation is divided in two times:
 * there's the generation phase and the release phase. This has been done to mimic very closely the behavior of the original
 * Material Design ripple effect, more info here: {@link #generate(double, double)}, {@link #release()}, {@link CircleRipple};
 * That said, one could also implement a ripple that does everything during just one of the two phases, there are many ways to
 * do it.
 * <p></p>
 * Last but not least about the generation, there are a few conditions that can prevent the generator from 'activating'.
 * <p> 1) Users can disable the generator in various ways: by disabling it, by setting its visibility to hidden
 * (can be done in CSS too, very convenient), or setting its opacity to 0.0 (also settable in CSS)
 * <p> 2) By default the generator will check if the given coordinates are withing the owner's bounds, if that is not the
 * case the effect won't be generated, check {@link #checkBoundsProperty()} and {@link #canGenerateAt(double, double)} for
 * more info
 * <p>
 * The public API which defines the common features and behaviors of ripple generators ({@link RippleGenerator}), only
 * specifies a {@link #generate(double, double)} method that takes the two needed coordinates. One small addition in this
 * implementation is an easy way to generate effects from a {@link MouseEvent}, see {@link #generate(MouseEvent)}.
 * <p></p>
 * Typically, ripple effects are just circles that fade in/out and grow, this is the default type used by the generator
 * and specified by {@link #defaultRippleSupplier()}. Users can define new shapes implementing the base API for ripples,
 * {@link Ripple}, and set it on the generator through {@link #setRippleSupplier(Supplier)}.
 * <p>
 * Material Design show that the effect also animates the background surface, this is probably for a smoother transition.
 * This feature here is disabled by default and can be enabled/customized as needed with {@link #animateBackgroundProperty()}
 * and {@link #backgroundColorProperty()}
 * <p></p>
 * Last but not least, the generator is also clipped to avoid the effect trespassing the generator's bounds (which typically
 * correspond to the ones of the owner). There are three ways to clip the generator by setting the {@link Supplier} responsible
 * for creating the clip node:
 * <p> 1) If you set a {@code null} supplier, the effect will be unbound, it will be able to trespass the bounds
 * <p> 2) If you set the supplier to return {@code null} then the clip node will be automatically generated based on the
 * owner's geometry
 * <p> 3) If you set the supplier to return a valid node than it will be used as the clip
 * <p> See also {@link #buildClip()}
 */
public class MFXRippleGenerator extends RippleGeneratorBase {
    //================================================================================
    // Static Properties
    //================================================================================
    public static final Color DEFAULT_RIPPLE_COLOR = Color.rgb(107, 107, 107, 0.12);

    //================================================================================
    // Properties
    //================================================================================
    private Region owner;
    protected GeneratorState state;

    private Supplier<Region> clipSupplier = () -> null;
    private Supplier<Ripple<?>> rippleSupplier = defaultRippleSupplier();
    private Function<MouseEvent, Position> meToPosConverter = e -> Position.of(e.getX(), e.getY());

    private EventHandler<MouseEvent> pressed;
    private EventHandler<MouseEvent> released;
    private EventHandler<MouseEvent> exited;
    private boolean disposed = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXRippleGenerator(Region owner) {
        this.owner = owner;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClass());
        setManaged(false);
        onMouseModeChanged();
    }

    /**
     * Uses the {@link Function} set by {@link #setMeToPosConverter(Function)} to convert the give {@link MouseEvent}
     * and feed it to {@link #generate(Position)}.
     */
    public void generate(MouseEvent me) {
        generate(meToPosConverter.apply(me));
    }

    /**
     * Responsible for updating the generator's state when needed. This will also trigger a layout pass to ensure that
     * the generator's clip is positioned and sized correctly.
     */
    protected void updateState() {
        state = new GeneratorState(this);
        state.init();
        requestLayout(); // In case the clip changes, this ensures it is positioned and sized
    }

    /**
     * Automatically called when {@link #mouseModeProperty()} changes to update the way {@link MouseEvent}s are handled.
     * <p></p>
     * <p> 1) {@link MouseMode#OFF}: mouseTransparent 'false' and pickOnBounds 'true'
     * <p> 2) {@link MouseMode#DONT_PICK_ON_BOUNDS}: mouseTransparent 'false' and pickOnBounds 'false'
     * <p> 3) {@link MouseMode#MOUSE_TRANSPARENT}: both mouseTransparent and pickOnBounds 'true'
     */
    protected void onMouseModeChanged() {
        MouseMode mode = getMouseMode();
        switch (mode) {
            case OFF: {
                setPickOnBounds(true);
                setMouseTransparent(false);
                break;
            }
            case DONT_PICK_ON_BOUNDS: {
                setPickOnBounds(false);
                setMouseTransparent(false);
                break;
            }
            case MOUSE_TRANSPARENT:
                setPickOnBounds(true);
                setMouseTransparent(true);
                break;
        }
    }

    /**
     * @return whether the generator has been disabled, set to be hidden, or it's opacity set to 0.0
     * @see #disableProperty()
     * @see #visibleProperty()
     * @see #opacityProperty()
     */
    public boolean isGeneratorDisabled() {
        return isDisabled() || !isVisible() || getOpacity() == 0;
    }

    /**
     * @return whether the given position for the ripple in inside the bounds of the generator's owner
     */
    public boolean canGenerateAt(double x, double y) {
        if (!isCheckBounds()) return true;
        return owner.getLayoutBounds().contains(x, y);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * Adds these three handlers on the owner region:
     * <p> 1) An handler for {@link MouseEvent#MOUSE_PRESSED} events that will trigger {@link #generate(MouseEvent)}
     * <p> 2) An handler for {@link MouseEvent#MOUSE_RELEASED} events that will trigger {@link #release()}
     * <p> 3) An handler for {@link MouseEvent#MOUSE_EXITED} events that also will trigger {@link #release()};
     * Material Design Guidelines show that the ripple effect should disappear if the mouse is still pressed but goes outside
     * the node
     * <p></p>
     * Little side note, if this was already enabled before, or it was disposed, does nothing.
     */
    @Override
    public void enable() {
        if (disposed) return;
        if (pressed == null) {
            pressed = this::generate;
            released = e -> release();
            exited = e -> release();

            owner.addEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
            owner.addEventHandler(MouseEvent.MOUSE_RELEASED, released);
            owner.addEventHandler(MouseEvent.MOUSE_EXITED, exited);
        }
    }

    /**
     * Removes and sets to {@code null} the handlers build and added by {@link #enable()}.
     */
    @Override
    public void disable() {
        if (pressed != null) {
            owner.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
            owner.removeEventHandler(MouseEvent.MOUSE_RELEASED, released);
            owner.removeEventHandler(MouseEvent.MOUSE_EXITED, exited);
        }
        pressed = null;
        released = null;
        exited = null;
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Exits immediately if the generator has been disabled ({@link #isGeneratorDisabled()}) or if the ripple cannot
     * be positioned at the given coordinated, {@link #canGenerateAt(double, double)}.
     * <p></p>
     * Sets the ripple state to {@link RippleState#WAITING_FOR_CLICK}, then asks the ripple node to reposition through
     * {@link Ripple#position(double, double)} and finally asks the ripple to play the in animations, {@link Ripple#playIn()}.
     */
    @Override
    public void generate(double x, double y) {
        if (isGeneratorDisabled() || !canGenerateAt(x, y)) return;
        state.rippleState = RippleState.WAITING_FOR_CLICK;
        state.ripple.position(x, y);
        state.ripple.playIn();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Exits immediately if the current ripple state is {@link RippleState#INACTIVE}.
     * <p></p>
     * Otherwise calls {@link Ripple#playOut()} and finally set the ripple state to {@link RippleState#INACTIVE}, thus
     * preventing further 'releases' occurring.
     */
    @Override
    public void release() {
        if (state.rippleState == RippleState.INACTIVE) return;
        state.ripple.playOut();
        state.rippleState = RippleState.INACTIVE;
    }

    /**
     * Creates the generator's clip node depending on the set {@link #getClipSupplier()}.
     * <p></p>
     * <p> 1) If you set a {@code null} supplier, the effect will be unbound, it will be able to trespass the bounds
     * <p> 2) If you set the supplier to return {@code null} then the clip node will be automatically generated based on the
     * owner's geometry
     * <p> 3) If you set the supplier to return a valid node than it will be used as the clip
     * <p></p>
     * To further explain the second case. This automatic geometry detection algorithm is not perfect but should work most
     * of the time. The clip will look at the owner's background and border and check whether a radius is applied on either
     * one of them. This detection is done by {@link StyleUtils#parseCornerRadius(Region)}.
     */
    @Override
    public Region buildClip() {
        Supplier<Region> supplier = getClipSupplier();
        if (supplier == null) return null;

        Region clip = supplier.get();
        if (clip != null) return clip;

        clip = new Region();
        clip.backgroundProperty().bind(Bindings.createObjectBinding(
                () -> {
                    CornerRadii radius = StyleUtils.parseCornerRadius(owner);
                    BackgroundFill fill = new BackgroundFill(Color.WHITE, radius, Insets.EMPTY);
                    return new Background(fill);
                },
                owner.backgroundProperty(), owner.borderProperty()
        ));
        return clip;
    }

    /**
     * Removes the ripple node, disables the generator, disposes the state and finally sets the owner to null,
     * making the generator not usable anymore!
     */
    @Override
    public void dispose() {
        disposed = true;
        getChildren().clear();
        disable();
        if (state != null) state.dispose();
        owner = null;
    }

    @Override
    protected void layoutChildren() {
        if (disposed) return;
        double w = getWidth();
        double h = getHeight();
        if (state == null) {
            updateState();
            state.clip.resizeRelocate(0, 0, w, h);
            return;
        }

        if (Size.of(w, h).equals(state.bounds)) {
            if (state.clipUpdated) {
                state.clip.resizeRelocate(0, 0, w, h);
                state.clipUpdated = false;
            }
            return;
        }
        updateState();
        state.clip.resizeRelocate(0, 0, w, h);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animateBackground = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATE_BACKGROUND,
            this,
            "animateBackground",
            false
    ) {
        @Override
        protected void invalidated() {
            if (state != null)
                state.updateRipple();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableObjectProperty<Color> backgroundColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.BACKGROUND_COLOR,
            this,
            "backgroundColor",
            DEFAULT_RIPPLE_COLOR
    ) {
        @Override
        protected void invalidated() {
            if (state != null && doAnimateBackground())
                state.updateRipple();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableObjectProperty<Color> rippleColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.RIPPLE_COLOR,
            this,
            "rippleColor",
            DEFAULT_RIPPLE_COLOR
    ) {
        @Override
        protected void invalidated() {
            if (state != null)
                state.updateRipple();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableSizeProperty ripplePrefSize = new StyleableSizeProperty(
            StyleableProperties.RIPPLE_PREF_SIZE,
            this,
            "ripplePrefSize",
            Size.invalid()
    ) {
        @Override
        protected void invalidated() {
            if (state != null)
                state.updateRipple();
        }
    };

    private final StyleableBooleanProperty checkBounds = new SimpleStyleableBooleanProperty(
            StyleableProperties.CHECK_BOUNDS,
            this,
            "checkBounds",
            true
    ) {
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    private final StyleableObjectProperty<MouseMode> mouseMode = new SimpleStyleableObjectProperty<>(
            StyleableProperties.MOUSE_MODE,
            this,
            "mouseMode",
            MouseMode.MOUSE_TRANSPARENT
    ) {
        @Override
        protected void invalidated() {
            onMouseModeChanged();
        }

        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.USER_AGENT;
        }
    };

    @Override
    public boolean doAnimateBackground() {
        return animateBackground.get();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Can be set in CSS via the property: '-mfx-animate-background";
     */
    @Override
    public StyleableBooleanProperty animateBackgroundProperty() {
        return animateBackground;
    }

    @Override
    public void setAnimateBackground(boolean animateBackground) {
        this.animateBackground.set(animateBackground);
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Can be set in CSS via the property: '-mfx-background-color";
     */
    @Override
    public StyleableObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    @Override
    public Color getRippleColor() {
        return rippleColor.get();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Can be set in CSS via the property: '-mfx-ripple-color";
     */
    @Override
    public StyleableObjectProperty<Color> rippleColorProperty() {
        return rippleColor;
    }

    @Override
    public void setRippleColor(Color rippleColor) {
        this.rippleColor.set(rippleColor);
    }

    @Override
    public Size getRipplePrefSize() {
        return ripplePrefSize.get();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Can be set in CSS via the property: '-mfx-ripple-pref-size";
     */
    @Override
    public StyleableSizeProperty ripplePrefSizeProperty() {
        return ripplePrefSize;
    }

    @Override
    public void setRipplePrefSize(Size ripplePrefSize) {
        this.ripplePrefSize.set(ripplePrefSize);
    }

    public boolean isCheckBounds() {
        return checkBounds.get();
    }

    /**
     * Specifies whether the generator should first check the coordinates given to {@link #generate(double, double)} before
     * generating the effect. See {@link #canGenerateAt(double, double)}.
     * <p></p>
     * Can be set in CSS via the property: '-mfx-check-bounds";
     */
    public StyleableBooleanProperty checkBoundsProperty() {
        return checkBounds;
    }

    public void setCheckBounds(boolean checkBounds) {
        this.checkBounds.set(checkBounds);
    }

    public MouseMode getMouseMode() {
        return mouseMode.get();
    }

    /**
     * Allows to specify the behavior of the generator regarding {@link MouseEvent}s. It may happen (for rounded components in particular)
     * that Mouse events are intercepted outside the node and thus triggering pseudo states when not intended.
     * <p></p>
     * By default, it's set to {@link MouseMode#MOUSE_TRANSPARENT} so that the generator ignores any {@link MouseEvent},
     * in the vast majority of cases, other nodes are responsible for intercepting the event that will generate the ripple
     * anyway, so I don't see why the generator should intercept the events. However, I still wanted to keep this configurable
     * for any exception.
     * <p>
     * Can be set in CSS via the property: '-mfx-mouse-mode'.
     */
    public StyleableObjectProperty<MouseMode> mouseModeProperty() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        this.mouseMode.set(mouseMode);
    }

    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXRippleGenerator> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXRippleGenerator, Boolean> ANIMATE_BACKGROUND =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-animate-background",
                        MFXRippleGenerator::animateBackgroundProperty,
                        false
                );

        private static final CssMetaData<MFXRippleGenerator, Color> BACKGROUND_COLOR =
                FACTORY.createColorCssMetaData(
                        "-mfx-background-color",
                        MFXRippleGenerator::backgroundColorProperty,
                        DEFAULT_RIPPLE_COLOR
                );

        private static final CssMetaData<MFXRippleGenerator, Color> RIPPLE_COLOR =
                FACTORY.createColorCssMetaData(
                        "-mfx-ripple-color",
                        MFXRippleGenerator::rippleColorProperty,
                        DEFAULT_RIPPLE_COLOR
                );

        private static final CssMetaData<MFXRippleGenerator, Size> RIPPLE_PREF_SIZE =
                StyleableSizeProperty.metaDataFor(
                        "-mfx-ripple-pref-size",
                        MFXRippleGenerator::ripplePrefSizeProperty,
                        Size.invalid()
                );

        private static final CssMetaData<MFXRippleGenerator, Boolean> CHECK_BOUNDS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-check-bounds",
                        MFXRippleGenerator::checkBoundsProperty,
                        true
                );

        private static final CssMetaData<MFXRippleGenerator, MouseMode> MOUSE_MODE =
                FACTORY.createEnumCssMetaData(
                        MouseMode.class,
                        "-mfx-mouse-mode",
                        MFXRippleGenerator::mouseModeProperty,
                        MouseMode.MOUSE_TRANSPARENT
                );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                    Region.getClassCssMetaData(),
                    ANIMATE_BACKGROUND, BACKGROUND_COLOR,
                    RIPPLE_COLOR, RIPPLE_PREF_SIZE,
                    CHECK_BOUNDS, MOUSE_MODE
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

    /**
     * @return the default style class of this generator
     */
    public final String defaultStyleClass() {
        return "mfx-ripple-generator";
    }

    @Override
    public Region getOwner() {
        return owner;
    }

    /**
     * @return the current ripple effect state, see {@link RippleState}
     */
    public RippleState getRippleState() {
        return Optional.ofNullable(state)
                .map(GeneratorState::getRippleState)
                .orElse(RippleState.INACTIVE);
    }

    @Override
    public Supplier<Region> getClipSupplier() {
        return clipSupplier;
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * If the generator' state is not null, this is also responsible for calling {@link GeneratorState#updateClip()}.
     */
    @Override
    public void setClipSupplier(Supplier<Region> clipSupplier) {
        this.clipSupplier = clipSupplier;
        if (state != null) state.updateClip();
    }

    @Override
    public Supplier<Ripple<?>> getRippleSupplier() {
        return rippleSupplier;
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * If the generator' state is not null, this is also responsible for calling {@link GeneratorState#updateRipple()}.
     */
    @Override
    public void setRippleSupplier(Supplier<Ripple<?>> rippleSupplier) {
        this.rippleSupplier = rippleSupplier;
        if (state != null) state.updateRipple();
    }

    /**
     * @return the {@link Function} used by the generator to convert a {@link MouseEvent} to a {@link Position}
     * bean, which will be used as the coordinates at which animate the ripple
     */
    public Function<MouseEvent, Position> getMeToPosConverter() {
        return meToPosConverter;
    }

    /**
     * Sets the {@link Function} used by the generator to convert a {@link MouseEvent} to a {@link Position}
     * bean, which will be used as the coordinates at which animate the ripple
     */
    public void setMeToPosConverter(Function<MouseEvent, Position> meToPosConverter) {
        this.meToPosConverter = meToPosConverter;
    }

    /**
     * @return whether the generator was disposed before
     */
    public boolean isDisposed() {
        return disposed;
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * Little wrapper class that holds info about a ripple generator' state such as:
     * <p> - the current state of the effect, see {@link RippleState}
     * <p> - the generator' sizes (width and height)
     * <p> - the generator's clip node
     * <p> - the generator's ripple node
     * <p></p>
     * When a new state is created, {@link #init()} must be called after so that the aforementioned properties can be
     * initialized. A new state is not always required and mostly happens when the generator's bounds change, since these
     * values may also influence animations. If properties such as {@link RippleGenerator#getRippleSupplier()} or
     * {@link RippleGenerator#getClipSupplier()} change, then it's enough to update just what's needed.
     */
    public static class GeneratorState {
        private MFXRippleGenerator generator;
        private RippleState rippleState = RippleState.INACTIVE;

        private Size bounds;
        private Node clip;
        private Ripple<?> ripple;
        private boolean clipUpdated = false;

        public GeneratorState(MFXRippleGenerator generator) {
            this.generator = generator;
        }

        /**
         * Takes and saves the generator's bounds, calls {@link #updateClip()} and then {@link #buildRipple()}.
         */
        protected void init() {
            bounds = Size.of(generator.getWidth(), generator.getHeight());
            updateClip();
            updateRipple();
        }

        /**
         * Calls {@link RippleGeneratorBase#buildClip()} to create the generator's clip, then sets it.
         */
        protected void updateClip() {
            clip = generator.buildClip();
            generator.setClip(clip);
        }

        /**
         * Calls {@link RippleGeneratorBase#buildRipple()} to create the ripple node, then initializes it through
         * {@link Ripple#init()}, and finally calls {@link RippleGeneratorBase#setRipple(Ripple)}.
         */
        protected void updateRipple() {
            ripple = generator.buildRipple();
            ripple.init();
            generator.setRipple(ripple);
        }

        /**
         * Disposes this state by setting everything to null and removing the generator's clip.
         */
        protected void dispose() {
            bounds = null;
            generator.setClip((null));
            clip = null;
            ripple = null;
            generator = null;
        }

        /**
         * @return the generator's instance this state refers to
         */
        public RippleGeneratorBase getGenerator() {
            return generator;
        }

        /**
         * @return the ripple effect state, see {@link RippleState}
         */
        public RippleState getRippleState() {
            return rippleState;
        }

        /**
         * @return the generator's sizes
         */
        public Size getBounds() {
            return bounds;
        }

        /**
         * @return the generator's clip node
         */
        public Node getClip() {
            return clip;
        }

        /**
         * @return the generator's ripple node
         */
        public Ripple<?> getRipple() {
            return ripple;
        }

        /**
         * @return whether the clip node needs to be updated
         */
        public boolean wasClipUpdated() {
            return clipUpdated;
        }
    }
}
