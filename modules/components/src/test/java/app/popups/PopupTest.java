package app.popups;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXTextButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcomponents.window.MFXRichContent;
import io.github.palexdev.mfxcomponents.window.popups.MFXTooltip;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PopupTest extends Application {
    private final String lorem = "Lorem ipsum dolor sit amet. " +
        "Aut nulla inventore in tempore suscipit ab possimus soluta id rerum maiores. " +
        "Vel voluptatem unde ea reprehenderit labore ut minima ratione. " +
        "Ut quos quam in tempora quibusdam id iste accusamus aut dolores facere et Quis dolore.";

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        ComboBox<Pos> anchors = new ComboBox<>(FXCollections.observableArrayList(Pos.values()));
        anchors.getItems().removeAll(Pos.BASELINE_CENTER, Pos.BASELINE_LEFT, Pos.BASELINE_RIGHT);
        anchors.getSelectionModel().select(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(anchors, Pos.TOP_CENTER);
        StackPane.setMargin(anchors, InsetsBuilder.top(20));

        MFXButton button = new MFXFilledButton("Show Popup");

        MFXTooltip tooltip = new MFXTooltip(button);
        tooltip.setContent(new MFXRichContent());
        tooltip.setHeader("A tooltip duh!");
        tooltip.setText(lorem);
        tooltip.setPrimaryAction(() -> {
            MFXButton primary = new MFXTextButton("Learn More");
            primary.setOnAction(e -> tooltip.setText("I already feel smarter"));
            return primary;
        });
        tooltip.setSecondaryAction(() -> {
            MFXButton secondary = new MFXTextButton("Cancel");
            secondary.setOnAction(e -> tooltip.setText(lorem));
            return secondary;
        });
        tooltip.install(button);
        anchors.valueProperty().addListener((ob, o, n) -> tooltip.setAnchor(n));

        pane.getChildren().addAll(button, anchors);
        Scene scene = new Scene(pane, 600, 600);
        MFXThemeManager.PURPLE_LIGHT.addOn(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
