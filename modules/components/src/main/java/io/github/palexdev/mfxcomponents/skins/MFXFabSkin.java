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

import io.github.palexdev.mfxcomponents.behaviors.MFXFabBehavior;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFabBase;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;

import static io.github.palexdev.mfxcore.observables.When.onChanged;

/**
 * Default skin implementation for {@link MFXFabBase} components.
 * <p>
 * Extends {@link MFXButtonSkin}.
 * <p></p>
 * This is needed for the animations defined in {@link MFXFabBehavior} to work properly.
 * <p>
 * The min width computation is overridden to return {@link Region#USE_COMPUTED_SIZE}, while the
 * pref width property is overridden to adapt to the {@link MFXFabBase#extendedProperty()}.
 * <p></p>
 * The layout strategy is also overridden so that the label is never truncated, this is also needed for the animations
 * to look as expected. Also, FABs are not supposed to be truncated since they are important UI elements.
 */
public class MFXFabSkin extends MFXButtonSkin {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFabSkin(MFXFabBase fab) {
		super(fab);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void addListeners() {
		MFXFabBase fab = getFab();

		// Extended FABs can only have trailing icons
		label.contentDisplayProperty().unbind();
		label.setContentDisplay(ContentDisplay.LEFT);
		onChanged(fab.contentDisplayProperty())
				.then((o, n) -> {
					ContentDisplay cd = (n == ContentDisplay.TEXT_ONLY) ? ContentDisplay.TEXT_ONLY : ContentDisplay.LEFT;
					label.setContentDisplay(cd);
				})
				.executeNow()
				.listen();
	}

	@Override
	public double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return Region.USE_COMPUTED_SIZE;
	}

	@Override
	public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXFabBase fab = getFab();
		MFXFontIcon icon = fab.getIcon();
		double iW = (icon != null) ? icon.getLayoutBounds().getWidth() : 0.0;
		double gap = (icon != null) ? fab.getGraphicTextGap() : 0.0;
		return fab.isExtended() ?
				leftInset + iW + gap + snapSizeX(TextUtils.computeTextWidth(fab.getFont(), fab.getText())) + rightInset :
				leftInset + iW + rightInset;
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		MFXFabBase fab = getFab();
		double lW = snapSizeX(LayoutUtils.boundWidth(label));
		double lH = snapSizeY(LayoutUtils.boundHeight(label));

		Position labelPos = LayoutUtils.computePosition(
				fab, label,
				x, y, w, h, 0, Insets.EMPTY,
				HPos.LEFT, fab.getAlignment().getVpos(),
				true, true
		);

		label.resizeRelocate(labelPos.getX(), labelPos.getY(), lW, lH);
		rg.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
	}

	//================================================================================
	// Getters
	//================================================================================

	/**
	 * @return {@link #getSkinnable()} cast to {@link MFXFabBase}
	 */
	protected MFXFabBase getFab() {
		return (MFXFabBase) getSkinnable();
	}
}
