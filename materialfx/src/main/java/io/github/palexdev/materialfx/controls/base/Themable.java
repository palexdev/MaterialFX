package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.css.themes.Stylesheets;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.css.themes.Themes;
import io.github.palexdev.materialfx.utils.SceneBuilderIntegration;
import javafx.scene.Parent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Public API for all controls that are integrated with the new Theming API, see {@link Theme}.
 * <p></p>
 * Ideally such controls should be instances of {@link Parent} for "proper" support in SceneBuilder, and they should specify
 * which is their own "theme"/stylesheet.
 * <p></p>
 * <b>SceneBuilder Support</b>
 * <p>
 * By default, all controls implementing this will automatically check upon creation if they are being used inside SceneBuilder.
 * When the software is detected, the control' stylesheet is added to itself, this is done on every new control added to the scene.
 * <p>
 * Despite appearances, this is the simplest and <b>fastest</b> solution.
 * <p>
 * Other approaches would involve adding a listener on the scene property of the node to detect when it was available.
 * Then the {@link Themes} were added to the scene. This is not feasible as controls have two scenes in two separate moments:
 * <p> 1) When the control is dragged into the main scene, the "drag effect" you see is basically a moving popup, so it's not the main scene
 * <p> 2) When the mouse is released and the control is placed, it is then in the main scene
 * <p>
 * Styling only the main scene resulted in an "ugly" experience since controls in the drag popup would be un-styled.
 * Styling both would lead to a huge performance drawback of the app, because now JavaFX would have to process the themes
 * (the entire themes!) on TWO scenes.
 * <p></p>
 * This approach on the other hand not only is fast, but it should also be more flexible. From my tests, it seems that
 * adding stylesheets to each component doesn't break styling, you will still be able to add stylesheets to each control
 * individually or on its parent, and it should still work fine in any case.
 * <p></p>
 * As always, bugs and unexpected behaviors are behind the corner, that's why there's also an emergency switch to completely
 * shut off the SceneBuilder integration.
 * <p>
 * To do so, it's just enough to create a file in the following directories:
 * <p> - For Windows users: ~\AppData\Local\Scene Builder
 * <p> - For MacOS users: ~/Library/Application Support/Scene Builder
 * <p> - For Linux users: ~/.scenebuilder
 * In the folder relative to your OS, create a file named exactly like this: MFX_SB_OFF
 */
public interface Themable {

	/**
	 * Implementations should return the {@link Parent} node onto which themes and stylesheets will be applied.
	 * Most of the case its themselves.
	 */
	Parent toParent();

	/**
	 * Implementations of this should return the {@link Theme} responsible for styling themselves, most MaterialFX controls
	 * return one of the constants offered by {@link Stylesheets}.
	 */
	Theme getTheme();

	/**
	 * This is the method responsible for SceneBuilder detection and integration.
	 * <p></p>
	 * By default, this adds the {@link Theme} returned by {@link #getTheme()} on the {@link Parent} returned by {@link #toParent()}.
	 *
	 * @return whether SceneBuilder was detected or not, allowing overrides of this to avoid calling the check again and just
	 * checking the return of this method
	 */
	default boolean sceneBuilderIntegration() {
		if (!SceneBuilderIntegration.isInSceneBuilder() || Helper.isInhibitSBSupport()) return false;
		Helper.themeIt(this);
		return true;
	}

	class Helper {
		public static final Path SB_WIN_PATH = Path.of(System.getenv("APPDATA") + "/Scene Builder");
		public static final Path SB_MAC_PATH = Path.of(System.getProperty("user.home") + "/Library/Application Support/Scene Builder");
		public static final Path SB_LIN_PATH = Path.of(System.getProperty("user.home") + "/.scenebuilder");
		private static OSType os = null;
		private static Boolean inhibitSBSupport = null;

		public enum OSType {
			Windows, MacOS, Linux, Other
		}

		protected static void themeIt(Themable t) {
			Parent parent = t.toParent();
			Set<String> stylesheets = new HashSet<>(parent.getStylesheets());
			String theme = t.getTheme().loadTheme();
			if (stylesheets.contains(theme)) return;
			parent.getStylesheets().add(theme);
		}

		protected static OSType detectOS() {
			if (os == null) {
				String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
				if ((OS.contains("mac")) || (OS.contains("darwin"))) {
					os = OSType.MacOS;
				} else if (OS.contains("win")) {
					os = OSType.Windows;
				} else if (OS.contains("nux")) {
					os = OSType.Linux;
				} else {
					os = OSType.Other;
				}
			}
			return os;
		}

		protected static boolean isInhibitSBSupport() {
			if (inhibitSBSupport == null) {
				switch (detectOS()) {
					case Windows: {
						inhibitSBSupport = Files.exists(SB_WIN_PATH.resolve("MFX_SB_OFF"));
						break;
					}
					case MacOS: {
						inhibitSBSupport = Files.exists(SB_MAC_PATH.resolve("MFX_SB_OFF"));
						break;
					}
					case Linux: {
						inhibitSBSupport = Files.exists(SB_LIN_PATH.resolve("MFX_SB_OFF"));
						break;
					}
					default: {
						inhibitSBSupport = false;
						break;
					}
				}
			}
			return inhibitSBSupport;
		}
	}
}
