import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.RandomUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NotificationsTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane stackPane = new StackPane();

		MFXNotificationCenter notificationCenter = new MFXNotificationCenter();
		notificationCenter.getStylesheets().add(MFXResourcesLoader.load("css/MFXNotificationCenter.css"));
		IntStream.range(0, 100).forEach(i -> notificationCenter.getNotifications().add(createDummyNotification()));
		stackPane.getChildren().add(notificationCenter);

		MFXNotificationCenterSystem.instance()
				.initOwner(primaryStage)
				.setOpenOnNew(false)
				.setCloseAutomatically(true)
				.setPosition(NotificationPos.TOP_LEFT);
		MFXNotificationSystem.instance()
				.initOwner(primaryStage)
				.setPosition(NotificationPos.TOP_RIGHT);
		stackPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case A -> MFXNotificationCenterSystem.instance().publish(createDummyNotification());
				case C -> notificationCenter.stopNotificationsUpdater();
				case S -> notificationCenter.startNotificationsUpdater(60, TimeUnit.SECONDS);
				case T -> MFXNotificationSystem.instance().publish(createDummyNotification());
				case P -> MFXNotificationCenterSystem.instance().delaySetPosition(NotificationPos.TOP_LEFT);
			}
		});

		Scene scene = new Scene(stackPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(notificationCenter.getScene());
	}

	private INotification createDummyNotification() {
		MFXTextField label = MFXTextField.asLabel("Random Label n." + RandomUtils.random.nextInt());
		label.setLeadingIcon(new MFXIconWrapper(MFXFontIcon.getRandomIcon(18, ColorUtils.getRandomColor()), 24));
		label.setAlignment(Pos.CENTER_LEFT);
		label.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(label, Priority.ALWAYS);

		MFXTextField time = MFXTextField.asLabel();
		time.setAlignment(Pos.CENTER_RIGHT);

		HBox box = new HBox(label, time);
		box.setMinSize(450, -1);
		box.setStyle("-fx-background-color: white");
		box.setAlignment(Pos.CENTER_LEFT);
		box.setPadding(InsetsFactory.right(20));
		MFXSimpleNotification notification = new MFXSimpleNotification(box);
		notification.setOnUpdateElapsed((longElapsed, stringElapsed) -> Platform.runLater(() -> time.setText(stringElapsed)));
		time.setText(notification.getTimeToStringConverter().apply(notification.getElapsedTime()));
		return notification;
	}
}
