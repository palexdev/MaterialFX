/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.controls;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

import static java.util.Optional.ofNullable;

/// Material Design 3 components are stratified. Different layers have different purposes. Two are particularly important:
/// the `state layer` and the `focus ring layer`.
///
/// The `state layer` is a transparent region at rest, with a color in contrast to the main layer. Depending on the
/// interaction with the component, the state layer opacity changes. Here are the defaults:
/// - On mouse hover: 8%
/// - On focused: 10%
/// - On pressed: 12%
/// - On dragged: 16%
/// The system is designed to be extensible, so you can add your own states. A state is represented by the [State] record.
/// Each state has a priority, which determines the order in which the states are applied. Only one can be active at a time.
///
/// The `focus ring layer` is an effect applied only when the component is being focused by a keyboard event, so
/// [Node#focusVisibleProperty()]. It's like an extra border spaced around the component.
///
/// On top of that, some components may also need a shadow effect to further separate themselves from other UI elements,
/// making them appear 3D. This is implemented with some caveats through the [#elevationProperty()]
///
/// The goal of this region is to replicate the effects seen in the Material Design 3 Guidelines easily, while still
/// keeping the nodes count as low as possible. Animating background in JavaFX is complicated and not very performant
/// through the [Region#setBackground(Background)] method. Recent versions introduced the possibility of animating them
/// in CSS but there are two issues still:
/// 1) It feels like magic, there doesn't seem to be any public [Animation]
/// implementation for the task, so I'm guessing it's all internal APIs (fuck that shit honestly)
/// 2) Technically, we are not simply animating a background. We are overlaying a color on top of another, so the result
/// is a combination of the two, exactly as intended by the guidelines.
///
/// The other impact on performance may come from animations. Both the opacity and the elevation (so the shadow) are
/// by default animated. You can disable them per-surface via the [#animatedProperty()] or **globally** by setting
/// [MFXSurface#ANIMATED] to `false`.
///
/// #### Note
/// When a `MFXSurface` is not needed anymore, it should be disposed by calling [#dispose()].
// TODO implement 'dragged' state
public class MFXSurface extends Region implements MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================

    /// Global flag to disable surfaces' animations throughout the entire app.
    /// For a finer control use the [#animatedProperty()].
    public static boolean ANIMATED = true;

    //================================================================================
    // Properties
    //================================================================================
    private Parent owner;

    private InvalidationListener stateListener;
    private final Queue<State> states = new PriorityQueue<>(Comparator.comparing(State::priority));

    protected Animation animation;
    protected double lastOpacity = 1.0;

    protected ElevationLevel lastElevation;
    protected DropShadow shadow;
    protected Animation elevationAnimation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSurface(Parent owner) {
        this.owner = owner;
        states.addAll(State.DEFAULT_STATES);
        init();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void init() {
        setDefaultStyleClasses();
        setManaged(false);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        double initOpacity = getTargetOpacity();
        setOpacity(initOpacity);
        lastOpacity = initOpacity;
        stateListener = _ -> updateOpacity();
        owner.getPseudoClassStates().addListener(stateListener);
    }

    /// This is the core method responsible for setting the surface's opacity according to the current interaction state
    /// with its `owner`. A common place where we can capture state changes is the [Styleable#getPseudoClassStates()].
    /// However, note that before performing a lookup in that [Set], preset states first check properties
    /// on the node (such as [Node#isHover()], [Node#isPressed()], etc...), which is faster.
    ///
    /// The new opacity value is determined by [#getTargetOpacity()], and it can be set immediately or by an animation
    /// started in [#animate(double)].
    public void updateOpacity() {
        double target = getTargetOpacity();
        if (lastOpacity == target) return;
        if (ANIMATED && isAnimated()) {
            animate(target);
        } else {
            setOpacity(target);
        }
        lastOpacity = target;
    }

    /// Stops the previous animation if still playing, creates a new one and brings the surface's opacity to the given
    /// target value.
    protected void animate(double opacity) {
        if (Animations.isPlaying(animation)) animation.stop();
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(M3Motion.SHORT4, opacityProperty(), opacity))
            .getAnimation();
        animation.play();
    }

    /// Iterates over the [#getStates()] queue, finds the first that is active, and retrieves the associated target opacity.
    /// If no state is active, [State#DEFAULT] is used, which will result in a transparent surface.
    public double getTargetOpacity() {
        return states.stream()
            .filter(s -> s.isActive(owner))
            .findFirst()
            .map(s -> s.opacity(this))
            .orElse(State.DEFAULT.opacity(this));
    }

    /// Disposes the surface by unregistering any listener and setting the `owner` to `null`.
    public void dispose() {
        states.clear();
        if (Animations.isPlaying(elevationAnimation)) elevationAnimation.stop();
        if (shadow != null) owner.setEffect(null);
        shadow = null;
        elevationAnimation = null;
        owner.getPseudoClassStates().removeListener(stateListener);
        stateListener = null;
        owner = null;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("surface");
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

    private final StyleableDoubleProperty defaultOpacity = new StyleableDoubleProperty(
        StyleableProperties.DEFAULT_OPACITY,
        this,
        "defaultOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty disabledOpacity = new StyleableDoubleProperty(
        StyleableProperties.DISABLED_OPACITY,
        this,
        "disabledOpacity",
        0.1
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty pressedOpacity = new StyleableDoubleProperty(
        StyleableProperties.PRESSED_OPACITY,
        this,
        "pressedOpacity",
        0.12
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty focusedOpacity = new StyleableDoubleProperty(
        StyleableProperties.FOCUSED_OPACITY,
        this,
        "focusedOpacity",
        0.10
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty hoverOpacity = new StyleableDoubleProperty(
        StyleableProperties.HOVER_OPACITY,
        this,
        "hoverOpacity",
        0.08
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableObjectProperty<ElevationLevel> elevation = new StyleableObjectProperty<>(
        StyleableProperties.ELEVATION,
        this,
        "elevation",
        ElevationLevel.LEVEL0
    ) {
        @Override
        protected void invalidated() {
            ElevationLevel lvl = ofNullable(get()).orElse(ElevationLevel.LEVEL0);
            ElevationLevel last = lastElevation;
            lastElevation = lvl;

            if (Animations.isPlaying(elevationAnimation))
                elevationAnimation.stop();

            if (last == null || !ANIMATED || !isAnimated()) {
                shadow = (lvl == ElevationLevel.LEVEL0) ? null : lvl.toShadow();
                owner.setEffect(shadow);
                return;
            }

            if (shadow == null) {
                if (lvl == ElevationLevel.LEVEL0) return;
                shadow = ElevationLevel.LEVEL0.toShadow();
                owner.setEffect(shadow);
            }
            elevationAnimation = ElevationLevel.animation(shadow, lvl);
            if (lvl == ElevationLevel.LEVEL0)
                elevationAnimation.setOnFinished(_ -> {
                    owner.setEffect(null);
                    shadow = null;
                });
            elevationAnimation.play();
        }
    };

    public boolean isAnimated() {
        return animated.get();
    }

    /// Specifies whether to animate the background's opacity when the interaction state changes,
    /// [#updateOpacity()] and [#animate(double)].
    ///
    /// Can be set from CSS via the property: '-mfx-animated'.
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getDefaultOpacity() {
        return defaultOpacity.get();
    }

    public StyleableDoubleProperty defaultOpacityProperty() {
        return defaultOpacity;
    }

    public void setDefaultOpacity(double defaultOpacity) {
        this.defaultOpacity.set(defaultOpacity);
    }

    public double getDisabledOpacity() {
        return disabledOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is disabled.
    ///
    /// Can be set from CSS via the property: '-mfx-disabled-opacity'.
    public StyleableDoubleProperty disabledOpacityProperty() {
        return disabledOpacity;
    }

    public void setDisabledOpacity(double disabledOpacity) {
        this.disabledOpacity.set(disabledOpacity);
    }

    public double getPressedOpacity() {
        return pressedOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is pressed.
    ///
    /// Can be set from CSS via the property: '-mfx-pressed-opacity'.
    public StyleableDoubleProperty pressedOpacityProperty() {
        return pressedOpacity;
    }

    public void setPressedOpacity(double pressedOpacity) {
        this.pressedOpacity.set(pressedOpacity);
    }

    public double getFocusedOpacity() {
        return focusedOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is focused.
    ///
    /// Can be set from CSS via the property: '-mfx-focused-opacity'.
    public StyleableDoubleProperty focusedOpacityProperty() {
        return focusedOpacity;
    }

    public void setFocusedOpacity(double focusedOpacity) {
        this.focusedOpacity.set(focusedOpacity);
    }

    public double getHoverOpacity() {
        return hoverOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is hovered.
    ///
    /// Can be set from CSS via the property: '-mfx-hover-opacity'.
    public StyleableDoubleProperty hoverOpacityProperty() {
        return hoverOpacity;
    }

    public void setHoverOpacity(double hoverOpacity) {
        this.hoverOpacity.set(hoverOpacity);
    }

    public ElevationLevel getElevation() {
        return elevation.get();
    }

    /// Specifies the elevation level of the owner, not the surface! Each level corresponds to a different [DropShadow]
    /// effect. At [ElevationLevel#LEVEL0] no effect is set on the owner at all, since effects are expensive. A zeroed
    /// shadow is installed on demand only to animate towards a higher level, and removed once the animation back to
    /// [ElevationLevel#LEVEL0] ends.
    ///
    /// Unfortunately, since the crap that is JavaFX, handles the effects in strange ways, the shadow cannot be applied to the
    /// surface for various reasons. So, the effect will be applied on the owner instead.
    ///
    /// Can be set from CSS via the property: '-mfx-elevation'.
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
        private static final StyleablePropertyFactory<MFXSurface> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXSurface, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MFXSurface::animatedProperty,
                true
            );

        private static final CssMetaData<MFXSurface, Number> DEFAULT_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-default-opacity",
                MFXSurface::defaultOpacityProperty,
                0.0
            );

        private static final CssMetaData<MFXSurface, Number> DISABLED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-disabled-opacity",
                MFXSurface::disabledOpacityProperty,
                0.1
            );

        private static final CssMetaData<MFXSurface, Number> PRESSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-press-opacity",
                MFXSurface::pressedOpacityProperty,
                0.12
            );

        private static final CssMetaData<MFXSurface, Number> FOCUSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-focus-opacity",
                MFXSurface::focusedOpacityProperty,
                0.10
            );

        private static final CssMetaData<MFXSurface, Number> HOVER_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-hover-opacity",
                MFXSurface::hoverOpacityProperty,
                0.08
            );

        private static final CssMetaData<MFXSurface, ElevationLevel> ELEVATION =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-elevation",
                MFXSurface::elevationProperty,
                ElevationLevel.LEVEL0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Region.getClassCssMetaData(),
                ANIMATED,
                DEFAULT_OPACITY, DISABLED_OPACITY, PRESSED_OPACITY, FOCUSED_OPACITY, HOVER_OPACITY,
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

    /// @return the [Parent] on which this surface is applied.
    public Parent getOwner() {
        return owner;
    }

    /// @return the [PriorityQueue] which determines the order of the various interaction states with the surface's owner.
    public Queue<State> getStates() {
        return states;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// This record represents interaction states for a [MFXSurface]. There are three values:
    /// 1) the [#priority()] of the state, which determines the order in which the states are considered.
    /// 2) the [#condition()] which determines whether the state is active or not.
    /// 3) the [#opacity()] function which determines the opacity of the surface when the condition is met.
    ///
    /// There are five default states:
    /// 1) [#DEFAULT]
    /// 2) [#DISABLED]
    /// 3) [#PRESSED]
    /// 4) [#FOCUSED]
    /// 5) [#HOVER]
    /// All included in a pre-made list for convenience, [#DEFAULT_STATES].
    public record State(
        int priority,
        Predicate<Parent> condition,
        Function<MFXSurface, Double> opacity
    ) {
        //================================================================================
        // Defaults
        //================================================================================

        /// Special state whose predicate is always `true`. Used when none of the other states is active.
        /// The opacity is retrieved from [MFXSurface#defaultOpacityProperty()].
        public static final State DEFAULT = State.of(Integer.MIN_VALUE, _ -> true, MFXSurface::getDefaultOpacity);

        /// This state is activated when the node is disabled or the [PseudoClass] `:disabled` is active.
        /// The opacity is retrieved from [MFXSurface#disabledOpacityProperty()].
        public static final State DISABLED = State.of(
            0,
            n -> n.isDisabled() || PseudoClasses.DISABLED.isActiveOn(n),
            MFXSurface::getDisabledOpacity
        );

        /// This state is activated when the node is pressed or the [PseudoClass] `:pressed` is active.
        /// The opacity is retrieved from [MFXSurface#pressedOpacityProperty()].
        public static final State PRESSED = State.of(
            1,
            n -> n.isPressed() || PseudoClasses.PRESSED.isActiveOn(n),
            MFXSurface::getPressedOpacity
        );

        /// This state is activated when the node or any of its children are focused. Or the [PseudoClasses][PseudoClass]
        /// `:focused` or `:focus-within` are active.
        /// The opacity is retrieved from [MFXSurface#focusedOpacityProperty()].
        public static final State FOCUSED = State.of(
            2,
            n -> (n.isFocused() || n.isFocusWithin() || PseudoClasses.isActiveOn(n, "focused", "focus-within")),
            MFXSurface::getFocusedOpacity
        );

        /// This state is activated when the node is hovered or the [PseudoClass] `:hover` is active.
        /// The opacity is retrieved from [MFXSurface#hoverOpacityProperty()].
        public static final State HOVER = State.of(
            3,
            n -> n.isHover() || PseudoClasses.HOVER.isActiveOn(n),
            MFXSurface::getHoverOpacity
        );

        /// This list contains all the default states, common to pretty much any surface/component. The order by priority
        /// is: disabled, pressed, focused, hover.
        public static final List<State> DEFAULT_STATES = List.of(DISABLED, PRESSED, FOCUSED, HOVER);

        //================================================================================
        // Constructors
        //================================================================================
        public static State of(int priority, Predicate<Parent> condition, Function<MFXSurface, Double> opacity) {
            return new State(priority, condition, opacity);
        }

        //================================================================================
        // Methods
        //================================================================================

        /// Shortcut for `condition.test(node)`.
        public boolean isActive(Parent node) {
            return condition.test(node);
        }

        /// Shortcut for `opacity.apply(surface)`.
        public double opacity(MFXSurface surface) {
            return opacity.apply(surface);
        }
    }
}
