module mfx.components {
    requires transitive javafx.controls;

    requires transitive mfx.core;
    requires transitive mfx.effects;
    requires transitive mfx.localization;
    requires transitive mfx.resources;

    // Behaviors
    exports io.github.palexdev.mfxcomponents.behaviors;

    // Controls
    exports io.github.palexdev.mfxcomponents.controls;
    exports io.github.palexdev.mfxcomponents.controls.base;
    exports io.github.palexdev.mfxcomponents.controls.buttons;
    exports io.github.palexdev.mfxcomponents.controls.fab;

    // Layout
    exports io.github.palexdev.mfxcomponents.layout;

    // Skins
    exports io.github.palexdev.mfxcomponents.skins;
    exports io.github.palexdev.mfxcomponents.skins.base;

    // Theming
    exports io.github.palexdev.mfxcomponents.theming.base;
    exports io.github.palexdev.mfxcomponents.theming.enums;

    // Window
    exports io.github.palexdev.mfxcomponents.window.popups;
}