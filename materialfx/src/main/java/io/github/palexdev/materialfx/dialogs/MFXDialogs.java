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
