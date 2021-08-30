/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;

/**
 * This control is a context menu built from scratch which extends {@code VBox}.
 * <p>
 * It easily styleable and allows to add separators between the context menu nodes.
 * The context menu is shown in a {@code PopupControl}.
 * <p>
 * It also allows to easily change owner and menu items even at runtime.
 * <p></p>
 * It is <b>highly recommended</b> to use the {@link MFXContextMenu.Builder} class to create a context menu.
 */
public class MFXContextMenu extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-context-menu";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXContextMenu.css");

    private final ObjectProperty<ObservableList<Node>> items = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Node> owner = new SimpleObjectProperty<>();
    private final PopupControl popup;
    private EventHandler<MouseEvent> openHandler;
    private ChangeListener<Boolean> ownerFocus;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXContextMenu(Node owner) {
        this(0, owner);
    }

    public MFXContextMenu(double spacing, Node owner) {
        super(spacing);

        popup = new PopupControl();
        popup.getScene().setRoot(this);
        popup.getScene().setFill(Color.TRANSPARENT);
        popup.setAutoHide(true);
        popup.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> hide());

        openHandler = event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                show(event);
            } else {
                hide();
            }
        };
        ownerFocus = (observable, oldValue, newValue) -> {
            if (!newValue && isShowing()) {
                hide();
            }
        };

        initialize();
        setOwner(owner);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setStyle("-fx-background-color: white");
        setMinWidth(100);
        setAlignment(Pos.TOP_CENTER);


        items.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.isEmpty()) {
                oldValue.clear();
            }
            if (newValue != null) {
                super.getChildren().setAll(newValue);
            }
        });

        owner.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, openHandler);
                oldValue.focusedProperty().removeListener(ownerFocus);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, openHandler);
                newValue.focusedProperty().addListener(ownerFocus);
            }
        });
    }

    /**
     * Shows the context menu' popup.
     */
    public void show(MouseEvent event) {
        popup.show(getOwner(), event.getScreenX(), event.getScreenY());
    }

    /**
     * Hides the context menu' popup.
     */
    public void hide() {
        popup.hide();
    }

    /**
     * Removes the popup handler from the current set owner (if not null) and then
     * sets it to null.
     */
    public void dispose() {
        if (getOwner() != null) {
            getOwner().removeEventFilter(MouseEvent.MOUSE_PRESSED, openHandler);
            getOwner().focusedProperty().removeListener(ownerFocus);
        }
        openHandler = null;
        ownerFocus = null;
    }

    /**
     * @return the item's list of this context menu, separators included
     */
    public ObservableList<Node> getItems() {
        return items.get();
    }

    /**
     * Sets the item's list of this context menu with the given list.
     */
    public void setItems(ObservableList<Node> items) {
        this.items.set(items);
    }

    public Node getOwner() {
        return owner.get();
    }

    /**
     * Specifies the popup's owner. This is needed to invoke {@link PopupControl#show(Node, double, double)}.
     */
    public ObjectProperty<Node> ownerProperty() {
        return owner;
    }

    public void setOwner(Node owner) {
        this.owner.set(owner);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public void setOnCloseRequest(EventHandler<WindowEvent> value) {
        popup.setOnCloseRequest(value);
    }

    public void setOnShowing(EventHandler<WindowEvent> value) {
        popup.setOnShowing(value);
    }

    public void setOnShown(EventHandler<WindowEvent> value) {
        popup.setOnShown(value);
    }

    public void setOnHiding(EventHandler<WindowEvent> value) {
        popup.setOnHiding(value);
    }

    public void setOnHidden(EventHandler<WindowEvent> value) {
        popup.setOnHidden(value);
    }

    public boolean isShowing() {
        return popup.isShowing();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return popup.showingProperty();
    }

    //================================================================================
    // Override Methods
    //================================================================================

    /**
     * @return an unmodifiable list containing the control's children
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildrenUnmodifiable();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        getItems().stream()
                .filter(node -> node instanceof Line)
                .map(node -> (Line) node)
                .forEach(line -> {
                    line.setStartX(0);
                    line.setEndX(getWidth() - (snappedRightInset() + snappedLeftInset()));
                });
    }

    //================================================================================
    // Builder
    //================================================================================

    /**
     * Builder class that facilitates the creation of context menus with fluent api.
     * <p>
     * An example:
     * <p></p>
     * <pre>
     * {@code
     * MFXContextMenuItem item1 = new MFXContextMenuItem()
     *         .setText("A")
     *         .setAccelerator("Shift + A")
     *         .setTooltipSupplier(() -> new Tooltip("A"))
     *         .setAction(event -> System.out.println("Action A"));
     *
     * MFXContextMenuItem item2 = new MFXContextMenuItem()
     *         .setText("B")
     *         .setAccelerator("Shift + B")
     *         .setTooltipSupplier(() -> new Tooltip("B"))
     *         .setAction(event -> System.out.println("Action B"));
     *
     * MFXContextMenuItem item3 = new MFXContextMenuItem()
     *         .setText("C")
     *         .setAccelerator("Shift + C")
     *         .setTooltipSupplier(() -> new Tooltip("C"))
     *         .setAction(event -> System.out.println("Action C"));
     *
     * MFXContextMenuItem item4 = new MFXContextMenuItem()
     *         .setText("D")
     *         .setAccelerator("Shift + D")
     *         .setTooltipSupplier(() -> new Tooltip("D"))
     *         .setAction(event -> System.out.println("Action D"));
     *
     * MFXContextMenuItem item5 = new MFXContextMenuItem()
     *         .setText("E")
     *         .setAccelerator("Shift + E")
     *         .setTooltipSupplier(() -> new Tooltip("E"))
     *         .setAction(event -> System.out.println("Action E"));
     *
     * MFXContextMenuItem item6 = new MFXContextMenuItem()
     *         .setText("F")
     *         .setAccelerator("Shift + F")
     *         .setTooltipSupplier(() -> new Tooltip("F"))
     *         .setAction(event -> System.out.println("Action F"));
     *
     * MFXContextMenu.Builder.build(owner)
     *         .addMenuItem(item1)
     *         .addMenuItem(item2)
     *         .addSeparator()
     *         .addMenuItem(item3)
     *         .addMenuItem(item4)
     *         .addSeparator()
     *         .addMenuItem(item5)
     *         .addMenuItem(item6)
     *         .install();
     * }
     * </pre>
     *
     * @see MFXContextMenuItem
     */
    public static class Builder {
        private final MFXContextMenu contextMenu;
        private final ObservableList<Node> items = FXCollections.observableArrayList();

        private Builder(Node owner) {
            this(0, owner);
        }

        private Builder(double spacing, Node owner) {
            contextMenu = new MFXContextMenu(spacing, owner);
        }

        /**
         * @return a new Builder instance with the given owner for the MFXContextMenu
         */
        public static Builder build(Node owner) {
            return new Builder(owner);
        }

        /**
         * @return a new Builder instance with the given owner for the MFXContextMenu
         * and the given spacing
         */
        public static Builder build(double spacing, Node owner) {
            return new Builder(spacing, owner);
        }

        /**
         * Adds the specifies node to the items list.
         */
        public Builder addMenuItem(Node node) {
            items.add(node);
            return this;
        }

        /**
         * Adds the specified action to the specified node by adding an event handler
         * for MOUSE_PRESSED to the node and then adds the node to the items list.
         */
        public Builder addMenuItem(Node node, EventHandler<MouseEvent> action) {
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, action);
            items.add(node);
            return this;
        }

        /**
         * Adds the specified {@link MFXContextMenuItem} to the items list.
         */
        public Builder addMenuItem(MFXContextMenuItem item) {
            items.add(item);
            return this;
        }

        /**
         * Adds a separator to the items list.
         */
        public Builder addSeparator() {
            items.add(getSeparator());
            return this;
        }

        /**
         * Installs the added items in the context menu.
         */
        public MFXContextMenu install() {
            contextMenu.setItems(items);
            return contextMenu;
        }

        /**
         * Builds a separator.
         */
        public static Line getSeparator() {
            Line separator = new Line();
            separator.getStyleClass().add("separator");
            VBox.setMargin(separator, new Insets(4, 0, 3, 0));
            return separator;
        }
    }
}
