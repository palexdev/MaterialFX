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

package io.github.palexdev.mfxcomponents.controls.base;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.scene.Node;
import javafx.scene.control.Labeled;

import java.util.function.Supplier;

/**
 * Base class for MaterialFX controls that are text based. The idea is to have a separate hierarchy of components from the JavaFX one,
 * * that perfectly integrates with the new Behavior and Theming APIs.
 * <p>
 * Extends {@link Labeled} and implements both {@link WithBehavior} and {@link MFXStyleable}.
 *
 * @param <B> the behavior type used by the control
 */
public abstract class MFXLabeled<B extends BehaviorBase<? extends Node>> extends Labeled implements WithBehavior<B>, MFXStyleable {
	//================================================================================
	// Properties
	//================================================================================
	private final SupplierProperty<B> behaviorProvider = new SupplierProperty<>();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLabeled() {
	}

	public MFXLabeled(String text) {
		super(text);
	}

	public MFXLabeled(String text, Node graphic) {
		super(text, graphic);
	}

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
	@Override
	public Supplier<B> getBehaviorProvider() {
		return behaviorProvider.get();
	}

	@Override
	public SupplierProperty<B> behaviorProviderProperty() {
		return behaviorProvider;
	}

	@Override
	public void setBehaviorProvider(Supplier<B> behaviorProvider) {
		this.behaviorProvider.set(behaviorProvider);
	}
}
