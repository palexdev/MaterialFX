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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.controls.MFXProgressIndicator;
import io.github.palexdev.mfxcomponents.skins.base.MFXProgressIndicatorSkin;
import io.github.palexdev.mfxeffects.animations.base.Curve;
import io.github.palexdev.mfxeffects.animations.motion.Motion;
import javafx.scene.layout.Region;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Duration;

import static io.github.palexdev.mfxcore.observables.When.observe;
import static java.lang.Math.clamp;
import static java.lang.Math.min;

public class MFXLinearProgressIndicatorSkin extends MFXProgressIndicatorSkin {
    //================================================================================
    // Properties
    //================================================================================
    protected static double MIN_WIDTH = 48.0;
    protected static double PREF_WIDTH = 240.0;
    protected static double AMPLITUDE = 3.0;
    protected static double WAVELENGTH = 40.0;
    protected static double WAVELENGTH_INDETERMINATE = 20.0;
    protected static double STOP_INDICATOR_SIZE = 4.0;
    protected static double STOP_INDICATOR_MAX_SPACE = 6.0;

    protected static Duration CYCLE_DURATION = Duration.millis(1750);
    protected static Curve CYCLE_CURVE = Motion.EASE_IN_OUT_SINE;
    protected static double FIRST_HEAD_DELAY = 0.0;
    protected static double FIRST_HEAD_DURATION = 1000.0;
    protected static double FIRST_TAIL_DELAY = 250.0;
    protected static double FIRST_TAIL_DURATION = 1000.0;
    protected static double SECOND_HEAD_DELAY = 650.0;
    protected static double SECOND_HEAD_DURATION = 850.0;
    protected static double SECOND_TAIL_DELAY = 900.0;
    protected static double SECOND_TAIL_DURATION = 850.0;

    private final Region stopIndicator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLinearProgressIndicatorSkin(MFXProgressIndicator control) {
        super(control);

        stopIndicator = new Region();
        stopIndicator.setManaged(false);
        stopIndicator.getStyleClass().setAll("stop-indicator");
        getChildren().add(stopIndicator);

        initialize();
        listeners(observe(control::requestLayout, control.showStopIndicatorProperty()));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXProgressIndicator control = getSkinnable();
        trackElements.clear();
        indicatorElements.clear();

        double inset = Math.max(capExtension(track), capExtension(indicator));
        double start = x + inset;
        double length = w - inset * 2;
        double centerY = snapPositionY(y + h / 2.0);

        stopIndicator.setVisible(!control.isIndeterminate() && control.isShowStopIndicator());
        layoutStopIndicator(x, w, centerY);
        if (length <= 0) {
            applyElements();
            return;
        }

        double gap = gap() / length;
        double amplitude = waveAmplitude();
        double wavelength = control.isIndeterminate() ? WAVELENGTH_INDETERMINATE : WAVELENGTH;
        double half = length / (Math.max(1, (int) (length / wavelength)) * 2.0);
        double shift = phase.get() * half * 2.0;

        Span trackSpan = (from, to) -> {
            double x0 = start + clamp(from, 0.0, 1.0) * length;
            double x1 = start + clamp(to, 0.0, 1.0) * length;
            if (x1 - x0 <= 0) return;
            trackElements.add(new MoveTo(x0, centerY));
            trackElements.add(new LineTo(x1, centerY));
        };
        Span indicatorSpan = (from, to) -> {
            double f0 = clamp(from, 0.0, 1.0);
            double f1 = clamp(to, 0.0, 1.0);
            if (f1 - f0 <= 0) return;
            if (amplitude <= 0) {
                indicatorElements.add(new MoveTo(start + f0 * length, centerY));
                indicatorElements.add(new LineTo(start + f1 * length, centerY));
                return;
            }
            appendWave(
                indicatorElements,
                f0 * length - shift, f1 * length - shift, half,
                amplitude, -amplitude,
                (distance, across, point, tangent) -> {
                    point[0] = start + shift + distance;
                    point[1] = centerY + across;
                    tangent[0] = 1.0;
                    tangent[1] = 0.0;
                }
            );
        };

        if (control.isIndeterminate()) {
            double time = cycle.get() * CYCLE_DURATION.toMillis();
            double firstHead = ease(time, FIRST_HEAD_DELAY, FIRST_HEAD_DURATION);
            double firstTail = ease(time, FIRST_TAIL_DELAY, FIRST_TAIL_DURATION);
            double secondHead = ease(time, SECOND_HEAD_DELAY, SECOND_HEAD_DURATION);
            double secondTail = ease(time, SECOND_TAIL_DELAY, SECOND_TAIL_DURATION);

            if (firstHead < 1.0 - gap) trackSpan.add(firstHead > 0 ? firstHead + gap : 0.0, 1.0);
            indicatorSpan.add(firstTail, firstHead);
            if (firstTail > gap) trackSpan.add(
                secondHead > 0 ? secondHead + gap : 0.0,
                firstTail < 1.0 ? firstTail - gap : 1.0
            );
            indicatorSpan.add(secondTail, secondHead);
            if (secondTail > gap) trackSpan.add(0.0, secondTail < 1.0 ? secondTail - gap : 1.0);
        } else {
            double progress = clamp(visualProgress.get(), 0.0, 1.0);
            double trackStart = progress + min(progress, gap);
            if (trackStart <= 1.0) trackSpan.add(trackStart, 1.0);
            indicatorSpan.add(0.0, progress);
        }
        applyElements();
    }

    @Override
    protected Duration cycleDuration() {
        return CYCLE_DURATION;
    }

    @Override
    protected double baseAmplitude() {
        return AMPLITUDE;
    }

    @Override
    public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + MIN_WIDTH + rightInset;
    }

    @Override
    public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + PREF_WIDTH + rightInset;
    }

    @Override
    public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + thickness() + (isWavy() ? AMPLITUDE * 2 : 0.0) + bottomInset;
    }

    @Override
    public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    //================================================================================
    // Methods
    //================================================================================

    protected static double ease(double time, double delay, double duration) {
        return CYCLE_CURVE.curve(clamp((time - delay) / duration, 0.0, 1.0));
    }

    protected void layoutStopIndicator(double x, double w, double centerY) {
        double thickness = track.getStrokeWidth();
        double size = min(STOP_INDICATOR_SIZE, thickness);
        double space = min((thickness - size) / 2.0, STOP_INDICATOR_MAX_SPACE);
        stopIndicator.resizeRelocate(
            snapPositionX(x + w - size - space),
            snapPositionY(centerY - size / 2.0),
            snapSizeX(size),
            snapSizeY(size)
        );
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    @FunctionalInterface
    private interface Span {
        void add(double from, double to);
    }
}
