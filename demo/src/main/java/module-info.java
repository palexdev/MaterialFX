module MaterialFX.Demo {
    requires MaterialFX;

    requires jdk.localedata;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.scenicview.scenicview;

    opens io.github.palexdev.materialfx.demo;
    opens io.github.palexdev.materialfx.demo.controllers;
}