package app.popups;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcomponents.window.popups.MFXPopup;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.utils.PositionUtils;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class PopupTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        MFXButton button = MFXButton.filled();
        button.setText("Show Popup");

        ComboBox<Pos> positions = new ComboBox<>(FXCollections.observableArrayList(Pos.values()));
        positions.getItems().removeAll(Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT);
        positions.getSelectionModel().selectFirst();
        positions.setEditable(false);
        StackPane.setAlignment(positions, Pos.TOP_CENTER);
        StackPane.setMargin(positions, InsetsBuilder.top(80));

/*        MFXButton button2 = MFXButton.filled();
        button2.setText("Show Popup2");
        StackPane.setMargin(button2, InsetsBuilder.left(400));

        MFXTooltip popup = new MFXTooltip(button);
        popup.setContent(new SimpleContent());
        popup.install();

        MFXTooltip popup2 = new MFXTooltip(button2);
        popup2.setContent(new SimpleContent("2nd Tooltip"));
        popup2.install();*/

        MFXPopup popup = new MFXPopup();
        popup.setContent(new SimpleContent());
        button.setOnAction(e -> popup.show(button, positions.getValue()));

        Runnable updateOffset = () -> {
            Pos pos = positions.getValue();
            if (pos == null || pos == Pos.CENTER) {
                popup.setOffset(Position.origin());
                return;
            }

            double x = 0;
            double y = 0;
            if (PositionUtils.isLeft(pos) || PositionUtils.isRight(pos)) x = 8;
            if (PositionUtils.isTop(pos) || PositionUtils.isBottom(pos)) y = 8;
            popup.setOffset(Position.of(x, y));
        };
        positions.valueProperty().addListener(i -> updateOffset.run());
        updateOffset.run();

        Label screen = new Label();
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Point2D toScreen = button.localToScreen(0, 0);
                screen.setText(Position.of(toScreen.getX(), toScreen.getY()).toString());
            }
        });
        StackPane.setAlignment(screen, Pos.TOP_CENTER);
        StackPane.setMargin(screen, InsetsBuilder.top(12));

        Label content = new Label();
        content.textProperty().bind(popup.contentBoundsProperty()
            .map(b -> Size.of(b.getWidth(), b.getHeight()).toString()));
        StackPane.setAlignment(content, Pos.TOP_CENTER);
        StackPane.setMargin(content, InsetsBuilder.top(42));

        CSSFragment.Builder.build()
            .addSelector(".mfx-popup .content")
            .addSelector(".mfx-tooltip .content")
            .addStyle("-fx-background-color: rgba(0, 0, 0, 0.2)")
            .addStyle("-fx-background-radius: 12px")
            .addStyle("-fx-padding: 12px")
            //.addStyle("-mfx-offset: \"0 8\"")
            .closeSelector()
            .applyOn(pane);

        pane.getChildren().addAll(positions, button, screen, content);
        Scene scene = new Scene(pane, 600, 600);
        MFXThemeManager.PURPLE_LIGHT.addOn(scene);
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }

    private static class SimpleContent extends StackPane {
        public SimpleContent() {
            Label l = new Label("This is a simple popup with just a Label!");
            getChildren().add(l);
        }

        public SimpleContent(String text) {
            Label l = new Label(text);
            getChildren().add(l);
        }
    }
}
