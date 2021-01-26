package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;
import io.github.palexdev.materialfx.controls.base.ISelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;

public class TreeView<T> extends MFXScrollPane {
    private final String STYLE_CLASS = "mfx-tree-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-treeview.css").toString();

    private final ObjectProperty<AbstractTreeItem<T>> root = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ISelectionModel<T>> selectionModel = new SimpleObjectProperty<>(null);

    public TreeView(TreeItem<T> root) {
        setStyle("-fx-border-color: gold");

        root.setTreeView(this);
        setRoot(root);
        installSelectionModel();

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
        setSelectionModel(new SelectionModel<>());
        getSelectionModel().setAllowsMultipleSelection(true);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    public AbstractTreeItem<T> getRoot() {
        return root.get();
    }

    public ObjectProperty<AbstractTreeItem<T>> rootProperty() {
        return root;
    }

    public void setRoot(AbstractTreeItem<T> root) {
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
