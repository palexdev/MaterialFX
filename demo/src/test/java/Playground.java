import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.models.spinner.ListSpinnerModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.List;

public class Playground extends Application {

	@Override
	public void start(Stage primaryStage) {
		CSSFX.start();
		BorderPane borderPane = new BorderPane();

		ObservableList<String> strings = FXCollections.observableArrayList(
				"String 1",
				"String 2",
				"String 3",
				"String 4",
				"String 5",
				"String 6",
				"String 7",
				"String 8"
		);

		MFXSpinner<String> spinner = new MFXSpinner<>();
		spinner.getStylesheets().add(MFXResourcesLoader.load("css/MFXSpinner.css"));
		spinner.setSpinnerModel(new ListSpinnerModel<>());
		spinner.getSpinnerModel().setWrapAround(true);
		((ListSpinnerModel<String>) spinner.getSpinnerModel()).setItems(strings);
		spinner.setTextTransformer((focused, text) -> ((!focused || !spinner.isEditable()) && !text.isEmpty()) ? text + " cm" : text);

		MFXButton add = new MFXButton("Add");
		add.setOnAction(event -> ((ListSpinnerModel<String>) spinner.getSpinnerModel()).getItems().addAll(2, List.of("String Added 1", "String Added 2")));
		MFXButton remove = new MFXButton("Remove");
		remove.setOnAction(event -> ((ListSpinnerModel<String>) spinner.getSpinnerModel()).getItems().clear());
		MFXButton removeSel = new MFXButton("Remove Selected");
		removeSel.setOnAction(event -> ((ListSpinnerModel<String>) spinner.getSpinnerModel()).getItems().remove(((ListSpinnerModel<String>) spinner.getSpinnerModel()).getCurrentIndex()));
		MFXButton replace = new MFXButton("Replace");
		replace.setOnAction(event -> ((ListSpinnerModel<String>) spinner.getSpinnerModel()).getItems().set(((ListSpinnerModel<String>) spinner.getSpinnerModel()).getCurrentIndex(), "Replaced"));
		MFXButton change = new MFXButton("Change List");
		change.setOnAction(event -> {
			ListSpinnerModel<String> model = (ListSpinnerModel<String>) spinner.getSpinnerModel();
			model.setItems(FXCollections.observableArrayList(
					"String 9",
					"String 10",
					"String 11",
					"String 12",
					"String 1234567890"
			));
		});
		HBox box = new HBox(15, add, remove, removeSel, replace, change);
		box.setAlignment(Pos.CENTER);

		borderPane.setCenter(spinner);
		borderPane.setBottom(box);
		Scene scene = new Scene(borderPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
		ScenicView.show(scene);
	}
}
