/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TooltipBuilder {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXTooltip tooltip;

	//================================================================================
	// Constructors
	//================================================================================
	public TooltipBuilder(Node owner) {
		this(new MFXTooltip(owner));
	}

	public TooltipBuilder(MFXTooltip tooltip) {
		this.tooltip = tooltip;
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public static MFXTooltip of(Node owner, String text) {
		return MFXTooltip.of(owner, text);
	}

	public static void disposeFor(Node node) {
		MFXTooltip.disposeFor(node);
	}

	public TooltipBuilder setPopupStyleableParent(Parent parent) {
		tooltip.setPopupStyleableParent(parent);
		return this;
	}

	public TooltipBuilder addStylesheet(String... stylesheets) {
		tooltip.getStyleSheets().addAll(stylesheets);
		return this;
	}

	public TooltipBuilder setStylesheet(String... stylesheets) {
		tooltip.getStyleSheets().setAll(stylesheets);
		return this;
	}

	public TooltipBuilder setAnimationProvider(BiFunction<Node, Scale, Animation> animationProvider) {
		tooltip.setAnimationProvider(animationProvider);
		return this;
	}

	public TooltipBuilder setAnimated(boolean animated) {
		tooltip.setAnimated(animated);
		return this;
	}

	public TooltipBuilder install() {
		tooltip.install();
		return this;
	}

	public TooltipBuilder uninstall() {
		tooltip.uninstall();
		return this;
	}

	public TooltipBuilder dispose() {
		tooltip.dispose();
		return this;
	}

	public TooltipBuilder setShowAction(Consumer<PositionBean> showAction) {
		tooltip.setShowAction(showAction);
		return this;
	}

	public TooltipBuilder setIcon(Node icon) {
		tooltip.setIcon(icon);
		return this;
	}

	public TooltipBuilder setText(String text) {
		tooltip.setText(text);
		return this;
	}

	public TooltipBuilder setShowDelay(Duration showDelay) {
		tooltip.setShowDelay(showDelay);
		return this;
	}

	public TooltipBuilder setHideAfter(Duration hideAfter) {
		tooltip.setHideAfter(hideAfter);
		return this;
	}
}
