/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.MFXContextMenuItem;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * This control is a context menu built from scratch which extends {@code VBox}.
 * <p>
 * It easily styleable and allows to add separators between the context menu nodes.
 * The context menu is shown in a {@code PopupControl}.
 * <p></p>
 * It is <b>highly recommended</b> to use the {@link Builder} class to create a context menu.
 */
public class MFXContextMenu extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-context-menu";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXContextMenu.css");

    private final List<Line> separators = new ArrayList<>();

    private WeakReference<Node> nodeReference;
    private final PopupControl popupControl;
    private final ChangeListener<Scene> sceneListener;
    private final ChangeListener<Boolean> windowFocusListener;
    private final EventHandler<MouseEvent> sceneHandler;
    private final EventHandler<MouseEvent> openHandler;
    private final EventHandler<KeyEvent> keyHandler;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXContextMenu() {
        this(0);
    }

    public MFXContextMenu(double spacing) {
        super(spacing);
        setMinWidth(100);
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().setAll(STYLE_CLASS);
        popupControl = new PopupControl();
        popupControl.getScene().setFill(Color.WHITE);
        popupControl.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> hide());

        sceneHandler = event -> {
            if (popupControl.isShowing()) {
                hide();
            }
        };
        sceneListener = (observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, sceneHandler);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneHandler);
            }
        };
        windowFocusListener = (observable, oldValue, newValue) -> {
            if (!newValue && popupControl.isShowing()) {
                hide();
            }
        };
        openHandler = event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                popupControl.getScene().setRoot(MFXContextMenu.this);
                show(nodeReference.get(), event.getScreenX(), event.getScreenY());
            }
        };
        keyHandler = event -> {
            if (event.getEventType() != KeyEvent.KEY_PRESSED) return;

            final KeyCode code = event.getCode();
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                hide();
            }
        };
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Installs the context menu to the given node.
     */
    public void install(Node node) {
        if (node.getScene() != null) {
            Scene scene = node.getScene();
            Window window = scene.getWindow();
            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> hide());
            if (window != null) {
                window.focusedProperty().addListener(windowFocusListener);
            }
        }

        node.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> hide());
                Window window = newValue.getWindow();
                if (window != null) {
                    window.focusedProperty().addListener(windowFocusListener);
                }
            }
        });

        Node nR = nodeReference != null ? nodeReference.get() : null;
        if (nR != null && nR == node) {
            return;
        }

        dispose();
        nodeReference = new WeakReference<>(node);
        installBehavior();
    }

    /**
     * Installs the context menu behavior to the given node.
     * Used in {@link #install(Node)}.
     */
    private void installBehavior() {
        if (nodeReference != null) {
            Node node = nodeReference.get();
            if (node != null) {
                node.sceneProperty().addListener(sceneListener);
                node.addEventHandler(MouseEvent.MOUSE_PRESSED, openHandler);
                node.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
            }
        }
    }

    /**
     * If the node reference is not null and {@link #install(Node)} is called again, this method is called to
     * remove the context menu from the previous node.
     */
    public void dispose() {
        if (nodeReference != null) {
            Node node = nodeReference.get();
            if (node != null) {
                node.sceneProperty().removeListener(sceneListener);
                node.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, sceneHandler);
                Window window = node.getScene().getWindow();
                if (window != null) {
                    window.focusedProperty().removeListener(windowFocusListener);
                }
                node.removeEventHandler(MouseEvent.MOUSE_PRESSED, openHandler);
                node.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
            }
        }
    }

    void addSeparator(Line line) {
        separators.add(line);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        separators.forEach(line -> {
            line.setStartX(0);
            line.setEndX(getWidth() - (snappedRightInset() + snappedLeftInset()));
        });
    }

    /**
     * Shows the context menu' popup.
     */
    public void show(Node ownerNode, double anchorX, double anchorY) {
        popupControl.show(ownerNode, anchorX, anchorY);
    }

    /**
     * Hides the context menu' popup.
     */
    public void hide() {
        popupControl.hide();
    }

    public void addPopupEventHandler(EventType<WindowEvent> eventType, EventHandler<WindowEvent> eventHandler) {
        popupControl.addEventHandler(eventType, eventHandler);
    }

    public void addPopupEventFilter(EventType<WindowEvent> eventType, EventHandler<WindowEvent> eventHandler) {
        popupControl.addEventFilter(eventType, eventHandler);
    }

    public void removePopupEventHandler(EventType<WindowEvent> eventType, EventHandler<WindowEvent> eventHandler) {
        popupControl.removeEventHandler(eventType, eventHandler);
    }

    public void removePopupEventFilter(EventType<WindowEvent> eventType, EventHandler<WindowEvent> eventHandler) {
        popupControl.removeEventFilter(eventType, eventHandler);
    }

    /**
     * Utils class that facilitates the creation of context menus with fluent api.
     * <p>
     * Example from {@code MFXTableView Skin}:
     * <p></p>
     * <pre>
     * {@code
     * MFXContextMenuItem restoreWidthThis = new MFXContextMenuItem(
     *      "Restore this column width",
     *      event -> column.setMinWidth(column.getInitialWidth())
     * );
     *
     * MFXContextMenuItem restoreWidthAll = new MFXContextMenuItem(
     *      "Restore all columns width",
     *      event -> columnsContainer.getChildren().stream()
     *          .filter(node -> node instanceof MFXTableColumnCell)
     *          .map(node -> (MFXTableColumnCell<T>) node)
     *          .forEach(c -> c.setMinWidth(c.getInitialWidth()))
     * );
     *
     * MFXContextMenuItem autoSizeThis = new MFXContextMenuItem(
     *      "Autosize this column",
     *      event -> autoSizeColumn(column)
     *  );
     *
     * MFXContextMenuItem autoSizeAll = new MFXContextMenuItem(
     *      "Autosize all columns",
     *      event -> columnsContainer.getChildren().stream()
     *          .filter(node -> node instanceof MFXTableColumnCell)
     *          .map(node -> (MFXTableColumnCell<T>) node)
     *          .forEach(this::autoSizeColumn)
     * );
     *
     * new MFXContextMenu.Builder()
     *      .addMenuItem(autoSizeAll)
     *      .addMenuItem(autoSizeThis)
     *      .addSeparator()
     *      .addMenuItem(restoreWidthAll)
     *      .addMenuItem(restoreWidthThis)
     *      .install(column);
     * }
     * </pre>
     */
    public static class Builder {
        private final MFXContextMenu contextMenu;

        /**
         * Creates a new Builder with a new MFXContextMenu instance.
         */
        public Builder() {
            contextMenu = new MFXContextMenu();
        }

        /**
         * Creates a new Builder with a new MFXContextMenu instance that has
         * the spacing set to the given value.
         */
        public Builder(double spacing) {
            contextMenu = new MFXContextMenu(spacing);
        }

        /**
         * Adds a new node to the context menu with the specified action on mouse pressed.
         */
        public Builder addMenuItem(Node node, EventHandler<MouseEvent> action) {
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, action);
            contextMenu.getChildren().add(node);
            return this;
        }

        /**
         * Adds the specified {@link MFXContextMenuItem} to the context menu.
         */
        public Builder addMenuItem(MFXContextMenuItem item) {
            contextMenu.getChildren().add(item.getNode());
            return this;
        }

        /**
         * Adds a separator to the context menu.
         */
        public Builder addSeparator() {
            Line separator = new Line();
            separator.getStyleClass().add("separator");
            VBox.setMargin(separator, new Insets(4, 0, 3, 0));
            contextMenu.addSeparator(separator);
            contextMenu.getChildren().add(separator);
            return this;
        }

        /**
         * @return the built context menu instance
         */
        public MFXContextMenu get() {
            return contextMenu;
        }

        /**
         * Installs the context menu to the given node and returns the
         * context menu instance.
         */
        public MFXContextMenu install(Node node) {
            contextMenu.install(node);
            return contextMenu;
        }
    }
}
