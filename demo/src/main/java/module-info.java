module MaterialFX.demo.main {
    requires MaterialFX.materialfx.main;

    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.fxml;
    requires javafx.graphics;

    opens io.github.palexdev.materialfx.demo.controllers;
    exports io.github.palexdev.materialfx.demo;
}