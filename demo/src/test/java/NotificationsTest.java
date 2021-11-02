import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.RandomInstance;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NotificationsTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane stackPane = new StackPane();

        MFXNotificationCenter notificationCenter = new MFXNotificationCenter();
        IntStream.range(0, 100).forEach(i -> notificationCenter.getNotifications().add(createDummyNotification()));
        stackPane.getChildren().add(notificationCenter);

        MFXNotificationCenterSystem.instance()
                .initOwner(primaryStage)
                .setOpenOnNew(false)
                .setCloseAutomatically(true)
                .setPosition(NotificationPos.TOP_RIGHT);
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

        ScenicView.show(scene);
    }

    private INotification createDummyNotification() {
        MFXLabel label = new MFXLabel("Random Label n." + RandomInstance.random.nextInt());
        label.setLeadingIcon(MFXFontIcon.getRandomIcon(32, ColorUtils.getRandomColor()));
        label.setAlignment(Pos.CENTER_LEFT);
        label.setLineColor(Color.TRANSPARENT);
        label.setUnfocusedLineColor(Color.TRANSPARENT);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        MFXLabel time = new MFXLabel();
        time.setAlignment(Pos.CENTER_RIGHT);
        time.setLineColor(Color.TRANSPARENT);
        time.setUnfocusedLineColor(Color.TRANSPARENT);

        HBox box = new HBox(label, time);
        box.setMinSize(450, 100);
        box.setStyle("-fx-background-color: white");
        box.setAlignment(Pos.CENTER_LEFT);
        MFXSimpleNotification notification = new MFXSimpleNotification(box);
        notification.setOnUpdateElapsed((longElapsed, stringElapsed) -> Platform.runLater(() -> time.setText(stringElapsed)));
        time.setText(notification.getTimeToStringConverter().apply(notification.getElapsedTime()));
        return notification;
    }
}
