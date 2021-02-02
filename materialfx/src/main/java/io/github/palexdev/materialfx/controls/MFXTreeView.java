package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import io.github.palexdev.materialfx.controls.base.ISelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;

/**
 * This is the container for a tree made of AbstractMFXTreeItems.
 *
 * @param <T> The type of the data within the items.
 */
public class MFXTreeView<T> extends MFXScrollPane {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-tree-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-treeview.css").toString();

    private final ObjectProperty<AbstractMFXTreeItem<T>> root = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ISelectionModel<T>> selectionModel = new SimpleObjectProperty<>(null);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTreeView(MFXTreeItem<T> root) {
        installSelectionModel();

        root.setTreeView(this);
        setRoot(root);
        setContent(root);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Sets the style class, sets the pref size, adds smooth vertical scroll and
     * binds the root prefWidth to this control width property (minus 10).
     */
    protected void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefSize(250, 500);
        setPadding(new Insets(3));
        MFXScrollPane.smoothVScrolling(this);

        getRoot().prefWidthProperty().bind(widthProperty().subtract(10));
    }

    /**
     * Installs the default selection model to use for the tree.
     * <p>
     * By default it is set to allow multiple selection.
     */
    protected void installSelectionModel() {
        ISelectionModel<T> selectionModel = new SelectionModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    public AbstractMFXTreeItem<T> getRoot() {
        return root.get();
    }

    public void setRoot(AbstractMFXTreeItem<T> root) {
        this.root.set(root);
    }

    public ObjectProperty<AbstractMFXTreeItem<T>> rootProperty() {
        return root;
    }

    public ISelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    public void setSelectionModel(ISelectionModel<T> selectionModel) {
        this.selectionModel.set(selectionModel);
    }

    public ObjectProperty<ISelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
