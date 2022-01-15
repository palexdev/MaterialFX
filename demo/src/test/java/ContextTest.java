import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXContextMenu;
import io.github.palexdev.materialfx.controls.MFXContextMenuItem;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ContextTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane bp = new BorderPane();

		MFXButton button = new MFXButton("Show");
		MFXContextMenu menu = new MFXContextMenu(button);
		populateMenu(menu, 5);

		Label labelSeparator = new Label("Separator");
		labelSeparator.setPadding(InsetsFactory.of(5, 3, 5, 0));
		menu.addSeparator(labelSeparator);
		menu.addItem(new MFXContextMenuItem("Separated Item", MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor())));

		menu.addLineSeparator(MFXContextMenu.Builder.getLineSeparator());
		menu.addItem(new MFXContextMenuItem("LSeparated Item", MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor())));

		menu.install();

		bp.setCenter(button);
		Scene scene = new Scene(bp, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void populateMenu(MFXContextMenu menu, int num) {
		MFXContextMenuItem[] items = new MFXContextMenuItem[num];
		for (int i = 0; i < num; i++) {
			MFXContextMenuItem item = new MFXContextMenuItem("Menu Item " + (i + 1), MFXFontIcon.getRandomIcon(12, ColorUtils.getRandomColor()));
			item.setAccelerator("Alt + " + StringUtils.randAlphabetic(1).toUpperCase());
			items[i] = item;
		}
		menu.addItems(items);
	}
}
