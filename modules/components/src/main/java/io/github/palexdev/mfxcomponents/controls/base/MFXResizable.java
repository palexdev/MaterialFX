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

package io.github.palexdev.mfxcomponents.controls.base;

import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;

/**
 * Simple interface which forces components to expose their size computation methods.
 * They are defined in {@link Parent} and {@link Region}, but they get more interesting when overridden in {@link Control},
 * because such class delegates the computations to the {@link SkinBase}.
 * <p>
 * Having these methods publicly available can be useful if one wants to compute a component's size directly from its skin,
 * bypassing the layout cache.
 */
public interface MFXResizable {

	double computeMinWidth(double height);

	double computeMinHeight(double width);

	double computePrefWidth(double height);

	double computePrefHeight(double width);

	double computeMaxWidth(double height);

	double computeMaxHeight(double width);
}
