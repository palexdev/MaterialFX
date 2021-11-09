import io.github.palexdev.materialfx.controls.MFXRadioButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Playground extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();

        MFXRadioButton radioButton = new MFXRadioButton("Radio Button");
        stackPane.getChildren().add(radioButton);

        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }
}
