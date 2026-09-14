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

package io.github.palexdev.mfxcomponents.skins.base;

import java.util.ArrayList;
import java.util.List;

import io.github.palexdev.mfxcomponents.controls.MFXProgressIndicator;
import io.github.palexdev.mfxcomponents.variants.ProgressIndicatorVariants.WaveVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.base.Curve;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.scene.shape.*;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.observables.OnInvalidated.withListener;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static io.github.palexdev.mfxeffects.animations.Animations.isPlaying;
import static java.lang.Math.clamp;

/// Common base for the two progress indicator skins. Both of them draw the same two children, a `.track`
/// and an `.indicator` [Path], the difference being only the shape their geometry describes.
///
/// #### Why paths
///
/// Regions and [javafx.scene.shape.Arc]s would be enough for a straight bar or a plain arc, but not for the
/// waveform, and the two must be the same code path, otherwise the wave becomes a rewrite rather than a
/// switch. Everything the skins draw is therefore a list of path elements rebuilt from scratch, which also
/// removes the need for clips and scale transforms.
///
/// #### When the geometry is computed
///
/// Never inside a listener. The animations here drive plain properties ([#visualProgress], [#cycle],
/// [#phase], [#amplitude]) and every listener does nothing but ask for a new layout pass, so a pulse in
/// which several of them change still rebuilds the paths only once, in `layoutChildren(...)`, after CSS has
/// been applied and the component's size is known.
///
/// #### Sizes
///
/// The thickness of both children is read back from CSS (`-fx-stroke-width`), as is the shape of their ends
/// (`-fx-stroke-line-cap`), because that is what the size variants drive. Every other measure descends from
/// those two, including the gap between the track and the active indicator, which grows by the caps' overhang
/// so that the visible space stays the one that was asked for.
public abstract class MFXProgressIndicatorSkin extends MFXSkinBase<MFXProgressIndicator> {
    //================================================================================
    // Properties
    //================================================================================
    protected static double GAP = 4.0;

    protected static Duration PROGRESS_DURATION = M3Motion.LONG4;
    protected static Curve PROGRESS_CURVE = M3Motion.STANDARD_DECELERATE;

    protected static Duration AMPLITUDE_DURATION = M3Motion.LONG2;
    protected static Curve AMPLITUDE_IN_CURVE = M3Motion.STANDARD;
    protected static Curve AMPLITUDE_OUT_CURVE = M3Motion.EMPHASIZED_ACCELERATE;
    protected static double AMPLITUDE_MIN_PROGRESS = 0.1;
    protected static double AMPLITUDE_MAX_PROGRESS = 0.95;

    protected static Duration WAVE_CYCLE_DURATION = Duration.seconds(1.0);
    protected static double WAVE_SMOOTHNESS = 0.48;

    protected final Path track;
    protected final Path indicator;
    protected final List<PathElement> trackElements = new ArrayList<>();
    protected final List<PathElement> indicatorElements = new ArrayList<>();

    // The progress actually drawn, which trails the indicator's progress while it is being eased into.
    protected final DoubleProperty visualProgress = new SimpleDoubleProperty(0.0);

    // The position within the indeterminate animation, in the `[0.0, 1.0]` range. Advances linearly, the
    // easing being applied by the skins to whatever they derive from it.
    protected final DoubleProperty cycle = new SimpleDoubleProperty(0.0);

    // How far the waveform has traveled along the track, in cycles.
    protected final DoubleProperty phase = new SimpleDoubleProperty(0.0);

    // How much of the nominal amplitude the waveform currently has. Ramped down towards the two ends of a
    // determinate run, where a wave would have no room to be seen.
    protected final DoubleProperty amplitude = new SimpleDoubleProperty(0.0);

    private Animation progressAnim;
    private Animation indAnim;
    private Animation waveAnim;
    private Animation amplitudeAnim;
    private double amplitudeTarget = 0.0;

    private MapChangeListener<Class<?>, Variant> vListener = c -> onVariantChanged(c.getKey());

