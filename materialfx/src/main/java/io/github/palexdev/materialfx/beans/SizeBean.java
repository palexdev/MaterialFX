/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.beans;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * This bean contains two {@link DoubleProperty} to keep track/specify the sizes of something
 * in terms of width and height.
 */
public class SizeBean {
	//================================================================================
	// Properties
	//================================================================================
	private final DoubleProperty width = new SimpleDoubleProperty(0.0);
	private final DoubleProperty height = new SimpleDoubleProperty(0.0);

	//================================================================================
	// Constructor
	//================================================================================
	public SizeBean(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static SizeBean of(double width, double height) {
		return new SizeBean(width, height);
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
