package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.controls.TreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractTreeCell;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class SimpleTreeCell<T> extends AbstractTreeCell<T> {

    public SimpleTreeCell(T data) {
        super(data);
    }

    public SimpleTreeCell(T data, double fixedHeight) {
        super(data, fixedHeight);
    }

    @Override
    protected void initialize() {
        super.initialize();

        defaultDisclosureNode();
        getChildren().add(0, getDisclosureNode());

        disclosureNode.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                getChildren().set(0, (Node) newValue);
            }
        });
    }

    @Override
    protected void defaultDisclosureNode() {
        StackPane disclosureNode = new StackPane();
        disclosureNode.getStyleClass().setAll("disclosure-node");
        disclosureNode.setPrefSize(22, 22);
        NodeUtils.makeRegionCircular(disclosureNode, 9.5);

        RippleGenerator generator = new RippleGenerator(disclosureNode);
        disclosureNode.getChildren().add(0, generator);
        disclosureNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            generator.setGeneratorCenterX(disclosureNode.getWidth() / 2);
            generator.setGeneratorCenterY(disclosureNode.getHeight() / 2);
            generator.createRipple();
        });
        setDisclosureNode(disclosureNode);
    }

    @Override
    public StackPane getDisclosureNode() {
        return (StackPane) disclosureNode.get();
    }

    @Override
    public <N extends Node> void setDisclosureNode(N node) {
        disclosureNode.set(node);
    }

    @Override
    protected void render(T data) {
        if (data instanceof Node) {
            getChildren().add((Node) data);
        } else {
            Label label = new Label(data.toString());
            label.getStyleClass().add("data-label");
            getChildren().add(label);
        }
    }

    @Override
    public void updateCell(TreeItem<T> item) {
        StackPane disclosureNode = getDisclosureNode();
        RippleGenerator generator = (RippleGenerator) disclosureNode.lookup(".ripple-generator");

        if (!item.getItems().isEmpty()) {
            MFXFontIcon icon = new MFXFontIcon("mfx-chevron-right", 12.5);
            icon.getStyleClass().add("disclosure-icon");
            disclosureNode.getChildren().setAll(generator, icon);
        } else {
            getDisclosureNode().getChildren().setAll(generator);
        }

        if (item.isStartExpanded()) {
            disclosureNode.setRotate(90);
        }
    }
}
