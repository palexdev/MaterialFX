/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.beans.SizeBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class which provides convenience methods for working with Nodes
 */
public class NodeUtils {

	private NodeUtils() {}

	/**
	 * Changes the background color of a {@code Region} to the desired one.
	 *
	 * @param region The region to change the background color to
	 * @param fill   The desired color
	 */
	public static void updateBackground(Region region, Paint fill) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, bf.getRadii(), bf.getInsets()));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	/**
	 * Changes the background color of a {@code Region} to the desired one and lets specify the background insets.
	 *
	 * @param region           The region to change the background color to
	 * @param fill             The desired color
	 * @param backgroundInsets The background insets to use
	 */
	public static void updateBackground(Region region, Paint fill, Insets backgroundInsets) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, bf.getRadii(), backgroundInsets));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	public static void updateBackground(Region region, Paint fill, CornerRadii cornerRadii, Insets backgroundInsets) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, cornerRadii, backgroundInsets));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	/**
	 * Sets the background of the given region to the given color.
	 */
	public static void setBackground(Region region, Paint fill) {
		setBackground(region, fill, CornerRadii.EMPTY, Insets.EMPTY);
	}

	/**
	 * Sets the background of the given region to the given color, with the given radius.
	 */
	public static void setBackground(Region region, Paint fill, CornerRadii radius) {
		setBackground(region, fill, radius, Insets.EMPTY);
	}

	/**
	 * Sets the background of the given region to the given color, with the given radius and insets.
	 */
	public static void setBackground(Region region, Paint fill, CornerRadii radius, Insets insets) {
		region.setBackground(new Background(new BackgroundFill(fill, radius, insets)));
	}

	/**
	 * Tries to parse tje given Region's corner radius.
	 * <p>
	 * To be more precise it tries to parse both the background and the
	 * border radius. The background radius is prioritized over the border one
	 * but in case the background is null or empty then the border one is used.
	 * <p>
	 * In case of both null or empty returns {@link  CornerRadii#EMPTY}.
	 */
	public static CornerRadii parseCornerRadius(Region region) {
		CornerRadii backRadius = CornerRadii.EMPTY;
		CornerRadii bordRadius = CornerRadii.EMPTY;

		Background background = region.getBackground();
		if (background != null && !background.isEmpty()) {
			backRadius = background.getFills().get(0).getRadii();
		}

		Border border = region.getBorder();
		if (border != null && !border.isEmpty()) {
			bordRadius = border.getStrokes().get(0).getRadii();
		}

		return !backRadius.equals(CornerRadii.EMPTY) ? backRadius : bordRadius;
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
	 * Checks if the specified node is in hierarchy of the pressed node, {@link PickResult#getIntersectedNode()}.
	 */
	public static boolean inHierarchy(MouseEvent event, Node node) {
		return inHierarchy(event.getPickResult().getIntersectedNode(), node);
	}

	/**
	 * Makes the given region circular.
	 * <p>
	 * <b>Notice: the region's pref width and height must be set and be equals</b>
	 *
	 * @param region The given region
	 */
	public static void makeRegionCircular(Region region) {
		Circle circle = new Circle();
		circle.radiusProperty().bind(region.widthProperty().divide(2.0));
		circle.centerXProperty().bind(region.widthProperty().divide(2.0));
		circle.centerYProperty().bind(region.heightProperty().divide(2.0));
		try {
			region.setClip(circle);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
		}
	}

	/**
	 * Makes the given region circular with the specified radius.
	 * <p>
	 * <b>Notice: the region's pref width and height must be set and be equals</b>
	 *
	 * @param region The given region
	 * @param radius The wanted radius
	 */
	public static void makeRegionCircular(Region region, double radius) {
		Circle circle = new Circle(radius);
		circle.centerXProperty().bind(region.widthProperty().divide(2.0));
		circle.centerYProperty().bind(region.heightProperty().divide(2.0));
		try {
			region.setClip(circle);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Could not set region's clip to make it circular", ex);
		}
	}

	/**
	 * Retrieves the region height if it isn't still laid out.
	 *
	 * @param region the Region of which to know the height
	 * @return the calculated height
	 */
	public static double getRegionHeight(Region region) {
		Group group = new Group(region);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		group.getChildren().clear();
		return region.getHeight();
	}

	/**
	 * Retrieves the region width if it isn't still laid out.
	 *
	 * @param region the Region of which to know the width
	 * @return the calculated width
	 */
	public static double getRegionWidth(Region region) {
		Group group = new Group(region);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		group.getChildren().clear();
		return region.getWidth();
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
	 * @return the computed width and height as a {@link SizeBean}
	 */
	public static SizeBean getNodeSizes(Node node) {
		Group group = new Group(node);
		Scene scene = new Scene(group);
		group.applyCss();
		group.layout();

		SizeBean sizes = SizeBean.of(node.prefWidth(-1), node.prefHeight(-1));
		group.getChildren().clear();
		return sizes;
	}

	/**
	 * Convenience method for adding the desired value to the region's prefWidth
	 */
	public static void addPrefWidth(Region region, double value) {
		double prefW = region.getPrefWidth();
		region.setPrefWidth(prefW + value);
	}

	/**
	 * Convenience method for adding the desired value to the region's prefHeight
	 */
	public static void addPrefHeight(Region region, double value) {
		double prefH = region.getPrefHeight();
		region.setPrefHeight(prefH + value);
	}

	/**
	 * Convenience method for programmatically fire a dummy MOUSE_PRESSED event on the desired node.
	 */
	public static void fireDummyEvent(Node node) {
		Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_PRESSED,
				0, 0, 0, 0, MouseButton.PRIMARY, 1,
				false, false, false, false, true, false, false, false, false, false, null));
	}

	/**
	 * Checks if the given alignment is set to RIGHT(any).
	 */
	public static boolean isRightAlignment(Pos alignment) {
		return alignment == Pos.BASELINE_RIGHT || alignment == Pos.BOTTOM_RIGHT ||
				alignment == Pos.CENTER_RIGHT || alignment == Pos.TOP_RIGHT;
	}

	/**
	 * Recursively gets all nodes that are descendants of the given root.
	 */
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
}
