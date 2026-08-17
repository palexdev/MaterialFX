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

import java.util.List;

import io.github.palexdev.mfxcomponents.controls.MFXProgressIndicator;
import io.github.palexdev.mfxcomponents.skins.base.MFXProgressIndicatorSkin;
import io.github.palexdev.mfxeffects.animations.base.Curve;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.animations.motion.Motion;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.util.Duration;

import static java.lang.Math.clamp;
import static java.lang.Math.min;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MFXCircularProgressIndicatorSkin extends MFXProgressIndicatorSkin {
    //================================================================================
    // Properties
    //================================================================================
    protected static double BASE_RADIUS = 14.0;
    protected static double WAVE_EXTRA_RADIUS = 4.0;
    protected static double AMPLITUDE = 1.6;
    protected static double WAVELENGTH = 15.0;
    protected static double REFERENCE_RADIUS = 22.0;
    protected static int MIN_CYCLES = 3;

    protected static Duration CYCLE_DURATION = Duration.millis(6000);
    protected static double TOTAL_ROTATION = 2160.0;
    protected static double STEP_ROTATION = 360.0;
    protected static int ROTATION_STEPS = 4;
    protected static double STEP_DURATION = 800.0;
    protected static Curve STEP_CURVE = M3Motion.STANDARD;
    protected static double MIN_SWEEP = 0.10;
    protected static double MAX_SWEEP = 0.87;
    protected static int SWEEP_CYCLES = 2;
    protected static double SWEEP_DURATION = 1000.0;
    protected static Curve SWEEP_CURVE = Motion.EASE_IN_OUT_SINE;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCircularProgressIndicatorSkin(MFXProgressIndicator control) {
        super(control);
        initialize();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXProgressIndicator control = getSkinnable();
        trackElements.clear();
        indicatorElements.clear();

        double thickness = thickness();
        double centerX = snapPositionX(x + w / 2.0);
        double centerY = snapPositionY(y + h / 2.0);
        double radius = (min(w, h) - thickness) / 2.0;
        if (radius <= 0) {
            applyElements();
            return;
        }

        double gap = toDegrees(gap() / radius);
        double circumference = 2 * Math.PI * radius;
        int cycles = 2 * Math.max(MIN_CYCLES, (int) (Math.PI * REFERENCE_RADIUS / WAVELENGTH));
        double half = circumference / (cycles * 2.0);
        double shift = phase.get() * half * 2.0;

        double start;
        double sweep;
        if (control.isIndeterminate()) {
            double c = cycle.get();
            double range = (MAX_SWEEP - MIN_SWEEP) * 360.0;
            double expand = ramps(c, 0.0);
            double collapse = ramps(c, CYCLE_DURATION.toMillis() / (SWEEP_CYCLES * 2.0));
            start = rotation(c) + collapse * range;
            sweep = MIN_SWEEP * 360.0 + (expand - collapse) * range;
        } else {
            start = 0.0;
            sweep = clamp(visualProgress.get(), 0.0, 1.0) * 360.0;
        }

        double arc = toRadians(sweep) * radius;
        double amplitude = waveAmplitude() * (radius / REFERENCE_RADIUS);
        double space = min(sweep, gap);
        addArc(trackElements, centerX, centerY, radius, start + sweep + space, start + 360.0 - space);
        if (sweep > 0) {
            if (amplitude <= 0) {
                addArc(indicatorElements, centerX, centerY, radius, start, start + sweep);
            } else {
                double origin = toRadians(start) + shift / radius;
                appendWave(
                    indicatorElements,
                    -shift, -shift + arc, half,
                    0.0, -amplitude * 2,
                    (distance, across, point, tangent) -> {
                        double angle = origin + distance / radius;
                        point[0] = centerX + (radius + across) * Math.sin(angle);
                        point[1] = centerY - (radius + across) * Math.cos(angle);
                        tangent[0] = Math.cos(angle);
                        tangent[1] = Math.sin(angle);
                    }
                );
            }
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
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double thickness = thickness();
        double radius = BASE_RADIUS + thickness + (isWavy() ? WAVE_EXTRA_RADIUS : 0.0);
        return leftInset + radius * 2 + thickness + rightInset;
    }

    @Override
    public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        double thickness = thickness();
        double radius = BASE_RADIUS + thickness + (isWavy() ? WAVE_EXTRA_RADIUS : 0.0);
        return topInset + radius * 2 + thickness + bottomInset;
    }

    @Override
    public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    //================================================================================
    // Methods
    //================================================================================

    protected static double rotation(double cycle) {
        double time = cycle * CYCLE_DURATION.toMillis();
        double step = CYCLE_DURATION.toMillis() / ROTATION_STEPS;
        int index = min((int) (time / step), ROTATION_STEPS - 1);
        double progress = STEP_CURVE.curve(clamp((time - index * step) / STEP_DURATION, 0.0, 1.0));
        double linear = TOTAL_ROTATION - STEP_ROTATION - SWEEP_CYCLES * (MAX_SWEEP - MIN_SWEEP) * 360.0;
        return linear * cycle + (index + progress) * (STEP_ROTATION / ROTATION_STEPS);
    }

    protected static double ramps(double cycle, double delay) {
        double time = cycle * CYCLE_DURATION.toMillis();
        double period = CYCLE_DURATION.toMillis() / SWEEP_CYCLES;
        double total = 0.0;
        for (int i = 0; i < SWEEP_CYCLES; i++) {
            total += SWEEP_CURVE.curve(clamp((time - delay - i * period) / SWEEP_DURATION, 0.0, 1.0));
        }
        return total;
    }

    protected void addArc(List<PathElement> elements, double centerX, double centerY, double radius, double from, double to) {
        double sweep = min(to - from, 359.99);
        if (sweep <= 0) return;
        int segments = (int) Math.ceil(sweep / 90.0);
        double step = sweep / segments;
        elements.add(new MoveTo(pointX(centerX, radius, from), pointY(centerY, radius, from)));
        for (int i = 1; i <= segments; i++) {
            double angle = from + step * i;
            elements.add(new ArcTo(
                radius, radius, 0,
                pointX(centerX, radius, angle), pointY(centerY, radius, angle),
                false, true
            ));
        }
    }

    protected static double pointX(double centerX, double radius, double angle) {
        return centerX + radius * Math.sin(toRadians(angle));
    }

    protected static double pointY(double centerY, double radius, double angle) {
        return centerY - radius * Math.cos(toRadians(angle));
    }
}
