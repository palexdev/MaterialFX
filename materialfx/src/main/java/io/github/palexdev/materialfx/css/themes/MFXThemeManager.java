package io.github.palexdev.materialfx.css.themes;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class MFXThemeManager {

	@SafeVarargs
	public static <T extends Theme> void addOn(Parent parent, T... ts) {
		for (T t : ts) {
			parent.getStylesheets().add(t.loadTheme());
		}
	}

	@SafeVarargs
	public static <T extends Theme> void setOn(Parent parent, T... ts) {
		parent.getStylesheets().clear();
		addOn(parent, ts);
	}

	@SafeVarargs
	public static <T extends Theme> void addOn(Scene scene, T... ts) {
		for (T t : ts) {
			scene.getStylesheets().add(t.loadTheme());
		}
	}

	@SafeVarargs
	public static <T extends Theme> void setOn(Scene scene, T... ts) {
		scene.getStylesheets().clear();
		addOn(scene, ts);
	}
}
