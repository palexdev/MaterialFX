import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Playground extends Application {
	private final double w = 445;
	private final double h = 270;

	@Override
	public void start(Stage primaryStage) {
		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);

		MFXTextField textField = new MFXTextField("15.0", "", "Pixels");

		MFXButton button = new MFXButton("Change Measure Unit");
		button.setOnAction(event -> {
			String measureUnit = textField.getMeasureUnit();
			measureUnit = (measureUnit == null || measureUnit.isEmpty()) ? "px" : "cm";
			textField.setMeasureUnit(measureUnit);
		});

		vBox.getChildren().addAll(button, textField);
		Scene scene = new Scene(vBox, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
