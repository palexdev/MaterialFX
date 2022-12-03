/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MFXCore (https://github.com/palexdev/MFXCore).
 *
 * MFXCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MFXCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MFXCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.base.beans;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Objects;

/**
 * This bean contains two {@link DoubleProperty} to keep track/specify the sizes of something
 * in terms of width and height.
 */
public class Size {
	//================================================================================
	// Properties
	//================================================================================
	private final DoubleProperty width = new SimpleDoubleProperty(0.0);
	private final DoubleProperty height = new SimpleDoubleProperty(0.0);

	//================================================================================
	// Constructor
	//================================================================================
	public Size(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static Size of(double width, double height) {
		return new Size(width, height);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Size size = (Size) o;
		return getWidth() == (size.getWidth()) && getHeight() == (size.getHeight());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getWidth(), getHeight());
	}

	@Override
	public String toString() {
		return "W x H (" + getWidth() + " x " + getHeight() + ")";
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public double getWidth() {
		return width.get();
	}

	public DoubleProperty widthProperty() {
		return width;
	}

	public void setWidth(double width) {
		this.width.set(width);
	}

	public double getHeight() {
		return height.get();
	}

	public DoubleProperty heightProperty() {
		return height;
	}

	public void setHeight(double height) {
		this.height.set(height);
	}
}
