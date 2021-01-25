package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.TreeItem;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

// TODO implement StringConverter
public abstract class AbstractTreeCell<T> extends HBox {
    protected final ObjectProperty<? super Node> disclosureNode = new SimpleObjectProperty<>();
    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty();

    public AbstractTreeCell(T data) {
        this(data, 27);
    }

    public AbstractTreeCell(T data, double fixedHeight) {
        this.fixedCellSize.set(fixedHeight);

        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(fixedCellSize);

        initialize();
        render(data);
    }

    protected void initialize() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
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
}
