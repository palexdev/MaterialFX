module MaterialFX {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    requires virtualizedfx;

    exports io.github.palexdev.materialfx;
    exports io.github.palexdev.materialfx.beans;
    exports io.github.palexdev.materialfx.beans.properties.base;
    exports io.github.palexdev.materialfx.beans.properties.resettable;
    exports io.github.palexdev.materialfx.beans.properties.synced;
    exports io.github.palexdev.materialfx.bindings;
    exports io.github.palexdev.materialfx.collections;
    exports io.github.palexdev.materialfx.controls;
    exports io.github.palexdev.materialfx.controls.base;
    exports io.github.palexdev.materialfx.controls.cell;
    exports io.github.palexdev.materialfx.controls.enums;
    exports io.github.palexdev.materialfx.controls.factories;
    exports io.github.palexdev.materialfx.controls.legacy;
    exports io.github.palexdev.materialfx.effects;
    exports io.github.palexdev.materialfx.effects.ripple;
    exports io.github.palexdev.materialfx.effects.ripple.base;
    exports io.github.palexdev.materialfx.filter;
    exports io.github.palexdev.materialfx.font;
    exports io.github.palexdev.materialfx.notifications;
    exports io.github.palexdev.materialfx.selection;
    exports io.github.palexdev.materialfx.selection.base;
    exports io.github.palexdev.materialfx.skins;
    exports io.github.palexdev.materialfx.skins.legacy;
    exports io.github.palexdev.materialfx.utils;
    exports io.github.palexdev.materialfx.utils.others;
    exports io.github.palexdev.materialfx.validation;
    exports io.github.palexdev.materialfx.validation.base;
}