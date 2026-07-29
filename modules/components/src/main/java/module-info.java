import io.github.palexdev.mfxcomponents.theming.MFXThemeEngine;
import io.github.palexdev.mfxcore.controls.ThemeEngine;

module mfx.components {
    requires transitive javafx.controls;

    requires transitive mfx.core;
    requires transitive mfx.effects;
    requires transitive mfx.localization;
    requires transitive mfx.resources;
    requires transitive material.color.utilities;
    requires transitive VirtualizedFX;

    // Behaviors
    exports io.github.palexdev.mfxcomponents.behaviors;

    // Controls
    exports io.github.palexdev.mfxcomponents.controls.base;
    exports io.github.palexdev.mfxcomponents.controls.cells;
    exports io.github.palexdev.mfxcomponents.controls;

    // Popups
    exports io.github.palexdev.mfxcomponents.popups;

    // Skins
    exports io.github.palexdev.mfxcomponents.skins.base;
    exports io.github.palexdev.mfxcomponents.skins;

    // Theming
    exports io.github.palexdev.mfxcomponents.theming;
    provides ThemeEngine with MFXThemeEngine.Provider;

    // Variants
    exports io.github.palexdev.mfxcomponents.variants.api;
    exports io.github.palexdev.mfxcomponents.variants;
}