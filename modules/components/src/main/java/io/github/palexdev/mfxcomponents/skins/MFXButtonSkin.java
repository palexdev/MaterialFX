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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.observables.When.disposeFor;
import static io.github.palexdev.mfxcore.observables.When.onChanged;

/**
 * Default skin implementation for {@link MFXButton}s.
 * <p>
 * Extends {@link MFXSkinBase} allowing seamless integration with the new Behavior API. This
 * skin uses behaviors of type {@link MFXButtonBehavior}.
 * <p></p>
 * The layout is simple, there are just the label to show the text and the {@link MFXRippleGenerator} to generate ripples.
 */
public class MFXButtonSkin extends MFXSkinBase<MFXButton, MFXButtonBehavior> {
	//================================================================================
	// Properties
	//================================================================================
	protected final BoundLabel label;
	protected final MFXRippleGenerator rg;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXButtonSkin(MFXButton button) {
		super(button);

		// Init nodes
		label = new BoundLabel(button);
		label.onSetTextNode(n -> n.opacityProperty().bind(button.textOpacityProperty()));

		rg = new MFXRippleGenerator(button);
		rg.setManaged(false);
		rg.setAnimateBackground(false);
		rg.setAutoClip(true);
		rg.setRipplePrefSize(50);
		rg.setRippleColor(Color.web("d7d1e7"));

		// Finalize init
		getChildren().addAll(rg, label);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners:
	 * <p> - A listener on the {@link MFXButton#contentDisplayProperty()} to activate/disable the pseudo classes
	 * {@link PseudoClasses#WITH_ICON_LEFT} and {@link PseudoClasses#WITH_ICON_RIGHT} accordingly
	 */
	protected void addListeners() {
		MFXButton button = getSkinnable();
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

	/**
	 * Initializes the given {@link MFXButtonBehavior} to handle events such as: {@link MouseEvent#MOUSE_PRESSED},
	 * {@link MouseEvent#MOUSE_CLICKED}.
	 */
	@Override
	protected void initBehavior(MFXButtonBehavior behavior) {
		MFXButton button = getSkinnable();
		handle(button, MouseEvent.MOUSE_PRESSED, e -> behavior.generateRipple(rg, e));
		handle(button, MouseEvent.MOUSE_PRESSED, behavior::mousePressed);
		handle(button, MouseEvent.MOUSE_CLICKED, behavior::mouseClicked);
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
	protected void layoutChildren(double x, double y, double w, double h) {
		super.layoutChildren(x, y, w, h);

		MFXButton button = getSkinnable();
		rg.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
	}

	@Override
	public void dispose() {
		MFXButton button = getSkinnable();
		label.getTextNode().ifPresent(n -> n.opacityProperty().unbind());
		disposeFor(button.contentDisplayProperty());
		super.dispose();
	}
}
