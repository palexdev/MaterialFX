/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.PickResult;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

import java.util.ArrayList;

public class NodeUtils {

	private NodeUtils() {
	}

	/**
	 * Centers the specified node in an {@code AnchorPane}.
	 */
	public static void centerNodeInAnchorPane(Node node, double topBottom, double leftRight) {
		AnchorPane.setTopAnchor(node, topBottom);
		AnchorPane.setBottomAnchor(node, topBottom);
		AnchorPane.setLeftAnchor(node, leftRight);
		AnchorPane.setRightAnchor(node, leftRight);
	}

	/**
	 * Checks if the specified element is in the hierarchy of the specified node.
	 */
	public static boolean inHierarchy(Node node, Node element) {
		if (element == null) {
			return true;
		}
		while (node != null) {
			if (node == element) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}

	/**
	 * Checks if the pressed node is in the hierarchy of the specified node, {@link PickResult#getIntersectedNode()}.
	 */
	public static boolean inHierarchy(Node node, MouseEvent event) {
		return inHierarchy(node, event.getPickResult().getIntersectedNode());
	}

	/**
	 * Checks if the pressed node is in the hierarchy of the specified node, {@link PickResult#getIntersectedNode()}.
	 */
	public static boolean inHierarchy(Node node, GestureEvent event) {
		return inHierarchy(node, event.getPickResult().getIntersectedNode());
	}

	/**
	 * Checks if the specified node is in hierarchy of the pressed node, {@link PickResult#getIntersectedNode()}.
	 */
	public static boolean inHierarchy(MouseEvent event, Node node) {
		return inHierarchy(event.getPickResult().getIntersectedNode(), node);
	}

	/**
	 * Checks if the specified node is in hierarchy of the pressed node, {@link PickResult#getIntersectedNode()}.
	 */
	public static boolean inHierarchy(GestureEvent event, Node node) {
		return inHierarchy(event.getPickResult().getIntersectedNode(), node);
	}

	/**
	 * Convenience method to check if a {@code Node} is visible by checking
	 * both {@link Node#visibleProperty()} and {@link Node#opacityProperty()}.
	 *
	 * @return true if the {@code Node} is visible and opacity is not 0.0
	 */
	public static boolean isVisible(Node node) {
		return node.isVisible() && node.getOpacity() != 0.0;
	}

	/**
	 * Retrieves the node height if it isn't still laid out.
	 *
	 * @param node the Node of which to know the height
	 * @return the calculated height
	 */
	public static double getNodeHeight(Node node) {
		Group group = new Group(node);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		double height = node.prefHeight(-1);
		group.getChildren().clear();
		return height;
	}

	/**
	 * Retrieves the node width if it isn't still laid out.
	 *
	 * @param node the Node of which to know the width
	 * @return the calculated width
	 */
	public static double getNodeWidth(Node node) {
		Group group = new Group(node);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		double width = node.prefWidth(-1);
		group.getChildren().clear();
		return width;
	}

	/**
	 * Retrieves the node's width and height if it isn't still laid out
	 *
	 * @param node the Node of which to know the sizes
	 * @return the computed width and height as a {@link Size}
	 */
	public static Size getNodeSizes(Node node) {
		Group group = new Group(node);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		Size sizes = Size.of(node.prefWidth(-1), node.prefHeight(-1));
		group.getChildren().clear();
		return sizes;
	}

	/**
	 * Convenience method for programmatically fire a dummy {@code MouseEvent} on the desired node.
	 */
	public static void fireDummyMouseEvent(Node node, EventType<MouseEvent> type) {
		Event.fireEvent(node, new MouseEvent(type,
				0, 0, 0, 0, MouseButton.PRIMARY, 1,
				false, false, false, false, true, false, false, false, false, false, null));
	}

	/**
	 * Recursively gets all nodes that are descendants of the given root.
	 */
	public static ArrayList<Node> getAllNodes(Parent root) {
		ArrayList<Node> nodes = new ArrayList<>();
		addAllDescendents(root, nodes);
		return nodes;
	}

	/**
	 * Convenience method to execute a given action after that the given control
	 * has been laid out and its skin is not null anymore.
	 * <p></p>
	 * If the skin is not null when called, the action is executed immediately.
	 * <p>
	 * The listener is added only if the skin is null or the addListenerIfNotNull parameter is true.
	 *
	 * @param control              the control to check for skin initialization
	 * @param action               the action to perform when the skin is not null
	 * @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not null
	 * @param isOneShot            to specify if the listener added to the skin property
	 *                             should be removed after it is not null anymore
	 */
	public static void waitForSkin(Control control, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
		if (control.getSkin() != null) {
			action.run();
		}

		if (control.getSkin() == null || addListenerIfNotNull) {
			control.skinProperty().addListener(new ChangeListener<>() {
				@Override
				public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
					if (newValue != null) {
						action.run();
						if (isOneShot) {
							control.skinProperty().removeListener(this);
						}
					}
				}
			});
		}
	}

	/**
	 * Convenience method to execute a given action after that the given node
	 * has been laid out and its scene is not null anymore.
	 * <p></p>
	 * If the scene is not null when called, the action is executed immediately.
	 * <p>
	 * The listener is added only if the scene is null or the addListenerIfNotNull parameter is true.
	 *
	 * @param node                 the node to check for scene initialization
	 * @param action               the action to perform when the scene is not null
	 * @param addListenerIfNotNull to specify if the listener should be added anyway even if the scene is not null
	 * @param isOneShot            to specify if the listener added to the scene property
	 *                             should be removed after it is not null anymore
	 */
	public static void waitForScene(Node node, Runnable action, boolean addListenerIfNotNull, boolean isOneShot) {
		if (node.getScene() != null) {
			action.run();
		}

		if (node.getScene() == null || addListenerIfNotNull) {
			node.sceneProperty().addListener(new ChangeListener<>() {
				@Override
				public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
					if (newValue != null) {
						action.run();
						if (isOneShot) {
							node.sceneProperty().removeListener(this);
						}
					}
				}
			});
		}
	}

	/**
	 * Checks if the given {@link PseudoClass} is currently active on the given {@link Control}.
	 */
	public static boolean isPseudoClassActive(Control control, PseudoClass pseudoClass) {
		return control.getPseudoClassStates().contains(pseudoClass);
	}

	/**
	 * Attempts to get the {@link Screen} instance on which
	 * the given {@link Node} is shown.
	 * If the screen is not found for any reason, returns null.
	 */
	public static Screen getScreenFor(Node node) {
		Bounds nodeBounds = node.localToScreen(node.getLayoutBounds());
		Rectangle2D boundsToRect = new Rectangle2D(nodeBounds.getMinX(), nodeBounds.getMinY(), nodeBounds.getWidth(), nodeBounds.getHeight());
		return Screen.getScreens().stream()
				.filter(screen -> screen.getBounds().contains(boundsToRect))
				.findFirst()
				.orElse(null);
	}

	private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Parent)
				addAllDescendents((Parent) node, nodes);
		}
	}
}
