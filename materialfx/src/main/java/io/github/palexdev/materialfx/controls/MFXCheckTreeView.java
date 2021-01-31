package io.github.palexdev.materialfx.controls;

public class MFXCheckTreeView<T> extends MFXTreeView<T> {

    public MFXCheckTreeView(MFXTreeItem<T> root) {
        super(root);
    }

    @Override
    protected void installSelectionModel() {
        CheckModel<T> checkModel = new CheckModel<>();
        checkModel.setAllowsMultipleSelection(true);
        setSelectionModel(checkModel);
    }

    public CheckModel<T> getCheckModel() {
        return (CheckModel<T>) super.getSelectionModel();
    }
}
