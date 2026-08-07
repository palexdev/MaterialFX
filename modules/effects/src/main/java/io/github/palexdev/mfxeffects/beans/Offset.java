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

package io.github.palexdev.mfxeffects.beans;

import io.github.palexdev.mfxeffects.beans.base.OffsetBase;

public class Offset extends OffsetBase {
    public static final Offset ZERO = new Offset(0, 0);
    public static final Offset INFINITE = new Offset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    public Offset(double dx, double dy) {
        super(dx, dy);
    }

    public static Offset fromDirection(double direction) {
        return fromDirection(direction, 1.0);
    }

    public static Offset fromDirection(double direction, double distance) {
        return new Offset(distance * Math.cos(direction), distance * Math.sin(direction));
    }

    public static Offset lerp(Offset a, Offset b, double t) {
        if (b == null) {
            if (a == null) {
                return null;
            } else {
                return a.mul(1.0 - t);
            }
        } else {
            if (a == null) {
                return b.mul(t);
            } else {
                return new Offset(lerpDouble(a.dx, b.dx, t), lerpDouble(a.dy, b.dy, t));
            }
        }
    }

    private static double lerpDouble(double a, double b, double t) {
        return a * (1.0 - t) + b * t;
    }

    public double getDistance() {
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getDistanceSquared() {
        return dx * dx + dy * dy;
    }

    public double getDirection() {
        return Math.atan2(dy, dx);
    }

    public Offset scale(double scaleX, double scaleY) {
        return new Offset(dx * scaleX, dy * scaleY);
    }

    public Offset translate(double translateX, double translateY) {
        return new Offset(dx + translateX, dy + translateY);
    }

    public Offset inverse() {
        return new Offset(-dx, -dy);
    }

    public Offset minus(Offset other) {
        return new Offset(dx - other.dx, dy - other.dy);
    }

    public Offset plus(Offset other) {
        return new Offset(dx + other.dx, dy + other.dy);
    }

    public Offset mul(double operand) {
        return new Offset(dx * operand, dy * operand);
    }

    public Offset div(double operand) {
        return new Offset(dx / operand, dy / operand);
    }

    public Offset divTruncate(double operand) {
        return new Offset((int) dx / operand, (int) dy / operand);
    }

    public Offset mod(double operand) {
        return new Offset(dx % operand, dy % operand);
    }
}
