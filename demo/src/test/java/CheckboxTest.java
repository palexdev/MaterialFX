import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class CheckboxTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();

        MFXCheckbox checkbox = new MFXCheckbox("Checkbox");
        checkbox.setFont(Font.font("Roboto Medium", 14));
        stackPane.getChildren().add(checkbox);

        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }
}
