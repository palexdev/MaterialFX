package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.controls.base.AbstractTreeItem;
import io.github.palexdev.materialfx.controls.cell.SimpleTreeCell;
import io.github.palexdev.materialfx.skins.TreeItemSkin;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.css.*;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TreeItem<T> extends AbstractTreeItem<T> {
    private static final StyleablePropertyFactory<TreeItem<?>> FACTORY = new StyleablePropertyFactory<>(TreeItem.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-tree-item";

    private final BooleanProperty expanded = new SimpleBooleanProperty(false);
    private final ReadOnlyBooleanWrapper animationRunning = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyDoubleWrapper initialHeight = new ReadOnlyDoubleWrapper(0);

    public TreeItem(T data) {
        super(data);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        items.addListener((ListChangeListener<? super AbstractTreeItem<T>>) change -> {
            List<AbstractTreeItem<T>> tmpRemoved = new ArrayList<>();
            List<AbstractTreeItem<T>> tmpAdded = new ArrayList<>();

            while (change.next()) {
                tmpRemoved.addAll(change.getRemoved());
                tmpAdded.addAll(change.getAddedSubList());
            }

            updateChildrenParent(tmpRemoved, null);
            updateChildrenParent(tmpAdded, this);
        });

        defaultCellFactory();
    }

    @Override
    protected void updateChildrenParent(List<? extends AbstractTreeItem<T>> treeItems, final AbstractTreeItem<T> newParent) {
        treeItems.forEach(item -> ((TreeItem<T>) item).setItemParent(newParent));
    }

    private final StyleableDoubleProperty animationDuration = new SimpleStyleableDoubleProperty(
            StyleableProperties.DURATION,
            this,
            "animationDuration",
            300.0
    );

    public double getAnimationDuration() {
        return animationDuration.get();
    }

    public StyleableDoubleProperty animationDurationProperty() {
        return animationDuration;
    }

    public void setAnimationDuration(double animationDuration) {
        this.animationDuration.set(animationDuration);
    }

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<TreeItem<?>, Number> DURATION =
                FACTORY.createSizeCssMetaData(
                        "-mfx-animation-duration",
                        TreeItem::animationDurationProperty,
                        300.0
                );

        static {
            cssMetaDataList = List.of(DURATION);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected void defaultCellFactory() {
        super.cellFactory.set(item -> new SimpleTreeCell<>(item.getData()));
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TreeItemSkin<>(this);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return TreeItem.getControlCssMetaDataList();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        items.forEach(
                item -> item.setPadding(new Insets(0, 0, 0, 20))
        );
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        EventDispatchChain chain = super.buildEventDispatchChain(tail);

        AbstractTreeItem<T> item = getItemParent();
        while (item != null) {
            chain.prepend(item.getEventDispatcher());
            item = item.getItemParent();
        }

        return chain;
    }

    @Override
    public String toString() {
        String className = getClass().getName();
        String simpleName = className.substring(className.lastIndexOf('.')+1);
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(simpleName);
        sb.append('@');
        sb.append(Integer.toHexString(hashCode()));
        sb.append("]");
        sb.append("[Data:").append(getData()).append("]");
        if (getId() != null) {
            sb.append("[id:").append(getId()).append("]");
        }

        return sb.toString();
    }

    public boolean isExpanded() {
        return expanded.get();
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
    }

    public double getInitialHeight() {
        return initialHeight.get();
    }

    public ReadOnlyDoubleProperty initialHeightProperty() {
        return initialHeight;
    }

    public void setInitialHeight(double height) {
        if (initialHeight.get() !=  0) {
            throw new RuntimeException("Initial Height Property is intended for internal use only.");
        }
        initialHeight.set(height);
    }

    public boolean isAnimationRunning() {
        return animationRunning.get();
    }

    public ReadOnlyBooleanWrapper animationRunningProperty() {
        return animationRunning;
    }

    public static class TreeItemEvent<T> extends Event {
        private final WeakReference<AbstractTreeItem<T>> itemRef;
        private final double value;

        public static final EventType<TreeItemEvent<?>> FORCE_UPDATE = new EventType<>(ANY, "FORCE_UPDATE");
        public static final EventType<TreeItemEvent<?>> EXPAND_EVENT = new EventType<>(ANY, "EXPAND_EVENT");
        public static final EventType<TreeItemEvent<?>> COLLAPSE_EVENT = new EventType<>(ANY, "COLLAPSE_EVENT");

        public TreeItemEvent(EventType<? extends Event> eventType, AbstractTreeItem<T> item, double value) {
            super(eventType);
            this.itemRef = new WeakReference<>(item);
            this.value = value;
        }

        public AbstractTreeItem<T> getItem() {
            return itemRef.get();
        }

        public double getValue() {
            return value;
        }
    }
}
