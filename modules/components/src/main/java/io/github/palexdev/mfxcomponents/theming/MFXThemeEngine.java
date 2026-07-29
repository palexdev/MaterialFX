/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.theming;

import java.util.Collections;
import java.util.Set;

import io.github.palexdev.mcu.MaterialTheme;
import io.github.palexdev.mcu.enums.ExportFormat;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.controls.ThemeEngine;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.NodeUtils;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion.MotionPreset;
import io.github.palexdev.mfxresources.MFXResources;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import static io.github.palexdev.mfxcore.controls.ThemeEngine.Stylesheet.stylesheet;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/// Default [ThemeEngine] implementation for `MaterialFX`, and the entry point of the Theming API.
///
/// This is a singleton, [#instance()], registered as a service by the [Provider] inner class. Modules that do not want
/// to depend on this one can still get hold of it through [java.util.ServiceLoader].
///
/// #### Setup
///
/// The engine is initialized once by [#init(Stage, boolean)] with the app's primary stage, which must already have a
/// [Scene] set on it. Any other scene can then be added with [#registerScene(Scene)] or [#registerWindow(Window)], and
/// dropped again with [#removeScene(Scene)] or [#removeWindow(Window)]. Scenes are held in a [WeakHashSet], so
/// forgetting to remove one won't leak it.
///
/// #### Stylesheets
///
/// Every registered scene gets the same list of [Stylesheets][Stylesheet], in the order they were given. Three of them
/// are shipped by the engine itself: [#FONTS], [#MD_COMPONENTS] and [#MD_MOTION]. The missing piece is the color scheme,
/// which is what really makes a theme, and can be either one of the [MaterialColors] presets or a custom
/// [MaterialTheme].
///
/// There are two ways to go about this:
/// - The color path, [#setColor(MaterialColors)] and [#setColor(MaterialTheme)]: the three stylesheets above are added
///   for you, in the right order, around the given color scheme. Since swapping the colors is by far the most common
///   operation, [#replaceColor(MaterialColors)] does just that, leaving every other stylesheet in the list untouched
/// - The generic path, [#setStylesheets(Stylesheet...)] and [#addStylesheets(Stylesheet...)]: the list is yours to
///   manage. Nothing is added automatically here, not even the three above, which is what you want when theming with
///   your own CSS
///
/// [#init(Stage, boolean)] uses the color path with [MaterialColors#defaultColor()] when asked for the defaults.
///
/// #### Theme Mode
///
/// The [#themeModeProperty()] is not applied by adding or removing stylesheets. Rather, the ':dark' pseudo state is
/// de-/activated on the root of every registered scene, as the themes define both variants. Stages are also informed
/// through [HeaderBar#setSystemColorScheme(Stage, ColorScheme)], so that the window decorations follow along.
///
/// [ThemeMode#SYSTEM] is honored by listening to [Platform#getPreferences()], which means switching the OS to dark mode
/// is enough for the app to switch too. Mode changes can also be animated, see [#animateApply(boolean)].
public class MFXThemeEngine implements ThemeEngine {

    //================================================================================
    // Properties
    //================================================================================

    private static final MFXThemeEngine INSTANCE = new MFXThemeEngine();

    public static MFXThemeEngine instance() {return INSTANCE;}

    /// Declares the fonts used by the themes.
    public static final Stylesheet FONTS = stylesheet(MFXResources.load("fonts/Fonts.css"));
    /// The styles of all `MaterialFX` components. Expects a color scheme to be applied before it.
    public static final Stylesheet MD_COMPONENTS = stylesheet(MFXResources.loadTheme("material/md-theme.css"));
    /// Enables the animations on the components that support them, according to the Material 3 motion system.
    public static final Stylesheet MD_MOTION = stylesheet(MFXResources.loadTheme("material/motion/md-motion.css"));

