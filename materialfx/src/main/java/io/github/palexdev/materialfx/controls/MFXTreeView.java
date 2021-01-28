package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.controls.base.ISelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;

public class MFXTreeView<T> extends MFXScrollPane {
    private final String STYLE_CLASS = "mfx-tree-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-treeview.css").toString();

    private final ObjectProperty<AbstractMFXTreeItem<T>> root = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ISelectionModel<T>> selectionModel = new SimpleObjectProperty<>(null);

    public MFXTreeView(MFXTreeItem<T> root) {
        setStyle("-fx-border-color: gold");
        installSelectionModel();

        root.setTreeView(this);
        setRoot(root);
        setContent(root);
        initialize();
    }

    protected void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE); // TODO remove
        setPrefSize(250, 500);
        setPadding(new Insets(3));
        MFXScrollPane.smoothVScrolling(this);

        getRoot().prefWidthProperty().bind(widthProperty().subtract(10));
    }

    protected void installSelectionModel() {
        ISelectionModel<T> selectionModel = new SelectionModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    public AbstractMFXTreeItem<T> getRoot() {
        return root.get();
    }

    public ObjectProperty<AbstractMFXTreeItem<T>> rootProperty() {
        return root;
    }

    public void setRoot(AbstractMFXTreeItem<T> root) {
        this.root.set(root);
    }

    public ISelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    public ObjectProperty<ISelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public void setSelectionModel(ISelectionModel<T> selectionModel) {
        this.selectionModel.set(selectionModel);
    }

}
