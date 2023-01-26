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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.behaviors.MFXButtonBehavior;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.theming.PseudoClasses;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;

import java.util.function.Supplier;

import static io.github.palexdev.mfxcore.observables.When.disposeFor;
import static io.github.palexdev.mfxcore.observables.When.onChanged;

public class MFXButtonSkin extends SkinBase<MFXButton, MFXButtonBehavior> {
	//================================================================================
	// Properties
	//================================================================================
	private final BoundLabel label;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXButtonSkin(MFXButton button) {
		super(button);

		// Init nodes
		label = new BoundLabel(button);

		// Init behavior
		Supplier<MFXButtonBehavior> bp = button.getBehaviorProvider();
		MFXButtonBehavior behavior = bp.get();
		setBehavior(behavior);

		// Finalize init
		getChildren().add(label);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXButton button = getSkinnable();
		onChanged(button.graphicProperty())
				.then((o, n) -> {
					if (o != null) getChildren().remove(o);
					if (n != null) getChildren().add(n);
				})
				.executeNow()
				.listen();
		onChanged(button.contentDisplayProperty())
				.then((o, n) -> {
					Node graphic = button.getGraphic();
					boolean wil = (graphic != null) && (n == ContentDisplay.LEFT);
					boolean wir = (graphic != null) && (n == ContentDisplay.RIGHT);
					PseudoClasses.WITH_ICON_LEFT.setOn(button, wil);
					PseudoClasses.WITH_ICON_RIGHT.setOn(button, wir);
				})
				.executeNow()
				.invalidating(button.graphicProperty())
				.listen();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void initBehavior() {
		MFXButton button = getSkinnable();
		handle(button, MouseEvent.MOUSE_PRESSED, e -> button.requestFocus());
		handle(button, MouseEvent.MOUSE_CLICKED, e -> button.fire());
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXButton button = getSkinnable();
		double insets = leftInset + rightInset;
		double tW = TextUtils.computeTextWidth(label.getFont(), label.getText());
		if (button.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) tW = 0;
		double gW = (button.getGraphic() != null) ? LayoutUtils.boundWidth(button.getGraphic()) + button.getGraphicTextGap() : 0.0;
		return insets + tW + gW;
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXButton button = getSkinnable();
		double insets = topInset + bottomInset;
		double tH = TextUtils.computeTextHeight(label.getFont(), label.getText());
		double gH = button.getGraphic() != null ? LayoutUtils.boundHeight(button.getGraphic()) : 0.0;
		return insets + Math.max(tH, gH);
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	public void dispose() {
		MFXButton button = getSkinnable();
		disposeFor(button.graphicProperty());
		disposeFor(button.contentDisplayProperty());
		super.dispose();
	}
}