    private Stage primary;
    private final Set<Scene> scenes = new WeakHashSet<>();
    private final ObservableList<Stylesheet> stylesheets = FXCollections.observableArrayList();
    private final ObjectProperty<ThemeMode> mode = new SimpleObjectProperty<>(ThemeMode.SYSTEM) {
        @Override
        protected void invalidated() {
            applyMode(animateApply);
        }
    };

    private Stylesheet appliedColor;
    private boolean animateApply = false;

    //================================================================================
    // Constructors
    //================================================================================

    private MFXThemeEngine() {
        onInvalidated(Platform.getPreferences().colorSchemeProperty())
            .condition(_ -> getThemeMode() == ThemeMode.SYSTEM)
            .then(_ -> applyMode(animateApply))
            .listen();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Initializes the engine with the app's primary stage, which is expected to already have a [Scene] set on it.
    /// The scene is registered, and the theme mode applied as soon as the stage is shown for the first time.
    ///
    /// When `setDefaults` is `true`, the whole stylesheets stack is set up with [MaterialColors#defaultColor()], see
    /// [#setColor(MaterialColors)]. Otherwise, any stylesheet added before this call is applied as it is.
    ///
    /// Can be called only once, further calls will fail with an [IllegalStateException].
    public MFXThemeEngine init(Stage primaryStage, boolean setDefaults) {
        if (primary != null) throw new IllegalStateException("Theme engine already initialized on: " + primary);
        Scene scene = primaryStage.getScene();
        if (scene == null) throw new IllegalArgumentException("Scene has not been set yet!");

        primary = primaryStage;
        registerScene(scene);
        onInvalidated(primaryStage.showingProperty())
            .condition(identity())
            .then(_ -> applyMode(false))
            .oneShot(true)
            .executeNow()
            .listen();

        if (setDefaults) {
            setColor(MaterialColors.defaultColor());
        } else if (!stylesheets.isEmpty()) {
            applyStylesheets();
        }
        return this;
    }

    /// Sets the current stylesheets on every registered scene, replacing whatever they had before.
    private void applyStylesheets() {
        for (Scene scene : scenes) {
            scene.getStylesheets().setAll(stylesheets.stream()
                .map(Stylesheet::src)
                .toList());
        }
    }

    /// Propagates the [#resolveThemeMode()] to every registered scene, by de-/activating the ':dark' pseudo state on
    /// their root and by setting the system color scheme on their [Stage], if any.
    ///
    /// When `animated` is `true`, the whole thing is wrapped in a cross-fade animation, see [#animate(Runnable)].
    private void applyMode(boolean animated) {
        if (!animated) {
            ThemeMode mode = resolveThemeMode();
            boolean isDark = mode == ThemeMode.DARK;
            for (Scene scene : scenes) {
                if (scene.getWindow() instanceof Stage s) {
                    HeaderBar.setSystemColorScheme(s, ColorScheme.valueOf(mode.name()));
                }

                Parent root = scene.getRoot();
                if (root != null) PseudoClasses.setOn(root, "dark", isDark);
            }
            return;
        }
        animate(() -> applyMode(false));
    }

    /// Runs the given callback behind a cross-fade.
    ///
    /// A snapshot of the primary stage's root is taken and added on top of it as a placeholder, so that the callback can
    /// change the UI unnoticed. The placeholder is then faded out and removed.
    ///
    /// This can only work if the root is a [Pane], otherwise the callback simply runs as it is.
    private void animate(Runnable applyCallback) {
        Parent root = ofNullable(primary)
            .map(Window::getScene)
            .map(Scene::getRoot)
            .orElse(null);
        if (!(root instanceof Pane p)) {
            applyCallback.run();
            return;
        }

        root.setMouseTransparent(true);
        double w = p.getWidth();
        double h = p.getHeight();
        ImageView placeholder = snapshot(p, w, h, new SnapshotParameters());
        p.getChildren().add(placeholder);

        applyCallback.run();
        MotionPreset motion = M3Motion.EXPRESSIVE_SLOW_EFFECTS;
        TimelineBuilder.build()
            .add(KeyFrames.of(motion.duration(), placeholder.opacityProperty(), 0.0, motion.curve()))
            .setOnFinished(_ -> {
                p.getChildren().remove(placeholder);
                p.setMouseTransparent(false);
            })
            .getAnimation()
            .play();
    }

    /// Adds the given scene to the ones managed by the engine, immediately bringing it up to date with the current
    /// stylesheets and theme mode.
    ///
    /// Scenes are held weakly, so this does not prevent them from being garbage collected.
    public MFXThemeEngine registerScene(Scene scene) {
        scenes.add(scene);
        // Apply current
        Parent root = scene.getRoot();
        if (root != null) {
            ThemeMode m = resolveThemeMode();
            PseudoClasses.setOn(root, "dark", m == ThemeMode.DARK);
        }
        scene.getStylesheets().setAll(stylesheets.stream()
            .map(Stylesheet::src)
            .toList());
        return this;
    }

    /// Convenience method to register the given window's scene, which is expected to be already set, otherwise fails
    /// with an [IllegalArgumentException].
    ///
    /// @see #registerScene(Scene)
    public MFXThemeEngine registerWindow(Window window) {
        Scene scene = window.getScene();
        if (scene == null) throw new IllegalArgumentException("Scene has not been set yet on: " + window);
        return registerScene(scene);
    }

    /// Stops managing the given scene. Note that its stylesheets are left as they are, only future updates won't reach
    /// it anymore.
    public MFXThemeEngine removeScene(Scene scene) {
        scenes.remove(scene);
        return this;
    }

    /// Convenience method to stop managing the given window's scene. Contrary to [#registerWindow(Window)], a `null`
    /// scene is simply ignored here.
    ///
    /// @see #removeScene(Scene)
    public MFXThemeEngine removeWindow(Window window) {
        Scene scene = window.getScene();
        if (scene != null) scenes.remove(scene);
        return this;
    }

    /// Appends the given stylesheets to the current ones and re-applies the list on every registered scene.
    public MFXThemeEngine addStylesheets(Stylesheet... stylesheets) {
        Collections.addAll(this.stylesheets, stylesheets);
        applyStylesheets();
        return this;
    }

    /// Replaces the current stylesheets with the given ones, order matters as it is preserved when applying them.
    public MFXThemeEngine setStylesheets(Stylesheet... stylesheets) {
        clearStylesheets();
        Collections.addAll(this.stylesheets, stylesheets);
        applyStylesheets();
        return this;
    }

    /// Removes the given stylesheets from the current ones and re-applies the list on every registered scene.
    public MFXThemeEngine removeStylesheets(Stylesheet... stylesheets) {
        for (Stylesheet stylesheet : stylesheets) {
            if (stylesheet == appliedColor) appliedColor = null;
            this.stylesheets.remove(stylesheet);
        }
        applyStylesheets();
        return this;
    }

    /// Removes every stylesheet, leaving the registered scenes completely unstyled.
    public MFXThemeEngine clearStylesheets() {
        appliedColor = null;
        stylesheets.clear();
        applyStylesheets();
        return this;
    }

    /// Sets up the whole Material stack around the given color preset: [#FONTS], the color scheme, [#MD_COMPONENTS] and
    /// [#MD_MOTION], in this exact order. Any other stylesheet set before this call is discarded.
    public MFXThemeEngine setColor(MaterialColors color) {
        setStylesheets(
            FONTS,
            color,
            MD_COMPONENTS,
            MD_MOTION
        );
        appliedColor = color;
        return this;
    }

    /// Swaps just the color scheme in use with the given one, leaving every other stylesheet in place. This is the way
    /// to go for apps that let the user pick a color at runtime.
    ///
    /// Falls back to [#setColor(MaterialColors)] if there is no color scheme to replace yet.
    public MFXThemeEngine replaceColor(MaterialColors color) {
        int idx;
        if (appliedColor != null && (idx = stylesheets.indexOf(appliedColor)) != -1) {
            stylesheets.set(idx, color);
            applyStylesheets();
            appliedColor = color;
            return this;
        }
        return setColor(color);
    }

    /// Same as [#setColor(MaterialColors)], but for a custom theme generated at runtime, which is converted to a
    /// stylesheet by [#convertTheme(MaterialTheme)].
    public MFXThemeEngine setColor(MaterialTheme color) {
        Stylesheet stylesheet = convertTheme(color);
        setStylesheets(
            FONTS,
            stylesheet,
            MD_COMPONENTS,
            MD_MOTION
        );
        appliedColor = stylesheet;
        return this;
    }

    /// Same as [#replaceColor(MaterialColors)], but for a custom theme generated at runtime, which is converted to a
    /// stylesheet by [#convertTheme(MaterialTheme)].
    public MFXThemeEngine replaceColor(MaterialTheme color) {
        int idx;
        if (appliedColor != null && (idx = stylesheets.indexOf(appliedColor)) != -1) {
            Stylesheet stylesheet = convertTheme(color);
            stylesheets.set(idx, stylesheet);
            applyStylesheets();
            appliedColor = stylesheet;
            return this;
        }
        return setColor(color);
    }

    /// Specifies whether changes to the [#themeModeProperty()] should be animated with a cross-fade. Disabled by
    /// default.
    ///
    /// Note that the very first apply, the one triggered by [#init(Stage, boolean)], is never animated.
    public MFXThemeEngine animateApply(boolean animate) {
        this.animateApply = animate;
        return this;
    }

    /// {@inheritDoc}
    ///
    /// The list is unmodifiable, use the `xxxStylesheets(...)` methods to change it.
    @Override
    public ObservableList<Stylesheet> stylesheets() {
        return FXCollections.unmodifiableObservableList(stylesheets);
    }

    public ObjectProperty<ThemeMode> themeModeProperty() {
        return mode;
    }

    /* Utility */

    /// Converts a [MaterialTheme] to a [Stylesheet] by exporting it as JavaFX CSS and wrapping the result in a data URI,
    /// see [CSSFragment#toDataUri()]. This is what allows color schemes to be generated at runtime rather than being
    /// loaded from a file.
    public static Stylesheet convertTheme(MaterialTheme theme) {
        String rawCss = theme.export(ExportFormat.JAVAFX, false);
        String css = new CSSFragment(rawCss).toDataUri();
        return stylesheet(css);
    }

    /// Takes a snapshot of the given node and returns it as an unmanaged [ImageView] of the given size.
    ///
    /// On HiDPI screens the snapshot is taken at the screen's output scale and then scaled back down through the fit
    /// sizes, otherwise the placeholder would look blurry next to the real UI.
    private static ImageView snapshot(Node node, double w, double h, SnapshotParameters parameters) {
        WritableImage snapshot;
        parameters.setViewport(new Rectangle2D(0, 0, w, h));

        ImageView iw = new ImageView();
        iw.setSmooth(false);
        iw.setPreserveRatio(false);
        iw.setManaged(false);

        Screen screen = NodeUtils.getScreenFor(node);
        if (screen != null && screen.getOutputScaleX() != 1.0) {
            double scale = screen.getOutputScaleX();
            int scaledW = (int) Math.ceil(w * scale);
            int scaledH = (int) Math.ceil(h * scale);
            snapshot = new WritableImage(scaledW, scaledH);
            parameters.setTransform(Transform.scale(scale, scale));
            node.snapshot(parameters, snapshot);

            iw.setFitWidth(scaledW / scale);
            iw.setFitHeight(scaledH / scale);
        } else {
            snapshot = node.snapshot(parameters, null);
            iw.setFitWidth(w);
            iw.setFitHeight(h);
        }

        iw.setImage(snapshot);
        return iw;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    public static class Provider {
        public static MFXThemeEngine provider() {
            return MFXThemeEngine.INSTANCE;
        }
    }
}
