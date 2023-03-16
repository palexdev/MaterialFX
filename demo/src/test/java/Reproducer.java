import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.stream.IntStream;

public class Reproducer extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		VBox box = new VBox(20);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(new Insets(20));

		MFXThemeManager.addOn(box, Themes.DEFAULT, Themes.LEGACY);

		ObservableList<String> strings = IntStream.range(0, 50)
				.mapToObj(i -> "String " + (i + 1))
				.collect(FXCollectors.toList());
		MFXLegacyComboBox<String> combo = new MFXLegacyComboBox<>(strings);
		box.getChildren().add(combo);

		Scene scene = new Scene(box, 400, 400);
		stage.setScene(scene);
		stage.show();
	}
}
