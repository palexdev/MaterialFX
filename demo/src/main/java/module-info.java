module MaterialFX.demo.main {
    requires MaterialFX.materialfx.main;

    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;

    opens io.github.palexdev.materialfx.demo;
    opens io.github.palexdev.materialfx.demo.controllers;
}