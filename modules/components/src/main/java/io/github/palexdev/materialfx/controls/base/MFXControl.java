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

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.function.Supplier;

public abstract class MFXControl<B extends BehaviorBase<? extends Node>> extends Control implements WithBehavior<B>, MFXStyleable {
	//================================================================================
	// Properties
	//================================================================================
	private final SupplierProperty<B> behaviorProvider = new SupplierProperty<>();

	//================================================================================
	// Abstract Methods
	//================================================================================
	public abstract void defaultBehaviorFactory();

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected SkinBase<?, ?> createDefaultSkin() {
		return null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Supplier<B> getBehaviorProvider() {
		return behaviorProvider.get();
	}

	public SupplierProperty<B> behaviorProviderProperty() {
		return behaviorProvider;
	}

	public void setBehaviorProvider(Supplier<B> behaviorProvider) {
		this.behaviorProvider.set(behaviorProvider);
	}
}
