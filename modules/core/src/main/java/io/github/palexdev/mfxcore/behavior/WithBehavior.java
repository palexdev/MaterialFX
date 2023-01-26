/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.behavior;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import javafx.scene.Node;

import java.util.function.Supplier;

public interface WithBehavior<B extends BehaviorBase<? extends Node>> {

	Supplier<B> defaultBehaviorProvider();

	void setBehaviorProvider(Supplier<B> factory);

	SupplierProperty<B> behaviorProviderProperty();

	Supplier<B> getBehaviorProvider();

	default void setDefaultBehaviorProvider() {
		setBehaviorProvider(defaultBehaviorProvider());
	}
}
