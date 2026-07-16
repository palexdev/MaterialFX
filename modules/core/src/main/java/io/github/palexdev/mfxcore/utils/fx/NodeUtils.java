/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx;

import java.util.ArrayList;
import java.util.Objects;

import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.observables.When;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

import static io.github.palexdev.mfxcore.base.beans.Size.size;

public class NodeUtils {

    private NodeUtils() {
    }

    /// Centers the specified node in an `AnchorPane`.
    ///
    /// If the `bind` parameter is `true`, then a listener will be used to keep the node centered in case the node or
    /// the pane sizes change.
    public static void centerNodeInAnchorPane(AnchorPane pane, Node node, boolean bind) {
        if (bind) {
            When.onInvalidated(node.layoutBoundsProperty())
                .then(b -> {
                    double top = (pane.getHeight() - b.getHeight()) / 2.0;
                    double left = (pane.getWidth() - b.getWidth()) / 2.0;
                    AnchorPane.setTopAnchor(node, top);
                    AnchorPane.setLeftAnchor(node, left);
                })
                .invalidating(pane.layoutBoundsProperty())
                .listen();
        } else {
            Bounds b = node.getLayoutBounds();
            double top = (pane.getHeight() - b.getHeight()) / 2.0;
            double left = (pane.getWidth() - b.getWidth()) / 2.0;
            AnchorPane.setTopAnchor(node, top);
            AnchorPane.setLeftAnchor(node, left);
        }
    }

    /**
     * Returns {@code true} if {@code node} is a descendant of (or equal to) {@code ancestor}.
     * Returns {@code true} unconditionally if {@code ancestor} is {@code null}.
     */
    public static boolean isDescendantOf(Node node, Node ancestor) {
        if (ancestor == null) {
            return true;
        }
        while (node != null) {
            if (node == ancestor) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Returns {@code true} if the event's intersected node is a descendant of {@code ancestor}.
     */
    public static boolean isDescendantOf(MouseEvent event, Node ancestor) {
        return isDescendantOf(event.getPickResult().getIntersectedNode(), ancestor);
    }

    /**
     * Returns {@code true} if the event's intersected node is a descendant of {@code ancestor}.
     */
    public static boolean isDescendantOf(GestureEvent event, Node ancestor) {
        return isDescendantOf(event.getPickResult().getIntersectedNode(), ancestor);
    }

    /**
     * Returns {@code true} if {@code node} contains the event's intersected node as a descendant.
     */
    public static boolean containsEventTarget(MouseEvent event, Node node) {
        return isDescendantOf(event.getPickResult().getIntersectedNode(), node);
    }

    /**
     * Returns {@code true} if {@code node} contains the event's intersected node as a descendant.
     */
    public static boolean containsEventTarget(GestureEvent event, Node node) {
        return isDescendantOf(event.getPickResult().getIntersectedNode(), node);
    }

    /// Convenience method to check if a `Node` is visible by checking both [Node#visibleProperty()] and [Node#opacityProperty()].
    ///
    /// @return true if the `Node` is visible and opacity is not 0.0
    public static boolean isVisible(Node node) {
        return node.isVisible() && node.getOpacity() != 0.0;
    }

    /// Retrieves the node height if it isn't still laid out.
    ///
    /// @param node the Node of which to know the height
    /// @return the calculated height
    public static double getNodeHeight(Node node) {
        Group group = new Group(node);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        double height = node.prefHeight(-1);
        group.getChildren().clear();
        return height;
    }

    /// Retrieves the node width if it isn't still laid out.
    ///
    /// @param node the Node of which to know the width
    /// @return the calculated width
    public static double getNodeWidth(Node node) {
        Group group = new Group(node);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        double width = node.prefWidth(-1);
        group.getChildren().clear();
        return width;
    }

    /// Retrieves the node's width and height if it isn't still laid out
    ///
    /// @param node the Node of which to know the sizes
    /// @return the computed width and height as a [Size]
    public static Size getNodeSizes(Node node) {
        Group group = new Group(node);
        Scene scene = new Scene(group);
        group.applyCss();
        group.layout();

        Size sizes = size(node.prefWidth(-1), node.prefHeight(-1));
        group.getChildren().clear();
        return sizes;
    }

    /// Convenience method for programmatically fire a dummy `MouseEvent` on the desired node.
    public static void fireDummyMouseEvent(Node node, EventType<MouseEvent> type) {
        Event.fireEvent(node, new MouseEvent(type,
            0, 0, 0, 0, MouseButton.PRIMARY, 1,
            false, false, false, false, true, false, false, false, false, false, null));
    }

    /// Recursively gets all nodes that are descendants of the given root.
    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent) node, nodes);
        }
    }

    /// Convenience method to execute a given action after that the given control has been laid out and its skin is not
    /// `null` anymore.
    ///
    /// If the skin is not `null` when called, the action is executed immediately.
    ///
    /// The listener is added only if the skin is `null` or the `addListenerIfNotNull` parameter is true.
    ///
    /// @param control the control to check for skin initialization
    /// @param action the action to perform when the skin is not `null`
    /// @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not `null`
    /// @param isOneShot to specify if the listener added to the skin property
    ///                                                         should be removed after it is not `null` anymore
    public static void waitForSkin(Control control, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
        if (control.getSkin() != null) {
            action.run();
        }

        if (control.getSkin() == null || addListenerIfNotNull) {
            When<Skin<?>> when = When.onInvalidated(control.skinProperty())
                .condition(Objects::nonNull)
                .then(s -> action.run());
            if (isOneShot) when.oneShot();
            when.listen();
        }
    }

    /// Convenience method to execute a given action after that the given node has been laid out and its scene is not `null` anymore.
    ///
    /// If the scene is not `null` when called, the action is executed immediately.
    ///
    /// The listener is added only if the scene is `null` or the `addListenerIfNotNull` parameter is true.
    ///
    /// @param node the node to check for scene initialization
    /// @param action the action to perform when the scene is not `null`
    /// @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not `null`
    /// @param isOneShot to specify if the listener added to the scene property
    ///                             should be removed after it is not `null` anymore
    public static void waitForScene(Node node, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
        if (node.getScene() != null) {
            action.run();
        }

        if (node.getScene() == null || addListenerIfNotNull) {
            When<Scene> when = When.onInvalidated(node.sceneProperty())
                .condition(Objects::nonNull)
                .then(s -> action.run());
            if (isOneShot) when.oneShot();
            when.listen();
        }
    }

    /// Checks if the given [PseudoClass] is currently active on the given [Control].
    public static boolean isPseudoClassActive(Control control, PseudoClass pseudoClass) {
        return control.getPseudoClassStates().contains(pseudoClass);
    }

    /// Attempts to get the [Screen] instance on which
    /// the given [Node] is shown.
    /// If the screen is not found for any reason, returns null.
    public static Screen getScreenFor(Node node) {
        Bounds nodeBounds = node.localToScreen(node.getLayoutBounds());
        Rectangle2D boundsToRect = new Rectangle2D(nodeBounds.getMinX(), nodeBounds.getMinY(), nodeBounds.getWidth(), nodeBounds.getHeight());
        return Screen.getScreens().stream()
            .filter(screen -> screen.getBounds().intersects(boundsToRect))
            .findFirst()
            .orElse(null);
    }
}
