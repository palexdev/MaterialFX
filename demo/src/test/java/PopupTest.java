import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.RandomInstance;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PopupTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane stackPane = new StackPane();

        MFXButton button = new MFXButton("SHOW");
        button.setPrefSize(180, 36);
        button.setButtonType(ButtonType.RAISED);
        button.setDepthLevel(DepthLevel.LEVEL1);

        button.setOnAction(event -> {
                Region content = createDummyNotification().getContent();
                MFXPopup popup = new MFXPopup(content);
                popup.show(button, HPos.LEFT, VPos.BOTTOM, -0, -0);
        });

        stackPane.getChildren().add(button);
        Scene scene = new Scene(stackPane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
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
        box.setStyle("" +
                "-fx-background-color: transparent;\n" +
                "-fx-border-color: red");
        return notification;
    }
}
