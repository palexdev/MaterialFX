package io.github.palexdev.materialfx.css.themes;

import io.github.palexdev.materialfx.controls.MFXPopup;
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

    @SafeVarargs
    public static <T extends Theme> void addOn(MFXPopup popup, T... ts) {
        for (T t : ts) {
            popup.getStyleSheets().add(t.loadTheme());
        }
    }

    @SafeVarargs
    public static <T extends Theme> void setOn(MFXPopup popup, T... ts) {
        popup.getStyleSheets().clear();
        addOn(popup, ts);
    }
}
