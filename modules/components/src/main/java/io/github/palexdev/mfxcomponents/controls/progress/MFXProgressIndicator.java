/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls.progress;

import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.skins.MFXCircularProgressIndicatorSkin;
import io.github.palexdev.mfxcomponents.skins.MFXLinearProgressIndicatorSkin;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Control;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom implementation of a progress indicator according to the Material Design 3 guidelines. Extends {@link MFXControl}
 * and has two skins:
 * <p> - {@link MFXLinearProgressIndicatorSkin} is to show the progress as a bar
 * <p> - {@link MFXCircularProgressIndicatorSkin} is to show progress as a circle/arc
 * <p>
 * You can switch skins either by calling {@link #changeSkin(SkinBase)} or preferably by setting the {@link #displayModeProperty()}.
 * <p>
 * The default style class is: '.mfx-progress-indicator'.
 * <p></p>
 * There are only two properties, one is the {@link #progressProperty()} which expresses progress in the range
 * {@code [0.0, 1.0]}; the other is {@link #indeterminateProperty()} and it's read-only. The latter will be active/true
 * when the progress is {@code -1.0}. Also note that any progress value lesser than 0 will set the {@link #progressProperty()}
 * automatically to {@code -1.0}.
 */
public class MFXProgressIndicator extends MFXControl<BehaviorBase<MFXProgressIndicator>> {
    //================================================================================
    // Properties
    //================================================================================
    private final DoubleProperty progress = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) {
            if (newValue < 0) {
                super.set(-1.0);
                return;
            }
            super.set(NumberUtils.clamp(
                newValue, 0.0, 1.0
            ));
        }

        @Override
        protected void invalidated() {
            setIndeterminate(get() < 0);
        }
    };
    private final ReadOnlyBooleanWrapper indeterminate = new ReadOnlyBooleanWrapper(false);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXProgressIndicator() {
        this(-1.0);
    }

    public MFXProgressIndicator(double progress) {
        setProgress(progress);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setDefaultBehaviorProvider();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXLinearProgressIndicatorSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-progress-indicator");
    }

    @Override
    public Supplier<BehaviorBase<MFXProgressIndicator>> defaultBehaviorProvider() {
        return () -> new BehaviorBase<>(this) {};
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<ProgressDisplayMode> displayMode = new StyleableObjectProperty<>(
        StyleableProperties.DISPLAY_MODE,
        this,
        "displayMode",
        ProgressDisplayMode.LINEAR
    ) {
        @Override
        protected void invalidated() {
            ProgressDisplayMode mode = get();
            if (mode == null) return;
            changeSkin(mode == ProgressDisplayMode.LINEAR ?
                new MFXLinearProgressIndicatorSkin(MFXProgressIndicator.this) :
                new MFXCircularProgressIndicatorSkin(MFXProgressIndicator.this)
            );
        }
    };

    private final StyleableBooleanProperty showStopPoint = new StyleableBooleanProperty(
        StyleableProperties.SHOW_STOP_POINT,
        this,
        "showStopPoint",
        true
    );

    private final StyleableDoubleProperty clipRadius = new StyleableDoubleProperty(
        StyleableProperties.CLIP_RADIUS,
        this,
        "clipRadius",
        6.0
    );

    public ProgressDisplayMode getDisplayMode() {
        return displayMode.get();
    }

    /**
     * Specifies how to display progress, currently, and by the Material Design 3 specs, there are only two ways:
     * with a bar or a circle/arc.
     * <p>
     * This property will automatically change the component's skin according to the specified mode to
     * {@link MFXLinearProgressIndicatorSkin} or {@link MFXCircularProgressIndicatorSkin}. If you set it to {@code null},
     * nothing will happen.
     * <p></p>
     * Can be set in CSS via the property: '-mfx-display-mode'.
     */
    public StyleableObjectProperty<ProgressDisplayMode> displayModeProperty() {
        return displayMode;
    }

    public void setDisplayMode(ProgressDisplayMode displayMode) {
        this.displayMode.set(displayMode);
    }

    public boolean isShowStopPoint() {
        return showStopPoint.get();
    }

    /**
     * According to Material Design 3 specs, linear progress indicators can show a little dot at the end of the bar for
     * better accessibility. It's suggested to turn it on if the contrast between the track and the node containing the
     * indicator is below 3:1.
     * <p></p>
     * Can be set in CSS via the property: '-mfx-show-stop-point'.
     */
    public StyleableBooleanProperty showStopPointProperty() {
        return showStopPoint;
    }

    public void setShowStopPoint(boolean showStopPoint) {
        this.showStopPoint.set(showStopPoint);
    }

    public double getClipRadius() {
        return clipRadius.get();
    }

    /**
     * The {@link MFXLinearProgressIndicatorSkin} applies a clip to the component to avoid bars from overflowing when
     * animated. Since by design the bars are rounded, the clip must be rounded too. This property specifies the clip's radius.
     * <p></p>
     * Can be set in CSS via the property: '-mfx-clip-radius'.
     */
    public StyleableDoubleProperty clipRadiusProperty() {
        return clipRadius;
    }

    public void setClipRadius(double clipRadius) {
        this.clipRadius.set(clipRadius);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXProgressIndicator> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXProgressIndicator, ProgressDisplayMode> DISPLAY_MODE =
            FACTORY.createEnumCssMetaData(
                ProgressDisplayMode.class,
                "-mfx-display-mode",
                MFXProgressIndicator::displayModeProperty,
                ProgressDisplayMode.LINEAR
            );

        private static final CssMetaData<MFXProgressIndicator, Boolean> SHOW_STOP_POINT =
            FACTORY.createBooleanCssMetaData(
                "-mfx-show-stop-point",
                MFXProgressIndicator::showStopPointProperty,
                true
            );

        private static final CssMetaData<MFXProgressIndicator, Number> CLIP_RADIUS =
            FACTORY.createSizeCssMetaData(
                "-mfx-clip-radius",
                MFXProgressIndicator::clipRadiusProperty,
                6.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Control.getClassCssMetaData(),
                DISPLAY_MODE, SHOW_STOP_POINT, CLIP_RADIUS
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

    /**
     * Specifies the progress in the range {@code [0.0, 1.0]}. Any negative number will automatically set the property
     * to {@code -1.0}, causing the {@link #indeterminateProperty()} to become {@code true}.
     */
    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public boolean isIndeterminate() {
        return indeterminate.get();
    }

    /**
     * Specifies whether the {@link #progressProperty()} is negative.
     */
    public ReadOnlyBooleanProperty indeterminateProperty() {
        return indeterminate.getReadOnlyProperty();
    }

    protected void setIndeterminate(boolean indeterminate) {
        this.indeterminate.set(indeterminate);
    }
}
