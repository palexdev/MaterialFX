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

package io.github.palexdev.mfxcore.base.beans;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Objects;

/**
 * Simple bean that keeps track of two coordinates, x and y.
 * <p>
 * Both are JavaFX properties to allow dynamic uses.
 */
public class Position {
	//================================================================================
	// Properties
	//================================================================================
	private final DoubleProperty x = new SimpleDoubleProperty(0);
	private final DoubleProperty y = new SimpleDoubleProperty(0);

	//================================================================================
	// Constructors
	//================================================================================
	public Position() {
	}

	public Position(double x, double y) {
		setX(x);
		setY(y);
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static Position of(double x, double y) {
		return new Position(x, y);
	}

	/**
	 * @return a new {@code Position} object with both x and y set to 0
	 */
	public static Position origin() {
		return of(0, 0);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position that = (Position) o;
		return getX() == (that.getX()) && getY() == (that.getY());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getX(), getY());
	}

	@Override
	public String toString() {
		return "X|Y (" + getX() + "; " + getY() + ")";
	}

	//================================================================================
	// Methods
	//================================================================================
	public double getX() {
		return x.get();
	}

	/**
	 * The x coordinate property.
	 */
	public DoubleProperty xProperty() {
		return x;
	}

	public void setX(double xPosition) {
		this.x.set(xPosition);
	}

	public double getY() {
		return y.get();
	}

	/**
	 * The y coordinate property
	 */
	public DoubleProperty yProperty() {
		return y;
	}

	public void setY(double yPosition) {
		this.y.set(yPosition);
	}
}

