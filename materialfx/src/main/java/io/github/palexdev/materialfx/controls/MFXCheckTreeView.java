package io.github.palexdev.materialfx.controls;

/**
 * This is the container for a tree made of MFXCheckTreeItems.
 * <p>
 * Note: this could also work with other item classes since the CheckModel extends SelectionModel,
 * but of course it is not recommended to do so.
 * @param <T> The type of the data within the items.
 */
public class MFXCheckTreeView<T> extends MFXTreeView<T> {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckTreeView() {
        super();
    }

    public MFXCheckTreeView(MFXTreeItem<T> root) {
        super(root);
    }

    //================================================================================
    // Methods
    //================================================================================
    public CheckModel<T> getCheckModel() {
        return (CheckModel<T>) super.getSelectionModel();
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * Overridden method to install a CheckModel.
     * <p>
     * By default it is set to allow multiple selection.
     */
    @Override
    protected void installSelectionModel() {
        CheckModel<T> checkModel = new CheckModel<>();
        checkModel.setAllowsMultipleSelection(true);
        setSelectionModel(checkModel);
    }
}