    //================================================================================
    // Constructors
    //================================================================================
    protected MFXProgressIndicatorSkin(MFXProgressIndicator control) {
        super(control);
        track = createPath("track");
        indicator = createPath("indicator");
        getChildren().setAll(track, indicator);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Registers every listener the skin needs. Subclasses are expected to call this at the very end of their
    /// constructor, as it immediately reacts to the current progress.
    protected void initialize() {
        MFXProgressIndicator indicator = getSkinnable();
        InvalidationListener redraw = o -> {
            // The value must be read here, or the property stays invalid and never notifies again
            ((ObservableValue<?>) o).getValue();
            indicator.requestLayout();
        };
        listen(
            onInvalidated(indicator.progressProperty())
                .then(_ -> onProgressChanged())
                .executeNow(),
            onInvalidated(indicator.animatedProperty())
                .then(_ -> {
                    onProgressChanged();
                    playWave();
                }),
            withListener(visualProgress, redraw),
            withListener(cycle, redraw),
            withListener(phase, redraw),
            withListener(amplitude, redraw),
            withListener(track.strokeWidthProperty(), redraw),
            withListener(track.strokeLineCapProperty(), redraw),
            withListener(this.indicator.strokeWidthProperty(), redraw),
            withListener(this.indicator.strokeLineCapProperty(), redraw)
        );
        indicator.getAppliedVariants().addListener(vListener);
        playWave();
    }

    protected void onProgressChanged() {
        MFXProgressIndicator control = getSkinnable();
        if (control.isIndeterminate()) {
            if (isPlaying(progressAnim)) progressAnim.stop();
            if (isPlaying(amplitudeAnim)) amplitudeAnim.stop();
            amplitudeTarget = 1.0;
            amplitude.set(1.0);
            playIndeterminate();
            return;
        }
        if (isPlaying(indAnim)) indAnim.stop();
        cycle.set(0.0);
        double progress = control.getProgress();
        animateProgress(progress);
        animateAmplitude(rampTarget(progress));
    }

    /// Eases [#visualProgress] towards the given value, or sets it right away if the component is not animated.
    protected void animateProgress(double target) {
        if (isPlaying(progressAnim)) progressAnim.stop();
        if (!getSkinnable().isAnimated()) {
            visualProgress.set(target);
            return;
        }
        progressAnim = TimelineBuilder.build()
            .add(KeyFrames.of(PROGRESS_DURATION, visualProgress, target, PROGRESS_CURVE))
            .getAnimation();
        progressAnim.play();
    }

    /// Eases [#amplitude] towards the given value. Growing and shrinking do not use the same curve: a wave
    /// that is about to disappear accelerates out of the way, while one that is appearing settles in.
    protected void animateAmplitude(double target) {
        if (amplitudeTarget == target) return;
        if (isPlaying(amplitudeAnim)) amplitudeAnim.stop();
        Curve curve = target > amplitude.get() ? AMPLITUDE_IN_CURVE : AMPLITUDE_OUT_CURVE;
        amplitudeTarget = target;
        if (!isWavy() || !getSkinnable().isAnimated()) {
            amplitude.set(target);
            return;
        }
        amplitudeAnim = TimelineBuilder.build()
            .add(KeyFrames.of(AMPLITUDE_DURATION, amplitude, target, curve))
            .getAnimation();
        amplitudeAnim.play();
    }

    protected void playIndeterminate() {
        if (isPlaying(indAnim)) return;
        indAnim = TimelineBuilder.build()
            .add(KeyFrames.of(Duration.ZERO, cycle, 0.0))
            .add(KeyFrames.of(cycleDuration(), cycle, 1.0, M3Motion.LINEAR))
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();
        indAnim.play();
    }

    protected void playWave() {
        if (isPlaying(waveAnim)) waveAnim.stop();
        phase.set(0.0);
        if (!isWavy() || !getSkinnable().isAnimated()) return;
        waveAnim = TimelineBuilder.build()
            .add(KeyFrames.of(Duration.ZERO, phase, 0.0))
            .add(KeyFrames.of(WAVE_CYCLE_DURATION, phase, 1.0, M3Motion.LINEAR))
            .setCycleCount(Animation.INDEFINITE)
            .getAnimation();
        waveAnim.play();
    }

    protected void onVariantChanged(Class<?> variant) {
        MFXProgressIndicator control = getSkinnable();
        if (variant == WaveVariant.class) {
            if (isPlaying(amplitudeAnim)) amplitudeAnim.stop();
            amplitudeTarget = control.isIndeterminate() ? 1.0 : rampTarget(control.getProgress());
            amplitude.set(amplitudeTarget);
            playWave();
        }
        control.requestLayout();
    }

    /// @return the amplitude the waveform should have at the given progress. A wave needs some track to be
    /// recognizable as one, so it is not drawn while the indicator is about to start or about to complete.
    protected double rampTarget(double progress) {
        return (progress <= AMPLITUDE_MIN_PROGRESS || progress >= AMPLITUDE_MAX_PROGRESS) ? 0.0 : 1.0;
    }

    protected Path createPath(String... classes) {
        Path path = new Path();
        path.setManaged(false);
        path.setFill(null);
        path.getStyleClass().setAll(classes);
        return path;
    }

    /// Hands the elements built during the layout pass over to the two paths, in one go each so that a rebuild
    /// costs a single change event.
    protected void applyElements() {
        track.getElements().setAll(trackElements);
        indicator.getElements().setAll(indicatorElements);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void dispose() {
        progressAnim = null;
        indAnim = null;
        waveAnim = null;
        amplitudeAnim = null;
        getSkinnable().getAppliedVariants().removeListener(vListener);
        vListener = null;
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================
    protected boolean isWavy() {
        return getSkinnable().getAppliedVariant(WaveVariant.class) == WaveVariant.WAVY;
    }

    /// @return the amplitude to draw with right now, which is the nominal one scaled by the ramp
    protected double waveAmplitude() {
        return isWavy() ? baseAmplitude() * amplitude.get() : 0.0;
    }

    /// @return how far beyond its end a path's cap sticks out
    protected double capExtension(Path path) {
        return path.getStrokeLineCap() == StrokeLineCap.BUTT ? 0.0 : path.getStrokeWidth() / 2.0;
    }

    protected boolean hasCaps() {
        return indicator.getStrokeLineCap() != StrokeLineCap.BUTT;
    }

    /// @return the space to leave between the track and the active indicator, widened by the two caps facing
    /// each other across it so that what is actually seen is [#GAP]
    protected double gap() {
        double caps = hasCaps() ? (track.getStrokeWidth() + indicator.getStrokeWidth()) / 2.0 : 0.0;
        return GAP + caps;
    }

    protected double thickness() {
        return Math.max(track.getStrokeWidth(), indicator.getStrokeWidth());
    }

    /// @return how long a full turn of the indeterminate animation lasts
    protected abstract Duration cycleDuration();

    /// @return the amplitude a fully grown waveform has
    protected abstract double baseAmplitude();

    //================================================================================
    // Geometry Stuff
    //================================================================================

    /// Describes the curve a waveform is wrapped around, be it a straight line or a circle.
    @FunctionalInterface
    protected interface WaveGeometry {
        /// Computes the point sitting `across` away from the curve, perpendicularly, at the given distance
        /// along it, as well as the curve's unit tangent there. Both are written into the given arrays.
        void at(double distance, double across, double[] point, double[] tangent);
    }

    /// Appends to the given elements the stretch of waveform that spans `[from, to]`, both expressed as
    /// distances along the curve `geometry` describes.
    ///
    /// The waveform is a chain of cubics, one per half cycle, alternating between the two given offsets from
    /// the curve. The chain is anchored at the curve's origin rather than at `from`, so that the wave stays
    /// put while the stretch that is drawn of it grows, shrinks or slides. Whichever half cycles the two ends
    /// fall in the middle of are cut exactly where they should be, see [#subCurve(double\[\], double, double)].
    protected static void appendWave(
        List<PathElement> elements,
        double from, double to, double half,
        double evenAcross, double oddAcross,
        WaveGeometry geometry
    ) {
        double[] start = new double[2], end = new double[2];
        double[] startTangent = new double[2], endTangent = new double[2];
        double[] curve = new double[8];
        double handle = half * WAVE_SMOOTHNESS;

        int first = (int) Math.floor(from / half);
        int last = (int) Math.ceil(to / half);
        if (last <= first) last = first + 1;

        boolean began = false;
        for (int i = first; i < last; i++) {
            double distance = i * half;
            geometry.at(distance, across(i, evenAcross, oddAcross), start, startTangent);
            geometry.at(distance + half, across(i + 1, evenAcross, oddAcross), end, endTangent);
            curve[0] = start[0];
            curve[1] = start[1];
            curve[2] = start[0] + handle * startTangent[0];
            curve[3] = start[1] + handle * startTangent[1];
            curve[4] = end[0] - handle * endTangent[0];
            curve[5] = end[1] - handle * endTangent[1];
            curve[6] = end[0];
            curve[7] = end[1];

            double t0 = (i == first) ? parameterAt((from - distance) / half) : 0.0;
            double t1 = (i == last - 1) ? parameterAt((to - distance) / half) : 1.0;
            if (t1 - t0 <= 1e-6) continue;

            double[] segment = subCurve(curve, t0, t1);
            if (!began) {
                elements.add(new MoveTo(segment[0], segment[1]));
                began = true;
            }
            elements.add(new CubicCurveTo(
                segment[2], segment[3],
                segment[4], segment[5],
                segment[6], segment[7]
            ));
        }
    }

    private static double across(int index, double even, double odd) {
        return (index & 1) == 0 ? even : odd;
    }

    /// A half cycle's handles sit at [#WAVE_SMOOTHNESS] of its span rather than at a third of it, so how far
    /// along the curve a point is and what parameter it has are two different things. This converts the former
    /// into the latter, which is what cutting a cubic short needs.
    protected static double parameterAt(double fraction) {
        double target = clamp(fraction, 0.0, 1.0);
        double lo = 0.0;
        double hi = 1.0;
        for (int i = 0; i < 24; i++) {
            double mid = (lo + hi) / 2.0;
            if (spanAt(mid) < target) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        return (lo + hi) / 2.0;
    }

    private static double spanAt(double t) {
        double mt = 1 - t;
        return 3 * WAVE_SMOOTHNESS * mt * mt * t + 3 * (1 - WAVE_SMOOTHNESS) * mt * t * t + t * t * t;
    }

    /// @return the control points of the stretch of the given cubic that lies between the two parameters,
    /// as a new cubic. Its ends and the slopes it has there are the original's, so the result is not an
    /// approximation, it is the very same curve seen through a smaller window.
    protected static double[] subCurve(double[] curve, double t0, double t1) {
        double scale = (t1 - t0) / 3.0;
        double x0 = valueAt(curve, 0, t0);
        double y0 = valueAt(curve, 1, t0);
        double x1 = valueAt(curve, 0, t1);
        double y1 = valueAt(curve, 1, t1);
        return new double[]{
            x0, y0,
            x0 + scale * slopeAt(curve, 0, t0), y0 + scale * slopeAt(curve, 1, t0),
            x1 - scale * slopeAt(curve, 0, t1), y1 - scale * slopeAt(curve, 1, t1),
            x1, y1
        };
    }

    private static double valueAt(double[] curve, int offset, double t) {
        double mt = 1 - t;
        return mt * mt * mt * curve[offset]
               + 3 * mt * mt * t * curve[offset + 2]
               + 3 * mt * t * t * curve[offset + 4]
               + t * t * t * curve[offset + 6];
    }

    private static double slopeAt(double[] curve, int offset, double t) {
        double mt = 1 - t;
        return 3 * mt * mt * (curve[offset + 2] - curve[offset])
               + 6 * mt * t * (curve[offset + 4] - curve[offset + 2])
               + 3 * t * t * (curve[offset + 6] - curve[offset + 4]);
    }
}
