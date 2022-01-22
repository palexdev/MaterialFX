import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Playground extends Application {

	@Override
	public void start(Stage primaryStage) {
		CSSFX.start();
		BorderPane borderPane = new BorderPane();

		MFXPasswordField textField = new MFXPasswordField("", "Prompt", "Floating Text");

		MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode("This should be a long text");
		toggleNode.setLabelLeadingIcon(new MFXFontIcon("mfx-google", 48));
		toggleNode.setLabelTrailingIcon(new MFXFontIcon("mfx-google", 24));

		MFXButton button = new MFXButton("Click Me!");
		button.setOnAction(event -> textField.setShowPassword(!textField.isShowPassword()));
		button.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		BorderPane.setAlignment(button, Pos.TOP_CENTER);
		BorderPane.setMargin(button, InsetsFactory.top(10));

		borderPane.getStylesheets().add(Playground.class.getResource("CustomField.css").toString());
		borderPane.setTop(button);
		borderPane.setCenter(toggleNode);
		Scene scene = new Scene(borderPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setOnShown(event -> button.requestFocus());
		primaryStage.show();
		ScenicView.show(scene);
	}
}
