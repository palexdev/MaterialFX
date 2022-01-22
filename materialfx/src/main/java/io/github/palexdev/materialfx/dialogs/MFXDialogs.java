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

package io.github.palexdev.materialfx.dialogs;

import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.font.MFXFontIcon;

/**
 * Utility class to build some preset dialogs.
 */
public class MFXDialogs {

	private MFXDialogs() {
	}

	public static MFXGenericDialogBuilder info() {
		MFXFontIcon infoIcon = new MFXFontIcon("mfx-info-circle-filled", 18);
		return MFXGenericDialogBuilder.build().addStyleClasses("mfx-info-dialog").setHeaderIcon(infoIcon);
	}

	public static MFXGenericDialogBuilder warn() {
		MFXFontIcon warnIcon = new MFXFontIcon("mfx-do-not-enter-circle", 18);
		return MFXGenericDialogBuilder.build().addStyleClasses("mfx-warn-dialog").setHeaderIcon(warnIcon);
	}

	public static MFXGenericDialogBuilder error() {
		MFXFontIcon errorIcon = new MFXFontIcon("mfx-exclamation-circle-filled", 18);
		return MFXGenericDialogBuilder.build().addStyleClasses("mfx-error-dialog").setHeaderIcon(errorIcon);
	}

	public static <T> MFXGenericDialogBuilder filter() {
		return MFXGenericDialogBuilder.build(new MFXFilterDialog<T>(new MFXFilterPane<>()));
	}

	public static <T> MFXGenericDialogBuilder filter(MFXFilterPane<T> filterPane) {
		return MFXGenericDialogBuilder.build(new MFXFilterDialog<>(filterPane));
	}
}
