/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.effects.ripple;

import io.github.palexdev.materialfx.effects.ripple.base.IRippleGenerator;
import io.github.palexdev.materialfx.skins.MFXToggleButtonSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.function.Function;

/**
 * Simple bean to wrap the coordinates of generated ripples.
 * <p>
 * This is used by the ripple generator's position function as the return type,
 * {@link IRippleGenerator#setRipplePositionFunction(Function)}.
 * <p>
 * Note that both the positions are JavaFX properties, this allows to change the ripple position during
 * its animation, an example can be seen in the {@link MFXToggleButtonSkin}
 * <p></p>
 * In {@link MFXCircleRippleGenerator} the ripple center properties are already bound to these values.
 */
public class RipplePosition {
    private final DoubleProperty xPosition = new SimpleDoubleProperty(0);
    private final DoubleProperty yPosition = new SimpleDoubleProperty(0);

    public RipplePosition() {
    }

    public RipplePosition(double xPosition, double yPosition) {
        setXPosition(xPosition);
        setYPosition(yPosition);
    }

    public double getXPosition() {
        return xPosition.get();
    }

    public DoubleProperty xPositionProperty() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition.set(xPosition);
    }

    public double getYPosition() {
        return yPosition.get();
    }

    public DoubleProperty yPositionProperty() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition.set(yPosition);
    }
}
