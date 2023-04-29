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

/**
 * Public API for all components that want to integrate with the new Behavior API.
 *
 * @param <B> the type of behavior the component will use
 */
public interface WithBehavior<B extends BehaviorBase<? extends Node>> {

	/**
	 * @return the instance of the current behavior object
	 */
	B getBehavior();

	/**
	 * @return a {@link Supplier} that is the provider for the default behavior used by the component.
	 */
	Supplier<B> defaultBehaviorProvider();

	Supplier<B> getBehaviorProvider();

	/**
	 * Specifies the {@link Supplier} used to produce a behavior object for the component.
	 */
	SupplierProperty<B> behaviorProviderProperty();

	void setBehaviorProvider(Supplier<B> factory);

	/**
	 * Restores the components behavior to the default one using {@link #defaultBehaviorProvider()}
	 * and {@link #setBehaviorProvider(Supplier)}.
	 */
	default void setDefaultBehaviorProvider() {
		setBehaviorProvider(defaultBehaviorProvider());
	}
}
