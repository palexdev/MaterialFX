module MaterialFX.materialfx.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires org.apache.logging.log4j;

    exports io.github.palexdev.materialfx;
    exports io.github.palexdev.materialfx.beans;
    exports io.github.palexdev.materialfx.beans.binding;
    exports io.github.palexdev.materialfx.collections;
    exports io.github.palexdev.materialfx.controls;
    exports io.github.palexdev.materialfx.controls.base;
    exports io.github.palexdev.materialfx.controls.enums;
    exports io.github.palexdev.materialfx.controls.factories;
    exports io.github.palexdev.materialfx.effects;
    exports io.github.palexdev.materialfx.notifications;
    exports io.github.palexdev.materialfx.skins;
    exports io.github.palexdev.materialfx.utils;
    exports io.github.palexdev.materialfx.validation;
    exports io.github.palexdev.materialfx.validation.base;
}