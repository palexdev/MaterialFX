package combobox;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.stream.IntStream;

public class ComboBoxTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		HBox hBox = new HBox(50);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.setPadding(new Insets(20, 0, 0, 0));

		ObservableList<String> s1 = FXCollections.observableArrayList();
		ObservableList<String> s2 = FXCollections.observableArrayList();
		ObservableList<String> s3 = FXCollections.observableArrayList();

		IntStream.rangeClosed(0, 10).forEach(i -> s1.add("String " + i));
		IntStream.rangeClosed(30, 40).forEach(i -> s2.add("String " + i));
		IntStream.rangeClosed(55, 65).forEach(i -> s3.add("String " + i));

		MFXFilterComboBox<String> c1 = new MFXFilterComboBox<>(s1);
		MFXFilterComboBox<String> c2 = new MFXFilterComboBox<>(s2);
		MFXFilterComboBox<String> c3 = new MFXFilterComboBox<>(s3);

		c2.valueProperty().bind(c1.valueProperty());
		c3.getSelectionModel().bindIndexBidirectional(c1.getSelectionModel());

		hBox.getChildren().addAll(c1, c2, c3);
		Scene scene = new Scene(hBox, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
