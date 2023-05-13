/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxeffects.beans.properties;

import io.github.palexdev.mfxeffects.beans.Position;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.Optional;

/**
 * Simple extension of {@link ReadOnlyObjectWrapper} for {@link Position} objects.
 */
public class PositionProperty extends ReadOnlyObjectWrapper<Position> {

    //================================================================================
    // Constructors
    //================================================================================
    public PositionProperty() {
    }

    public PositionProperty(Position initialValue) {
        super(initialValue);
    }

    public PositionProperty(Object bean, String name) {
        super(bean, name);
    }

    public PositionProperty(Object bean, String name, Position initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Convenience method to create a new {@link Position} object with the given parameters and set it
     * as the new value of this property.
     */
    public void setPosition(double x, double y) {
        set(Position.of(x, y));
    }

    /**
     * Convenience method to set only the x of the current {@link Position} of this property.
     * Note that if the value is null a new {@link Position} object is created with Y = 0.0.
     * Also, if the value was not null, {@link #invalidated()} and {@link #fireValueChangedEvent()} are invoked programmatically
     * only if the x was not the same as the given one, this is needed as the object will remain the same.
     */
    public void setX(double x) {
        Optional.ofNullable(get())
                .ifPresentOrElse(
                        p -> {
                            boolean changed = p.getX() != x;
                            p.setX(x);
                            if (changed) {
                                invalidated();
                                fireValueChangedEvent();
                            }
                        },
                        () -> setPosition(x, 0)
                );
    }

    /**
     * Convenience method to set only the y of the current {@link Position} of this property.
     * Note that if the value is null a new {@link Position} object is created with  X = 0.0.
     * Also, if the value was not null, {@link #invalidated()} and {@link #fireValueChangedEvent()} are invoked programmatically
     * only if the y was not the same as the given one, this is needed as the object will remain the same.
     */
    public void setY(double y) {
        Optional.ofNullable(get())
                .ifPresentOrElse(
                        p -> {
                            boolean changed = p.getY() != y;
                            p.setY(y);
                            if (changed) {
                                invalidated();
                                fireValueChangedEvent();
                            }
                        },
                        () -> setPosition(0, y)
                );
    }

    /**
     * Null-safe alternative to {@code get().getX()}, if the value is null returns 0.0.
     */
    public double getX() {
        return Optional.ofNullable(get())
                .map(Position::getX)
                .orElse(0.0);
    }

    /**
     * Null-safe alternative to {@code get().getX()}, if the value is null returns the given value.
     */
    public double getX(double or) {
        return Optional.ofNullable(get())
                .map(Position::getX)
                .orElse(or);
    }

    /**
     * Null-safe alternative to {@code get().getY()}, if the value is null returns 0.0.
     */
    public double getY() {
        return Optional.ofNullable(get())
                .map(Position::getY)
                .orElse(0.0);
    }

    /**
     * Null-safe alternative to {@code get().getY()}, if the value is null returns the given value.
     */
    public double getY(double or) {
        return Optional.ofNullable(get())
                .map(Position::getY)
                .orElse(or);
    }
}
