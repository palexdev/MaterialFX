module MaterialFX.demo.main {
    requires MaterialFX.materialfx.main;

    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.javafx;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens it.paprojects.materialfx.demo.controllers;
    exports it.paprojects.materialfx.demo;
}