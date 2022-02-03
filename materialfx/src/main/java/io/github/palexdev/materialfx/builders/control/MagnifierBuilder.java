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
import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXMagnifierPane;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class MagnifierBuilder extends ControlBuilder<MFXMagnifierPane> {

	//================================================================================
	// Constructors
	//================================================================================
	public MagnifierBuilder() {
		this(new MFXMagnifierPane(null));
	}

	public MagnifierBuilder(MFXMagnifierPane magnifier) {
		super(magnifier);
	}

	public static MagnifierBuilder textField() {
		return new MagnifierBuilder();
	}

	public static MagnifierBuilder textField(MFXMagnifierPane magnifier) {
		return new MagnifierBuilder(magnifier);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public MagnifierBuilder setLensSize(double lensSize) {
		node.setLensSize(lensSize);
		return this;
	}

	public MagnifierBuilder setZoom(double zoom) {
		node.setZoom(zoom);
		return this;
	}

	public MagnifierBuilder setZoomIncrement(double zoomIncrement) {
		node.setZoomIncrement(zoomIncrement);
		return this;
	}

	public MagnifierBuilder setMinZoom(double minZoom) {
		node.setMinZoom(minZoom);
		return this;
	}

	public MagnifierBuilder setMaxZoom(double maxZoom) {
		node.setMaxZoom(maxZoom);
		return this;
	}

	public MagnifierBuilder setPickerPos(VPos pickerPos) {
		node.setPickerPos(pickerPos);
		return this;
	}

	public MagnifierBuilder setPickerSpacing(double pickerSpacing) {
		node.setPickerSpacing(pickerSpacing);
		return this;
	}

	public MagnifierBuilder setHideCursor(boolean hideCursor) {
		node.setHideCursor(hideCursor);
		return this;
	}

	public MagnifierBuilder setShowZoomLabel(boolean showZoomLabel) {
		node.setShowZoomLabel(showZoomLabel);
		return this;
	}

	public MagnifierBuilder setHideZoomLabelAfter(double hideZoomLabelAfter) {
		node.setHideZoomLabelAfter(hideZoomLabelAfter);
		return this;
	}

	public MagnifierBuilder setContent(Node content) {
		node.setContent(content);
		return this;
	}

	public MagnifierBuilder setPosition(PositionBean position) {
		node.setPosition(position);
		return this;
	}

	public MagnifierBuilder setColorConverter(StringConverter<Color> colorConverter) {
		node.setColorConverter(colorConverter);
		return this;
	}
}
