import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.demo.model.Model;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.theming.CSSFragment;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Reproducer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StackPane pane = new StackPane();
        pane.setPadding(InsetsBuilder.all(10));

        MFXTableView<Person> table = new MFXTableView<>(Model.people);
        MFXTableColumn<Person> name = new MFXTableColumn<>("Name");
        name.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getName));
        MFXTableColumn<Person> surname = new MFXTableColumn<>("Surname");
        surname.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getSurname));
        MFXTableColumn<Person> age = new MFXTableColumn<>("Age");
        age.setRowCellFactory(p -> new MFXTableRowCell<>(Person::getAge));
        table.getTableColumns().addAll(name, surname, age);
        pane.getChildren().add(table);
        table.setMinSize(400.0, 400.0);

        table.getFilters().addAll(
            new StringFilter<>("Name", Person::getName),
            new StringFilter<>("Surname", Person::getSurname),
            new IntegerFilter<>("Age", Person::getAge)
        );

        CSSFragment.Builder.build()
                .addSelector(".mfx-filter-pane")
                .addStyle("-mfx-main: blue")
                .closeSelector()
                .applyOn(table);

        UserAgentBuilder.builder()
            .themes(JavaFXThemes.MODENA)
            .themes(MaterialFXStylesheets.forAssemble(false))
            .setResolveAssets(true)
            .setDeploy(true)
            .build()
            .setGlobal();
        Scene scene = new Scene(pane, 600, 600);
        stage.setScene(scene);
        stage.show();
    }
}
