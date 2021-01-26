package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.TreeItem;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

// TODO implement StringConverter
public abstract class AbstractTreeCell<T> extends HBox {
    protected final ObjectProperty<? super Node> disclosureNode = new SimpleObjectProperty<>();
    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public AbstractTreeCell(AbstractTreeItem<T> item) {
        this(item, 27);
    }

    public AbstractTreeCell(AbstractTreeItem<T> item, double fixedHeight) {
        this.fixedCellSize.set(fixedHeight);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(fixedCellSize);

        initialize(item);
        render(item.getData());
    }

    protected void initialize(AbstractTreeItem<T> item) {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        selected.bind(item.selectedProperty());
        addListeners();
    }

    private void addListeners() {
        selected.addListener(invalidate -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
    }

    protected abstract void defaultDisclosureNode();
    public abstract Node getDisclosureNode();
    public abstract <N extends Node> void setDisclosureNode(N node);

    protected abstract void render(T data);
    public abstract void updateCell(TreeItem<T> item);

    public double getFixedCellSize() {
        return fixedCellSize.get();
    }

    public DoubleProperty fixedCellSizeProperty() {
        return fixedCellSize;
    }

    public void setFixedCellSize(double fixedCellSize) {
        this.fixedCellSize.set(fixedCellSize);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}
