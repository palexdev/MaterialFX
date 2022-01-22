import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class BorderWithGap extends Application {

	// Background
	@Override
	public void start(Stage primaryStage) {
		AnchorPane pane = new AnchorPane();

		Label label = new Label("Border with gap");
		AnchorPane.setTopAnchor(label, 50.0);
		AnchorPane.setLeftAnchor(label, 50.0);
		label.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		pane.getChildren().add(label);

		label.setStyle(
				"""
						-fx-background-color: blue, -fx-background, -fx-background;
						-fx-background-insets: 0, 0 10 38 10, 1;
						-fx-background-radius: 5, 0, 3;
						-fx-padding: 10;
						"""
		);

		Scene scene = new Scene(pane, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
