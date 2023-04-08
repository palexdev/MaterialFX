import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.css.CSSFragment;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.demo.model.Person;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Reproducer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox box = new VBox(20);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));

        MFXThemeManager.addOn(box, Themes.DEFAULT, Themes.LEGACY);

        MFXTableView<Person> table = new MFXTableView<>(Model.people);
        MFXTableColumn<Person> name = new MFXTableColumn<>("Name");
        name.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getName));
        MFXTableColumn<Person> surname = new MFXTableColumn<>("Surname");
        surname.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getSurname));
        MFXTableColumn<Person> age = new MFXTableColumn<>("Age");
        age.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getAge));
        table.getTableColumns().addAll(name, surname, age);
        box.getChildren().add(table);

        CSSFragment.Builder.build()
                .addSelector(".mfx-filter-pane")
                .addStyle("-mfx-main: blue")
                .closeSelector()
                .applyOn(table);

        MFXComboBox<String> combo = new MFXComboBox<>(Model.strings);
        box.getChildren().add(combo);
        CSSFragment.Builder.build()
                .addSelector(".mfx-combo-box .combo-popup .virtual-flow")
                .addStyle("-fx-background-color: gold")
                .closeSelector()
                .applyOn(combo);

        Scene scene = new Scene(box, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}
