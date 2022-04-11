import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class Playground extends Application {

	@Override
	public void start(Stage primaryStage) {
		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);

		DecimalFormat format = new DecimalFormat("#.0");
		MFXTextField field = new MFXTextField("", "", "Numbers");
		field.delegateSetTextFormatter(new TextFormatter<>(c ->
		{
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			ParsePosition parsePosition = new ParsePosition(0);
			Object object = format.parse(c.getControlNewText(), parsePosition);

			if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
				return null;
			} else {
				return c;
			}
		}));

		vBox.getChildren().addAll(field);
		Scene scene = new Scene(vBox, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
