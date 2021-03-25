module MaterialFX.demo.main {
    requires MaterialFX.materialfx.main;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.javafx;

    opens io.github.palexdev.materialfx.demo;
    opens io.github.palexdev.materialfx.demo.controllers;
}