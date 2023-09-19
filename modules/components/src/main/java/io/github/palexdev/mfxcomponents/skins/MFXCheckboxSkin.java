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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXCheckboxBehavior;
import io.github.palexdev.mfxcomponents.controls.MaterialSurface;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onChanged;

/**
 * Default skin implementation for {@link MFXCheckbox} components, extends {@link MFXLabeledSkin}.
 * <p></p>
 * The layout is simple. There are only three nodes: one is the box which also contains the check mark icon,
 * then there's the {@link MaterialSurface} node used to represent the various interaction states, and finally the
 * label (which is removed if setting {@link ContentDisplay#GRAPHIC_ONLY}). The box sits on top of the surface, the label
 * at their right.
 * <p>
 * It is allowed to change the checkbox state by clicking on the label too, as the handlers are added on the entire
 * checkbox rather than just the box. However, the {@link MFXRippleGenerator} resides in the {@link MaterialSurface},
 * when events come from outside the surface area, it's desirable to still show the ripple effect, as a 'fallback' they are
 * generated at the center of the box/surface.
 */
public class MFXCheckboxSkin extends MFXLabeledSkin<MFXCheckbox, MFXCheckboxBehavior> {
	//================================================================================
	// Properties
	//================================================================================
	private final MaterialSurface surface;
	private final MFXIconWrapper icon;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckboxSkin(MFXCheckbox checkBox) {
		super(checkBox);
		initTextMeasurementCache();

		// Init icon
		icon = new MFXIconWrapper(new MFXFontIcon());
		icon.setCacheShape(false);

		// Init surface
		surface = new MaterialSurface(checkBox)
			.initRipple(rg -> {
				rg.setMeToPosConverter(me -> {
					if (rg.canGenerateAt(me.getX(), me.getY())) return Position.of(me.getX(), me.getY());
					Bounds b = icon.getBoundsInParent();
					return Position.of(b.getCenterX(), b.getCenterY());
				});
				rg.setRippleColor(Color.web("#d7d1e7"));
			});

		// Finalize init
		getChildren().addAll(surface, icon);
		if (checkBox.getContentDisplay() != ContentDisplay.GRAPHIC_ONLY) getChildren().add(label);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners:
	 * <p> - A listener on the {@link MFXCheckbox#contentDisplayProperty()} to add/remove the label node
	 * when the values is/is not {@link ContentDisplay#GRAPHIC_ONLY}.
	 */
	private void addListeners() {
		MFXCheckbox checkBox = getSkinnable();
		listeners(
			onChanged(checkBox.contentDisplayProperty())
				.then((o, n) -> {
					if (n == ContentDisplay.GRAPHIC_ONLY) {
						getChildren().remove(label);
						return;
					}
					getChildren().add(label);
				})
		);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * Initializes the given {@link MFXCheckboxBehavior} to handle events such as: {@link MouseEvent#MOUSE_PRESSED},
	 * {@link MouseEvent#MOUSE_RELEASED}, {@link MouseEvent#MOUSE_CLICKED}, {@link MouseEvent#MOUSE_EXITED} and
	 * {@link KeyEvent#KEY_PRESSED}.
	 */
	@Override
	protected void initBehavior(MFXCheckboxBehavior behavior) {
		MFXCheckbox checkBox = getSkinnable();
		MFXRippleGenerator rg = surface.getRippleGenerator();
		behavior.init();
		events(
			intercept(checkBox, MouseEvent.MOUSE_PRESSED)
				.process(behavior::mousePressed),

			intercept(checkBox, MouseEvent.MOUSE_RELEASED)
				.process(behavior::mouseReleased),

			intercept(checkBox, MouseEvent.MOUSE_CLICKED)
				.process(behavior::mouseClicked),

			intercept(checkBox, MouseEvent.MOUSE_EXITED)
				.process(behavior::mouseExited),

			intercept(checkBox, KeyEvent.KEY_PRESSED)
				.process(e -> behavior.keyPressed(e, c -> {
					Bounds b = icon.getLayoutBounds();
					rg.generate(b.getCenterX(), b.getCenterY());
				}))
		);
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXCheckbox checkBox = getSkinnable();
		double gap = checkBox.getGraphicTextGap();
		double insets = leftInset + rightInset;
		double tW = getCachedTextWidth();
		if (checkBox.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
			tW = 0;
			gap = 0;
		}
		double gW = Math.max(
			LayoutUtils.boundWidth(surface),
			icon.getSize()
		);
		return insets + gap + tW + gW;
	}

	@Override
	public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXCheckbox checkBox = getSkinnable();
		double insets = topInset + bottomInset;
		double tH = getCachedTextHeight();
		if (checkBox.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) tH = 0;
		double gH = Math.max(LayoutUtils.boundHeight(surface), icon.getSize());
		return Math.max(tH, gH) + insets;
	}

	@Override
	public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXCheckbox checkBox = getSkinnable();
		double gap = checkBox.getGraphicTextGap();
		layoutInArea(surface, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);
		layoutInArea(icon, x, y, surface.getWidth(), surface.getHeight(), 0, HPos.CENTER, VPos.CENTER);
		if (checkBox.getContentDisplay() != ContentDisplay.GRAPHIC_ONLY)
			layoutInArea(label, x + surface.getWidth() + gap, y, w, h, 0, HPos.LEFT, VPos.CENTER);
	}

	@Override
	public void dispose() {
		surface.dispose();
		super.dispose();
	}
}