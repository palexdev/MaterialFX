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

package io.github.palexdev.materialfx.controls.buttons;

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

/**
 * Custom implementation of a button which extends {@link MFXLabeled}, has its own skin
 * {@link MFXButtonSkin} and its own behavior {@link MFXButtonBehavior}.
 * <p></p>
 * {@code MFXButton} has 5 variants that mainly override the {@link #defaultStyleClasses()} method
 * to allow themes to style them according to their style classes:
 * <p> - {@link MFXElevatedButton}
 * <p> - {@link MFXFilledButton}
 * <p> - {@link MFXTonalFilledButton}
 * <p> - {@link MFXOutlinedButton}
 * <p> - {@link MFXTextButton}
 * <p></p>
 * <b>This base class is un-styled by the default official themes, a perfect start to implement custom styled buttons.</b>
 * <p>
 * The default style class of this component is: '.mfx-button'.
 */
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
	// Variants
	//================================================================================

	/**
	 * @return a new {@link MFXElevatedButton}
	 */
	public static MFXElevatedButton elevated() {
		return new MFXElevatedButton();
	}

	/**
	 * @return a new {@link MFXFilledButton}
	 */
	public static MFXFilledButton filled() {
		return new MFXFilledButton();
	}

	/**
	 * @return a new {@link MFXTonalFilledButton}
	 */
	public static MFXTonalFilledButton tonalFilled() {
		return new MFXTonalFilledButton();
	}

	/**
	 * @return a new {@link MFXOutlinedButton}
	 */
	public static MFXOutlinedButton outlined() {
		return new MFXOutlinedButton();
	}

	/**
	 * @return a new {@link MFXTextButton}
	 */
	public static MFXTextButton text() {
		return new MFXTextButton();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().setAll(defaultStyleClasses());
		setDefaultBehaviorProvider();
	}

	/**
	 * If not disabled, fires a new {@link ActionEvent}, triggering the {@link EventHandler} specified
	 * by the {@link #onActionProperty()}.
	 */
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
	// Getters/Setters
	//================================================================================
	public EventHandler<ActionEvent> getOnAction() {
		return onAction.get();
	}

	/**
	 * Specifies the action to execute when an {@link ActionEvent} is fired on this button.
	 */
	public EventHandlerProperty<ActionEvent> onActionProperty() {
		return onAction;
	}

	public void setOnAction(EventHandler<ActionEvent> onAction) {
		this.onAction.set(onAction);
	}
}
