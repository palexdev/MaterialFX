package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.TreeItem;
import io.github.palexdev.materialfx.controls.base.AbstractTreeCell;
import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.github.palexdev.materialfx.controls.TreeItem.TreeItemEvent;

public class TreeItemSkin<T> extends SkinBase<TreeItem<T>> {
    private final VBox box;
    private final AbstractTreeCell<T> cell;
    private final ListChangeListener<AbstractTreeItem<T>> itemsListener;

    private final Interpolator interpolator;
    private Timeline animation;

    private boolean forcedUpdate = false;

    @SuppressWarnings("SuspiciousMethodCalls")
    public TreeItemSkin(TreeItem<T> item) {
        super(item);

        cell = createCell();
        box = new VBox(cell);
        box.setMinHeight(Region.USE_PREF_SIZE);
        box.setMaxHeight(Region.USE_PREF_SIZE);

        item.setInitialHeight(NodeUtils.getNodeHeight(box));
        getChildren().add(box);
        box.setPrefHeight(item.getInitialHeight());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(box.widthProperty());
        clip.heightProperty().bind(box.heightProperty());
        box.setClip(clip);

        interpolator = Interpolator.SPLINE(0.0825D, 0.3025D, 0.0875D, 0.9975D);

        itemsListener = change -> {
            List<AbstractTreeItem<T>> tmpRemoved = new ArrayList<>();
            List<AbstractTreeItem<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }

            box.getChildren().removeAll(tmpRemoved);
            if (!tmpAdded.isEmpty() && item.isExpanded()) {
                box.getChildren().addAll(tmpAdded);
                FXCollections.sort(box.getChildren(), Comparator.comparingInt(item.getItems()::indexOf));
            }
            //cell.updateCell(item);
        };
        addListeners();

        if (item.isStartExpanded() && !item.getItems().isEmpty()) {
            forcedUpdate = true;
            box.getChildren().addAll(item.getItems());
            box.applyCss();
            box.layout();
            box.setPrefHeight(item.getInitialHeight() + computeExpandCollapse());
            cell.updateCell(item);
            item.setExpanded(true);
            forcedUpdate = false;
        }
    }

    protected void checkStartExpanded() {
        TreeItem<T> item = getSkinnable();


    }

    private void addListeners() {
        TreeItem<T> item = getSkinnable();

        item.getItems().addListener(itemsListener);
        item.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!forcedUpdate) {
                updateDisplay();
            }
        });

        item.addEventHandler(TreeItemEvent.EXPAND_EVENT, expandEvent -> {
            buildAnimation((item.getHeight() + expandEvent.getValue()));
            animation.setOnFinished(event -> System.out.println("Final:" + item.getHeight()));
            animation.play();

            if (item.getItemParent() == null) {
                expandEvent.consume();
            }
        });

        item.addEventHandler(TreeItemEvent.COLLAPSE_EVENT, collapseEvent -> {
            buildAnimation((item.getHeight() - collapseEvent.getValue()));
            if (collapseEvent.getItem() == item) {
                animation.setOnFinished(event -> box.getChildren().subList(1, box.getChildren().size()).clear());
            }
            animation.play();
        });

        //================================================================================
        // DEBUG
        //================================================================================
        //debugListeners(); // TODO remove
    }

    protected void updateDisplay() {
        TreeItem<T> item = getSkinnable();

        if (item.isExpanded()) {
            box.getChildren().addAll(item.getItems());
            box.applyCss();
            box.layout();
            item.fireEvent(new TreeItemEvent<>(TreeItemEvent.EXPAND_EVENT, item, computeExpandCollapse()));
        } else {
            item.fireEvent(new TreeItemEvent<>(TreeItemEvent.COLLAPSE_EVENT, item, computeExpandCollapse()));
        }
    }

    protected void buildAnimation(double fHeight) {
        TreeItem<T> item = getSkinnable();

        KeyValue expCollValue = new KeyValue(box.prefHeightProperty(), fHeight, interpolator);
        KeyFrame expCollFrame = new KeyFrame(Duration.millis(item.getAnimationDuration()), expCollValue);
        KeyValue disclosureValue = new KeyValue(cell.getDisclosureNode().rotateProperty(), (item.isExpanded() ? 90 : 0), interpolator);
        KeyFrame disclosureFrame = new KeyFrame(Duration.millis(250), disclosureValue);
        animation = new Timeline(expCollFrame, disclosureFrame);

        item.animationRunningProperty().bind(animation.statusProperty().isEqualTo(Animation.Status.RUNNING));
    }

    private boolean animationIsRunning() {
        TreeItem<T> item = getSkinnable();
        List<TreeItem<T>> tmp = new ArrayList<>();
        while (item != null) {
            tmp.add(item);
            item = (TreeItem<T>) item.getItemParent();
        }

        for (TreeItem<T> i : tmp) {
            if (i != null && i.isAnimationRunning()) {
                return true;
            }
        }
        return false;
    }

    protected double computeExpandCollapse() {
        TreeItem<T> item = getSkinnable();
        double value = item.getItems().stream().mapToDouble(AbstractTreeItem::getHeight).sum();

        if (item.isRoot() && !forcedUpdate && !item.isExpanded()) {
            value = item.getHeight() - item.getInitialHeight();
        }
        return value;
    }

    protected AbstractTreeCell<T> createCell() {
        TreeItem<T> item = getSkinnable();

        AbstractTreeCell<T> cell = item.getCellFactory().call(item);
        Node disclosureNode = cell.getDisclosureNode();
        disclosureNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (animationIsRunning()) {
                return;
            }

            item.setExpanded(!item.isExpanded());
        });
        cell.updateCell(item);

        return cell;
    }

    //================================================================================
    // DEBUG
    //================================================================================
    private void debugListeners() {
        TreeItem<T> item = getSkinnable();
        item.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            System.out.println("\n-------------------------------------------");
            System.out.println("DATA:" + item.getData());
            System.out.println("CELL:" + cell.getHeight());
            System.out.println("ITEM:" + item.getHeight() + ", " + snapSizeY(item.getHeight()));
            System.out.println("BOX:" + box.getHeight());
            System.out.println("-------------------------------------------\n");
        });
    }
}

