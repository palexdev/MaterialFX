import io.github.palexdev.mfxcore.controls.ThemeEngine;

module mfx.core {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.desktop;

    requires transitive mfx.localization;

    requires java.prefs;

    // Base
    exports io.github.palexdev.mfxcore.base;
    exports io.github.palexdev.mfxcore.base.beans;
    exports io.github.palexdev.mfxcore.base.beans.range;
    exports io.github.palexdev.mfxcore.base.bindings;
    exports io.github.palexdev.mfxcore.base.properties;
    exports io.github.palexdev.mfxcore.base.properties.base;
    exports io.github.palexdev.mfxcore.base.properties.functional;
    exports io.github.palexdev.mfxcore.base.properties.range;
    exports io.github.palexdev.mfxcore.base.properties.resettable;
    exports io.github.palexdev.mfxcore.base.properties.styleable;
    exports io.github.palexdev.mfxcore.base.properties.synced;

    // Behavior
    exports io.github.palexdev.mfxcore.behavior;

    // Builders
    exports io.github.palexdev.mfxcore.builders.base;
    exports io.github.palexdev.mfxcore.builders.bindings;
    exports io.github.palexdev.mfxcore.builders.nodes;

    // Collections
    exports io.github.palexdev.mfxcore.collections;

    // Controls
    exports io.github.palexdev.mfxcore.controls;
    uses ThemeEngine;

    // Enums
    exports io.github.palexdev.mfxcore.enums;

    // Events
    exports io.github.palexdev.mfxcore.events;
    exports io.github.palexdev.mfxcore.events.bus;

    // Filter
    exports io.github.palexdev.mfxcore.filter;
    exports io.github.palexdev.mfxcore.filter.base;

    // Input
    exports io.github.palexdev.mfxcore.input;

    // Observables
    exports io.github.palexdev.mfxcore.observables;

    // Popups
    exports io.github.palexdev.mfxcore.popups;
    exports io.github.palexdev.mfxcore.popups.menu;
    exports io.github.palexdev.mfxcore.popups.notifications;

    // Selection
    exports io.github.palexdev.mfxcore.selection;
    exports io.github.palexdev.mfxcore.selection.model;

    // Settings
    exports io.github.palexdev.mfxcore.settings;

    // Utils
    exports io.github.palexdev.mfxcore.utils;
    exports io.github.palexdev.mfxcore.utils.converters;
    exports io.github.palexdev.mfxcore.utils.fx;
    exports io.github.palexdev.mfxcore.utils.fx.loader;
    exports io.github.palexdev.mfxcore.utils.fx.resize;
    exports io.github.palexdev.mfxcore.utils.fx.resize.base;
    exports io.github.palexdev.mfxcore.utils.fx.resize.shapes;

    // Validations
    exports io.github.palexdev.mfxcore.validation;
}