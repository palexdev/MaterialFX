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
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.skins.MFXCircularProgressIndicatorSkin;
import io.github.palexdev.mfxcomponents.skins.MFXLinearProgressIndicatorSkin;
import io.github.palexdev.mfxcomponents.variants.ProgressIndicatorVariants.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.ProgressIndicatorVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ProgressIndicatorVariants.WaveVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXControl;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.fx.PropUtils;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;
import static java.lang.Math.clamp;

/// Implementation of Material Design's 'Progress Indicators'. Extends [MFXControl], uses either
/// [MFXLinearProgressIndicatorSkin] or [MFXCircularProgressIndicatorSkin] depending on the applied
/// [ShapeVariant]. The default CSS style class is `.mfx-progress-indicator`.
///
/// Progress indicators show the status of a process, either as a known fraction of the work
/// ('determinate') or as an unspecified wait ('indeterminate'). The two modes are not separate
/// components here, they are just the two states of the [#progressProperty()]: any negative value,
/// for which the [#INDETERMINATE] constant is provided, switches the indicator to the indeterminate
/// animation and activates the `:indeterminate` pseudo class.
///
/// Like other components, this has variants which determine its shape and size. They are implemented
/// through the [WithVariants] API. These are the available configs:
/// - [ShapeVariant] determines the anatomy, linear or circular, can be set through [#setShape(ShapeVariant)].
///   Changing it swaps the skin
/// - [SizeVariant] determines the thickness, can be set through [#setSize(SizeVariant)]
/// - [WaveVariant] determines whether the active indicator is a straight line/arc or a waveform,
///   can be set through [#setWave(WaveVariant)]
/// The defaults are applied by [#defaultVariants()] upon initialization.
///
/// #### Sizing
///
/// Every measure of this component descends from the thickness of the track and of the active indicator,
/// which the skins read from CSS (`-fx-stroke-width` on the `.track` and `.indicator` children, driven by
/// the size variants). The linear indicator's height and the circular indicator's diameter are computed
/// from it, so the size variants are enough to resize the whole component and there is nothing else to set.
///
/// The linear indicator has no preferred width worth speaking of, it is meant to be stretched by its
/// parent, and the value returned by the skin is just a sensible fallback.
public class MFXProgressIndicator extends MFXControl implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    public static final double INDETERMINATE = -1.0;

    private final VariantsHandler<MFXProgressIndicator> variantsHandler = new VariantsHandler<>(this);

    private final ReadOnlyBooleanWrapper indeterminate = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.INDETERMINATE.setOn(MFXProgressIndicator.this, get());
        }
    };

    private final DoubleProperty progress = new PropUtils.DoublePropertyBuilder()
        .bean(this)
        .name("progress")
        .mapper(v -> v < 0 ? INDETERMINATE : clamp(v, 0.0, 1.0))
        .onInvalidated(v -> indeterminate.set(v < 0))
        .build();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXProgressIndicator() {
        this(INDETERMINATE);
    }

    public MFXProgressIndicator(double progress) {
        setProgress(progress);
    }

    {
        defaultVariants();
        getAppliedVariants().addListener((MapChangeListener<Class<?>, Variant>) c -> {
            if (c.getKey() != ShapeVariant.class || getSkin() == null) return;
            setSkin(buildSkin());
        });
    }

    //================================================================================
    // Configs
    //================================================================================

    public MFXProgressIndicator setShape(ShapeVariant shape) {
        variantsHandler.setVariant(shape);
        return this;
    }

    public MFXProgressIndicator setSize(SizeVariant size) {
        variantsHandler.setVariant(size);
        return this;
    }

    public MFXProgressIndicator setWave(WaveVariant wave) {
        variantsHandler.setVariant(wave);
        return this;
    }

    public MFXProgressIndicator setWavy(boolean wavy) {
        return setWave(wavy ? WaveVariant.WAVY : WaveVariant.FLAT);
    }

    /// Applies the default variants to the indicator:
    /// - [ShapeVariant#LINEAR]
    /// - [SizeVariant#S]
    /// - [WaveVariant#FLAT]
    @Override
    public MFXProgressIndicator defaultVariants() {
        variantsHandler.setVariants(ShapeVariant.LINEAR, SizeVariant.S, WaveVariant.FLAT);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXBehavior<? extends Node>> defaultBehaviorFactory() {
        return () -> new MFXBehavior<Node>(this) {};
    }

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> getAppliedVariant(ShapeVariant.class) == ShapeVariant.CIRCULAR ?
            new MFXCircularProgressIndicatorSkin(this) :
            new MFXLinearProgressIndicatorSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-progress-indicator");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
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

    private final StyleableBooleanProperty showStopIndicator = new StyleableBooleanProperty(
        StyleableProperties.SHOW_STOP_INDICATOR,
        this,
        "showStopIndicator",
        true
    );

    public boolean isAnimated() {
        return animated.get();
    }

    /// Specifies whether progress changes are eased into rather than applied at once, and whether the
    /// waveform, if any, is allowed to travel along the track.
    ///
    /// The indeterminate animation is not affected: it is the only thing an indeterminate indicator shows.
    ///
    /// Only one side should be animating the progress. While this is `true` the skin owns the motion, so
    /// [#progressProperty()] is meant to be set, or bound to something that is set, and never itself driven by
    /// an animation: the skin restarts its own easing on every change, which cannot keep up with a target that
    /// moves every frame and swings the other way whenever that target reverses. Set this to `false` to own the
    /// motion instead, and the values will be drawn exactly as they come.
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public boolean isShowStopIndicator() {
        return showStopIndicator.get();
    }

    /// Specifies whether the linear indicator shows the little dot sitting at the end of the track.
    ///
    /// It exists to make the track's extent perceivable when the track itself has a poor contrast against
    /// its background, so it should be turned off only when that is not a concern. Ignored by the circular
    /// indicator, which has no such element.
    public StyleableBooleanProperty showStopIndicatorProperty() {
        return showStopIndicator;
    }

    public void setShowStopIndicator(boolean showStopIndicator) {
        this.showStopIndicator.set(showStopIndicator);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXProgressIndicator> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXProgressIndicator, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MFXProgressIndicator::animatedProperty,
                true
            );

        private static final CssMetaData<MFXProgressIndicator, Boolean> SHOW_STOP_INDICATOR =
            FACTORY.createBooleanCssMetaData(
                "-mfx-show-stop-indicator",
                MFXProgressIndicator::showStopIndicatorProperty,
                true
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                ANIMATED, SHOW_STOP_INDICATOR
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public double getProgress() {
        return progress.get();
    }

    /// Specifies the amount of work done, in the `[0.0, 1.0]` range. Values outside it are clamped, except
    /// for negative ones which all mean [#INDETERMINATE].
    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public boolean isIndeterminate() {
        return indeterminate.get();
    }

    /// Specifies whether the indicator is currently showing an unspecified wait, which is the case for any
    /// negative [#progressProperty()]. Kept in sync with the `:indeterminate` pseudo class.
    public ReadOnlyBooleanProperty indeterminateProperty() {
        return indeterminate.getReadOnlyProperty();
    }
}
