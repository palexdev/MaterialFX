import io.github.palexdev.materialfx.controls.MFXTitledPane;
import io.github.palexdev.materialfx.enums.HeaderPosition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Playground extends Application {
	private final double w = 445;
	private final double h = 270;

	@Override
	public void start(Stage primaryStage) {
		BorderPane bp = new BorderPane();

		MFXTitledPane tp = new MFXTitledPane("SideBar", new Rectangle(200, 1200));
		tp.setHeaderPos(HeaderPosition.LEFT);
		bp.setRight(tp);

		Scene scene = new Scene(bp, 1440, 900);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
