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

import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Control;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * Extension of {@link Control} and base class for MaterialFX components. The idea is to have a separate hierarchy of
 * components from the JavaFX one that perfectly integrates with the new Behavior and Theming APIs. In addition to the
 * integrations brought by {@link Control}, this also implements {@link MFXStyleable} and {@link MFXResizable}.
 * <p>
 * <b>Note</b>: the correct way to change the skin is to call {@link #changeSkin(SkinBase)}.
 * <p></p>
 * MaterialFX components, descendants of this, support the usage of custom tooltips through the property {@link #mfxTooltipProperty()}.
 *
 * @see MFXSkinBase
 * @see MFXResizable
 * @see MFXTooltip
 */
public abstract class MFXControl<B extends BehaviorBase<? extends Node>> extends Control<B> implements MFXStyleable, MFXResizable {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<MFXTooltip> mfxTooltip = new SimpleObjectProperty<>() {
		@Override
		public void set(MFXTooltip newValue) {
			MFXTooltip oldValue = get();
			if (oldValue != null) oldValue.dispose();
			if (oldValue == newValue) return;
			newValue.install();
			super.set(newValue);
		}
	};

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Create a new instance of the default skin for this component.
	 */
	protected abstract MFXSkinBase<?, ?> buildSkin();

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public double computeMinWidth(double height) {
		return super.computeMinWidth(height);
	}

	@Override
	public double computeMinHeight(double width) {
		return super.computeMinHeight(width);
	}

	@Override
	public double computePrefWidth(double height) {
		return super.computePrefWidth(height);
	}

	@Override
	public double computePrefHeight(double width) {
		return super.computePrefHeight(width);
	}

	@Override
	public double computeMaxWidth(double height) {
		return super.computeMaxWidth(height);
	}

	@Override
	public double computeMaxHeight(double width) {
		return super.computeMaxHeight(width);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXTooltip getMFXTooltip() {
		return mfxTooltip.get();
	}

	/**
	 * Specifies the {@link MFXTooltip} to use on this control.
	 */
	public ObjectProperty<MFXTooltip> mfxTooltipProperty() {
		return mfxTooltip;
	}

	public void setMFXTooltip(MFXTooltip mfxTooltip) {
		this.mfxTooltip.set(mfxTooltip);
	}
}
