package io.github.palexdev.materialfx.controls.tableview;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.cell.tableview.MFXTableRow;
import io.github.palexdev.materialfx.skins.tableview.MFXTableViewSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class MFXTableView<S> extends TableView<S> {
    private final String STYLE_CLASS = "mfx-table-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/tableview/mfx-tableview.css").toString();

    public MFXTableView() {
        initialize();
    }

    public MFXTableView(ObservableList<S> items) {
        super(items);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setRowFactory(row -> new MFXTableRow<>());
        setFixedCellSize(27);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
