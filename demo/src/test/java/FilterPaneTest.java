import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.BoundTextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.demo.model.Device;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class FilterPaneTest extends Application {
	private String text =
			"""
					Lorem Ipsum is simply dummy text of the printing and typesetting industry.
					Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
					when an unknown printer took a galley of type and scrambled it to make a type specimen book.
					It has survived not only five centuries, but also the leap into electronic typesetting,
					remaining essentially unchanged.
					It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
					and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
					""";
	private MFXStageDialog dialog;

	@Override
	public void start(Stage primaryStage) throws Exception {
		CSSFX.start();

		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(InsetsFactory.all(20));

		dialog = MFXDialogs.info()
				.setHeaderText("Info Dialog")
				//.setContentText("This is a simple test for the new MaterialFX dialogs!")
				.setContentText(text)
				.addActions(new MFXButton("Cancel"))
				.addActions(new MFXButton("Ok"))
				.makeScrollable(true)
				.addStylesheets(MFXResourcesLoader.load("css/MFXDialogs.css"))
				.toStageDialogBuilder()
				.setTitle("MaterialFX - Dialogs Test")
				.initOwner(primaryStage)
				.setOwnerNode(borderPane)
				.setScrimOwner(true)
				.setDraggable(true)
				.setOverlayClose(true)
				.get();

		MFXFilterPane<Device> filterPane = new MFXFilterPane<>();
		filterPane.getStylesheets().add(MFXResourcesLoader.load("css/MFXFilterPane.css"));
		filterPane.getFilters().add(new EnumFilter<>("State", Device::getState, Device.State.class));
		filterPane.getFilters().add(new IntegerFilter<>("Machine ID", Device::getID));

		MFXButton button = new MFXButton("Show Dialog");
		button.setOnAction(event -> {
			//dialog.showDialog();
			MFXStageDialog dialog = MFXDialogs.filter(filterPane)
					.makeScrollable(false)
					.addStylesheets(MFXResourcesLoader.load("css/MFXDialogs.css"))
					.toStageDialogBuilder()
					.setTitle("MFXFilterDialog - Test")
					.setAlwaysOnTop(true)
					.setDraggable(true)
					.get();
			dialog.showDialog();
			ScenicView.show(dialog.getScene());
		});

		MFXTextField textField = new MFXTextField("", "", "Float");
		NodeUtils.waitForSkin(textField, () -> {
			BoundTextField node = (BoundTextField) textField.lookup(".text-field");
			NodeUtils.waitForSkin(node, () -> {
				Text text = (Text) node.lookup(".text");
				text.wrappingWidthProperty().bind(textField.widthProperty());
			}, false, true);
		}, false, true);

		borderPane.setTop(button);
		//borderPane.setCenter(filterPane);
		borderPane.setBottom(textField);
		Scene scene = new Scene(borderPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
