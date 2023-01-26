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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.behaviors.MFXButtonBehavior;
import io.github.palexdev.materialfx.controls.base.MFXLabeled;
import io.github.palexdev.materialfx.skins.MFXButtonSkin;
import io.github.palexdev.mfxcore.base.properties.EventHandlerProperty;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.List;
import java.util.function.Supplier;

public class MFXButton extends MFXLabeled<MFXButtonBehavior> {
	//================================================================================
	// Properties
	//================================================================================
	private final EventHandlerProperty<ActionEvent> onAction = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, get());
		}
	};

	//================================================================================
	// Constructors
	//================================================================================

	public MFXButton() {
		this("");
	}

	public MFXButton(String text) {
		this(text, null);
	}

	public MFXButton(String text, Node icon) {
		super(text, icon);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().setAll(defaultStyleClasses());
		setDefaultBehaviorProvider();
	}

	public void fire() {
		if (!isDisabled()) fireEvent(new ActionEvent());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public Supplier<MFXButtonBehavior> defaultBehaviorProvider() {
		return () -> new MFXButtonBehavior(this);
	}

	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button");
	}

	@Override
	protected SkinBase<?, ?> createDefaultSkin() {
		return new MFXButtonSkin(this);
	}

	//================================================================================
	// Variants
	//================================================================================
	public static class MFXElevatedButton extends MFXButton {
		public MFXElevatedButton() {
		}

		public MFXElevatedButton(String text) {
			super(text);
		}

		public MFXElevatedButton(String text, Node icon) {
			super(text, icon);
		}

		@Override
		public List<String> defaultStyleClasses() {
			return List.of("mfx-button", "elevated");
		}
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public EventHandler<ActionEvent> getOnAction() {
		return onAction.get();
	}

	public EventHandlerProperty<ActionEvent> onActionProperty() {
		return onAction;
	}

	public void setOnAction(EventHandler<ActionEvent> onAction) {
		this.onAction.set(onAction);
	}
}
