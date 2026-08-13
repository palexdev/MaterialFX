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

import java.util.List;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import javafx.animation.Animation;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;
import static java.util.Optional.ofNullable;

/// Material Design 3 components are stratified. This region is the `state layer`: transparent at rest, with a color in
/// contrast to the main layer, its opacity changing according to the interaction with its `owner`.
///
/// The opacity is driven entirely by CSS. JavaFX propagates `:disabled` down the scene graph and sets `:hover`/`:pressed`
/// on the picked node, so the layer is styled by rules keyed on the owner's state, e.g. `*:hover > .surface`.
/// Note that `:focused` is set **only** on the focus owner, so a focus state layer must be selected through the owner
/// via [Node#focusVisibleProperty()], never on the surface itself.
///
/// Some components may also need a shadow effect to further separate themselves from other UI elements, making them
/// appear 3D. This is implemented with some caveats through the [#elevationProperty()], and it is the only part of this
/// region still animated in Java.
///
/// #### Note
/// When a `MFXSurface` is not needed anymore, it should be disposed by calling [#dispose()].
public class MFXSurface extends Region implements MFXStyleable, Disposable {

    //================================================================================
    // Properties
    //================================================================================

    private Parent owner;

    protected ElevationLevel lastElevation;
    protected DropShadow shadow;
    protected Animation elevationAnimation;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSurface(Parent owner) {
        this.owner = owner;
        setDefaultStyleClasses();
        setManaged(false);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("surface");
    }

    @Override
    public void dispose() {
        if (Animations.isPlaying(elevationAnimation)) elevationAnimation.stop();
        if (shadow != null) owner.setEffect(null);
        shadow = null;
        elevationAnimation = null;
        owner = null;
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

            if (last == null || !isAnimated()) {
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

    /// Specifies whether to animate the [DropShadow] effect applied by [#elevationProperty()].
    ///
    /// Can be set from CSS via the property: '-mfx-animated'.
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
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
                ANIMATED, ELEVATION
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
}
