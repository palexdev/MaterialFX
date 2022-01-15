package io.github.palexdev.materialfx.demo;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.demo.controllers.DemoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.scenicview.ScenicView;

public class Demo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		CSSFX.start();

		FXMLLoader loader = new FXMLLoader(MFXDemoResourcesLoader.loadURL("fxml/Demo.fxml"));
		loader.setControllerFactory(c -> new DemoController(primaryStage));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(scene);
		primaryStage.setTitle("MaterialFX Demo");
		primaryStage.show();

		ScenicView.show(scene);
	}
}
